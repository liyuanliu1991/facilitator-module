package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.FacilitatorID;
import dsd.cherry.tater.types.ImageData;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by James Beach on 4/29/2016.
 */
public class AuthRequestLogin {
    @NotNull
    private ImageData image;
    @NotNull
    private List<FacilitatorID> FACIDs;

    public AuthRequestLogin() {}

    @JsonProperty("picture")
    public void setImage(ImageData image) { this.image = image; }
    @JsonIgnore
    public ImageData getImage() { return image; }

    @JsonProperty("facilitatorIds")
    public void setFACIDs(List<FacilitatorID> IDs) { FACIDs = IDs; }
    @JsonIgnore
    public List<FacilitatorID> getFACIDs() { return FACIDs; }
}
