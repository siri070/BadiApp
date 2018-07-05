package com.example.bburki.badiapp2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img = (ImageView)findViewById(R.id.badilogo);
        img.setImageResource(R.drawable.badi);
        addBadiToList();
        //Listener für den Button
        OnClick_Favoriten();
    }

    private void addBadiToList(){
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout. simple_list_item_1 );
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        for (ArrayList<String> b : allBadis) {
            badiliste.add(b.get(5)+"-"+b.get(8));
        }
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
                String seleced = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, seleced, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", seleced);
                intent.putExtra("becken", allBadis.get(position).get(8));
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);

    }
    private void OnClick_Favoriten(){
        //Listener für den Mein-Favoriten Button
        Button favoriten = (Button) findViewById(R.id.favoriten);
        View.OnClickListener wpListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FavoritenActivity.class);
                startActivity(intent);
            }
        };
        favoriten.setOnClickListener(wpListener);

    }

}
