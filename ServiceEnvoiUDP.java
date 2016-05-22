package Ringo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.UUID;

public class ServiceEnvoiUDP implements Runnable {

	private DatagramSocket dso;
	private Entite entite;
	
	public ServiceEnvoiUDP(DatagramSocket s, Entite entite)
	{
		this.dso = s;
		this.entite = entite;
	}
	
	public void run() 
	{
		Scanner sc = new Scanner(System.in);
		
		boolean isTest = false;
		boolean valid = false;
		
		String mess;
		String messSplit[];
		String realMess = null;
		String idm;
		
		while(true)
		{
			try
			{
				mess = sc.nextLine();
				messSplit = mess.split(" ");
				
				switch(messSplit[0])
				{
					case "WHOS":
						idm = UUID.randomUUID().toString().substring(0, 8);
						realMess = messSplit[0] + " " + idm;
						valid = true;
						this.entite.getidmMem().add(idm);//on ajoute l'id unique pour le mémoriser
						break;
					case "GBYE":
						idm = UUID.randomUUID().toString().substring(0, 8);
						realMess = messSplit[0] + " " + idm + " " + InetAddress.getLocalHost().getHostAddress() + " " + this.entite.getLportRecvMess() + " " + this.entite.getIpNextMachine() + " " + this.entite.getLportNextMachine();
						valid = true;
						this.entite.getidmMem().add(idm);
						break;
					case "TEST":
						idm = UUID.randomUUID().toString().substring(0, 8);
						realMess = messSplit[0] + " " + idm + " " + this.entite.getRing().getIpMulticast() + " " + this.entite.getRing().getPortMulticast();
						valid = true;
						this.entite.getidmMem().add(idm);
						isTest = true;
						break;
					default:
						valid = false;
						break;	
				}
				
				if(valid)
				{
					byte[] data = realMess.getBytes();
					InetSocketAddress ia = new InetSocketAddress(this.entite.getIpNextMachine(),this.entite.getLportNextMachine());
					DatagramPacket paquet = new DatagramPacket(data,data.length,ia);
					System.out.println("J'envoi " + new String(paquet.getData(),0,paquet.getLength()));
					this.dso.send(paquet);
					
					if(isTest)
					{
						Entite.ttl = true;
						int i = 0;
						while(Entite.ttl && i<5){
							System.out.println("...");
							Thread.sleep(1000);
							i++;
						}
						if(i >= 5){
							data = "DOWN".getBytes();
							ia = new InetSocketAddress(this.entite.getRing().getIpMulticast(),this.entite.getRing().getPortMulticast());
							paquet = new DatagramPacket(data,data.length,ia);
							System.out.println("J'envoi DOWN");
							this.dso.send(paquet);
						}
							
						isTest = false;
					}
				}
				else
				{
					System.err.println("Message inconnu ou invalide...");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(0);
				sc.close();
			}
		}
	}
}

