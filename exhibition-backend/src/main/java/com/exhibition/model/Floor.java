package com.exhibition.model;

public class Floor {
    private int floorId;
    private int floorNumber;
    private String layoutImage;
    private String exhibitorIds;
    private String conferenceIds;

    // Constructor
    public Floor() {
    }

    // Getters and setters
    public int getFloorId() { return floorId; }
    public void setFloorId(int floorId) { this.floorId = floorId; }

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public String getLayoutImage() { return layoutImage; }
    public void setLayoutImage(String layoutImage) { this.layoutImage = layoutImage; }

    public String getExhibitorIds() { return exhibitorIds; }
    public void setExhibitorIds(String exhibitorIds) { this.exhibitorIds = exhibitorIds; }

    public String getConferenceIds() { return conferenceIds; }
    public void setConferenceIds(String conferenceIds) { this.conferenceIds = conferenceIds; }
}