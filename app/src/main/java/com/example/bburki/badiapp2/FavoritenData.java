package com.example.bburki.badiapp2;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FavoritenData {
    private static ArrayList<ArrayList<String>> dataFromFile;

    private FavoritenData(Context c) {
      // String path=  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"favoriten"+File.separator+"favoriten_data.csv";
//hi
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
    public static ArrayList<ArrayList<String>> allBadis(Context c) {
        if (null == dataFromFile) {
            new FavoritenData(c);
        }
        return dataFromFile;
    }
}
