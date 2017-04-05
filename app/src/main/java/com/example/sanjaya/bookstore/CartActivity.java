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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CartActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    private final String DASHBOARD_SERVICE_URL = CommonConstant.BASE_URL+"dashboardService.php";
    ArrayList<HashMap<String, String>> bookList;
    private ArrayList<HashMap<String, Integer>> cartList = new ArrayList<HashMap<String,Integer>>();
    private HashSet<String> uniqueCart = new HashSet<>();

    ListView list;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cart);
        list = (ListView) findViewById(R.id.listView);
        bookList = new ArrayList<HashMap<String,String>>();
    }

    public void addToCart(View arg0){
        CheckBox cb;
        ListView mainListView = (ListView) findViewById(R.id.listView);
        for (int x = 0; x<mainListView.getChildCount();x++){
            cb = (CheckBox)mainListView.getChildAt(x).findViewById(R.id.checkbox);
            if(cb.isChecked()){
                    HashMap<String, Integer> cart = new HashMap<String, Integer>();
                    cart.put(cb.getText().toString(), 1);
                    cartList.add(cart);
            }
        }
        Toast.makeText(CartActivity.this, "Cart Updated!", Toast.LENGTH_LONG).show();
    }

    public void goToDashboard(View arg0){
        Intent intent = new Intent(CartActivity.this,DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate( R.menu.menu_cart, menu );
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
            Intent i = new Intent( CartActivity.this, DashboardActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_profile ) {
            Intent i = new Intent( CartActivity.this, DashboardActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_logout ) {
            Intent i = new Intent( CartActivity.this, MainActivity.class );
            startActivity( i );
        }
        return super.onOptionsItemSelected( item );
    }
    private void displayData(final String searchKey){

        class ServiceClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ServiceHandler serviceHandler = new ServiceHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CartActivity.this, "Loading ...",null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();
                showList();
            }
            @Override
            protected String doInBackground(String... params){

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("searchTitle",params[0]);

                String result = serviceHandler.sendPostRequest(DASHBOARD_SERVICE_URL,data);

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
                /*try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    books = jsonObj.getJSONArray("result");

                    for(int i=0;i<books.length();i++){
                        JSONObject c = books.getJSONObject(i);
                        String ISBN = c.getString("ISBN");
                        String title = c.getString("title");

                        HashMap<String,String> book = new HashMap<String,String>();

                        book.put("ISBN",ISBN);
                        book.put("title",title);
                        bookList.add(book);
                    }

                    ListAdapter adapter = new SimpleAdapter(
                            CartActivity.this, bookList, R.layout.table_view,
                            new String[]{"ISBN","title"},
                            new int[]{R.id.checkbox, R.id.title}
                    );

                    list.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            }

        }

        ServiceClass serviceClass= new ServiceClass();
        serviceClass.execute(searchKey);
    }
}