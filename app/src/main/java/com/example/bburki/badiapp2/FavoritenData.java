package com.example.bburki.badiapp2;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FavoritenData {
    private static ArrayList<ArrayList<String>> dataFromFile;

    private FavoritenData(Context c) {
        Scanner scanner = new Scanner(c.getResources().openRawResource(R.raw.favoriten_data));
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
