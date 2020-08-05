package com.example.btarduino;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import  android.bluetooth.BluetoothDevice;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btnPair;
    ListView listedDevices;

    private BluetoothAdapter bluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPair = (Button)findViewById(R.id.buttonPairing);
        listedDevices = (ListView)findViewById(R.id.listView);


        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if(bluetooth == null){
            Toast.makeText(getApplicationContext(),
                    "No bluetooth", Toast.LENGTH_LONG).show();

        }else{
            if(bluetooth.isEnabled()){

            }else {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,1);
            }
        }

        btnPair.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               pairedList();
           }
        });
    }

    public void pairedList(){
        pairedDevices = bluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size()>0){
            for(BluetoothDevice bt : pairedDevices){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }else{
            Toast.makeText(getApplicationContext(), "No bluetooth devices", Toast.LENGTH_LONG).show();

        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listedDevices.setAdapter(adapter);
        listedDevices.setOnItemClickListener(listListener);

    }

    public AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent(MainActivity.this, led_control.class);
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        }
    };
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.app_name){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}