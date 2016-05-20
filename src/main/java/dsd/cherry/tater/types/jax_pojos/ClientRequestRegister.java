package dsd.cherry.tater.types.jax_pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dsd.cherry.tater.types.FacilitatorID;
import dsd.cherry.tater.types.ImageData;

import java.util.List;

/**
 * Created by James Beach on 4/29/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientRequestRegister {
    private List<ImageData> images;

    @JsonProperty("pictures")
    public void setImages(List<ImageData> images) { this.images = images; }
    @JsonProperty("pictures")
    public List<ImageData> getImages() { return images; }
}
