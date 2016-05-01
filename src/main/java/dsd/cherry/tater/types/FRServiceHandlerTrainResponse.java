package dsd.cherry.tater.types;

/**
 * Created by James Beach on 4/29/2016.
 */
public class FRServiceHandlerTrainResponse {
    private boolean isTrained;
    private boolean serviceResponded;

    public FRServiceHandlerTrainResponse(boolean isTrained, boolean serviceResponded) {
        this.isTrained = isTrained;
        this.serviceResponded = serviceResponded;
    }

    public boolean getTrainingStatus() { return isTrained; }

    public boolean getServiceResponded() { return serviceResponded; }
}
