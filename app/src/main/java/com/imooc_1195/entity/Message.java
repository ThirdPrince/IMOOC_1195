package com.imooc_1195.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

     private String content;
     private boolean isSendSuccess;

     public void setContent(String content) {
          this.content = content;
     }

     public String getContent() {
          return content;
     }

     public void setSendSuccess(boolean sendSuccess) {
          isSendSuccess = sendSuccess;
     }

     public boolean isSendSuccess() {
          return isSendSuccess;
     }

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeString(this.content);
          dest.writeByte(this.isSendSuccess ? (byte) 1 : (byte) 0);
     }

     public void readFromParcel(Parcel source) {
          this.content = source.readString();
          this.isSendSuccess = source.readByte() != 0;
     }

     public Message() {
     }

     protected Message(Parcel in) {
          this.content = in.readString();
          this.isSendSuccess = in.readByte() != 0;
     }

     public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
          @Override
          public Message createFromParcel(Parcel source) {
               return new Message(source);
          }

          @Override
          public Message[] newArray(int size) {
               return new Message[size];
          }
     };

     @Override
     public String toString() {
          return "Message{" +
                  "content='" + content + '\'' +
                  ", isSendSuccess=" + isSendSuccess +
                  '}';
     }
}
