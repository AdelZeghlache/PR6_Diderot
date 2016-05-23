package Ringo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ServiceMulticast implements Runnable
{
	private MulticastSocket mso;
	private Entite entite;
	
	public ServiceMulticast(MulticastSocket mso, Entite entite)
	{
		this.mso = mso;
		this.entite = entite;
	}
	
	public void run() 
	{
		try
		{
			this.mso.joinGroup(InetAddress.getByName(this.entite.getRing().getFirst().getIpMulticast()));
			byte[]data=new byte[100];
			DatagramPacket paquet=new DatagramPacket(data,data.length);
			
			while(true)
			{
				this.mso.receive(paquet);
				String st = new String(paquet.getData(),0,paquet.getLength());
				System.out.println("From Multicast > " + st);
				
				String stSplit[] = st.split(" ");
				switch(stSplit[0])
				{
					case "DOWN":
						System.exit(1);
				}
				
			}
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
