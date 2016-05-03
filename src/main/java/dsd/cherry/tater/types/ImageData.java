package dsd.cherry.tater.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 4/29/2016.
 */
public class ImageData {
    private String imageID;
    private byte[] image;
    private List<ImageCode> codes;
    private boolean accepted;

    private ImageData() {
        codes = new ArrayList<ImageCode>();
        accepted = false;
    }

    public void setImageBinary(byte[] image) {
        this.image = image;
    }
    public byte[] getImageBinary() {
        return this.image;
    }

    public void setImageID(String imageID) { this.imageID = imageID; }
    public String getImageID() { return imageID; }

    public void addCode(ImageCode code) { codes.add(code); }
    public void setCodes(List<ImageCode> codes) { this.codes = codes; }
    public ImageCode getCode() {
        for (ImageCode code : codes) {
            if (!code.equals(ImageCode.IMAGE_OK)) return code;
        }
        return ImageCode.IMAGE_OK;
    }

    public void setAcceptedByService(boolean accepted) { this.accepted = accepted; }
    public boolean getAcceptedByService() { return accepted; }
}
