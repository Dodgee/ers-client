package uk.ac.aston.jonesja1.ersclient.service.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.jonesja1.ersclient.service.api.RegisterAPI;

public class EnrolWithServer extends AsyncTask<HashMap<String, String>, String, String> {

    public static final String ENROLLED_DEVICE_ID = "ENROLLED_DEVICE_ID";

    private EnrolWithServerCallback callback;

    private Context context;

    public EnrolWithServer(EnrolWithServerCallback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ers-server-dev.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI.RegisterRequest registerRequest = new RegisterAPI.RegisterRequest();
        registerRequest.setName(params[0].get("name"));
        registerRequest.setEmailAddress(params[0].get("email"));
        registerRequest.setEmployeeId(params[0].get("employeeId"));
        registerRequest.setConnectionDetails(params[0].get("firebaseToken"));
        RegisterAPI registerAPI = retrofit.create(RegisterAPI.class);
        Response<String> response = null;
        try {
            response = registerAPI.enrol(registerRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            return "400";
        }
        saveID(response, context);
        return "" + response.code();
    }

    private void saveID(Response<String> response, Context context) {
        String id = response.body();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENROLLED_DEVICE_ID, id);
        editor.commit();
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

}
