package uk.ac.aston.jonesja1.ersclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import uk.ac.aston.jonesja1.ersclient.service.FirebaseTokenService;

import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_DEVICE_ID;
import static uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer.ENROLLED_FIREBASE_TOKEN;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("ERSUpdate");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String id = sharedPreferences.getString(ENROLLED_DEVICE_ID, null);

        Button enrolButton = (Button) findViewById(R.id.button_enrol);
        Button reauthButton = (Button) findViewById(R.id.button_reauth);
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
            reauthButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateCurrentStatus(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String firebaseToken = sharedPreferences.getString(ENROLLED_FIREBASE_TOKEN, null);

        if (!isTokenValid(firebaseToken)) {
            Button reauthButton = (Button) findViewById(R.id.button_reauth);
            reauthButton.setVisibility(View.VISIBLE);
            reauthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            FirebaseTokenService.reauthorise(getBaseContext());
                            return null;
                        }
                    };
                    task.execute();
                }
            });
        }
    }

    private boolean isTokenValid(String savedToken) {
        String currentToken = FirebaseInstanceId.getInstance().getToken();
        return currentToken.equals(savedToken);
    }

    private void updateCurrentStatus(Intent intent) {
        Bundle extras = intent.getExtras();
        String message = extras.getString("MESSAGE");
        new AlertDialog.Builder(this)
                .setTitle("Capgemini Incident Update")
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
