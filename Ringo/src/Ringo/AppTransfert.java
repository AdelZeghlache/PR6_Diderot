package Ringo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

public class AppTransfert extends Application
{
	private final String id = "TRANS###";
	String idTrans = null;
	int nummess = 0;
	int i = 1;

	public String getId() {
		return id;
	}

	public void exec(String idm, Entite entite, DatagramSocket dso)
	{
		try
		{
			Scanner sc = new Scanner(System.in);
			System.out.println("Nom du fichier : ");
			String filename = sc.nextLine();
			entite.setFichierDemande(filename);
			String ret = "APPL " + idm + " " + this.id + " REQ " + filename.length() + " " + filename;
			
			byte[] data = ret.getBytes();
			InetSocketAddress ia;
			DatagramPacket paquet;
			
			for(int i = 0;i<entite.getAlDests().size();i++)
			{
				ia = new InetSocketAddress(entite.getAlDests().get(i).getIp(),entite.getAlDests().get(i).getPort());
				paquet = new DatagramPacket(data,data.length,ia);
				System.out.println("J'envoie " + new String(paquet.getData(),0,paquet.getLength()));
				dso.send(paquet);
				entite.getidmMem().add(idm);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void traiter(String mess,Entite entite,DatagramSocket dso)
	{
		String split[] = mess.split(" ");
		
		if(split[3].equals("REQ"))
		{
			//Je vérifie si j''ai le fichier filename sur ma machine
			String filename = split[5];
			if(new File(filename).isFile())
			{
				entite.setSendRequest(false);
				try 
				{
					Path path = Paths.get("./" + filename);
					byte[] file = Files.readAllBytes(path);
					
					int nbmess = (int)Math.ceil((double)file.length/(512.0-49.0));
					
//					On met en little endian
//					ByteBuffer bb = ByteBuffer.allocate(8);
//				    bb.order(ByteOrder.LITTLE_ENDIAN);
//				    bb.putInt(nbmess);
//				    bb.flip();
					
				    String idm = entite.generateUniqId();
				    this.idTrans = entite.generateUniqId();
					String ret = "APPL " + idm + " " + this.id + " " + "ROK " + this.idTrans + " " + filename.length() + " " + filename + " " + nbmess;

					byte[] data = ret.getBytes();
					InetSocketAddress ia;
					DatagramPacket paquet;
					
					for(int j = 0;j<entite.getAlDests().size();j++)
					{
						ia = new InetSocketAddress(entite.getAlDests().get(j).getIp(),entite.getAlDests().get(j).getPort());
						paquet = new DatagramPacket(data,data.length,ia);
						if(entite.isVerbeux())
							System.out.println("J'envoie " + new String(paquet.getData(),0,paquet.getLength()));
						dso.send(paquet);
						entite.getidmMem().add(idm);
					}
					
					try{Thread.sleep(1000);}catch(InterruptedException e){e.printStackTrace();}
					
					//On envoi le fichier, a faire dans un thread
					
					int min = 0;
					int max = 0;
					if(file.length < 462)
						max = file.length;
					else
						max = 462;
					for(int j = 0;j<nbmess;j++)
					{
						String content = new String(Arrays.copyOfRange(file, min, max),0,Arrays.copyOfRange(file, min, max).length);
						String sen = "APPL " + entite.generateUniqId() + " " + this.id + " " + "SEN " + this.idTrans + " " + j + " " + content.length() + " " + content;
						min = max;
						if(j == nbmess-2)
							max = file.length;
						else
							max += 462;
						
						data = sen.getBytes();
						
						for(int k = 0;k<entite.getAlDests().size();k++)
						{
							ia = new InetSocketAddress(entite.getAlDests().get(k).getIp(),entite.getAlDests().get(k).getPort());
							paquet = new DatagramPacket(data,data.length,ia);
							if(entite.isVerbeux())
								System.out.println("J'envoie " + new String(paquet.getData(),0,paquet.getLength()));
							dso.send(paquet);

							try{Thread.sleep(100);}catch(InterruptedException e){e.printStackTrace();}
						}
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
//				System.out.println("J'ai pas le fichier");
				entite.setSendRequest(true);
			}
		}
		
		if(split[3].equals("ROK") && split[6].equals(entite.getFichierDemande()))
		{
			//Dans le cas ou j'ai demandé un fichier, et une entité l'a, elle va me l'envoyer
			this.idTrans = split[4];
			this.nummess = Integer.parseInt(split[7]);
		}
		
		if(split[3].equals("SEN") && split[4].equals(idTrans))
		{
			//Je recoit un fichier morceau par morceau
			entite.setSendRequest(false);
			try 
			{
				BufferedWriter out = new BufferedWriter(new FileWriter("bis" + entite.getFichierDemande(),true));
				String toWrite = "";
				for(int j = 7;j<split.length;j++)
				{
					toWrite += split[j] + " ";
				}
				
				out.write(toWrite);
				out.close();
				
				if(i == nummess)
				{	
					entite.setFichierDemande(null);
					this.i = 1;
					this.nummess = 0;
					this.idTrans = null;
				}
				i++;
			} 
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		entite.setApp(false);
	}
}
