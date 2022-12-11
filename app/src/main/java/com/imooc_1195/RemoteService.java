package com.imooc_1195;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imooc_1195.entity.Message;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 子进程
 */
public class RemoteService extends Service {

    private static final String TAG = "RemoteService";
    private boolean isConnected;

    private ScheduledExecutorService scheduledExecutorService;

    private ScheduledFuture scheduledFuture;

    private RemoteCallbackList<MessageReceiveListener> remoteCallbackList = new RemoteCallbackList<>();





    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            Messenger client = msg.replyTo;
            bundle.setClassLoader(Message.class.getClassLoader());
            Message message = bundle.getParcelable("message");
            showToast(message.getContent());
            Message reply = new Message();
            reply.setContent("message reply from remote");
            android.os.Message data = new android.os.Message();
            Bundle bundleReply = new Bundle();
            bundleReply.putParcelable("message",message);
            data.setData(bundleReply);
            try {
                client.send(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }
    };

    private Messenger messenger = new Messenger(handler);

    private int count ;

    IServiceManager iServiceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if(IConnectService.class.getSimpleName().equals(serviceName)){
                Log.d(TAG,serviceName);
                return connectService.asBinder();
            }else if(IMessageService.class.getSimpleName().equals(serviceName)){
                Log.d(TAG,serviceName);
                return iMessageService.asBinder();
            }else if(Messenger.class.getSimpleName().equals(serviceName)){
                return messenger.getBinder();
            }
            return null;
        }

//        @Override
//        public IBinder asBinder() {
//            return iServiceManager.asBinder();
//        }
    };




    private IConnectService connectService = new IConnectService.Stub() {
        @Override
        public void connect() throws RemoteException {
            Log.e(TAG,"CurrentThread:"+Thread.currentThread().getName());
            try {
                Thread.sleep(5000);
                isConnected = true;
                showToast("连接成功");
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    int size = remoteCallbackList.beginBroadcast();
                    Log.d(TAG,"size = "+size);
                    for(int i = 0;i< size;i++){
                        Message message = new Message();
                        message.setContent("这是来自Remote 的消息:"+(count++));
                        Log.d(TAG,message.toString());
                        try {
                            remoteCallbackList.getBroadcastItem(i).onReceiveMessage(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    remoteCallbackList.finishBroadcast();


                }
            },5000,5000, TimeUnit.MILLISECONDS);

        }

        @Override
        public void disConnect() throws RemoteException {
            showToast("断开连接");
            isConnected = false;
        }

        @Override
        public boolean isConnect() throws RemoteException {
            return isConnected;
        }
    };

    IMessageService iMessageService = new IMessageService.Stub() {
        @Override
        public void sendMessage(Message message) throws RemoteException {
            showToast(message.getContent());
            if(isConnected){
                message.setSendSuccess(true);
            }else {
                message.setSendSuccess(false);
            }


        }

        @Override
        public void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener) throws RemoteException {
            Log.d(TAG,"messageReceiveListener");
            remoteCallbackList.register(messageReceiveListener);
        }

        @Override
        public void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener) throws RemoteException {
            remoteCallbackList.unregister(messageReceiveListener);

        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iServiceManager.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scheduledFuture.cancel(true);

    }

    private void  showToast(String str){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RemoteService.this,str,Toast.LENGTH_LONG).show();
            }
        });

    }
}