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
						idm = this.entite.generateUniqId();
						realMess = messSplit[0] + " " + idm;
						valid = true;
						this.entite.getidmMem().add(idm);//on ajoute l'id unique pour le mémoriser
						break;
					case "GBYE":
						if(this.entite.getAlDests().size() >= 2)
						{
							//Pas de GBYE quand on est en double anneau
							System.out.println("Impossible de quitter en étant une entité doubleur");
							valid = false;
							break;
						}
						else
						{
							idm = this.entite.generateUniqId();
							realMess = messSplit[0] + " " + idm + " " + this.entite.getFirstNonLoopbackAddress() + " " + this.entite.getLportRecvMess() + " " + this.entite.getAlDests().get(0).getIp() + " " + this.entite.getAlDests().get(0).getPort();
							valid = true;
							this.entite.getidmMem().add(idm);
							break;
						}
						
					case "TEST":
						Entite.nbTest = 0;
						for(int i = 0;i<this.entite.getAlDests().size();i++)
						{
							idm = this.entite.generateUniqId();
							realMess = messSplit[0] + " " + idm + " " + this.entite.getRing().get(i).getIpMulticast() + " " + this.entite.getRing().get(i).getPortMulticast();
							Ring r = new Ring(this.entite.getRing().get(i).getIpMulticast(),this.entite.getRing().get(i).getPortMulticast());
							Entite.alRing.add(r);
							this.entite.getidmMem().add(idm);
							
							//On envoie 2 messages différents, on l'envoi donc ici
							byte[] data = realMess.getBytes();
							InetSocketAddress ia = new InetSocketAddress(this.entite.getAlDests().get(i).getIp(),this.entite.getAlDests().get(i).getPort());
							DatagramPacket paquet = new DatagramPacket(data,data.length,ia);
							if(this.entite.isVerbeux())
								System.out.println("J'envoie " + new String(new String(paquet.getData(),0,paquet.getLength())));
							this.dso.send(paquet);
							Entite.nbTest++;
						}
						valid = false;
						isTest = true;
						break;
					case "APPL":
						if(this.entite.getAlApp().size() == 0)
							System.out.println("Vous n'avez aucune application d'installer");
						else
						{
							Scanner sc2 = new Scanner(System.in);
							System.out.println("0. Retour");
							for(int i = 0;i<this.entite.getAlApp().size();i++)
							{
								System.out.println(i+1 + ". " + this.entite.getAlApp().get(i).getId());
							}
							System.out.println("Faire un choix : ");
							int choix = 0;
							choix = sc2.nextInt();
							while(choix < 0 || choix > this.entite.getAlApp().size())
							{
								System.out.println("Choix invalide, recommencer : ");
								choix = sc2.nextInt();
							}
							this.entite.setApp(true);
							if(choix != 0)
								this.entite.getAlApp().get(choix-1).exec(this.entite.generateUniqId(),this.entite,this.dso);
							break;
						}
					default:
						valid = false;
						break;	
				}
				
				//Si le message est TEST, on attend qu'il(s) revienne(nt)
				if(isTest)
				{
					int i = 0;
					//Si la variable nbTest ne revient pas à 0 en 5 secondes, c'est qu'on a pas recu le ou les messages TEST envoyés, un des anneaux est donc cassé
					while(i<5 && Entite.nbTest != 0){
						System.out.println("...");
						Thread.sleep(1000);
						i++;
					}
					//On envoi donc DOWN a tous les anneaux de Entite.alRing, car le TEST ne répond a rien
					if(i >= 5 && Entite.alRing.size() >= 1){
						byte[] data = "DOWN".getBytes();
						InetSocketAddress ia;
						DatagramPacket paquet;
						for(int j = 0;j<Entite.alRing.size();j++)
						{
							ia = new InetSocketAddress(Entite.alRing.get(j).getIpMulticast(),Entite.alRing.get(j).getPortMulticast());
							paquet = new DatagramPacket(data,data.length,ia);
							if(this.entite.isVerbeux())
								System.out.println("J'envoie DOWN sur l'anneau " + Entite.alRing.get(j).toString());
							this.dso.send(paquet);
						}
					}
					//Entite.alRing.clear();
					isTest = false;
					
				}
				else //Sinon c'est un autre message, on peut vérifier si il est valide
				{
					if(valid)
					{
						byte[] data = realMess.getBytes();
						InetSocketAddress ia;
						DatagramPacket paquet;
						
						for(int i = 0;i<this.entite.getAlDests().size();i++)
						{
							ia = new InetSocketAddress(this.entite.getAlDests().get(i).getIp(),this.entite.getAlDests().get(i).getPort());
							paquet = new DatagramPacket(data,data.length,ia);
							if(this.entite.isVerbeux())
								System.out.println("J'envoie " + new String(paquet.getData(),0,paquet.getLength()));
							this.dso.send(paquet);
						}
					}
					else if(!this.entite.isApp())
					{
						System.err.println("Message inconnu ou invalide...");
					}
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

