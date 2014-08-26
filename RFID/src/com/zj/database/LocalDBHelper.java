package com.zj.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBHelper extends SQLiteOpenHelper{

	public LocalDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table User (ID INTEGER PRIMARY KEY NOT NULL, UserName VARCHAR, Password VARCHAR)");
		db.execSQL("create table setup (ID INTEGER PRIMARY KEY NOT NULL, SetupName VARCHAR, SetupData VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
