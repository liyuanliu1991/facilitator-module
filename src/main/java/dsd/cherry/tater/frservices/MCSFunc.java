package dsd.cherry.tater.frservices;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/*
 * i implement functions from MCS which i think will be used, i assume all the parameters can directly use, you don't need to parse it. 
 * later maybe i can overwrite the function for less codes
 * the date is 2016_5_19
 * next i will implement the MCSHandler class extends from FacilitatorHandler
 * here i have a problem that if you want to create a person, you have to give a groupId for calling. so what groupId should i give? 
 * Is the groupId means internalId?
 */

public class MCSFunc {
	String key = null;
	HttpClient httpClient = null;
	String personGroupId = null;
	boolean groupIdAvailable = false;

	public MCSFunc() {
		key = "ed8d253cd42249b89f39aad5bc45b4f3";
		httpClient = HttpClients.createDefault();
		personGroupId = "group1_2016_5_18"; // personGroupId is a problem, i
											// will read the code Andrew writes.
		deletePersonGroup();
		groupIdAvailable = false;
		if (!groupIdAvailable) {
			createPersonGroup();
		}
	}

	/*
	 * detectFace description: Detect human faces in an image and returns face
	 * locations, and optionally with face ID. param: url is the image location,
	 * returnFaceId: ture or false return: A successful call returns an array of
	 * face entries ranked by face rectangle size in descending order. An empty
	 * response indicates no faces detected you can get a faceId which will
	 * expire in 24 hours after detection call if you choose returnFaceId
	 */
	public String detectFace(String url, boolean returnFaceId) {
		String res = null;
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * createPersonGroup description: create a group named as personGroupId
	 * param: null return: if createPersonGroup successes, print null and return
	 * true, else, print errors and return false;
	 * 
	 */
	public boolean createPersonGroup() {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId;
		boolean res = false;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpPut httpRequest = new HttpPut(uri);
			httpRequest.setHeader("Content-Type", "application/json");
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", "group1");
			jsonObject.put("userData", "first group");
			StringEntity requestEntity = new StringEntity(jsonObject.toString());
			httpRequest.setEntity(requestEntity);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}

			if ((EntityUtils.toString(entity)).equals(""))
				res = true;
			else
				res = false;
		} catch (Exception e) {
			System.out.println("createPersonGroup errors!");
			e.printStackTrace();
		}
		if (res)
			System.out.println("createPersonGroup success!");
		return res;
	}

	/*
	 * deletePersonGroup description: delete personGroupId belonging to key
	 * param: no parameters return: if deletePersonGroup successes, print
	 * nothing and return true, else if any errors occurs, print the errors and
	 * return false;
	 */
	public boolean deletePersonGroup() {
		HttpEntity entity = null;
		boolean res = false;
		try {
			String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId;
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpDelete httpRequest = new HttpDelete(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			entity = httpResponse.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}

			if ((EntityUtils.toString(entity)).equals(""))
				res = true;
			else
				res = false;
		} catch (Exception e) {
			System.out.println("deletePersonGroup errors!");
			e.printStackTrace();
		}

		if (res)
			System.out.println("deletePersonGroup success!");
		return res;
	}

	/*
	 * createPerson description: create a new person in a specified person
	 * group, you have to give a personGroupId if you want to create a person
	 * param: the person's name such as "fanny" and userData such as
	 * "love eating apples! and she is a girl!" return: a persisted personId
	 * {"personId":....}
	 */
	public String createPerson(String name, String userData) {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons";
		String res = null;
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * deletePerson description: Delete an existing person from a person group.
	 * Persisted face images of the person will also be deleted. param: personId
	 * such as "028e08b1-032a-4b7b-8a26-ec071693dbaa" not jsonObject return: A
	 * successful call returns an empty response body. if the body is empty,
	 * return true; else print the error message, return false;
	 */

	public boolean deletePerson(String personId) {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId;
		boolean res = false;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpDelete httpRequest = new HttpDelete(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}

			if (EntityUtils.toString(entity).equals(""))
				res = true;
			else
				res = false;
		} catch (Exception e) {
			System.out.println("deletePerson errors!");
			e.printStackTrace();
		}

		if (res)
			System.out.println("deletePerson successes!");
		return res;
	}

	/*
	 * addPersonFace description: param: url is image location, personId is not
	 * json, userData is information you give about the image, targetFace
	 * represents face location in the image eg: targetFace: "167,38,47,47"
	 * means "left, top, width, height" return: A successful call returns the
	 * new persisted face ID.
	 */
	public String addPersonFace(String url, String personId, String userData, String targetFace) {// i
																									// thought
																									// userData
																									// is
																									// meaningless
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces";
		String res = null;

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
			}
		} catch (Exception e) {
			System.out.println("addPersonFace fails!");
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * deleteePersonFace description: Delete a face from a person. Relative
	 * image for the persisted face will also be deleted. param: personId is not
	 * json, persistedFaceId is not json return: a successful call will print an
	 * empty body, and return true, else print the wrong message and return
	 * false;
	 */

	public boolean deletePersonFace(String personId, String persistedFaceId) {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces/" + persistedFaceId;
		boolean res = false;
		try {
			URIBuilder builder = new URIBuilder(temp);
			URI uri = builder.build();
			HttpDelete httpRequest = new HttpDelete(uri);
			httpRequest.setHeader("Ocp-Apim-Subscription-Key", key);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				String bodyRes = EntityUtils.toString(entity);
				if (bodyRes.equals(""))
					res = true;
				System.out.println(bodyRes);
			}
		} catch (Exception e) {
			System.out.println("deletePersonFace fails!");
			e.printStackTrace();
		}
		if (res)
			System.out.println("deletePersonFace successes!");
		return res;
	}

	/*
	 * getGroupPersons description: List all people in a person group, and
	 * retrieve person information (including person ID, name, user data and
	 * registered faces of the person). param: null return: A successful call
	 * returns a person information array of all people belong to the person
	 * group.
	 */

	public String getGroupPersons() {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons";
		String res = null;
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
			}
		} catch (Exception e) {
			System.out.println("getGroupPersons fails!");
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * getPerson description: Retrieve a person's information, including
	 * registered faces, name and userData. param: personId not json return: A
	 * successful call returns the person's information.
	 */
	public String getPerson(String personId) {// personId can directly use
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId;
		String res = null;
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * getPersonFaceInfo description: Retrieve information about a face
	 * (specified by face ID, person ID and its belonging person group ID).
	 * param: personId not json, persistedFaceId not json return: A successful
	 * call returns face's information (face ID and user data).
	 */
	public String getPersonFaceInfo(String personId, String persistedFaceId) {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces/" + persistedFaceId;
		String res = null;
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * updatePersonInfo description: Update a person's name or userData field.
	 * param: personId not json, name not json, userData not json; return: a
	 * successful call print empty body and return true, else print wrong
	 * message and return false;
	 */

	public boolean updatePersonInfo(String personId, String name, String userData) {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId;
		boolean res = false;
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
				String bodyRes = EntityUtils.toString(entity);
				if (bodyRes.equals(""))
					res = true;
				System.out.println(bodyRes);
			}
		} catch (Exception e) {
			System.out.println("updatePersonInfo has an error!");
			e.printStackTrace();
		}

		return res;
	}

	public boolean updatePersonFaceInfo(String personId, String persistedFaceId, String userData) {
		String temp = "https://api.projectoxford.ai/face/v1.0/persongroups/" + personGroupId + "/persons/" + personId
				+ "/persistedFaces/" + persistedFaceId;
		boolean res = false;
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
				String bodyRes = EntityUtils.toString(entity);
				if (bodyRes.equals(""))
					res = true;
				System.out.println(bodyRes);
			}
		} catch (Exception e) {
			System.out.println("updatePersonFaceInfo fails!");
			e.printStackTrace();
		}

		return res;
	}

}
