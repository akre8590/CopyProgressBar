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
    public String rutaOrigen, archivoOrigen, rutaDestino, archivoDestino;

    public MyTaskGenerar(ProgressDialog progress, Context context, ImageView imageView, String rutaOrigen, String archivoOrigen, String rutaDestino, String archivoDestino) {
        this.progress = progress;
        this.context = context;
        this.package1 = imageView;
        this.rutaOrigen = rutaOrigen;
        this.archivoOrigen = archivoOrigen;
        this.rutaDestino = rutaDestino;
        this.archivoDestino = archivoDestino;
    }
    public void onPreExecute() {
        progress.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Void doInBackground(Void... unused) {
        zipFile( rutaOrigen,  archivoOrigen);
        zipFile2(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino);
        return null;
    }

    public void onPostExecute(Void unused) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        cueMsg.cueCorrect("Paquetes generados con éxito!!!");
        package1.setBackgroundResource(R.drawable.cerclebackgroundgreen);
        package1.setImageResource(R.drawable.ic_check_black_24dp);

    }

    private void zipFile(String rutaOrigen, String nombreDB){

        ZipArchive zipArchive3 = new ZipArchive();
        zipArchive3.zip(rutaOrigen + nombreDB, "storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip", "");
    }
    private void zipFile2(String rutaOrigen, String nombreDB, String rutaDestino, String archivoDestino){

        ZipArchive zipArchive = new ZipArchive();
        zipArchive.zip(rutaOrigen + nombreDB, rutaDestino + archivoDestino, "");
    }
}