package dsd.cherry.tater.types;

/**
 * Corresponds to 'appCode' in the Facilitator Interface Specification.
 * Created by James Beach on 5/1/2016.
 */
public enum ImageCode {
    IMAGE_OK(1300) {
        @Override
        public String getMessage() {
            return "IMAGE_OK";
        }
        @Override
        public int getHTTPCode() {
            return 200;
        }
    },
    IMAGE_ERROR_UNSUPPORTED_FORMAT(1301) {
        @Override
        public String getMessage() {
            return "IMAGE_ERROR_UNSUPPORTED_FORMAT";
        }
        @Override
        public int getHTTPCode() {
            return 431;
        }
    },
    IMAGE_ERROR_FAILED_TO_DOWNLOAD(1302) {
        @Override
        public String getMessage() {
            return "IMAGE_ERROR_FAILED_TO_DOWNLOAD";
        }
        @Override
        public int getHTTPCode() {
            return 432;
        }
    },
    IMAGE_ERROR_FILE_TOO_LARGE(1303) {
        @Override
        public String getMessage() {
            return "IMAGE_ERROR_FILE_TOO_LARGE";
        }
        @Override
        public int getHTTPCode() {
            return 433;
        }
    },
    IMAGE_ERROR(1304) {
        @Override
        public String getMessage() {
            return "IMAGE_ERROR";
        }
        @Override
        public int getHTTPCode() {
            return 434;
        }
    };

    private int value;

    public abstract String getMessage();

    public abstract int getHTTPCode();

    ImageCode(int value) { this.value = value; }
}
