package com.example.bburki.badiapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
public class FavoritenActivity extends AppCompatActivity {
    ArrayAdapter favoritenListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoriten);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addBadiToList();
    }

    private void addBadiToList(){
        ListView badis = (ListView) findViewById(R.id.favoritenListe);
        favoritenListe = new ArrayAdapter<String>(this, android.R.layout. simple_list_item_1 );
        final ArrayList<ArrayList<String>> allBadis = FavoritenData.allBadis(getApplicationContext());
        for (ArrayList<String> b : allBadis) {
            favoritenListe.add(b.get(2)+"-"+b.get(3));
        }
        badis.setAdapter(favoritenListe);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
                String seleced = parent.getItemAtPosition(position).toString();
                Toast.makeText(FavoritenActivity.this, seleced, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", seleced);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);

    }
}


