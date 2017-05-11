package uk.ac.aston.jonesja1.ersclient;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public class EnrolWithServer extends AsyncTask<HashMap<String, String>, String, String> {

    private EnrolWithServerCallback callback;

    public EnrolWithServer(EnrolWithServerCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ers-server-dev.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(params[0].get("name"));
        registerRequest.setEmailAddress(params[0].get("email"));
        registerRequest.setEmployeeId(params[0].get("employeeId"));
        registerRequest.setConnectionDetails(params[0].get("firebaseToken"));
        EnrolCall enrolCall = retrofit.create(EnrolCall.class);
        Response<RegisterRequest> response = null;
        try {
            response = enrolCall.enrol(registerRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response == null ? "400" : "" + response.code();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if ("202".equals(s) || "201".equals(s) || "200".equals(s)) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    public interface EnrolWithServerCallback {
        void onSuccess();
        void onFailure();
    }

    private interface EnrolCall {
        @PUT("register/")
        Call<RegisterRequest> enrol(@Body RegisterRequest registerRequest);
    }

    private class RegisterRequest {

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
