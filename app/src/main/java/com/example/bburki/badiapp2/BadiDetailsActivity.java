package com.example.bburki.badiapp2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
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

import java.net.URLConnection;
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
    private int MY_PERMISSON_REQUEST_ACCESS_NETWORK_STATE;

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
        //Permisson holen
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
                    error("Die Badi wurde als Favorit gesetzt. :) ");
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
        //Den ArrayAdapter wollen wir später verwenden um die Temperaturen zu speichern
        //angezeigt sollen sie im Format der simple_list_item_1 werden (einem Standard Android Element)
        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)){

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSON_REQUEST_ACCESS_NETWORK_STATE);
            }
        }

        //Android verlangt, dass die Datenverarbeitung von den GUI Prozessen getrennt wird
        //Darum starten wir hier einen asynchronen Task (quasi einen Hintergrundprozess).
        new AsyncTask<String,String,String>() {
            //Der AsyncTask verlangt die implementation der Methode doInBackground.
            //Nachdem doInBackground ausgeführt wurde, startet automatisch die Methode onPostExecute
            //mit den Daten die man in der Metohde doInBackground mit return zurückgegeben hat (hier msg).
            @Override
            protected String doInBackground(String[] badi) {
                //In der variable msg soll die Antwort der Seite wiewarm.ch gespeichert werden
                String msq= "";
                try {

                    URL url = new URL(badi[0]);

                    //Hier bauen wir die Verbindung auf:
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    //Lesen des Antwortcodes der Webseite:
                    int code = conn.getResponseCode();
                    //Hier lesen wir die Nachricht der Webseite wiewarm.ch für Badi XY:
                    msq = IOUtils.toString(conn.getInputStream());
                    //und Loggen den Statuscode in der Konsole:
                    Log.i(TAG, Integer.toString(code));


                }catch(Exception e) {
                    Log.v(TAG, e.toString());

                }
                return msq;
            }
            @Override
            public void onPostExecute(String result) {
                //In result werden zurückgelieferten Daten der Methode doInBackground (return msg;) übergeben.
                // Hier ist also unser Resultat der Seite z.B. http://www.wiewarm.ch/api/v1/bad.json/55
                // In einem Browser IE, Chrome usw. sieht man schön das Resulat als JSON formatiert.
                // JSON Daten können wir aber nicht direkt ausgeben, also müssen wir sie umformatieren.
                try {
                    //Nun können wir den Lade Dialog wieder ausblenden (die Daten sind ja gelesen)
                    mDialog.dismiss();
                    //Daten vorbereiten
                    List<String> badiInfos = parseBadiTemp(result);
                    //Jetzt müssen wir nur noch alle Elemente der Liste badidetails hinzufügen.
                    // Dazu holen wir die ListView badidetails vom GUI
                    ListView badidetails = (ListView) findViewById(R.id.badidetails);

                    //und befüllen unser ArrayAdapter den wir am Anfang definiert haben (braucht es zum befüllen eines ListViews)
                    temps.addAll(badiInfos);

                    badidetails.setAdapter(temps);

                }catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    //Errorhandling
                    if(result == "" || result== null) {
                        error("Ein Fehler beim Holen der Daten ist aufgetreten. Bitte stelle eine Internetverbindung her.");
                    }
                    else {
                        error("Daten können nicht gelesen werden.");
                    }
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
