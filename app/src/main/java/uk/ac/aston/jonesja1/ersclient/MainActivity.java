package uk.ac.aston.jonesja1.ersclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();


        Button enrolButton = (Button) findViewById(R.id.button_enrol);
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EnrolActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
}
