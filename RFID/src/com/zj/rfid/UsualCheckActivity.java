package com.zj.rfid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zj.net.ServerSoap;
import com.zj.rfid.R;
import com.zj.rfid.R.id;
import com.zj.rfid.R.layout;

public class UsualCheckActivity extends Activity {
	private TextView Title_left;
	private String loginUser = null;
	private List<String> Task = new ArrayList<String>();
	private List<String> temp = new ArrayList<String>();
	private List<String> update = new ArrayList<String>();
	private List<String> status = new ArrayList<String>();
	private List<String> Detail = new ArrayList<String>();
	private List<String> OKtaskid = new ArrayList<String>();
	private List<Integer> id = new ArrayList<Integer>();
	private ListView lv;

	// 2013.10.20 ���thread
	private updateThread myThread;
	private Handler mHandler;
	private String muid;
	private MyHandler myHandler;
	private ArrayAdapter<String> adapter;
	private ProgressDialog mDialog;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.usualchecklist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		String TitleUser = loginUser.split(",")[0];
		muid = intent.getStringExtra("user").split(",")[1];
		Title_left = (TextView) findViewById(R.id.title_left_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("�û�:" + TitleUser);
		lv = (ListView) this.findViewById(R.id.usualchecklist);

		myHandler = new MyHandler();
		myThread = new updateThread();
		myThread.start();
		mDialog = new ProgressDialog(this);
		mDialog.setTitle("���Ժ�");
		mDialog.setMessage("���ڼ������ݣ�");
		mDialog.show();

	}
/**
 * ��handler�л�ȡ���߳��д��ݹ�������Ϣ�����Ҹ���adapter�е�����
 * @author Ennis
 *
 */
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			temp = (List) msg.obj;
			for (int i = 0; i < temp.size(); i++) {
				Detail.add("�������ţ�"+temp.get(i).split(",")[0]+"\n�û�����" + temp.get(i).split(",")[2] + "\n��ַ��"
						+ temp.get(i).split(",")[8] + "\n������״̬��"
						+ status.get(i));
				if (status.get(i).equals("���")) {
					id.add(i);
				}
				OKtaskid.add(temp.get(i).split(",")[0]);
			}
			adapter = new ArrayAdapter<String>(UsualCheckActivity.this,
					android.R.layout.simple_list_item_1, Detail);
			adapter.notifyDataSetChanged();

			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (id.contains(arg2)) {
						Intent intent = new Intent();
						intent.setClass(UsualCheckActivity.this,
								showcheck.class);
						intent.putExtra("user",
								loginUser + ";" + OKtaskid.get(arg2));
						startActivity(intent);
						UsualCheckActivity.this.finish();
					} else {
						Log.d("�������",String.valueOf(arg2));
						Intent intent = new Intent();
						intent.setClass(UsualCheckActivity.this,
								UsualCheckDetailActivity.class);
						intent.putExtra("user",
								loginUser + ";" + OKtaskid.get(arg2));
						startActivity(intent);
						UsualCheckActivity.this.finish();
					}
				}
			});
			mDialog.dismiss();
		}
		
	}
/**
 * ���߳��첽�����������񣬷���workthread�н��С������������̣߳����¼��ػ�����
 * @author Ennis
 *
 */
	private class updateThread extends Thread {
		@Override
		public void run() {
			Task = ServerSoap.GetTaskMaster(muid, "0");
//			status = ServerSoap.GetTaskMaster(muid, "0");
			for (int i = 0; i < Task.size(); i++) {
				Log.d("??", Task.get(i));
				if(Task.get(i).split(",")[6].equals("�´�")||Task.get(i).split(",")[6].equals("���")){
					status.add(Task.get(i).split(",")[6]);
					update.add(ServerSoap.GetCheckDetail(Task.get(i).split(",")[0]));}

			}
			Message msg = myHandler.obtainMessage();
			msg.obj = update;
			myHandler.sendMessage(msg);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)// ��дonKeyDown����
	{
		if (keyCode == 4) {// �����·��ؼ�
			Intent intent = new Intent();
			intent.setClass(UsualCheckActivity.this, Function_slect.class);
			intent.putExtra("user", loginUser.split(";")[0]);
			startActivity(intent);
			UsualCheckActivity.this.finish();
		}
		return false;
	}
}
