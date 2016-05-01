package dsd.cherry.tater;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import dsd.cherry.tater.types.AuthRequestVerify;
import dsd.cherry.tater.types.AuthRequestTrain;

/**
 * The FacilitatorService exposes the Facilitator API as a RESTful web service for use by the Authentication Server.
 *
 * @author Andrew James Beach
 * @version 0.1
 * Created by James Beach on 4/27/2016.
 */
@Path("")
public class FacilitatorService {
    /**
     * Exposes a training function through which the Authentication Server can commence the training of facial
     * recognition services and at the same time get the training status of those services.
     * @param req An object data-bound with a JSON request from the Authentication Server. See the Facilitator
     *            Interface Specification and the definition for AuthRequestTrain.
     * @return An HTTP response and a JSON data-bound object. See the Facilitator Interface Specification and the
     *            definition for the AuthResponseTrain object.
     */
    @POST
    @Path("/train")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response train(AuthRequestTrain req) {
        System.out.println("UserId: " + req.getInternalID());
        System.out.println(req.getImages().toString());
        return Response.status(200).entity("I'm alive, too").build();
    }

    /**
     * Exposes a verification function through which the Authentication Server can attempt to verify a photo of a face
     * against a person's ID.
     * @param req An object data-bound with a JSON request from the Authentication Server. See the Facilitator
     *            Interface Specification and the definition for AuthRequestVerify.
     * @return An HTTP response and a JSON data-bound object. See the Facilitator Interface Specifcation and the
     *            definition for the AuthResponseVerify object.
     */
    @POST
    @Path("/verify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response verify(AuthRequestVerify req) {
        System.out.println("UserId: " + req.getInternalID());
        System.out.println("ImageId: " + req.getImage().getImageID());
        System.out.println("ImageB64: " + DatatypeConverter.printBase64Binary(req.getImage().getImageBinary()));
        // System.out.println(json);
        return Response.status(200).entity("I'm alive").build();
    }

    /**
     * A diagnostic function for determining if the FacilitatorService is live and functioning.
     * @param message A string that will be repeated back to the requesting client.
     * @return A hello message featuring the provided string argument.
     */
    @GET
    @Path("/test/{param}")
    public Response test(@PathParam("param") String message) {
        String output = "Tater is alive and says " + message;
        return Response.status(200).entity(output).build();
    }
}
