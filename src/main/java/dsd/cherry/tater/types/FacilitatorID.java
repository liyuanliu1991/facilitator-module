package dsd.cherry.tater.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by James Beach on 4/29/2016.
 */
public class FacilitatorID {
    private String FR_service;
    private String FR_userID;

    @JsonProperty("facId")
    public void setFRPersonID(String FR_userID) { this.FR_userID = FR_userID; }
    @JsonProperty("facId")
    public String getFRPersonID() { return FR_userID; }

    @JsonProperty("facType")
    public void setFRService(String FR_service) { this.FR_service = FR_service; }
    @JsonProperty("facType")
    public String getFRService() { return FR_service; }
}
