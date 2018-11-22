package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.ImageView;


import java.io.File;


import ir.mahdi.mzip.zip.ZipArchive;

public class MyTaskGenerar extends AsyncTask<Void, Void, Void> {


    ProgressDialog progress;
    ImageView package1;
    Context context;
    String rutaOrigen = "storage/emulated/0/Download/";
    String nombreDB = "version1.apk";
    String rutaDestino = "storage/emulated/0/Documents/";
    String nombreZip = "prueba.zip";


    public MyTaskGenerar(ProgressDialog progress, Context context, ImageView imageView) {
        this.progress = progress;
        this.context = context;
        this.package1 = imageView;
    }
    public void onPreExecute() {
        progress.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Void doInBackground(Void... unused) {

        zipFile( rutaOrigen,  nombreDB,  rutaDestino,  nombreZip);
        return null;
    }

    public void onPostExecute(Void unused) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        cueMsg.cueCorrect("Paquetes generados con éxito!!!");
        package1.setBackgroundResource(R.drawable.cerclebackgroundgreen);
        package1.setImageResource(R.drawable.ic_check_black_24dp);

    }

    public void zipFile(String rutaOrigen, String nombreDB, String rutaDestino, String nombreZip){
            ZipArchive zipArchive1 = new ZipArchive();
            zipArchive1.zip(rutaOrigen + nombreDB, rutaOrigen + nombreZip, "CONTA22015");

            ZipArchive zipArchive2 = new ZipArchive();
            zipArchive2.zip(rutaOrigen + nombreZip, rutaDestino + nombreZip, "CONTA22015");

            ZipArchive zipArchive3 = new ZipArchive();
            zipArchive3.zip(rutaOrigen + nombreDB, "storage/emulated/0/AdmCensal/envios/prueba.zip", "");

            File fdelete = new File(rutaOrigen + nombreZip);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.d("DELETE", rutaOrigen + nombreZip);
                } else {
                    Log.d("DELETE", rutaOrigen + nombreZip);
                }
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
}