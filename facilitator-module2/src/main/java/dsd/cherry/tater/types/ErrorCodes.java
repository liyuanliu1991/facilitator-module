package dsd.cherry.tater.types;

import dsd.cherry.tater.types.jax_serializers.StatusCodeSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Corresponds to 'appCode' in the Facilitator Interface Specification.
 * @author Andrew James Beach
 * @version 0.2
 * Created by James Beach on 5/1/2016.
 */
@JsonSerialize(using = StatusCodeSerializer.class, as = String.class)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public enum ErrorCodes {
    /* A placeholder error code for when one is absolutely needed but no error has occurred.
     * In general, this code should not be used; objects should be left null or lists empty.
     */
    OK(0) {
        @Override
        public String getMessage() {
            return "OK";
        }
    },
    IMAGE_ERROR_FILE_TOO_LARGE(1) {
        @Override
        public String getMessage() {
            return "The image file is too large.";
        }
    },
    IMAGE_ERROR_FACE_NOT_DETECTED(2) {
        @Override
        public String getMessage() {
            return "No face could be detected in the image.";
        }
    },
    IMAGE_ERROR_UNKNOWN(3) {
        @Override
        public String getMessage() {
            return "There is something unknown wrong with the image.";
        }
    },
    IMAGE_ERROR_UNSUPPORTED_FORMAT(4) {
        @Override
        public String getMessage() {
            return "The image is in an unsupported file format.";
        }
    },
    BAD_JSON_TAG(5) {
        @Override
        public String getMessage() { return "A JSON tag is missing or formatted incorrectly."; }
    },
    @Deprecated
    IMAGE_ERROR_FAILED_TO_DOWNLOAD(1302) {
        @Override
        public String getMessage() {
            return "IMAGE_ERROR_FAILED_TO_DOWNLOAD";
        }
    };

    private int value;

    public abstract String getMessage();

    public int getErrorCode() {
        return value;
    }

    ErrorCodes(int value) { this.value = value; }
}
