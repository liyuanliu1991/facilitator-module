package dsd.cherry.tater;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import dsd.cherry.tater.types.AuthRequestVerify;
import dsd.cherry.tater.types.AuthRequestTrain;

/**
 * Created by James Beach on 4/27/2016.
 */
@Path("")
public class FacilitatorService {
    @POST
    @Path("/train")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response train(AuthRequestTrain req) {
        System.out.println("UserId: " + req.getInternalID());
        System.out.println(req.getImages().toString());
        return Response.status(200).entity("I'm alive, too").build();
    }

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

    @GET
    @Path("/test/{param}")
    public Response test(@PathParam("param") String message) {
        String output = "Tater is alive and says " + message;
        return Response.status(200).entity(output).build();
    }
}
