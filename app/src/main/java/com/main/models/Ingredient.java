package com.main.models;

import androidx.annotation.NonNull;

public class Ingredient {
    String name;
    Long quantity;
    String unit;
    String category;
    String expiryDate;
    String lastUpdatedAt;

    // Required empty constructor for Firestore deserialization
    public Ingredient() {
    }

    public Ingredient(String name, Long quantity, String unit, String category, String expiryDate, String lastUpdatedAt) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.expiryDate = expiryDate;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", category='" + category + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", lastUpdatedAt='" + lastUpdatedAt + '\'' +
                '}';
    }
}
