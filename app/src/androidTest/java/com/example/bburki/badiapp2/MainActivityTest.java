package com.example.bburki.badiapp2;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    ArrayList<ArrayList<String>>dataFromFile;
    @Before
    public void vorbereitung(){
        String TAG = "Test";
        int badiId= 7;
        String ort="Aarberg";
        String name="Aarberg";
        String becken="Schwimmbecken";
        File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"test");
        //èberprüfung ob es den Ordner schon gibt, falls nicht wird er erstellt
        if(!fileDir.exists()){
            try {
                fileDir.mkdir();
            }
            catch (Exception e ){
                Log.v(TAG, e.toString());
            }
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"test"+File.separator+"favotiten_data.csv");
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

            }

        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(Environment.getExternalStorageDirectory()+File.separator+"favoriten"+File.separator+"favotiten_data.csv"));
        } catch (FileNotFoundException e) {
            Log.i("ohhneii",e.toString());
        }
        scanner.useDelimiter(";");
        dataFromFile = new ArrayList<ArrayList<String>>();
        while (scanner.hasNext()){
            String dataInRow = scanner.nextLine();
            String[]dataInRowArray = dataInRow.split(";");
            ArrayList<String> rowDataFromFile = new ArrayList<String>(Arrays.asList(dataInRowArray));
            dataFromFile.add(rowDataFromFile);
        }
        scanner.close();

    }
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void listViewcontainsAarberg(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        MainActivity mainActivity= mActivityRule.getActivity();
        ListView listView= (ListView) mainActivity.findViewById(R.id.badiliste);

        assertEquals("Aarberg-Schwimmbecken", listView.getItemAtPosition(0));


    }

    public void listViewCountsFour(){
         boolean hatVier = false;
        MainActivity mainActivity= mActivityRule.getActivity();
         ListView listView= (ListView) mainActivity.findViewById(R.id.badiliste);
         if( listView.getCount()>=4){
             hatVier=true;

         }
        assertEquals(true, hatVier);
    }
    public void favortitenSetzen(){

        for (ArrayList<String> b : dataFromFile) {
            favoritenListe.add(b.get(2)+"-"+b.get(3));
            assertEquals("Aarberg", b.get());
        }
        assertEquals("Aarberg", );
    }
}
