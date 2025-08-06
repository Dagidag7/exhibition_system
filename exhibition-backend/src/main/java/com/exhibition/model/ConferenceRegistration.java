package com.exhibition.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConferenceRegistration {
    @JsonProperty("registration_id")
    private Integer registrationId;
    
    @JsonProperty("conference_id")
    private Integer conferenceId;
    
    @JsonProperty("attendee_id")
    private Integer attendeeId;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("job_title")
    private String jobTitle;
    
    @JsonProperty("dietary_restrictions")
    private String dietaryRestrictions;
    
    @JsonProperty("special_requirements")
    private String specialRequirements;
    
    @JsonProperty("registration_date")
    private String registrationDate;
    
    @JsonProperty("status")
    private String status;

    public ConferenceRegistration() {
        this.status = "confirmed";
        this.registrationDate = java.time.LocalDate.now().toString();
    }

    public ConferenceRegistration(Integer conferenceId, String firstName, String lastName, 
                                 String email, String phone, String company, String jobTitle,
                                 String dietaryRestrictions, String specialRequirements) {
        this();
        this.conferenceId = conferenceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.jobTitle = jobTitle;
        this.dietaryRestrictions = dietaryRestrictions;
        this.specialRequirements = specialRequirements;
    }

    // Getters and Setters
    public Integer getRegistrationId() { return registrationId; }
    public void setRegistrationId(Integer registrationId) { this.registrationId = registrationId; }

    public Integer getConferenceId() { return conferenceId; }
    public void setConferenceId(Integer conferenceId) { this.conferenceId = conferenceId; }

    public Integer getAttendeeId() { return attendeeId; }
    public void setAttendeeId(Integer attendeeId) { this.attendeeId = attendeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(String dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }

    public String getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(String specialRequirements) { this.specialRequirements = specialRequirements; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 