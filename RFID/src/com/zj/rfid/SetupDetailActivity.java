package com.zj.rfid;
import java.util.Date;
import java.text.SimpleDateFormat;    

import com.zj.net.ServerSoap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemSelectedListener;

public class SetupDetailActivity extends Activity{
	private TextView Title_left, Title_right;
	private String[] info = null;
	private String loginUser = null;
	private String muid = null;
	private static String ConnectBtAddress = null;
	private String Taskid = null;
	private String spinner = null;
	private String name = "SetupDetailActivity";
	private int sp1;
//	private int sp2;
	private String text;
	private String state;
	private Spinner Equsp;
	private Spinner Locsp;
	private String meter = null;
	private String Equ;
	private String Loc = "表耳";
	private static String[] m = {"电能表","互感器","电表箱"};
	private static String[] s = {"表耳","接线盒","箱门","接线端子","其他"};
	private static String[] s0 = {"箱门","其他"};
	private static String[] s1 = {"箱门","其他"};
	private ArrayAdapter<String> adapter;
	private EditText EquNo;
	private EditText lockserial;
	private EditText locknumber;
	private EditText color;
	private EditText note1;
	private Button detail;
	private Button scan;	
	private Button GET;
	private Button clean;
	private Button delet;
	private Button save;
	private Button submit;
	private BluetoothService mBluetoothService;
	private RFIDOperate mRfidOperate;
	private BluetoothSocket btSocket = null;
	private BluetoothDevice device;
	private ProgressDialog mProgressDialog;
	private BluetoothAdapter mBtAdapter;
	private getRFIDresultHandler mgetRFIDresultHandler = new getRFIDresultHandler();
	private BtresultHandler mBtresultHandler = new BtresultHandler();
	private String ConnectResult = "ConnectResult";
	private String BluetoothState = "BluetoothState";
	private funhandler funchandler = new funhandler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.setupdetail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
		//loginUser="用户名,ID;蓝牙地址"
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		meter = intent.getStringExtra("meter");
		state = intent.getStringExtra("state");
		spinner = intent.getStringExtra("spinner");
		String TitleUser = loginUser.split(",")[0];
		muid = intent.getStringExtra("user").split(",")[1];
//		ConnectBtAddress = intent.getStringExtra("user").split(";")[1];
//		Log.d("wtf",ConnectBtAddress);
		Taskid = intent.getStringExtra("Taskid");
		if(spinner.equals("nomeg")){

		}else{
			sp1 = Integer.parseInt(spinner);
		}

		
		
//		mBluetoothService = new BluetoothService(SetupDetailActivity.this, mTitleHandler);
		Title_left = (TextView) findViewById(R.id.title_left_text);
		Title_right = (TextView) findViewById(R.id.title_right_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("用户:" + TitleUser);
		Title_right.setText("设备未连接");
		


		info = ServerSoap.GetSetupDetail(Taskid).split(",");
		
		TextView tvBody=(TextView)this.findViewById(R.id.body1);
		tvBody.setText("用户号："+info[1]+"\n用户名："+info[2]+"\n地址："+info[8]);
		tvBody.setTextSize(20);
		
		
		detail = (Button)this.findViewById(R.id.detail);
		detail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SetupDetailActivity.this, lockdetail.class);
				intent.putExtra("user",loginUser);
				intent.putExtra("Taskid",Taskid);
				intent.putExtra("meter", EquNo.getText().toString());
				intent.putExtra("state", state);
				intent.putExtra("spinner", String.valueOf(sp1));
				intent.putExtra("name", name);
				startActivity(intent);
				SetupDetailActivity.this.finish();
			}
		});
		
		Equsp=(Spinner)this.findViewById(R.id.EquType);
		Locsp=(Spinner)this.findViewById(R.id.lockloc);		
        //将可选内容与ArrayAdapter连接起来  
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);  
          
        //设置下拉列表的风格  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        
        //将adapter 添加到spinner中  
        Equsp.setAdapter(adapter);  
        Equsp.setSelection(sp1);
        Equsp.setOnItemSelectedListener(new SpinnerSelectedListener());
          
        //添加事件Spinner事件监听    
        Locsp.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Loc = Locsp.getSelectedItem().toString();
				Log.d("Equ+Loc",Equ+Loc);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });

        //Loc = Locsp.getSelectedItem().toString();//*/
        
        //设置默认值  
        //Equsp.setVisibility(View.VISIBLE); 
		mBluetoothService = new BluetoothService(SetupDetailActivity.this, mBtresultHandler);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		IntentFilter filter = new IntentFilter(ConstDefine.Action_getRFID);
		registerReceiver(getRFIDReceiver, filter);

		filter = new IntentFilter(ConstDefine.Action_NAME);
        registerReceiver(RFIDreceiver, filter);
		// 注册Service蓝牙连接广播
		filter = new IntentFilter(ConnectResult);
		registerReceiver(mReceiver, filter);
		// 注册Service蓝牙连接状态广播
		filter = new IntentFilter(BluetoothState);
		registerReceiver(mReceiver, filter);
       
        EquNo=(EditText)this.findViewById(R.id.EquNo);
        EquNo.setText(meter);
        lockserial=(EditText)this.findViewById(R.id.lockserial);
        locknumber=(EditText)this.findViewById(R.id.locknumber);
        color=(EditText)this.findViewById(R.id.color);
        note1=(EditText)this.findViewById(R.id.note1);
        scan = (Button)this.findViewById(R.id.scan);
        if(state.equals("doing")){
        	
        }else{scan.setEnabled(false);}
        scan.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SetupDetailActivity.this, CaptureActivity.class);
				intent.putExtra("name","SetupDetailActivity");
				intent.putExtra("Taskid",Taskid);
				intent.putExtra("user",loginUser);
				intent.putExtra("state", state);
				intent.putExtra("spinner", String.valueOf(sp1));
				startActivity(intent);
				SetupDetailActivity.this.finish();
			}
		});
		mRfidOperate = new RFIDOperate(SetupDetailActivity.this, btSocket, device);        
		GET=(Button)this.findViewById(R.id.get);
		GET.setEnabled(false);
		GET.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Function_slect.fb.Gets() == ConstDefine.STATE_CONNECTED) {//mBluetoothService.getState()==ConstDefine.STATE_CONNECTED){
					DisplayToast("数据读取中，请等待...");
					ConstDefine.AutoQueryFlag = true;
					mRfidOperate.ReadData(ConstDefine.TID, 0, 6);
				}else{
					AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
					builder.setTitle("提示");
					builder.setMessage("蓝牙未连接，请连接蓝牙设备");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
						});
					builder.create().show();
				}
			}
		});
		
		clean=(Button)this.findViewById(R.id.clean);
		clean.setEnabled(false);
		clean.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
				builder.setTitle("警告！");
				builder.setMessage("本次操作将会删除任务中的所有数据");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ServerSoap.SetSetupDetail(Taskid, null, null, null, null, null, null, null,
								null, null, null, null, null, String.valueOf(2), null);
						ServerSoap.SetSetupDetail(Taskid, info[1], info[2], null, null, null, null, null,
								info[8], muid, null, null, null, String.valueOf(0), null);
					}
				});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					    // TODO Auto-generated method stub
						
					}
				});
				builder.create().show();
			}
			});
		
	    delet=(Button)this.findViewById(R.id.delet);
	    delet.setEnabled(false);
	    delet.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
				builder.setTitle("您正在清空本次填写 的数据，若没有保存数据将丢失");
				builder.setMessage("是否确定清空？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EquNo.setText("");
						lockserial.setText("");
						locknumber.setText("");
						color.setText("");
						note1.setText("无");
					}
				});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					    // TODO Auto-generated method stub

					}
				});
				builder.create().show();
			}
		});
	    
	    save=(Button)this.findViewById(R.id.save);
	    save.setEnabled(false);
	    save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss");       
				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
				String    str    =    formatter.format(curDate);       
				String mmeternumber = EquNo.getText().toString();
				String mlockserial = lockserial.getText().toString();
				String mlocknumber = locknumber.getText().toString();
				String mcolor = color.getText().toString();
				String mnote = note1.getText().toString();
				String msetpos = Equ+Loc;
				if("".equals(mmeternumber.trim())&&"".equals(mlockserial.trim())&&"".equals(mlocknumber.trim())
						&&"".equals(mcolor.trim())){
//					DisplayToast("请填写好数据");
					AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
					builder.setTitle("提示！");
					builder.setMessage("请填写好相关数据");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});

					builder.create().show();
				}else{
				ServerSoap.SetSetupDetail(Taskid, info[1], info[2], mlockserial, mlocknumber, mcolor, msetpos, str, null, muid,
						mmeternumber, mmeternumber, mnote, String.valueOf(0), null);
//				DisplayToast("保存数据成功");
				AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
				builder.setTitle("提示！");
				builder.setMessage("保存数据成功");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});

				builder.create().show();
						}
			}
		});
	    
	    submit=(Button)this.findViewById(R.id.submit);
	    submit.setEnabled(false);
	    submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
				builder.setTitle("注意！");
				builder.setMessage("提交的单据将不能再次编辑");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ServerSoap.UPSetupDetail(Taskid, muid);
						GET.setEnabled(false);
						clean.setEnabled(false);
						delet.setEnabled(false);
						save.setEnabled(false);
						submit.setEnabled(false);
						EquNo.setText("");
						lockserial.setText("");
						locknumber.setText("");
						color.setText("");
						note1.setText("");
						DisplayToast("提交成功！");
					}
				});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					    // TODO Auto-generated method stub

					}
				});
				builder.create().show();
				
			}
		});

		if (Function_slect.fb == null) {
			Title_right.setText("设备未连接");
		} else {
			int s = Function_slect.fb.Gets();
			if (s == BluetoothService.STATE_NONE) {
				Title_right.setTextColor(Color.RED);
				Title_right.setText("设备未连接");
			} else if (s == BluetoothService.STATE_LISTEN) {
				Title_right.setText("设备正在监听");
			} else if (s == BluetoothService.STATE_CONNECTING) {
				Title_right.setText("设备正在连接");
				Title_right.setTextColor(Color.BLUE);
			} else if (s == BluetoothService.STATE_CONNECTED) {
				Title_right.setTextColor(Color.GREEN);
				Title_right.setText("设备已连接");
		    	if(state.equals("doing")){
		    		GET.setEnabled(true);
		    	    clean.setEnabled(true);
				    delet.setEnabled(true);
				    save.setEnabled(true);
				    submit.setEnabled(true);}
			}
		}
//		connectBt2server();

		
    }
    


	public class BtresultHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch (msg.what){
			case ConstDefine.success:
				DisplayToast("成功连接到设备");
				Title_right.setText("设备连接成功");
				mRfidOperate = new RFIDOperate(SetupDetailActivity.this, btSocket,device);//new RFIDOperate(SetupDetailActivity.this,btSocket,mBtresultHandler,device,mBluetoothService);
				if(state.equals("doing")){
				GET.setEnabled(true);
				clean.setEnabled(true);
				delet.setEnabled(true);
				save.setEnabled(true);
				submit.setEnabled(true);}
				break;
			case ConstDefine.faild:
//				DisplayToast("连接失败");
				Function_slect.fb.Stop();
//				mBluetoothService.stop();
				break;
			case ConstDefine.BluetoothState:

				switch (msg.arg1) {
				case ConstDefine.STATE_NONE:
						Title_right.setText("设备未连接");
						Title_right.setTextColor(Color.RED);
					break;
				case ConstDefine.STATE_LISTEN:
					Title_right.setText("设备正在监听");
					break;
				case ConstDefine.STATE_CONNECTING:
					Title_right.setText("设备正在连接");
					Title_right.setTextColor(Color.BLUE);
					break;
				case ConstDefine.STATE_CONNECTED:
					Title_right.setTextColor(Color.GREEN);
					Title_right.setText("设备已连接");
					break;

				default:
					break;
				}
			break;
				
			case ConstDefine.updatetext:
				locknumber.setText(msg.obj.toString());
			break;
			}
		}
	}
/*	public void connectBt2server(){ 
		if (ConstDefine.Debug) {
			Log.d("start connect to server!", "1");
		}
	    if(ConnectBtAddress.equals("null")){
	    	if(state.equals("doing")){
    	    clean.setEnabled(true);
		    delet.setEnabled(true);
		    save.setEnabled(true);
		    submit.setEnabled(true);}
		    ConnectBtAddress = "00:1F:B7:05:03:1A";
	    }
			if(ConnectBtAddress.equals(null)){
				if (ConstDefine.Debug) {
					Log.d("", "ConnectBtAddress is null");	
				}
				
				ConnectBtAddress = "00:1F:B7:05:03:1A";
			}
			if (ConstDefine.Debug) {
				Log.d("ConnectBtAddress", ConnectBtAddress);
			}
			
			device = mBtAdapter.getRemoteDevice(ConnectBtAddress);
			Function_slect.fb.connectTodevice(device);
//			mBluetoothService.connectTodevice(device);	
	}*/
	
	public class getRFIDThread extends Thread{
		String mID = null;
		public getRFIDThread(String ID){
			mID = ID;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			ServerSoap mServerSoap = new ServerSoap(SetupDetailActivity.this, mID);
			mServerSoap.getRFID();
		}
		
	}
    
	public class getRFIDresultHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			text = (String)msg.obj;
			if(text.equals("没有相关数据")){DisplayToast("没有相关数据");}else{
			String s1[] = text.split(":");
			lockserial.setText(s1[1].split("\n")[0]);
			color.setText(s1[2].split("\n")[0]);}
			//LockID.setText((String)msg.obj);
		}
		
	}
	
	//建立RFID数据接收广播
	BroadcastReceiver RFIDreceiver = new BroadcastReceiver() {
		
		@Override
	public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getExtras().getString("type");
			if (action.equals(ConstDefine.Action_RFU)) {
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "RFU:"+intent.getExtras().getString(ConstDefine.Action_RFU);
				mBtresultHandler.sendMessage(msg);
			} else if(action.equals(ConstDefine.Action_EPC)){
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "EPC:"+intent.getExtras().getString(ConstDefine.Action_EPC);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_TID)){
				if(ConstDefine.AutoQueryFlag&&!intent.getExtras().getString(ConstDefine.Action_TID).equals("没有读取到卡片")){
					if (ConstDefine.Debug) {
						Log.d("自动查询", "");
					}
					
					mProgressDialog=ProgressDialog.show(SetupDetailActivity.this, "正在获取信息", "请稍后...");
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
					getRFIDThread mgetRfidThread = new getRFIDThread(intent.getExtras().getString(ConstDefine.Action_TID));
					mgetRfidThread.start();
					ConstDefine.AutoQueryFlag = false;
				}
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				Log.d("",intent.getExtras().getString(ConstDefine.Action_TID));
				msg.obj = intent.getExtras().getString(ConstDefine.Action_TID);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_USR)){
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "USR:"+intent.getExtras().getString(ConstDefine.Action_USR);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_SearchCard)){

				//如果自动查询还是进行广播显示信息
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "ID:"+intent.getExtras().getString(ConstDefine.Action_SearchCard);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_ReadFirmware)){
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "固件版本:"+intent.getExtras().getString(ConstDefine.Action_ReadFirmware);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_SetReadMode)){
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "设置读取模式:"+intent.getExtras().getString(ConstDefine.Action_ReadFirmware);
				mBtresultHandler.sendMessage(msg);
			}
		}
	};
	
	BroadcastReceiver getRFIDReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String result = intent.getStringExtra(ConstDefine.Action_getRFID);
			Message msg = new Message();
			msg.obj = result;
			SetupDetailActivity.this.mgetRFIDresultHandler.sendMessage(msg);
		}
	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {


		public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
           if (ConnectResult.equals(action)) {
				Message ms = new Message();
				ms.what = intent
						.getIntExtra("ConnectResult", ConstDefine.faild);
				funchandler.sendMessage(ms);
			} else if (BluetoothState.equals(action)) {
				Message ms = new Message();
				ms.what = ConstDefine.BluetoothState;
				ms.arg1 = intent.getIntExtra("BluetoothState",
						ConstDefine.STATE_NONE);
				funchandler.sendMessage(ms);
			}
		}

	};	
	
	public class funhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstDefine.success:
				DisplayToast("成功连接到设备");
				Title_right.setText("设备连接成功");
				break;
			case ConstDefine.faild:
				DisplayToast("连接失败");
				Title_right.setText("设备连接失败");
				//devices.clear();
				Function_slect.fb.Stop();
				//searchbtlist.clear();
				break;
			case ConstDefine.BluetoothState:
				switch (msg.arg1) {
				case ConstDefine.STATE_NONE:
					Title_right.setText("设备未连接");
					Title_right.setTextColor(Color.RED);
					break;
				case ConstDefine.STATE_LISTEN:
					Title_right.setText("设备正在监听");
					break;
				case ConstDefine.STATE_CONNECTING:
					Title_right.setText("设备正在连接");
					Title_right.setTextColor(Color.BLUE);
					break;
				case ConstDefine.STATE_CONNECTED:
					Title_right.setTextColor(Color.GREEN);
					Title_right.setText("设备已连接");
					break;

				default:
					break;
				}
				break;
			case ConstDefine.updatetext:
				break;
			default:
				break;
			}
		}
	}
	
	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}
	
	public class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Equ = Equsp.getSelectedItem().toString();
			sp1 = arg2;
			switch(arg2){
			case 0:
				ArrayAdapter adapter1 = new ArrayAdapter(
						SetupDetailActivity.this,
						android.R.layout.simple_spinner_item, s);
				adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Locsp.setAdapter(adapter1);
			    break;
			case 1:
				ArrayAdapter adapter2 = new ArrayAdapter(
						SetupDetailActivity.this,
						android.R.layout.simple_spinner_item, s0);
				adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Locsp.setAdapter(adapter2);
				break;
			case 2:
				ArrayAdapter adapter3 = new ArrayAdapter(
						SetupDetailActivity.this,
						android.R.layout.simple_spinner_item, s1);
				adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Locsp.setAdapter(adapter3);
				break;
			default:
				break;
			}
			Log.d("Equ+Loc",Equ+Loc);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub		
		unregisterReceiver(RFIDreceiver);
		unregisterReceiver(getRFIDReceiver);
		unregisterReceiver(mReceiver);
//		mBluetoothService.exit();
//		if(mBluetoothService!=null)mBluetoothService.stop();
		super.finish();
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
				Intent intent = new Intent();
				intent.setClass(SetupDetailActivity.this, SetupActivity.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				SetupDetailActivity.this.finish();
		}
		return false;
    }
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		if(mBluetoothService!=null)mBluetoothService.stop();
		
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}

