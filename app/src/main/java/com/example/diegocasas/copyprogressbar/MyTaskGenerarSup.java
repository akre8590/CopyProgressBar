package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import ir.mahdi.mzip.zip.ZipArchive;

public class MyTaskGenerarSup extends AsyncTask<Void, Void, Void> {

    ProgressDialog progress;
    ImageView package1;
    Context context;
    public String rutaOrigen, archivoOrigen,  rutaDestino, archivoDestino;

    public MyTaskGenerarSup(ProgressDialog progress, Context context, ImageView imageView, String rutaOrigen, String archivoOrigen, String rutaDestino, String archivoDestino) {
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

    @Override
    protected Void doInBackground(Void... voids) {
        zipFile( rutaOrigen,  archivoOrigen,  rutaDestino,  archivoDestino);
        return null;
    }
    public void onPostExecute(Void unused) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        cueMsg.cueCorrect("Paquetes generados con éxito!!!");
        package1.setBackgroundResource(R.drawable.cerclebackgroundgreen);
        package1.setImageResource(R.drawable.ic_check_black_24dp);

    }
    private void zipFile(String rutaOrigen, String nombreDB, String rutaDestino, String nombreZip){
        ZipArchive zipArchive1 = new ZipArchive();
        zipArchive1.zip(rutaOrigen + nombreDB, rutaOrigen + nombreZip, "CONTA22015");

        ZipArchive zipArchive2 = new ZipArchive();
        zipArchive2.zip(rutaOrigen + nombreZip, rutaDestino + nombreZip, "CONTA22015");

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
