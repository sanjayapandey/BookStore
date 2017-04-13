package com.example.sanjaya.bookstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.util.HashSet;

public class ProfileActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    private final String PURCHASE_HISTORY_SERVICE_URL = CommonConstant.BASE_URL+"purchaseHistory.php";
    private final String PROFILE_SERVICE_URL = CommonConstant.BASE_URL+"profileService.php";
    ArrayList<HashMap<String, String>> bookList;
    private String myJSON;
    JSONArray books = null;
    ListView list;
    private int customerId;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile);
        //add user profile data
        SharedPreferences prefs = getSharedPreferences(CommonConstant.MY_PREFS_NAME, MODE_PRIVATE);
        customerId = prefs.getInt("customerId",0);
        String firstName = prefs.getString("firstName","");
        String middleName = prefs.getString("middleName","");
        String lastName = prefs.getString("lastName","");

        list = (ListView) findViewById(R.id.listView);
        bookList = new ArrayList<HashMap<String,String>>();
        //construct book list
        getPurchaseData(customerId);
        //set userData
        final TextView tvView1 = (TextView) findViewById( R.id.fName );
        tvView1.setText( firstName);
        final TextView tvView2 = (TextView) findViewById( R.id.mName );
        tvView2.setText( middleName);
        final TextView tvView3 = (TextView) findViewById( R.id.lName );
        tvView3.setText( lastName);
        final TextView tvView4 = (TextView) findViewById( R.id.userId );
        tvView4.setText( String.valueOf(customerId));

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate( R.menu.menu_profile, menu );
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
            Intent i = new Intent( ProfileActivity.this, DashboardActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_cart ) {
            Intent i = new Intent( ProfileActivity.this, CartActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_logout ) {
            logoutAction();
            Intent i = new Intent( ProfileActivity.this, MainActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    private void getPurchaseData(int customerId){

        class ServiceClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ServiceHandler serviceHandler = new ServiceHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Loading ...",null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();
                myJSON = result;
                showList();
            }
            @Override
            protected String doInBackground(String... params){

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("customer_id",params[0]);

                String result = serviceHandler.sendPostRequest(PURCHASE_HISTORY_SERVICE_URL,data);

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
            protected void showList(){
                double totalCartAmount = 0.0;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    books = jsonObj.getJSONArray("result");

                    for(int i=0;i<books.length();i++){
                        JSONObject c = books.getJSONObject(i);
                        String ISBN = c.getString("ISBN");
                        String title = c.getString("title");
                        String quantity = c.getString("quantity");
                        String price = "$"+new DecimalFormat("##.##").format(Integer.valueOf(quantity)*Double.valueOf(c.getString("price")));
                        totalCartAmount = totalCartAmount +Integer.valueOf(quantity)*Double.valueOf(c.getString("price")) ;
                        HashMap<String,String> book = new HashMap<String,String>();

                        book.put("ISBN",ISBN);
                        book.put("title",title);
                        book.put("quantity",quantity);
                        book.put("price",price);
                        bookList.add(book);
                    }

                    ListAdapter adapter = new SimpleAdapter(
                            ProfileActivity.this, bookList, R.layout.table_view_for_profile,
                            new String[]{"title","quantity", "price"},
                            new int[]{R.id.title,R.id.quantity, R.id.price}
                    );

                    list.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView tvTotal = (TextView)findViewById(R.id.totalCost);
                tvTotal.setText("$"+new DecimalFormat("##.##").format(totalCartAmount));
            }

        }

        ServiceClass serviceClass= new ServiceClass();
        serviceClass.execute(String.valueOf(customerId));
    }
    private void logoutAction(){
        SharedPreferences.Editor editor = getSharedPreferences(CommonConstant.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}