package com.example.hp.jsonparsing;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "http://jsonplaceholder.typicode.com/users";

    ArrayList<HashMap<String, String>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.listview);

        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                   // JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray JA = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject c = JA.getJSONObject(i);


                        String name = c.getString("name");
                        String email = c.getString("email");


                        // Phone node is JSON Object
                        JSONObject address = c.getJSONObject("address");
                        String city = address.getString("city");

                        JSONObject geo = address.getJSONObject("geo");
                        String lat = geo.getString("lat");
                        String lng = geo.getString("lng");
                       /* String home = phone.getString("home");
                        String office = phone.getString("office"); */

                        // tmp hash map for single contact
                        HashMap<String, String> user = new HashMap<>();

                        // adding each child node to HashMap key => value
                    //    contact.put("id", id);
                        user.put("name", name);
                        user.put("email", email);
                        user.put("city",city);
                        user.put("lat",lat);
                        user.put("lng",lng);
                      //  contact.put("mobile", mobile);

                        // adding contact to contact list
                        userList.add(user);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            final ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, userList,
                    R.layout.list_item, new String[]{"name", "email","city","lat","lng"
                    }, new int[]{R.id.name,
                    R.id.email,R.id.city,R.id.lat,R.id.lng});

            lv.setAdapter(adapter);


        }



    }

}