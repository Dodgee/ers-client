package uk.ac.aston.jonesja1.ersclient.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.jonesja1.ersclient.service.api.RegisterAPI;

import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_DEVICE_ID;

public class FirebaseTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String id = preferences.getString(ENROLLED_DEVICE_ID, null);
        if (id != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ers-server-dev.herokuapp.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RegisterAPI registerAPI = retrofit.create(RegisterAPI.class);
            Response<String> response = null;
            try {
                response = registerAPI.reauthenticate(id).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
