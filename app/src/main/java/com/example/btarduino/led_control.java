package com.example.btarduino;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class led_control extends AppCompatActivity {

    Button btnAdelante, btnAtras, btnIzquierda, btnDerecha, btnDis;
    FloatingActionButton postBtn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter bluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID id = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        setContentView(R.layout.activity_led_control);

        btnAdelante = (Button)findViewById(R.id.frwBtn);
        btnAtras = (Button)findViewById(R.id.bckBtn);
        btnIzquierda = (Button)findViewById(R.id.lftBtn);
        btnDerecha = (Button)findViewById(R.id.rgtBtn);
        btnDis = (Button)findViewById(R.id.dscBtn);
        postBtn = (FloatingActionButton)findViewById(R.id.postBtn);


        new ConnectBT().execute();

        btnAdelante.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    adelante();
                    return true;
                }if(event.getAction() == MotionEvent.ACTION_UP) {
                    stop();
                    return false;
                }
                return true;
            }
        });

        btnAtras.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    atras();
                    return true;
                }if(event.getAction() == MotionEvent.ACTION_UP) {
                    stop();
                    return false;
                }
                return true;
            }
        });

        btnIzquierda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    izquierda();
                    return true;
                }if(event.getAction() == MotionEvent.ACTION_UP) {
                    stop();
                    return false;
                }
                return true;
            }
        });

        btnDerecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    derecha();
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    stop();
                    return false;
                }

                return true;
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { postJSON(); }
        });
    }

    private void postJSON() {
        try {
            URL url = new URL("http://r2bis.ngrok.io/postAndroid");
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.setRequestMethod("POST");
            req.setRequestProperty("Content-Type", "application/json");
            req.setDoOutput(true);
            req.setDoInput(true);
            req.connect();

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("uname", "test");
            jsonParam.put("message", "test");

            DataOutputStream os = new DataOutputStream(req.getOutputStream());
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(req.getResponseCode()));
            Log.i("MSG" , req.getResponseMessage());

            req.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void adelante(){
        if(btSocket != null){
           try {
               btSocket.getOutputStream().write("0".toString().getBytes());
           }catch(IOException e){
               Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
        adelante();
    }

    private void atras(){
        if(btSocket != null){
            try {
                btSocket.getOutputStream().write("1".toString().getBytes());
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
        atras();
    }

    private void izquierda(){
        if(btSocket != null){
            try {
                btSocket.getOutputStream().write("2".toString().getBytes());
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
        izquierda();
    }

    private void derecha(){
        if(btSocket != null){
            try {
                btSocket.getOutputStream().write("3".toString().getBytes());
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
        derecha();
    }

    private void stop(){
        if(btSocket != null){
            try {
                btSocket.getOutputStream().write("4".toString().getBytes());
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
        stop();
    }

    private void disconnect(){
        if(btSocket != null){
            try{
                btSocket.close();
            }catch (IOException e){
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
    /*private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_name) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute(){
            progress = ProgressDialog.show(led_control.this, "Conecting, please wait","XDxdxdxxdddddxdxx");
        }

        @Override
        protected Void doInBackground(Void... devices){
            try{
                if(btSocket != null || !isBtConnected){
                    bluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice btDevice = bluetooth.getRemoteDevice(address);
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(id);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }catch (IOException e){
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(!ConnectSuccess){
                Toast.makeText(getApplicationContext(), "Impossible to connect", Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}