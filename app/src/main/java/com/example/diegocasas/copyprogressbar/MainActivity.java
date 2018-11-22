package com.example.diegocasas.copyprogressbar;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;

import android.support.annotation.RequiresApi;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ImageView pkg, usb, trans;
    TextView textInfo;
    UsbDevice device;
    UsbManager manager;
    PendingIntent mPermissionIntent;
    CardView detect, transf, gene;
    private static final String ACTION_USB_PERMISSION = "com.example.diegocasas.copyprogressbar";
    Button copy;
    CueMsg cueMsg = new CueMsg(MainActivity.this);
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gene = (CardView)findViewById(R.id.genePkg);
        textInfo = (TextView) findViewById(R.id.usb);
        pkg = (ImageView)findViewById(R.id.pkg);
        usb = (ImageView)findViewById(R.id.usbImg);

        verifyStoragePermissions(MainActivity.this);

        File fdelete1 = new File("storage/emulated/0/AdmCensal/envios/prueba.zip");
        if (fdelete1.exists()) {
            if (fdelete1.delete()) {
                Log.d("DELETE", "storage/emulated/0/AdmCensal/envios/prueba.zip");
            } else {
                Log.d("DELETE", "storage/emulated/0/AdmCensal/envios/prueba.zip");
            }
        }
        File fdelete2 = new File("storage/emulated/0/Documents/prueba.zip");
        if (fdelete2.exists()) {
            if (fdelete2.delete()) {
                Log.d("DELETE", "storage/emulated/0/Documents/prueba.zip");
            } else {
                Log.d("DELETE", "storage/emulated/0/Documents/prueba.zip");
            }
        }
        gene.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                generar();

            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))
        {
            UsbDevice device  = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
           cueMsg.cueCorrect("Memoria USB Conectada");
        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void generar(){
        File fileSource = new File("storage/emulated/0/Download/version1.apk");
        if (fileSource.exists()){
            ProgressDialog progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Generando paquetes...");
            progress.setCancelable(false);
            new MyTaskGenerar(progress, MainActivity.this, pkg).execute();
            detect =  (CardView)findViewById(R.id.detectUsb);
            gene.setClickable(false);
            detect.setClickable(true);
            detect.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    detectarUsb();
                }
            });
        }else {
            cueMsg.cueError("Archivo origen no encontrado");
            pkg.setBackground(getDrawable(R.drawable.cerclebackgroundred));
            pkg.setImageResource(R.drawable.ic_clear_black_24dp);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void detectarUsb(){
        check();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void transferir(){
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(MainActivity.this);
        if (devices.length > 0){
            for (UsbMassStorageDevice device : devices) {
                try {
                    device.init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileSystem currentFs = device.getPartitions().get(0).getFileSystem();
                if (currentFs != null){
                    if (checkFileExist()){
                        detect.setClickable(false);
                        trans = (ImageView)findViewById(R.id.trans);
                        ProgressDialog progress = new ProgressDialog(MainActivity.this);
                        progress.setMessage("Copiando paquetes a USB...");
                        progress.setCancelable(false);
                        new MyTaskTransferir(progress, MainActivity.this, trans).execute();
                        transf.setClickable(false);
                    } else {
                        cueMsg.cueError("El paquete generado no se encuentra o está dañado, favor de generarlo nuevamente");
                    }
                }
            }
        } else {
            usb.setBackground(getDrawable(R.drawable.cerclebackgroundred));
            usb.setImageResource(R.drawable.ic_clear_black_24dp);
            cueMsg.cueError("Asegurese que la USB está conectada y vuelva a presionar DETECTAR USB...");
            textInfo.setText("USB no detectada");
            transf.setClickable(false);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void check() {
                manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                /*
                 * this block required if you need to communicate to USB devices it's
                 * take permission to device
                 * if you want than you can set this to which device you want to communicate
                 */
                // ------------------------------------------------------------------
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(mUsbReceiver, filter);
                // -------------------------------------------------------------------
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                String i = "";
                while (deviceIterator.hasNext()) {
                    device = deviceIterator.next();
                    manager.requestPermission(device, mPermissionIntent);
                    i += "\n" + "USB Detectada!!" + "\n"
                            + "DeviceID: " + device.getDeviceId() + "\n";
                    //+ "VendorID: " + device.getVendorId() + "\n";//2385
                    //+ "ProductID: " + device.getProductId() + "\n";//5733
                }
                textInfo.setText(i);
                transf = (CardView) findViewById(R.id.transUsb);
            if(!textInfo.getText().toString().matches("")) {
                cueMsg.cueCorrect("USB detectada. Presione el botón transferir");
                transf.setClickable(true);
                usb.setBackground(getDrawable(R.drawable.cerclebackgroundgreen));
                usb.setImageResource(R.drawable.ic_check_black_24dp);
                transf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferir();
                    }
                });
            } else {
                textInfo.setText("USB no detectada");
                usb.setBackground(getDrawable(R.drawable.cerclebackgroundred));
                usb.setImageResource(R.drawable.ic_clear_black_24dp);
                cueMsg.cueError("USB no detectada...");
                transf.setClickable(false);
            }
        }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (ACTION_USB_PERMISSION.equals(action)) {
                // Permission requested
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //cueMsg.cueCorrect("Permiso otorgado");
                        // User has granted permission
                        // ... Setup your UsbDeviceConnection via mUsbManager.openDevice(usbDevice) ...
                    } else {
                        cueMsg.cueError("Permiso denegado");
                        // User has denied permission
                    }
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                cueMsg.cueError("Usb desconectado");
                // Device removed
                synchronized (this) {
                    // ... Check to see if usbDevice is yours and cleanup ...
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
               cueMsg.cueCorrect("Usb conectado");
                // Device attached
            }
        }
    };
    public boolean checkFileExist(){
        File fileSource = new File("storage/emulated/0/AdmCensal/envios/prueba.zip");
        if (fileSource.exists()){
            return true;
        } else {
            return false;
        }
    }
}
