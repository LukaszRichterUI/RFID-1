package com.zj.net;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.ksoap2.transport.HttpTransportSE;

import com.zj.rfid.ConstDefine;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServerSoap {
	// 定义Web Service的命名空间
	static final String SERVICE_NS = "http://WebXml.com.cn/";
	static final String NS = "http://tempuri.org/";
	// 定义Web Service提供服务的URL
	static final String SERVICE_URL = "http://webservice.webxml.com.cn/webservices/ChinaTVprogramWebService.asmx";
	static final String URL = "http://59.37.161.150/DGMService.asmx";
	
	String mUser,mPassword;
	String mID;
	String mStyle;
	Context mContext;
	public ServerSoap(Context context,String User,String Password){
		mContext = context;
		mUser = User;
		mPassword = Password;
	}
	public ServerSoap(Context context,String ID){
		mContext = context;
		mID = ID;
	}
	public void getUser(){
		String User = null;
		try {
			String METHOD_NAME = "Get_User_Grant";
			//1.实例化SoapObject对象  
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			Log.d("Uer:"+mUser, "PSW:"+mPassword);
			//2.如果方法需要参数，设置参数  
			request.addProperty("uid", mUser);
			request.addProperty("psw", mPassword);
			//3.设置Soap的请求信息,参数部分为Soap协议的版本号  
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			//4.构建传输对象  	
			try {
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
//				MyAndroidHttpTransport httpTranstation = new MyAndroidHttpTransport(URL, 10000);
				httpTranstation.debug = true;
				

			//5.访问WebService,第一个参数为命名空间 + 方法名,第二个参数为Envelope对象
				
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				//6.解析返回的数据  
				String tempString = envelope.getResponse().toString();
				Log.d("unlock",tempString);
				User = tempString.substring(tempString.indexOf("=")+1, tempString.indexOf(","));	
				Log.d("unlock",User);
			Intent mIntent = new Intent(ConstDefine.Action_login); 
			mIntent.putExtra(ConstDefine.Action_login, User); 
			mContext.sendBroadcast(mIntent); 
			}catch (Exception e) {
				// TODO: handle exception
				Log.d("", "Exception连接超时");
				Intent mIntent = new Intent(ConstDefine.Action_login); 
				mIntent.putExtra(ConstDefine.Action_login, ConstDefine.login_faild); 
				mContext.sendBroadcast(mIntent); 
			}
		} catch (Exception e) {
			Log.e("no response", "读取信息，返回信息错误");
			e.printStackTrace();
		}
		 
	}
	
	public static List<String> GetTaskMaster(String muid,String mstyle)
	{
		String[] s = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			String METHOD_NAME = "Get_Task_Master";
			//1.实例化SoapObject对象  
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			//Log.d("Uer:"+mUser, "PSW:"+mPassword);
			//2.如果方法需要参数，设置参数  
			request.addProperty("uid", muid);
			request.addProperty("style", mstyle);
			//Log.d("222",muid+mPassword);
			//3.设置Soap的请求信息,参数部分为Soap协议的版本号  
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			//4.构建传输对象  	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
//				MyAndroidHttpTransport httpTranstation = new MyAndroidHttpTransport(URL, 10000);
				httpTranstation.debug = true;
			//5.访问WebService,第一个参数为命名空间 + 方法名,第二个参数为Envelope对象
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				//6.解析返回的数据  
				String tempString = envelope.getResponse().toString();
				Log.d("xxx",tempString);
				Log.d("unlock","ServerSoap.GetTaskMaster------>"+tempString);
				s = tempString.split(";");
				for(int i = 0;i<s.length-1;i++){
					if(!s[i].split("=")[1].equals("null")){
					s[i] = s[i].split("=")[1];
//					s[i] = s[i].split(",")[arg0];
					result.add(s[i]);
					}}
		} catch (Exception e) {
			Log.e("no response", "读取信息，返回信息错误");
			e.printStackTrace();
		}
		return result;
	}
	
	public static String GetTaskList(String muid , String mstyle){
		String str = null;
		try{
			String METHOD_NAME = "Get_Task_List";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("uid", muid);
			request.addProperty("style", mstyle);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				str = envelope.getResponse().toString();
/*				String[] s = str.split("string=");


				for(int i=1;i<s.length;i++){
					if(!s[i].equals("null; ")&&!s[i].equals("null; }")){
						s0 = s0+s[i];
						
					}
				}
				result = s0.split("null")[1];//str.substring(str.indexOf("=")+1, str.indexOf(";"));//*/
		}catch(Exception e){
			e.printStackTrace();
			}
		return str;
		
	}
	
	public static String GetSetupDetail(String mtaskid){
		String result = null;
		String s0 = null;
		try{
			String METHOD_NAME = "Get_Setup_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			Log.d("ser",mtaskid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				str = str.substring(str.indexOf("{")+1, str.indexOf("}"));
				Log.d("str",str);
				if(str!=null){
				String[] s = str.split("string=");
				Log.d("s",s[1]);
				for(int i=1;i<s.length;i++){
					s0 = s0+s[i];
				}
				result = s0.split("null")[1];
				Log.d("unlock","ServerSoap.GetSetupDetail--->"+result);
				//ServerSoap.GetSetupDetail--->201310061001,00000001,A小区,,,,,,江门市A小区,,,,; 

				}else{result = "nomeg";}
		}catch(Exception e){
			e.printStackTrace();
			}
		return result;
		
	}
	
	public static void SetSetupDetail(String mtaskid,String mcustid,String mcustname,
			String mlockserial,String mlocknumber,String mcolor,String msetpos,
			String msettime,String maddr,String msetuserid,String mmeterserial,
			String mmeternumber,String mnote,String mtype,String moldlocknumber){
		//String result = null;
		try{
			String METHOD_NAME = "Set_Setup_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			request.addProperty("custid",mcustid);
			request.addProperty("custname",mcustname);
			request.addProperty("lockserial",mlockserial);
			request.addProperty("locknumber",mlocknumber);
			request.addProperty("color",mcolor);
			request.addProperty("setpos",msetpos);
			request.addProperty("settime",msettime);
			request.addProperty("addr",maddr);
			request.addProperty("setuserid",msetuserid);
			request.addProperty("meterserial",mmeterserial);
			request.addProperty("meternumber",mmeternumber);
			request.addProperty("note",mnote);
			request.addProperty("type",mtype);
			request.addProperty("oldlocknumber",moldlocknumber);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				Log.d("str",str);
				//result = str.substring(str.indexOf("=")+1, str.indexOf(";"));
				//Log.d("result",result);
		}catch(Exception e){
			e.printStackTrace();
			}
		
	}
	
	public static void UPSetupDetail(String mtaskid,String muid){
		//String result = null;
		try{
			String METHOD_NAME = "UP_Setup_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			request.addProperty("uid",muid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				Log.d("str",str);
				//result = str.substring(str.indexOf("=")+1, str.indexOf(";"));
				//Log.d("result",result);
		}catch(Exception e){
			e.printStackTrace();
			}
		
	}
	
	public static String GetCheckDetail(String mtaskid){
		String result = null;
		String s0 = null;
		try{
			String METHOD_NAME = "Get_Check_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			Log.d("Soap_taskid",mtaskid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				Log.d("str",str);
				String[] s = str.split("string=");
				Log.d("s",s[1]);
//				if()
				for(int i=1;i<s.length;i++){
					if(!s[i].equals("null; ")&&!s[i].equals("null; }")){
						s0 = s0+s[i];
						//Log.d("s0",s0);
						
					}
				}
				result = s0.split("null")[1];//str.substring(str.indexOf("=")+1, str.indexOf(";"));
				Log.d("result",result);
		}catch(Exception e){
			e.printStackTrace();
			}
		return result;
		
	}
	
	public static void SetCheckDetail(String mtaskid, String mlockserial, String msetresult,String muid){
		//String result = null;
		try{
			String METHOD_NAME = "Set_Check_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			request.addProperty("lockserial", mlockserial);
			request.addProperty("setresult", msetresult);
			request.addProperty("uid", muid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				//result = str.substring(str.indexOf("=")+1, str.indexOf(";"));
				Log.d("omg",str);
		}catch(Exception e){
			e.printStackTrace();
			}
		
	}

	public static void InsertCheckDetail(String mtaskid, String mlockserial, String mStatus){
		//String result = null;
		try{
			String METHOD_NAME = "Insert_Check_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			request.addProperty("lockserial", mlockserial);
			request.addProperty("status", mStatus);
//			request.addProperty("uid", muid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				//result = str.substring(str.indexOf("=")+1, str.indexOf(";"));
				Log.d("omg",str);
		}catch(Exception e){
			e.printStackTrace();
			}
		
	}
	
	public static void UPCheckDetail1(String mtaskid){
		//String result = null;
		try{
			String METHOD_NAME = "UP_Check_Detail1";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
//			request.addProperty("uid",muid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				Log.d("UPCheckDetai",str);
				//result = str.substring(str.indexOf("=")+1, str.indexOf(";"));
				//Log.d("result",result);
		}catch(Exception e){
			e.printStackTrace();
			}
		
	}
	
	public static void UPCheckDetail(String mtaskid,String muid){
		//String result = null;
		try{
			String METHOD_NAME = "UP_Check_Detail";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("taskid", mtaskid);
			request.addProperty("uid",muid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				Log.d("UPCheckDetai",str);
				//result = str.substring(str.indexOf("=")+1, str.indexOf(";"));
				//Log.d("result",result);
		}catch(Exception e){
			e.printStackTrace();
			}
		
	}
	
	public static String GetLockInfo(String mlocknumber){
		String result = null;
		String s0 = null;
		try{
			String METHOD_NAME = "Get_Lock_Info";
			SoapObject request = new SoapObject(NS, METHOD_NAME);
			request.addProperty("LockNumber", mlocknumber);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);	
			envelope.bodyOut = request;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			 	
				AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
				httpTranstation.debug = true;
				httpTranstation.call(NS+METHOD_NAME, envelope);	
				String str = envelope.getResponse().toString();
				String[] s = str.split("string=");

				for(int i=1;i<s.length;i++){
					if(!s[i].equals("null; ")&&!s[i].equals("null; }")){
						s0 = s0+s[i];
						
					}
				}
				result = s0.split("null")[1];//str.substring(str.indexOf("=")+1, str.indexOf(";"));
				Log.d("result",result);
		}catch(Exception e){
			e.printStackTrace();
			}
		return result;
		
	}
	
	public void GetLockInfo(){
	String temp = null;
	String result = null;
	String[] s = null;
	try {
//		String NAMESPACE = "GetService";
//		String URL = "http://59.37.161.150/service.asmx";
		String METHOD_NAME = "Get_Lock_Info";
//		String SOAP_ACTION = "GetService/get_RFID";
		SoapObject request = new SoapObject(NS, METHOD_NAME);
		request.addProperty("LockNumber", mID);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);	
		envelope.bodyOut = request;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
//		HttpTransportSE httpTransportSE = new HttpTransportSE(URL, 10000);
		
		httpTranstation.debug = true;
		httpTranstation.call(NS+METHOD_NAME, envelope);
		temp = envelope.getResponse().toString();
		Log.d("sc",temp);
	} catch (Exception e) {
		e.printStackTrace();
	}
	temp = temp.substring(temp.indexOf("=")+1, temp.indexOf(";"));
	if(!temp.equals("anyType{}")){	

	
	result = temp;
	}else{

	result = "没有相关数据";
	}
	
	Intent mIntent = new Intent(ConstDefine.Action_getRFID); 
    mIntent.putExtra(ConstDefine.Action_getRFID, result); 
    mContext.sendBroadcast(mIntent); 
	
}
	
	public void getRFID(){
	String temp = null;
	String result = null;
	String[] s = null;
	try {
//		String NAMESPACE = "GetService";
//		String URL = "http://59.37.161.150/service.asmx";
		String METHOD_NAME = "Get_Lock_List";
//		String SOAP_ACTION = "GetService/get_RFID";
		SoapObject request = new SoapObject(NS, METHOD_NAME);
		request.addProperty("LockNumber", mID);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);	
		envelope.bodyOut = request;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		AndroidHttpTransport httpTranstation = new AndroidHttpTransport(URL);
//		HttpTransportSE httpTransportSE = new HttpTransportSE(URL, 10000);
		
		httpTranstation.debug = true;
		httpTranstation.call(NS+METHOD_NAME, envelope);
		temp = envelope.getResponse().toString();
		Log.d("sc",temp);
	} catch (Exception e) {
		e.printStackTrace();
	}
//	
	if(!temp.equals("anyType{}")){	

	temp = temp.substring(temp.indexOf("=")+1, temp.indexOf(";"));
	s = temp.split(",");
	result = "封印编号:"+s[0]+"\n封印颜色:"+s[1]+"\n加封位置:"+s[2]+"\n电表号:"+s[3]+"\n电表识别码:"
			+s[4]+"\n客户编号:"+s[5]+"\n客户名称:"+s[6]+"\n加封人员:"+s[7]+"\n加封时间:"+s[8]+"\n状态:"+s[9];
	}else{

	result = "没有相关数据";
	}
	
	Intent mIntent = new Intent(ConstDefine.Action_getRFID); 
    mIntent.putExtra(ConstDefine.Action_getRFID, result); 
    mContext.sendBroadcast(mIntent); 
	
}  


}
