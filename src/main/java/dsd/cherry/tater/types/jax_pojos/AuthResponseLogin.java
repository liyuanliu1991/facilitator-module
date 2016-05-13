package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.frservices.FRServiceHandlerVerifyResponse;
import dsd.cherry.tater.types.SMVerifyData;
import dsd.cherry.tater.types.StatusCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/3/2016.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class AuthResponseLogin {
    private boolean match;
    private List<StatusCode> codes;
    private int HTTPStatusCode;

    public AuthResponseLogin() {
        codes = new ArrayList<StatusCode>();
    }

    public void addStatusCode(StatusCode code) {
        codes.add(code);
    }

    @JsonProperty("errors")
    public List<StatusCode> getStatusCodes() {
        return codes;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    @JsonProperty("success")
    public boolean getMatch() { return match; }

    public void setHTTPStatusCode(int code) { this.HTTPStatusCode = code; }

    public int getHTTPStatusCode() { return HTTPStatusCode; }
}
