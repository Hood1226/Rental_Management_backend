package com.rental.service;

import com.rental.dto.request.CustomerRequest;
import com.rental.dto.response.CustomerResponse;
import com.rental.entity.AppUser;
import com.rental.entity.CustomerMaster;
import com.rental.exception.ResourceNotFoundException;
import com.rental.repository.AppUserRepository;
import com.rental.repository.CustomerMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    @Autowired
    private CustomerMasterRepository customerRepository;
    
    @Autowired
    private AppUserRepository appUserRepository;
    
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        logger.debug("Fetching all customers");
        List<CustomerMaster> customers = customerRepository.findAll();
        logger.info("Found {} customers", customers.size());
        return customers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Integer id) {
        logger.debug("Fetching customer with ID: {}", id);
        CustomerMaster customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with ID: " + id);
                });
        logger.info("Customer found: {}", customer.getCustomerName());
        return convertToResponse(customer);
    }
    
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        logger.info("Creating new customer: {}", request.getCustomerName());
        
        CustomerMaster customer = new CustomerMaster();
        
        // Link to AppUser if userId is provided
        if (request.getUserId() != null) {
            AppUser user = appUserRepository.findById(request.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User not found with ID: {}", request.getUserId());
                        return new ResourceNotFoundException("User not found with ID: " + request.getUserId());
                    });
            customer.setUser(user);
        }
        
        customer.setCustomerName(request.getCustomerName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setWhatsappNumber(request.getWhatsappNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setIdProofType(request.getIdProofType());
        customer.setIdProofNo(request.getIdProofNo());
        customer.setIdProofImg(request.getIdProofImg());
        
        CustomerMaster savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully with ID: {}", savedCustomer.getCustomerId());
        
        return convertToResponse(savedCustomer);
    }
    
    @Transactional
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {
        logger.info("Updating customer with ID: {}", id);
        
        CustomerMaster customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found with ID: {}", id);
                    return new ResourceNotFoundException("Customer not found with ID: " + id);
                });
        
        // Update user link if provided
        if (request.getUserId() != null) {
            AppUser user = appUserRepository.findById(request.getUserId())
                    .orElseThrow(() -> {
                        logger.warn("User not found with ID: {}", request.getUserId());
                        return new ResourceNotFoundException("User not found with ID: " + request.getUserId());
                    });
            customer.setUser(user);
        }
        
        customer.setCustomerName(request.getCustomerName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setWhatsappNumber(request.getWhatsappNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setIdProofType(request.getIdProofType());
        customer.setIdProofNo(request.getIdProofNo());
        if (request.getIdProofImg() != null) {
            customer.setIdProofImg(request.getIdProofImg());
        }
        
        CustomerMaster updatedCustomer = customerRepository.save(customer);
        logger.info("Customer updated successfully: {}", updatedCustomer.getCustomerName());
        
        return convertToResponse(updatedCustomer);
    }
    
    private CustomerResponse convertToResponse(CustomerMaster customer) {
        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(customer.getCustomerId());
        response.setUserId(customer.getUser() != null ? customer.getUser().getUserId() : null);
        response.setCustomerName(customer.getCustomerName());
        response.setMobileNumber(customer.getMobileNumber());
        response.setWhatsappNumber(customer.getWhatsappNumber());
        response.setEmail(customer.getEmail());
        response.setAddress(customer.getAddress());
        response.setIdProofType(customer.getIdProofType());
        response.setIdProofNo(customer.getIdProofNo());
        response.setIdProofImg(customer.getIdProofImg());
        response.setCreatedBy(customer.getCreatedBy());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedBy(customer.getUpdatedBy());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }
}

