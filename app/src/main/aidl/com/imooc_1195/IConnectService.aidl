// IConnectService.aidl
package com.imooc_1195;

// Declare any non-default types here with import statements

interface IConnectService {

  oneway void connect();
  void disConnect();
  boolean isConnect();
}