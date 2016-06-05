/**
 * Constructor
 * Creates a FacePPCommunicator to be used for communicating with facepp
 */
public FacePPCommunicator(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        useChineseServer = false;
        useHttp = true;
        groupName = "faceSqaud2";
        initializeHttpRequests();
        }

/**
 * Initializes the HTTP request object with the API key and API secret.
 */
private void initializeHttpRequests() {
        httpRequests = new HttpRequests(apiKey, apiSecret, useChineseServer, useHttp);
        }

/** NOT USING THIS METHOD
 * Attempts to detect a face in the image at the given URL.
 * Returns the face ID associated with the face in the image
 */
protected String detectFace(String url){
        System.out.println("detecting face in FacePP with url:" + url +"\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.detectionDetect(new PostParameters().setUrl(url));
        } catch (FaceppParseException e) {
        e.printStackTrace();
        return IMAGE_ERROR + e.getMessage();
        }
        System.out.println(result.toString());
        return getFaceId(result);
        }


/** NOT USING THIS METHOD
 * Attempts to detect a face in the image file.
 * Returns the faceId associated with the face in the image.
 */
protected String detectFace(File file){
        System.out.println("detecting face in FacePP" + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.detectionDetect(new PostParameters().setImg(file));
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        return IMAGE_ERROR + e.getMessage();
        }
        System.out.println(result.toString());
        return getFaceId(result);
        }

/**
 * Attempts to detect a face in the image byte array.
 * Returns the faceId associated with the face in the image.
 */
protected String detectFace(byte[] data) {
        System.out.println("detecting face in FacePP" + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.detectionDetect(new PostParameters().setImg(data));
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        return IMAGE_ERROR + e.getMessage();
        }
        System.out.println("result: " + result.toString() + "\n");
        return getFaceId(result);
        }

/**
 * NOT USING THIS METHOD
 * Creates a person with the given name and adds them to the existing group.
 * We want to do this since for each user, only one group will be necessary, and since
 * each user gets its own instance of FacePPCommunicator, so there shouldn't be any issues
 * implementing this method this way.
 */
protected String createPersonInGroup(String personName) {
        System.out.println("Creating person in group on FACEPP: " + personName + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personCreate(new PostParameters().setPersonName(personName));
        System.out.println("result: " + result.toString() +  "\n");
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        }
        String personID = null;
        try {
        personID = result.getString("person_id");
        } catch (JSONException e) {
        e.printStackTrace();
        }
        System.out.println("person ID: " + personID);
        addPersonToGroup(personID);
        return personID;
        }

/**
 * @return: the personID assigned by facepp. This is another way of communicating to facepp who we are dealing with.
 */
protected String createPerson(){
        System.out.println("Creating person in FACEPP: " + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personCreate(new PostParameters());
        System.out.println("result: " + result.toString() +  "\n");
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        if (e.getMessage().contains(busyServer)){
        return busyServer;
        }
        }
        String personID = null;
        try {
        personID = result.getString("person_id");
        } catch (JSONException e) {
        e.printStackTrace();
        }
        System.out.println("person ID: " + personID);
        return personID;
        }

/** NOT USING THIS METHOD
 * @param personName An identifier for a given person assigned by user. Doesn't have to be their actual name
 *                   Note: personID is different. This is assigned by facepp.
 * @return: the personID assigned by facepp. This is another way of communicating to facepp who we are dealing with.
 */
protected String createPerson(String personName){
        System.out.println("Creating person in FACEPP: " + personName + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personCreate(new PostParameters().setPersonName(personName));
        System.out.println("result: " + result.toString() +  "\n");
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        if (e.getMessage().contains(busyServer)){
        return busyServer;
        }
        }
        String personID = null;
        try {
        personID = result.getString("person_id");
        } catch (JSONException e) {
        e.printStackTrace();
        }

        System.out.println("person ID: " + personID);
        return personID;
        }

/** NOT USING THIS METHOD
 * Removes a person from the group using the person's facepp id.
 */
protected String removePersonFromGroup(String personID) {
        System.out.println("Removing person from group: " + personID + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.groupRemovePerson(new PostParameters().setGroupName(groupName).setPersonId(personID));
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

    /* NOT USING THIS METHOD.
     * removes a person from facepp using the person's facepp id.
     */
protected String removePerson(String personID){
        System.out.println("Removing person: " + personID + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personDelete(new PostParameters().setPersonId(personID));
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

    /* NOT USING THIS METHOD
     * Removes a person with the given name from facepp if that name exists on facepp and was created by the user.
     * @personName: the name assigned to a person by the user when a person is created on facepp.
     * This may not exist if a person was created with using their name.
     */
protected String removePersonWName(String personName){
        System.out.println("Removing person: " + personName + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personDelete(new PostParameters().setPersonName(personName));
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/**
 * Gets the face ID from the given JSONObject and returns it.
 * @result: the JSONObject returned when a person is created on facepp.
 * It contains the user id assigned by facepp.
 */
private String getFaceId (JSONObject result){
        String faceId = null;
        System.out.println("Retrieving face ID\n");
        try {
        faceId = result.getJSONArray("face").getJSONObject(0).getString("face_id");
        } catch (JSONException e) {
        e.printStackTrace();
        }
        System.out.println("faceId: " + faceId);
        return faceId;
        }


/** NOT USING THIS METHOD.
 * Adds a face to a person using the person's facepp Id and a url image.
 * @param personID: facepp Id
 * @param url: url of an image.
 * If the image has a problem, that error is returned.
 * Otherwise, it converts the JSONObject to a string a returns it.
 */
protected String addFaceToPerson(String personID, String url) {
        System.out.println("adding face to person: " + personID + "\n");
        String faceID = detectFace(url); //gets the faceID associated with an image.
        if (faceID == null) {
        return null;
        }
        if (faceID.contains(IMAGE_ERROR))
        {
        return IMAGE_ERROR + faceID;
        }
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personAddFace(new PostParameters().setPersonId(personID).setFaceId(faceID));
        } catch (FaceppParseException e) {
        e.printStackTrace();
        if (e.getMessage().contains(busyServer)) {
        return busyServer;
        }
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/**
 * @param personID: facepp Id for a person on facepp, created by the user.
 * @param data: image data for a single image.
 * Converts the resultant JSONObject to a string and returns it if everything went well.
 * If the image has an error, that is returned instead. If nothing is returned from detectFace,
 * null is returned.
 */
protected String addFaceToPerson(String personID, byte[] data) {
        System.out.println("adding face to person: " + personID + "\n");
        String faceID = detectFace(data); //gets the faceID associated with an image.
        if (faceID == null) {
        return null;
        }
        if (faceID.contains(IMAGE_ERROR)) //something went wrong when trying to detect a face.
        {
        return faceID;
        }
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personAddFace(new PostParameters().setPersonId(personID).setFaceId(faceID));
        } catch (FaceppParseException e) {
        e.printStackTrace();
        if (e.getMessage().contains(busyServer)) {
        return busyServer;
        }
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/**
 * NOT USING THIS METHOD
 * @param personID: facepp Id for a person on facepp, created by the user.
 * @param file: image file to be used.
 * Returns result in the form of a string.
 */
protected String addFaceToPerson(String personID, File file) {
        System.out.println("adding face to person: " + personID + "\n");
        String faceID = detectFace(file); //gets the faceID associated with an image.
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personAddFace(new PostParameters().setPersonId(personID).setFaceId(faceID));
        } catch (FaceppParseException e) {
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/** NOT USING THIS METHOD.
 * Removes a face from a person using the person's ID and the face Id associated with the image to be removed.
 */
protected String removeFace(String personID, String faceID){
        System.out.println("Removing face from person: " + personID);
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.personRemoveFace(new PostParameters().setPersonId(personID).setFaceId(faceID));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("Remove face result: " + result.toString());
        return result.toString();
        }



/** NOT USING THIS METHOD
 * Creates a person group.
 * returns the group id
 */
protected String createGroup(){
        System.out.println("[facepp] Creating group: " + groupName + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.groupCreate(new PostParameters().setGroupName(groupName));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("create group result: " + result.toString() + "\n");
        return result.toString();
        }

/** NOT USING THIS METHOD
 * Removes an existing person group.
 * @param groupName: name of group to be removed.
 */
protected String removeGroup(String groupName){
        JSONObject result = new JSONObject();
        System.out.println("removing group: " + groupName + "\n");
        try {
        result = httpRequests.groupDelete(new PostParameters().setGroupName(groupName));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/** NOT USING THIS METHOD
 * Adds a person to class instantiated group with the person's ID.
 * @param personID: facepp person id of someone user created.
 */
private String addPersonToGroup(String personID){
        System.out.println("Adding person to group: " + personID + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.groupAddPerson(new PostParameters().setGroupName(groupName).setPersonId(personID));
        }
        catch (FaceppParseException e) {
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }


/**
 * Face trains a person in preparation for identification.
 * Ideally the given person associated with the personID will have had at least 5 images of their face added before training.
 * @param  personID: facepp id of someone user created.
 * Returns the session_id as a string if everything went well. If not, this method returns "FAILURE". This
 * is most likely to happen if the server is busy the momemnt we attempt to train.
 */
protected String trainPerson(String personID){
        System.out.println("Training Person" + personID + "\n");
        JSONObject result = new JSONObject();
        try {
        result  = httpRequests.trainVerify(new PostParameters().setPersonId(personID));
        return result.getString("session_id");
        } catch (FaceppParseException e){
        e.printStackTrace();
        } catch (JSONException e) {
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return "FAILURE";
        }

/**
 * NOT USING THIS METHOD
 * Face trains the group instantiated by the instance of this class.
 * @returns session info of training training session.
 */
protected String trainGroup(){
        System.out.println("Training group: " + groupName + "\n");
        JSONObject result = new JSONObject();
        JSONObject sessionInfo;
        try {
        result = httpRequests.trainIdentify(new PostParameters().setGroupName(groupName));
        sessionInfo = httpRequests.infoGetSession(new PostParameters().setSessionId(result.getString("session_id")));
        System.out.println("Session Info: " + sessionInfo.toString() + "\n");
        return sessionInfo.toString();
        } catch (FaceppParseException e){
        e.printStackTrace();
        } catch (JSONException e) {
        e.printStackTrace();
        }
        return "UNSUCCESFULL RETRIEVAL OF SESSION INFO FOR TRAINING";
        }



/** NOT USING THIS IMAGE
 * Attempts to identify the person in the group in the image at the given URL
 */
protected String identifyPersonInGroup(String url){
        System.out.println("Identifying person in url: " + url + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.recognitionIdentify(new PostParameters().setGroupName(groupName).setUrl(url));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/** NOT USING THIS METHOD
 * @param personId, facepp id of a person created by user.
 * @param url: url of image.
 * @return returns verificatication result.
 */
protected String identifyPerson(String personId, String url){
        System.out.println("Identifying person in url: " + url + "\n");
        JSONObject result = new JSONObject();
        String faceId = detectFace(url);
        try {
        result = httpRequests.recognitionVerify(new PostParameters().setPersonId(personId).setFaceId(faceId));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }


/** NOT USING THIS METHOD
 * Attempts to identify the person in the given image file.
 * returns result of verification attempt.
 */
protected String identifyPersonInGroup(File file){
        System.out.println("Identifying person in file: " + file + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.trainIdentify(new PostParameters().setGroupName(groupName).setImg(file));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/** NOT USING THIS METHOD
 * @param personId: facepp id of a person created by user.
 * @param file: image file to be used for verifcation
 * @return verification result in string form
 */
protected String identifyPerson(String personId, File file){
        System.out.println("Identifying person in url: " + file + "\n");
        JSONObject result = new JSONObject();
        String faceId = detectFace(file);
        try {
        result = httpRequests.recognitionVerify(new PostParameters().setPersonId(personId).setFaceId(faceId));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/** NOT USING THIS METHOD
 * Attempts to identify the person in the given image byte array.
 * Returns verification result in string form.
 */
protected String identifyPersonInGroup(byte[] data){
        System.out.println("Identifying person in data: " + data + "\n");
        JSONObject result = new JSONObject();
        try {
        result = httpRequests.trainIdentify(new PostParameters().setGroupName(groupName).setImg(data));
        } catch (FaceppParseException e){
        e.printStackTrace();
        }
        System.out.println("result: " + result.toString() + "\n");
        return result.toString();
        }

/**
 * If the image has any problems, this method will add the appropriate error code to the image.
 * @param personId: facepp id for a person created by user.
 * @param image: image to used for verifcation
 * @return: null if the server is busy or if faceId is null,  or something goes wrong with verification attempt.
 * If everything went well, returns the JSONObject resultant from verification attempt.
 */
protected JSONObject identifyPerson(String personId, ImageData image){
        //System.out.println("Identifying person in url: " + data + "\n");
        JSONObject result = new JSONObject();
        String faceId = detectFace(image.getImageBinary());
        if (faceId.contains(IMAGE_ERROR)) {
        if (faceId.contains(IMAGE_FORMAT_ERROR)){
        image.addCode(ErrorCodes.IMAGE_ERROR_UNSUPPORTED_FORMAT);
        }
        else if (faceId.contains(IMAGE_DOWNLOAD_ERROR)){
        image.addCode(ErrorCodes.IMAGE_ERROR_FAILED_TO_DOWNLOAD);
        }
        else if (faceId.contains(IMAGE_FACE_NOT_DETECTED)){
        image.addCode(ErrorCodes.IMAGE_ERROR_FACE_NOT_DETECTED);
        }
        else if (faceId.contains(IMAGE_BAD_JSON_TAG)){
        image.addCode(ErrorCodes.BAD_JSON_TAG);
        }
        else if (faceId.contains(IMAGE_FILE_TOO_LARGE)){
        image.addCode(ErrorCodes.IMAGE_ERROR_FILE_TOO_LARGE);
        }
        else if (faceId.contains(IMAGE_ERROR)) {
        image.addCode(ErrorCodes.IMAGE_ERROR_UNKNOWN);
        }
        else {
        image.addCode(ErrorCodes.OK);
        }
        }
        if (faceId == null) {
        return null;
        }
        if (faceId == busyServer) {
        return null;
        }
        try {
        result = httpRequests.recognitionVerify(new PostParameters().setPersonId(personId).setFaceId(faceId));
        } catch (FaceppParseException e){
        e.printStackTrace();
        return null;
        }
        System.out.println("result: " + result.toString() + "\n");
        return result;
        }

/**
 * @param userID: facepp Id of person created by user.
 * @param images: images to be used for training the person with facepp id userID
 * returns a FRServiceHanlderTrainResponse with the appropriate fields.
 * Partial training implemented
 * If any images have errors, the appropriate image error code is added to the image.
 * If server is busy, this method waits a couple seconds and then tries to add a face to person again.
 */
public FRServiceHandlerTrainResponse train(String userID, List<ImageData> images) {
        String serviceName = "facepp";
        FacePPCommunicator fpp = new FacePPCommunicator(apiKey, apiSecret);
        FRServiceHandlerTrainResponse response =
                new FRServiceHandlerTrainResponse(serviceName, false, userID, false, images);
        if (userID == null){
            return response;
        }
        if (images == null) {
            return response;
        }
        String personId = fpp.createPerson();
        if (personId == busyServer) { //server too busy.
            return response;
        }
        Iterator<ImageData> it = images.iterator();
        JSONObject result;
        boolean goodTraining = false;
        boolean serverResponded = false;
        int index = 0;
        int goodIndex = -1;
        int goodImages = 0;
        while (it.hasNext()) {
            ImageData image = it.next();
            String addFaceResult = fpp.addFaceToPerson(personId,image.getImageBinary());
            if (addFaceResult.contains(busyServer)){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                addFaceResult = fpp.addFaceToPerson(personId,image.getImageBinary());
            }
            if (addFaceResult.contains(busyServer)) {
            //do nothing. The image may be good to use. The server is just busy.
                index++;
            }
            else
            {
                if (addFaceResult.contains(IMAGE_FORMAT_ERROR)){
                    images.get(index).addCode(ErrorCodes.IMAGE_ERROR_UNSUPPORTED_FORMAT);
                }
                else if (addFaceResult.contains(IMAGE_DOWNLOAD_ERROR)){
                    images.get(index).addCode(ErrorCodes.IMAGE_ERROR_FAILED_TO_DOWNLOAD);
                }
                else if (addFaceResult.contains(IMAGE_FACE_NOT_DETECTED)){
                    images.get(index).addCode(ErrorCodes.IMAGE_ERROR_FACE_NOT_DETECTED);
                }
                else if (addFaceResult.contains(IMAGE_BAD_JSON_TAG)){
                    images.get(index).addCode(ErrorCodes.BAD_JSON_TAG);
                }
                else if (addFaceResult.contains(IMAGE_FILE_TOO_LARGE)){
                    images.get(index).addCode(ErrorCodes.IMAGE_ERROR_FILE_TOO_LARGE);
                }
                else if (addFaceResult.contains(IMAGE_ERROR)) {
                    images.get(index).addCode(ErrorCodes.IMAGE_ERROR_UNKNOWN);
                }
                else {
                    images.get(index).addCode(ErrorCodes.OK);
                    goodImages++;
                    goodIndex = index;
                }
            }
            index++;
        }
        int tries = 0;
        String sessionId = null;
            do {
                try {
                    if (!serverResponded) { //only need to call train once.
                        sessionId = fpp.trainPerson(personId); //if the server is busy this should execute again.
                        if (sessionID != null){
                            if(sessionId! = "FAILURE"){
                                serverResponded = true; //if we get here, then the server responded.
                            }
                        }
                    }
                    TimeUnit.SECONDS.sleep(2); //give the server a couple seconds to train.
                    if ( ((sessionId != null) && (sessionId != "FAILURE")) ){
                        result = httpRequests.infoGetSession(new PostParameters().setSessionId(sessionId));
                        goodTraining = (result.getString("status") == "SUCC"); //training was successful.
                    }
                } catch (FaceppParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tries++;
            } while ((!goodTraining) && (tries < 5));
            if (goodIndex != -1){
                JSONObject check = identifyPerson(personId,images.get(goodIndex));
            }
            FRServiceHandlerTrainResponse response2 =
                new FRServiceHandlerTrainResponse(serviceName, serverResponded, personId, false, images);
            if (check == null) {
                return response2;
            }
            try {
                double con = check.getDouble("confidence");
                if (con >= 80){
                    goodTraining = true;
                }
                else
                {
                    goodTraining = false;
                }
            }
            catch (JSONException e) {
             e.printStackTrace();
            }
            FRServiceHandlerTrainResponse response3 =
                    new FRServiceHandlerTrainResponse(serviceName, serverResponded, personId, goodTraining, images);
            return response3;
        //Need a new response to account for error codes added to the images.
    }

    /**
     * attempts to verify a person's identity using the given image.
     * @param personID: facepp id of person to be verified.
     * @param image: image to be used for verification.
     * returns FRServiceHanlderVerifyResponse object with appropriate fields.
     */
    public FRServiceHandlerVerifyResponse verify(String personID, ImageData image){
        String serviceName = "facepp";
        boolean serviceResponded;
        float confidence;
        final float cutoff;
        String FRPersonID = personID;
        JSONObject result = identifyPerson(personID, image);
        FRServiceHandlerVerifyResponse response = new FRServiceHandlerVerifyResponse(serviceName,false,0,80,personID);
        if (result == null) //the result is null if something went wrong with face detection in identify
        {
            return response;
        }
        if (personID == null) {
            return response;
        }
        if (image == null){
            return response;
        }
        if (result != null) {
            {
                try {
                    confidence = (float)result.getDouble("confidence");
                }
                catch (JSONException e){
                    e.printStackTrace();
                    return response;
                }
                serviceResponded = true;
                cutoff = 80;
                FRServiceHandlerVerifyResponse response2 =
                        new FRServiceHandlerVerifyResponse(serviceName,serviceResponded,confidence,cutoff,personID);
                return response2;
            }
        }
        return response;
    }

    public String getFRServiceName(){
        return "facepp";
    }

    public double getFRServiceCutoff(){
        return 80;
    }

    public static void main(String[] args) {
        String apiKey = "15bd4507cc5030eb330d4ab62ce5fd69";
        String apiSecret = "LI5HIlv6RsvY_Si2t0GwE9NQZPGbuHW6";
        FacePPCommunicator fpp = new FacePPCommunicator(apiKey, apiSecret);
        //fpp.removeGroup(fpp.groupName);
        fpp.createGroup();
        // fpp.removePersonWName("person_8");
        //   fpp.removePerson("person_8");
        String personId = fpp.createPersonInGroup("person_8");
        fpp.addFaceToPerson(personId,
                "https://www.whitehouse.gov/sites/whitehouse.gov/files/images/first-family/44_barack_obama%5B1%5D.jpg");

        fpp.addFaceToPerson(personId,
                "http://www.worldnewspolitics.com/wp-content/uploads/2016/02/140718-barack-obama-2115_86aea53294a878936633ec10495866b6.jpg");

        fpp.addFaceToPerson(personId,
                "http://i2.cdn.turner.com/cnnnext/dam/assets/150213095929-27-obama-0213-super-169.jpg");

        fpp.addFaceToPerson(personId,
                "http://a.abcnews.go.com/images/US/AP_obama8_ml_150618_16x9_992.jpg");

        fpp.addFaceToPerson(personId,
                "http://www.dailystormer.com/wp-content/uploads/2015/06/2014-10-12-obama-618x402.jpg");

        fpp.addFaceToPerson(personId,
                "http://media.vocativ.com/photos/2015/10/RTS2O4I-22195838534.jpg");

        fpp.addFaceToPerson(personId,
                "https://upload.wikimedia.org/wikipedia/commons/e/e9/Official_portrait_of_Barack_Obama.jpg");

        fpp.addFaceToPerson(personId,
                "http://cbsnews2.cbsistatic.com/hub/i/r/2015/10/09/f27caaea-86d1-41e2-bec2-97e89e5dba03/thumbnail/770x430/0a4b24d154ee526bb6811f1888e16600/presidentobamamain.jpg");
        fpp.trainPerson(personId);
        try{
            TimeUnit.SECONDS.sleep(5);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        fpp.identifyPerson(personId, "http://i.huffpost.com/gen/2518262/images/n-OBAMA-628x314.jpg");
        fpp.removePersonWName("person_8"); //removing the person removes them from facepp removes them from the group.
        // fpp.removePersonFromGroup(personId);
        fpp.removeGroup(fpp.groupName);
    }
}


