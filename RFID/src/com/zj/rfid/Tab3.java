package com.zj.rfid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Tab3 extends Activity
{
	
	private String loginUser = null;
	public static enum WhichView//标志界面的安全枚举
	{
		MAIN,//主界面
		RZ	//日志界面	
	}
	
	static WhichView curr=null;		//记录当前界面
	static WhichView pre=null;		//记录上一个界面
	
	String stemp;
	
	//所有资源图片id的数组
	int[] titleIds={R.string.cc,R.string.gg,
			R.string.qbmy,R.string.lsdf,R.string.shg};
	//所有资源字符串id的数组
	int[] bodyIds={R.string.cc_body,R.string.gg_body,
			R.string.qbmy_body,R.string.lsdf_body,R.string.shg_body};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gotoMain(curr);
    } 
    
    public void gotoMain(WhichView preTemp)//去主界面的方法
    {
    	pre=preTemp;
    	this.setContentView(R.layout.tab3);
    	curr=WhichView.MAIN;
        
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		
        //初始化ListView
        ListView lv=(ListView)this.findViewById(R.id.ListView01);
        
        //为ListView准备内容适配器
        BaseAdapter ba=new BaseAdapter()
        {
			@Override
			public int getCount() {
				return 5;//总共5个选项
			}

			@Override
			public Object getItem(int arg0) { return null; }

			@Override
			public long getItemId(int arg0) { return 0; }

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				
				//初始化LinearLayout
				LinearLayout ll=new LinearLayout(Tab3.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);		//设置朝向	
				ll.setPadding(5,5,5,5);//设置四周留白
				//初始化TextView
				TextView tvTitle=new TextView(Tab3.this);
				tvTitle.setText(getResources().getText(titleIds[arg0]));//设置内容
				tvTitle.setTextSize(24);//设置字体大小
				tvTitle.setTextColor(Tab3.this.getResources().getColor(R.color.black));//设置字体颜色
				tvTitle.setPadding(5,5,5,5);//设置四周留白
			    tvTitle.setGravity(Gravity.LEFT);
				ll.addView(tvTitle);//添加到LinearLayout中		*/		
				return ll;
			}       	
        };
        
        lv.setAdapter(ba);//为ListView设置内容适配器
        
        //设置选项被单击的监听器
        lv.setOnItemClickListener(
           new OnItemClickListener()
           {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {//重写选项被单击事件的处理方法
				
				StringBuilder sb=new StringBuilder();//用StringBuilder动态生成信息
				sb.append(getResources().getText(bodyIds[arg2]));
				stemp=sb.toString();	
				gotoRZ(curr);
			}        	   
           }
        );
    }
    
    public void gotoRZ(WhichView preTemp)//去日志界面的方法
    {
    	pre=preTemp;
    	this.setContentView(R.layout.tab3);
    	curr=WhichView.RZ;
    	
    	this.setContentView(R.layout.diary_body);
		TextView tvBody=(TextView)this.findViewById(R.id.body);
		tvBody.setText(stemp);//信息设置进主界面TextView	
		tvBody.setTextSize(20);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//重写onKeyDown方法
    {
    	if(keyCode==4){//若按下返回键
			switch(curr){//当前界面
			case MAIN:
	            //System.exit(0);	//若是主界面，退出程序
				Intent intent = new Intent();
				intent.setClass(Tab3.this, Function_slect.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				Tab3.this.finish();
			break; 
			case RZ:
				gotoMain(curr);//若是日志界面，返回主界面
			break;			
			}
			return true;
		}
		return false;
    }
}
