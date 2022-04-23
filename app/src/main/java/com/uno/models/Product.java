package com.uno.models;

import java.util.ArrayList;

public class Product {

    private String id;
    private String userId;
    private String title;
    private String description;
    private ArrayList<String> images;
    private String category;
    private double price;

    public Product(String id, String userId, String title, String description, ArrayList<String> images, String category, double price) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.images = images;
        this.category = category;
        this.price = price;
    }

    public Product() {

    }

    public String getUSerId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
