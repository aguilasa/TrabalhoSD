package com.github.aguilasa.smartphone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

import com.github.aguilasa.common.Processing;

public class ScreenApp extends BaseScreen {

	private static final long serialVersionUID = -6145320864740849476L;

	private MulticastSocket multicastSocket = null;
	private SocketAddress socketAddress = null;
	private ReceiveReader receiveReader;
	private Thread receiveThread;

	public ScreenApp() {
		super();

		socketAddress = new InetSocketAddress("228.5.6.7", 4321);

		try {
			multicastSocket = new MulticastSocket(4321);
			multicastSocket.setLoopbackMode(false);

			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			multicastSocket.joinGroup(socketAddress, networkInterface);
		} catch (Exception e) {
			closeApplication(true);
		}
		
		receiveReader = new ReceiveReader();
		receiveThread = new Thread(receiveReader);
		receiveThread.start();
	}

	class ReceiveReader extends Processing {

		public ReceiveReader() {
			pause();
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
			//String line = new String(p.getData(), 0, p.getLength());
		}

	}

}
