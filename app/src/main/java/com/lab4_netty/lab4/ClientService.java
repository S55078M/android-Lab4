package com.lab4_netty.lab4;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lab4_netty.lab4.network.client.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientService extends Service {

    public static final String ACTION_SEND_MESSAGE_ACTIVITY = "SEND_MESSAGE_ACTIVITY";
    public static final String KEY_MESSAGE_ACTIVITY = "MESSAGE_ACTIVITY";

    private Client client;

    private ServiceReceiver receiver;

    private ExecutorService executorService;


    public ClientService() {
        executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (client == null) {
            client = new Client();
        }

        createReceiver();

        return START_STICKY;
    }

    private void createReceiver() {

        receiver = new ServiceReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_SEND_MESSAGE_SERVICE);
        registerReceiver(receiver, intentFilter);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (MainActivity.ACTION_SEND_MESSAGE_SERVICE.equals(action)) {
                String message = intent.getStringExtra(MainActivity.KEY_MESSAGE_SERVICE);
                executorService.execute(new Task(message));
            }
        }
    }

    private class Task implements Runnable {

        private String message;

        public Task(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            client.sendMessage(message);
            String response = client.getResponse();

            Intent intentBack = new Intent();
            intentBack.setAction(ACTION_SEND_MESSAGE_ACTIVITY);
            intentBack.putExtra(KEY_MESSAGE_ACTIVITY, response);
            sendBroadcast(intentBack);
        }
    }
}
