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
				this.entite.setSendRequest(true); //par défaut on retransmet tous les messages
				
				byte[] data = new byte[512];
				DatagramPacket paquet = new DatagramPacket(data,data.length);
				this.dso.receive(paquet);
				String st = new String(paquet.getData(),0,paquet.getLength());
				if(this.entite.isVerbeux())
					System.out.println("J'ai recu " + st);
				else
					System.out.println(st);
				String[] stSplit = st.split(" ");
				String idm = stSplit[1];
				
				//Si on recoit un message test qu'on a deja vu, c'est que l'anneau n'est pas cassé
				if(this.entite.getidmMem().contains(idm) && stSplit[0].equals("TEST")){
					//On supprime le test recu de l'arraylist d'anneau car il n'est pas cassé, on créer un nouvel objet Ring correspondant aux infos contenu dans le message TEST
					//Puis on le supprime de l'arraylist grace a la méthode remove(Ring ring)
					
					String ipMulticastRing = stSplit[2];
					int portMulticastRing = Integer.parseInt(stSplit[3]);
					
					Ring r = new Ring(ipMulticastRing,portMulticastRing);
					Entite.alRing.remove(r);
					Entite.nbTest--;
				}
				
				//premier switch pour les messages a ne pas retransmettre, même si c'est la première fois qu'on les recoit
				//Surement a changer pour la duplication
				switch(stSplit[0])
				{
					case "EYBG":
						System.out.println("Sortie de l'anneau...");
						System.exit(0);
					case "APPL":
						String idApp = stSplit[2];
						for(int i = 0;i<this.entite.getAlApp().size();i++)
						{
							if(this.entite.getAlApp().get(i).getId().equals(idApp))
							{
								this.entite.getAlApp().get(i).traiter(st,this.entite,this.dso);
							}
						}
						break;
				}
				if(this.entite.isSendRequest())
				{
					if(!this.entite.getidmMem().contains(idm))
					{
						//Je retransmet le message que j'ai recu
						InetSocketAddress ia = null;
						for(int i = 0;i<this.entite.getAlDests().size();i++)
						{
							ia = new InetSocketAddress(this.entite.getAlDests().get(i).getIp(),this.entite.getAlDests().get(i).getPort());
							paquet = new DatagramPacket(st.getBytes(),st.getBytes().length,ia);
							if(this.entite.isVerbeux())
								System.out.println("Je transmet " + new String(paquet.getData(),0,paquet.getLength()));
							this.dso.send(paquet);
							this.entite.getidmMem().add(idm);
						}
						
						//Je fais le traitement sur ce message
						switch(stSplit[0])
						{
							case "WHOS":
								String idmMemb = this.entite.generateUniqId();
								String memb = "MEMB" + 
											" " + 
											idmMemb + 
											" " + 
											this.entite.getId() + 
											" " +
											this.entite.convertIpIn15Bytes(this.entite.getFirstNonLoopbackAddress()) + 
											" " + 
											this.entite.getLportRecvMess();
								for(int i = 0;i<this.entite.getAlDests().size();i++)
								{
									ia = new InetSocketAddress(this.entite.getAlDests().get(i).getIp(),this.entite.getAlDests().get(i).getPort());
									paquet = new DatagramPacket(memb.getBytes(),0,memb.length(),ia);
									if(this.entite.isVerbeux())
										System.out.println("J'envoi " + memb);
									this.dso.send(paquet);
									this.entite.getidmMem().add(idmMemb);
								}
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
										continue;
									}
									else
									{
										//On forme le message EYBG
										String idmEybg = this.entite.generateUniqId();
										String eybg = "EYBG " + idmEybg;
										
										//On envoie le message EYBG
										ia = new InetSocketAddress(ipSucc,portSucc); //On envoi le EYGB uniquement a celui qui a demandé GBYE
										paquet = new DatagramPacket(eybg.getBytes(),0,eybg.length(),ia);
										if(this.entite.isVerbeux())
											System.out.println("J'envoi " + eybg);
										this.dso.send(paquet);
										this.entite.getidmMem().add(idmEybg);
										
										//On change l'ip et le port du succ
										this.entite.getAlDests().get(i).setIp(ipNextSucc);
										this.entite.getAlDests().get(i).setPort(portNextSucc);
										
										if(this.entite.getRing().size() == 2 && this.entite.getAlDests().get(i).getIp().equals(InetAddress.getLocalHost().getHostAddress()) && this.entite.getAlDests().get(i).getPort() == this.entite.getLportRecvMess())
										{
											//On est dans le cas ou on s'envoi les messages a nous même alors qu'on est une entité doubleur, on supprime donc l'anneau double ou on est seul dessus
											this.entite.getRing().remove(i);
											this.entite.getAlDests().remove(i);
										}
									}
								}
								break;
								
						}
					}
					else
					{
						//this.entite.getidmMem().remove(idm);
						if(this.entite.isVerbeux())
							System.out.println("J'ai déja recu je ne transmet pas...");
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

