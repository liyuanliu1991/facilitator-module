package dsd.cherry.tater;

import dsd.cherry.tater.frservices.FRServiceHandler;
import dsd.cherry.tater.frservices.FRServiceHandlerTrainResponse;
import dsd.cherry.tater.frservices.FRServiceHandlerVerifyResponse;
import dsd.cherry.tater.types.FacilitatorID;
import dsd.cherry.tater.types.ImageData;
import dsd.cherry.tater.types.SMTrainData;
import dsd.cherry.tater.types.SMVerifyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Andrew James Beach
 * @version 0.8
 * Created by James Beach on 4/30/2016.
 */
class ServiceManager {
    private int timeoutSec;
    private Map<String,FRServiceHandler> services;

    public ServiceManager(int timeoutSeconds) {
        this.timeoutSec = timeoutSeconds;
        services = new HashMap<>();
    }

    public int getTimeout() { return timeoutSec; }
    public void setTimeout(int timeoutSeconds) { this.timeoutSec = timeoutSeconds; }

    public void addService(FRServiceHandler service) {
        service.setTimeout(timeoutSec);
        services.put(service.getFRServiceName(), service);
    }

    SMTrainData train(final String internalID, List<FacilitatorID> FACIDs, List<ImageData> images) {
        boolean trained = true;

        Map<String, String> FRPersonIDs = new HashMap<>();

        if (FACIDs != null) {
            for (FacilitatorID f : FACIDs) {
                FRPersonIDs.put(f.getFRService(), f.getFRPersonID());
            }
        }

        List<FRServiceHandlerTrainResponse> responses = new ArrayList<>();
        List<FacilitatorID> facIDs = new ArrayList<>();

        // for each supported service
        for (Map.Entry<String,FRServiceHandler> s : services.entrySet()) {
            FRServiceHandlerTrainResponse response;
            // further train the service if it has been trained before
            // (NOT SUPPORTED AT THIS TIME)
            // TODO: Remove or comment out this code.
            if (FRPersonIDs.containsKey(s.getKey())) {
                response = s.getValue().train(FRPersonIDs.get(s.getKey()), images);
            }
            // or else train it anew.
            else {
                response = s.getValue().train(internalID, images);
            }
            responses.add(response);
            if (response.getTrainingStatus()) {
                FacilitatorID id = new FacilitatorID();
                id.setFRPersonID(response.getFRPersonID());
                id.setFRService(response.getServiceName());
                facIDs.add(id);
            }
        }

        if (responses.isEmpty()) {
            return new SMTrainData(internalID, trained = false, facIDs, images);
        }

        for (FRServiceHandlerTrainResponse r : responses) {
            trained = trained && r.getTrainingStatus();
        }

        return new SMTrainData(internalID, trained, facIDs, images);
    }

    SMVerifyData verify(List<FacilitatorID> FACIDs, ImageData image) {
        boolean match;
        double confidence = 0, cutoff = 0;
        int totalServices = services.size();
        int totalResponded = 0;
        boolean consensus = true;
        List<FRServiceHandlerVerifyResponse> inConsensus = new ArrayList<>();
        List<FRServiceHandlerVerifyResponse> notInConsensus = new ArrayList<>();

        for (FacilitatorID facID : FACIDs) {
            // TODO: What if the supplied FR service name doesn't exist in our list of services?
            FRServiceHandlerVerifyResponse response =
                services.get(facID.getFRService()).verify(facID.getFRPersonID(), image);

            if (response.getServiceResponded()) {
                ++totalResponded;
                confidence += response.getConfidence();
                cutoff += response.getCutoff();
                if (!response.getFRPersonID().equals(facID.getFRPersonID())) {
                    consensus = false;
                    notInConsensus.add(response);
                }
                else {
                    inConsensus.add(response);
                }
            }
        }

        confidence = confidence / totalResponded;
        cutoff = cutoff / totalResponded;
        match = consensus && (confidence >= cutoff);

        return new SMVerifyData(match,
                                inConsensus,
                                notInConsensus,
                                totalServices,
                                totalResponded,
                                confidence,
                                cutoff,
                                consensus);
    }
}