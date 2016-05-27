package Ringo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class AppDiff extends Application
{
	private final String id = "DIFF####";
	
	public String getId() {
		return id;
	}

	public void exec(String idm,Entite entite,DatagramSocket dso)
	{
		try
		{
			Scanner sc = new Scanner(System.in);
			System.out.println("Votre message : ");
			String message = sc.nextLine();
			String ret = "APPL " + idm + " " + this.id + " " + message.length() + " " + message; 
			
			byte[] data = ret.getBytes();
			InetSocketAddress ia;
			DatagramPacket paquet;
			
			for(int i = 0;i<entite.getAlDests().size();i++)
			{
				ia = new InetSocketAddress(entite.getAlDests().get(i).getIp(),entite.getAlDests().get(i).getPort());
				paquet = new DatagramPacket(data,data.length,ia);
				if(entite.isVerbeux())
					System.out.println("J'envoie " + new String(paquet.getData(),0,paquet.getLength()));
				dso.send(paquet);
				entite.getidmMem().add(idm);
			}
			//sc.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void traiter(String mess,Entite entite,DatagramSocket dso)
	{
		String messSplit[] = mess.split(" ");
		String fin = "";
		for(int i = 4;i<messSplit.length;i++)
			fin += " " + messSplit[i];
		System.out.println("DIFF >" + fin);
	}
}
