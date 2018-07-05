package com.example.bburki.badiapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BadiDetailsActivity extends AppCompatActivity {

    private static String TAG = "BadiInfo";
    private String badiId;
    private String name;
       private String ort;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_details);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        badiId= intent.getStringExtra("badi");
        name = intent.getStringExtra("name");

        TextView text = (TextView) findViewById(R.id.badiinfos);

        text.setText(name);

        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "Bitte warten...");
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
       OnClick_WetterPrognose();
    }
    private void OnClick_WetterPrognose(){
        //Listener für den Wetterprognose-Button
        Button wetterprognose = (Button) findViewById(R.id.wetterprognose);
        View.OnClickListener wpListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), wetterActivity.class);
                intent.putExtra("ort", ort);
                intent.putExtra("badi",badiId);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        };
        wetterprognose.setOnClickListener(wpListener);

    }

    private void getBadiTemp(String url) {
        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        new AsyncTask<String,String,String>() {
            @Override
            protected String doInBackground(String[] badi) { //In der variable msg soll die Antwort der Seite wiewarm
                String msq= "";
                try {
                    URL url = new URL(badi[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // webseite antwort lesen
                    int code = conn.getResponseCode();

                    msq = IOUtils.toString(conn.getInputStream());

                    Log.i(TAG, Integer.toString(code));

                }catch(Exception e) {
                    Log.v(TAG, e.toString());
                }
                return msq;
            }
            @Override
            public void onPostExecute(String result) {
                try {
                    mDialog.dismiss();
                    List<String> badiInfos = parseBadiTemp(result);

                    ListView badidetails = (ListView) findViewById(R.id.badidetails);


                    temps.addAll(badiInfos);

                    badidetails.setAdapter(temps);

                }catch (JSONException e) {
                    Log.v(TAG, e.toString());
                }
            }
            private List parseBadiTemp(String jonString)throws JSONException {
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObj = new JSONObject(jonString);
                ort = jsonObj.getString("ort");
                JSONArray a = jsonObj.getJSONArray("wetter");
                JSONObject object = a.getJSONObject(0);
                resultList.add("Lufttemperatur:" + object.getString("wetter_temp")+"C");

                JSONObject becken = jsonObj.getJSONObject("becken");
                Iterator keys = becken.keys();
                while(keys.hasNext()) {
                    String key = (String) keys.next();

                    JSONObject subObj = becken.getJSONObject(key); //Wenn man die Antwort der Webseite anschaut, steckt

                    String name = subObj.getString("beckenname");

                    String temp = subObj.getString("temp");

                    resultList.add(name + ":" + temp + "°C");

                }
                return resultList;
            }
        }.execute(url);
    }



}
