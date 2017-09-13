package com.github.aguilasa.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketCommon {

	public static void sendMultipleFiles(File[] files, Socket socket) throws IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream()); DataOutputStream dos = new DataOutputStream(bos)) {
			dos.writeInt(files.length);

			for (File file : files) {
				dos.writeLong(file.length());

				dos.writeUTF(file.getName());

				try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
					int read = 0;
					while ((read = bis.read()) != -1) {
						bos.write(read);
					}
				}
			}
		}
	}

	public static void receiveMultipleFiles(String dirPath, Socket socket) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream()); DataInputStream dis = new DataInputStream(bis);) {
			int filesCount = dis.readInt();
			File[] files = new File[filesCount];

			for (int i = 0; i < filesCount; i++) {
				long fileLength = dis.readLong();
				String fileName = dis.readUTF();

				files[i] = new File(dirPath + "/" + fileName);

				try (FileOutputStream fos = new FileOutputStream(files[i]); BufferedOutputStream bos = new BufferedOutputStream(fos);) {

					for (int j = 0; j < fileLength; j++) {
						bos.write(bis.read());
					}
				}
			}
		}
	}

}
