package uk.ac.aston.jonesja1.ersclient.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.jonesja1.ersclient.service.api.RegisterAPI;

import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_DEVICE_ID;
import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_FIREBASE_TOKEN;

public class FirebaseTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        reauthorise(getBaseContext());
    }

    public static void reauthorise(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String id = preferences.getString(ENROLLED_DEVICE_ID, null);
        String token = FirebaseInstanceId.getInstance().getToken();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ers-server-dev.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RegisterAPI registerAPI = retrofit.create(RegisterAPI.class);
        Response<String> response = null;
        try {
            response = registerAPI.reauthenticate(id, token).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENROLLED_FIREBASE_TOKEN, token);
        editor.commit();
    }
}
