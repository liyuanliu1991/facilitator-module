package dsd.cherry.tater;

// import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dsd.cherry.tater.frservices.FRFacePP;
import dsd.cherry.tater.frservices.FRServiceHandlerTrainResponse;
import org.json.JSONObject;

import dsd.cherry.tater.types.*;
import dsd.cherry.tater.types.jax_mixins.MxImageDataAuthRequest;
import dsd.cherry.tater.types.jax_pojos.AuthRequestRegister;
import dsd.cherry.tater.types.jax_pojos.AuthRequestLogin;
import dsd.cherry.tater.types.jax_pojos.AuthResponseLogin;
import dsd.cherry.tater.types.jax_pojos.AuthResponseRegister;

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
     *            definition for the AuthResponseRegister object.
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response train(String JSON) {
        // set it up so images are mapped correctly
        ObjectMapper mapIn = this.mapper.copy();
        mapIn.addMixIn(ImageData.class, MxImageDataAuthRequest.class);

        AuthRequestRegister req; // JSON data will be marshaled to this POJO
        try {
            req = mapIn.readValue(JSON, AuthRequestRegister.class);
        } catch (IOException e) {
            System.out.println("Error reading JSON Train Request: " + e.getMessage());
            e.printStackTrace();
            return Response.status(452).entity("Error reading JSON request.").build();
        }
        System.out.println(req.getImages().toString());

        ObjectMapper mapOut = this.mapper.copy();

        SMTrainData result = services.train(null, null, req.getImages());
        AuthResponseRegister reply = new AuthResponseRegister();
        reply.setTrainingStatus(result.getTrainingStatus());
        reply.setFACIDs(result.getFacIDs());
        reply.setHTTPCode(Response.Status.OK);

        for (ImageData img : result.getImageData()) {
            reply.addCodes(img.getCodes());
        }

        try {
            return Response.status(reply.getHTTPCode()).entity(mapOut.writeValueAsString(reply)).build();
        } catch (JsonProcessingException e) {
            System.out.println("/train: Error producing JSON reply: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("tater/train: Error producing JSON reply.").build();
        }
    }

    /**
     * Exposes a verification function through which a client can attempt to verify a photograph of a face against a
     * previously-trained identity.
     * @param JSON A JSON request for verification.
     * @return An HTTP response and a JSON data-bound object. See the Facilitator Interface Specifcation and the
     *            definition for the AuthResponseLogin object.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verify(String JSON) {
        ObjectMapper mapIn = this.mapper.copy();
        mapIn.addMixIn(ImageData.class, MxImageDataAuthRequest.class);
        AuthRequestLogin req;
        try {
            req = mapIn.readValue(JSON, AuthRequestLogin.class);
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
        AuthResponseLogin reply = new AuthResponseLogin();
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
