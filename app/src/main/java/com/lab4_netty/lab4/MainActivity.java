package com.lab4_netty.lab4;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.lab4_netty.lab4.utils.JSEvaluator;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class MainActivity extends AppCompatActivity {

    final static String KEY_MESSAGE_SERVICE = "MESSAGE_SERVICE";
    final static String ACTION_SEND_MESSAGE_SERVICE = "SEND_MESSAGE_SERVICE";

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.button2)
    Button button2;

    @BindView(R.id.textView2)
    TextView textView2;

    MainActivityReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.templates_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Check permissions and start server and client
        checkPermissions();
        startServer();
        startClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.INTERNET},
                    1
            );
        }
    }

    private void startServer() {
        Intent intent = new Intent(this, ServerService.class);
        startService(intent);
    }

    private void startClient() {
        Intent intent = new Intent(this, ClientService.class);
        startService(intent);
    }

    private void createReceiver() {
        receiver = new MainActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClientService.ACTION_SEND_MESSAGE_ACTIVITY);
        registerReceiver(receiver, intentFilter);
    }


    @OnItemSelected(R.id.spinner)
    void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String code = "";
        switch (i) {
            case 0:
                code = "";
                break;
            case 1:
                code  = "function run() { \n" +
                        "    return 2+2; \n" +
                        "}";
                break;
            case 2:
                code  = "function run(a) { \n" +
                        "    return 'test: ' + a; \n" +
                        "}";
                break;
            case 3:
                code  = "function run() {\n" +
                        "  var power = function(base, exponent) {\n" +
                        "    var result = 1;\n" +
                        "    for (var count = 0; count < exponent; count++)\n" +
                        "      result *= base;\n" +
                        "    return result;\n" +
                        "  };\n" +
                        "  \n" +
                        "  return power(2, 10);\n" +
                        "}";
                break;


        }

        editText.setText(code);
    }

    @OnClick(R.id.button2)
    void onClick(View view) {
        String message = editText.getText().toString();

        if (!"".equals(message.trim())) {

            Intent intent = new Intent();
            intent.setAction(ACTION_SEND_MESSAGE_SERVICE);
            intent.putExtra(KEY_MESSAGE_SERVICE, message);
            sendBroadcast(intent);
        }
    }


    private class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(android.content.Context context, Intent intent) {

            String action = intent.getAction();

            if (ClientService.ACTION_SEND_MESSAGE_ACTIVITY.equals(action)) {
                String message = intent.getStringExtra(ClientService.KEY_MESSAGE_ACTIVITY);
                textView2.setText(message);
            }
        }
    }
}
