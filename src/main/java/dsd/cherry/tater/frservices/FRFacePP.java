package dsd.cherry.tater.frservices;

import dsd.cherry.tater.types.ErrorCodes;
import dsd.cherry.tater.types.ImageData;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Handles communication with the Face++ facial recognition service, using the Face++ SDK to do so. Extends the
 * FRServiceHandler class so it can be accessed in a standard way.
 *
 * @author Andrew James Beach
 * @version 0.9
 * Created by James Beach on 5/14/2016.
 */
public class FRFacePP extends FRServiceHandler {
    private final static String SERVICE_NAME = "facepp";
    private final static int REQ_DELAY_MILLISECONDS = 300;
    private final static int TRAINING_PHOTOS_MINIMUM = 5;
    private final static double CUTOFF = 0.9;
    private HttpRequests httpRequests;
    private boolean useChina, noHTTPS;

    public FRFacePP(String api_key, String api_secret, int timeoutSeconds) {
        httpRequests = new HttpRequests(api_key, api_secret, useChina, noHTTPS);
        setTimeout(timeoutSeconds);
    }

    @Override
    public FRServiceHandlerTrainResponse train(String userID, List<ImageData> images) {
        boolean imagesAccepted,
                personCreated       = false,
                trainSuccess        = false,
                serviceResponded    = true;
        int nPhotosAccepted = 0;
        PostParameters param;
        ArrayList<String> faceIDs; // must be an array list because PostParameters expects it
        String facID = "";
        // Detect faces
        param = new PostParameters();
        faceIDs = new ArrayList<>();
        for (ImageData img : images) {
            String id = detectFace(img);
            if (id != null) {
                ++nPhotosAccepted;
                faceIDs.add(id);
            }
        }

        imagesAccepted = nPhotosAccepted >= TRAINING_PHOTOS_MINIMUM;

        if (imagesAccepted) {

            // Create person
            param = new PostParameters();
            param.setFaceId(faceIDs);
            try {
                JSONObject rst = httpRequests.personCreate(param);
                facID = rst.getString("person_id");
                System.out.println("Person ID: " + facID);
                personCreated = true;
            } catch (FaceppParseException e) {
                System.err.println("Error creating person: " + e.getMessage());
                FaceppCode code = FaceppCode.valueOf(e.getErrorMessage());

                switch (code) {
                    default:
                        System.err.println("Unhandled error.");
                }
            } catch (JSONException e) {
                System.err.println("Error getting personID from JSON response: " + e.getMessage());
                e.printStackTrace();
            }

            // Train person for verification
            if (personCreated) {
                param = new PostParameters();
                param.setPersonId(facID);
                try {
                    // start training
                    JSONObject rst = httpRequests.trainVerify(param);
                    String session = rst.getString("session_id");
                    System.out.println("Got training session ID: " + session);

                    // get results
                    System.out.println("Await training results...");
                    // check every so often for a result for as long as the timeout will allow
                    int nTimes = (getTimeout() * 1000) / REQ_DELAY_MILLISECONDS;
                    for (int i = 0; i < nTimes; ++i) {
                        int res = trainGetResults(session);
                        if (res == 1) {
                            trainSuccess = true;
                            break;
                        } else if (res == -1) {
                            System.out.println("Training failed. No response?");
                            serviceResponded = false;
                            break;
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(REQ_DELAY_MILLISECONDS);
                        } catch (InterruptedException e) {
                            System.err.println("Sleep interrupted: " + e.getMessage());
                        }
                    }
                } catch (FaceppParseException e) {
                    System.err.println("Error training person: " + e.getMessage());
                } catch (JSONException e) {
                    System.err.println("Error reading JSON response: " + e.getMessage());
                }
            }

            // Delete person if training failed
            if (personCreated && !trainSuccess) {
                personDelete(facID);
            }
        }

        return new FRServiceHandlerTrainResponse(
                getFRServiceName(),
                serviceResponded,
                facID,
                trainSuccess,
                images
        );
    }

    @Override
    public FRServiceHandlerVerifyResponse verify(String personID, ImageData image) {
        PostParameters param = new PostParameters();
        double confidence = 0;
        boolean isSamePerson = false;
        String sessionId;
        boolean faceDetected, serviceResponded = false;

        String faceID = detectFace(image);
        faceDetected = faceID != null;

        if (faceDetected) {
            param.setPersonId(personID);
            param.setFaceId(faceID);
            try {
                JSONObject rst = httpRequests.recognitionVerify(param);
                serviceResponded = true;
                confidence = rst.getDouble("confidence") / 100;
                isSamePerson = rst.getBoolean("is_same_person");
                sessionId = rst.getString("session_id");
            } catch (FaceppParseException e) {
                System.err.println("Error verifying face: " + e.getMessage());
            } catch (JSONException e) {
                System.err.println("Error reading JSON response: " + e.getMessage());
            }


        }

        return new FRServiceHandlerVerifyResponse(
                getFRServiceName(),
                serviceResponded,
                confidence,
                getFRServiceCutoff(),
                personID
        );
    }

    @Override
    public String getFRServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public double getFRServiceCutoff() { return CUTOFF; }

    private String detectFace(ImageData img) {
        String faceID = null;
        PostParameters param = new PostParameters();
        param.setImg(img.getImageBinary());
        JSONObject rst;
        try {
            rst = httpRequests.detectionDetect(param);
            // It's in an array called "face" because it's stupid
            JSONArray elements = rst.getJSONArray("face");
            // Don't assume order is guaranteed, find "face_id" field
            for (int i = 0; i < elements.length(); ++i) {
                JSONObject obj = elements.getJSONObject(i);
                if (obj.has("face_id")) {
                    faceID = obj.getString("face_id");
                    System.out.println("Face ID: " + faceID);
                    break;
                }
            }
        } catch (FaceppParseException e) {
            String m = e.getMessage();
            System.err.println("Error detecting face: " + m);
            try {
                // m.getErrorCode() uselessly doesn't return the error code, so we must parse
                FaceppCode code = FaceppCode.fromErrorCode(
                        Integer.parseInt(m.substring(m.indexOf("code=") + 5, m.indexOf(",")))
                );
                switch (code) {
                    case IMAGE_ERROR_UNSUPPORTED_FORMAT:
                        System.err.println("Marking image as being in an unsupported format.");
                        img.addCode(ErrorCodes.IMAGE_ERROR_UNSUPPORTED_FORMAT);
                        break;
                    case IMAGE_ERROR_FILE_TOO_LARGE:
                        System.err.println("Marking image as being too large.");
                        img.addCode(ErrorCodes.IMAGE_ERROR_FILE_TOO_LARGE);
                        break;
                    default:
                        System.err.println("Unknown error. Marking image as having an unknown problem.");
                        img.addCode(ErrorCodes.IMAGE_ERROR_UNKNOWN);
                }
            } catch (NullPointerException f) {
                System.err.println("Error getting Facepp Code: " + f.getMessage());
                System.err.println("Marking image as having an unknown problem.");
                img.addCode(ErrorCodes.IMAGE_ERROR_UNKNOWN);
            }
        } catch (JSONException e) {
            System.err.println("Error getting faceID: " + e.getMessage());
            e.printStackTrace();
        }

        return faceID;
    }

    private int trainGetResults(String sessionID) {
        System.out.println("Checking on training session status.");
        try {
            PostParameters param = new PostParameters();
            param.setSessionId(sessionID);
            JSONObject rst = httpRequests.infoGetSession(param);
            System.out.println("JSON: " + rst);
            switch (rst.getString("status")) {
                case "INQUEUE":
                    return 0;
                case "SUCC":
                    return 1;
                default:
                    return -1;
            }
        } catch (FaceppParseException e) {
            System.err.println("Error getting training session results: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Error reading JSON response: " + e.getMessage());
        }
        return -1;
    }

    public int personDelete(String personID) {
        if (personID != null && !personID.equals("")) {
            PostParameters param = new PostParameters();
            System.out.print("Attempting to delete person with ID " + personID + "...");
            try {
                param = new PostParameters();
                param.setPersonId(personID);
                JSONObject rst = httpRequests.personDelete(param);
                if (rst.getInt("deleted") >= 1) {
                    System.out.println("succeeded.");
                    return 0;
                }
                else {
                    System.out.println("failed.");
                }
            } catch (FaceppParseException e) {
                System.out.println("failed.");
                System.err.println("Error deleting person: " + e.getMessage());
            } catch (JSONException e) {
                System.out.println("result unknown.");
                System.err.println("Error reading JSON response: " + e.getMessage());
            }
        }

        return -1;
    }

    private enum FaceppCode {
        INTERNAL_ERROR                                  (1001, 500),
        AUTHORIZATION_ERROR                             (1003, 403),
        INSUFFICIENT_PRIVILEGE_OR_QUOTA_LIMIT_EXCEEDED  (1003, 403),
        MISSING_ARGUMENTS                               (1004, 400),
        INVALID_ARGUMENTS                               (1005, 400),
        ILLEGAL_USE_OF_DEMO_KEY                         (1006, 403),
        TOO_MANY_ITEMS_TO_ADD                           (1008, 400),
        SERVER_TOO_BUSY                                 (1202, 502),
        IMAGE_ERROR_UNSUPPORTED_FORMAT                  (1301, 431),
        IMAGE_ERROR_FAILED_TO_DOWNLOAD                  (1302, 432),
        IMAGE_ERROR_FILE_TOO_LARGE                      (1303, 433),
        IMAGE_ERROR                                     (1304, 434),
        OBJECT_UNTRAINED                                (1401, 441),
        BAD_NAME                                        (1501, 451),
        BAD_TAG                                         (1502, 452),
        NAME_EXIST                                      (1503, 453);

        int errorCode, httpCode;

        private static Map<Integer, FaceppCode> ECmap = new HashMap<>();
        private static Map<Integer, FaceppCode> HTTPmap = new HashMap<>();

        static {
            for (FaceppCode fppc : FaceppCode.values()) {
                ECmap.put(fppc.errorCode, fppc);
                HTTPmap.put(fppc.httpCode, fppc);
            }
        }

        FaceppCode(int errorCode, int httpCode) {
            this.errorCode = errorCode;
            this.httpCode = httpCode;
        }

        public static FaceppCode fromErrorCode(int code) { return ECmap.get(code); }
        public static FaceppCode fromHTTPCode(int code) { return HTTPmap.get(code); }
    }
}
