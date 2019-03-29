package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.sqlitecrypt.database.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import ir.mahdi.mzip.zip.ZipArchive;

public class MyTaskGenerarSup extends AsyncTask<Void, Void, Void> {

    SQLiteDatabase db;
    ProgressDialog progress;
    ImageView package1;
    Context context;
    public String rutaOrigen, archivoOrigen,  rutaDestino, archivoDestino, md5;

    public MyTaskGenerarSup(ProgressDialog progress, Context context, ImageView imageView, String rutaOrigen, String archivoOrigen, String rutaDestino, String archivoDestino, String md5) {
        this.progress = progress;
        this.context = context;
        this.package1 = imageView;
        this.rutaOrigen = rutaOrigen;
        this.archivoOrigen = archivoOrigen;
        this.rutaDestino = rutaDestino;
        this.archivoDestino = archivoDestino;
        this.md5 = md5;
    }
    public void onPreExecute() {
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        generateFile(rutaOrigen, archivoOrigen, rutaDestino, md5);
        cryptDataBase(rutaOrigen,archivoOrigen);
        //cryptDataBase();
        zipFile( rutaOrigen,  archivoOrigen,  rutaDestino,  archivoDestino, md5);
        return null;
    }
    public void onPostExecute(Void unused) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        cueMsg.cueCorrect("Paquetes generados con éxito!!!");
        package1.setBackgroundResource(R.drawable.cerclebackgroundgreen);
        package1.setImageResource(R.drawable.ic_check_black_24dp);

    }
    public static String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal;
    }

    public void generateFile(String rutaOrigen, String archivoOrigen, String rutaDestino, String md5) {
        try {
            File root = new File(rutaOrigen);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(rutaDestino, md5 + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(fileToMD5(rutaOrigen + archivoOrigen));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void zipFile(String rutaOrigen, String nombreDB, String rutaDestino, String nombreZip, String md5){
        ZipArchive zipArchive1 = new ZipArchive();
        zipArchive1.zip(rutaOrigen + nombreDB, rutaOrigen + nombreZip, "CONTA22015");

        ZipArchive zipArchive2 = new ZipArchive();
        zipArchive2.zip(rutaOrigen + nombreZip, rutaDestino + nombreZip, "CONTA22015");
        zipArchive2.zip( rutaDestino + md5 + ".txt", rutaDestino + nombreZip, "CONTA22015");

        File fdelete = new File(rutaOrigen + nombreZip);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("DELETE", rutaOrigen + nombreZip);
            } else {
                Log.d("DELETE", rutaOrigen + nombreZip);
            }
        }
        File fdelete2 = new File(rutaDestino + md5 + ".txt");
        if (fdelete2.exists()){
            fdelete2.delete();
        }
        /**File fdelete2 = new File(rutaOrigen + nombreDB);
         if (fdelete2.exists()) {
         if (fdelete2.delete()) {
         Log.d("DELETE", rutaOrigen + nombreDB);
         } else {
         Log.d("DELETE", rutaOrigen + nombreDB);
         }
         }**/
    }

    private void cryptDataBase(String rutaOrigen, String nombreDB){
        db = SQLiteDatabase.openOrCreateDatabase(rutaOrigen + nombreDB,"", null);
        db.changePassword("abc123");
        db.close();
    }

    /**private void cryptDataBase(){
        //SQLiteDatabase db;
        SQLiteDatabase db;
        String path = "/storage/emulated/0/datos1.db3";
        db = SQLiteDatabase.openOrCreateDatabase(path,"", null);
        db.changePassword("abc123");
        db.close();
    }**/
}
