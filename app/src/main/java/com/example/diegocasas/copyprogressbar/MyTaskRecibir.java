package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ir.mahdi.mzip.zip.ZipArchive;

public class MyTaskRecibir extends AsyncTask<Void, Void, Void> {

    ProgressDialog progress;
    Context context;
    ImageView trans;
    TextView rec;

    public MyTaskRecibir(ProgressDialog progress, Context context, ImageView imageView, TextView textView){
        this.progress = progress;
        this.context = context;
        this.trans = imageView;
        this.rec = textView;

    }

    @Override
    protected void onPreExecute() {
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        recived();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        File file1 = new File("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
        if (file1.exists()){
            cueMsg.cueCorrect("El proceso finalizó");
            trans.setBackgroundResource(R.drawable.cerclebackgroundgreen);
            trans.setImageResource(R.drawable.ic_check_black_24dp);
            rec.setText("Archivo recibido correctamente");
        }else {
            cueMsg.cueError("Archivo no encontrado en la USB");
            trans.setBackgroundResource(R.drawable.cerclebackgroundred);
            trans.setImageResource(R.drawable.ic_clear_black_24dp);
            rec.setText("Archivo no recibido");
        }
    }
    private void recived()  {
        File to = new File("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
        try {

            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(context);

            for (UsbMassStorageDevice device : devices) {
                // before interacting with a device you need to call init()!
                device.init();
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();

                //UsbFile file = root.createFile("opera.txt");
                    try {
                        UsbFile file = root.search("datos_AdmCensal.zip");
                        InputStream is = new UsbFileInputStream(file);
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
                        byte[] bytes = new byte[currentFs.getChunkSize()];
                        int count;
                        long total = 0;

                        while ((count = is.read(bytes)) != -1) {
                            out.write(bytes, 0, count);
                            total += count;
                        }
                        out.close();
                        is.close();
                        file.delete();
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                //UsbFile[] files = root.listFiles();
                //for(UsbFile file: files) {

                /**File fdelete2 = new File("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
                 if (fdelete2.exists()) {
                 if (fdelete2.delete()) {
                 Log.d("DELETE", "/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
                 } else {
                 Log.d("DELETE", "/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip");
                 }
                 }**/
            }
        } catch(FileNotFoundException e1){
            e1.printStackTrace();
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch(IOException e1){
            e1.printStackTrace();
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
        ZipArchive zipArchive = new ZipArchive();
        zipArchive.unzip("/storage/emulated/0/AdmCensal/recepciones/datos_AdmCensal.zip", "/storage/emulated/0/AdmCensal/recepciones/","");

    }
}
