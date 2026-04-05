package com.rental.service;

import com.rental.dto.request.BranchRequest;
import com.rental.dto.request.ShopRequest;
import com.rental.dto.response.BranchResponse;
import com.rental.dto.response.ShopResponse;
import com.rental.entity.Branch;
import com.rental.entity.Shop;
import com.rental.exception.ResourceNotFoundException;
import com.rental.repository.BranchRepository;
import com.rental.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopBranchService {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Transactional(readOnly = true)
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream().map(this::toShopResponse).collect(Collectors.toList());
    }

    @Transactional
    public ShopResponse createShop(ShopRequest request) {
        Shop shop = new Shop();
        shop.setShopName(request.getShopName());
        shop.setShopCode(request.getShopCode());
        shop.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return toShopResponse(shopRepository.save(shop));
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getBranches(Integer shopId) {
        List<Branch> branches = shopId != null
                ? branchRepository.findByShopShopIdAndIsActiveTrue(shopId)
                : branchRepository.findAll();
        return branches.stream().map(this::toBranchResponse).collect(Collectors.toList());
    }

    @Transactional
    public BranchResponse createBranch(BranchRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with ID: " + request.getShopId()));
        Branch branch = new Branch();
        branch.setShop(shop);
        branch.setBranchName(request.getBranchName());
        branch.setBranchCode(request.getBranchCode());
        branch.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return toBranchResponse(branchRepository.save(branch));
    }

    private ShopResponse toShopResponse(Shop shop) {
        ShopResponse response = new ShopResponse();
        response.setShopId(shop.getShopId());
        response.setShopName(shop.getShopName());
        response.setShopCode(shop.getShopCode());
        response.setIsActive(shop.getIsActive());
        return response;
    }

    private BranchResponse toBranchResponse(Branch branch) {
        BranchResponse response = new BranchResponse();
        response.setBranchId(branch.getBranchId());
        response.setShopId(branch.getShop().getShopId());
        response.setShopName(branch.getShop().getShopName());
        response.setBranchName(branch.getBranchName());
        response.setBranchCode(branch.getBranchCode());
        response.setIsActive(branch.getIsActive());
        return response;
    }
}
