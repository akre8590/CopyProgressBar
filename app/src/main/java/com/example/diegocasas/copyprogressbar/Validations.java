package com.example.diegocasas.copyprogressbar;

import android.util.Log;

import java.io.File;

public class Validations {
    String sup_ent, rutaDestino, archivoDestino;


    public Validations(String sup_ent, String rutaDestino, String archivoDestino){
       this.sup_ent = sup_ent;
       this.rutaDestino = rutaDestino;
       this.archivoDestino = archivoDestino;
    }
    public boolean checkFileExist(){
        if (sup_ent.equals("S")){
            File fileSource = new File(rutaDestino + archivoDestino);
            if (fileSource.exists()){
                return true;
            } else {
                return false;
            }
        } else {
            File fileSource = new File("storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip");
            if (fileSource.exists()){
                return true;
            } else {
                return false;
            }
        }
    }
    public void deleteAdmCensal(){
        File fdelete1 = new File("storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip");
        if (fdelete1.exists()) {
            if (fdelete1.delete()) {
                Log.d("DELETE", "storage/emulated/0/AdmCensal/envios/datos_AdmCensal.zip");
            } else {
                Log.d("DELETE", "storage/emulated/0/AdmCensal/envios/prueba.zip");
            }
        }
    }

}
