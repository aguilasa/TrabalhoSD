package com.github.aguilasa.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.aguilasa.common.SocketCommon;

public class ServerBackup {
	public final static int SOCKET_PORT = 13267;

	public static void main(String[] args) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT);) {
			while (true) {
				System.out.println("Esperando...");
				try (Socket socket = serverSocket.accept();) {
					System.out.println("Conexão aceita: " + socket);
					SocketCommon.receiveMultipleFiles("received", socket);
					System.out.println("Terminado.");
				}
			}
		}
	}
}
