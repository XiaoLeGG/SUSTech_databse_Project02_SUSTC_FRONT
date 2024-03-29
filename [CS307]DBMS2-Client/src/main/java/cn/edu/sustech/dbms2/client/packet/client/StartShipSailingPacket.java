package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class StartShipSailingPacket extends Packet {
	
	private String context;
	
	public StartShipSailingPacket(String cookie, String ship) {
		this.context = cookie + "$" + ship;
	}
	
	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 33;
	}
	
}
