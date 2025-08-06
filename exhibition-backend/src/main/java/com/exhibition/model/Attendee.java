package com.exhibition.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Attendee {
    private int attendeeId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String registrationDate;
    private String sessionIds;
    private String status;

    // Constructor that sets registration date automatically
    public Attendee() {
        this.registrationDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        this.sessionIds = ""; // Empty by default
        this.status = "active"; // Default status
    }

    // Getters and setters
    public int getAttendeeId() { return attendeeId; }
    public void setAttendeeId(int attendeeId) { this.attendeeId = attendeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRegistrationDate() { return registrationDate; }
    
    // Keep the setter but make it package-private (only accessible within the same package)
    void setRegistrationDate(String registrationDate) { 
        this.registrationDate = registrationDate; 
    }

    public String getSessionIds() { return sessionIds; }
    public void setSessionIds(String sessionIds) { this.sessionIds = sessionIds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}