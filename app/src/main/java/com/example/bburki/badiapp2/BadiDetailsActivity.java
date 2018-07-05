package com.example.bburki.badiapp2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private int MY_PERMISSON_REQUEST_WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_details);
        Intent intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        badiId= intent.getStringExtra("badi");
        name = intent.getStringExtra("name");
        becken = intent.getStringExtra("becken");
        ort= intent.getStringExtra("ort");
        TextView text = (TextView) findViewById(R.id.badiinfos);

        text.setText(name);

        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "Bitte warten...");
        //Daten holen
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
        //Listener für die Buttons
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
        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    }
                    else{
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSON_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }
        //Listener für den Button zum hinzufügen eines Favoriten
        Button hinzufuegen = (Button) findViewById(R.id.favoritHinzufuegen);
        View.OnClickListener wpListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"favoriten");
                 //èberprüfung ob es den Ordner schon gibt, falls nicht wird er erstellt
                 if(!fileDir.exists()){
                     try {
                         fileDir.mkdir();
                     }
                     catch (Exception e ){
                         Log.v(TAG, e.toString());
                     }
                 }
                 File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"favoriten"+File.separator+"favotiten_data.csv");
                 //Überprüfung ob das File schon existiert, falls nicht wird es erstellt
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
                         //Daten in das File schreiben
                         bufferedWriter.write(badiId+";"+name+";"+ort+";"+becken);
                         bufferedWriter.close();
                     }
                     catch (Exception e){
                         Log.v(TAG, e.toString());
                         error("Es konnte kein neuer Favorit gesetzt werden");
                     }
                    error("Das hinzufügen der Badi hat geklappt: :) ");
                 }


            }

        };
        hinzufuegen.setOnClickListener(wpListener);


    }

    //Alert für das ausgeben von Fehlern
    private void error(String text ){

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Meldung");
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

    //Holen der Daten
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
                    //Daten vorbereiten
                    List<String> badiInfos = parseBadiTemp(result);

                    ListView badidetails = (ListView) findViewById(R.id.badidetails);


                    temps.addAll(badiInfos);

                    badidetails.setAdapter(temps);

                }catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    //Errorhandling
                    error("Daten können nicht gelesen werden.");
                }
            }
            private List parseBadiTemp(String jonString)throws JSONException {

                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObj = new JSONObject(jonString);
                //Auslesen der Daten aus dem JSONObject, für das Wetter musste zuerst noch ein JSONArray gemacht werden und aus dem ein JSONObjekt
                JSONArray a = jsonObj.getJSONArray("wetter");
                JSONObject object = a.getJSONObject(0);
                resultList.add("Lufttemperatur:" + object.getString("wetter_temp")+"C");

                JSONObject becken = jsonObj.getJSONObject("becken");
                Iterator keys = becken.keys();
                while(keys.hasNext()) {
                    //Hinzufügen der Daten zu der Liste
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
