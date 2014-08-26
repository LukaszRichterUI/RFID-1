package com.zj.rfid;

import java.util.ArrayList;
import java.util.List;

import com.zj.net.ServerSoap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class lockdetail extends Activity{
	private TextView Title_left, Title_right;
	private String loginUser = null;
	private String meter = null;
	private String state;
	private String muid = null;
	private String ConnectBtAddress = null;
	private String Taskid = null;
	private String spinner = null;
	private List<String> lockinfo = new ArrayList<String>();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.lockdetail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
		
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		meter = intent.getStringExtra("meter");
		state = intent.getStringExtra("state");
		String TitleUser = loginUser.split(",")[0];
		muid = intent.getStringExtra("user").split(",")[1].split(";")[0];
//		ConnectBtAddress  = intent.getStringExtra("user").split(";")[1];
		Taskid = intent.getStringExtra("Taskid");
		spinner = intent.getStringExtra("spinner");
		Log.d("sp",spinner);
		
		Title_left = (TextView) findViewById(R.id.title_left_text);
		Title_right = (TextView) findViewById(R.id.title_right_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("用户:" + TitleUser);
//		Title_right.setText("设备未连接");
		String a = ServerSoap.GetSetupDetail(Taskid);
		Log.i("unlock", a);
		if(a.equals("nomeg")){
			
		}else{
		String[] temp = a.split("; ");
		Log.i("unlock", "temp--->"+temp.length);
		if(temp.length>1){
			for(int i=1;i<temp.length;i++){
				lockinfo.add("加封位置："+temp[i].split(",")[6]+"\n设备条码："+temp[i].split(",")[10]+
						"\n封印编号："+temp[i].split(",")[3]+"\n封印识别码："+temp[i].split(",")[4]+"\n颜色："+
						temp[i].split(",")[5]+"\n备注："+temp[i].split(",")[12]);}
			
			}else{
				lockinfo.add("没有数据");
			}
		ListView lv=(ListView)this.findViewById(R.id.lockdetaillist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lockinfo);
		lv.setAdapter(adapter);
		
		}
    }    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
				Intent intent = new Intent();
				intent.setClass(lockdetail.this, SetupDetailActivity.class);
				intent.putExtra("user",loginUser);
	    		intent.putExtra("Taskid",Taskid);
				intent.putExtra("meter",meter);
				intent.putExtra("state", state);
				intent.putExtra("spinner", spinner);
				startActivity(intent);
				lockdetail.this.finish();
		}
		return false;
    }
}
