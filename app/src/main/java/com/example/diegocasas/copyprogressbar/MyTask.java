package com.example.diegocasas.copyprogressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyTask extends AsyncTask<Void, Void, Void> {

    File sourceFile = new File("storage/emulated/0/AdmCensal/formato08.zip");
    File destFile = new File("storage/emulated/0/formato08.zip");

    ProgressDialog progress;
    Context context;

    public MyTask(ProgressDialog progress, Context context) {
        this.progress = progress;
        this.context = context;
    }

    public void onPreExecute() {
        progress.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Void doInBackground(Void... unused) {

        try {
            copy(sourceFile,destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void onPostExecute(Void unused) {
        progress.dismiss();
        Toast.makeText(context, "Finalizó el copiado", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
            }
        }
    }
}