package com.example.wangpan.irobot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnW,btnA,btnS,btnD,btnStop,btnCon;
    private TextView tvRadius,tvVelocity,tvStatus;
    private SeekBar sbRadius,sbVelocity;

    private ServerSocket serverSocket;
    private Socket socket;
    private boolean isConneected = false;
    buildServer Server;

    //除定义或声明之外的任意语句必须放在方法内部，包括赋值语句，可以在声明时进行赋值，如下
    int radiusHigh = 0x00,radiusLow = 0x32;
    int velocityHigh = 0x00,velocityLow = 0xC8;
    int backVelHigh = 0xFF,backVelLow = 0x38;

    //data format:(128)0x80:Start;(130)0x82:Control;(137)0x89:Drive;0x00 and 0xC8:the bytes of velocity;0x80 and 0x00 the bytes of radius
    byte[] forward = {(byte)0x80,(byte)0x82,(byte)0x89,(byte)0x00,(byte)0xC8,(byte)0x80,(byte)0x00,(byte)0x2B};
    byte[] back = {(byte)0x80,(byte)0x82,(byte)0x89,(byte)0xFF,(byte)0x38,(byte)0x80,(byte)0x00,(byte)0x2B};

    //data format:(128)0x80:Start;(130)0x82:Control;(145)0x91:Drive Direct;0x00 and 0xC8:the bytes of right velocity;0x00 and 0x00 the bytes of left velocity
    byte[] left = {(byte)0x80,(byte)0x82,(byte)0x91,(byte)0x00,(byte)0xC8,(byte)0x00,(byte)0x00,(byte)0x2B};
    byte[] right = {(byte)0x80,(byte)0x82,(byte)0x91,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xC8,(byte)0x2B};

    //data format:(173)0xAD:Stop
    byte[] stop = {(byte)0xAD,(byte)0x2B};

    /**
    * @Description: 重写父类AppCompatActivity的方法
    * @Param: [savedInstanceState]
    * @return: void
    * @Author: WangPan wangpanhust@qq.com
    * @Date: 2018/11/20 17:05
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定相应控件
        bind();

        //建立ServerSocket
        Server = new buildServer(8000);
        serverSocket = Server.getServer();

        //设置发送信息按钮的监听事件
        btnCon.setOnClickListener(this);
        btnW.setOnClickListener(this);
        btnA.setOnClickListener(this);
        btnS.setOnClickListener(this);
        btnD.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        //此处使用了匿名内部类，在构造器参数后直接跟大括号加具体方法实现
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int realRadius;
                realRadius = 2 * i - 2000;
                tvRadius.setText("Radius:" + Integer.toString(realRadius));

                MainActivity.pair highAndLow = getHighAndLow(realRadius);
                String high = null,low = null;
                high = highAndLow.getHigh();
                low = highAndLow.getLow();

                //字符串转整型，以16进制解析字符串，如high = "12"，则radiusHigh = 18
                radiusHigh = Integer.valueOf(high,16);
                radiusLow = Integer.valueOf(low,16);

                forward[5] = (byte)radiusHigh;
                back[5] = (byte)radiusHigh;

                forward[6] = (byte)radiusLow;
                back[6] = (byte)radiusLow;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbVelocity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int realVelocity,backRealVelocity;
                realVelocity = 2 * i - 500;
                tvVelocity.setText("Velocity:" + Integer.toString(realVelocity));

                MainActivity.pair highAndLow = getHighAndLow(realVelocity);
                String high = null,low = null;
                high = highAndLow.getHigh();
                low = highAndLow.getLow();
                velocityHigh = Integer.valueOf(high,16);
                velocityLow = Integer.valueOf(low,16);

                forward[3] = (byte)velocityHigh;
                left[3] = (byte)velocityHigh;
                right[5] = (byte)velocityHigh;

                forward[4] = (byte)velocityLow;
                left[4] = (byte)velocityLow;
                right[6] = (byte)velocityLow;

                backRealVelocity = 500 - 2 * i;
                MainActivity.pair bHighAndLow = getHighAndLow(backRealVelocity);
                String bHigh = bHighAndLow.getHigh();
                String bLow = bHighAndLow.getLow();
                backVelHigh = Integer.valueOf(bHigh,16);
                backVelLow = Integer.valueOf(bLow,16);
                back[3] = (byte)backVelHigh;
                back[4] = (byte)backVelLow;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
    * @Description: 用来将界面的控件与布局绑定
    * @Param: []
    * @return: void
    * @Author: WangPan wangpanhust@qq.com
    * @Date: 2018/11/20 17:06
    */
    public void bind(){
        btnW = (Button)findViewById(R.id.btnW);
        btnA = (Button)findViewById(R.id.btnA);
        btnS = (Button)findViewById(R.id.btnS);
        btnD = (Button)findViewById(R.id.btnD);
        btnStop = (Button)findViewById(R.id.btnStop);
        btnCon = (Button)findViewById(R.id.btnCon);
        tvStatus = (TextView)findViewById(R.id.tvStatus);
        tvRadius = (TextView)findViewById(R.id.tvRadius);
        tvVelocity = (TextView)findViewById(R.id.tvVelocity);
        sbRadius = (SeekBar)findViewById(R.id.sbRadius);
        sbVelocity = (SeekBar)findViewById(R.id.sbVelocity);
    }

    //静态内部类，可以获得两个返回值
    public static class pair{

        private String high,low;

        public pair(String h,String l){
            high = h;
            low = l;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }
    }

    /**
    * @Description: 求取（速度或半径）的十六进制的高低位
    * @Param: [progress] ：半径或速度的值（十进制）
    * @return: com.example.wangpan.irobot.MainActivity.pair
    * @Author: WangPan wangpanhust@qq.com
    * @Date: 2018/11/20 17:07
    */
    public pair getHighAndLow(int progress){
        int realValue = progress;
        String hex = Integer.toHexString(realValue);
        String high = null,low = null;
        switch (hex.length()){
            case 1:
                high = "00";
                low = "0" + hex;
                break;
            case 2:
                high = "00";
                low = hex;
                break;
            case 3:
                high = "0" + hex.substring(0,1);
                low = hex.substring(1);
                break;
            case 8:
                high = hex.substring(4,6);
                low = hex.substring(6);
                break;
            default:
                break;
        }
        return new pair(high,low);
    }

    /**
    * @Description: 接口View.OnClickListener的重载方法,用于监听按钮
    * @Param: [view]
    * @return: void
    * @Author: WangPan wangpanhust@qq.com
    * @Date: 2018/11/20 17:09
    */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //CONNECT按钮，建立用于通信的socket
            case R.id.btnCon:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //子线程中不能更改UI，可以使用如下方法
                        try {
                            if(!isConneected){
                                runOnUiThread(new changeStatus("Waiting for connecting..."));
                            }
                            socket = serverSocket.accept();
                            isConneected = socket.isConnected();
                            if(isConneected){
                                runOnUiThread(new changeStatus("Connected!"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            //W按钮，发送前进的信息
            case R.id.btnW:
                //在Thread类的构造函数中传入实现了Runnable接口的实例对象
                new Thread(new sendInfo(forward,socket,isConneected)).start();
                break;
            //A按钮，发送左转信息
            case R.id.btnA:
                new Thread(new sendInfo(left,socket,isConneected)).start();
                break;
            //S按钮，发送后退信息
            case R.id.btnS:
                new Thread(new sendInfo(back,socket,isConneected)).start();
                break;
            //D按钮，发送右转信息
            case R.id.btnD:
                new Thread(new sendInfo(right,socket,isConneected)).start();
                break;
            //STOP按钮，发送停止信息
            case R.id.btnStop:
                new Thread(new sendInfo(stop,socket,isConneected)).start();
                break;
            default:
                break;

        }
    }

    //发送信息的子线程，内部类
    public class sendInfo implements Runnable{

        private byte[] info;
        private Socket sendSocket;
        private boolean isConnected;

        public sendInfo(byte[] aInfo,Socket aSocket,boolean connection){
            this.info = aInfo;
            this.sendSocket = aSocket;
            this.isConnected = connection;
        }

        @Override
        public void run() {
            if(isConnected){
                try {
                    OutputStream sendOS = sendSocket.getOutputStream();//获得输出流
                    byte[] sendStringByte = info;
                    sendOS.write(sendStringByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                runOnUiThread(new changeStatus("Please connect server first!"));
            }

        }

    }

    //改变UI的子线程，即改变tvStatus的文本，内部类
    public class changeStatus implements Runnable{

        private String status;

        public changeStatus(String aStatus){
            status = aStatus;
        }

        @Override
        public void run() {
            tvStatus.setText(status);
        }
    }

}

//创建socket通信的服务器端
class buildServer{

    private ServerSocket server;
    //private Socket sendScocket;

    public buildServer(int port){

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServerSocket getServer(){
        return server;
    }
}














