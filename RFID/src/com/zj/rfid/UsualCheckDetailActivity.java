
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
		
		//����intent
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		String TitleUser = loginUser.split(",")[0];
		muid = intent.getStringExtra("user").split(",")[1].split(";")[0];
//		ConnectBtAddress = intent.getStringExtra("user").split(";")[1];
		Taskid = intent.getStringExtra("user").split(";")[1];
		
		//����Title
		Title_left = (TextView) findViewById(R.id.title_left_text);
		Title_right = (TextView) findViewById(R.id.title_right_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("�û�:" + TitleUser);
		Title_right.setText("�豸δ����");
		
		if (Function_slect.fb == null) {
			Title_right.setText("�豸δ����");
		} else {
			int s = Function_slect.fb.Gets();
			if (s == BluetoothService.STATE_NONE) {
				Title_right.setTextColor(Color.RED);
				Title_right.setText("�豸δ����");
			} else if (s == BluetoothService.STATE_LISTEN) {
				Title_right.setText("�豸���ڼ���");
			} else if (s == BluetoothService.STATE_CONNECTING) {
				Title_right.setText("�豸��������");
				Title_right.setTextColor(Color.BLUE);
			} else if (s == BluetoothService.STATE_CONNECTED) {
				Title_right.setTextColor(Color.GREEN);
				Title_right.setText("�豸������");

			}
		}
		//����ͷ��textView����
		info = ServerSoap.GetCheckDetail(Taskid).split(",");
//		for(int i=0;i<info.length;i++){
			
//			mmap.put(String.valueOf(i), info[i]);
//		}
		TextView tvBody=(TextView)this.findViewById(R.id.detailtext);
		tvBody.setText("�û��ţ�"+info[1]+"\n�û�����"+info[2]+"\n��ַ��"+info[8]);
		tvBody.setTextSize(20);
		
		//ע��㲥
		mBluetoothService = new BluetoothService(UsualCheckDetailActivity.this, mBtresultHandler);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter = new IntentFilter(ConstDefine.Action_getRFID);
		registerReceiver(getRFIDReceiver, filter);
		filter = new IntentFilter(ConstDefine.Action_NAME);
        registerReceiver(RFIDreceiver, filter);
		// ע��Service�������ӹ㲥
		filter = new IntentFilter(ConnectResult);
		registerReceiver(mReceiver, filter);
		// ע��Service��������״̬�㲥
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
		    read.setText("    ��ȡ    ");
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
	    	Target.setText("�豸��ţ�"+temp.split(",")[11]+"\n�ӷ�λ�ã�"+temp.split(",")[6]
	    			+"\n��ӡ���룺"+temp.split(",")[3]);
	    	Target.setTextSize(20);
	    	
	    	meter_Layout.addView(Target);
		    final Button read = new Button(this);
		    read.setText("    ��ȡ    ");
		    
		    read.setId(i);
		    read.setTag(temp);
		    read.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(Function_slect.fb.Gets() == ConstDefine.STATE_CONNECTED) {//mBluetoothService.getState()==ConstDefine.STATE_CONNECTED){
						DisplayToast("���ݶ�ȡ�У���ȴ�...");
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
			radio.setText("����");
			radio.setId(i);
			RadioButton radio1 = new RadioButton(this);
			radio1.setText("�쳣");
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
					if(id>99){ServerSoap.InsertCheckDetail(Taskid, temp1.split(",")[3], "�쳣");
					}else{ServerSoap.InsertCheckDetail(Taskid, temp1.split(",")[3], "����");}
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
	    checkresult.setText("�����쳣�����");
	    checkresult.setTextSize(25);
	    meter_Layout.addView(checkresult);
	    final EditText result = new EditText(this);
	    result.setLayoutParams(jp);
	    result.setId(10000);
	    meter_Layout.addView(result);//*/
	    
	    Button tijiao = new Button(this);
	    tijiao.setText(" ��  �� ");
	    tijiao.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkOptions() < meterinfo.length-1){
					DisplayToast("������Ŀδ�����д");
				}//else if(checkout()&&"".equals(result.getText().toString().trim())){DisplayToast("����д�����");}
				else{
					Log.d("ID",Taskid);
						AlertDialog.Builder builder = new Builder(UsualCheckDetailActivity.this);
						builder.setTitle("��ȷ�ϣ�");
						builder.setMessage("�����ύ���λ��������");
						builder.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								ServerSoap.UPCheckDetail1(Taskid);
								ServerSoap.UPCheckDetail(Taskid, muid);
								
								ServerSoap.UPCheckDetail1(Taskid);
								DisplayToast("�ύ�ɹ�");
								Intent intent = new Intent();
								intent.setClass(UsualCheckDetailActivity.this, Function_slect.class);
								intent.putExtra("user",loginUser.split(";")[0]);
								startActivity(intent);
								UsualCheckDetailActivity.this.finish();
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
	radio.setText("����");
	radio.setId(id);
	RadioButton radio1 = new RadioButton(this);
	radio1.setText("�쳣");
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
    		serverinfo.setText("��ӡ��ţ�"+text.split(",")[3]+"\n��ӡ��ɫ��"+text.split(",")[5]+"\n�ӷ�λ�ã�"+text.split(",")[6]
    		+"\n���ţ�"+text.split(",")[10]+"\n���ʶ���룺"+text.split(",")[11]+"\n�ͻ���ţ�"+text.split(",")[1]+
    		"\n�ͻ����ƣ�"+text.split(",")[2]+"\n�ӷ���Ա��"+text.split(",")[9]);
    		serverinfo.setTextSize(18);
			
    		final TextView readinfo = (TextView)contentView.findViewById(R.id.readinfo);
    		readinfo.setText(str);
    		readinfo.setTextSize(18);
    		TextView status = (TextView)contentView.findViewById(R.id.status);
    		if(str.equals("û���������")){
    			contentView.setBackgroundColor(Color.RED);
    			status.setText("��ӡ��ʵ�ʲ��������飡");
    			status.setTextSize(25);
    			MediaPlayer mediaPlayer01;  
    			mediaPlayer01 = MediaPlayer.create(getBaseContext(), R.raw.alarm); 
    			mediaPlayer01.setVolume(0, 100); 
    			mediaPlayer01.start(); 

			}else if(!text.split(",")[3].equals(str.substring(str.indexOf(":")+1, str.indexOf("\n")))){
    			contentView.setBackgroundColor(Color.RED);
    			status.setText("��ӡ��ʵ�ʲ��������飡");
    			status.setTextSize(25);
    			MediaPlayer mediaPlayer01;  
    			mediaPlayer01 = MediaPlayer.create(getBaseContext(), R.raw.alarm);  
    			mediaPlayer01.setVolume(0, 100); 
    			mediaPlayer01.start(); 
			}else {
				status.setText("��ӡ״̬������");
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
				DisplayToast("�ɹ����ӵ��豸");
				Title_right.setText("�豸���ӳɹ�");
				mRfidOperate = new RFIDOperate(UsualCheckDetailActivity.this, btSocket,device);//new RFIDOperate(UsualCheckDetailActivity.this,btSocket,mBtresultHandler,device,mBluetoothService);
//				read.setEnabled(true);
				break;
			case ConstDefine.faild:
				DisplayToast("����ʧ��");
				Function_slect.fb.Stop();
				//mBluetoothService.stop();
				break;
			case ConstDefine.BluetoothState:

				switch (msg.arg1) {
				case ConstDefine.STATE_NONE:
						Title_right.setText("�豸δ����");
						Title_right.setTextColor(Color.RED);
					break;
				case ConstDefine.STATE_LISTEN:
					Title_right.setText("�豸���ڼ���");
					break;
				case ConstDefine.STATE_CONNECTING:
					Title_right.setText("�豸��������");
					Title_right.setTextColor(Color.BLUE);
					break;
				case ConstDefine.STATE_CONNECTED:
					Title_right.setTextColor(Color.GREEN);
					Title_right.setText("�豸������");
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
//			String s1[] = text.split("��");
//			lockserial.setText(s1[2].split("\n")[0]);
//			color.setText(s1[3].split("\n")[0]);
			//LockID.setText((String)msg.obj);
		}
		
	}
	
	//����RFID���ݽ��չ㲥
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
				if(ConstDefine.AutoQueryFlag&&!intent.getExtras().getString(ConstDefine.Action_TID).equals("û�ж�ȡ����Ƭ")){
					if (ConstDefine.Debug) {
						Log.d("�Զ���ѯ", "");
					}
					
					mProgressDialog=ProgressDialog.show(UsualCheckDetailActivity.this, "���ڻ�ȡ��Ϣ", "���Ժ�...");
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

				//����Զ���ѯ���ǽ��й㲥��ʾ��Ϣ
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "ID:"+intent.getExtras().getString(ConstDefine.Action_SearchCard);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_ReadFirmware)){
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "�̼��汾:"+intent.getExtras().getString(ConstDefine.Action_ReadFirmware);
				mBtresultHandler.sendMessage(msg);
			}else if(action.equals(ConstDefine.Action_SetReadMode)){
				Message msg = new Message();
				msg.what = ConstDefine.updatetext;
				msg.obj = "���ö�ȡģʽ:"+intent.getExtras().getString(ConstDefine.Action_ReadFirmware);
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
				DisplayToast("�ɹ����ӵ��豸");
				Title_right.setText("�豸���ӳɹ�");
				break;
			case ConstDefine.faild:
				DisplayToast("����ʧ��");
				Title_right.setText("�豸����ʧ��");
				//devices.clear();
				Function_slect.fb.Stop();
				//searchbtlist.clear();
				break;
			case ConstDefine.BluetoothState:
				switch (msg.arg1) {
				case ConstDefine.STATE_NONE:
					Title_right.setText("�豸δ����");
					Title_right.setTextColor(Color.RED);
					break;
				case ConstDefine.STATE_LISTEN:
					Title_right.setText("�豸���ڼ���");
					break;
				case ConstDefine.STATE_CONNECTING:
					Title_right.setText("�豸��������");
					Title_right.setTextColor(Color.BLUE);
					break;
				case ConstDefine.STATE_CONNECTED:
					Title_right.setTextColor(Color.GREEN);
					Title_right.setText("�豸������");
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
    public boolean onKeyDown(int keyCode, KeyEvent e)//��дonKeyDown����
    {
    	if(keyCode==4){//�����·��ؼ�
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
