package com.zj.rfid;

import java.util.ArrayList;
import java.util.HashMap;

import com.zj.rfid.MyService.FirstBinder;
import com.zj.rfid.R;
import com.zj.rfid.R.id;
import com.zj.rfid.R.layout;
import com.zj.rfid.R.string;
import com.zj.database.LocalDBHelper;
import com.zj.database.LocalData;
import com.zj.fileutils.Fileutils;
import com.zj.net.ConnectionDetector;
import com.zj.net.ServerSoap;
import com.dkt.activity.Select;
import com.igexin.slavesdk.MessageManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;

public class LoginActivity extends Activity{
	private Button loginButton,cancelButton;
	private EditText userNameEditText;
	private EditText userPasswordEditText;
	private	CheckBox savePassword;
	private DataInit mDataInit;
	private LoginThread mLoginThread;
	private ServerSoap mServerSoap;
	private mHandler dataInitHandler = new mHandler();
	private ProgressDialog mProgressDialog;
	private LocalData mLocalData; 
	private String user;

	public static FirstBinder fb = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mProgressDialog=ProgressDialog.show(LoginActivity.this, "初始化", "请稍后...");
		loginButton = (Button)findViewById(R.id.login);
		cancelButton = (Button)findViewById(R.id.clean);
		userNameEditText = (EditText)findViewById(R.id.usersname);
		userPasswordEditText =(EditText)findViewById(R.id.userspsw);
		savePassword = (CheckBox)findViewById(R.id.savepsw);
		
		//注册登录接收广播
		IntentFilter mIntentFilter = new IntentFilter(ConstDefine.Action_login);
		registerReceiver(loginresultReceiver, mIntentFilter);
		mDataInit = new DataInit();
		mDataInit.start();
		//初始化个推
		MessageManager.getInstance().initialize(this.getApplicationContext());
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				user = userNameEditText.getText().toString();
				String userpsw = userPasswordEditText.getText().toString();
				Intent intent = new Intent(getApplicationContext(), Select.class);
				intent.putExtra("user", "unlock"+","+"001");
				startActivity(intent);
				LoginActivity.this.finish();
				if (!user.equals("")&&!userpsw.equals("")) {
					if(savePassword.isChecked()){
						LocalData.UpdateUser(user,userpsw);
						LocalData.UpdateSetupData("savepwd", "true");
					}else {
						LocalData.UpdateUser(user,null);
						LocalData.UpdateSetupData("savepwd", "flse");
					}
					mProgressDialog=ProgressDialog.show(LoginActivity.this, "正在登录", "请稍后...");
					mProgressDialog.setCancelable(false);
					mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							// TODO Auto-generated method stub
							
							switch (keyCode) {
							case KeyEvent.KEYCODE_BACK:
								
								 if(mProgressDialog.isShowing()){
									 mProgressDialog.dismiss();
								 }
								break;

							default:
								break;
							}
							return false;
						}
					});
					
					mLoginThread = new LoginThread(user,userpsw);
					mLoginThread.start();
				} else {
					AlertDialog.Builder builder = new Builder(LoginActivity.this);
					builder.setTitle("提示");
					builder.setMessage("请输入完整的信息");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							LoginActivity.this.finish();
						}
					});
					builder.create().show();
					
				}

			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StopSer();
				LoginActivity.this.finish();
			}
		}); 


	
	}
	
	private class mHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if (msg.arg1==ConstDefine.DataInitComplete) {

				ArrayList<HashMap<String, String>> mArrayList=LocalData.getsetupinfo();
				for (HashMap<String, String> map : mArrayList) {
					if (map.get("SetupName").equals("savepwd")) {
						if (map.get("SetupData").equals("true")) {
							savePassword.setChecked(true);
						} else {
							savePassword.setChecked(false);
						}
					}
				}
				if(LocalData.getLocalUser()!=null){
				
					userNameEditText.setText(LocalData.getLocalUser());
				}
				if(LocalData.getUserPsw()!=null){
					userPasswordEditText.setText(LocalData.getUserPsw());
				}
				
				if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
				}
				
				if(msg.arg2==ConstDefine.NetStateBreak){
					AlertDialog.Builder builder = new Builder(LoginActivity.this);
					builder.setTitle("网络异常");
					builder.setMessage("请检查设备网络连接状况");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							loginButton.setEnabled(false);
//							LoginActivity.this.finish();
						}
					});
					builder.create().show();
				}
			}else if (msg.arg1==ConstDefine.LoginSuccess) {
				//登录成功
				
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, Function_slect.class);
				intent.putExtra("user", (String)msg.obj+","+user);
				startActivity(intent);
				LoginActivity.this.finish();
			}else if (msg.arg1==ConstDefine.LoginFaild) {
				//登录失败
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				AlertDialog.Builder builder = new Builder(LoginActivity.this);
				builder.setTitle("登录错误");
				builder.setMessage("请输入正确的用户名和密码");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						userPasswordEditText.setText("");
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						LoginActivity.this.finish();
					}
				});
				builder.create().show();
				
			}
		}
		
	}
	

	public class DataInit extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Fileutils mFileutils = new Fileutils();
			String DefaultPath = mFileutils.getDefaultDB_Path(LoginActivity.this);
			ConstDefine.DB_STORAGE_PATH = DefaultPath;
	
			//判断数据库是否存在
			if(!mFileutils.FileisExist(DefaultPath, ConstDefine.LOCAL_DB_NAME)){
				if (ConstDefine.Debug) {
					Log.d("", "数据库不存在，新建数据库");
				}
				
				LocalDBHelper dbHelper = new LocalDBHelper(LoginActivity.this, ConstDefine.DB_STORAGE_PATH + ConstDefine.LOCAL_DB_NAME, null, 1);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.close();
			}else {
		
			}
			//检查网络状态
			ConnectionDetector mConnectionDetector = new ConnectionDetector(LoginActivity.this);
			int netstate = mConnectionDetector.isConnectingToInternet();
//			mLocalData = new LocalData();
			LocalData.openDB();
			try {
				LocalData.Useradjust();
			} catch (Exception e) {
				// TODO: handle exception
				if (ConstDefine.Debug) {
					Log.d("", "useradjust error");
				}
				
			}
			
			Message msg = new Message();
			msg.arg1 = ConstDefine.DataInitComplete;
			msg.arg2 = netstate;
			LoginActivity.this.dataInitHandler.sendMessage(msg);
			
		}
		
	}
	
	public class LoginThread extends Thread{
		private String mUser,mPassword;
		
		public LoginThread(String User,String Password){
			mUser = User;
			mPassword = Password;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			mServerSoap = new ServerSoap(LoginActivity.this,mUser,mPassword);
			//验证用户名，等待广播
			mServerSoap.getUser();
		}
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
			super.destroy();
		}
		
		
	}
	

	BroadcastReceiver loginresultReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			String result = intent.getExtras().getString(ConstDefine.Action_login);
			if(!result.equals("anyType{}")&&!result.equals(ConstDefine.login_faild)){
				//登录成功
				Message msg = new Message();
				msg.arg1 = ConstDefine.LoginSuccess;
				msg.obj = result;
				LoginActivity.this.dataInitHandler.sendMessage(msg);
				
				if (ConstDefine.Debug) {
					Log.d("result", result);	
				}
				
			}else {
				Message msg = new Message();
				msg.arg1 = ConstDefine.LoginFaild;
				LoginActivity.this.dataInitHandler.sendMessage(msg);
				if (ConstDefine.Debug) {
					Log.d("", "result is empty");
				}
				
			}
		}
	};
	
	private void StopSer() {
		try {
			fb.Stop();
			fb.Exit();
		} catch (Exception e) {

		}
		Intent intentSer = new Intent();
		intentSer.setClass(LoginActivity.this, MyService.class);
		stopService(intentSer);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		unregisterReceiver(loginresultReceiver);
		LocalData.closeDB();
		super.finish();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		super.onPause();
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		super.onStop();
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AlertDialog.Builder builder = new Builder(LoginActivity.this);
			builder.setTitle("提示");
			builder.setMessage("确认退出？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					LoginActivity.this.finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
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

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, ConstDefine.MENU_SHOW_ABOUT, 0, R.string.app_about);
        return true;
	}

	private void showAbout() {
		TextView textAbout = new TextView(this);
		textAbout.setText(R.string.about_text);
		textAbout.setTextColor(0xEEFF6347);
		textAbout.setMovementMethod(LinkMovementMethod.getInstance());

        Dialog dlg = new AlertDialog.Builder(this)
            .setTitle(R.string.app_about)
            .setView(textAbout)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
            .create();
        dlg.show();
    }

	public boolean onOptionsItemSelected(MenuItem item) {
		if (ConstDefine.Debug) {
			Log.d("", "get menuitem");	
		}
		
        switch (item.getItemId()) {
        case ConstDefine.MENU_SHOW_ABOUT:
        	showAbout();
        	break;
		default:
			break;			
        }
        return false;
    }

}
