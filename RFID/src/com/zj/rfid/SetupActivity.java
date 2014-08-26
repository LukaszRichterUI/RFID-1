package com.zj.rfid;


import java.util.ArrayList;


import java.util.List;
import com.zj.net.ServerSoap;
import com.zj.rfid.R;
import com.zj.rfid.R.id;
import com.zj.rfid.R.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * 修复BUG：初始化过慢 in class updateThread
 * 每一条联一次网，等待修改web端
 * @author 陈坚填
 *
 * 
 */
public class SetupActivity extends Activity{
	private TextView Title_left;
	private String loginUser = null;
	private List<String> Task = new ArrayList<String>();
	private List<String> temp = new ArrayList<String>();
	private List<String> update = new ArrayList<String>();
	private List<String> Detail = new ArrayList<String>();
	private List<String> status = new ArrayList<String>();
	private List<Integer> id = new ArrayList<Integer>();
	private ListView lv;
	
	 
	private updateThread myThread;
	private Handler mHandler;
	private String muid;
	private MyHandler myHandler;
	private ArrayAdapter<String> adapter1;
	private ProgressDialog mDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.setup);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
        
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		String TitleUser = loginUser.split(",")[0];
		muid = intent.getStringExtra("user").split(",")[1];
		Title_left = (TextView) findViewById(R.id.title_left_text);
		TextView textView = (TextView) findViewById(R.id.title_textview1);
		textView.setVisibility(View.INVISIBLE);
		Title_left.setText("用户:" + TitleUser);
		lv=(ListView)this.findViewById(R.id.SetupTaskList);
		
		myHandler = new MyHandler();
		myThread = new updateThread();
		myThread.start();
		mDialog = new ProgressDialog(this);
		mDialog.setTitle("请稍候");
		mDialog.setMessage("正在加载数据！");
		mDialog.show();
    }
    
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			temp = (List) msg.obj;
			for (int i = 0; i < temp.size(); i++) {
				Detail.add("用户名：" + temp.get(i).split(",")[2] + "\n地址："
						+ temp.get(i).split(",")[8] + "\n工作单状态："
						+ status.get(i).split(",")[6]);
			}
			adapter1 = new ArrayAdapter<String>(SetupActivity.this,
					android.R.layout.simple_list_item_1, Detail);
			adapter1.notifyDataSetChanged();

			lv.setAdapter(adapter1);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if (id.contains(arg2)) {
						Intent intent = new Intent();
						intent.setClass(SetupActivity.this,
								showsetup.class);
						intent.putExtra("user",
								loginUser);
						intent.putExtra("Taskid", Task.get(arg2).split(",")[0]);
						intent.putExtra("state", "complete");
						intent.putExtra("spinner", "nomeg");
						startActivity(intent);
						SetupActivity.this.finish();
					} else {
						Intent intent = new Intent();
						intent.setClass(SetupActivity.this,
								SetupDetailActivity.class);
						intent.putExtra("user",
								loginUser);
						intent.putExtra("Taskid", Task.get(arg2).split(",")[0]);
						intent.putExtra("state", "doing");
						intent.putExtra("spinner", "nomeg");
						startActivity(intent);
						SetupActivity.this.finish();
					}
				}
			});
			mDialog.dismiss();
		}
		
	}
	
	private class updateThread extends Thread {
		@Override
		public void run() {
			Task = ServerSoap.GetTaskMaster(muid, String.valueOf(0));
			//初始化两次
			//status = ServerSoap.GetTaskMaster(muid, String.valueOf(0));
			status = Task;
			for (int i = 0; i < Task.size(); i++) {
				update.add(ServerSoap.GetSetupDetail(Task.get(i).split(",")[0]));
				if (status.get(i).split(",")[6].equals("完成")) {
					id.add(i);
				}
			}
			Message msg = myHandler.obtainMessage();
			msg.obj = update;
			myHandler.sendMessage(msg);
		}
	}
/*        if(!ServerSoap.GetTaskMaster(muid, String.valueOf(0),0).equals(null)){
       	Task = ServerSoap.GetTaskMaster(muid, String.valueOf(0),0);
//			Task = ServerSoap.getAreaString(0);
        	status = ServerSoap.GetTaskMaster(muid, String.valueOf(0),6);
		for(int i=0;i<Task.size();i++){
			Log.d("??",Task.get(i));
//			temp.add(ServerSoap.getTVstationString(Integer.valueOf(Task.get(i))));
			temp.add(ServerSoap.GetSetupDetail(Task.get(i)));
			if(status.get(i).equals("完成")){
				id.add(i);
			}
		}
		for(int i=0;i<temp.size();i++){
			Detail.add("用户名："+temp.get(i).split(",")[2]+"\n地址："+temp.get(i).split(",")[8]+"\n工作单状态："+status.get(i));
			}//*/

        
		//为ListView准备内容适配器
/*		BaseAdapter ba=new BaseAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return Detail.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				LinearLayout ll=new LinearLayout(SetupActivity.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setPadding(5,5,5,5);
				TextView tvTitle=new TextView(SetupActivity.this);
				tvTitle.setText(Detail.get(position));
				tvTitle.setPadding(5,5,5,5);
				tvTitle.setGravity(Gravity.LEFT);
				return ll;
			}};//

		        
		    lv.setAdapter(ba);//为ListView设置内容适配器
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Task);
        
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SetupActivity.this, SetupDetailActivity.class);
				intent.putExtra("user", loginUser+";"+Task.get(arg2).split(",")[0]);
				if(id.contains(arg2)){
					intent.putExtra("state", "complete");
				}else{
					intent.putExtra("state", "doing");
				}
				intent.putExtra("spinner", "nomeg");
				startActivity(intent);
				SetupActivity.this.finish();
			}
		});}
        
        
        
    }//*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
				Intent intent = new Intent();
				intent.setClass(SetupActivity.this, Function_slect.class);
				intent.putExtra("user",loginUser.split(";")[0]);
				startActivity(intent);
				SetupActivity.this.finish();
		}
		return false;
    }
}
