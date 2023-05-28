package com.example.thelocalplates8.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class BusinessModel {
    private int deliveryCost;
    private double rating;
    private String city;
    private String destinationLimit;
    private String firstName;
    private String lastName;
    private String openTime;
    private String closedTime;
    private String phone;
    private String street;
    private String userId;
    private String email;

    private GeoPoint location;


    public BusinessModel(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(int deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDestinationLimit() {
        return destinationLimit;
    }

    public void setDestinationLimit(String destinationLimit) {
        this.destinationLimit = destinationLimit;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getClosedTime() {
        return closedTime;
    }

    public void setClosedTime(String closedTime) {
        this.closedTime = closedTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
