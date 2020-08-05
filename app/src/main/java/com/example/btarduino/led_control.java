package com.example.btarduino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class led_control extends AppCompatActivity {

    Button btnOn, btnOff, btnDis;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter bluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID id = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(MainActivity.EXTRA_ADDRESS);
        setContentView(R.layout.activity_led_control);

        btnOn = (Button)findViewById(R.id.onBtn);
        btnOff = (Button)findViewById(R.id.offBtn);
        btnDis = (Button)findViewById(R.id.dscBtn);

        new ConnectBT().execute();

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledOn();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledOff();
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    private void ledOn(){
        if(btSocket != null){
           try {
               btSocket.getOutputStream().write("12".toString().getBytes());
           }catch(IOException e){
               Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ledOff(){
        if(btSocket != null){
            try {
                btSocket.getOutputStream().write("11".toString().getBytes());
            }catch(IOException e){
                Toast.makeText(getApplicationContext(), "send error", Toast.LENGTH_LONG).show();
            }
        }
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