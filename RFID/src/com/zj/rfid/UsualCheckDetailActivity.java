
package com.zj.rfid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UsualCheckDetailActivity extends Activity{
	private TextView Title_left, Title_right;
	private String[] info = null;
	private String[] meterinfo =null;
	private List<String> Equid = new ArrayList<String>();
	private String loginUser = null;
	private String muid = null;
	private String ConnectBtAddress = null;
	private String Taskid = null;
	private String text;
	private String temp;
	private String temp1;
	private Button read;
	private Button detail;
	private BluetoothService mBluetoothService;
	private RFIDOperate mRfidOperate;
	private BluetoothSocket btSocket = null;
	private BluetoothDevice device;
	private ProgressDialog mProgressDialog;
	private BluetoothAdapter mBtAdapter;
	private getRFIDresultHandler mgetRFIDresultHandler = new getRFIDresultHandler();
	private BtresultHandler mBtresultHandler = new BtresultHandler();
	private PopupWindow compareinfoPopupWindow;
	private LinearLayout meter_Layout;
	private LinearLayout state_Layout;
//	private HashMap<String, String> mmap = new HashMap<String, String>();
	private List<RadioButton> radioButtonList;
	View contentView = null;
	private String ConnectResult = "ConnectResult";
	private String BluetoothState = "BluetoothState";
	private funhandler funchandler = new funhandler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.checkdetail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
		
		//接收intent
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		String TitleUser = loginUser.split(",")[0];
		muid = intent.getStringExtra("user").split(",")[1].split(";")[0];
//		ConnectBtAddress = intent.getStringExtra("user").split(";")[1];
		Taskid = intent.getStringExtra("user").split(";")[1];
		
		//设置Title
		Title_left = (TextView) findViewById(R.id.title_left_text);
		Title_right = (TextView) findViewById(R.id.title_right_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("用户:" + TitleUser);
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
		//设置头部textView内容
		info = ServerSoap.GetCheckDetail(Taskid).split(",");
//		for(int i=0;i<info.length;i++){
			
//			mmap.put(String.valueOf(i), info[i]);
//		}
		TextView tvBody=(TextView)this.findViewById(R.id.detailtext);
		tvBody.setText("用户号："+info[1]+"\n用户名："+info[2]+"\n地址："+info[8]);
		tvBody.setTextSize(20);
		
		//注册广播
		mBluetoothService = new BluetoothService(UsualCheckDetailActivity.this, mBtresultHandler);
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
        

		
		meterinfo = ServerSoap.GetCheckDetail(Taskid).split(";");
		for(int i=0;i<meterinfo.length-1;i++){
			temp = meterinfo[i];
			String s =temp.split(",")[11];
			if(!Equid.contains(s))
				{Equid.add(s);}
			
		}
		meter_Layout = (LinearLayout) findViewById(R.id.meter_Layout);
//		state_Layout = (LinearLayout) findViewById(R.id.state_Layout);
		radioButtonList = new ArrayList<RadioButton>();
		mRfidOperate = new RFIDOperate(UsualCheckDetailActivity.this, btSocket, device); 
		initView();
		
    }
private void initView() {
		// TODO Auto-generated method stub
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	    		LinearLayout.LayoutParams.WRAP_CONTENT);

	    
/*	    for(int i=0;i<Equid.size();i++){
	    	TextView Target = new TextView(this);
	    	Target.setLayoutParams(lp);
	    	Target.setText(Equid.get(i));
	    	Target.setTextSize(25);
	    	
	    	meter_Layout.addView(Target);
		    final Button read = new Button(this);
		    read.setText("    读取    ");
		    read.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(UsualCheckDetailActivity.this,CaptureActivity.class);
					intent.putExtra("name","UsualCheckDetailActivity");
					intent.putExtra("user",loginUser);
					startActivity(intent);
					UsualCheckDetailActivity.this.finish();
					
				}
		    	
		    });
		    read.setLayoutParams(lp);
		    meter_Layout.addView(read);
		    
		    
		    
	    }//*/
	    for(int i=0;i<meterinfo.length-1;i++){
	    	TextView Target = new TextView(this);
	    	Target.setLayoutParams(lp);
	    	temp = meterinfo[i];
	    	Target.setText("设备编号："+temp.split(",")[11]+"\n加封位置："+temp.split(",")[6]
	    			+"\n封印编码："+temp.split(",")[3]);
	    	Target.setTextSize(20);
	    	
	    	meter_Layout.addView(Target);
		    final Button read = new Button(this);
		    read.setText("    读取    ");
		    
		    read.setId(i);
		    read.setTag(temp);
		    read.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(Function_slect.fb.Gets() == ConstDefine.STATE_CONNECTED) {//mBluetoothService.getState()==ConstDefine.STATE_CONNECTED){
						DisplayToast("数据读取中，请等待...");
						ConstDefine.AutoQueryFlag = true;
						mRfidOperate.ReadData(ConstDefine.TID, 0, 6);}
					text = (String) read.getTag();

					
				}
			});
		    read.setLayoutParams(lp);
		    meter_Layout.addView(read);
		    
			final RadioGroup radioGroup = new RadioGroup(this);
			radioGroup.setTag(temp);
			radioGroup.setOrientation(RadioGroup.HORIZONTAL);
			RadioButton radio = new RadioButton(this);
			radio.setText("正常");
			radio.setId(i);
			RadioButton radio1 = new RadioButton(this);
			radio1.setText("异常");
			radio1.setId(i+100);
			radioButtonList.add(radio);
			radioButtonList.add(radio1);
			radioGroup.addView(radio);
			radioGroup.addView(radio1);
			radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					int id= group.getCheckedRadioButtonId();
					temp1 = (String)radioGroup.getTag();
					if(id>99){ServerSoap.InsertCheckDetail(Taskid, temp1.split(",")[3], "异常");
					}else{ServerSoap.InsertCheckDetail(Taskid, temp1.split(",")[3], "正常");}
				}
				
			});
		    meter_Layout.addView(radioGroup);
		    
	    
		    ImageView line = new ImageView(this);
		    line.setImageResource(R.drawable.line);//setImageBitmap().getResources().getDrawable(R.drawable.line);
		    line.setLayoutParams(lp);
		    meter_Layout.addView(line);
	    }//*/
    
	    LinearLayout.LayoutParams jp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
	    		LinearLayout.LayoutParams.WRAP_CONTENT);
/*	    TextView checkresult = new TextView(this);
        checkresult.setLayoutParams(lp);
	    checkresult.setText("其他异常情况：");
	    checkresult.setTextSize(25);
	    meter_Layout.addView(checkresult);
	    final EditText result = new EditText(this);
	    result.setLayoutParams(jp);
	    result.setId(10000);
	    meter_Layout.addView(result);//*/
	    
	    Button tijiao = new Button(this);
	    tijiao.setText(" 提  交 ");
	    tijiao.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkOptions() < meterinfo.length-1){
					DisplayToast("还有项目未完成填写");
				}//else if(checkout()&&"".equals(result.getText().toString().trim())){DisplayToast("请填写检查结果");}
				else{
					Log.d("ID",Taskid);
						AlertDialog.Builder builder = new Builder(UsualCheckDetailActivity.this);
						builder.setTitle("请确认！");
						builder.setMessage("您将提交本次稽查的数据");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								ServerSoap.UPCheckDetail1(Taskid);
								ServerSoap.UPCheckDetail(Taskid, muid);
								
								ServerSoap.UPCheckDetail1(Taskid);
								DisplayToast("提交成功");
								Intent intent = new Intent();
								intent.setClass(UsualCheckDetailActivity.this, Function_slect.class);
								intent.putExtra("user",loginUser.split(";")[0]);
								startActivity(intent);
								UsualCheckDetailActivity.this.finish();
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
			}
		});
	    tijiao.setLayoutParams(jp);
	    meter_Layout.addView(tijiao);
	}
/*private View getRadioGroup(int id) {
	// TODO Auto-generated method stub
	RadioGroup radioGroup = new RadioGroup(this);
	radioGroup.setOrientation(RadioGroup.HORIZONTAL);
	RadioButton radio = new RadioButton(this);
	radio.setText("正常");
	radio.setId(id);
	RadioButton radio1 = new RadioButton(this);
	radio1.setText("异常");
	radio1.setId(id+100);
	radioButtonList.add(radio);
	radioButtonList.add(radio1);
	radioGroup.addView(radio);
	radioGroup.addView(radio1);
	return radioGroup;
}//*/


private int checkOptions(){
	int a = 0;
	if (radioButtonList != null){
		for(int i=0; i<radioButtonList.size(); i++){
			boolean isSelected = radioButtonList.get(i).isChecked();
			if (isSelected == true){
				a = a+1;
			}
			}
		}
	return a;
	}

private boolean checkout(){
	boolean b = false;
	if(radioButtonList != null){
		for(int i=1; i<radioButtonList.size(); i=i+2){
			boolean isSelected = radioButtonList.get(i).isChecked();
			if (isSelected == true){
				b = true;
			}
		}
	}
	return b;
}

private int findout(){
	int b = 0;
	if(radioButtonList != null){
		for(int i=1; i<radioButtonList.size(); i=i+2){
			boolean isSelected = radioButtonList.get(i).isChecked();
			if (isSelected == true){
				b=i/2;
			}
		}
	}
	return b;
}
    private void CompareWindowShow(String str , String text)
    {
    	//String[] s = str.split(",");

    		contentView = getLayoutInflater().inflate(R.layout.popcheckinfo , null);
    		final TextView serverinfo = (TextView)contentView.findViewById(R.id.serverinfo);
    		serverinfo.setText("封印编号："+text.split(",")[3]+"\n封印颜色："+text.split(",")[5]+"\n加封位置："+text.split(",")[6]
    		+"\n电表号："+text.split(",")[10]+"\n电表识别码："+text.split(",")[11]+"\n客户编号："+text.split(",")[1]+
    		"\n客户名称："+text.split(",")[2]+"\n加封人员："+text.split(",")[9]);
    		serverinfo.setTextSize(18);
			
    		final TextView readinfo = (TextView)contentView.findViewById(R.id.readinfo);
    		readinfo.setText(str);
    		readinfo.setTextSize(18);
    		TextView status = (TextView)contentView.findViewById(R.id.status);
    		if(str.equals("没有相关数据")){
    			contentView.setBackgroundColor(Color.RED);
    			status.setText("封印与实际不符！请检查！");
    			status.setTextSize(25);
    			MediaPlayer mediaPlayer01;  
    			mediaPlayer01 = MediaPlayer.create(getBaseContext(), R.raw.alarm); 
    			mediaPlayer01.setVolume(0, 100); 
    			mediaPlayer01.start(); 

			}else if(!text.split(",")[3].equals(str.substring(str.indexOf(":")+1, str.indexOf("\n")))){
    			contentView.setBackgroundColor(Color.RED);
    			status.setText("封印与实际不符！请检查！");
    			status.setTextSize(25);
    			MediaPlayer mediaPlayer01;  
    			mediaPlayer01 = MediaPlayer.create(getBaseContext(), R.raw.alarm);  
    			mediaPlayer01.setVolume(0, 100); 
    			mediaPlayer01.start(); 
			}else {
				status.setText("封印状态正常！");
    			status.setTextSize(25);
			}

    		
    	compareinfoPopupWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	compareinfoPopupWindow.setFocusable(true);

    	Button ok = (Button)contentView.findViewById(R.id.ok);
    	ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				serverinfo.setText("");
				compareinfoPopupWindow.dismiss();
			}
			});

    	compareinfoPopupWindow.showAtLocation(findViewById(R.id.checklinearlayout), Gravity.CENTER, 0, 0);
    }//*/
    
    
	public class BtresultHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch (msg.what){
			case ConstDefine.success:
				DisplayToast("成功连接到设备");
				Title_right.setText("设备连接成功");
				mRfidOperate = new RFIDOperate(UsualCheckDetailActivity.this, btSocket,device);//new RFIDOperate(UsualCheckDetailActivity.this,btSocket,mBtresultHandler,device,mBluetoothService);
//				read.setEnabled(true);
				break;
			case ConstDefine.faild:
				DisplayToast("连接失败");
				Function_slect.fb.Stop();
				//mBluetoothService.stop();
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
				
//				locknumber.setText(msg.obj.toString());
			break;
			}
		}
	}
/*	public void connectBt2server(){ 
		if (ConstDefine.Debug) {
			Log.d("start connect to server!", "1");
		}
	    if(ConnectBtAddress.equals("null")){

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
			mBluetoothService.connectTodevice(device);	
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
			ServerSoap mServerSoap = new ServerSoap(UsualCheckDetailActivity.this, mID);
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
//			lock.setText((String)msg.obj);
//			text = (String)msg.obj;
			CompareWindowShow((String)msg.obj , text);
//			String s1[] = text.split("：");
//			lockserial.setText(s1[2].split("\n")[0]);
//			color.setText(s1[3].split("\n")[0]);
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
					
					mProgressDialog=ProgressDialog.show(UsualCheckDetailActivity.this, "正在获取信息", "请稍后...");
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
			UsualCheckDetailActivity.this.mgetRFIDresultHandler.sendMessage(msg);
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
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub		
		unregisterReceiver(RFIDreceiver);
		unregisterReceiver(getRFIDReceiver);
		unregisterReceiver(mReceiver);
		//mBluetoothService.exit();
		//if(mBluetoothService!=null)mBluetoothService.stop();
		super.finish();
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
				Intent intent = new Intent();
				intent.setClass(UsualCheckDetailActivity.this, UsualCheckActivity.class);
				intent.putExtra("user",loginUser.split(";")[0]);
				startActivity(intent);
				UsualCheckDetailActivity.this.finish();
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
