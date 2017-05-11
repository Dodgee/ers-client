package uk.ac.aston.jonesja1.ersclient;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EnrolActivity extends AppCompatActivity {

    private static final String LOG_MARKER = "EnrolActivity";

    private EditText nameField;
    private EditText emailField;
    private EditText employeeNumberField;
    private Button enrolButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol);

        nameField = (EditText) findViewById(R.id.input_name);
        emailField = (EditText) findViewById(R.id.input_email);
        employeeNumberField = (EditText) findViewById(R.id.input_employee_number);
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

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String employeeNumber = employeeNumberField.getText().toString();

        if (validate(name, email, employeeNumber)) {
            onSignupFailed();
            return;
        }

        enrolButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(
                EnrolActivity.this,
                R.style.AppTheme
        );
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Attempting to Enrol...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        enrolButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Failed to Enrol. Please try again.", Toast.LENGTH_LONG).show();
        enrolButton.setEnabled(true);
    }

    public boolean validate(String name, String email, String employeeNumber) {
        boolean valid = true;

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

        return valid;
    }
}