// MessageReceiveListener.aidl
package com.imooc_1195;
import com.imooc_1195.entity.Message;
// Declare any non-default types here with import statements

interface MessageReceiveListener {
   void onReceiveMessage(inout Message message);
}