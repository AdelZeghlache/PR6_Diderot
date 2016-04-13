public class Ring
{
	private String ipMulticast;
	private int portMulticast;
	
	public Ring(String ipMulticast, int portMulticast)
	{
		this.ipMulticast = ipMulticast;
		this.portMulticast = portMulticast;
	}

	public String getIpMulticast() {
		return ipMulticast;
	}

	public void setIpMulticast(String ipMulticast) {
		this.ipMulticast = ipMulticast;
	}

	public int getPortMulticast() {
		return portMulticast;
	}

	public void setPortMulticast(int portMulticast) {
		this.portMulticast = portMulticast;
	}
}