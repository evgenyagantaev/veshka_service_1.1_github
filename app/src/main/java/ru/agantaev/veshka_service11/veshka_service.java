package ru.agantaev.veshka_service11;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 4/4/2015.
 */
public class veshka_service extends Service
{



    public Boolean react = true;

    public Byte id = 1;
    public Byte sensitivity = 91;

    byte[] buffer = new byte[15];

    //DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);



    private boolean showToast = false;



    @Override
    public void onCreate()
    {



        Toast.makeText(this, "Service created!", Toast.LENGTH_SHORT).show();
        ServiceThread serviceThread = new ServiceThread();
        serviceThread.start();

    }// end public void onCreate()


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        id = intent.getByteExtra("id", (byte)1);
        sensitivity = intent.getByteExtra("sensitivity", (byte)91);
        buffer[0] = id;
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    private class ServiceThread extends Thread implements SensorEventListener
    {


        private Float x = 0.0f;
        private Float y = 0.0f;
        private Float z = 0.0f;
        private Double norm = 0.0;

        private int counter = 0;

        Boolean actuated = false;
        Boolean sending = false;

        private DatagramSocket udpTransmitter = null;
        private InetAddress serverAddr;
        private DatagramPacket udpOut;
        //private String address = "192.168.43.249";
        private String address = "192.168.88.21";

        private SensorManager mSensorManager;
        private Sensor mAccelerometerSensor;
        //////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void onSensorChanged(SensorEvent event)
        {

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            norm = Math.sqrt(x * x + y * y + z * z);
            Double threshold = 10.0 + 100.0 - sensitivity;

            if(norm >= threshold && !sending)
            {
                actuated = true;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }


        ////////////////////////////////////////////////////////////////////////////////////////

        public void run()
        {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometerSensor = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            mSensorManager.registerListener(this, mAccelerometerSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);

            try
            {
                serverAddr = InetAddress.getByName(address);
            } catch (UnknownHostException e)
            {
                e.printStackTrace();
            }


            try
            {
                udpTransmitter = new DatagramSocket();

            }
            catch (SocketException e)
            {
                e.printStackTrace();
            }

            buffer[0] = id;

            counter = 0;
            while(true)
            {
                //*
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //*/

                counter++;



                if(actuated)
                {
                    sending = true;
                    actuated = false;

                    buffer[1] = 1; // actuated!
                    udpOut = new DatagramPacket(buffer, 13, serverAddr, 1225);

                    /*
                    try
                    {
                        udpTransmitter.send(udpOut);
                        //Toast.makeText(this, "Datagramm sent!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //*/
                    try
                    {
                        udpTransmitter.send(udpOut);
                        //Toast.makeText(this, "Datagramm sent!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try
                    {
                        udpTransmitter.send(udpOut);
                        //Toast.makeText(this, "Datagramm sent!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //*/

                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    sending = false;
                }

                if(counter >= 40)
                {
                    counter = 0;

                    buffer[1] = 0; // regular packet; means sensor online; no actuation
                    udpOut = new DatagramPacket(buffer, 12, serverAddr, 1225);

                    try
                    {
                        udpTransmitter.send(udpOut);
                        //Toast.makeText(this, "Datagramm sent!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }



                showToast = true;
            }
        }
    }
    //*********************************************************************************************





    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private class Sleep_thread extends  Thread
    {
        @Override
        public void run()
        {
            try {sleep(5000L, 0);}
            catch (InterruptedException e) {e.printStackTrace();}

            react = true;
        }
    }
}


