package dsd.cherry.tater.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by James Beach on 4/29/2016.
 */
public class ImageData {
    private String imageID;
    private byte[] image;
    private List<ErrorCode> codes;
    private boolean accepted;

    private ImageData() {
        codes = new ArrayList<ErrorCode>();
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

    // TODO: Throw exception when it is not an image-related error code
    public void addCode(ErrorCodes code) { codes.add(new ErrorCode(code, imageID)); }

    public List<ErrorCode> getCodes() { return codes; }

    public void setAcceptedByService(boolean accepted) { this.accepted = accepted; }
    public boolean getAcceptedByService() { return accepted; }
}
