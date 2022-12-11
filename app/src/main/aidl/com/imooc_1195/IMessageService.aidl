// IMessageService.aidl
package com.imooc_1195;
import com.imooc_1195.entity.Message;
import com.imooc_1195.MessageReceiveListener;
// Declare any non-default types here with import statements

interface IMessageService {
   void sendMessage(inout Message message);
   void registerMessageReceiveListener(MessageReceiveListener messageReceiveListener);
   void unRegisterMessageReceiveListener(MessageReceiveListener messageReceiveListener);
}