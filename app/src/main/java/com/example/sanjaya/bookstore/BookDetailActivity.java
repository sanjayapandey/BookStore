package com.example.sanjaya.bookstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BookDetailActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    private final String BOOK_SERVICE_URL = CommonConstant.BASE_URL+"bookService.php";
    ArrayList<HashMap<String, String>> bookList;
    private String myJSON;
    JSONArray books = null;
    ListView list;
    private int customerId;
    private String ISBN;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_book_detail);
        Intent intent = getIntent( );
        ISBN = intent.getStringExtra( "ISBN" );

        //add user profile data
        SharedPreferences prefs = getSharedPreferences(CommonConstant.MY_PREFS_NAME, MODE_PRIVATE);
        customerId = prefs.getInt("customerId",0);

        //construct book list
        getBookInformation(ISBN);

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate( R.menu.menu_book_detail, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId( );

        // noinspection SimplifiableIfStatement
        if ( id == R.id.action_dashboard ) {
            Intent i = new Intent( BookDetailActivity.this, DashboardActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_cart ) {
            Intent i = new Intent( BookDetailActivity.this, CartActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_profile ) {
            Intent i = new Intent( BookDetailActivity.this, ProfileActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_logout ) {
            logoutAction();
            Intent i = new Intent( BookDetailActivity.this, MainActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    private void getBookInformation(String ISBN){

        class ServiceClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ServiceHandler serviceHandler = new ServiceHandler();

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                myJSON = result;
                showData();
            }
            @Override
            protected String doInBackground(String... params){

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("ISBN",params[0]);

                String result = serviceHandler.sendPostRequest(BOOK_SERVICE_URL,data);

                String value= "";
                try{
                    InputStream inputStream = new ByteArrayInputStream(result.getBytes());
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    value = sb.toString();
                }catch (Exception e) {
                    // Oops
                }
                return  value;
            }
            protected void showData(){
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    String ISBN = jsonObj.getString("ISBN");
                    String title = jsonObj.getString("title");
                    String price = jsonObj.getString("price");

                    final TextView tvView1 = (TextView) findViewById( R.id.ISBN );
                    tvView1.setText( ISBN);
                    final TextView tvView2 = (TextView) findViewById( R.id.title );
                    tvView2.setText( title);
                    final TextView tvView3 = (TextView) findViewById( R.id.price );
                    tvView3.setText( price);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        ServiceClass serviceClass= new ServiceClass();
        serviceClass.execute(ISBN);
    }
    private void logoutAction(){
        SharedPreferences.Editor editor = getSharedPreferences(CommonConstant.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}