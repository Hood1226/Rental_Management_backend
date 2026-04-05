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
import java.util.Arrays;
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

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private SequenceCounterRepository sequenceCounterRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;
    
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
        if (request.getShopId() == null || request.getBranchId() == null) {
            throw new IllegalArgumentException("Shop ID and Branch ID are required for booking");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setBookingNo(generateBookingNo(request.getBookingType()));
        booking.setBookingType(request.getBookingType());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        booking.setIsAdvanceBooking(request.getIsAdvanceBooking() != null ? request.getIsAdvanceBooking() : false);
        booking.setScheduledDate(request.getScheduledDate());
        booking.setAdvancePaymentAmount(request.getAdvancePaymentAmount() != null ? request.getAdvancePaymentAmount() : BigDecimal.ZERO);
        applyShopAndBranch(booking, request.getShopId(), request.getBranchId());
        
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
        auditLogRepository.save(createAuditLog("booking", savedBooking.getBookingId(), "INSERT", null,
                String.format("{\"bookingNo\":\"%s\",\"bookingType\":\"%s\"}", savedBooking.getBookingNo(), savedBooking.getBookingType())));
        
        // Create booking items and automatically update inventory
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            BigDecimal computedTotal = BigDecimal.ZERO;
            for (BookingRequest.BookingItemRequest itemRequest : request.getItems()) {
                BookingItem item = createBookingItem(savedBooking, itemRequest);
                if (item.getSubtotal() != null) {
                    computedTotal = computedTotal.add(item.getSubtotal());
                }
                
                // Automatically create inventory transaction and update inventory based on booking type
                updateInventoryForBooking(savedBooking, item, request.getBookingType());
            }
            savedBooking.setTotalAmount(computedTotal);
            bookingRepository.save(savedBooking);
        }
        
        // Create additional inventory transactions only (RETURN/DAMAGE); RENT_OUT/SALE are from booking items
        if (request.getTransactions() != null && !request.getTransactions().isEmpty()) {
            for (BookingRequest.InventoryTransactionRequest transactionRequest : request.getTransactions()) {
                String type = transactionRequest.getTransactionType();
                if ("RETURN".equals(type) || "DAMAGE".equals(type)) {
                    createInventoryTransaction(savedBooking, transactionRequest);
                }
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
        if (request.getShopId() != null || request.getBranchId() != null) {
            applyShopAndBranch(booking, request.getShopId(), request.getBranchId());
        }
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }
        if (request.getIsAdvanceBooking() != null) {
            booking.setIsAdvanceBooking(request.getIsAdvanceBooking());
        }
        if (request.getScheduledDate() != null) {
            booking.setScheduledDate(request.getScheduledDate());
        }
        if (request.getAdvancePaymentAmount() != null) {
            booking.setAdvancePaymentAmount(request.getAdvancePaymentAmount());
        }
        
        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<BookingItem> existingItems = bookingItemRepository.findByBookingBookingId(id);
            String bookingType = request.getBookingType() != null ? request.getBookingType() : booking.getBookingType();
            // Restore inventory for removed items before deleting
            for (BookingItem existingItem : existingItems) {
                restoreInventoryForBookingItem(existingItem, bookingType);
            }
            // Remove auto-created RENT_OUT/SALE transactions for this booking so we can recreate from new items
            List<InventoryTransaction> rentOrSaleTransactions = transactionRepository
                    .findByBookingBookingIdAndTransactionTypeIn(id, Arrays.asList("RENT_OUT", "SALE"));
            transactionRepository.deleteAll(rentOrSaleTransactions);
            bookingItemRepository.deleteAll(existingItems);
            
            // Create new items and reduce inventory for each
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (BookingRequest.BookingItemRequest itemRequest : request.getItems()) {
                BookingItem item = createBookingItem(booking, itemRequest);
                if (item.getSubtotal() != null) {
                    totalAmount = totalAmount.add(item.getSubtotal());
                }
                updateInventoryForBooking(booking, item, bookingType);
            }
            booking.setTotalAmount(totalAmount);
        } else if (request.getTotalAmount() != null) {
            booking.setTotalAmount(request.getTotalAmount());
        }
        
        // Update or create transactions if provided (only RETURN/DAMAGE for new; always allow update by id)
        if (request.getTransactions() != null && !request.getTransactions().isEmpty()) {
            for (BookingRequest.InventoryTransactionRequest transactionRequest : request.getTransactions()) {
                if (transactionRequest.getTransactionId() != null) {
                    // Skip if transaction was deleted (e.g. RENT_OUT/SALE replaced when items were updated)
                    if (transactionRepository.existsById(transactionRequest.getTransactionId())) {
                        updateInventoryTransaction(booking, transactionRequest);
                    }
                } else {
                    // Create only additional transaction types (RENT_OUT/SALE are created from booking items)
                    String type = transactionRequest.getTransactionType();
                    if ("RETURN".equals(type) || "DAMAGE".equals(type)) {
                        createInventoryTransaction(booking, transactionRequest);
                    }
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

        BigDecimal baseUnitPrice = request.getUnitPrice() != null ? request.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal defaultDiscount = variant.getProduct().getDiscountPercent() != null
                ? variant.getProduct().getDiscountPercent()
                : BigDecimal.ZERO;
        BigDecimal maxDiscount = variant.getProduct().getMaxDiscountPercent() != null
                ? variant.getProduct().getMaxDiscountPercent()
                : BigDecimal.ZERO;
        BigDecimal appliedDiscount = request.getDiscountPercent() != null ? request.getDiscountPercent() : defaultDiscount;
        if (appliedDiscount.compareTo(maxDiscount) > 0) {
            throw new IllegalArgumentException(
                    String.format("Discount %.2f%% exceeds max discount %.2f%% for product %s",
                            appliedDiscount, maxDiscount, variant.getProduct().getProductName()));
        }
        if (appliedDiscount.compareTo(BigDecimal.ZERO) < 0) {
            appliedDiscount = BigDecimal.ZERO;
        }

        BigDecimal discountAmountPerUnit = baseUnitPrice
                .multiply(appliedDiscount)
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal finalUnitPrice = baseUnitPrice.subtract(discountAmountPerUnit);
        if (finalUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalUnitPrice = BigDecimal.ZERO;
        }
        item.setDiscountPercent(appliedDiscount);
        item.setDiscountAmount(discountAmountPerUnit.multiply(BigDecimal.valueOf(request.getQuantity())));
        item.setFinalUnitPrice(finalUnitPrice);

        // Calculate subtotal if not provided
        BigDecimal subtotal = request.getSubtotal();
        if (subtotal == null && request.getQuantity() != null) {
            subtotal = finalUnitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
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
        
        // When a RENT_OUT is marked COMPLETED (returned), restore inventory
        if (request.getStatus() != null && "COMPLETED".equalsIgnoreCase(request.getStatus()) 
                && "RENT_OUT".equals(updatedTransaction.getTransactionType())) {
            restoreInventoryOnReturn(updatedTransaction);
        }
        
        // Update or create damage record if provided
        if (request.getDamageRecord() != null) {
            createOrUpdateDamageRecord(updatedTransaction, request.getDamageRecord());
        }
        
        return updatedTransaction;
    }
    
    /**
     * When a rented item is returned (transaction status = COMPLETED), add quantity back to inventory
     * and update availability status (AVAILABLE or PARTIALLY_RENTED).
     */
    private void restoreInventoryOnReturn(InventoryTransaction transaction) {
        if (!"RENT_OUT".equals(transaction.getTransactionType()) || transaction.getVariant() == null) {
            return;
        }
        Integer variantId = transaction.getVariant().getVariantId();
        Inventory inventory = inventoryRepository.findByVariantVariantId(variantId)
                .orElse(null);
        if (inventory == null) {
            logger.warn("Inventory not found for variant ID: {} when restoring on return", variantId);
            return;
        }
        int addBack = transaction.getQuantity() != null ? transaction.getQuantity() : 0;
        if (addBack <= 0) return;
        
        int newQuantity = inventory.getAvailableQuantity() + addBack;
        inventory.setAvailableQuantity(newQuantity);
        inventory.setAvailabilityStatus(newQuantity > 0 ? "AVAILABLE" : "UNAVAILABLE");
        inventory.setExpectedRestoreDate(null);
        inventory.setNextAvailabilityDate(null);
        inventoryRepository.save(inventory);
        logger.info("Restored inventory for variant ID: {} on return. New quantity: {}, Status: {}", 
                variantId, newQuantity, inventory.getAvailabilityStatus());
    }
    
    /**
     * Restore inventory when a booking item is removed (e.g. on booking update). Adds quantity back
     * and clears rented/sold status for that variant.
     */
    private void restoreInventoryForBookingItem(BookingItem item, String bookingType) {
        if (item.getVariant() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
            return;
        }
        Integer variantId = item.getVariant().getVariantId();
        Inventory inventory = inventoryRepository.findByVariantVariantId(variantId)
                .orElse(null);
        if (inventory == null) {
            logger.warn("Inventory not found for variant ID: {} when restoring for booking item", variantId);
            return;
        }
        int addBack = item.getQuantity();
        int newQuantity = inventory.getAvailableQuantity() + addBack;
        inventory.setAvailableQuantity(newQuantity);
        if (newQuantity > 0) {
            inventory.setAvailabilityStatus("AVAILABLE");
        }
        inventory.setExpectedRestoreDate(null);
        inventory.setNextAvailabilityDate(null);
        inventoryRepository.save(inventory);
        logger.info("Restored inventory for variant ID: {} (booking item). New quantity: {}", variantId, newQuantity);
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

    private void applyShopAndBranch(Booking booking, Integer shopId, Integer branchId) {
        if (shopId != null) {
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + shopId));
            booking.setShop(shop);
        }
        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));
            if (booking.getShop() != null && !branch.getShop().getShopId().equals(booking.getShop().getShopId())) {
                throw new IllegalArgumentException("Branch does not belong to selected shop");
            }
            booking.setBranch(branch);
        }
    }

    private String generateBookingNo(String bookingType) {
        String yy = String.valueOf(LocalDate.now().getYear()).substring(2);
        String typeCode = "SALE".equalsIgnoreCase(bookingType) ? "S" : "R";
        String sequenceKey = "BOOKING_" + typeCode + "_" + yy;
        SequenceCounter counter = sequenceCounterRepository.findBySequenceKeyForUpdate(sequenceKey)
                .orElseGet(() -> {
                    SequenceCounter created = new SequenceCounter();
                    created.setSequenceKey(sequenceKey);
                    created.setLastNumber(0);
                    return created;
                });
        int next = counter.getLastNumber() + 1;
        counter.setLastNumber(next);
        sequenceCounterRepository.save(counter);
        return "HC" + typeCode + yy + String.format("%03d", next);
    }

    private AuditLog createAuditLog(String table, Integer recordId, String action, String oldData, String newData) {
        AuditLog log = new AuditLog();
        log.setTableName(table);
        log.setRecordId(recordId);
        log.setAction(action);
        log.setOldData(oldData);
        log.setNewData(newData);
        log.setChangedBy("system");
        return log;
    }
    
    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getBookingId());
        response.setBookingNo(booking.getBookingNo());
        response.setCustomerId(booking.getCustomer().getCustomerId());
        response.setCustomerName(booking.getCustomer().getCustomerName());
        if (booking.getShop() != null) {
            response.setShopId(booking.getShop().getShopId());
            response.setShopName(booking.getShop().getShopName());
        }
        if (booking.getBranch() != null) {
            response.setBranchId(booking.getBranch().getBranchId());
            response.setBranchName(booking.getBranch().getBranchName());
        }
        response.setBookingType(booking.getBookingType());
        response.setBookingDate(booking.getBookingDate());
        response.setStatus(booking.getStatus());
        response.setTotalAmount(booking.getTotalAmount());
        response.setIsAdvanceBooking(booking.getIsAdvanceBooking());
        response.setScheduledDate(booking.getScheduledDate());
        response.setAdvancePaymentAmount(booking.getAdvancePaymentAmount());
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
            itemResponse.setDiscountPercent(item.getDiscountPercent());
            itemResponse.setDiscountAmount(item.getDiscountAmount());
            itemResponse.setFinalUnitPrice(item.getFinalUnitPrice());
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
