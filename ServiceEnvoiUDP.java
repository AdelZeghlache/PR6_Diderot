import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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
		while(true)
		{
			try
			{
				String mess = sc.nextLine();
				String messSplit[] = mess.split(" ");
				String realMess = "";
				boolean valid = false;
				switch(messSplit[0])
				{
					case "WHOS":
						String idm = UUID.randomUUID().toString().substring(0, 8);
						realMess = messSplit[0] + " " + idm;
						valid = true;
						this.entite.getidmMem().add(idm);//on ajoute l'id unique pour le mémoriser
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
				}
				else
				{
					System.err.println("Message inconnu ou invalide...");
				}
			}
			catch(Exception e)
			{
				sc.close();
			}
		}
	}
}
