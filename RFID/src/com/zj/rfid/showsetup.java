package com.zj.rfid;

import java.util.ArrayList;
import java.util.List;

import com.zj.net.ServerSoap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class showsetup extends Activity{
	private TextView Title_left, Title_right;
	private String[] info = null;
	private String loginUser = null;
	private String Taskid = null;
	private List<String> lockinfo = new ArrayList<String>();
	
	public void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.showsetup);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.custom_title);
			
			Intent intent = getIntent();
			loginUser = intent.getStringExtra("user");
			String TitleUser = loginUser.split(",")[0];
			Taskid = intent.getStringExtra("Taskid");
			Title_left = (TextView) findViewById(R.id.title_left_text);
			Title_right = (TextView) findViewById(R.id.title_right_text);
			TextView textView = (TextView) findViewById(R.id.title_textview1);
			textView.setVisibility(View.INVISIBLE);
			Title_left.setText("用户:" + TitleUser);
//			Title_right.setText("设备未连接");
			
			info = ServerSoap.GetSetupDetail(Taskid).split("; ");
			TextView tv=(TextView)this.findViewById(R.id.tv);
			tv.setText("用户号："+info[0].split(",")[1]+"\n用户名："+info[0].split(",")[2]+"\n地址："+info[0].split(",")[8]);
			tv.setTextSize(20);
			
			for(int i=1;i<info.length;i++){
				lockinfo.add("加封位置："+info[i].split(",")[6]+"\n设备条码："+info[i].split(",")[10]+
						"\n封印编号："+info[i].split(",")[3]+"\n封印识别码："+info[i].split(",")[4]+"\n颜色："+
						info[i].split(",")[5]+"\n备注："+info[i].split(",")[12]);}
			ListView lv=(ListView)this.findViewById(R.id.list);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lockinfo);
			lv.setAdapter(adapter);
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
				Intent intent = new Intent();
				intent.setClass(showsetup.this, SetupActivity.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				showsetup.this.finish();
		}
		return false;
    }
}
