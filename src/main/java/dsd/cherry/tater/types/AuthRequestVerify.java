package dsd.cherry.tater.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by James Beach on 4/29/2016.
 */
public class AuthRequestVerify {
    private String internalID;
    private ImageData image;

    public AuthRequestVerify() {};

    @JsonProperty("UserId")
    public void setInternalID(String internalID) {
        this.internalID = internalID;
    }
    @JsonProperty("UserId")
    public String getInternalID() { return internalID; }

    @JsonProperty("Image")
    public void setImage(ImageData image) { this.image = image; }
    @JsonProperty("Image")
    public ImageData getImage() { return image; }
}
