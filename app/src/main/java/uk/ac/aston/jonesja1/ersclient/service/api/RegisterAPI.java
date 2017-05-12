package uk.ac.aston.jonesja1.ersclient.service.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RegisterAPI {

    @PUT("register/")
    Call<String> enrol(@Body RegisterRequest registerRequest);

    @POST("register/reauthenticate/{id}")
    Call<String> reauthenticate(@Path("id") String id, @Body String connectionDetails);

    class RegisterRequest {

        private String employeeId;

        private String name;

        private String emailAddress;

        private String connectionDetails;

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getConnectionDetails() {
            return connectionDetails;
        }

        public void setConnectionDetails(String connectionDetails) {
            this.connectionDetails = connectionDetails;
        }
    }

}
