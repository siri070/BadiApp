package com.example.bburki.badiapp2;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class wetterActivity extends AppCompatActivity {

    private  static String TAG="Wetterprognose";
    private String ort;
    private String badiId;
    private String name;
    private ProgressDialog mDialog;
    private String wetterArt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wetter);

        Intent intent = getIntent();
        ort = intent.getStringExtra("ort");
        badiId= intent.getStringExtra("badi");
        name = intent.getStringExtra("name");
        mDialog= ProgressDialog.show(this, "Lade Wetterprognose","Bitte warten...");
        getWetter("http://api.openweathermap.org/data/2.5/weather?APPID=5e76a7fcbd44a92d2b2b0b39064eab05&q=" +(String) ort );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView img = (ImageView) findViewById(R.id.wetterBild);

        }

    private void getWetter(String url){
               final ArrayAdapter temps= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        new AsyncTask<String,String,String>(){
            @Override protected String doInBackground(String[] wetter){
               String msq="";
               try{
                   URL url = new URL(wetter[0]);

                   HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                   int code = conn.getResponseCode();

                   msq = IOUtils.toString(conn.getInputStream());
                   Log.i(TAG, Integer.toString(code));

               }catch (Exception e){
                    Log.v(TAG, e.toString());
               }

                return msq;
            }
            public void onPostExecute(String result){
                try {
                    mDialog.dismiss();
                    List<String> wetter = parseWetterprognose(result);

                    ListView wetterprognose = (ListView) findViewById(R.id.wetter);
                    temps.addAll(wetter);

                    wetterprognose.setAdapter(temps);
                }catch (JSONException e){
                    Log.v(TAG, e.toString());
                }

            }
            private List parseWetterprognose(String jonString)throws JSONException{
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(jonString);
                JSONArray a = jsonObject.getJSONArray("weather");
                JSONObject object = a.getJSONObject(0);
                wetterArt= object.getString("main");
                ImageView img = (ImageView) findViewById(R.id.wetterBild);
                switch (wetterArt){
                    case "Clouds":
                        //Bild
                        img.setImageResource(R.drawable.clouds);
                        resultList.add("Bewölkt");
                        break;
                    case "Sun":
                        //Bild
                        img.setImageResource(R.drawable.sun);
                        resultList.add("Sonne");
                        break;
                    case "Rain":
                        //Bild
                        img.setImageResource(R.drawable.rain);
                        resultList.add("Regen");
                        break;
                    case "Snow":
                        //Bild
                        img.setImageResource(R.drawable.snow);
                        resultList.add("Schnee");
                        break;

                    case "Thunderstorm":
                        //Bild
                        img.setImageResource(R.drawable.thunderstorm);
                        resultList.add("Gewitter");
                        break;
                    default:
                        img.setImageResource(R.drawable.notfound);
                        resultList.add("Wetterart ist nicht definiert");
                        break;
                }

                JSONObject wetter = jsonObject.getJSONObject("main");
                Iterator keys = wetter.keys();

                double temp_k = wetter.getDouble("temp");
                double temp_c = temp_k- 273.15;
                resultList.add("Momentan"+ (float)temp_c+" °C");

                double max_k = wetter.getDouble("temp_max");
                double max_c = max_k-273.15;
                resultList.add("Max: "+(float) max_c+"°C");

                double min_k = wetter.getDouble("temp_min");
                double min_c= min_k-273.15;
                resultList.add("Min: " + (float)min_c +"°C");



                return resultList;
            }

        }.execute(url);


    }

}
