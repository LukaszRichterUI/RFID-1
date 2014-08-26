package com.zj.rfid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.zj.rfid.MyService.FirstBinder;
import com.zj.rfid.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Function_slect extends Activity {
	public static String muid;
	private ImageButton OutsideEetupBtn,UsualCheckBtn,SettingBtn,ExitBtn,LockCheck,SpecialCheckBtn;
	private TextView Title_left, Title_right;
	private String loginUser = null;
	private BluetoothAdapter mBtAdapter;
	private PopupWindow searchBtPopupWindow;
	private static String ConnectBtAddress = null;
	private String add;
	private BluetoothDevice device;
	private funhandler funchandler = new funhandler();
	public static FirstBinder fb = null;
	private List<String> searchbtlist = new ArrayList<String>();
	private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	View contentView = null;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private boolean shoudlPlayBeep = true;
	private MediaPlayer mediaPlayer;
	private String ConnectResult = "ConnectResult";
	private String BluetoothState = "BluetoothState";
	public static TextView tLogView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.function_select);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title);
		
		//接收Intent
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		String TitleUser = loginUser.split(",")[0];
		muid = loginUser.split(",")[1];
//		mBluetoothService = new BluetoothService(Function_slect.this, mTitleHandler);
		
		//title栏信息
		Title_left = (TextView) findViewById(R.id.title_left_text);
		Title_right = (TextView) findViewById(R.id.title_right_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("用户:" + TitleUser);
		Title_right.setText("设备未连接");
		if (fb == null) {
			Intent intentSer = new Intent();
			intentSer.setClass(Function_slect.this, MyService.class);
			bindService(intentSer, cnn, BIND_AUTO_CREATE);
			Title_right.setText("设备未连接");
		} else {
			int s = fb.Gets();
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
		
		//注册广播,蓝牙广播
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		//注册查询广播
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		// 注册Service蓝牙连接广播
		filter = new IntentFilter(ConnectResult);
		registerReceiver(mReceiver, filter);
		// 注册Service蓝牙连接状态广播
		filter = new IntentFilter(BluetoothState);
		registerReceiver(mReceiver, filter);
		//按钮事件
		setlistener();
		//蓝牙连接声音
		 setVolumeControlStream(AudioManager.STREAM_MUSIC);
		    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		    if (audioManager.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL) {
				shoudlPlayBeep = false;
			}

		    mediaPlayer = new MediaPlayer();
		    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		    mediaPlayer.setOnCompletionListener(new OnCompletionListener(){

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

	private void setlistener() {
			//外勤装表
		    OutsideEetupBtn =(ImageButton)findViewById(R.id.OutsideEetupBtn);
		    if(muid.equals("002")){
		    OutsideEetupBtn.setEnabled(true);
		    OutsideEetupBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Function_slect.this, SetupActivity.class);
					intent.putExtra("user", loginUser);
					startActivity(intent);
					Function_slect.this.finish();
				    }
				});}else{OutsideEetupBtn.setEnabled(false);}
			//常规稽查
		    UsualCheckBtn = (ImageButton)findViewById(R.id.UsualCheckBtn);
		    if(muid.equals("001")){
		    UsualCheckBtn.setEnabled(true);
		    UsualCheckBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Function_slect.this, UsualCheckActivity.class);
					intent.putExtra("user", loginUser);
					startActivity(intent);
					Function_slect.this.finish();
				    }
				});}else{UsualCheckBtn.setEnabled(false);}
		    //专项稽查
		    SpecialCheckBtn = (ImageButton)findViewById(R.id.SpecialCheckBtn);
		    SpecialCheckBtn.setEnabled(false);
		    SpecialCheckBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Function_slect.this, SpecialCheck.class);
					intent.putExtra("user", loginUser);
					startActivity(intent);
					Function_slect.this.finish();
				    }
				});
		    //封印验证
		    LockCheck = (ImageButton)findViewById(R.id.LockCheck);
		    LockCheck.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Function_slect.this, MainActivity.class);
					intent.putExtra("user", loginUser);
					startActivity(intent);
					Function_slect.this.finish();
				}});
		    
		    //系统设置
		    SettingBtn = (ImageButton)findViewById(R.id.SettingBtn);
		    SettingBtn.setEnabled(false);
		    SettingBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Function_slect.this, SystemSetting.class);
					intent.putExtra("user", loginUser);
					startActivity(intent);
					Function_slect.this.finish();
				    }
				});
		    //退出
		    ExitBtn = (ImageButton)findViewById(R.id.ExitBtn);
		    ExitBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(Function_slect.this, LoginActivity.class);
					startActivity(intent);
					Function_slect.this.finish();
				    }
				});
	}

	//蓝牙设备列表
	private void BTsearchWindowShow() {//List<String> list
		
		if (searchBtPopupWindow == null) {
			contentView = getLayoutInflater().inflate(R.layout.popwindow_searchwidget_layout, null);
		
			Button scanButton = (Button) contentView.findViewById(R.id.search);
	        scanButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
	                doDiscovery(contentView);
	                v.setVisibility(View.GONE);
					
				}
			});
	        ListView pairedListView = (ListView) contentView.findViewById(R.id.paired_devices);
	        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
	        pairedListView.setOnItemClickListener(mDeviceClickListener);

	        // Find and set up the ListView for newly discovered devices
	        ListView newDevicesListView = (ListView) contentView.findViewById(R.id.new_devices);
	        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
	        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
	        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
	        if (pairedDevices.size() > 0) {
	        	contentView.findViewById(R.id.paired_devices_title).setVisibility(View.VISIBLE);
	            for (BluetoothDevice device : pairedDevices) {
	                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            }
	        } else {
	            String noDevices = getResources().getText(R.string.none_paired).toString();
	            ((TextView)contentView.findViewById(R.id.paired_devices_title)).setText(noDevices);
	            ((ListView)contentView.findViewById(R.id.paired_devices)).setVisibility(View.INVISIBLE);
	        }
			
			searchBtPopupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);//设置搜索窗口属性
						
			searchBtPopupWindow.setFocusable(true);
			Button closeButton = (Button)contentView.findViewById(R.id.pop_searchWidgetCloseBtn);
			closeButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					((Button)contentView.findViewById(R.id.search)).setVisibility(View.VISIBLE);
					mNewDevicesArrayAdapter.clear();
					((TextView)contentView.findViewById(R.id.new_devices_title)).setVisibility(View.INVISIBLE);
					((ListView)contentView.findViewById(R.id.new_devices)).setVisibility(View.INVISIBLE);
					((LinearLayout)contentView.findViewById(R.id.linerlayout_searchnewdevices)).setVisibility(View.INVISIBLE);
					searchBtPopupWindow.dismiss();
					devices.clear();
					searchbtlist.clear();
				}
			});
		}
	searchBtPopupWindow.showAtLocation(findViewById(R.id.RelativeLayout1), Gravity.CENTER, 0, 0);
}
	//蓝牙连接
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
			fb.connectTodevice(device);
			//mBluetoothService.connectTodevice(device);	
	}

	
	public class funhandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstDefine.success:
				DisplayToast("成功连接到设备");
				add = ConnectBtAddress;
				Title_right.setText("设备连接成功");
				break;
			case ConstDefine.faild:
				if (shoudlPlayBeep) {
					mediaPlayer.start();
				}
				DisplayToast("连接失败");
				Title_right.setText("设备连接失败");
				devices.clear();
				fb.Stop();
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

	//
	private void StopSer() {
		try {
			fb.Stop();
			fb.Exit();
		} catch (Exception e) {

		}
		Intent intentSer = new Intent();
		intentSer.setClass(Function_slect.this, MyService.class);
		stopService(intentSer);
	}
	
	ServiceConnection cnn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			fb = (FirstBinder) arg1;
		}
	};
	

	//广播
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
	
	private void doDiscovery(View v) {
        // Indicate scanning in the title

        // Turn on sub-title for new devices
        v.findViewById(R.id.new_devices_title).setVisibility(View.VISIBLE);
        ((LinearLayout)v.findViewById(R.id.linerlayout_searchnewdevices)).setVisibility(View.VISIBLE);
        ((TextView)v.findViewById(R.id.new_devices_title)).setText("正在搜索...");
//        ((ProgressBar)v.findViewById(R.id.searchprogressbar)).setVisibility(View.VISIBLE);
        ((TextView)v.findViewById(R.id.paired_devices_title)).setText("已配对的蓝牙设备");
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
	
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            ConnectBtAddress = info.substring(info.length() - 17);
            
            searchBtPopupWindow.dismiss();
            //result.setText("正在连接设备，请稍后...");
			DisplayToast("开始连接蓝牙设备");
//			threadBt2server.start();
			connectBt2server();
			
        }
    };
    

	
	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, ConstDefine.MENU_SHOW_SEARCH,0,R.string.btsearchMenu);
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
		
        switch (item.getItemId()) {
		case ConstDefine.MENU_SHOW_SEARCH:
			BTsearchWindowShow();
           break;
        case ConstDefine.MENU_SHOW_ABOUT:
        	showAbout();
        	break;
		default:
			break;			
        }
        return false;
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AlertDialog.Builder builder = new Builder(Function_slect.this);
			builder.setTitle("提示");
			builder.setMessage("确认退出？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							StopSer();
							Function_slect.this.finish();
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

			break;

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);

	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub		
		//unregisterReceiver(RFIDreceiver);
		//unregisterReceiver(getRFIDReceiver);
		unregisterReceiver(mReceiver);
		
//		mBluetoothService.exit();
//		if(mBluetoothService!=null)mBluetoothService.stop();
		super.finish();
	}


}


