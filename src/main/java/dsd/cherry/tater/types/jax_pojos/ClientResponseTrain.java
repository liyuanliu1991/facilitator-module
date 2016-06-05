package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.ErrorCode;
import dsd.cherry.tater.types.ErrorCodes;
import dsd.cherry.tater.types.FacilitatorID;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/2/2016.
 */
public class ClientResponseTrain {
    private List<FacilitatorID> FacIDs;
    private List<ErrorCode> codes;
    private boolean isTrained;
    private Response.Status HTTPCode;

    public ClientResponseTrain() {
        FacIDs = new ArrayList<>();
        codes = new ArrayList<>();
        isTrained = false;
        HTTPCode = Response.Status.OK;
    }

    @JsonIgnore
    public void setFacIDs(List<FacilitatorID> FACIDs) { this.FacIDs = FACIDs; }
    @JsonIgnore
    public void addFacID(FacilitatorID FACID) { this.FacIDs.add(FACID); }
    @JsonProperty("facilitatorIds")
    public List<FacilitatorID> getFacIDs() { return FacIDs; }

    @JsonIgnore
    public void setTrainingStatus(boolean isTrained) { this.isTrained = isTrained; }
    @JsonIgnore
    public void setAsTrained() { this.isTrained = true; }
    @JsonIgnore
    public void setAsUntrained() { this.isTrained = false; }
    @JsonProperty("success")
    public boolean getTrainingStatus() { return isTrained; }

    @JsonIgnore
    public void setHTTPCode(int HTTPCode) { this.HTTPCode = Response.Status.fromStatusCode(HTTPCode); }
    @JsonIgnore
    public void setHTTPCode(Response.Status HTTPCode) { this.HTTPCode = HTTPCode; }
    @JsonIgnore
    public Response.Status getHTTPCode() { return HTTPCode; }

    @JsonIgnore
    public void setCodes(List<ErrorCode> codes) { this.codes = codes; }
    @JsonIgnore
    public void addCode(ErrorCode code) { this.codes.add(code); }
    @JsonIgnore
    public void addCode(ErrorCodes code) {this.codes.add(new ErrorCode(code)); }
    @JsonIgnore
    public void addCodes(List<ErrorCode> codes) { this.codes.addAll(codes); }
    @JsonProperty("errors")
    public List<ErrorCode> getCodes() { return codes; }
}
