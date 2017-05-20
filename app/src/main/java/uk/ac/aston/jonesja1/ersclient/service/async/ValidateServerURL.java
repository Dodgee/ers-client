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

public class ValidateServerURL extends AsyncTask<HashMap<String, String>, String, String> {

    public static final String ERS_SERVER_URL_KEY = "ERSServerURL";

    private ValidateServerURLCallback callback;

    private Context context;

    public ValidateServerURL(ValidateServerURLCallback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        String url = params[0].get(ERS_SERVER_URL_KEY);
        if (url != null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            RegisterAPI registerAPI = retrofit.create(RegisterAPI.class);
            Response<String> response = null;
            try {
                response = registerAPI.validateServerURL().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null || response.code() != 200) {
                return null;
            }
            return url;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        super.onPostExecute(url);
        if (url == null) {
            callback.onFailure();
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(ERS_SERVER_URL_KEY, url);
            editor.commit();
            callback.onSuccess();
        }
    }

    public interface ValidateServerURLCallback {
        void onSuccess();
        void onFailure();
    }

}
