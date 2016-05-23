package Ringo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceTCP implements Runnable 
{
	private Socket sock;
	private Entite entite;
	
	public ServiceTCP(Socket sock, Entite entite)
	{
		this.sock = sock;
		this.entite = entite;
	}
	
	public void run() 
	{
		try
		{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(this.sock.getOutputStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
			
			if(this.entite.getRing().size() == 2){
				pw.write("NOTC\n");
				pw.flush();
				this.sock.close();
			}
			else
			{
				//On suppose que les messages sont automatiquement envoyé
				String welc = "WELC" + 
								" " + 
								this.entite.getAlDests().get(0).getIp() + //Le premier élément de l'arraylist est le destinaire de l'anneau courant
								" " + 
								this.entite.getAlDests().get(0).getPort() + 
								" " + 
								this.entite.getRing().getFirst().getIpMulticast() + 
								" " + 
								this.entite.getRing().getFirst().getPortMulticast() + "\n";
				pw.write(welc);
				pw.flush();
				
				String newc = br.readLine();
				System.out.println(newc);
				String newcSplit[] = newc.split(" ");
				
				switch(newcSplit[0])
				{
					case "NEWC":
						String newIp = newcSplit[1];
						int newPort = Integer.parseInt(newcSplit[2]);
						
						this.entite.getAlDests().get(0).setIp(newIp);
						this.entite.getAlDests().get(0).setPort(newPort);
						
						pw.write("ACKC\n");
						pw.flush();
						
						this.sock.close();
						break;
					case "DUPL":
						String ipDupl = newcSplit[1];
						int portDupl = Integer.parseInt(newcSplit[2]);
						
						Dests d = new Dests(ipDupl,portDupl);
						this.entite.getAlDests().add(d);
						
						String duplIpDiff = newcSplit[3];
						int duplPortDiff = Integer.parseInt(newcSplit[4]);
						
						Ring newRing = new Ring(duplIpDiff,duplPortDiff);
						this.entite.getRing().add(newRing);
						
						pw.write("ACKD " + this.entite.getLportRecvMess() + "\n");
						pw.flush();
						
						this.sock.close();
						System.out.println("APRES DUPL");
						System.out.println(this.entite.toString());
						break;
				}
			}		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}	
}
