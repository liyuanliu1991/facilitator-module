package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.FacilitatorID;
import dsd.cherry.tater.types.ImageData;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by James Beach on 4/29/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ClientRequestTrain {
    @Valid
    @NotNull
    private List<ImageData> images;
    @Valid
    private List<FacilitatorID> FacIDs;

    @JsonProperty("pictures")
    public void setImages(List<ImageData> images) { this.images = images; }
    public List<ImageData> getImages() { return images; }

    @JsonProperty("facilitatorIds")
    public void setFacIDs(List<FacilitatorID> FacIDs) { this.FacIDs = FacIDs; }
    public List<FacilitatorID> getFacIDs() { return FacIDs; }
}
