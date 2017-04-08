package com.example.sanjaya.bookstore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    private final String DASHBOARD_SERVICE_URL = CommonConstant.BASE_URL+"dashboardService.php";
    private final String CART_SAVE_SERVICE_URL = CommonConstant.BASE_URL+"createCartService.php";
    private EditText etSearchKey;
    private String myJSON;
    JSONArray books = null;
    ArrayList<HashMap<String, String>> bookList;
    private HashSet<String> uniqueCart = new HashSet<>();
    private int customerId;

    ListView list;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard);
        SharedPreferences prefs = getSharedPreferences(CommonConstant.MY_PREFS_NAME, MODE_PRIVATE);
        customerId = prefs.getInt("customerId",0);
        list = (ListView) findViewById(R.id.listView);
        bookList = new ArrayList<HashMap<String,String>>();
        etSearchKey = (EditText) findViewById( R.id.searchKey);
        displayData(etSearchKey.getText().toString());

    }

    public void search(View arg0){
        list = (ListView) findViewById(R.id.listView);
        bookList = new ArrayList<HashMap<String,String>>();
        etSearchKey = (EditText) findViewById( R.id.searchKey);
        displayData(etSearchKey.getText().toString());
    }

    public void showBookDetail(View arg0){
        // EditText etBookTitle = (EditText) findViewById(arg0.getId());
        //Toast.makeText(DashboardActivity.this, arg0.getId()+"<>"+R.id.title, Toast.LENGTH_LONG).show();
    }

    public void addToCart(View arg0){
        CheckBox cb;
        ListView mainListView = (ListView) findViewById(R.id.listView);
        for (int x = 0; x<mainListView.getChildCount();x++){
            cb = (CheckBox)mainListView.getChildAt(x).findViewById(R.id.checkbox);
            if(cb.isChecked()){
                //check if already exists or not
                if(uniqueCart.add(cb.getText().toString())) {
                    //save to database
                    cartSave(cb.getText().toString(),customerId,1);
                }
            }
        }
        Toast.makeText(DashboardActivity.this, "added to cart!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater( ).inflate( R.menu.menu_dashboard, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId( );

        // noinspection SimplifiableIfStatement
        if ( id == R.id.action_profile ) {
            Intent i = new Intent( DashboardActivity.this, ProfileActivity.class );
            startActivity( i );
        }
        else if ( id == R.id.action_logout ) {
            logoutAction();
            Intent i = new Intent( DashboardActivity.this, MainActivity.class );
            startActivity( i );
        }else if ( id == R.id.action_cart){
           Intent i = new Intent( DashboardActivity.this, CartActivity.class );
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
                loading = ProgressDialog.show(DashboardActivity.this, "Loading ...",null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();
                etSearchKey.setText(searchKey);
                myJSON = result;
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
                try {
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
                            DashboardActivity.this, bookList, R.layout.table_view,
                            new String[]{"ISBN","title"},
                            new int[]{R.id.checkbox, R.id.title}
                    ){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                            // get filled view from SimpleAdapter
                            View itemView=super.getView(position, convertView, parent);
                            // find our button there
                            TextView tv = (TextView) itemView.findViewById(R.id.title);
                            DashboardActivity.makeTextViewHyperlink(tv);
                            final CheckBox cb = (CheckBox)itemView.findViewById(R.id.checkbox);
                            tv.setOnClickListener( new View.OnClickListener( ) {
                                @Override
                                public void onClick( View v ) {

                                    Intent intent = new Intent( DashboardActivity.this, BookDetailActivity.class );
                                    intent.putExtra( "ISBN", cb.getText().toString( ) );
                                    startActivity( intent );
                                }
                            } );
                            return itemView;
                        }
                    };

                    list.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        ServiceClass serviceClass= new ServiceClass();
        serviceClass.execute(searchKey);
    }

    private void cartSave(String ISBN, int customer_id, int quantity) {

        class ServiceClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ServiceHandler serviceHandler = new ServiceHandler();

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String, String>();
                data.put("ISBN", params[0]);
                data.put("customer_id", params[1]);
                data.put("quantity", params[2]);

                String result = serviceHandler.sendPostRequest(CART_SAVE_SERVICE_URL, data);

                return result;
            }
        }

        ServiceClass serviceClass = new ServiceClass();
        serviceClass.execute(ISBN, String.valueOf(customer_id), String.valueOf(quantity));
    }
    private void logoutAction(){
        SharedPreferences.Editor editor = getSharedPreferences(CommonConstant.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    // Sets a hyperlink style to the textview.
    public static void makeTextViewHyperlink( TextView tv ) {
        SpannableStringBuilder ssb = new SpannableStringBuilder( );
        ssb.append( tv.getText( ) );
        ssb.setSpan( new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv.setText( ssb, TextView.BufferType.SPANNABLE );
        tv.setLinkTextColor(Color.BLUE);
    }
}