package com.zj.rfid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.Consts;

public class GexinSdkMsgReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GexinSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(Consts.CMD_ACTION)) {

		case Consts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			if (payload != null) {
				String data = new String(payload);

				Log.d("GexinSdkDemo", "Got Payload:" + data);
				if (Function_slect.tLogView != null)
					Function_slect.tLogView.append(data + "\n");

			}
			break;
		case Consts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			if (Function_slect.muid != null)
//				Function_slect.muid = cid;
			break;

		case Consts.BIND_CELL_STATUS:
			String cell = bundle.getString("cell");

			Log.d("GexinSdkDemo", "BIND_CELL_STATUS:" + cell);
			if (Function_slect.tLogView != null)
				Function_slect.tLogView.append("BIND_CELL_STATUS:" + cell + "\n");
			break;
		case Consts.THIRDPART_FEEDBACK:
			// sendMessage接口调用回执
			String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String actionid = bundle.getString("actionid");
			String result = bundle.getString("result");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GexinSdkDemo", "appid:" + appid);
			Log.d("GexinSdkDemo", "taskid:" + taskid);
			Log.d("GexinSdkDemo", "actionid:" + actionid);
			Log.d("GexinSdkDemo", "result:" + result);
			Log.d("GexinSdkDemo", "timestamp:" + timestamp);
			break;
		default:
			break;
		}
	}
}
