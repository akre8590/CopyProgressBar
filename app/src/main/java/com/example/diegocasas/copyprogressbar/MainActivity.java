package com.example.diegocasas.copyprogressbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;

import android.support.annotation.RequiresApi;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    Button transferir, recibir;
    ImageView pkg, usb, trans;
    TextView textInfo, capacityTxt, transfTxt, exitTxt, detectTxt;
    UsbDevice device;
    UsbManager manager;
    PendingIntent mPermissionIntent;
    CardView detect, transf, gene, exit;
    Boolean rec = false;
    Boolean transBoolean = false;
    String rutaOrigen, rutaDestino, archivoOrigen, archivoDestino, sup_ent;
    private static final String ACTION_USB_PERMISSION = "com.example.diegocasas.copyprogressbar";
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
        deleteAdmCensal();
        verifyStoragePermissions(MainActivity.this);
        final LinearLayout myll = (LinearLayout) findViewById(R.id.orientationLayaout);
        transfTxt = (TextView)findViewById(R.id.transfTxt);
        exitTxt = (TextView)findViewById(R.id.exitTxt);
        detectTxt = (TextView)findViewById(R.id.detectUsbTxt);
        transferir = (Button)findViewById(R.id.transferir);
        recibir = (Button)findViewById(R.id.recibir);
        exit = (CardView)findViewById(R.id.exit);
        gene = (CardView)findViewById(R.id.genePkg);
        detect =  (CardView)findViewById(R.id.detectUsb);
        transf = (CardView) findViewById(R.id.transUsb);
        textInfo = (TextView) findViewById(R.id.usb);
        pkg = (ImageView)findViewById(R.id.pkg);
        usb = (ImageView)findViewById(R.id.usbImg);
        capacityTxt = (TextView)findViewById(R.id.capacity);

        if (getIntent().getStringExtra("rutaOrigen") != null && getIntent().getStringExtra("archivoOrigen") != null && getIntent().getStringExtra("rutaDestino") != null && getIntent().getStringExtra("archivoDestino") != null && getIntent().getStringExtra("tipofigura") != null){
            rutaOrigen = getIntent().getStringExtra("rutaOrigen"); // ruta del archivo que se va a zipear
            archivoOrigen = getIntent().getStringExtra("archivoOrigen"); //  archivo que se va a zipear
            rutaDestino = getIntent().getStringExtra("rutaDestino"); // ruta donde se zipea
            archivoDestino = getIntent().getStringExtra("archivoDestino"); //nombre del zip
            sup_ent = getIntent().getStringExtra("tipofigura");

           if (sup_ent.equals("S")){
               transferir.setVisibility(View.VISIBLE);
               recibir.setVisibility(View.VISIBLE);
               capacityTxt.setText("Ruta de origen: " +rutaOrigen +
                       "\n" + "Nombre archivo origen: " + archivoOrigen +
                       "\n" + "Ruta destino: " + rutaDestino +
                       "\n" + "Archivo destino: " + archivoDestino +
                       "\n" + "Figura: " +sup_ent);
               transferir.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       gene.setVisibility(View.VISIBLE);
                       exit.setVisibility(View.VISIBLE);
                       detect.setVisibility(View.VISIBLE);
                       transf.setVisibility(View.VISIBLE);
                       rec = false;
                       transferir.setVisibility(View.INVISIBLE);
                       recibir.setVisibility(View.INVISIBLE);
                   }
               });
               recibir.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       myll.setOrientation(LinearLayout.VERTICAL);
                       myll.setBottom(30);
                        exit.setVisibility(View.VISIBLE);
                        detect.setVisibility(View.VISIBLE);
                        transf.setVisibility(View.VISIBLE);
                        gene.setVisibility(View.INVISIBLE);
                        rec = true;
                        transferir.setVisibility(View.INVISIBLE);
                        recibir.setVisibility(View.INVISIBLE);
                        detectTxt.setText("1. Detectar USB");
                        transfTxt.setText("2. Recibe de USB");
                        exitTxt.setText("3. Integrar");
                        detect.setClickable(true);
                        detect.setOnClickListener(new View.OnClickListener() {
                           @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                           @Override
                           public void onClick(View v) {
                               detectarUsb();
                           }
                       });
                   }
               });
           } else if (sup_ent.equals("E")){
               gene.setVisibility(View.VISIBLE);
               exit.setVisibility(View.VISIBLE);
               detect.setVisibility(View.VISIBLE);
               transf.setVisibility(View.VISIBLE);
           }
            gene.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    if (sup_ent.equals("S")){
                        generarSup(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino);
                    }else {
                        generarEnt(rutaOrigen, archivoOrigen, rutaDestino, archivoDestino);
                    }
                }
            });
            capacityTxt.setText("Ruta de origen: " +rutaOrigen +
                    "\n" + "Nombre archivo origen: " + archivoOrigen +
                    "\n" + "Ruta destino: " + rutaDestino +
                    "\n" + "Archivo destino: " + archivoDestino +
                    "\n" + "Figura: " +sup_ent);
        }else {
            cueMsg.cueError("Sin parámetros");
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (sup_ent.equals("S") && !rec){
                   showAlertDialogExit();
                } else if (sup_ent.equals("S") && rec) {
                    showAlertDialogInteger();
                } else {
                    showAlertDialogExit();
                }
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
    public void generarEnt(String rutaOrigen, String archivoOrigen, String rutaDestino, String archivoDestino){
        File fileSource = new File(rutaOrigen + archivoOrigen);
        if (fileSource.exists()){
            ProgressDialog progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Generando paquetes...");
            progress.setCancelable(false);
            new MyTaskGenerar(progress, MainActivity.this, pkg, rutaOrigen, archivoOrigen, rutaDestino, archivoDestino).execute();
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
            cueMsg.cueError("Archivo no encontrado");
            pkg.setBackground(getDrawable(R.drawable.cerclebackgroundred));
            pkg.setImageResource(R.drawable.ic_clear_black_24dp);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void generarSup(String rutaOrigen, String archivoOrigen, String rutaDestino, String archivoDestino){
        File fileSource = new File(rutaOrigen + archivoOrigen);
        if (fileSource.exists()){
            ProgressDialog progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Generando paquetes...");
            progress.setCancelable(false);
            new MyTaskGenerarSup(progress, MainActivity.this, pkg, rutaOrigen, archivoOrigen, rutaDestino, archivoDestino).execute();
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
            cueMsg.cueError("Archivo no encontrado");
            pkg.setBackground(getDrawable(R.drawable.cerclebackgroundred));
            pkg.setImageResource(R.drawable.ic_clear_black_24dp);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void detectarUsb(){
        check();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void transferirEnt(){
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
                    if (currentFs.getFreeSpace() > 10485760) {
                        capacityTxt.setText("Capacidad:" + currentFs.getCapacity() + "\n" +
                                "Espacio libre: " + currentFs.getFreeSpace());
                        if (checkFileExist()) {
                            detect.setClickable(false);
                            trans = (ImageView) findViewById(R.id.trans);
                            ProgressDialog progress = new ProgressDialog(MainActivity.this);
                            progress.setMessage("Copiando paquetes a USB...");
                            progress.setCancelable(false);
                            new MyTaskTransferir(progress, MainActivity.this, trans, rutaDestino, archivoDestino, transfTxt).execute();
                            transf.setClickable(false);
                        } else {
                            cueMsg.cueError("El paquete generado no se encuentra o está dañado, favor de generarlo nuevamente");
                        }
                    } else {
                        cueMsg.cueError("La USB no cuenta con suficiente espacio libre, por favor libere espacio para que se pueda completar el proceso");
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
    public void transferirSup(String rutaDestino, String archivoDestino){
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
                    if (currentFs.getFreeSpace() > 10485760) {
                        capacityTxt.setText("Capacidad:" + currentFs.getCapacity() + "\n" +
                                "Espacio libre: " + currentFs.getFreeSpace());
                        if (checkFileExist()) {
                            detect.setClickable(false);
                            trans = (ImageView) findViewById(R.id.trans);
                            ProgressDialog progress = new ProgressDialog(MainActivity.this);
                            progress.setMessage("Copiando paquetes a USB...");
                            progress.setCancelable(false);
                            new MyTaskTransferirSup(progress, MainActivity.this, trans, rutaDestino, archivoDestino, transfTxt).execute();
                            transf.setClickable(false);
                        } else {
                            cueMsg.cueError("El paquete generado no se encuentra o está dañado, favor de generarlo nuevamente");
                        }
                    } else {
                        cueMsg.cueError("La USB no cuenta con suficiente espacio libre, por favor libere espacio para que se pueda completar el proceso");
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
    private void recived(){
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(MainActivity.this);
        if (devices.length > 0){
            detect.setClickable(false);
            trans = (ImageView) findViewById(R.id.trans);
            ProgressDialog progress = new ProgressDialog(MainActivity.this);
            progress.setMessage("Recibiendo paquetes desde USB...");
            progress.setCancelable(false);
            new MyTaskRecibir(progress, MainActivity.this, trans, transfTxt).execute();
            transf.setClickable(false);
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
                    i +=  "USB Detectada!!";
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
                        if (sup_ent.equals("S") && !rec){
                            transferirSup(rutaDestino, archivoDestino);
                        } else if (sup_ent.equals("S") && rec) {
                            recived();
                        } else {
                            transferirEnt();
                        }
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
    public void showAlertDialogExit() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar aplicación");
        builder.setMessage("¿Está seguro?");
        // add the buttons
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void showAlertDialogInteger() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Integrar base de datos");
        builder.setMessage("¿Está seguro?");
        // add the buttons
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new MyTaskIntegrar().execute();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public class MyTaskIntegrar extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected Void doInBackground(Void... voids) {
            Intent intent = new Intent("com.embarcadero.AdmCensal");
            intent.setType("text/pas");
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra("test","Esto es una prueba");
            startActivity(intent);
            return null;
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Void aVoid) {
            finishAndRemoveTask();
        }

    }
}
