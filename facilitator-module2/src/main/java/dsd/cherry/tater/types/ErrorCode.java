package dsd.cherry.tater.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by James Beach on 5/13/2016.
 */
public class ErrorCode {
    private ErrorCodes code;
    private String imageID;

    @JsonIgnore
    public ErrorCode(ErrorCodes code) {
        this.code = code;
        imageID = null;
    }

    @JsonIgnore
    public ErrorCode(ErrorCodes code, String imageID) {
        this.code = code;
        this.imageID = imageID;
    }

    @JsonProperty("errorCode")
    public int getCode() { return code.getErrorCode(); }

    @JsonProperty("errorMessage")
    public String getMessage() { return code.getMessage(); }

    @JsonProperty("imageId")
    public String getImageID() { return imageID; }
}
