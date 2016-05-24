package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.ErrorCodes;

import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/3/2016.
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ClientResponseVerify {
    private boolean match;
    private List<ErrorCodes> codes;
    private Status HTTPStatusCode;

    public ClientResponseVerify() {
        codes = new ArrayList<ErrorCodes>();
    }

    public void addStatusCode(ErrorCodes code) {
        codes.add(code);
    }

    @JsonProperty("errors")
    public List<ErrorCodes> getStatusCodes() {
        return codes;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    @JsonProperty("success")
    public boolean getMatch() { return match; }

    public void setHTTPStatusCode(Status code) { this.HTTPStatusCode = code; }
    public void setHTTPStatusCode(int code) { this.HTTPStatusCode = Status.fromStatusCode(code); }

    public Status getHTTPStatusCode() { return HTTPStatusCode; }
}
