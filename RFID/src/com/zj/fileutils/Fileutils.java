package com.zj.fileutils;

import java.io.File;
import java.io.IOException;

import org.apache.http.entity.FileEntity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class Fileutils {
	/** 返回默认的数据库文件存储位置，先在默认目录构造一个临时数据库，之后再利用db.getpath 方法得到Path*/
	
	public String getDefaultDB_Path(Context context){
		String path = new String();
		TempDBHelper dbHelper = new TempDBHelper(context, "temp", null, 1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		path = db.getPath();
		path = path.substring(0, path.length()-4);
		File file = new File(db.getPath());
		db.close();
		file.delete();
		return path;
	}
	/** 暂时的DBHelper，用来在默认目录产生一个数据库文件，利用这个来得到默认目录的Path（没有找到直接得到默认Path的方法，所以只好这么做） */
	public class TempDBHelper extends SQLiteOpenHelper{

		public TempDBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public boolean FileisExist(String path,String name){
		File file = new File(path, name);
		return file.exists();
	}
	
	public void FileCreatFile(String path,String name){
		File file = new File(path, name);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
