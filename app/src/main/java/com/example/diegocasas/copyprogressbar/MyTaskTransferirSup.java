package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MyTaskTransferirSup extends AsyncTask<Void, Void, Void> {

    ProgressDialog progress;
    Context context;
    ImageView trans;
    String rutaDestino, archivoDestino;
    TextView transTxt;

    public MyTaskTransferirSup(ProgressDialog progress, Context context, ImageView imageView, String rutaDestino, String archivoDestino, TextView  textView) {
        this.progress = progress;
        this.context = context;
        this.trans = imageView;
        this.rutaDestino = rutaDestino;
        this.archivoDestino = archivoDestino;
        this.transTxt = textView;
    }
    public void onPreExecute() {
        progress.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Void doInBackground(Void... unused) {
        try {
            createDir2();
        }catch (Exception e){
            e.printStackTrace();
        }
        copyFile2(rutaDestino, archivoDestino);
        return null;
    }
    public void onPostExecute(Void unused) {
        progress.dismiss();
        CueMsg cueMsg = new CueMsg(context);
        cueMsg.cueCorrect("El proceso finalizó");
        trans.setBackgroundResource(R.drawable.cerclebackgroundgreen);
        trans.setImageResource(R.drawable.ic_check_black_24dp);
        transTxt.setText("Archivo transferido correctamente");
    }
    public void copyFile2(String rutaDestino, String archivoDestino) {

        try {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(context);

            for (UsbMassStorageDevice device : devices) {

                // before interacting with a device you need to call init()!
                device.init();
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();
                /**try{
                    if (currentFs == null){
                        throw new Exception("USB no detectada, por favor inserte una...");
                    }
                    UsbFile[] files = root.listFiles();

                    for (UsbFile file : files) {
                        file.delete();
                    }
                } catch (Exception x){

                } finally {

                }**/

                File fileSource = new File(rutaDestino + archivoDestino);
                // Toast.makeText(this, "Source: " + fileSource.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                InputStream in = new FileInputStream(fileSource);
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                int len;

                UsbFile folder = root.search("envios");
                UsbFile file = folder.createFile(archivoDestino);
                //UsbFile file = root.createFile(archivoDestino);
                UsbFileOutputStream mOutPut = new UsbFileOutputStream(file);

                while ((len = in.read(buffer.array())) > 0) {
                    mOutPut.write(buffer.array());//This the key Point
                }
                in.close();
                mOutPut.close();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void createDir2(){
        try {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(context);

            for (UsbMassStorageDevice device : devices) {

                // before interacting with a device you need to call init()!
                device.init();
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                UsbFile root = currentFs.getRootDirectory();
                root.createDirectory("envios");
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
