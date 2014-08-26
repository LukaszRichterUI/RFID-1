package com.zj.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothService {
	// Debugging
	private static final String TAG = "BluetoothService-zj";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME = "RFID";

	// Unique UUID for this application
	// private static final UUID MY_UUID =
	// UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	private Context mContext;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	private boolean finish_throwflag = true;

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothService(Context context, Handler handler) {
		mContext = context;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;
		// Give the new state to the Handler so the UI Activity can update
		if (finish_throwflag) {
			mHandler.obtainMessage(ConstDefine.BluetoothState, state, -1)
					.sendToTarget();
		}

	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to listen on a BluetoothServerSocket
		// if (mAcceptThread == null) {
		// mAcceptThread = new AcceptThread();
		// mAcceptThread.start();
		// }
		setState(STATE_LISTEN);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void connectTodevice(BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();
		setState(STATE_CONNECTED);

		// Send the name of the connected device back to the UI Activity
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		try {
			r.write(out);
		} catch (Exception e) {
			// TODO: handle exception
			connectionLost();
		}

	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(STATE_LISTEN);
		mHandler.obtainMessage(ConstDefine.faild).sendToTarget();
		Log.e("", "connect error");
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_LISTEN);
		mHandler.obtainMessage(ConstDefine.faild).sendToTarget();
		// Send a failure message back to the Activity

	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			int sdk = Integer.parseInt(android.os.Build.VERSION.SDK);
			Log.d("sdk version is:", String.valueOf(sdk));
			if (sdk >= 10) {
				Log.d("", "createInsecureRfcommSocketToServiceRecord");
				try {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("", "createRFcommSocket error");
					e.printStackTrace();
				}

			} else {
				Log.d("", "createRfcommSocketToServiceRecord");
				try {
					tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("", "createRFcommSocket error");
					e.printStackTrace();
				}
			}

			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			if (mAdapter.isDiscovering()) {
				mAdapter.cancelDiscovery();
			}

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				Log.d("", "正在连接");
				mmSocket.connect();
				Log.d("", "连接成功");
				mHandler.obtainMessage(ConstDefine.success).sendToTarget();
			} catch (IOException e) {
				connectionFailed();
				e.printStackTrace();
				// Close the socket
				try {
					Log.e("", "连接失败");
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG,
							"unable to close() socket during connection failure",
							e2);
				}
				// Start the service over to restart listening mode
				// BluetoothService.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
			return;
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private BluetoothSocket mmSocket;
		private InputStream mmInStream = null;
		private OutputStream mmOutStream = null;
		private InputStream inStream = null;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			mConnectThread = null;
			byte[] buffer = new byte[1024];
			// Keep listening to the InputStream while connected
			Log.d("", "start while true");
			int bytes;
			while (true) {
				try {
					// Read from the InputStream
					Log.d("", "start read instream");
					bytes = mmInStream.read(buffer);
					Log.d("", "exit read instream");
					String data = bytes2HexString(buffer);
					if (data.contains("55AA")) {
						data = data.substring(0, data.indexOf("AA55") + 4);
						Log.d("get data", data);
						String test = data.substring(12, 14);
						if (test.equals("FF") || test.equals("00")) {
							data = "没有读取到卡片";
							// return data;
						} else if (ConstDefine.RFID_Response_ID == ConstDefine.SetReadMode) {
							String data1 = data.substring(12, 14);
							String data2 = data.substring(14, 16);
							if (data1.equals("17")) {
								if (data2.equals("01")) {
									data = "设置盘点模式成功";
								} else {
									data = "设置盘点模式失败";
								}
							} else if (data1.equals("19")) {
								if (data2.equals("01")) {
									data = "关闭盘点模式成功";
								} else {
									data = "关闭盘点模式失败";
								}
							} else {
								data = "操作失败";
							}
						} else {
							try {
								data = data.substring(14, data.indexOf("AA55"));
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
						Intent mIntent = new Intent(ConstDefine.Action_NAME);
						switch (ConstDefine.RFID_Response_ID) {
						case ConstDefine.SearchCard:
							mIntent.putExtra("type",
									ConstDefine.Action_SearchCard);
							mIntent.putExtra(ConstDefine.Action_SearchCard,
									data);
							break;
						case ConstDefine.RFU: {
							mIntent.putExtra(ConstDefine.Action_RFU, data);
							mIntent.putExtra("type", ConstDefine.Action_RFU);
						}
							break;
						case ConstDefine.EPC: {
							mIntent.putExtra(ConstDefine.Action_EPC, data);
							mIntent.putExtra("type", ConstDefine.Action_EPC);
						}
							break;
						case ConstDefine.TID:
							mIntent.putExtra(ConstDefine.Action_TID, data);
							mIntent.putExtra("type", ConstDefine.Action_TID);
							break;
						case ConstDefine.USR:
							mIntent.putExtra(ConstDefine.Action_USR, data);
							mIntent.putExtra("type", ConstDefine.Action_USR);
							break;
						case ConstDefine.ReadFirmware:
							mIntent.putExtra(ConstDefine.Action_ReadFirmware,
									data);
							mIntent.putExtra("type",
									ConstDefine.Action_ReadFirmware);
							break;
						case ConstDefine.SetReadMode:
							mIntent.putExtra(ConstDefine.Action_SetReadMode,
									data);
							mIntent.putExtra("type",
									ConstDefine.Action_SetReadMode);
							break;
						default:
							break;
						}
						mContext.sendBroadcast(mIntent);
					} else {
					}
				} catch (IOException e) {
					if (finish_throwflag) {
						Log.e(TAG, "disconnected", e);
						connectionLost();
					}
					break;
				}
			}

		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				Log.d(NAME, "Thread write byte:" + bytes2HexString(buffer));
				mmOutStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				Log.d("", "connected thread cancel");
				mmInStream.close();
				mmOutStream.close();
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	public static byte[] HexString2Bytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		// Log.d("string length", String.valueOf(src.length()));
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public void exit() {
		finish_throwflag = false;
	}
}
