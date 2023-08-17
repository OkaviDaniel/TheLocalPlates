package com.example.thelocalplates8.Models;

import java.util.Map;

public class OrderModel {

    private String customerId;
    private String orderData;
    private String orderId;
    private double totalOrderPrice;
    private Map<String,Object> products;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderData() {
        return orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(double totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    public Map<String, Object> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Object> products) {
        this.products = products;
    }
}
