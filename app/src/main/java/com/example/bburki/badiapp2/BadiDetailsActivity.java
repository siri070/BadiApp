package com.example.bburki.badiapp2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.Writer;


public class BadiDetailsActivity extends AppCompatActivity {

    private static String TAG = "BadiInfo";
    private String badiId;
    private String name;
       private String ort;
       private String becken;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_details);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        badiId= intent.getStringExtra("badi");
        name = intent.getStringExtra("name");
        becken = intent.getStringExtra("becken");

        TextView text = (TextView) findViewById(R.id.badiinfos);

        text.setText(name);

        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "Bitte warten...");
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
       OnClick_WetterPrognose();
       OnClick_hinzufuegen();

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
    private void OnClick_hinzufuegen(){
        //Listener für den Wetterprognose-Button
        Button hinzufuegen = (Button) findViewById(R.id.favoritHinzufuegen);
        View.OnClickListener wpListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                 File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"favoriten");
                 if(!fileDir.exists()){
                     try {
                         fileDir.mkdir();
                     }
                     catch (Exception e ){
                         Log.v(TAG, e.toString());
                     }
                 }
                 File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"favoriten"+File.separator+"favotiten_data.csv");
                 if(!file.exists()){
                     try{
                         file.createNewFile();
                     }
                     catch (Exception e){
                         Log.v(TAG, e.toString());
                     }
                 }

                 if(file.exists()){
                     try{
                         FileWriter fileWriter = new FileWriter(file);
                         BufferedWriter bufferedWriter= new BufferedWriter(fileWriter);
                         bufferedWriter.write(badiId+";"+name+";"+ort+";"+becken);
                         bufferedWriter.close();
                     }
                     catch (Exception e){
                         Log.v(TAG, e.toString());
                     }
                    error("Das hinzufügen der Badi hat geklappt: :) ");
                 }


            }

        };
        hinzufuegen.setOnClickListener(wpListener);


    }
    private void export_FavoritenData() throws IOException{
        File folder = new File(Environment.getExternalStorageDirectory()+"/favoriten");
        boolean hatGeklappt= false;
         if (!folder.exists()){
             hatGeklappt = folder.mkdirs();
             final String filename = folder.toString()+File.separator+"favoriten_data.csv";

         }



    }
    private void error(String text ){

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Fehler");
        helpBuilder.setMessage(text);
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();

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
                    error("Ein Fehler beim Holen der Daten ist aufgetreten. Bitte stelle eine Internetverbindung her.");
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
                    error("Daten können nicht gelesen werden.");
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
