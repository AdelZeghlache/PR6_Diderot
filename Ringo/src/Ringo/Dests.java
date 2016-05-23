package Ringo;

public class Dests {
	private String ip;
	private int port;
	
	public Dests(String _ip, int _port)
	{
		this.ip = _ip;
		this.port = _port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
