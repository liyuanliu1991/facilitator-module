package dsd.cherry.tater.types;

/**
 * Created by James Beach on 4/29/2016.
 */
public class FRServiceHandlerVerifyResponse {
    private float confidence;
    private String FRPersonID;
    private boolean serviceResponded;

    public FRServiceHandlerVerifyResponse(float confidence, String FRPersonID, boolean serviceResponded) {
        this.confidence = confidence;
        this.FRPersonID = FRPersonID;
        this.serviceResponded = serviceResponded;
    }

    public float getConfidenceValue() { return confidence; }

    public String getFRPersonID() { return FRPersonID; }

    public boolean getServiceResponded() { return serviceResponded; }
}
