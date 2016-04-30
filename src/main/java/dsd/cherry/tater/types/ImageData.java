package dsd.cherry.tater.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by James Beach on 4/29/2016.
 */
public class ImageData {
    private String imageID;
    private BufferedImage image;

    @JsonProperty("base64_image")
    public void setImageBinary(byte[] base64) {
        try {
            image = ImageIO.read(new ByteArrayInputStream(base64));
        } catch (IOException e) {
            System.out.println("Error reading byte array: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @JsonProperty("base64_image")
    public byte[] getImageBinary() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", out);
        } catch (IOException e) {
            System.out.println("Error writing byte array: " + e.getMessage());
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    @JsonProperty("internal_id")
    public void setImageID(String imageID) { this.imageID = imageID; }
    @JsonProperty("internal_id")
    public String getImageID() { return imageID; }
}
