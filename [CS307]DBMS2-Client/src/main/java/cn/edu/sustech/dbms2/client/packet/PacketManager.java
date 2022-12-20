package cn.edu.sustech.dbms2.client.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cn.edu.sustech.dbms2.client.packet.client.LoginPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;


public class PacketManager {
	
	private static PacketManager manager = new PacketManager();
	private HashMap<Integer, Class<? extends Packet>> packetCodes;
	
	private PacketManager() {
		this.init();
	}
	
	private void init() {
		packetCodes = new HashMap<>();
		packetCodes.put(LoginPacket.getStaticCode(), LoginPacket.class);
		packetCodes.put(LoginInfoPacket.getStaticCode(), LoginInfoPacket.class);
	}
	
	public static PacketManager getInstance() {
		return manager;
	}
	
	public Packet receivePacket(int len, byte[] packetBytes) {
		String msg = new String(packetBytes, 0, len);
		int index = msg.indexOf('@');
		if (index == -1) {
			return null;
		}
		int code = Integer.parseInt(msg.substring(0, index));
		String context = msg.substring(index + 1);
		Class<? extends Packet> packetClazz = packetCodes.get(code);
		try {
			Constructor<? extends Packet> constructor = packetClazz.getConstructor(String.class);
			return constructor.newInstance(context);
		} catch (Exception e) {
			//ThrowableHandler.handleThrowable(e);
		}
		return null;
	}
	
}
