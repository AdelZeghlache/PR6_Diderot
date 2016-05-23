package Ringo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
				
				//Tester si le message est déstiné a l'anneau courant ou non
				if(this.entite.getidmMem().contains(idm) && stSplit[0].equals("TEST")){
					Entite.ttl = false;
				}
				
				//premier switch pour les messages a ne pas retransmettre, même si c'est la première fois qu'on les recoit
				//Surement a changer pour la duplication
				switch(stSplit[0])
				{
					case "EYBG":
						System.out.println("Sortie de l'anneau...");
						System.exit(0);
				}
				
				if(!this.entite.getidmMem().contains(idm))
				{
					//Je retransmet le message que j'ai recu
					InetSocketAddress ia = null;
					for(int i = 0;i<this.entite.getAlDests().size();i++)
					{
						ia = new InetSocketAddress(this.entite.getAlDests().get(i).getIp(),this.entite.getAlDests().get(i).getPort());
						paquet = new DatagramPacket(st.getBytes(),st.getBytes().length,ia);
						System.out.println("Je transmet " + new String(paquet.getData(),0,paquet.getLength()));
						this.dso.send(paquet);
						this.entite.getidmMem().add(idm);
					}
					
					//Je fais le traitement sur ce message
					switch(stSplit[0])
					{
						case "WHOS":
							String idmMemb = UUID.randomUUID().toString().substring(0, 8);
							String memb = "MEMB" + 
										" " + 
										idmMemb + 
										" " + 
										InetAddress.getLocalHost().getHostAddress() + 
										" " + 
										this.entite.getLportRecvMess();
							paquet = new DatagramPacket(memb.getBytes(),0,memb.length(),ia);
							System.out.println("J'envoi " + memb);
							this.dso.send(paquet);
							this.entite.getidmMem().add(idmMemb);
							break;
						case "GBYE":
							String ipSucc = stSplit[2];
							int portSucc = Integer.parseInt(stSplit[3]);
							
							String ipNextSucc = stSplit[4];
							int portNextSucc = Integer.parseInt(stSplit[5]);
							
							for(int i = 0;i<this.entite.getAlDests().size();i++)
							{
								if(!ipSucc.equals(this.entite.getAlDests().get(i).getIp()) || portSucc != this.entite.getAlDests().get(i).getPort())
								{
									//Le message GBYE ne m'est pas déstiné, je ne répond rien
									System.out.println("GBYE pas pour moi");
									continue;
								}
								else
								{
									//On forme le message EYBG
									String idmEybg = UUID.randomUUID().toString().substring(0, 8);
									String eybg = "EYBG " + idmEybg;
									
									//On envoie le message EYBG
									paquet = new DatagramPacket(eybg.getBytes(),0,eybg.length(),ia);
									System.out.println("J'envoi " + eybg);
									this.dso.send(paquet);
									this.entite.getidmMem().add(idmEybg);
									
									//On change l'ip et le port du succ
									this.entite.getAlDests().get(i).setIp(ipNextSucc);
									this.entite.getAlDests().get(i).setPort(portNextSucc);
								}
								
							}
							break;
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

