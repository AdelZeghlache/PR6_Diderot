import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.UUID;

public class ServiceRecvUDP implements Runnable
{
	private DatagramSocket dso;
	private Entite entite;
	
	public ServiceRecvUDP(DatagramSocket dso, Entite entite)
	{
		this.dso = dso;
		this.entite = entite;
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				byte[] data = new byte[512];
				DatagramPacket paquet = new DatagramPacket(data,data.length);
				this.dso.receive(paquet);
				String st = new String(paquet.getData(),0,paquet.getLength());
				System.out.println("J'ai recu " + st);
				String[] stSplit = st.split(" ");
				String idm = stSplit[1];
				
				if(!this.entite.getidmMem().contains(idm))
				{
					//Je retransmet le message que j'ai recu
					InetSocketAddress ia = new InetSocketAddress(this.entite.getIpNextMachine(),this.entite.getLportNextMachine());
					paquet = new DatagramPacket(st.getBytes(),st.getBytes().length,ia);
					System.out.println("Je transmet " + new String(paquet.getData(),0,paquet.getLength()));
					this.dso.send(paquet);
					this.entite.getidmMem().add(idm);
					
					//Je fais le traitement sur ce message
					switch(stSplit[0])
					{
						case "WHOS":
							String idmMemb = UUID.randomUUID().toString().substring(0, 8);
							String memb = "MEMB" + 
										" " + 
										idmMemb + 
										" " + 
										"127.0.0.1" + 
										" " + 
										this.entite.getLportRecvMess();
							paquet = new DatagramPacket(memb.getBytes(),0,memb.length(),ia);
							System.out.println("J'envoi " + memb);
							this.dso.send(paquet);
							this.entite.getidmMem().add(idmMemb);
					}
				}
				else
				{
					this.entite.getidmMem().remove(idm);
					System.out.println("J'ai déja recu je ne renvoi pas...");
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
