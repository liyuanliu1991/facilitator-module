package dsd.cherry.tater.types;

import java.util.List;

/**
 * Created by James Beach on 4/29/2016.
 */
public abstract class FRServiceHandler {
    abstract public FRServiceHandlerTrainResponse train(String internalID, List<ImageData> images);
    abstract public FRServiceHandlerVerifyResponse verify(String internalID, ImageData image);
    abstract public String getFRServiceName();
    abstract public float getFRServiceCutoff();
}
