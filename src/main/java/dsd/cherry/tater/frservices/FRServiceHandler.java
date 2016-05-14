package dsd.cherry.tater.frservices;

import dsd.cherry.tater.types.ImageData;

import java.util.List;

/**
 * Created by James Beach on 4/29/2016.
 */
public abstract class FRServiceHandler {
    private int timeoutSec;

    /**
     * Exposes training (aka registration) functionality for a given service. This method takes a user ID that may or
     * may not be supplied by the server and a set of photos. It can use the user ID (if provided), generate its own,
     * or let the service generate an ID. It will attempt to train an identity using the supplied photos.
     *
     * If training succeeds, it will respond with a person ID (the ID of the identity in the service).
     *
     * If training does not succeed, it will attempt to undo any partial training if it occurred and respond with
     * data indicating why training failed, including:
     *  - Images being refused by the service for whatever reason
     *  - Insufficient number of images (the service handler is responsible for making a judgment call, if necessary.)
     *  - The service was unavailable or unresponsive
     *
     * Please see the return type definition and the definitions of its member types for more information on how to
     * encode this data.
     *
     * @param userID The user ID of the person supplied by the Authentication Server. This parameter may be null, in
     *               which case an ID may need to be created for services which require one to be supplied. It is not
     *               required that this parameter be used.
     * @param images A list of images corresponding to a single person. Guaranteed to not be null but the list may
     *               be empty.
     * @return A collection of values indicating the results of training. See the FRServiceHandlerTrainResponse type
     *          definition and its member variable type definitions for further information.
     */
    abstract public FRServiceHandlerTrainResponse train(String userID, List<ImageData> images);

    /**
     *
     * @param personID The unique ID corresponding to a facial identity previously trained on the corresponding service.
     * @param image An image to be checked by the service to verify whether it does or does not likely feature the face
     *              of the person previously trained to the personID.
     * @return A collection of values indicating the results of verification. See the FRServiceHandlerVerifyResponse
     *          type definition for further information.
     */
    abstract public FRServiceHandlerVerifyResponse verify(String personID, ImageData image);

    abstract public String getFRServiceName();

    /**
     * <b>Getter.</b>
     * @return The confidence value cutoff below which a confidence value returned by verify() should not constitute a
     *          match.
     */
    abstract public float getFRServiceCutoff();

    final public void setTimeout(int seconds) {
        timeoutSec = seconds;
    }

    final public int getTimeout() { return timeoutSec; }
}
