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
	private InetAddress ip;
	
	public ServiceMulticast(MulticastSocket mso, Entite entite, InetAddress ip)
	{
		this.mso = mso;
		this.entite = entite;
		this.ip = ip;
	}
	
	public void run() 
	{
		try
		{
			this.mso.joinGroup(ip);
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
						//Si on est une entité doubleur, on ne se coupe pas mais on envoi plus de message sur l'anneau cassé, sinon on quitte
						if(this.entite.getAlDests().size() >= 2)
						{
							for(int i = 0;i<this.entite.getRing().size();i++)
							{
								for(int j = 0;j<Entite.alRing.size();j++)
								{
									if(this.entite.getRing().get(i).equals(Entite.alRing.get(j)))
									{
										this.entite.getRing().remove(i);
										this.entite.getAlDests().remove(i);
									}
								}
							}
							Entite.alRing.clear();
						}
						else
						{
							Entite.alRing.clear();
							System.exit(1);
						}
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
