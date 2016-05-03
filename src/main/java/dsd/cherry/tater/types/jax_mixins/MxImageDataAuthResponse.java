package dsd.cherry.tater.types.jax_mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.ImageCode;

import java.util.List;

/**
 * This class provides the annotations to support necessary JSON data binding to ImageData objects for Authorization
 * Server replies only.
 * Created by James Beach on 5/1/2016.
 */
public abstract class MxImageDataAuthResponse {
    @JsonProperty("internal_id")
    public abstract void setImageID(String imageID);
    @JsonProperty("internal_id")
    public abstract String getImageID();

    @JsonIgnore
    public abstract void setImageBinary(byte[] image);
    @JsonIgnore
    public abstract byte[] getImageBinary();

    @JsonProperty("appCode")
    public abstract void addCode(ImageCode code);
    @JsonIgnore
    public abstract void setCodes(List<ImageCode> codes);
    @JsonProperty("appCode")
    public abstract ImageCode getCode();

    @JsonProperty("isSuccess")
    public abstract void setAcceptedByService(boolean accepted);
    @JsonProperty("isSuccess")
    public abstract boolean getAcceptedByService();
}