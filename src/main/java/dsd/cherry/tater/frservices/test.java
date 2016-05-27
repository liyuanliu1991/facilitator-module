package dsd.cherry.tater.frservices;

import java.util.Random;

import dsd.cherry.tater.types.ErrorCodes;

//import org.apache.http.client.HttpClient;
//import org.apache.http.impl.client.HttpClients;
//
//public class test {
//
//	public static void main(String[] args){
//		HttpClient httpClient = HttpClients.createDefault();
//		System.out.println("*******************");
//	}
//}

//
//public class ErrorCode{
//	private ErrorCodes code;
//	private String imageID;
//	
//	@JsonIgnore
//	public ErrorCode(ErrorCodes code){
//		this.code = code;
//		imageID = null;
//	}
//	
//	@JsonIgnore
//	public ErrorCode(ErrorCodes code, String imageID){
//		this.code = code;
//		this.imageID = imageID;
//	}
//	
//	@JsonProperty("errorCode")
//	public getCode(){
//		return code.getErrorCode();
//	}
//	
//	@JsonProperty("errorMessage")
//	public String getMessage(){
//		return code.getMessage();
//	}
//	
//	@JsonProperty("imageId")
//	public String getImageID(){
//		return imageID;
//	}
//}
//
//@JsonSerialize(using = StatusCodeSerializer.class, as = String.class)
//@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,isGetterVisibility = JsonAutoDetect.Visibility.NONE)
//
//public enum ErrorCodes{
//	OK(0){
//		@Override
//		public String getMessage(){
//			return "OK";
//		}
//	},
//	IMAGE_ERROR_FILE_TOO_LARGE(1){
//		@Override
//		public String getMessage(){
//			return "The image file is too large";
//		}
//	},
//	IMAGE_ERROR_FACE_NOT_DETECTED(2){
//		@Override
//		public String getMessage(){
//			return "No face could be detected in the image. ";
//		}
//	},
//	IMAGE_ERROR_UNKNOWN(3){
//		@Override
//		public String getMessage(){
//			return "There is something unknown wrong with the image";
//		}
//	},
//	IMAGE_ERROR_UNSUPPORTED_FORMAT(4){
//		@Override
//		public String getMessage(){
//			return "The image is in an unsupported file format.";
//		}
//	},
//	BAD_JSON_TAG(5){
//		@Override
//		public String getMessage(){
//			return "A JSON tag is missing or formatted incorrectly.";
//		}
//	},
//	@Deprecated
//	IMAGE_ERROR_FAILED_TO_DOWNLOAD(1302){
//		@Override
//		public String getMessage(){
//			return "IMAGE_ERROR_FAILED_TO_DOWNLOAD";
//		}
//	};
//	
//	private int value;
//	public abstract String getMessage();
//	public int getErrorCode(){
//		return value;
//	}
//	ErrorCodes(int value){
//		this.value = value;
//	}
//}
//
//
//
//

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
		// TODO Auto-generated method stub
		boolean imagesAccepted, personCreated = false, trainSuccess = false, serviceResponded = true;
		int nPhotosAccepted = 0;
		PostParameters param = new PostParameters();
		ArrayList<String> faceIDs = new ArrayList<>();
		String facID = "";

		for (ImageData img : images) {
			String id = detectFace(img);
			if (id != null) {
				++nPhotosAccepted;
				faceIDs.add(id);
			}
		}

		imagesAccepted = nPhotosAccepted >= TRAINING_PHOTOS_MINIMUM;
		if (imagesAccepted) {
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
					System.err.println("Unhandled error. ");
				}
			} catch (JSONException e) {
				System.err.println("Error getting personID from JSON response. ");
				e.printStackTrace();
			}

			if (personCreated) {
				param = new PostParameters();
				param.setPersonId(facID);
				try {
					JSONObject rst = httpRequests.trainVerify(param);
					String session = rst.getString("session_id");
					System.out.println("Got training session ID: " + session);

					System.out.println("Awaiting for results...");
					int nTimes = (getTimeout() * 100) / REQ_DELAY_MILLISECONDS;
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
							System.out.println("Sleep interrupted: " + e.getMessage());
						}
					}

				} catch (FaceppParseException e) {
					System.out.println("Error training person: " + e.getMessage());
				} catch (JSONException e) {
					System.err.println("Error reading JSON response: " + e.getMessage());
				}
			}
		}

		if (personCreated && !trainSuccess) {
			personDelete(facID);
		}
		return new FRServiceHandlerTrainResponse(getFRServiceName(), serviceResponded, facID, trainSuccess, images);
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
				} else {
					System.out.println("failed.");
				}
			} catch (FaceppParseException e) {
				System.out.println("failed. ");
				System.out.println("Error deleting person: " + e.getMessage());
			} catch (JSONException e) {
				System.out.println("result unknown. ");
				System.out.println("Error reading JSON response: " + e.getMessage());
			}
		}
		return -1;
	}

	private int trainGetResults(String sessionID) {
		System.out.println("Checking on training session status. ");
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
			System.out.println("Error getting training session results: " + e.getMessage());
		} catch (JSONException e) {
			System.out.println("Error reading JSON response: " + e.getMessage());
		}
		return -1;
	}

	public String detectFace(ImageData img) {
		String faceID = null;
		PostParameters param = new PostParameters();
		param.setImg(img.getImageBinary());
		JSONObject rst;
		try {
			rst = httpRequests.detectionDetect(param);
			JSONArray elements = rst.getJSONArray("face");
			for (int i = 0; i < elements.length(); ++i) {
				JSONObject obj = elements.getJSONObject(i);
				if (obj.has("face_id")) { // get first faceId
					faceID = obj.getString("face_id");
					System.out.println("Face ID: " + faceID);
					break;
				}
			}
		} catch (FaceppParseException e) {
			System.err.println("Error detecting face: " + e.getMessage());
			try {
				FaceppCode code = FaceppCode.fromErrorCode(e.getErrorCode());
				switch (code) {
				case IMAGE_ERROR_UNSPPORTED_FORMAT:
					System.err.println("Marking image as being in an unsupported format.");
					img.addCode(ErrorCodes.IMAGE_ERROR_UNSUPPORTED_FORMAT);
					break;
				case IMAGE_ERROR_FILE_TOO_LARGE:
					System.err.println("Marking image as being too large. ");
					img.addCode(ErrorCodes.IMAGE_ERROR_FILE_TOO_LARGE);
					break;
				default:
					System.err.println("Unhandled error. ");
				}
			} catch (NullPointerException f) {
				System.out.println("Error getting Facepp Code: " + f.getMessage());
				System.out.println("Marking image as having an unknown problem");
				img.addCode(ErrorCodes.IMAGE_ERROR_UNKNOWN);
			}
		}
		return faceID;
	}

	private enum FaceppCode {
		INTERNAL_ERROR(1001, 500), AUTHENRIZATION_ERROR(1003, 403), INSUFFICIENT_PRIVILEGE_OR_QUOTA_LIMIT_EXCEEDED(1003,
				403), MISSING_ARGUMENTS(1004, 400), INVALID_ARGUMENTS(1005, 400), ILLEGAL_USE_OF_DEMO_KEY(1006,
						403), TOO_MANY_ITREMS_TO_ADD(1008, 400), SERVER_TOO_BUSY(1202,
								502), IMAGE_ERROR_UNSPPORTED_FORMAT(1301, 431), IMAGE_ERROR_FAILED_TO_DOWNLOAD(1302,
										432), IMAGE_ERROR_FILE_TOO_LARGE(1303, 433), IMAGE_ERROR(1304,
												434), OBJECT_UNTARINED(1401, 441), BAD_NAME(1501,
														451), BAD_TAG(1502, 452), NAME_EXIST(1503, 453);

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

		public static FaceppCode fromErrorCode(int code) {
			return ECmap.get(code);
		}

		public static FaceppCode fromHttpCode(int code) {
			return HTTPmap.get(code);
		}
	}

	@Override
	public FRServiceHandlerVerifyResponse verify(String personID, ImageData image) {
		// TODO Auto-generated method stub
		PostParameters param = new PostParameters();
		double confidence = 0;
		boolean isSamePerson = false;
		String sessionId;
		boolean faceDetected, serviceResponded = false;
		
		String faceID = detectFace(image);
		faceDetected = faceID!=null;
		if(faceDetected){
			param.setPersonId(personID);
			param.setFaceId(faceID);
			try{
				JSONObject rst = httpRequests.recognitionVerify(param);
				serviceResponded = true;
				confidence = rst.getDouble("confidence")/100;
				isSamePerson = rst.getBoolean("is_same_person");
				sessionId = rst.getString("session_id");
			} catch (FaceppParseException e){
				System.err.println("Error verifying face: "+e.getMessage());
			} catch (JSONException e){
				System.err.println("Error reading JSON response: "+e.getMessage());
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
		// TODO Auto-generated method stub
		return SERVICE_NAME;
	}

	@Override
	public double getFRServiceCutoff() {
		// TODO Auto-generated method stub
		return CUTOFF;
	}

}
