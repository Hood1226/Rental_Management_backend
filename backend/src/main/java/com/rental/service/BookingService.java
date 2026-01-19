package com.rental.service;

import com.rental.dto.request.BookingRequest;
import com.rental.dto.response.BookingResponse;
import com.rental.entity.*;
import com.rental.exception.InsufficientInventoryException;
import com.rental.exception.ResourceNotFoundException;
import com.rental.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerMasterRepository customerRepository;
    
    @Autowired
    private ProductVariantRepository variantRepository;
    
    @Autowired
    private BookingItemRepository bookingItemRepository;
    
    @Autowired
    private InventoryTransactionRepository transactionRepository;
    
    @Autowired
    private DamageRecordRepository damageRecordRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        logger.debug("Fetching all bookings with items and transactions");
        List<Booking> bookings = bookingRepository.findAllWithCustomer();
        logger.info("Found {} bookings", bookings.size());
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Integer id) {
        logger.debug("Fetching booking with ID: {} and all related data", id);
        Booking booking = bookingRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> {
                    logger.warn("Booking not found with ID: {}", id);
                    return new ResourceNotFoundException("Booking not found with ID: " + id);
                });
        logger.info("Booking found with ID: {}", id);
        return convertToResponse(booking);
    }
    
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        logger.info("Creating new booking for customer ID: {}, type: {}, with {} items and {} transactions", 
                request.getCustomerId(), 
                request.getBookingType(),
                request.getItems() != null ? request.getItems().size() : 0,
                request.getTransactions() != null ? request.getTransactions().size() : 0);
        
        // Validate customer exists
        CustomerMaster customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    logger.warn("Customer not found with ID: {}", request.getCustomerId());
                    return new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId());
                });
        
        // Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setBookingType(request.getBookingType());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        
        // Calculate total amount from items if not provided
        BigDecimal totalAmount = request.getTotalAmount();
        if (totalAmount == null && request.getItems() != null && !request.getItems().isEmpty()) {
            totalAmount = request.getItems().stream()
                    .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        booking.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created successfully with ID: {}", savedBooking.getBookingId());
        
        // Create booking items and automatically update inventory
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (BookingRequest.BookingItemRequest itemRequest : request.getItems()) {
                BookingItem item = createBookingItem(savedBooking, itemRequest);
                
                // Automatically create inventory transaction and update inventory based on booking type
                updateInventoryForBooking(savedBooking, item, request.getBookingType());
            }
        }
        
        // Create additional inventory transactions if explicitly provided
        if (request.getTransactions() != null && !request.getTransactions().isEmpty()) {
            for (BookingRequest.InventoryTransactionRequest transactionRequest : request.getTransactions()) {
                createInventoryTransaction(savedBooking, transactionRequest);
            }
        }
        
        return convertToResponse(savedBooking);
    }
    
    @Transactional
    public BookingResponse updateBooking(Integer id, BookingRequest request) {
        logger.info("Updating booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Booking not found with ID: {}", id);
                    return new ResourceNotFoundException("Booking not found with ID: " + id);
                });
        
        // Update customer if provided
        if (request.getCustomerId() != null && !request.getCustomerId().equals(booking.getCustomer().getCustomerId())) {
            CustomerMaster customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> {
                        logger.warn("Customer not found with ID: {}", request.getCustomerId());
                        return new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId());
                    });
            booking.setCustomer(customer);
        }
        
        if (request.getBookingType() != null) {
            booking.setBookingType(request.getBookingType());
        }
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }
        
        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Delete existing items
            List<BookingItem> existingItems = bookingItemRepository.findByBookingBookingId(id);
            bookingItemRepository.deleteAll(existingItems);
            
            // Create new items
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (BookingRequest.BookingItemRequest itemRequest : request.getItems()) {
                BookingItem item = createBookingItem(booking, itemRequest);
                if (item.getSubtotal() != null) {
                    totalAmount = totalAmount.add(item.getSubtotal());
                }
            }
            booking.setTotalAmount(totalAmount);
        } else if (request.getTotalAmount() != null) {
            booking.setTotalAmount(request.getTotalAmount());
        }
        
        // Update or create transactions if provided
        if (request.getTransactions() != null && !request.getTransactions().isEmpty()) {
            for (BookingRequest.InventoryTransactionRequest transactionRequest : request.getTransactions()) {
                if (transactionRequest.getTransactionId() != null) {
                    // Update existing transaction
                    updateInventoryTransaction(booking, transactionRequest);
                } else {
                    // Create new transaction
                    createInventoryTransaction(booking, transactionRequest);
                }
            }
        }
        
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking updated successfully with ID: {}", updatedBooking.getBookingId());
        
        return convertToResponse(updatedBooking);
    }
    
    private BookingItem createBookingItem(Booking booking, BookingRequest.BookingItemRequest request) {
        // Validate variant exists
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> {
                    logger.warn("Product variant not found with ID: {}", request.getVariantId());
                    return new ResourceNotFoundException("Product variant not found with ID: " + request.getVariantId());
                });
        
        // Create booking item
        BookingItem item = new BookingItem();
        item.setBooking(booking);
        item.setVariant(variant);
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setRentalStart(request.getRentalStart());
        item.setRentalEnd(request.getRentalEnd());
        
        // Calculate subtotal if not provided
        BigDecimal subtotal = request.getSubtotal();
        if (subtotal == null && request.getUnitPrice() != null && request.getQuantity() != null) {
            subtotal = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        }
        item.setSubtotal(subtotal != null ? subtotal : BigDecimal.ZERO);
        
        BookingItem savedItem = bookingItemRepository.save(item);
        logger.debug("Created booking item ID: {} for booking ID: {}", savedItem.getBookingItemId(), booking.getBookingId());
        
        return savedItem;
    }
    
    /**
     * Automatically update inventory when booking is created
     * - For RENT: Reduce quantity, set status to RENTED if quantity becomes 0
     * - For SALE: Reduce quantity, set status to SOLD if quantity becomes 0
     */
    private void updateInventoryForBooking(Booking booking, BookingItem item, String bookingType) {
        ProductVariant variant = item.getVariant();
        Integer requestedQuantity = item.getQuantity();
        
        // Get or create inventory for this variant
        Inventory inventory = inventoryRepository.findByVariantVariantId(variant.getVariantId())
                .orElseThrow(() -> {
                    logger.warn("Inventory not found for variant ID: {}", variant.getVariantId());
                    return new ResourceNotFoundException("Inventory not found for variant ID: " + variant.getVariantId());
                });
        
        // Check if sufficient quantity is available
        if (inventory.getAvailableQuantity() < requestedQuantity) {
            logger.warn("Insufficient inventory for variant ID: {}. Available: {}, Requested: {}", 
                    variant.getVariantId(), inventory.getAvailableQuantity(), requestedQuantity);
            throw new InsufficientInventoryException(
                    String.format("Insufficient inventory for product '%s' (Size: %s). Available: %d, Requested: %d",
                            variant.getProduct().getProductName(),
                            variant.getSize().getSizeCode(),
                            inventory.getAvailableQuantity(),
                            requestedQuantity));
        }
        
        // Reduce available quantity
        int newQuantity = inventory.getAvailableQuantity() - requestedQuantity;
        inventory.setAvailableQuantity(newQuantity);
        
        // Update availability status based on booking type and new quantity
        if (newQuantity == 0) {
            if ("RENT".equalsIgnoreCase(bookingType)) {
                inventory.setAvailabilityStatus("RENTED");
            } else if ("SALE".equalsIgnoreCase(bookingType)) {
                inventory.setAvailabilityStatus("SOLD");
            } else {
                inventory.setAvailabilityStatus("UNAVAILABLE");
            }
        } else {
            // If quantity > 0, set status based on booking type for partial inventory
            if ("RENT".equalsIgnoreCase(bookingType)) {
                inventory.setAvailabilityStatus("PARTIALLY_RENTED");
            } else if ("SALE".equalsIgnoreCase(bookingType)) {
                inventory.setAvailabilityStatus("PARTIALLY_SOLD");
            } else {
                inventory.setAvailabilityStatus("AVAILABLE");
            }
        }
        
        // Set expected restore date for RENT bookings
        if ("RENT".equalsIgnoreCase(bookingType) && item.getRentalEnd() != null) {
            inventory.setExpectedRestoreDate(item.getRentalEnd());
            inventory.setNextAvailabilityDate(item.getRentalEnd());
        }
        
        inventoryRepository.save(inventory);
        logger.info("Updated inventory for variant ID: {}. New quantity: {}, Status: {}", 
                variant.getVariantId(), newQuantity, inventory.getAvailabilityStatus());
        
        // Automatically create inventory transaction
        String transactionType = "RENT".equalsIgnoreCase(bookingType) ? "RENT_OUT" : "SALE";
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setBooking(booking);
        transaction.setVariant(variant);
        transaction.setTransactionType(transactionType);
        transaction.setQuantity(requestedQuantity);
        transaction.setStatus("ACTIVE");
        transaction.setNotes(String.format("Auto-generated transaction for booking ID: %d", booking.getBookingId()));
        
        if ("RENT".equalsIgnoreCase(bookingType) && item.getRentalEnd() != null) {
            transaction.setExpectedReturnDate(item.getRentalEnd());
        }
        
        transactionRepository.save(transaction);
        logger.debug("Created automatic inventory transaction for booking ID: {}, variant ID: {}, type: {}", 
                booking.getBookingId(), variant.getVariantId(), transactionType);
    }
    
    private InventoryTransaction createInventoryTransaction(Booking booking, BookingRequest.InventoryTransactionRequest request) {
        // Validate variant exists
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> {
                    logger.warn("Product variant not found with ID: {}", request.getVariantId());
                    return new ResourceNotFoundException("Product variant not found with ID: " + request.getVariantId());
                });
        
        // Create inventory transaction
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setBooking(booking);
        transaction.setVariant(variant);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setQuantity(request.getQuantity());
        transaction.setExpectedReturnDate(request.getExpectedReturnDate());
        transaction.setActualReturnDate(request.getActualReturnDate());
        transaction.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        transaction.setNotes(request.getNotes());
        
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);
        logger.debug("Created inventory transaction ID: {} for booking ID: {}", 
                savedTransaction.getTransactionId(), booking.getBookingId());
        
        // Create damage record if provided and transaction type is DAMAGE
        if (request.getDamageRecord() != null && "DAMAGE".equals(request.getTransactionType())) {
            createOrUpdateDamageRecord(savedTransaction, request.getDamageRecord());
        }
        
        return savedTransaction;
    }
    
    private InventoryTransaction updateInventoryTransaction(Booking booking, BookingRequest.InventoryTransactionRequest request) {
        // Find existing transaction
        InventoryTransaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> {
                    logger.warn("Inventory transaction not found with ID: {}", request.getTransactionId());
                    return new ResourceNotFoundException("Inventory transaction not found with ID: " + request.getTransactionId());
                });
        
        // Verify transaction belongs to this booking
        if (!transaction.getBooking().getBookingId().equals(booking.getBookingId())) {
            logger.warn("Transaction ID {} does not belong to booking ID {}", 
                    request.getTransactionId(), booking.getBookingId());
            throw new IllegalArgumentException("Transaction does not belong to this booking");
        }
        
        // Update variant if provided
        if (request.getVariantId() != null && !request.getVariantId().equals(transaction.getVariant().getVariantId())) {
            ProductVariant variant = variantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> {
                        logger.warn("Product variant not found with ID: {}", request.getVariantId());
                        return new ResourceNotFoundException("Product variant not found with ID: " + request.getVariantId());
                    });
            transaction.setVariant(variant);
        }
        
        // Update transaction fields
        if (request.getTransactionType() != null) {
            transaction.setTransactionType(request.getTransactionType());
        }
        if (request.getQuantity() != null) {
            transaction.setQuantity(request.getQuantity());
        }
        if (request.getExpectedReturnDate() != null) {
            transaction.setExpectedReturnDate(request.getExpectedReturnDate());
        }
        if (request.getActualReturnDate() != null) {
            transaction.setActualReturnDate(request.getActualReturnDate());
        }
        if (request.getStatus() != null) {
            transaction.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            transaction.setNotes(request.getNotes());
        }
        
        InventoryTransaction updatedTransaction = transactionRepository.save(transaction);
        logger.debug("Updated inventory transaction ID: {} for booking ID: {}", 
                updatedTransaction.getTransactionId(), booking.getBookingId());
        
        // Update or create damage record if provided
        if (request.getDamageRecord() != null) {
            createOrUpdateDamageRecord(updatedTransaction, request.getDamageRecord());
        }
        
        return updatedTransaction;
    }
    
    private DamageRecord createOrUpdateDamageRecord(InventoryTransaction transaction, BookingRequest.DamageRecordRequest request) {
        DamageRecord damageRecord;
        
        if (request.getDamageId() != null) {
            // Update existing damage record
            damageRecord = damageRecordRepository.findById(request.getDamageId())
                    .orElseThrow(() -> {
                        logger.warn("Damage record not found with ID: {}", request.getDamageId());
                        return new ResourceNotFoundException("Damage record not found with ID: " + request.getDamageId());
                    });
            
            // Verify damage record belongs to this transaction
            if (!damageRecord.getTransaction().getTransactionId().equals(transaction.getTransactionId())) {
                logger.warn("Damage record ID {} does not belong to transaction ID {}", 
                        request.getDamageId(), transaction.getTransactionId());
                throw new IllegalArgumentException("Damage record does not belong to this transaction");
            }
            
            logger.debug("Updating damage record ID: {} for transaction ID: {}", 
                    request.getDamageId(), transaction.getTransactionId());
        } else {
            // Create new damage record
            damageRecord = new DamageRecord();
            damageRecord.setTransaction(transaction);
            logger.debug("Creating new damage record for transaction ID: {}", transaction.getTransactionId());
        }
        
        // Update or set fields
        if (request.getDescription() != null) {
            damageRecord.setDescription(request.getDescription());
        }
        if (request.getRepairCost() != null) {
            damageRecord.setRepairCost(request.getRepairCost());
        }
        
        DamageRecord savedDamage = damageRecordRepository.save(damageRecord);
        logger.debug("Saved damage record ID: {} for transaction ID: {}", 
                savedDamage.getDamageId(), transaction.getTransactionId());
        
        return savedDamage;
    }
    
    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setCustomerId(booking.getCustomer().getCustomerId());
        response.setCustomerName(booking.getCustomer().getCustomerName());
        response.setBookingType(booking.getBookingType());
        response.setBookingDate(booking.getBookingDate());
        response.setStatus(booking.getStatus());
        response.setTotalAmount(booking.getTotalAmount());
        response.setCreatedBy(booking.getCreatedBy());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedBy(booking.getUpdatedBy());
        response.setUpdatedAt(booking.getUpdatedAt());
        
        // Load booking items
        List<BookingItem> items = bookingItemRepository.findByBookingBookingId(booking.getBookingId());
        List<BookingResponse.BookingItemResponse> itemResponses = new ArrayList<>();
        
        for (BookingItem item : items) {
            BookingResponse.BookingItemResponse itemResponse = new BookingResponse.BookingItemResponse();
            itemResponse.setBookingItemId(item.getBookingItemId());
            itemResponse.setVariantId(item.getVariant().getVariantId());
            itemResponse.setProductId(item.getVariant().getProduct().getProductId());
            itemResponse.setProductName(item.getVariant().getProduct().getProductName());
            itemResponse.setSizeCode(item.getVariant().getSize().getSizeCode());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setRentalStart(item.getRentalStart());
            itemResponse.setRentalEnd(item.getRentalEnd());
            itemResponse.setSubtotal(item.getSubtotal());
            itemResponses.add(itemResponse);
        }
        
        response.setItems(itemResponses);
        
        // Load inventory transactions with damage records
        List<InventoryTransaction> transactions = transactionRepository.findByBookingBookingId(booking.getBookingId());
        List<BookingResponse.InventoryTransactionResponse> transactionResponses = new ArrayList<>();
        
        // Fetch all damage records for all transactions in one query (optimization)
        List<Integer> transactionIds = transactions.stream()
                .map(InventoryTransaction::getTransactionId)
                .collect(Collectors.toList());
        
        List<DamageRecord> allDamageRecords = transactionIds.isEmpty() 
                ? new ArrayList<>() 
                : damageRecordRepository.findByTransactionTransactionIds(transactionIds);
        
        // Group damage records by transaction ID for efficient lookup
        Map<Integer, List<DamageRecord>> damageRecordsByTransaction = allDamageRecords.stream()
                .collect(Collectors.groupingBy(dr -> dr.getTransaction().getTransactionId()));
        
        for (InventoryTransaction transaction : transactions) {
            BookingResponse.InventoryTransactionResponse transactionResponse = 
                    new BookingResponse.InventoryTransactionResponse();
            transactionResponse.setTransactionId(transaction.getTransactionId());
            transactionResponse.setVariantId(transaction.getVariant().getVariantId());
            transactionResponse.setProductId(transaction.getVariant().getProduct().getProductId());
            transactionResponse.setProductName(transaction.getVariant().getProduct().getProductName());
            transactionResponse.setSizeCode(transaction.getVariant().getSize().getSizeCode());
            transactionResponse.setTransactionType(transaction.getTransactionType());
            transactionResponse.setQuantity(transaction.getQuantity());
            transactionResponse.setTransactionDate(transaction.getTransactionDate());
            transactionResponse.setExpectedReturnDate(transaction.getExpectedReturnDate());
            transactionResponse.setActualReturnDate(transaction.getActualReturnDate());
            transactionResponse.setStatus(transaction.getStatus());
            transactionResponse.setNotes(transaction.getNotes());
            transactionResponse.setCreatedAt(transaction.getCreatedAt());
            
            // Get damage records for this transaction from the pre-loaded map
            List<DamageRecord> damageRecords = damageRecordsByTransaction.getOrDefault(
                    transaction.getTransactionId(), new ArrayList<>());
            List<BookingResponse.DamageRecordResponse> damageResponses = new ArrayList<>();
            
            for (DamageRecord damage : damageRecords) {
                BookingResponse.DamageRecordResponse damageResponse = new BookingResponse.DamageRecordResponse();
                damageResponse.setDamageId(damage.getDamageId());
                damageResponse.setTransactionId(damage.getTransaction().getTransactionId());
                damageResponse.setDescription(damage.getDescription());
                damageResponse.setRepairCost(damage.getRepairCost());
                damageResponse.setCreatedAt(damage.getCreatedAt());
                damageResponses.add(damageResponse);
            }
            
            // Set damage records list
            transactionResponse.setDamageRecords(damageResponses);
            
            transactionResponses.add(transactionResponse);
        }
        
        response.setTransactions(transactionResponses);
        return response;
    }
}
