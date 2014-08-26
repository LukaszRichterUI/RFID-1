package com.zj.rfid;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class MyService extends Service {
	private BluetoothService bs = null;
	private BTSHandler ConnectBluetoothHandler = new BTSHandler();

	@Override
	public IBinder onBind(Intent arg0) {
		bs = new BluetoothService(MyService.this, ConnectBluetoothHandler);
		IBinder fb = new FirstBinder();
		return fb;
	}

	public class FirstBinder extends Binder {

		public void connectTodevice(BluetoothDevice bd) {
			bs.connectTodevice(bd);
		}

		public void Stop() {
			bs.stop();
		}

		public void Exit() {
			bs.exit();
		}

		public void Write(byte[] b) {
			bs.write(b);
		}

		public int Gets() {
			return bs.getState();
		}
	}

	private class BTSHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConstDefine.success: {
				Intent intent_success = new Intent("ConnectResult");
				intent_success.putExtra("ConnectResult", ConstDefine.success);
				MyService.this.sendBroadcast(intent_success);
				break;
			}
			case ConstDefine.faild: {
				Intent intent_faild = new Intent("ConnectResult");
				intent_faild.putExtra("ConnectResult", ConstDefine.faild);
				MyService.this.sendBroadcast(intent_faild);
				break;
			}
			case ConstDefine.BluetoothState: {
				Intent intent_state = new Intent("BluetoothState");
				// 发送广播 ：蓝牙连接状态
				switch (msg.arg1) {
				case ConstDefine.STATE_NONE:
					intent_state.putExtra("BluetoothState",
							ConstDefine.STATE_NONE);
					MyService.this.sendBroadcast(intent_state);
					break;
				case ConstDefine.STATE_LISTEN:
					intent_state.putExtra("BluetoothState",
							ConstDefine.STATE_LISTEN);
					MyService.this.sendBroadcast(intent_state);
					break;
				case ConstDefine.STATE_CONNECTING:
					intent_state.putExtra("BluetoothState",
							ConstDefine.STATE_CONNECTING);
					MyService.this.sendBroadcast(intent_state);
					break;
				case ConstDefine.STATE_CONNECTED:
					intent_state.putExtra("BluetoothState",
							ConstDefine.STATE_CONNECTED);
					MyService.this.sendBroadcast(intent_state);
					break;

				default:
					break;
				}
				break;
			}
			default:
				break;
			}
		}
	}
}