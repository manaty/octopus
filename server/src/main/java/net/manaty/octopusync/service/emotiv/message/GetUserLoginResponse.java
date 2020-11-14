package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.emotiv.ISO8601OffsetDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.List;

public class GetUserLoginResponse extends BaseResponse<List<GetUserLoginResponse.User>> {

    private List<User> result;

    @Override
    public List<User> result() {
        return result;
    }

    public void setResult(List<User> result) {
        this.result = result;
    }

    public static class User {
        private String username;
        private String currentOSUId;
        private String currentOSUsername;
        private String loggedInOSUId;
        private String loggedInOSUsername;
        @JsonDeserialize(using = ISO8601OffsetDateTimeDeserializer.class)
        private LocalDateTime lastLoginTime;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCurrentOSUId() {
            return currentOSUId;
        }

        public void setCurrentOSUId(String currentOSUId) {
            this.currentOSUId = currentOSUId;
        }

        public String getCurrentOSUsername() {
            return currentOSUsername;
        }

        public void setCurrentOSUsername(String currentOSUsername) {
            this.currentOSUsername = currentOSUsername;
        }

        public String getLoggedInOSUId() {
            return loggedInOSUId;
        }

        public void setLoggedInOSUId(String loggedInOSUId) {
            this.loggedInOSUId = loggedInOSUId;
        }

        public String getLoggedInOSUsername() {
            return loggedInOSUsername;
        }

        public void setLoggedInOSUsername(String loggedInOSUsername) {
            this.loggedInOSUsername = loggedInOSUsername;
        }

        public LocalDateTime getLastLoginTime() {
            return lastLoginTime;
        }

        public void setLastLoginTime(LocalDateTime lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("username", username)
                    .add("currentOSUId", currentOSUId)
                    .add("currentOSUsername", currentOSUsername)
                    .add("loggedInOSUId", loggedInOSUId)
                    .add("loggedInOSUsername", loggedInOSUsername)
                    .add("lastLoginTime", lastLoginTime)
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
