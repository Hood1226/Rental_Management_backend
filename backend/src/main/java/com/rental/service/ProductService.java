package com.rental.service;

import com.rental.dto.request.ProductRequest;
import com.rental.dto.response.ProductResponse;
import com.rental.entity.*;
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
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductVariantRepository variantRepository;
    
    @Autowired
    private ProductSizeRepository sizeRepository;
    
    @Autowired
    private RentPriceRepository rentPriceRepository;
    
    @Autowired
    private SalePriceRepository salePriceRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        logger.debug("Fetching all products with variants");
        List<Product> products = productRepository.findAll();
        logger.info("Found {} products", products.size());
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Integer id) {
        logger.debug("Fetching product with ID: {} and all variants", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found with ID: " + id);
                });
        logger.info("Product found: {}", product.getProductName());
        return convertToResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        logger.info("Creating new product: {} with {} variants", 
                request.getProductName(), 
                request.getVariants() != null ? request.getVariants().size() : 0);
        
        // Create product
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setDepositAmount(request.getDepositAmount());
        product.setIsForSale(request.getIsForSale() != null ? request.getIsForSale() : false);
        product.setIsForRent(request.getIsForRent() != null ? request.getIsForRent() : true);
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getProductId());
        
        // Create variants with prices and inventory
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (ProductRequest.ProductVariantRequest variantRequest : request.getVariants()) {
                createVariantWithChildren(savedProduct, variantRequest);
            }
        }
        
        return convertToResponse(savedProduct);
    }
    
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        logger.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found with ID: " + id);
                });
        
        // Update product fields
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setDepositAmount(request.getDepositAmount());
        if (request.getIsForSale() != null) {
            product.setIsForSale(request.getIsForSale());
        }
        if (request.getIsForRent() != null) {
            product.setIsForRent(request.getIsForRent());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        
        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully: {}", updatedProduct.getProductName());
        
        // Update variants if provided
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            // Delete existing variants (or update if variantId is provided)
            List<ProductVariant> existingVariants = variantRepository.findByProductProductId(id);
            for (ProductVariant existing : existingVariants) {
                // Check if variant still exists in request
                boolean existsInRequest = request.getVariants().stream()
                        .anyMatch(v -> existing.getSize().getSizeId().equals(v.getSizeId()));
                
                if (!existsInRequest) {
                    // Delete variant and its children
                    deleteVariantAndChildren(existing.getVariantId());
                }
            }
            
            // Create or update variants
            for (ProductRequest.ProductVariantRequest variantRequest : request.getVariants()) {
                ProductVariant existingVariant = variantRepository
                        .findByProductProductIdAndSizeSizeId(id, variantRequest.getSizeId())
                        .orElse(null);
                
                if (existingVariant != null) {
                    updateVariantWithChildren(existingVariant, variantRequest);
                } else {
                    createVariantWithChildren(updatedProduct, variantRequest);
                }
            }
        }
        
        return convertToResponse(updatedProduct);
    }
    
    @Transactional
    public void deleteProduct(Integer id) {
        logger.info("Deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found with ID: " + id);
                });
        
        // Delete all variants and their children first
        List<ProductVariant> variants = variantRepository.findByProductProductId(id);
        for (ProductVariant variant : variants) {
            deleteVariantAndChildren(variant.getVariantId());
        }
        
        // Soft delete product
        product.setIsActive(false);
        productRepository.save(product);
        logger.info("Product soft deleted successfully: {}", product.getProductName());
    }
    
    private void createVariantWithChildren(Product product, ProductRequest.ProductVariantRequest request) {
        // Validate size exists
        ProductSize size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new ResourceNotFoundException("Size not found with ID: " + request.getSizeId()));
        
        // Create variant
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSize(size);
        variant.setPurchasePrice(request.getPurchasePrice());
        ProductVariant savedVariant = variantRepository.save(variant);
        logger.debug("Created variant ID: {} for product ID: {}", savedVariant.getVariantId(), product.getProductId());
        
        // Create rent price if product is for rent
        if (product.getIsForRent() && request.getRentPrice() != null) {
            RentPrice rentPrice = new RentPrice();
            rentPrice.setVariant(savedVariant);
            rentPrice.setRentPrice(request.getRentPrice());
            rentPrice.setEffectiveFrom(request.getRentEffectiveFrom() != null ? request.getRentEffectiveFrom() : LocalDate.now());
            rentPrice.setEffectiveTo(request.getRentEffectiveTo());
            rentPriceRepository.save(rentPrice);
            logger.debug("Created rent price for variant ID: {}", savedVariant.getVariantId());
        }
        
        // Create sale price if product is for sale
        if (product.getIsForSale() && request.getSalePrice() != null) {
            SalePrice salePrice = new SalePrice();
            salePrice.setVariant(savedVariant);
            salePrice.setSalePrice(request.getSalePrice());
            salePrice.setEffectiveFrom(request.getSaleEffectiveFrom() != null ? request.getSaleEffectiveFrom() : LocalDate.now());
            salePrice.setEffectiveTo(request.getSaleEffectiveTo());
            salePriceRepository.save(salePrice);
            logger.debug("Created sale price for variant ID: {}", savedVariant.getVariantId());
        }
        
        // Create inventory
        Inventory inventory = new Inventory();
        inventory.setVariant(savedVariant);
        inventory.setAvailableQuantity(request.getAvailableQuantity() != null ? request.getAvailableQuantity() : 0);
        inventory.setAvailabilityStatus(request.getAvailabilityStatus());
        inventory.setExpectedRestoreDate(request.getExpectedRestoreDate());
        inventory.setNextAvailabilityDate(request.getNextAvailabilityDate());
        inventoryRepository.save(inventory);
        logger.debug("Created inventory for variant ID: {}", savedVariant.getVariantId());
    }
    
    private void updateVariantWithChildren(ProductVariant variant, ProductRequest.ProductVariantRequest request) {
        variant.setPurchasePrice(request.getPurchasePrice());
        variantRepository.save(variant);
        
        // Update rent price
        if (request.getRentPrice() != null) {
            RentPrice rentPrice = rentPriceRepository.findCurrentPrice(variant.getVariantId(), LocalDate.now())
                    .orElse(new RentPrice());
            rentPrice.setVariant(variant);
            rentPrice.setRentPrice(request.getRentPrice());
            if (rentPrice.getRentPriceId() == null) {
                rentPrice.setEffectiveFrom(request.getRentEffectiveFrom() != null ? request.getRentEffectiveFrom() : LocalDate.now());
            }
            rentPrice.setEffectiveTo(request.getRentEffectiveTo());
            rentPriceRepository.save(rentPrice);
        }
        
        // Update sale price
        if (request.getSalePrice() != null) {
            SalePrice salePrice = salePriceRepository.findCurrentPrice(variant.getVariantId(), LocalDate.now())
                    .orElse(new SalePrice());
            salePrice.setVariant(variant);
            salePrice.setSalePrice(request.getSalePrice());
            if (salePrice.getSalePriceId() == null) {
                salePrice.setEffectiveFrom(request.getSaleEffectiveFrom() != null ? request.getSaleEffectiveFrom() : LocalDate.now());
            }
            salePrice.setEffectiveTo(request.getSaleEffectiveTo());
            salePriceRepository.save(salePrice);
        }
        
        // Update inventory
        Inventory inventory = inventoryRepository.findByVariantVariantId(variant.getVariantId())
                .orElse(new Inventory());
        inventory.setVariant(variant);
        inventory.setAvailableQuantity(request.getAvailableQuantity() != null ? request.getAvailableQuantity() : 0);
        inventory.setAvailabilityStatus(request.getAvailabilityStatus());
        inventory.setExpectedRestoreDate(request.getExpectedRestoreDate());
        inventory.setNextAvailabilityDate(request.getNextAvailabilityDate());
        inventoryRepository.save(inventory);
    }
    
    private void deleteVariantAndChildren(Integer variantId) {
        // Delete inventory
        inventoryRepository.findByVariantVariantId(variantId).ifPresent(inventoryRepository::delete);
        
        // Delete prices
        rentPriceRepository.findAll().stream()
                .filter(rp -> rp.getVariant().getVariantId().equals(variantId))
                .forEach(rentPriceRepository::delete);
        
        salePriceRepository.findAll().stream()
                .filter(sp -> sp.getVariant().getVariantId().equals(variantId))
                .forEach(salePriceRepository::delete);
        
        // Delete variant
        variantRepository.deleteById(variantId);
        logger.debug("Deleted variant ID: {} and all its children", variantId);
    }
    
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setCategory(product.getCategory());
        response.setDescription(product.getDescription());
        response.setDepositAmount(product.getDepositAmount());
        response.setIsForSale(product.getIsForSale());
        response.setIsForRent(product.getIsForRent());
        response.setIsActive(product.getIsActive());
        response.setCreatedBy(product.getCreatedBy());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedBy(product.getUpdatedBy());
        response.setUpdatedAt(product.getUpdatedAt());
        
        // Load variants with all children
        List<ProductVariant> variants = variantRepository.findByProductProductId(product.getProductId());
        List<ProductResponse.ProductVariantResponse> variantResponses = new ArrayList<>();
        
        for (ProductVariant variant : variants) {
            ProductResponse.ProductVariantResponse variantResponse = new ProductResponse.ProductVariantResponse();
            variantResponse.setVariantId(variant.getVariantId());
            variantResponse.setSizeId(variant.getSize().getSizeId());
            variantResponse.setSizeCode(variant.getSize().getSizeCode());
            variantResponse.setPurchasePrice(variant.getPurchasePrice());
            variantResponse.setCreatedAt(variant.getCreatedAt());
            variantResponse.setUpdatedAt(variant.getUpdatedAt());
            
            // Load rent price
            rentPriceRepository.findCurrentPrice(variant.getVariantId(), LocalDate.now())
                    .ifPresent(rentPrice -> {
                        variantResponse.setRentPrice(rentPrice.getRentPrice());
                        variantResponse.setRentEffectiveFrom(rentPrice.getEffectiveFrom());
                        variantResponse.setRentEffectiveTo(rentPrice.getEffectiveTo());
                    });
            
            // Load sale price
            salePriceRepository.findCurrentPrice(variant.getVariantId(), LocalDate.now())
                    .ifPresent(salePrice -> {
                        variantResponse.setSalePrice(salePrice.getSalePrice());
                        variantResponse.setSaleEffectiveFrom(salePrice.getEffectiveFrom());
                        variantResponse.setSaleEffectiveTo(salePrice.getEffectiveTo());
                    });
            
            // Load inventory
            inventoryRepository.findByVariantVariantId(variant.getVariantId())
                    .ifPresent(inventory -> {
                        variantResponse.setInventoryId(inventory.getInventoryId());
                        variantResponse.setAvailableQuantity(inventory.getAvailableQuantity());
                        variantResponse.setAvailabilityStatus(inventory.getAvailabilityStatus());
                        variantResponse.setExpectedRestoreDate(inventory.getExpectedRestoreDate());
                        variantResponse.setNextAvailabilityDate(inventory.getNextAvailabilityDate());
                    });
            
            variantResponses.add(variantResponse);
        }
        
        response.setVariants(variantResponses);
        return response;
    }
}
