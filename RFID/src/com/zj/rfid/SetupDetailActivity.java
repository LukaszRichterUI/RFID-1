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
	private String Loc = "���";
	private static String[] m = {"���ܱ�","������","�����"};
	private static String[] s = {"���","���ߺ�","����","���߶���","����"};
	private static String[] s0 = {"����","����"};
	private static String[] s1 = {"����","����"};
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
		//loginUser="�û���,ID;������ַ"
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
		Title_left.setText("�û�:" + TitleUser);
		Title_right.setText("�豸δ����");
		


		info = ServerSoap.GetSetupDetail(Taskid).split(",");
		
		TextView tvBody=(TextView)this.findViewById(R.id.body1);
		tvBody.setText("�û��ţ�"+info[1]+"\n�û�����"+info[2]+"\n��ַ��"+info[8]);
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
        //����ѡ������ArrayAdapter��������  
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);  
          
        //���������б�ķ��  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        
        //��adapter ��ӵ�spinner��  
        Equsp.setAdapter(adapter);  
        Equsp.setSelection(sp1);
        Equsp.setOnItemSelectedListener(new SpinnerSelectedListener());
          
        //����¼�Spinner�¼�����    
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
        
        //����Ĭ��ֵ  
        //Equsp.setVisibility(View.VISIBLE); 
		mBluetoothService = new BluetoothService(SetupDetailActivity.this, mBtresultHandler);
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
					DisplayToast("���ݶ�ȡ�У���ȴ�...");
					ConstDefine.AutoQueryFlag = true;
					mRfidOperate.ReadData(ConstDefine.TID, 0, 6);
				}else{
					AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
					builder.setTitle("��ʾ");
					builder.setMessage("����δ���ӣ������������豸");
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

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
				builder.setTitle("���棡");
				builder.setMessage("���β�������ɾ�������е���������");
				builder.setPositiveButton("ȷ��",
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
				builder.setNegativeButton("ȡ��",
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
				builder.setTitle("��������ձ�����д �����ݣ���û�б������ݽ���ʧ");
				builder.setMessage("�Ƿ�ȷ����գ�");
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EquNo.setText("");
						lockserial.setText("");
						locknumber.setText("");
						color.setText("");
						note1.setText("��");
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
		});
	    
	    save=(Button)this.findViewById(R.id.save);
	    save.setEnabled(false);
	    save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss");       
				Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
				String    str    =    formatter.format(curDate);       
				String mmeternumber = EquNo.getText().toString();
				String mlockserial = lockserial.getText().toString();
				String mlocknumber = locknumber.getText().toString();
				String mcolor = color.getText().toString();
				String mnote = note1.getText().toString();
				String msetpos = Equ+Loc;
				if("".equals(mmeternumber.trim())&&"".equals(mlockserial.trim())&&"".equals(mlocknumber.trim())
						&&"".equals(mcolor.trim())){
//					DisplayToast("����д������");
					AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
					builder.setTitle("��ʾ��");
					builder.setMessage("����д���������");
					builder.setPositiveButton("ȷ��",
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
//				DisplayToast("�������ݳɹ�");
				AlertDialog.Builder builder = new Builder(SetupDetailActivity.this);
				builder.setTitle("��ʾ��");
				builder.setMessage("�������ݳɹ�");
				builder.setPositiveButton("ȷ��",
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
				builder.setTitle("ע�⣡");
				builder.setMessage("�ύ�ĵ��ݽ������ٴα༭");
				builder.setPositiveButton("ȷ��",
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
						DisplayToast("�ύ�ɹ���");
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
		});

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
				DisplayToast("�ɹ����ӵ��豸");
				Title_right.setText("�豸���ӳɹ�");
				mRfidOperate = new RFIDOperate(SetupDetailActivity.this, btSocket,device);//new RFIDOperate(SetupDetailActivity.this,btSocket,mBtresultHandler,device,mBluetoothService);
				if(state.equals("doing")){
				GET.setEnabled(true);
				clean.setEnabled(true);
				delet.setEnabled(true);
				save.setEnabled(true);
				submit.setEnabled(true);}
				break;
			case ConstDefine.faild:
//				DisplayToast("����ʧ��");
				Function_slect.fb.Stop();
//				mBluetoothService.stop();
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
			if(text.equals("û���������")){DisplayToast("û���������");}else{
			String s1[] = text.split(":");
			lockserial.setText(s1[1].split("\n")[0]);
			color.setText(s1[2].split("\n")[0]);}
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
					
					mProgressDialog=ProgressDialog.show(SetupDetailActivity.this, "���ڻ�ȡ��Ϣ", "���Ժ�...");
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
    public boolean onKeyDown(int keyCode, KeyEvent e)//��дonKeyDown����
    {
    	if(keyCode==4){//�����·��ؼ�
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

