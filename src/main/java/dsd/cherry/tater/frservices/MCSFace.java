package dsd.cherry.tater.frservices;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dsd.cherry.tater.types.ErrorCodes;
import dsd.cherry.tater.types.ImageData;

public class MCSFace extends FRServiceHandler {
	private final static String SERVICE_NAME = "Microsoft Cognitive Service Face";
	private final static int REQ_DELAY_MILLISECONDS = 300;
	private final static int TRAINING_PHOTO_MINIMUM = 5;
	private final static double CUTOFF = 0.9;
	private MCSFunc mcs = null;

	public MCSFace(int timeoutSeconds) {
		mcs = new MCSFunc();
		setTimeout(timeoutSeconds);
	}

	// 判断当前groupId存不存在
	// 存在，说明以前train过，此时userID有效，判断这个人已经有几张图片，得出还差几张图片。将当前的images能加的加进去，如果数量不够，不train了，返回图片的信息。
	// 不存在，说明第一次train，此时userID为创建的groupID的标志符，创建一个人，并且将images能加的加进去，如果没到数量就不要train了。
	// 在整个train过程中如果有异常，说明内部有错误，此时应该返回trainSucces ＝ false； 各张图片都不要accept了。


	@Override
	public FRServiceHandlerTrainResponse train(String userID, List<ImageData> images) {
		// TODO Auto-generated method stub
		boolean groupExisted = false;
		boolean personExisted = false;
		boolean serviceResponded = false;
		boolean trainSuccessed = false; // if the person have enough photos and
										// the train has successful, it will be
										// true;
		boolean imagesAccepted = false;
		int nPhotosExisted = 0, nPhotosNeeded = 0;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		MCSFunc mcs = new MCSFunc();

		if (userID == null) {
			System.err.println(
					"MCS need a userID whenever you call the train function. create a person group need it but i don't want to create it myself!");
			return new FRServiceHandlerTrainResponse(this.getFRServiceName(), serviceResponded, null, trainSuccessed,
					images);
		}

		String personGroupId = userID;

		try {
			jsonObject = mcs.createPersonGroup(personGroupId);
			if (jsonObject == null) {
				groupExisted = true;
			} else if (jsonObject.has("error")) {
				jsonObject = jsonObject.getJSONObject("error");
				if (jsonObject.has("code") && jsonObject.getString("code").equals("PersonGroupExists")) {
					groupExisted = true;
				} else {
					System.err.println("createPersonGroup internal error won't handler!");
					groupExisted = false;
				}
			}

			if (!groupExisted) {
				System.err.println("group doesn't exist, so next step won't be excuted!");
				return new FRServiceHandlerTrainResponse(this.getFRServiceName(), serviceResponded, userID,
						trainSuccessed, images);
			}

			String personId = null;
			jsonObject = mcs.getGroupPersons(personGroupId);
			if (jsonObject == null) {// there is no person in personGroupId
				personExisted = false;
			} else if (jsonObject.has("personId")) {
				personExisted = true;
				personId = jsonObject.getString("personId");
				jsonArray = jsonObject.getJSONArray("persistedFaceIds");
				nPhotosExisted = jsonArray.length();
				nPhotosNeeded = TRAINING_PHOTO_MINIMUM - nPhotosExisted;
			} else if (jsonObject.has("error")) {
				System.err.println("getGroupPersons internal error won't handler!");
			} else if (jsonObject.has("own_created_error")) {
				System.err.println(
						"getGroupPersons has more than one person, it's a terrible problem if it happens. i think it won't happen in normal secenes. so i will delete the group and return. ");
				mcs.deletePersonGroup(personGroupId);
				return new FRServiceHandlerTrainResponse(this.getFRServiceName(), serviceResponded, userID,
						trainSuccessed, images);
			}

			if (!personExisted) {
				jsonObject = mcs.createPerson(personGroupId);
				if(jsonObject == null){
					personExisted = true;
					personId = jsonObject.getString("personId");
					nPhotosExisted = 0;
					nPhotosNeeded = TRAINING_PHOTO_MINIMUM;
				} else if(jsonObject.has("error")){
					jsonObject = jsonObject.getJSONObject("error");
					if(jsonObject.has("code") && jsonObject.getString("code").equals("PersonGroupTrainingNotFinished"))
						System.out.println("Person group 'sample_group' is under training.");
					else 
						System.out.println("createPerson has an internal error that i won't handler");
					personExisted = false;
				}
			}
			
			if(!personExisted){
				System.out.println("createPerson failed. so i will return train results. ");
				return new FRServiceHandlerTrainResponse(this.getFRServiceName(), serviceResponded, userID, trainSuccessed, images);
			}
			
			for(int i=0; i<images.size() && nPhotosNeeded >0; i++){
				ImageData image = images.get(i);
				jsonObject = mcs.detectFace(image.getImageBinary(),false);
				if(!(jsonObject.has("own_created_error") || jsonObject.has("error"))){
					jsonObject = mcs.addPersonFace(personGroupId, personId, image.getImageBinary());
					if(!jsonObject.has("error")){
						images.get(i).setAcceptedByService(true);
						images.get(i).addCode(ErrorCodes.OK);
						nPhotosNeeded--;
					} else{
						images.get(i).setAcceptedByService(false);
						images.get(i).addCode(ErrorCodes.IMAGE_ERROR_UNKNOWN);
					}
				} else if(jsonObject.has("own_created_error")){
					images.get(i).setAcceptedByService(false);
					images.get(i).addCode(ErrorCodes.IMAGE_ERROR_FACE_NOT_DETECTED);//this error is caused by no or more than one face in an image;
				} else if(jsonObject.has("error")){
					images.get(i).setAcceptedByService(false);
					jsonObject = jsonObject.getJSONObject("error");
					if(jsonObject.has("code") && jsonObject.getString("code").equals("InvalidImage"))
						images.get(i).addCode(ErrorCodes.IMAGE_ERROR_UNSUPPORTED_FORMAT);
					else if(jsonObject.has("code") && jsonObject.getString("code").equals("InvalidImageSize"))
						images.get(i).addCode(ErrorCodes.IMAGE_ERROR_FILE_TOO_LARGE);
				}
			}
			
			if(nPhotosNeeded == 0){
				jsonObject = mcs.trainPersonGroup(personGroupId);
				if(jsonObject != null && jsonObject.has("error")){
					System.out.println("trainPersonGroup internal error!");
					return new FRServiceHandlerTrainResponse(this.getFRServiceName(), serviceResponded, userID, trainSuccessed, images);
				}
				
				
				int nTimes = (this.getTimeout()*1000)/this.REQ_DELAY_MILLISECONDS;
				for(int i=0; i<nTimes; i++){
					jsonObject = mcs.getGroupTrainingStatus(personGroupId);
					if(jsonObject.has("status") && jsonObject.getString("status").equals("succeeded")){
						serviceResponded = true;
						break;
					}
					else if(jsonObject.has("error")){
						System.err.println("getGroupTrainingStatus has an error i won't handler");
						break;
					}
				}
				
				if(serviceResponded)   trainSuccessed = true;
			}
		} catch (Exception e) {
			System.out.println("MCS train: has an error!");
		}

		return new FRServiceHandlerTrainResponse(this.getFRServiceName(), serviceResponded, userID, trainSuccessed, images);
	}
	
	
	@Override
	public FRServiceHandlerVerifyResponse verify(String personID, ImageData image) {
		boolean serviceResponded = false;
		boolean groupTrained = false;
		double confidence = 0.0;
		MCSFunc mcs = new MCSFunc();
		
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		
		String personGroupId = personID;
		String tempFaceId = null;
		try{
			jsonObject = mcs.detectFace(image.getImageBinary(), true);
			if(jsonObject == null){
				System.err.println("MCS verify: image has no face detected!");
				return new FRServiceHandlerVerifyResponse(this.getFRServiceName(), serviceResponded, confidence, this.CUTOFF, personID);
			} else if(jsonObject.has("own_created_error")){
				System.err.println("MCS verify: image has more than one face");
				return new FRServiceHandlerVerifyResponse(this.getFRServiceName(), serviceResponded, confidence, this.CUTOFF, personID);
			} else if(jsonObject.has("error")){
				System.err.println("MCS verify: detectFace has an error, and i won't handler!");
				return new FRServiceHandlerVerifyResponse(this.getFRServiceName(), serviceResponded, confidence, this.CUTOFF, personID);
			} else if(jsonObject.has("faceId")){
				tempFaceId = jsonObject.getString("faceId");
			} 
			
			jsonObject = mcs.getGroupTrainingStatus(personGroupId);
			if(jsonObject.has("status") && jsonObject.getString("status").equals("succeeded")){
				groupTrained = true;
			} else if(jsonObject.has("error")){
				System.err.println("MCS verify: getGroupTrainingStatus has an error and i won't handler");
				return new FRServiceHandlerVerifyResponse(this.getFRServiceName(), serviceResponded, confidence, this.CUTOFF, personID);
			}
			
			if(groupTrained){
				jsonArray = new JSONArray();
				jsonArray.put(tempFaceId);
				JSONObject faceIds = new JSONObject();
				faceIds.put("faceIds", jsonArray);
				System.out.println(faceIds.toString()+"_________");
				System.out.println(faceIds.length());
				jsonObject = mcs.identify(personGroupId, faceIds.toString(), 1);
				if(!jsonObject.has("error")){
					jsonArray = jsonObject.getJSONArray("candidates");
					if(jsonArray.length() != 1){
						System.err.println("MCS verify: identify no person or more person!");
						return new FRServiceHandlerVerifyResponse(this.getFRServiceName(), serviceResponded, confidence, this.CUTOFF, personID);
					} else{
						confidence = jsonArray.getJSONObject(0).getDouble("confidence");
						serviceResponded = true;
					}
				} else {
					System.err.println("MCS verify: identify has an error!");
				}
			}
		} catch(Exception e){
			System.out.println("MCS verify: has an exception");
			e.printStackTrace();
		}
		
		return new FRServiceHandlerVerifyResponse(this.getFRServiceName(), serviceResponded, confidence, this.CUTOFF, personID);
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 String userId = "group_2016_5_26";
		 MCSFace fr = new MCSFace(1000);
		 ArrayList<ImageData> images = new ArrayList<>() ;
		 String path = "/Users/a864016618/Downloads/fanbing.jpg";
		 for(Integer i=0; i<5; i++){
		 ImageData image = new ImageData();
		 image.setImageID(i.toString());
		 byte[] imageByte = MCSFunc.imageToBytes(path);
		 image.setImageBinary(imageByte);
		 images.add(image);
		 }
		
		 System.out.println(images.get(0).getImageID());
		 System.out.println(images.get(1).getImageBinary().toString());
		
		
		// fr.train(userId, images);
		 ImageData image = new ImageData();
		 image.setImageID("1");
		 byte[] imageByte = MCSFunc.imageToBytes(path);
		 image.setImageBinary(imageByte);
		 fr.verify(userId, image);
	
	}

}
