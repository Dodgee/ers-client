package uk.ac.aston.jonesja1.ersclient.service.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.jonesja1.ersclient.service.api.UpdateLocationAPI;

import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_DEVICE_ID;

public class LocationUpdate extends AsyncTask<HashMap<String, String>, String, String> {

    private Location location;

    private Context context;

    private LocationUpdateCallback callback;

    public LocationUpdate(Location location, Context context, LocationUpdateCallback callback) {
        this.location = location;
        this.context = context;
        this.callback = callback;
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String id = preferences.getString(ENROLLED_DEVICE_ID, null);
        UpdateLocationAPI.LocationUpdateRequest request = new UpdateLocationAPI.LocationUpdateRequest();
        request.setId(id);
        request.setLongitude(location.getLongitude());
        request.setLatitude(location.getLatitude());
        UpdateLocationAPI locationAPI = retrofit.create(UpdateLocationAPI.class);
        Response<String> response = null;
        try {
            response = locationAPI.update(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            Log.w("LocationUpdate", "no response when sending updated location");
            return "400";
        } else {
            Log.i("LocationUpdate", "location updated");
        }
        return "" + response.code();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //if the server is not accepting location updates call the callback function
        if ("404".equals(s) || "405".equals(s)) {
            callback.onRejected();
        }
    }

    public interface LocationUpdateCallback {
        void onRejected();
    }
}
