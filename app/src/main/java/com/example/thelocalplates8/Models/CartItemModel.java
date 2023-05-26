package com.example.thelocalplates8.Models;

public class CartItemModel {
    private String productId;
    private int quantity;
    private double totalPrice;
    private double price;

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CartItemModel(){

    }

    public CartItemModel(String productId, int quantity, double price, String title) {
        this.productId = productId;
        this.quantity = quantity;
        this.price =price;
        this.totalPrice = price*quantity;
        this.title = title;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
