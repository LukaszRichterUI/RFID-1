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
			Title_left.setText("�û�:" + TitleUser);
//			Title_right.setText("�豸δ����");
			
			info = ServerSoap.GetSetupDetail(Taskid).split("; ");
			TextView tv=(TextView)this.findViewById(R.id.tv);
			tv.setText("�û��ţ�"+info[0].split(",")[1]+"\n�û�����"+info[0].split(",")[2]+"\n��ַ��"+info[0].split(",")[8]);
			tv.setTextSize(20);
			
			for(int i=1;i<info.length;i++){
				lockinfo.add("�ӷ�λ�ã�"+info[i].split(",")[6]+"\n�豸���룺"+info[i].split(",")[10]+
						"\n��ӡ��ţ�"+info[i].split(",")[3]+"\n��ӡʶ���룺"+info[i].split(",")[4]+"\n��ɫ��"+
						info[i].split(",")[5]+"\n��ע��"+info[i].split(",")[12]);}
			ListView lv=(ListView)this.findViewById(R.id.list);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lockinfo);
			lv.setAdapter(adapter);
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//��дonKeyDown����
    {
    	if(keyCode==4){//�����·��ؼ�
				Intent intent = new Intent();
				intent.setClass(showsetup.this, SetupActivity.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				showsetup.this.finish();
		}
		return false;
    }
}
