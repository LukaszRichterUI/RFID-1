package com.zj.rfid;

import com.zj.net.ServerSoap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class showcheck extends Activity{
	private TextView Title_left, Title_right;
	private String temp;
	private String Taskid = null;
	private String loginUser = null;
	private String[] info = null;
	private String[] meterinfo =null;
	private LinearLayout meter_Layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.checkfinish);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
		
		//����intent
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		String TitleUser = loginUser.split(",")[0];
		Taskid = intent.getStringExtra("user").split(";")[1];
		
		//����Title
		Title_left = (TextView) findViewById(R.id.title_left_text);
		Title_right = (TextView) findViewById(R.id.title_right_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("�û�:" + TitleUser);
//		Title_right.setText("�豸δ����");
		
		//����ͷ��textView����
		info = ServerSoap.GetCheckDetail(Taskid).split(",");
		TextView tvBody=(TextView)this.findViewById(R.id.infodetail);
		tvBody.setText("�û��ţ�"+info[1]+"\n�û�����"+info[2]+"\n��ַ��"+info[8]);
		tvBody.setTextSize(20);
		
		meterinfo = ServerSoap.GetCheckDetail(Taskid).split(";");
		meter_Layout = (LinearLayout) findViewById(R.id.meter1_Layout);
		initView();
    }

	private void initView() {
		// TODO Auto-generated method stub
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	    		LinearLayout.LayoutParams.WRAP_CONTENT);
	    for(int i=0;i<meterinfo.length-1;i++){
	    	TextView Target = new TextView(this);
	    	Target.setLayoutParams(lp);
	    	temp = meterinfo[i];
	    	Target.setText("�豸��ţ�"+temp.split(",")[11]+"\n�ӷ�λ�ã�"+temp.split(",")[6]
	    			+"\n��ӡ���룺"+temp.split(",")[3]+"\n״̬��"+temp.split(",")[22]);
	    	Target.setTextSize(20);
	    	meter_Layout.addView(Target);
	    	
		    ImageView line = new ImageView(this);
		    line.setImageResource(R.drawable.line);//setImageBitmap().getResources().getDrawable(R.drawable.line);
		    line.setLayoutParams(lp);
		    meter_Layout.addView(line);
	    
	    }
	} 
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//��дonKeyDown����
    {
    	if(keyCode==4){//�����·��ؼ�
				Intent intent = new Intent();
				intent.setClass(showcheck.this, UsualCheckActivity.class);
				intent.putExtra("user",loginUser.split(";")[0]);
				startActivity(intent);
				showcheck.this.finish();
		}
		return false;
    }

}
