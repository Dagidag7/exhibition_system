package com.exhibition.model;

public class Speaker {
    private int speakerId;
    private String name;
    private String bio;
    private String expertise;
    private String email;
    private String phone;
    private String organization;

    public Speaker() {}

    public Speaker(int speakerId, String name, String bio, String expertise, String email, String phone, String organization) {
        this.speakerId = speakerId;
        this.name = name;
        this.bio = bio;
        this.expertise = expertise;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
    }

    public int getSpeakerId() { return speakerId; }
    public void setSpeakerId(int speakerId) { this.speakerId = speakerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
}