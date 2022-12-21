package cn.edu.sustech.dbms2.client.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cn.edu.sustech.dbms2.client.ThrowableHandler;
import cn.edu.sustech.dbms2.client.packet.client.CityCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CompanyCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CourierCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ItemPacket;
import cn.edu.sustech.dbms2.client.packet.client.LoginPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipPacket;
import cn.edu.sustech.dbms2.client.packet.client.StaffPacket;
import cn.edu.sustech.dbms2.client.packet.server.CityCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CompanyCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ContainerInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CourierCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.StaffInfoPacket;
import cn.edu.sustech.dbms2.client.packet.client.ContainerPacket;

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
		packetCodes.put(CompanyCountPacket.getStaticCode(), CompanyCountPacket.class);
		packetCodes.put(CompanyCountInfoPacket.getStaticCode(), CompanyCountInfoPacket.class);
		packetCodes.put(CityCountPacket.getStaticCode(), CityCountPacket.class);
		packetCodes.put(CityCountInfoPacket.getStaticCode(), CityCountInfoPacket.class);
		packetCodes.put(CourierCountPacket.getStaticCode(), CourierCountPacket.class);
		packetCodes.put(CourierCountInfoPacket.getStaticCode(), CourierCountInfoPacket.class);
		packetCodes.put(ShipCountPacket.getStaticCode(), ShipCountPacket.class);
		packetCodes.put(ShipCountInfoPacket.getStaticCode(), ShipCountInfoPacket.class);
		packetCodes.put(ContainerPacket.getStaticCode(), ContainerPacket.class);
		packetCodes.put(ContainerInfoPacket.getStaticCode(), ContainerInfoPacket.class);
		packetCodes.put(ShipPacket.getStaticCode(), ShipPacket.class);
		packetCodes.put(ShipInfoPacket.getStaticCode(), ShipInfoPacket.class);
		packetCodes.put(ItemPacket.getStaticCode(), ItemPacket.class);
		packetCodes.put(ItemInfoPacket.getStaticCode(), ItemInfoPacket.class);
		packetCodes.put(StaffPacket.getStaticCode(), StaffPacket.class);
		packetCodes.put(StaffInfoPacket.getStaticCode(), StaffInfoPacket.class);
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
			ThrowableHandler.handleThrowable(e);
		}
		return null;
	}
	
}
