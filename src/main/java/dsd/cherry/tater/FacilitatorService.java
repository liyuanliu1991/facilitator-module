package dsd.cherry.tater;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dsd.cherry.tater.frservices.FRFacePP;

import dsd.cherry.tater.types.*;
import dsd.cherry.tater.types.jax_mixins.MxImageDataAuthRequest;
import dsd.cherry.tater.types.jax_pojos.ClientRequestRegister;
import dsd.cherry.tater.types.jax_pojos.ClientRequestVerify;
import dsd.cherry.tater.types.jax_pojos.ClientResponseVerify;
import dsd.cherry.tater.types.jax_pojos.ClientResponseRegister;

import java.io.IOException;

/**
 * Exposes the public API of the Tater web service. Tater is modeled after the requirements set forth for the
 * Distributed Software Development 2016 exercise. It supports the training of facial identities and the subsequent
 * verification of photographs against those identities using one or more facial recognition services.
 *
 * @author Andrew James Beach
 * @version 0.7
 * Created by James Beach on 4/27/2016.
 */
@Path("")
public class FacilitatorService {
    @Context private HttpServletRequest contextReq;
    @Context private UriInfo  contextUri;
    private ObjectMapper mapper;
    private ServiceManager services;

    public FacilitatorService() {
        mapper = new ObjectMapper();
        services = new ServiceManager(10);
        services.addService(new FRFacePP("30f10080215674ebed72c18753e6a830",
                                            "CBtHB1BdopXQsDClTl6f2F9r4rquPOTk",
                                            5));
    }

    /**
     * Exposes a training function through which a client can supply a set of photographs of faces to create a new
     * identity with one or more supported facial recognition services. This identity can later be used to verify
     * whether a different photograph is of a face that matches the one in the original photos used for training.
     * @param JSON A JSON training request from a client.
     * @return An HTTP response and a JSON data-bound object. See the Facilitator Interface Specification and the
     *            definition for the ClientResponseRegister object.
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response train(String JSON) {
        ClientResponseRegister reply = new ClientResponseRegister(); // this will be the response
        Response.Status statusCode = Response.Status.OK; // this will be the HTTP response code

        // set it up so images are mapped correctly
        ObjectMapper map = this.mapper.copy();
        map.addMixIn(ImageData.class, MxImageDataAuthRequest.class);

        ClientRequestRegister req; // JSON data will be marshaled to this POJO

        try {
            req = map.readValue(JSON, ClientRequestRegister.class);

            // validate request


            // train the services and put the results in the reply
            SMTrainData result;
            result = services.train(null, null, req.getImages());
            reply.setTrainingStatus(result.getTrainingStatus());
            reply.setFACIDs(result.getFacIDs());
            reply.setHTTPCode(Response.Status.OK);
            for (ImageData img : result.getImageData()) {
                reply.addCodes(img.getCodes());
            }
        } catch (IOException e) {
            System.out.println("Error reading JSON Train Request: " + e.getMessage());
            reply.addFACID(null);
            reply.addCode(ErrorCodes.BAD_JSON_TAG);
        }

        // validate request


        // train the services and put the results in the reply

        try {
            return Response.status(statusCode).entity(map.writeValueAsString(reply)).build();
        } catch (JsonProcessingException f) {
            statusCode = Response.Status.INTERNAL_SERVER_ERROR;
            return Response.status(statusCode)
                    .entity("tater/" + contextUri.getPath() + ": Fatal JSON processing error.").build();
        }
    }

    /**
     * Exposes a verification function through which a client can attempt to verify a photograph of a face against a
     * previously-trained identity.
     * @param JSON A JSON request for verification.
     * @return An HTTP response and a JSON data-bound object. See the Facilitator Interface Specification and the
     *            definition for the ClientResponseVerify object.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verify(String JSON) {
        ObjectMapper mapIn = this.mapper.copy();
        mapIn.addMixIn(ImageData.class, MxImageDataAuthRequest.class);
        ClientRequestVerify req;
        try {
            req = mapIn.readValue(JSON, ClientRequestVerify.class);
        } catch (IOException e) {
            System.out.println("Error reading JSON Verify Request: " + e.getMessage());
            e.printStackTrace();
            return Response.status(400).entity("Error reading JSON request.").build();
        }

        System.out.println("Request from "
                + contextReq.getRemoteAddr() + " port " + contextReq.getRemotePort()
                + (contextReq.getRemoteAddr().equals(contextReq.getLocalAddr()) ?
                " (The call is coming from inside the house.)" :
                " (The call is coming from outside the house.)"));
        System.out.println("ImageId: " + req.getImage().getImageID());
        System.out.println("ImageB64: " + DatatypeConverter.printBase64Binary(req.getImage()
                                                                              .getImageBinary()).substring(0,32) + "...");

        SMVerifyData result = services.verify(req.getFACIDs(), req.getImage());
        ClientResponseVerify reply = new ClientResponseVerify();
        reply.setHTTPStatusCode(200);
        reply.setMatch(result.isMatch());

        return Response.status(reply.getHTTPStatusCode()).entity(reply).build();
    }

    /**
     * A diagnostic function for determining if the FacilitatorService is live and functioning.
     * @param message A string that will be repeated back to the requesting client.
     * @return A hello message featuring the provided string argument.
     */
    @GET
    @Path("/test/{param}")
    public Response test(@PathParam("param") String message) {
        String output = "Tater, the Facilitating Potato, is alive and says " + message;
        return Response.status(Response.Status.OK).entity(output).build();
    }
}
