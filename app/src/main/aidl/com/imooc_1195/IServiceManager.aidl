// IServiceManager.aidl
package com.imooc_1195;

// Declare any non-default types here with import statements

interface IServiceManager {
   IBinder getService(String serviceName);
}