package com.exhibition.model;

public class Product {
    private int productId;
    private String name;
    private String description;
    private int exhibitorId; // Foreign key to Exhibitor
    private String category;
    private String imageUrl;

    // Constructor
    public Product() {
    }

    // Getters and setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getExhibitorId() { return exhibitorId; }
    public void setExhibitorId(int exhibitorId) { this.exhibitorId = exhibitorId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}