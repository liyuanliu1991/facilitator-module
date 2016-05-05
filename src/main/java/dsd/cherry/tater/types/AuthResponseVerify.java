package dsd.cherry.tater.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.frservices.FRServiceHandlerVerifyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/3/2016.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class AuthResponseVerify extends SMVerifyData {
    List<StatusCode> codes;

    public AuthResponseVerify(String                                  internalID,
                        boolean                                 match,
                        List<FRServiceHandlerVerifyResponse> inConsensus,
                        List<FRServiceHandlerVerifyResponse>    notInConsensus,
                        int                                     totalServices,
                        int                                     totalResponded,
                        float                                   confidence,
                        float                                   cutoff,
                        boolean                                 consensus) {

        super(internalID,
                match,
                inConsensus,
                notInConsensus,
                totalServices,
                totalResponded,
                confidence,
                cutoff,
                consensus);

        codes = new ArrayList<StatusCode>();
    }
}
