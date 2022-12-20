package cn.edu.sustech.dbms2.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import cn.edu.sustech.dbms2.client.packet.Packet;
import cn.edu.sustech.dbms2.client.packet.PacketManager;

public class DBClient {
	
	private static final String host = "localhost";
	private static final int port = 23333;
	
	public DBClient() {}
	
	public Packet sendAndReceivePacket(Packet packet) throws IOException {
		try {
			Socket socket = new Socket();
			socket.setSoTimeout(10000);
			socket.connect(new InetSocketAddress(host, port));
			BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
			writer.write((packet.getCode() + "@" + packet.getContext()).getBytes());
			writer.flush();
			writer.close();
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
			byte[] bytes = new byte[1024 * 8];
			int len;
			if ((len = input.read(bytes)) != -1) {
				socket.close();
				return PacketManager.getInstance().receivePacket(len, bytes);
			}
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
}
