package com.example.proyecto_cloudlamp;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 2;
    private final static int MESSAGE_READ = 4; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 5; // used in bluetooth handler to identify message status

    private BluetoothAdapter mBtAdapter;
    private BluetoothSocket btSocket; // mBTSocket
    private BluetoothDevice DispositivoSeleccionado;
    private ConnectedThread MyConexionBT;// mConnectedThread;
    private ArrayList<String> mNameDevices = new ArrayList<>();
    private Handler mHandler;
    private ArrayAdapter<String> deviceAdapter;
    ImageButton btbuscar, btconectar, btdesconectar;
    Button btefecto, btiniciar, btapagar;
    Spinner dispos_encontrados, spiner_efectos;
    String humidity;  // Hum: 70.40%
    String temperature;  // Temp: 19.60°C
    String heatIndex;
    ImageView rueda_color;
    View colorViewg,colorViewg2,colorViewg3;
    TextView  lvlintensity, txt_color_seleccionado, txt_color_seleccionado2, txthex, txtrgb;
    TextView tempc,tempf,hum;
    SeekBar seekbar;
    Bitmap bitmap;
    //Trama de datos a enviar
    //(orden,r,g,b,intensidad)
    int bandera=0;
    int o=0,r,g,b,l=50;
    String[] categorias = {"Color Wipe", "Theater Chase","Rainbow","Theater Chase Rainbow","Normal","Temperatura"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestBluetoothConnectPermission();
        requestLocationPermission();

        btbuscar = findViewById(R.id.btbuscar);
        seekbar = findViewById(R.id.seekbar);
        lvlintensity = findViewById(R.id.lvlintensity);
        btconectar = findViewById(R.id.btconectar);
        btdesconectar = findViewById(R.id.btdesconectar);
        btefecto = findViewById(R.id.btefecto);
        btiniciar = findViewById(R.id. btiniciar);
        btapagar = findViewById(R.id.btapagar);
        dispos_encontrados = findViewById(R.id.dispos_encontrados);
        rueda_color = findViewById(R.id.rueda_color);
        colorViewg = findViewById(R.id.colorViewg);
        colorViewg2 = findViewById(R.id.colorViewg2);
        colorViewg3 = findViewById(R.id.colorViewg3);
        spiner_efectos = findViewById(R.id.spiner_efectos);
        tempc = findViewById(R.id.tempc);
       // tempf = findViewById(R.id.tempf);
        hum = findViewById(R.id.hum);


        txt_color_seleccionado = findViewById(R.id.txt_color_seleccionado);
        txt_color_seleccionado2 = findViewById(R.id.txt_color_seleccionado2);

        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mNameDevices);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dispos_encontrados.setAdapter(deviceAdapter);
        spiner_efectos.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categorias));

        btbuscar.setOnClickListener(this);
        btconectar.setOnClickListener(this);
        btdesconectar.setOnClickListener(this);
        btefecto.setOnClickListener(this);
        btiniciar.setOnClickListener(this);
        btapagar.setOnClickListener(this);
        rueda_color.setDrawingCacheEnabled(true);
        rueda_color.buildDrawingCache(true);

        mHandler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        String[] parts = readMessage.split(",");

                        // Asegurarse de tener suficientes partes antes de intentar acceder a ellas
                        if (parts.length == 2) {
                            humidity = parts[0];  // Hum: 70.40%
                            temperature = parts[1];  // Temp: 19.60°C
                            // Heat index: 19.45°C

                            // Mostrar cada valor en su TextView correspondiente

                        }

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    tempc.setText(readMessage);
                    // hum.setText(humidity);
                    //tempc.setText(temperature);

                }

                // if(msg.what == CONNECTING_STATUS){
                //if(msg.arg1 == 1)
                // mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                //else
                // mBluetoothStatus.setText("Connection Failed");
                // }
            }
        };


        //ImageView on touch listener

        rueda_color.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN||event.getAction() == MotionEvent.ACTION_MOVE){
                    bitmap = rueda_color.getDrawingCache();
                    int pixel = bitmap.getPixel((int)event.getX(),(int)event.getY());
                    //getting RGB VALUES
                    /*
                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);
                    */
                    r = Color.red(pixel);
                    g = Color.green(pixel);
                    b = Color.blue(pixel);
                    //getting HEX VALUES
                    String hex = "#" +Integer.toHexString(pixel);

                    //set backgroundcolor

                    //colorViewg.setBackgroundColor(Color.rgb(r,g,b));
                    // colorViewg2.setBackgroundColor(Color.rgb(r,g,b));
                    colorViewg3.setBackgroundColor(Color.rgb(r,g,b));
                    //colorViewg4.setBackgroundColor(Color.rgb(r,g,b));
                    txt_color_seleccionado.setBackgroundColor(Color.rgb(r,g,b));
                    txt_color_seleccionado2.setBackgroundColor(Color.rgb(r,g,b));
                    //  btefecto.setBackgroundColor((Color.rgb(r,g,b)));
                    // btapagar.setBackgroundColor((Color.rgb(r,g,b)));
                    // btiniciar.setBackgroundColor((Color.rgb(r,g,b)));
                    // seekbar.setBackgroundColor((Color.rgb(r,g,b)));
                    seekbar.setProgressTintList(ColorStateList.valueOf((Color.rgb(r,g,b))));
                    seekbar.setThumbTintList(ColorStateList.valueOf((Color.rgb(r,g,b))));
                    // rueda_color.setBackgroundColor((Color.rgb(r,g,b)));
                    txt_color_seleccionado.setText( hex);
                    txt_color_seleccionado2.setText(" ("+r+", "+g+","+b+")");
                    //set hex rgb
                    if(bandera==1){
                        MyConexionBT.writec((o+","+r+","+g+","+b+","+l).toCharArray());}

                    //txtrgb.setText("RGB: ("+r, TextView.BufferType.valueOf(" ,"+g+" ,"+ b+")"));

                }

                return true;
            }
        });
        spiner_efectos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoria = (String) spiner_efectos.getSelectedItem();
                switch (categoria){
                    case "Color Wipe":
                        o = 21;
                        break;
                    case "Theater Chase":
                        o= 22;
                        break;
                    case "Rainbow":
                        o= 23;
                        break;
                    case "Theater Chase Rainbow":
                        o= 24;
                        break;
                    case "Normal":
                        o= 25;
                        break;
                    case "Temperatura":
                        o= 31;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //No seleccionaron Nada
            }
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lvlintensity.setText(String.valueOf(progress));
                l = progress;
                Toast.makeText(getBaseContext(), "l: "+l, Toast.LENGTH_SHORT).show();
                if(bandera==1){
                    MyConexionBT.writec((o+","+r+","+g+","+b+","+l).toCharArray());}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == MainActivity.REQUEST_ENABLE_BT) {
                        Log.d(TAG, "ACTIVIDAD REGISTRADA");
                        //Toast.makeText(getBaseContext(), "ACTIVIDAD REGISTRADA", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public void DispositivosVinculados() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            showToast("Bluetooth no disponible en este dispositivo.");
            finish();
            return;
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            someActivityResultLauncher.launch(enableBtIntent);
        }

        dispos_encontrados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DispositivoSeleccionado = getBluetoothDeviceByName(mNameDevices.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                DispositivoSeleccionado = null;
            }
        });

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mNameDevices.add(device.getName());
            }
            deviceAdapter.notifyDataSetChanged();
        } else {
            showToast("No hay dispositivos Bluetooth emparejados.");
        }
    }

    // Agrega este método para solicitar el permiso
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
    }

    // Agrega este método para solicitar el permiso
    private void requestBluetoothConnectPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_CONNECT_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso concedido, ahora puedes utilizar funciones de Bluetooth que requieran BLUETOOTH_CONNECT");
            } else {
                Log.d(TAG, "Permiso denegado, debes manejar este caso según tus necesidades");
            }
        }
    }

    private BluetoothDevice getBluetoothDeviceByName(String name) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, " ----->>>>> ActivityCompat.checkSelfPermission");
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }
    private void ConectarDispBT() {
        if (DispositivoSeleccionado == null) {
            showToast("Selecciona un dispositivo Bluetooth.");
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            btSocket = DispositivoSeleccionado.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
            btSocket.connect();
            MyConexionBT = new ConnectedThread(btSocket);
            MyConexionBT.start();
            bandera=1;
            showToast("Conexión exitosa.");
        } catch (IOException e) {
            showToast("Error al conectar con el dispositivo.");
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;
        private final InputStream mmInStream;
        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                showToast("Error al crear el flujo de datos.");
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        public void write(char input) {
            //byte msgBuffer = (byte)input;
            try {
                mmOutStream.write((byte)input);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        public void writec(char[] input) {
            try {
                for (char c : input) {
                    mmOutStream.write((byte) c);
                }
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btbuscar:
                DispositivosVinculados();
                Toast.makeText(getBaseContext(),"Buscando",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btconectar:
                Toast.makeText(getBaseContext(),"Conectar",Toast.LENGTH_SHORT).show();
                ConectarDispBT();

                break;
            case R.id.btdesconectar:
                if (btSocket!=null)
                {
                    try {btSocket.close();
                        bandera=0;}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();}
                }
                finish();
                break;
            case R.id.btiniciar:

                MyConexionBT.writec((o+","+r+","+g+","+b+","+l).toCharArray());
                break;
            case R.id.btapagar:
                MyConexionBT.writec("apagar".toCharArray());
                break;
            case R.id.btefecto:
                MyConexionBT.writec(("efecto,"+"("+r+", "+g+","+b+")").toCharArray());
                break;
        }
    }
}

