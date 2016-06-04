package dsd.cherry.tater.types.jax_mixins;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class provides the annotations to support necessary JSON data binding to ImageData objects for Authorization
 * Server requests only.
 * @author Andrew James Beach
 * @version 0.4
 * Created by James Beach on 5/1/2016.
 */
public abstract class MxImageDataAuthRequest {
    @JsonProperty("base64")
    public abstract void setImageBinary(byte[] image);
    @JsonProperty("base64")
    public abstract byte[] getImageBinary();

    @JsonProperty("pictureId")
    public abstract void setImageID(String imageID);
    @JsonProperty("pictureId")
    public abstract String getImageID();
}
