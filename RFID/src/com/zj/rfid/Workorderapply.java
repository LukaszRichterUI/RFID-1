package com.zj.rfid;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class Workorderapply extends Activity{
	private TextView Title_left;
	private Button return_Button;
	private static String []m = {"����װ��","�������","ר�����","��ӡ���"};
	private ArrayAdapter<String> adapter;
	//int[] msgIds ={R.string.p1,R.string.p2,R.string.p3,R.string.p4};
	int [] textIds =
		{
			R.id.UserNO,
			R.id.UserNa,
			R.id.UserAdd,
			R.id.operator,
			R.id.phoneNo,
		};
	EditText [] textArray;
	private Button submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.apply);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		Intent intent = getIntent();
		String loginUser = intent.getStringExtra("user");
		
		Title_left = (TextView)findViewById(R.id.title_left_text);
		Title_left.setText("�û�:"+loginUser);
		
        Spinner sp=(Spinner)this.findViewById(R.id.workselect);
        //����ѡ������ArrayAdapter��������  
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);  
          
        //���������б�ķ��  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
          
        //��adapter ��ӵ�spinner��  
        sp.setAdapter(adapter);  
/*        BaseAdapter ba=new BaseAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return msgIds.length;//ѡ���ܸ���
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				
				//��ʼ��LinearLayout
				LinearLayout ll=new LinearLayout(Workorderapply.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
				
				TextView tv=new TextView(Workorderapply.this);
				tv.setText(getResources().getText(msgIds[arg0]));//��������
				tv.setTextSize(24);//���������С
				tv.setTextColor(R.color.black);//����������ɫ
				ll.addView(tv);//��ӵ�LinearLayout��
				return ll;
			}
			};
			
	        sp.setAdapter(ba);//ΪSpinner��������������*/
	        
	    return_Button = (Button)findViewById(R.id.back1);
	    return_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Workorderapply.this, Function_slect.class);
				intent.putExtra("user", Title_left.getText().subSequence(Title_left.getText().toString().indexOf(":")+1, Title_left.getText().toString().length()));
				startActivity(intent);
				Workorderapply.this.finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AlertDialog.Builder builder = new Builder(Workorderapply.this);
			builder.setTitle("��ʾ");
			builder.setMessage("ȷ���˳���");
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Workorderapply.this.finish();
						}
					});
			builder.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			builder.create().show();

			break;

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);

	}
}
