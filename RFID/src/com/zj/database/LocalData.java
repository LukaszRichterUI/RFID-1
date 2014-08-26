package com.zj.database;

import java.util.ArrayList;
import java.util.HashMap;

import com.zj.rfid.ConstDefine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalData {
	
	private static SQLiteDatabase db; //= SQLiteDatabase.openDatabase(ConstDefine.DB_STORAGE_PATH + ConstDefine.LOCAL_DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	
	
	
	public static synchronized void openDB(){
		db = SQLiteDatabase.openDatabase(ConstDefine.DB_STORAGE_PATH + ConstDefine.LOCAL_DB_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
	
	/** 关闭数据库 */
	public static synchronized void closeDB(){
		db.close();
	}
	
	public static synchronized String getLocalUser(){
		String User = null;
		Cursor cursor = db.query("User", null, null, null, null, null, null);
		if(cursor.moveToNext()){
			User = cursor.getString(cursor.getColumnIndex("UserName"));
		}
		cursor.close();
		return User;
	}
	
	public static synchronized String getUserPsw(){
		String Password = null;
		Cursor cursor = db.query("User", null, null, null, null, null, null);
		if(cursor.moveToNext()){
			Password = cursor.getString(cursor.getColumnIndex("Password"));
		}
		cursor.close();
		return Password;
	}
    /*根据配置选项更新配置*/
    public static synchronized boolean UpdateUser(String UserName,String Password){
    	ContentValues values = new ContentValues();
    	values.put("UserName", UserName);
    	values.put("Password", Password);
    	db.update("User", values, "ID=?", new String[]{"1"});
    	return true;
    }
    
    public static synchronized void Useradjust(){
    	Cursor cursor = db.query("User", null, null, null, null, null, null);
    	
		if(cursor.moveToNext()){
			
		}else {
			ContentValues values = new ContentValues();
	    	values.put("ID", 1);
	    	db.insert("User", null, values);
		}
		cursor.close();
    }

	/*获取设置配置*/
    public static synchronized ArrayList<HashMap<String, String>> getsetupinfo(){
    	ArrayList<HashMap<String, String>> mArrayList = new ArrayList<HashMap<String, String>>();
    	Cursor cursortest = db.query("setup",null, null, null, null, null, null);
    	//判断查询是否存在设置表内容
    	if (!cursortest.moveToNext()) {
    		//初始化配置
    		Log.d("database", "init setup table!"+String.valueOf(cursortest.getCount()));
    		ContentValues values = new ContentValues();
        	values.put("SetupName","savepwd");
        	values.put("SetupData", "true");
        	db.insert("setup", null, values);
        	cursortest.close();
		}else {
			cursortest.close();
		}
    	Cursor cursor = db.query("setup",null, null, null, null, null, null);
    	Log.d("cursor size", String.valueOf(cursor.getCount()));
    	while(cursor.moveToNext()){
    		Log.d("database", "get database setup");
    		HashMap<String, String> map = new HashMap<String, String>();
    		map.put("SetupName", cursor.getString(cursor.getColumnIndex("SetupName")));
    		map.put("SetupData", cursor.getString(cursor.getColumnIndex("SetupData")));
    		mArrayList.add(map);
    	}
    	cursor.close();
    	Log.d("database", "getsetupinfo");
    	return mArrayList;
    }
    /*根据配置选项更新配置*/
    public static synchronized boolean UpdateSetupData(String SetupName,String SetupData){
    	ContentValues values = new ContentValues();
    	Log.d("database", "update setupdata");
    	values.put("SetupData", SetupData);
    	db.update("setup", values, "SetupName=?", new String[]{SetupName});
    	return true;
    }



}
