package com.rental.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ShopRequest {
    @NotBlank(message = "Shop name is required")
    private String shopName;
    @NotBlank(message = "Shop code is required")
    private String shopCode;
    private Boolean isActive = true;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
