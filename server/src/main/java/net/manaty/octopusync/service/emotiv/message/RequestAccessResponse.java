package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

public class RequestAccessResponse extends BaseResponse<RequestAccessResponse.RequestAccessResult> {

    private RequestAccessResult result;

    @Override
    public RequestAccessResult result() {
        return result;
    }

    public void setResult(RequestAccessResult result) {
        this.result = result;
    }

    public static class RequestAccessResult {
        private boolean accessGranted;
        private String message;

        public boolean isAccessGranted() {
            return accessGranted;
        }

        public void setAccessGranted(boolean accessGranted) {
            this.accessGranted = accessGranted;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("accessGranted", accessGranted)
                    .add("message", message)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", result)
                .add("jsonrpc", jsonrpc)
                .add("id", id)
                .add("error", error)
                .toString();
    }
}
