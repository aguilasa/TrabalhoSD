package com.github.aguilasa.server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.github.aguilasa.common.SocketCommon;

public class SendTest {
	public final static int SOCKET_PORT = 13267;
	public final static String SERVER = "127.0.0.1";

	public static void main(String[] args) throws IOException {

		try (Socket socket = new Socket(SERVER, SOCKET_PORT);) {
			File[] files = new File("files/").listFiles();
			SocketCommon.sendMultipleFiles(files, socket);
		}
	}
}
