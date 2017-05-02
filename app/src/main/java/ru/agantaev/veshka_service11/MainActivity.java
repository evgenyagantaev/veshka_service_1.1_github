package ru.agantaev.veshka_service11;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.system.Os;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
{
    PowerManager pm;
    PowerManager.WakeLock wl;

    private Intent intent;

    EditText id_editText;
    byte id;
    EditText sensitivity_editText;
    byte sensitivity;
    Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_editText = (EditText)findViewById(R.id.id_editText);
        sensitivity_editText = (EditText)findViewById(R.id.sensitivity_editText);


        start_button = (Button)findViewById(R.id.start_button);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void UI_thread_flasher()
    {
        Runnable flasher = new Runnable()
        {
            @Override
            public void run()
            {

            }
        };

        runOnUiThread(flasher);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void start_button_Click(View v)
    {
        //start_button.setEnabled(false);

        id = Byte.parseByte((id_editText.getText()).toString());
        sensitivity = Byte.parseByte((sensitivity_editText.getText()).toString());
        if(sensitivity > 100)
            sensitivity = 100;

        intent = new Intent(this, veshka_service.class);
        intent.putExtra("id", id);
        intent.putExtra("sensitivity", sensitivity);
        startService(intent);

        sensitivity_editText.setEnabled(false);
        id_editText.setEnabled(false);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////


    //****************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        stopService(intent);
        //***** exit *****
        System.exit(0);
        //***** exit *****

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings)
        //{
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
