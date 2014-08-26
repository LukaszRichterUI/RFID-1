package com.zj.rfid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.photopay.barcode.BarcodeDetailedData;
import net.photopay.base.BaseBarcodeActivity;

import com.igexin.slavesdk.MessageManager;
import mobi.pdf417.Pdf417MobiScanData;
import mobi.pdf417.Pdf417MobiSettings;
import mobi.pdf417.activity.Pdf417ScanActivity;

import com.zj.net.ServerSoap;
import com.zj.rfid.LoginActivity.LoginThread;

import android.R.color;
import android.R.integer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MainActivity extends Activity {
	
	private Button queryButton,codebar_button,btn417;
	private TextView RFID,Title_left,Title_right,result;
	private BluetoothAdapter mBtAdapter;
	private ProgressDialog progressDialog;
	private PopupWindow searchBtPopupWindow;
	private BluetoothSocket btSocket = null;
	private static String ConnectBtAddress = null;
	private RFIDOperate mRfidOperate;
	private ProgressDialog mProgressDialog;
	private BluetoothDevice device;
	private BluetoothService mBluetoothService;
	
	private BtresultHandler mBtresultHandler = new BtresultHandler();
	private getRFIDresultHandler mgetRFIDresultHandler = new getRFIDresultHandler();
	private List<String> searchbtlist = new ArrayList<String>();
	private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	View contentView = null;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private boolean shoudlPlayBeep = true;
	private MediaPlayer mediaPlayer;
	private String loginUser = null;
	private String meter = null;
	private String meter1 = null;
	//myservice
	private String ConnectResult = "ConnectResult";
	private String BluetoothState = "BluetoothState";
	private funhandler funchandler = new funhandler();
//	private String add;
	private static final int MY_REQUEST_CODE = 1337;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		//在标题栏显示用户名
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		meter = intent.getStringExtra("meter");
			
		mBluetoothService = new BluetoothService(MainActivity.this, mBtresultHandler);
		Title_left = (TextView)findViewById(R.id.title_left_text);
		Title_right = (TextView)findViewById(R.id.title_right_text);
		Title_left.setText("用户:"+loginUser.split(",")[0]);
		Title_right.setText("设备未连接");
		
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
			}
		}
		
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		mBtAdapter= BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "蓝牙设备不可用，请打开蓝牙！", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (!mBtAdapter.isEnabled()) {
			DisplayToast("蓝牙未打开，程序将自动打开蓝牙！");
			mBtAdapter.enable();
		} 
		//屏蔽掉输入框
		//注册广播,蓝牙广播
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		//注册查询官博
		filter = new IntentFilter(ConstDefine.Action_getRFID);
		registerReceiver(getRFIDReceiver, filter);
		//注册读卡广播
		filter = new IntentFilter(ConstDefine.Action_NAME);
        registerReceiver(RFIDreceiver, filter);
		// 注册Service蓝牙连接广播
		filter = new IntentFilter(ConnectResult);
		registerReceiver(mReceiver, filter);
		// 注册Service蓝牙连接状态广播
		filter = new IntentFilter(BluetoothState);
		registerReceiver(mReceiver, filter);
        
        RFID = (TextView)findViewById(R.id.RFID);
        RFID.setText(meter);
        result = (TextView)findViewById(R.id.result);
        if(meter!=null){
        	Log.d("meter",meter);
			mProgressDialog=ProgressDialog.show(MainActivity.this, "正在获取信息", "请稍后...");
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
			ServerSoap mServerSoap = new ServerSoap(MainActivity.this, meter);
			mServerSoap.getRFID();}
        
		mRfidOperate = new RFIDOperate(MainActivity.this, btSocket, device);        
        queryButton = (Button)findViewById(R.id.queryButton);   
	    queryButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					//自动读取卡片并且验证
						if(Function_slect.fb.Gets() == ConstDefine.STATE_CONNECTED) {//mBluetoothService.getState()==ConstDefine.STATE_CONNECTED){
							result.setText("数据读取中，请等待...");
							ConstDefine.AutoQueryFlag = true;
							mRfidOperate.ReadData(ConstDefine.TID, 0, 6);
						}else {
							AlertDialog.Builder builder = new Builder(MainActivity.this);
							builder.setTitle("提示");
							builder.setMessage("蓝牙未连接，请连接蓝牙设备");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							});
							builder.create().show();
						}
			}
		});
	
	    codebar_button = (Button)findViewById(R.id.codebar_button);
	    codebar_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
/*				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Function_slect.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				MainActivity.this.finish();*/
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CaptureActivity.class);
				intent.putExtra("name","MainActivity");
				intent.putExtra("user",loginUser);
				startActivity(intent);
				MainActivity.this.finish();
				
			}
		});
	    
	    btn417 = (Button)findViewById(R.id.btn417);
	    btn417.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent for Pdf417ScanActivity.class
				Intent intent = new Intent(MainActivity.this, Pdf417ScanActivity.class);
				intent.putExtra(Pdf417ScanActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
				Pdf417MobiSettings sett = new Pdf417MobiSettings();
				// set this to true to enable PDF417 scanning
				sett.setPdf417Enabled(true);
				// set this to true to enable QR code scanning
				sett.setQrCodeEnabled(true);
				// set this to true to prevent showing dialog after successful scan
				sett.setDontShowDialog(true);
				// if license permits this, remove Pdf417.mobi logo overlay on scan activity
				// if license forbids this, this option has no effect
				sett.setRemoveOverlayEnabled(true);
				// put settings as intent extra
				intent.putExtra(Pdf417ScanActivity.EXTRAS_SETTINGS, sett);

				// Start Activity
				startActivityForResult(intent, MY_REQUEST_CODE);
			}
		});
	    
	    setVolumeControlStream(AudioManager.STREAM_MUSIC);
	    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    if (audioManager.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL) {
			shoudlPlayBeep = false;
		}

	    mediaPlayer = new MediaPlayer();
	    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	    mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.seekTo(0);
			}
		});
	    AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
	    try { 
	    	mediaPlayer.setDataSource(file.getFileDescriptor(), 
	    	file.getStartOffset(), file.getLength()); 
	    	file.close(); 
	    	mediaPlayer.setVolume(0, 100); 
	    	mediaPlayer.prepare(); 
	    	} catch (IOException ioe) { 
	    	mediaPlayer = null; 
	    	} 
	    
	}
	
	public class BtresultHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstDefine.success:
					DisplayToast("成功连接到设备");
					Title_right.setText("设备连接成功");
					result.setText("连接成功,可进行信息读取");
					mRfidOperate = new RFIDOperate(MainActivity.this, btSocket,device);//new RFIDOperate(MainActivity.this,btSocket,mBtresultHandler,device,mBluetoothService);
					queryButton.setEnabled(true);
				break;
			case ConstDefine.faild:
					if (shoudlPlayBeep) {
						mediaPlayer.start();
					}
					DisplayToast("连接失败");
					Title_right.setText("设备连接失败");
					result.setText("连接失败，请重新连接");
					devices.clear();
					Function_slect.fb.Stop();
					//mBluetoothService.stop();
					
					searchbtlist.clear();
				break;
			case ConstDefine.BluetoothState:

					switch (msg.arg1) {
					case ConstDefine.STATE_NONE:
							if (shoudlPlayBeep) {
								mediaPlayer.start();
							}
							Title_right.setText("设备未连接");
							Title_right.setTextColor(Color.RED);
//							readCardButton.setEnabled(false);
						break;
					case ConstDefine.STATE_LISTEN:
						Title_right.setText("设备正在监听");
						break;
					case ConstDefine.STATE_CONNECTING:
						Title_right.setText("设备正在连接");
						Title_right.setTextColor(Color.BLUE);
						break;
					case ConstDefine.STATE_CONNECTED:
						if (shoudlPlayBeep) {
							mediaPlayer.start();
						}
						Title_right.setTextColor(Color.GREEN);
						Title_right.setText("设备已连接");
						break;
	
					default:
						break;
					}
				break;
			case ConstDefine.updatetext:
					result.setText("");
					RFID.setText(msg.obj.toString());
				break;
			default:
				break;
			}
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
			result.setText((String)msg.obj);
//			String[] items = (String[])msg.obj;
//			AlertDialog.Builder builder = new Builder(MainActivity.this);
//			builder.setTitle("查询结果");
//			builder.setItems(items, null);
//			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//				}
//			});
//			builder.create().show();
			
		}
		
	}
	
	public void connectBt2server(){ 
		if (ConstDefine.Debug) {
			Log.d("start connect to server!", "1");
		}
			
			if(ConnectBtAddress == null){
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
			//mBluetoothService.connectTodevice(device);	
	}
	
	public class getRFIDThread extends Thread{
		String mID = null;
		public getRFIDThread(String ID){
			mID = ID;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			ServerSoap mServerSoap = new ServerSoap(MainActivity.this, mID);
			mServerSoap.getRFID();
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
					
					mProgressDialog=ProgressDialog.show(MainActivity.this, "正在获取信息", "请稍后...");
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
			MainActivity.this.mgetRFIDresultHandler.sendMessage(msg);
		}
	};
	
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {


		public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();
           ((ListView)contentView.findViewById(R.id.new_devices)).setVisibility(View.VISIBLE);
			((ListView)contentView.findViewById(R.id.new_devices)).setAdapter(mNewDevicesArrayAdapter);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
    				if (ConstDefine.Debug) {
    					Log.d("get bluetooth", device.getName().toString());
    				}
                	
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            	mBtAdapter.cancelDiscovery();
            	((TextView)contentView.findViewById(R.id.new_devices_title)).setText("搜索到的新设备");
				if (ConstDefine.Debug) {
					Log.d("", "search finished");
				}           
            }else if (ConnectResult.equals(action)) {
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
//				add = ConnectBtAddress;
				Title_right.setText("设备连接成功");
				break;
			case ConstDefine.faild:
				if (shoudlPlayBeep) {
					mediaPlayer.start();
				}
				DisplayToast("连接失败");
				Title_right.setText("设备连接失败");
				devices.clear();
				Function_slect.fb.Stop();
				searchbtlist.clear();
				break;
			case ConstDefine.BluetoothState:
				switch (msg.arg1) {
				case ConstDefine.STATE_NONE:
					if (shoudlPlayBeep) {
						mediaPlayer.start();
					}
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
					if (shoudlPlayBeep) {
						mediaPlayer.start();
					}
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_REQUEST_CODE && resultCode == BaseBarcodeActivity.RESULT_OK) {
			// read scan result
			Pdf417MobiScanData scanData = data.getParcelableExtra(BaseBarcodeActivity.EXTRAS_RESULT);

			// read scanned barcode type (PDF417 or QR code)
			String barcodeType = scanData.getBarcodeType();
			// read the data contained in barcode
			String barcodeData = scanData.getBarcodeData();
			meter1 = barcodeData;
			RFID.setText(meter1);
	        if(meter1!=null){
	        	Log.d("meter",meter1);
				mProgressDialog=ProgressDialog.show(MainActivity.this, "正在获取信息", "请稍后...");
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
				ServerSoap mServerSoap = new ServerSoap(MainActivity.this, meter1);
				mServerSoap.getRFID();}
			}
	}
	
	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
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
				intent.setClass(MainActivity.this, Function_slect.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				MainActivity.this.finish();
		}
		return false;
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//if(mBluetoothService!=null)mBluetoothService.stop();
		
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	
	
}
