package com.example.bburki.badiapp2;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img = (ImageView)findViewById(R.id.badilogo);
        img.setImageResource(R.drawable.badi);
        addBadiToList();
    }

    private void addBadiToList(){
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout. simple_list_item_1 );
        badiliste.add( getString(R.string.badaarberg) );
        badiliste.add( getString(R.string.badadelboden)  );
        badiliste.add( getString(R.string.badbern)  );
        badis.setAdapter(badiliste);

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
                String seleced = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, seleced, Toast.LENGTH_SHORT).show();
                if(seleced.equals(getString(R.string.badaarberg) )){
                    intent.putExtra("badi","71");
                }
                if(seleced.equals(getString(R.string.badadelboden) )){
                    intent.putExtra("badi","27");
                }
                if(seleced.equals(getString(R.string.badbern) )){
                    intent.putExtra("badi","6");
                }
                intent.putExtra("name", seleced);
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);

    }

}
