package com.zj.rfid;

import com.zj.net.ServerSoap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SystemSetting extends Activity{
	private EditText editText1;
	private EditText editText2;
	private EditText editText3;
	private EditText editText4;
	private EditText editText5;
	private EditText editText6;
	private EditText editText7;
	private EditText editText8;
	private EditText editText9;
	private EditText editText10;
	private EditText editText11;
	private EditText editText12;
	private EditText editText13;
	private EditText editText14;
	private EditText editText15;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private TextView textview1;
	private String loginUser = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputid);
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
        editText1 = (EditText)findViewById(R.id.editText1);
        editText2 = (EditText)findViewById(R.id.editText2);
        editText3 = (EditText)findViewById(R.id.editText3);
        editText4 = (EditText)findViewById(R.id.editText4);
        editText5 = (EditText)findViewById(R.id.note1);
        editText6 = (EditText)findViewById(R.id.editText6);
        editText7 = (EditText)findViewById(R.id.editText7);
        editText8 = (EditText)findViewById(R.id.editText8);
        editText9 = (EditText)findViewById(R.id.editText9);
        editText10 = (EditText)findViewById(R.id.editText10);
        editText11 = (EditText)findViewById(R.id.editText11);
        editText12 = (EditText)findViewById(R.id.editText12);
        editText13 = (EditText)findViewById(R.id.editText13);
        editText14 = (EditText)findViewById(R.id.editText14);
        editText15 = (EditText)findViewById(R.id.editText15);
        button1 = (Button)findViewById(R.id.btn417);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);
        button5 = (Button)findViewById(R.id.button5);
        textview1 = (TextView)findViewById(R.id.status);	

        button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mTaskID = editText1.getText().toString();
		        String mCustID = editText2.getText().toString();
		        String mCustName = editText3.getText().toString();
		        String mLockSerial = editText4.getText().toString();
		        String mLockNumber = editText5.getText().toString();
		        String mColor = editText6.getText().toString();
		        String mSetPos = editText7.getText().toString();
		        String mSetTime = editText8.getText().toString();
		        String mAddr = editText9.getText().toString();
		        String mSetUserID = editText10.getText().toString();
		        String mMeterSerial = editText11.getText().toString();
		        String mMeterNumber = editText12.getText().toString();
		        String mNote = editText13.getText().toString();
		        String mType = editText14.getText().toString();
		        String OldLockNumber = editText15.getText().toString();
				ServerSoap.SetSetupDetail(mTaskID, mCustID, mCustName, mLockSerial, mLockNumber,
						mColor, mSetPos, mSetTime, mAddr, mSetUserID, mMeterSerial, mMeterNumber, 
						mNote, mType, OldLockNumber);
				Log.d("mTaskID",mTaskID);
				//Log.d("mType",mType);
				textview1.setText(ServerSoap.GetSetupDetail(mTaskID));
			}});
	
        button2.setOnClickListener(new OnClickListener(){

    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			String mTaskID = editText1.getText().toString();
    	        String mCustID = editText2.getText().toString();
    	        String mCustName = editText3.getText().toString();
    	        String mLockSerial = editText4.getText().toString();
    	        String mLockNumber = editText5.getText().toString();
    	        String mColor = editText6.getText().toString();
    	        String mSetPos = editText7.getText().toString();
    	        String mSetTime = editText8.getText().toString();
    	        String mAddr = editText9.getText().toString();
    	        String mSetUserID = editText10.getText().toString();
    	        String mMeterSerial = editText11.getText().toString();
    	        String mMeterNumber = editText12.getText().toString();
    	        String mNote = editText13.getText().toString();
    	        String mType = editText14.getText().toString();
    	        String OldLockNumber = editText15.getText().toString();
//    			ServerSoap.SetSetupDetail(mTaskID, mCustID, mCustName, mLockSerial, mLockNumber,
    					//mColor, mSetPos, mSetTime, mAddr, mSetUserID, mMeterSerial, mMeterNumber, 
    					//mNote, mType, OldLockNumber);
    			//Log.d("mTaskID",mTaskID);
    			//Log.d("mType",mType);
    			textview1.setText(ServerSoap.GetTaskList(mSetUserID , mType));
    		}});


}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
				Intent intent = new Intent();
				intent.setClass(SystemSetting.this, Function_slect.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				SystemSetting.this.finish();
		}
		return false;
    }
}

