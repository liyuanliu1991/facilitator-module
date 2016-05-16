package dsd.cherry.tater.frservices;

import dsd.cherry.tater.types.ErrorCode;
import dsd.cherry.tater.types.ImageData;
import junit.framework.TestCase;

import javax.validation.constraints.Null;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Beach on 5/14/2016.
 */
public class FaceppTrainTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testTrain() throws Exception {
        FRFacePP fpp = new FRFacePP("30f10080215674ebed72c18753e6a830", "CBtHB1BdopXQsDClTl6f2F9r4rquPOTk", 5);
        List<ImageData> lst = new ArrayList<>();

        File trainPics = new File("src/test/resources/testpics/obama_train");

        try {
            for (File f : trainPics.listFiles()) {
                byte[] fbyte = new byte[(int) f.length()];
                FileInputStream fin = new FileInputStream(f);
                fin.read(fbyte);
                fin.close();
                ImageData img = new ImageData();
                img.setImageBinary(fbyte);
                img.setImageID(f.getName());
                lst.add(img);
            }
        } catch (NullPointerException e) {
            System.out.println("Obamapics directory is null!");
        }

        FRServiceHandlerTrainResponse response = fpp.train(null, lst);
        System.out.println("=====TRAINING RESULTS=====");
        System.out.println("Service Name: " + response.getServiceName());
        System.out.println("Service Responded: " + response.getServiceResponded());
        System.out.println("Person ID: " + response.getFRPersonID());
        System.out.println("Training Successful: " + response.getTrainingStatus());
        System.out.println(lst.size() + " images:");
        for (ImageData img : lst) {
            System.out.println("    Image ID: " + img.getImageID());
            for (ErrorCode c : img.getCodes()) {
                System.out.println("        Error: " + c.getMessage());
            }
        }
        System.out.println("==========================");

        fpp.personDelete(response.getFRPersonID());
    }

}