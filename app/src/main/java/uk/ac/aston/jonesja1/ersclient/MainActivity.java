package uk.ac.aston.jonesja1.ersclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_DEVICE_ID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("ERSUpdate");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String id = sharedPreferences.getString(ENROLLED_DEVICE_ID, null);

        Button enrolButton = (Button) findViewById(R.id.button_enrol);
        TextView enrolledID = (TextView) findViewById(R.id.text_enrolled_id);
        if (id == null) {
            enrolButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EnrolActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
        } else {
            enrolButton.setClickable(false);
            enrolButton.setVisibility(View.INVISIBLE);
            enrolledID.setText("Enrolled ID: " + id);
            enrolledID.setVisibility(View.VISIBLE);
        }
    }
}
