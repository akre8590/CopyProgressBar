package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

public class MyTaskEncrypt extends AsyncTask<Void, Void, Void> {

    private ProgressDialog progress;
    private SQLiteDatabase sqLiteDatabase;
    public static String DB_NAME = "";
    public static String DB_PATH = "";
    Context context;

    public MyTaskEncrypt(ProgressDialog progress, Context context){
        this.progress = progress;
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        createDataBase();
        try {
            encryptDataBase("123456");
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public void createDataBase(){
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/storage/emulated/0/prueba.db3","", null);
        sqLiteDatabase.close();
    }

    public void encryptDataBase(String passphrase) throws IOException{
        File originalFile = context.getDatabasePath(DB_NAME);

        File root = new File("/storage/emulated/0/prueba.db3");

        SQLiteDatabase existing_db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, "", null, SQLiteDatabase.OPEN_READWRITE);

        existing_db.rawExecSQL("ATTACH DATABASE '" + root.getPath() + "' AS encrypted KEY '" + passphrase + "';");
        existing_db.rawExecSQL("SELECT sqlcipher_export('encrypted');");
        existing_db.rawExecSQL("DETACH DATABASE encrypted;");

        existing_db.close();

        originalFile.delete();

        root.renameTo(originalFile);

    }
}
