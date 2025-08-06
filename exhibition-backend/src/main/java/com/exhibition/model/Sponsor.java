package com.exhibition.model;

public class Sponsor {
    private int sponsorId;
    private String name;
    private String contactPerson;
    private double contributionAmount;
    private String benefits;
    private String logoUrl; // or imageUrl

    // Constructor
    public Sponsor() {
    }

    // Getters and setters
    public int getSponsorId() { return sponsorId; }
    public void setSponsorId(int sponsorId) { this.sponsorId = sponsorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public double getContributionAmount() { return contributionAmount; }
    public void setContributionAmount(double contributionAmount) { this.contributionAmount = contributionAmount; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}