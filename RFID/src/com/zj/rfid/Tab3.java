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
	public static enum WhichView//��־����İ�ȫö��
	{
		MAIN,//������
		RZ	//��־����	
	}
	
	static WhichView curr=null;		//��¼��ǰ����
	static WhichView pre=null;		//��¼��һ������
	
	String stemp;
	
	//������ԴͼƬid������
	int[] titleIds={R.string.cc,R.string.gg,
			R.string.qbmy,R.string.lsdf,R.string.shg};
	//������Դ�ַ���id������
	int[] bodyIds={R.string.cc_body,R.string.gg_body,
			R.string.qbmy_body,R.string.lsdf_body,R.string.shg_body};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gotoMain(curr);
    } 
    
    public void gotoMain(WhichView preTemp)//ȥ������ķ���
    {
    	pre=preTemp;
    	this.setContentView(R.layout.tab3);
    	curr=WhichView.MAIN;
        
		Intent intent = getIntent();
		loginUser = intent.getStringExtra("user");
		
        //��ʼ��ListView
        ListView lv=(ListView)this.findViewById(R.id.ListView01);
        
        //ΪListView׼������������
        BaseAdapter ba=new BaseAdapter()
        {
			@Override
			public int getCount() {
				return 5;//�ܹ�5��ѡ��
			}

			@Override
			public Object getItem(int arg0) { return null; }

			@Override
			public long getItemId(int arg0) { return 0; }

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				
				//��ʼ��LinearLayout
				LinearLayout ll=new LinearLayout(Tab3.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);		//���ó���	
				ll.setPadding(5,5,5,5);//������������
				//��ʼ��TextView
				TextView tvTitle=new TextView(Tab3.this);
				tvTitle.setText(getResources().getText(titleIds[arg0]));//��������
				tvTitle.setTextSize(24);//���������С
				tvTitle.setTextColor(Tab3.this.getResources().getColor(R.color.black));//����������ɫ
				tvTitle.setPadding(5,5,5,5);//������������
			    tvTitle.setGravity(Gravity.LEFT);
				ll.addView(tvTitle);//��ӵ�LinearLayout��		*/		
				return ll;
			}       	
        };
        
        lv.setAdapter(ba);//ΪListView��������������
        
        //����ѡ������ļ�����
        lv.setOnItemClickListener(
           new OnItemClickListener()
           {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {//��дѡ������¼��Ĵ�����
				
				StringBuilder sb=new StringBuilder();//��StringBuilder��̬������Ϣ
				sb.append(getResources().getText(bodyIds[arg2]));
				stemp=sb.toString();	
				gotoRZ(curr);
			}        	   
           }
        );
    }
    
    public void gotoRZ(WhichView preTemp)//ȥ��־����ķ���
    {
    	pre=preTemp;
    	this.setContentView(R.layout.tab3);
    	curr=WhichView.RZ;
    	
    	this.setContentView(R.layout.diary_body);
		TextView tvBody=(TextView)this.findViewById(R.id.body);
		tvBody.setText(stemp);//��Ϣ���ý�������TextView	
		tvBody.setTextSize(20);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)//��дonKeyDown����
    {
    	if(keyCode==4){//�����·��ؼ�
			switch(curr){//��ǰ����
			case MAIN:
	            //System.exit(0);	//���������棬�˳�����
				Intent intent = new Intent();
				intent.setClass(Tab3.this, Function_slect.class);
				intent.putExtra("user",loginUser);
				startActivity(intent);
				Tab3.this.finish();
			break; 
			case RZ:
				gotoMain(curr);//������־���棬����������
			break;			
			}
			return true;
		}
		return false;
    }
}
