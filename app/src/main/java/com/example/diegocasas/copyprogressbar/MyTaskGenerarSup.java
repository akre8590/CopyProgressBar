package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
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

    private long time;
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
        time = System.currentTimeMillis();
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        cryptDataBase(rutaOrigen,archivoOrigen);//encripta la base de datos origen
        zipFile1( rutaOrigen,  archivoOrigen,  archivoDestino);//crea el primer archivo zip
        generateFile1(rutaOrigen, archivoDestino, md5);//saca el md5 del zip
        zipFile2(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino, md5);//crea el segundo zip

        return null;
    }
    public void onPostExecute(Void unused) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        cueMsg.cueCorrect("Diferencia de tiempo = " + (System.currentTimeMillis() - time));
        package1.setBackgroundResource(R.drawable.cerclebackgroundgreen);
        package1.setImageResource(R.drawable.ic_check_black_24dp);
        //Log.d("TestTask", "Diferencia de Tiempo = " + (System.currentTimeMillis() - time));
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

    public void generateFile1(String rutaOrigen, String archivoDestino, String md5) {
        try {

            File gpxfile = new File(rutaOrigen, md5 + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(fileToMD5(rutaOrigen + archivoDestino));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateFile(String rutaOrigen, String archivoDestino, String md5) {
        try {
            File root = new File(rutaOrigen);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(rutaOrigen, md5 + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(fileToMD5(rutaOrigen + archivoDestino));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void zipFile1(String rutaOrigen, String nombreDB, String nombreZip){
        ZipArchive zipArchive1 = new ZipArchive();
        zipArchive1.zip(rutaOrigen + nombreDB, rutaOrigen + nombreZip, "CONTA22015");

    }
    private void zipFile2(String rutaOrigen, String archivoOrigen, String rutaDestino, String archivoDestino, String md5){
        ZipArchive zipArchive2 = new ZipArchive();
        zipArchive2.zip(rutaOrigen + archivoDestino, rutaDestino + archivoDestino, "CONTA22015");
        zipArchive2.zip( rutaOrigen + md5 + ".txt", rutaDestino + archivoDestino, "CONTA22015");

        File fdelete = new File(rutaOrigen + archivoOrigen);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("DELETE", rutaOrigen + archivoOrigen);
            } else {
                Log.d("DELETE", rutaOrigen + archivoOrigen);
            }
        }
        File fdelete2 = new File(rutaOrigen + archivoDestino);
        if (fdelete2.exists()) {
            if (fdelete2.delete()) {
                Log.d("DELETE", rutaOrigen + archivoDestino);
            } else {
                Log.d("DELETE", rutaOrigen + archivoDestino);
            }
        }
        File fdelete3 = new File(rutaOrigen + md5 + ".txt");
        if (fdelete3.exists()){
            fdelete3.delete();
        }
    }

    private void cryptDataBase(String rutaOrigen, String nombreDB){
        db = SQLiteDatabase.openOrCreateDatabase(rutaOrigen + nombreDB,"", null);
        db.changePassword("abc123");
        db.close();
    }
}