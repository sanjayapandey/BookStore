package com.example.sanjaya.bookstore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private final String LOGIN_URL = CommonConstant.BASE_URL+"createUserService.php";
    private EditText etFirstName;
    private EditText etMiddleName;
    private EditText etLastName;
    private EditText etUserName;
    private EditText etPassword;
    private EditText etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etFirstName = (EditText) findViewById(R.id.firstName);
        etMiddleName = (EditText) findViewById(R.id.middleName);
        etLastName = (EditText) findViewById(R.id.lastName);
        etUserName = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        etPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
    }

    // Triggers when LOGIN Button clicked
    public void signUp(View arg0) {
        // Initialize  AsyncLogin() class with email and password
        signupProcess(etFirstName.getText().toString(), etMiddleName.getText().toString(), etLastName.getText().toString(),etUserName.getText().toString(),etPassword.getText().toString(),etPhoneNumber.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent i = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void signupProcess(String firstName, String middleName, String lastName, String userName, String password, String phoneNumber) {

        class ServiceClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ServiceHandler serviceHandler = new ServiceHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignupActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();
                if (result.equalsIgnoreCase("success")) {
                     /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    // If username and password does not match display a error message
                    Toast.makeText(SignupActivity.this, "Signup Success!", Toast.LENGTH_LONG).show();

                } else {
                    // If username and password does not match display a error message
                    Toast.makeText(SignupActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String, String>();
                data.put("firstName", params[0]);
                data.put("middleName", params[1]);
                data.put("lastName", params[2]);
                data.put("userName", params[3]);
                data.put("password", params[4]);
                data.put("phoneNumber", params[5]);

                String result = serviceHandler.sendPostRequest(LOGIN_URL, data);

                return result;
            }
        }

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.execute(firstName, middleName, lastName, userName,password,phoneNumber);
    }
}