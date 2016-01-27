package com.example.android.udpchat2;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class MainActivity extends AppCompatActivity {
    Button b;
    EditText iptxt,msgtxt;
    TextView ipt,msgt,myip;
    ReceiveThread r1;
    DatagramSocket sock;
    MyAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b=(Button)findViewById(R.id.sendbutton);
        iptxt=(EditText)findViewById(R.id.ip);
        msgtxt=(EditText)findViewById(R.id.msg);


        ipt=(TextView)findViewById(R.id.t1);
        msgt=(TextView)findViewById(R.id.t2);
        myip=(TextView)findViewById(R.id.myip);

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        myip.setText("My IP :"+ip.toString());


        Log.v("MyLog", "Creating Client Socket....");
        r1=new ReceiveThread(handler);
        r1.start();

        Log.v("MyLog", "Creating Server socket....");
        try
        {
            sock = new DatagramSocket(7777);

            Log.v("MyLog", "Socket Created....");
        }
        catch(IOException e)
        {
            Log.v("MyLog",e.toString());
        }


    }

    public void onTap(View view)
    {
        String s1= msgtxt.getText().toString();                        //Send msg text
        msgtxt.setText("");

        String s2=iptxt.getText().toString();
        //Toast.makeText(MainActivity.this, s1, Toast.LENGTH_SHORT).show();

        // MyAsyncTask task = new MyAsyncTask(s2);
        task = new MyAsyncTask(sock);

        task.setmsgstring(s1);
        Log.v("MyLog", "Starting thread....");

        task.setipstring(s2);

        task.execute();

        while (task.bool==false)
        {

        }
        task.cancel(true);
        Log.v("MyLog","Thread stopped....");

    }

    final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Toast.makeText(MainActivity.this,msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
}
