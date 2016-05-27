package dsd.cherry.tater.frservices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;

import javax.imageio.stream.FileImageInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * i implement functions from MCS which i think will be used, i assume all the parameters can directly use, you don't need to parse it. 
 * later maybe i can overwrite the function for less codes
 * the date is 2016_5_19
 * next i will implement the MCSHandler class extends from FacilitatorHandler
 * here i have a problem that if you want to create a person, you have to give a groupId for calling. so what groupId should i give? 
 * Is the groupId means internalId?
 */



//pay attention: 
// detectFace and getGroupPersons return jsonArray. when implement them, you will handler the error, and create your own json error
public class MCSFunc {
	String key = null;
	HttpClient httpClient = null;

	public MCSFunc() {
		key = "ed8d253cd42249b89f39aad5bc45b4f3";
		httpClient = HttpClients.createDefault();
	}

	public MCSFunc(String key) {
		this.key = key;
		httpClient = HttpClients.createDefault();
	}

	/*
	 * detectFace description: Detect human faces in an image and returns face
	 * locations, and optionally with face ID. param: url is the image location,
	 * returnFaceId: ture or false return: A successful call returns an array of
	 * face entries ranked by face rectangle size in descending order. An empty
	 * response indicates no faces detected you can get a faceId which will
	 * expire in 24 hours after detection call if you choose returnFaceId
	 */
	public JSONObject detectFace(String url, boolean returnFaceId) throws Exception {
		JSONObject jsonRes = null;
		String res = null;
		JSONArray jsonArray = null;
		try {
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/face/v1.0/detect");
			if (returnFaceId)
				builder.setParameter("returnFaceId", "true");
			else
				builder.setParameter("returnFaceId", "false");
			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", url);
			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				try{
					jsonArray = new JSONArray(res);
					if(jsonArray.length() != 1){
						jsonRes = new JSONObject();
						jsonRes.put("own_created_error", "image has more than one face!");
					} else{
						jsonRes = jsonArray.getJSONObject(0);
					}
				} catch(JSONException e){   //After calling mcs detectface, it return an error message.
					jsonRes = new JSONObject(res);
				}
			}
		} catch (Exception e) {
			System.err.println("detectFace has exception");
			throw e;
		}

		return jsonRes;
	}

	public JSONObject detectFace(byte[] image, boolean returnFaceId) throws Exception {
		JSONObject json = null;
		String res = null;
		JSONArray jsonArray = null;
		try {
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/face/v1.0/detect");
			if (returnFaceId)
				builder.setParameter("returnFaceId", "true");
			else
				builder.setParameter("returnFaceId", "false");
			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/octet-stream");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			ByteArrayEntity requestEntity = new ByteArrayEntity(image);
			httpRequest.setEntity(requestEntity);

			// StringEntity requestEntity = new StringEntity(image.toString());
			// httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			System.out.println("status:     " + httpResponse.getStatusLine().getStatusCode());
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				try{
					jsonArray = new JSONArray(res);
					if(jsonArray.length() != 1){
						json = new JSONObject();
						json.put("own_created_error","iamge has more than one face!//byte[] image");
					} else{
						json = jsonArray.getJSONObject(0);
					}
				} catch(JSONException e){  // return mcs error
					json = new JSONObject(res);
				}
			}
		} catch (Exception e) {
			System.err.println("detectFace has an error!///byte[] image");
			throw e;
		}

		return json;
	}

	/*
	 * return: if there has any exception, return null; else if there has any
	 * error about the request, verify will return message mcs gives, else
	 * return the right resutls;
	 */

	public JSONObject verify(String faceId1, String faceId2) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/verify";
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("faceId1", faceId1);
			jsonObject.put("faceId2", faceId2);
			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("verify has exceptions");
			throw e;
		}

		return json;
	}

	// /*
	// * createPersonGroup description: create a group named as personGroupId
	// * param: null return: if createPersonGroup successes, print null and
	// return
	// * true, else, print errors and return false;
	// *
	// */
	// public boolean createPersonGroup() {
	// String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" +
	// personGroupId;
	// boolean res = false;
	// try {
	// URIBuilder builder = new URIBuilder(temp);
	// URI uri = builder.build();
	// HttpPut httpRequest = new HttpPut(uri);
	// httpRequest.setHeader("Content-Type", "application/json");
	// httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);
	//
	// JSONObject jsonObject = new JSONObject();
	// jsonObject.put("name", "group1");
	// jsonObject.put("userData", "first group");
	// StringEntity requestEntity = new StringEntity(jsonObject.toString());
	// httpRequest.setEntity(requestEntity);
	//
	// HttpResponse httpResponse = httpClient.execute(httpRequest);
	// HttpEntity entity = httpResponse.getEntity();
	//
	// if (entity != null) {
	// System.out.println(EntityUtils.toString(entity));
	// }
	//
	// if ((EntityUtils.toString(entity)).equals(""))
	// res = true;
	// else
	// res = false;
	// } catch (Exception e) {
	// System.out.println("createPersonGroup errors!");
	// e.printStackTrace();
	// }
	// if (res)
	// System.out.println("createPersonGroup success!");
	// return res;
	// }

	/*
	 * createPersonGroup: param: name and userData can't be null or empty.
	 * return successful call return null json, else, return the error json;
	 */

	public JSONObject createPersonGroup(String personGroupId) throws Exception {
		String name = "group_name_default";
		String userData = "group_userData_default";
		JSONObject json = null;
		try {
			json = createPersonGroup(personGroupId, name, userData);
		} catch (Exception e) {
			throw e;
		}
		
		return json;
	}

	/*
	 * return: successful call return null json; other, return the error json
	 */
	public JSONObject createPersonGroup(String personGroupId, String name, String userData) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();

			HttpPut httpRequest = new HttpPut(uri);
			httpRequest.setHeader("Content-type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", name);
			jsonObject.put("userData", userData);

			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				if (!res.equals(""))
					json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("createPersonGroup failed");
			throw e;
		}
		if (res.equals(""))
			System.out.println("createPersonGroup success!");
		return json;
	}
/*
 * successful call returns null json; else return the error json;
 */
	public JSONObject trainPersonGroup(String personGroupId) throws Exception {
		JSONObject json = null;
		String res = null;
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/train";
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				if(!res.equals(""))
					json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("trainPersonGroup has an exception!");
			throw e;
		}

		return json;
	}

	/*
	 * return: successful call returns json results. else return the error code.
	 */
	public JSONObject getGroupTrainingStatus(String personGroupId) throws Exception {
		JSONObject json = null;
		String res = null;
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/training";
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();

			HttpGet httpRequest = new HttpGet(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("getGroupTrainingStatus has an exception!");
			throw e;
		}

		return json;
	}

	/*
	 * return: successful call returns the json array, although the mcs html shows the wrong result format, else return json error;
	 * 
	 * 
	 */
	public JSONObject identify(String personGroupId, String faceIds, int maxNumOfCandidatesReturned) throws Exception {
		String res = null;
		JSONObject json = null;
		JSONArray jsonArray = null;
		try {
			URIBuilder builder = new URIBuilder("https://api.projectoxford.ai/face/v1.0/identify");
			URI uri = builder.build();

			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject(faceIds);
			jsonObject.put("personGroupId", personGroupId);
			jsonObject.put("maxNumofCandidatesReturned", maxNumOfCandidatesReturned);
			
			System.out.println(jsonObject.toString());

			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				try{
					jsonArray = new JSONArray(res);
					if(jsonArray.length() != 1) System.out.println("identify more than two faces, we only return personId of one face");
					json = jsonArray.getJSONObject(0);
				} catch(JSONException e){
					System.out.println("there is an error in calling identify of mcs api");
					json = new JSONObject(res);
				}
			}
		} catch (Exception e) {
			System.err.println("identify has an Exception");
			throw e;
		}

		return json;
	}

	
	/*
	 * successful call return null json, else return json error!
	 */
	public JSONObject deletePersonGroup(String personGroupId) throws Exception {
		JSONObject json = null;
		String res = null;
		try {
			String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId;
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpDelete httpRequest = new HttpDelete(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				if(!res.equals(""))  json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("deletePersonGroup failed!");
			throw e;
		}
		if (res.equals(""))
			System.out.println("deletePersonGroup success!");
		
		return json;
	}

	// /*
	// * deletePersonGroup description: delete personGroupId belonging to key
	// * param: no parameters return: if deletePersonGroup successes, print
	// * nothing and return true, else if any errors occurs, print the errors
	// and
	// * return false;
	// */
	// public boolean deletePersonGroup() {
	// HttpEntity entity = null;
	// boolean res = false;
	// try {
	// String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" +
	// personGroupId;
	// URIBuilder builder = new URIBuilder(temp);
	// URI uri = builder.build();
	// HttpDelete httpRequest = new HttpDelete(uri);
	// httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);
	//
	// HttpResponse httpResponse = httpClient.execute(httpRequest);
	// entity = httpResponse.getEntity();
	//
	// if (entity != null) {
	// System.out.println(EntityUtils.toString(entity));
	// }
	//
	// if ((EntityUtils.toString(entity)).equals(""))
	// res = true;
	// else
	// res = false;
	// } catch (Exception e) {
	// System.out.println("deletePersonGroup errors!");
	// e.printStackTrace();
	// }
	//
	// if (res)
	// System.out.println("deletePersonGroup success!");
	// return res;
	// }

	/*
	 * createPerson description: create a new person in a specified person
	 * group, you have to give a personGroupId if you want to create a person
	 * param: the person's name such as "fanny" and userData such as
	 * "love eating apples! and she is a girl!" return: a persisted personId
	 * {"personId":....}
	 */

	public JSONObject createPerson(String personGroupId) throws Exception {
		JSONObject json = null;
		String name = "person_name_default";
		String userData = "person_userData_default";
		try {
			json = createPerson(personGroupId, name, userData);
		} catch (Exception e) {
			throw e;
		}

		return json;
	}
/*
 * return: successful call return personId json; else return the json error!
 */
	public JSONObject createPerson(String personGroupId, String name, String userData) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons";
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();

			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", name);
			jsonObject.put("userData", userData);

			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("createPerson has an Exception");
			throw e;
		}

		return json;
	}

	/*
	 * deletePerson description: Delete an existing person from a person group.
	 * Persisted face images of the person will also be deleted. param: personId
	 * such as "028e08b1-032a-4b7b-8a26-ec071693dbaa" not jsonObject return: A
	 * successful call returns an empty response body. if the body is empty,
	 * return true; else print the error message, return false;
	 */
	
	/*
	 * return: successful call returns json null, else return json error;
	 */

	public JSONObject deletePerson(String personGroupId, String personId) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpDelete httpRequest = new HttpDelete(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				if(!res.equals(""))
					json = new JSONObject(res);
				System.out.println(res);
			}
		} catch (Exception e) {
			System.err.println("deletePerson has an Exception!");
			throw e;
		}

		if (res.equals(""))
			System.out.println("deletePerson successes!");
		
		return json;
	}

	/*
	 * addPersonFace description: param: url is image location, personId is not
	 * json, userData is information you give about the image, targetFace
	 * represents face location in the image eg: targetFace: "167,38,47,47"
	 * means "left, top, width, height" return: A successful call returns the
	 * new persisted face ID.
	 */
	
	public JSONObject addPersonFace(String personGroupId, String personId, byte[] image) throws Exception{
		String res = null;
		JSONObject json = null;
		
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces";		
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/octet-stream");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			ByteArrayEntity requestEntity = new ByteArrayEntity(image);
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("addPersonFace has an exception!");
			throw e;
		}

		return json;
	}
	
	/*
	 * return: successful call returns persistedFaceId, else returns json error!
	 */
	public JSONObject addPersonFace(String personGroupId, String personId, byte[] image, String targetFace)
			throws Exception {
		String userData = "person_face_userData_default";
		JSONObject json = null;
		try {
			json = addPersonFace(personGroupId, personId, image, userData, targetFace);
		} catch (Exception e) {
			throw e;
		}

		return json;
	}

	public JSONObject addPersonFace(String personGroupId, String personId, byte[] image, String userData, String targetFace)
			throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces";
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			builder.setParameter("userData", userData);
			builder.setParameter("targetFace", targetFace);
			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/octet-stream");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			ByteArrayEntity requestEntity = new ByteArrayEntity(image);
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("addPersonFace has an exception!");
			throw e;
		}

		return json;
	}

	public JSONObject addPersonFace(String personGroupId, String personId, String url, String userData, String targetFace)
			throws Exception {// i
		// thought
		// userData
		// is
		// meaningless
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces";
		String res = null;
		JSONObject json = null;
		
		try {
			URIBuilder builder = new URIBuilder(temp);
			builder.setParameter("userData", userData);
			builder.setParameter("targetFace", targetFace);

			URI uri = builder.build();
			HttpPost httpRequest = new HttpPost(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("url", url);
			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("addPersonFace has an Exception!");
			throw e;
		}

		return json;
	}

	/*
	 * deleteePersonFace description: Delete a face from a person. Relative
	 * image for the persisted face will also be deleted. param: personId is not
	 * json, persistedFaceId is not json return: a successful call will print an
	 * empty body, and return true, else print the wrong message and return
	 * false;
	 */

	/*
	 * return: successful call returns null json; else returns the error json
	 */
	public JSONObject deletePersonFace(String personGroupId, String personId, String persistedFaceId) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces/" + persistedFaceId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpDelete httpRequest = new HttpDelete(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				if(!res.equals(""))
					json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("deletePersonFace has an Exception!");
			throw e;
		}
		if (res.equals(""))
			System.out.println("deletePersonFace successes!");

		return json;
	}

	/*
	 * getGroupPersons description: List all people in a person group, and
	 * retrieve person information (including person ID, name, user data and
	 * registered faces of the person). param: null return: A successful call
	 * returns a person information array of all people belong to the person
	 * group.
	 */
	
	/*
	 * return: a successful call returns json result, else return json error
	 */

	public JSONObject getGroupPersons(String personGroupId) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons";
		String res = null;
		JSONObject json = null;
		JSONArray jsonArray = null;
		
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpGet httpRequest = new HttpGet(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				try{
					jsonArray = new JSONArray(res);
					if(jsonArray.length() > 1){
						json = new JSONObject();
						json.put("own_created_error", "group has more than one person!");
					} else if(jsonArray.length() == 1){
						json = jsonArray.getJSONObject(0);
					}
				} catch(JSONException e){
					json = new JSONObject(res);
				}
			}
		} catch (Exception e) {
			System.err.println("getGroupPersons has an Exception!");
			throw e;
		}

		return json;
	}

	/*
	 * getPerson description: Retrieve a person's information, including
	 * registered faces, name and userData. param: personId not json return: A
	 * successful call returns the person's information including
	 * persistedFaceIds.
	 */
	public JSONObject getPerson(String personGroupId, String personId) throws Exception {// personId
		// can
		// directly
		// use
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpGet httpRequest = new HttpGet(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("getPerson has an Exception!");
			throw e;
		}

		return json;
	}

	/*
	 * getPersonFaceInfo description: Retrieve information about a face
	 * (specified by face ID, person ID and its belonging person group ID).
	 * param: personId not json, persistedFaceId not json return: A successful
	 * call returns face's information (face ID and user data).
	 */
	public JSONObject getPersonFaceInfo(String personGroupId, String personId, String persistedFaceId) throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces/" + persistedFaceId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();

			HttpGet httpRequest = new HttpGet(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("getPersonFaceInfo has an Exception!");
			throw e;
		}

		return json;
	}

	/*
	 * updatePersonInfo description: Update a person's name or userData field.
	 * param: personId not json, name not json, userData not json; return: a
	 * successful call print empty body and return true, else print wrong
	 * message and return false;
	 */

	/*
	 * return: successful call returns null json, else returns json error
	 */
	public JSONObject updatePersonInfo(String personGroupId, String personId, String name, String userData)
			throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();

			HttpPatch httpRequest = new HttpPatch(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", name);
			jsonObject.put("userData", userData);
			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				if(!res.equals(""))
					json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.err.println("updatePersonInfo has an Exception!");
			throw e;
		}
		if (res.equals(""))
			System.out.println("updatePersonInfo success!");

		return json;
	}

	public JSONObject updatePersonFaceInfo(String personGroupId, String personId, String persistedFaceId, String userData)
			throws Exception {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces/" + persistedFaceId;
		String res = null;
		JSONObject json = null;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();

			HttpPatch httpRequest = new HttpPatch(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("userData", userData);
			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				res = EntityUtils.toString(entity);
				System.out.println(res);
				if(res.equals(""))
					json = new JSONObject(res);
			}
		} catch (Exception e) {
			System.out.println("updatePersonFaceInfo has an Exception!");
			throw e;
		}
		if (res.equals(""))
			System.out.println("updatePersonFaceInfo success!");

		return json;
	}

	public static byte[] imageToBytes(String path) {
		byte[] res = null;
		try {
			FileImageInputStream in = new FileImageInputStream(new File(path));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bufferReadSize = 0;
			while ((bufferReadSize = in.read(buffer)) != -1) {
				out.write(buffer, 0, bufferReadSize);
			}
			res = out.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static void main(String[] args) {

		// JSONObject obj = new JSONObject();
		// try {
		// obj.put("persistedFaceId", "12345");
		// System.out.println(obj.get("Hello"));
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//

		MCSFunc obj = new MCSFunc();
		String url = "http://img2.fznews.com.cn/cms/c81e72/20151009/14443805143484.jpg"; 
		String path = "/Users/a864016618/Downloads/fanbing.jpg";

		byte[] image = obj.imageToBytes(path);
		
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		String groupId = "group_2016_5_27";
		try{
			jsonObject = obj.detectFace(image, true);
			System.out.println(jsonObject.toString());
			String tempFaceId = jsonObject.getString("faceId");
			System.out.println(tempFaceId);
			
			obj.deletePersonGroup(groupId);
			obj.createPersonGroup(groupId);
			
			jsonObject = obj.getGroupPersons(groupId);
			System.out.println(jsonObject);
			
			jsonObject = obj.createPerson(groupId);
			String personId = jsonObject.getString("personId");
			obj.addPersonFace(groupId, personId, image);
			obj.getGroupPersons(groupId);
			
			obj.trainPersonGroup(groupId);
			obj.getGroupTrainingStatus(groupId);
			
			jsonArray = new JSONArray();
			jsonArray.put(tempFaceId);
			System.out.println(jsonArray);
			jsonObject = new JSONObject();
			jsonObject.put("faceIds", jsonArray);
			System.out.println(jsonObject.toString());
			
			System.out.println(obj.identify(groupId, jsonObject.toString(), 1));
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
		

		// try{
		// obj.getGroupPersons("group_2016_5_26");
		// obj.trainPersonGroup("group_2016_5_26");
		// obj.getGroupTrainingStatus("group_2016_5_26");
		// } catch (Exception e){
		// e.printStackTrace();
		// }

		
		
		
		
	
//		try {
//			JSONArray jsonArray = new JSONArray(obj.detectFace(image, true));
//			JSONObject jsonObject = jsonArray.getJSONObject(0);
//			String tempFaceId = jsonObject.getString("faceId");
//
//			jsonObject = jsonObject.getJSONObject("faceRectangle");
//			String faceRectangle = jsonObject.get("left") + "," + jsonObject.get("top") + "," + jsonObject.get("width")
//					+ "," + jsonObject.get("height");
//
//			String personGroupId = "group_2016_5_25";
//			obj.deletePersonGroup(personGroupId);
//			obj.createPersonGroup(personGroupId);
//
//			jsonObject = new JSONObject(obj.createPerson("group_2016_5_25"));
//			String personId = jsonObject.getString("personId");
//
//			jsonObject = new JSONObject(obj.addPersonFace(personGroupId, personId, image, "userData", faceRectangle));
//			String persistedFaceId1 = jsonObject.getString("persistedFaceId");
//
//			jsonObject = new JSONObject(obj.addPersonFace(personGroupId, personId, image, faceRectangle));
//			String persistedFaceId2 = jsonObject.getString("persistedFaceId");
//
//			obj.trainPersonGroup(personGroupId);
//
//			obj.getGroupPersons(personGroupId);
//			obj.getGroupTrainingStatus(personGroupId);
//
//			jsonArray = new JSONArray();
//			jsonArray.put(tempFaceId);
//			JSONObject faceIds = new JSONObject();
//			faceIds.put("faceIds", jsonArray);
//			// faceIds.put("faceIds", tempFaceId); faceIds's value must be an
//			// Array!
//
//			System.out.println(faceIds.toString());
//			obj.identify(personGroupId, faceIds.toString(), 1);
//
//			// System.out.println("***************************");
//
//			// obj.getPerson(personGroupId, personId);
//			// obj.deletePersonFace(personGroupId, personId, persistedFaceId1);
//			// obj.getPerson(personGroupId, personId);
//			// obj.deletePerson(personGroupId, personId);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// File file = new File("/Users/a864016618/Downloads/fanbing.jpg");
		// byte [] image;
		// try {
		// FileInputStream in = new FileInputStream(file);
		// byte b = 0;
		// while((b =in.read()) != -1){
		//
		// }
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		//
		// try {
		// JSONArray jsonArray = new JSONArray(obj.detectFace(url, true));
		// JSONObject jsonObject = jsonArray.getJSONObject(0);
		// // System.out.println(jsonObject);
		// String tempFaceId1 = jsonObject.getString("faceId");
		//
		// jsonArray = new JSONArray(obj.detectFace(url, true));
		// jsonObject = jsonArray.getJSONObject(0);
		// String tempFaceId2 = jsonObject.getString("faceId");
		// obj.verify(tempFaceId1, tempFaceId2);
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// String personId1 = obj.createPerson("person1", "nothing to say about
		// the person");
		// JSONObject jsonObject = new JSONObject(personId1);
		// String value1 = jsonObject.get("personId").toString();
		//
		// String persistedFaceId = obj.addPersonFace(url, value1, "xiaoya's
		// photo1", "272,79,125,125");
		// jsonObject = new JSONObject(persistedFaceId);
		// String value2 = jsonObject.get("persistedFaceId").toString();
		//
		// obj.getGroupPersons();
		// obj.getPerson(value1);
		// obj.getPersonFaceInfo(value1, value2);
		//
		// obj.updatePersonInfo(value1, "xiao ya update", "xiaoya userData
		// update!");
		// obj.getPerson(value1);
		//
		// obj.updatePersonFaceInfo(value1, value2, "xiaoya's photo1 update!");
		// obj.getPersonFaceInfo(value1, value2);
		//
		// obj.deletePersonFace(value1, value2);
		// obj.deletePerson(value1);

	}

}
