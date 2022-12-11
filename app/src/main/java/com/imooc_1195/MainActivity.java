package com.imooc_1195;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.imooc_1195.entity.Message;

public class MainActivity extends AppCompatActivity {


    private IConnectService iConnectService;

    private AppCompatButton connectBtn;

    private AppCompatButton disConnectBtn;

    private AppCompatButton isDisConnectBtn;

    private AppCompatButton registerBtn;


    private IMessageService messageServiceProxy;

    private IServiceManager iServiceManager;


    private Messenger messengerProxy;


    /**
     * remote 返回
     */
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            bundle.setClassLoader(Message.class.getClassLoader());
            Message message = bundle.getParcelable("message");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,message.getContent(),Toast.LENGTH_LONG).show();

                }
            },3000);

        }
    };

    private Messenger client = new Messenger(handler);






    private AppCompatButton messenger_btn;


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iServiceManager = IServiceManager.Stub.asInterface(service);
            try {
                iConnectService = IConnectService.Stub.asInterface(iServiceManager.getService(IConnectService.class.getSimpleName()));
                messageServiceProxy = IMessageService.Stub.asInterface(iServiceManager.getService(IMessageService.class.getSimpleName()));
                messengerProxy = new Messenger(iServiceManager.getService(Messenger.class.getSimpleName()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iConnectService = null;

        }
    };


    private MessageReceiveListener messageReceiveListener = new MessageReceiveListener.Stub() {
        @Override
        public void onReceiveMessage(Message message) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,message.getContent(),Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindService();


    }

    private void initView(){
        connectBtn = findViewById(R.id.connect_btn);
        disConnectBtn = findViewById(R.id.disconnect_btn);
        isDisConnectBtn = findViewById(R.id.isConnected_btn);
        registerBtn = findViewById(R.id.register_btn);
        messenger_btn = findViewById(R.id.messenger_btn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iConnectService.connect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        disConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iConnectService.disConnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        isDisConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   boolean isConnect = iConnectService.isConnect();
                    isDisConnectBtn.setText(isConnect+"");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    messageServiceProxy.registerMessageReceiveListener(messageReceiveListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                Message message = new Message();
                message.setContent("this is from client");
                try {
                    messageServiceProxy.sendMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        messenger_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setContent("this is from messenger client");
                android.os.Message data = android.os.Message.obtain();
                data.replyTo = client;
                Bundle bundle = new Bundle();
                bundle.putParcelable("message",message);
                data.setData(bundle);
                try {
                    messengerProxy.send(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    /**
     * 绑定remoteService
     */
    private void bindService(){
        Intent intent = new Intent(MainActivity.this, RemoteService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }
}