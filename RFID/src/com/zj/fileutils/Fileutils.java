package com.zj.fileutils;

import java.io.File;
import java.io.IOException;

import org.apache.http.entity.FileEntity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class Fileutils {
	/** ����Ĭ�ϵ����ݿ��ļ��洢λ�ã�����Ĭ��Ŀ¼����һ����ʱ���ݿ⣬֮��������db.getpath �����õ�Path*/
	
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
	/** ��ʱ��DBHelper��������Ĭ��Ŀ¼����һ�����ݿ��ļ�������������õ�Ĭ��Ŀ¼��Path��û���ҵ�ֱ�ӵõ�Ĭ��Path�ķ���������ֻ����ô���� */
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
