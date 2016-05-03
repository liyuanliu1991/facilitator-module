package dsd.cherry.tater;

import dsd.cherry.tater.frservices.FRServiceHandler;
import dsd.cherry.tater.frservices.FRServiceHandlerTrainResponse;
import dsd.cherry.tater.types.FacilitatorID;
import dsd.cherry.tater.types.ImageData;
import dsd.cherry.tater.types.SMTrainData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by James Beach on 4/30/2016.
 */
public class ServiceManager {
    private int timeoutSec;
    private Map<String,FRServiceHandler> services;

    public ServiceManager(int timeoutSeconds) {
        this.timeoutSec = timeoutSeconds;
        services = new HashMap<String, FRServiceHandler>();
    }

    public int getTimeout() { return timeoutSec; }
    public void setTimeout(int timeoutSeconds) { this.timeoutSec = timeoutSeconds; }

    public void addService(FRServiceHandler service) {
        service.setTimeout(timeoutSec);
        services.put(service.getFRServiceName(), service);
    }

    public SMTrainData train(List<FacilitatorID> FACIDs, List<ImageData> images, String internalID) {
        boolean trained = true;

        Map<String,String> FRPersonIDs = new HashMap<String, String>();

        for (FacilitatorID f : FACIDs) {
            FRPersonIDs.put(f.getFRService(), f.getFRPersonID());
        }

        List<FRServiceHandlerTrainResponse> responses = new ArrayList<FRServiceHandlerTrainResponse>();

        for (Map.Entry<String,FRServiceHandler> s : services.entrySet()) {
            FRServiceHandlerTrainResponse response;
            if (FRPersonIDs.containsKey(s.getKey())) {
                response = s.getValue().train(FRPersonIDs.get(s.getKey()), images);
            }
            else {
                response = s.getValue().train(internalID, images);
            }
        }

        if (responses.isEmpty()) {
            return new SMTrainData(internalID, trained = false, responses, images);
        }

        for (FRServiceHandlerTrainResponse r : responses) {
            trained = trained && r.getTrainingStatus();
        }

        return new SMTrainData(internalID, trained, responses, images);
    }
}