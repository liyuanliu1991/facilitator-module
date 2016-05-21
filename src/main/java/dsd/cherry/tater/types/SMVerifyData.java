package dsd.cherry.tater.types;

import dsd.cherry.tater.frservices.FRServiceHandlerVerifyResponse;

import java.util.List;

/**
 * Created by James Beach on 5/3/2016.
 */
public class SMVerifyData {
    private List<FRServiceHandlerVerifyResponse> inConsensus;
    private List<FRServiceHandlerVerifyResponse> notInConsensus;
    private int     totalServices,  totalResponded;
    private double  confidence,     cutoff;
    private boolean match,          consensus;

    public SMVerifyData(boolean                                 match,
                        List<FRServiceHandlerVerifyResponse>    inConsensus,
                        List<FRServiceHandlerVerifyResponse>    notInConsensus,
                        int                                     totalServices,
                        int                                     totalResponded,
                        double                                  confidence,
                        double                                  cutoff,
                        boolean                                 consensus) {
        this.inConsensus = inConsensus;
        this.totalServices = totalServices;
        this.totalResponded = totalResponded;
        this.confidence = confidence;
        this.cutoff = cutoff;
        this.match = match;
        this.consensus = consensus;
    }

    public boolean isMatch() { return match; }

    public List<FRServiceHandlerVerifyResponse> getInConsensus() { return inConsensus; }

    public List<FRServiceHandlerVerifyResponse> getNotInConsensus() { return notInConsensus; }

    public int getTotalServices() { return totalServices; }

    public int getTotalResponsed() { return totalResponded; }

    public double getConfidence() { return confidence; }

    public double getCutoff() { return cutoff; }

    public boolean isConsensus() { return consensus; }
}
