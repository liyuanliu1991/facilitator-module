package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.ErrorCode;
import dsd.cherry.tater.types.ErrorCodes;
import dsd.cherry.tater.types.FacilitatorID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/2/2016.
 */
public class AuthResponseRegister {
    private List<FacilitatorID> FACIDs;
    private List<ErrorCode> codes;
    private boolean isTrained;
    private int HTTPCode;

    public AuthResponseRegister() {
        FACIDs = new ArrayList<>();
        codes = new ArrayList<>();
        isTrained = false;
        HTTPCode = 200;
    }

    @JsonIgnore
    public void setFACIDs(List<FacilitatorID> FACIDs) { this.FACIDs = FACIDs; }
    @JsonProperty("facilitatorIds")
    public List<FacilitatorID> getFACIDs() { return FACIDs; }

    @JsonIgnore
    public void setTrainingStatus(boolean isTrained) { this.isTrained = isTrained; }
    @JsonIgnore
    public void setAsTrained() { this.isTrained = true; }
    @JsonIgnore
    public void setAsUntrained() { this.isTrained = false; }
    @JsonProperty("success")
    public boolean getTrainingStatus() { return isTrained; }

    @JsonIgnore
    public void setHTTPCode(int HTTPCode) { this.HTTPCode = HTTPCode; }
    @JsonIgnore
    public int getHTTPCode() { return HTTPCode; }

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
