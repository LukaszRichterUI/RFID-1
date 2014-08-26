package com.zj.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


import android.R.bool;
import android.R.integer;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.text.StaticLayout;
import android.util.Log;
import android.widget.Toast;
import android.content.BroadcastReceiver;

public class RFIDOperate {
	private OutputStream outStream = null;
	private InputStream InStream = null;
	BluetoothSocket mbtSocket;
	Context mContext;
	private int mMode;
	private int mtype,mstart,mlength;
	//2013.04.27
//	private BluetoothService mBluetoothService = null;
	
	public RFIDOperate(Context context, BluetoothSocket btSocket,
			BluetoothDevice device) {
		mbtSocket = btSocket;
		mContext = context;
		
//		mBluetoothService = bluetoothService;
	}
	
	public void SearchCard() throws IOException{
		Thread searchThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				result=CmdHeadConstuctor(0x09,0x10,null);
				byte[] msgBuffer = HexString2Bytes(result);
				ConstDefine.RFID_Response_ID = ConstDefine.SearchCard;
				sendMessage(msgBuffer);
			}
		});
		searchThread.start();
	}
	
	public void ReadData(int type,int start,int length){
        this.mtype = type;
        this.mstart = start;
        this.mlength =length;
		
		Thread readDateThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				String data = null;
				switch (mtype) {
				case ConstDefine.RFU:
					int CmdData0[]={ConstDefine.RFU,0x00,mstart,mlength};
					ConstDefine.RFID_Response_ID = ConstDefine.RFU;
					result=CmdHeadConstuctor(0x0D,0x12,CmdData0);
					break;
				case ConstDefine.EPC:
					int CmdData1[]={ConstDefine.EPC,0x00,mstart,mlength};
					ConstDefine.RFID_Response_ID = ConstDefine.EPC;
					result=CmdHeadConstuctor(0x0D,0x12,CmdData1);
					break;
				case ConstDefine.TID:
					int CmdData3[]={ConstDefine.TID,0x00,mstart,mlength};
					ConstDefine.RFID_Response_ID = ConstDefine.TID;
					result=CmdHeadConstuctor(0x0D,0x12,CmdData3);
					break;
				case ConstDefine.USR:
					int CmdData4[]={ConstDefine.USR,0x00,mstart,mlength};
					ConstDefine.RFID_Response_ID = ConstDefine.USR;
					result=CmdHeadConstuctor(0x0D,0x12,CmdData4);
					break;
				default:
					break;
				}
				
				Log.d("send cmd", result);
				byte[] msgBuffer = HexString2Bytes(result);
				sendMessage(msgBuffer);
			}
		});
		readDateThread.start();
		
	}

	public void ReadFirmware(){
		Thread ReadFirmware = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				result=CmdHeadConstuctor(0x09,0x14,null);
				byte[] msgBuffer = HexString2Bytes(result);
				ConstDefine.RFID_Response_ID = ConstDefine.ReadFirmware;
				sendMessage(msgBuffer);
			}
		});
		ReadFirmware.start();
		
	}
	
	public void SetReadMode(int Mode){
		mMode = Mode;
		Thread SetMode = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = null;
				if(mMode==ConstDefine.ByAndroid){
					result=CmdHeadConstuctor(0x09,0x18,null);
				}else if(mMode==ConstDefine.ByRFID){
					result=CmdHeadConstuctor(0x09,0x16,null);
				}
				
				byte[] msgBuffer = HexString2Bytes(result);
				ConstDefine.RFID_Response_ID = ConstDefine.SetReadMode;
				sendMessage(msgBuffer);
			}
		});
		SetMode.start();		
	}
	
	public String CmdHeadConstuctor(int length,int type,int data[]){
		StringBuffer result = new StringBuffer();
		result = result.append("55");
		result = result.append("AA");
		for(int i=0;i<4;i++){
			if (i!=3) {
				result = result.append(encode(length/(256*(3-i))));
				length = length%(256*(3-i));
			}else {
				result = result.append(encode(length));
			}
		}
		result.append(encode(type));
		if(data!=null){
			for(int i=0;i<data.length;i++){
				result.append(encode(data[i]));
			}
		}
		result = result.append("AA");		
		result = result.append("55");
		//String returnString = String.valueOf(result);
		return result.toString();
	}
	
	public static String bytes2HexString(byte[] b) {  
	    String ret = "";  
	    for (int i = 0; i < b.length; i++) {  
	        String hex = Integer.toHexString(b[ i ] & 0xFF);  
	    if (hex.length() == 1) {  
	        hex = '0' + hex;  
	    }  
	     ret += hex.toUpperCase();  
	  }  
	  return ret;  
	}  
	
	public static byte uniteBytes(byte src0, byte src1) {	
		    byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();		
		    _b0 = (byte)(_b0 << 4);		
		    byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();	
		    byte ret = (byte)(_b0 ^ _b1);		
		    return ret;		
		}

	public static byte[] HexString2Bytes(String src){
		    byte[] ret = new byte[src.length()/2];
		    //Log.d("string length", String.valueOf(src.length()));
		    byte[] tmp = src.getBytes();
		    for(int i=0; i<src.length()/2; i++){	
		        ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);	
		    }	
		    return ret;
		}	
	
	public String encode(int value) {
		   // 根据默认编码获取字节数组
		   final String hexString = "0123456789ABCDEF"; //此处不可随意改动
		   value =value & 0xff;
		   StringBuffer result = new StringBuffer();
		   result=result.append(hexString.charAt(value/16));
		   result=result.append(hexString.charAt(value%16));
		   
		   return result.toString();
	}

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
	private void sendMessage(byte[] message) {
		if (Function_slect.fb.Gets() != BluetoothService.STATE_CONNECTED) {
			Log.d("", "蓝牙没有连接");
			return;
		}
		Function_slect.fb.Write(message);
	}
/*    private void sendMessage(byte[] message) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
        	Log.d("", "蓝牙没有连接");
            return;
        }
        // Check that there's actually something to send
            // Get the message bytes and tell the BluetoothChatService to write
            mBluetoothService.write(message);
    }*/
}
