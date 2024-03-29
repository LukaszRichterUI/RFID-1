package com.dkt.activity;

import com.zj.rfid.R;
import com.zj.rfid.fragment.HomeFragment;
import com.zj.rfid.fragment.MoreFragment;
import com.zj.rfid.fragment.BuletoothFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class Select extends FragmentActivity{


	//定义数组来存放Fragment界面
		private Class<?> fragmentArray[] = {HomeFragment.class,BuletoothFragment.class,MoreFragment.class};
		
		//定义数组来存放按钮图片
		private int mImageViewArray[] = {R.drawable.tab_home_btn,R.drawable.tab_bluetooth_btn,R.drawable.tab_more_btn};
		
		//Tab选项卡的文字
		private String mTextviewArray[] = {"首页", "蓝牙","更多"};

		private LayoutInflater layoutInflater;

		private FragmentTabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		
		initView();
		
			
	}

	private void initView() {
		layoutInflater = LayoutInflater.from(this);
						
				//实例化TabHost对象，得到TabHost
				mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
				mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);	
				
				//得到fragment的个数
				int count = fragmentArray.length;	
						
				for(int i = 0; i < count; i++){	
					//为每一个Tab按钮设置图标、文字和内容
					TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
					//将Tab按钮添加进Tab选项卡中
					mTabHost.addTab(tabSpec, fragmentArray[i], null);
					//设置Tab按钮的背景
					mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
				}
	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
	
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		
		TextView textView = (TextView) view.findViewById(R.id.textview);		
		textView.setText(mTextviewArray[index]);
	
		return view;
	}
		
		
}
