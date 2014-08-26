package com.zj.rfid;

import android.R.integer;

/**
 * @author Öì¾ü
 *
 * ÉÏÎç12:37:31
 */
public class ConstDefine {
	public static boolean Debug = true;
	public static final int RFU=0x00;
	public static final int EPC=0x01;
	public static final int TID=0x02;
	public static final int USR=0x03;
	public static final int SearchCard=0x10;
	public static final int ByAndroid = 0x04;
	public static final int ByRFID = 0x05;
	public static final int success=0x06;
	public static final int faild=0x07;
	public static final int updatetext=0x08;
	public static final int LoginSuccess = 0x09;
	public static final int LoginFaild = 0x0A;
	public static final int DataInitComplete = 0x0B;
	public static final int NetStateConnected = 0x0C;
	public static final int NetStateBreak = 0x0D;
	public static final int MENU_SHOW_ABOUT = 0x0E;
	public static final int MENU_SHOW_SEARCH = 0x0F;
	public static final int ReadFirmware = 0x12;
	public static final int SetReadMode = 0x13;
	public static final int MENU_SHOW_HELP = 0x14;
	
	
	
	
	/*2013.04.27*/
	public static int RFID_Response_ID = -1;
	public static final int BluetoothState = 0x11;
	
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	///////////
	public static final String Action_NAME = "RFID";
	public static final String Action_SearchCard = "SearchCard";
	public static final String Action_ReadFirmware = "ReadFirmware";
	public static final String Action_SetReadMode = "SetReadMode";
	public static final String Action_RFU = "RFU";
	public static final String Action_EPC = "EPC";
	public static final String Action_TID = "TID";
	public static final String Action_USR = "USR";
	public static final String Action_getRFID = "getRFID";
	public static String DB_STORAGE_PATH =null;
	public static String LOCAL_DB_NAME = "local.sqlite3";
	public static final String Action_login = "login";
	public static final String login_faild = "login_faild";
	public static final String test = "test";
	
	public static boolean AutoQueryFlag = false;
		
	
}
