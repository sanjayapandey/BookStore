package com.example.sanjaya.bookstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private final String LOGIN_URL = CommonConstant.BASE_URL+"loginService.php";
    private EditText etUsername;
    private EditText etPassword;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        etUsername = (EditText) findViewById( R.id.name );
        etPassword = (EditText) findViewById(R.id.password);
    }
    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0){
        // Initialize  AsyncLogin() class with email and password
        loginProcess(etUsername.getText().toString(),etPassword.getText().toString());

    }
    // Triggers when create account Button clicked
    public void signUp(View arg0){
        Intent intent = new Intent(MainActivity.this,SignupActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater( ).inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId( );

        // noinspection SimplifiableIfStatement
        if ( id == R.id.action_signup ) {
            Intent i = new Intent( MainActivity.this, SignupActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }

    private void loginProcess(String username, String password){

        class ServiceClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ServiceHandler serviceHandler = new ServiceHandler();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();
                if(result.equalsIgnoreCase("success"))
                {
                     /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                    Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                    startActivity(intent);
                }else if (result.equalsIgnoreCase("incorrect")){
                    // If username and password does not match display a error message
                    Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    etUsername.setText("");
                    etPassword.setText("");
                } else if (result.equalsIgnoreCase("error")) {
                    Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                    etUsername.setText("");
                    etPassword.setText("");
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("username",params[0]);
                data.put("password",params[1]);

                String result = serviceHandler.sendPostRequest(LOGIN_URL,data);

                return  result;
            }
        }

        ServiceClass serviceClass= new ServiceClass();
        serviceClass.execute(username,password);
    }
}