package com.github.aguilasa.smartphone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.aguilasa.common.Chronometer;
import com.github.aguilasa.common.Processing;

public class ScreenApp extends BaseScreen {

	private static final long serialVersionUID = -6145320864740849476L;

	private MulticastSocket multicastSocket = null;
	private SocketAddress socketAddress = null;
	private ReceiveReader receiveReader;
	private Thread receiveThread;
	private Sender sender;

	public ScreenApp() {
		super();

		socketAddress = new InetSocketAddress("228.5.6.7", 4321);

		try {
			multicastSocket = new MulticastSocket(4321);
			multicastSocket.setLoopbackMode(false);

			InetAddress localHost = InetAddress.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			multicastSocket.joinGroup(socketAddress, networkInterface);
		} catch (Exception e) {
			closeApplication(true);
		}

		receiveReader = new ReceiveReader();
		receiveThread = new Thread(receiveReader);
		receiveThread.start();
		sender = new Sender(2);
		sender.resume();
	}

	class ReceiveReader extends Processing {

		public ReceiveReader() {
			resume();
		}

		@Override
		public void doingRun() {
			byte[] buf = new byte[1024];
			DatagramPacket p = null;
			try {
				p = new DatagramPacket(buf, buf.length);
				multicastSocket.receive(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String value = new String(p.getData(), 0, p.getLength());
			try {
				JSONObject json = new JSONObject(value);
				if (json.has("type") && json.getInt("type") == 2) {
					System.out.println(json.getInt("port"));
					System.out.println(json.getString("ip"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class Sender extends Chronometer {

		public Sender(int seconds) {
			super(seconds);
		}

		@Override
		protected void task() {
			try {
				JSONObject json = new JSONObject();
				json.put("type", 1);
				String message = json.toString();
				byte[] b = message.getBytes();
				DatagramPacket packet = new DatagramPacket(b, 0, b.length, socketAddress);
				multicastSocket.send(packet);
			} catch (IOException | JSONException e) {
				this.pause();
			}
		}
	}

}
