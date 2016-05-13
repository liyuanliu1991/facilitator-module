package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.FacilitatorID;
import dsd.cherry.tater.types.ImageData;
import dsd.cherry.tater.types.StatusCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/2/2016.
 */
public class AuthResponseRegister {
    private List<FacilitatorID> FACIDs;
    private List<StatusCode> codes;
    private boolean isTrained;
    private int HTTPCode;

    public AuthResponseRegister() {
        FACIDs = new ArrayList<>();
        codes = new ArrayList<>();
        isTrained = false;
        HTTPCode = 200;
    }

    @JsonProperty("facilitatorIds")
    public void setFACIDs(List<FacilitatorID> FACIDs) { this.FACIDs = FACIDs; }
    @JsonProperty("facilitatorIds")
    public List<FacilitatorID> getFACIDs() { return FACIDs; }

    @JsonProperty("success")
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
    public void setCodes(List<StatusCode> codes) { this.codes = codes; }
    @JsonIgnore
    public void addCode(StatusCode code) { this.codes.add(code); }
    @JsonProperty("errors")
    public List<StatusCode> getCodes() { return codes; }
}
