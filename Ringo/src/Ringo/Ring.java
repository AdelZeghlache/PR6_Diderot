package Ringo;

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
	
	public String toString()
	{
		return this.ipMulticast + ":" + this.portMulticast;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof Ring))
			return false;
		Ring other = (Ring)o;
		return this.ipMulticast.equals(other.ipMulticast) && this.portMulticast == other.portMulticast;
	}
}
