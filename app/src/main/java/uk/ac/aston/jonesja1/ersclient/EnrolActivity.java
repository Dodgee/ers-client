package uk.ac.aston.jonesja1.ersclient;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import uk.ac.aston.jonesja1.ersclient.service.async.EnrolWithServer;
import uk.ac.aston.jonesja1.ersclient.service.async.ValidateServerURL;

import static uk.ac.aston.jonesja1.ersclient.service.async.ValidateServerURL.ERS_SERVER_URL_KEY;


public class EnrolActivity extends AppCompatActivity {

    private static final String LOG_MARKER = "EnrolActivity";

    private EditText nameField;
    private EditText emailField;
    private EditText employeeNumberField;
    private EditText serverURLField;
    private Button enrolButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol);

        nameField = (EditText) findViewById(R.id.input_name);
        emailField = (EditText) findViewById(R.id.input_email);
        employeeNumberField = (EditText) findViewById(R.id.input_employee_number);
        serverURLField = (EditText) findViewById(R.id.input_server_url);
        enrolButton = (Button) findViewById(R.id.button_enrol);

        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrol();
            }
        });

    }

    public void enrol() {
        Log.d(LOG_MARKER, "Enrol Activated");

        if (!validateInputs()) {
            Toast.makeText(getBaseContext(), "Enrollment Fields are Invalid.", Toast.LENGTH_LONG).show();
            enrolButton.setEnabled(true);
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(
                EnrolActivity.this,
                R.style.AppTheme
        );
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Attempting to Enrol...");
        progressDialog.show();

        String serverURL = serverURLField.getText().toString();
        HashMap<String, String> validateParams = new HashMap();
        validateParams.put(ERS_SERVER_URL_KEY, serverURL);
        new ValidateServerURL(new ValidateServerURL.ValidateServerURLCallback() {
            @Override
            public void onSuccess() {
                onValidateSuccess();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(EnrolActivity.this, "Unable to verify server url. Please check url and try again.", Toast.LENGTH_LONG).show();
            }
        }, this).execute(validateParams);

    }

    public void onValidateSuccess() {
        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String employeeNumber = employeeNumberField.getText().toString();

        enrolButton.setEnabled(false);

        HashMap<String, String> params = new HashMap();
        params.put("name", name);
        params.put("email", email);
        params.put("employeeId", employeeNumber);
        params.put("firebaseToken", FirebaseInstanceId.getInstance().getToken());

        new EnrolWithServer(new EnrolWithServer.EnrolWithServerCallback() {
            @Override
            public void onSuccess() {
                FirebaseMessaging.getInstance().subscribeToTopic("ERSUpdate");
                onEnrolSuccess();
            }

            @Override
            public void onFailure() {
                enrolButton.setEnabled(true);
            }
        }, this).execute(params);
    }


    public void onEnrolSuccess() {
        enrolButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public boolean validateInputs() {
        boolean valid = true;

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String employeeNumber = employeeNumberField.getText().toString();
        String serverURL = serverURLField.getText().toString();

        if (name == null || name.isEmpty()) {
            nameField.setError("Please enter your name.");
            valid = false;
        } else {
            nameField.setError(null);
        }

        if (email == null || email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please enter a valid email address.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (employeeNumber == null) {
            employeeNumberField.setError("Please enter your employee number.");
        } else if (employeeNumber.length() != 6) {
            employeeNumberField.setError("Please check your employee number is correct.");
            valid = false;
        } else {
            employeeNumberField.setError(null);
        }

        if (serverURL == null || serverURL.isEmpty()) {
            serverURLField.setError("Please enter the ERS Server URL");
            valid = false;
        } else {
            serverURLField.setError(null);
        }

        return valid;
    }
}