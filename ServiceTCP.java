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
			
			//On suppose que les messages sont automatiquement envoyé
			String welc = "WELC" + 
							" " + 
							this.entite.getIpNextMachine() + 
							" " + 
							this.entite.getLportNextMachine() + 
							" " + 
							this.entite.getRing().getIpMulticast() + 
							" " + 
							this.entite.getRing().getPortMulticast() + "\n";
			pw.write(welc);
			pw.flush();
			
			String newc = br.readLine();
			System.out.println(newc);
			String newcSplit[] = newc.split(" ");
			String newIp = newcSplit[1];
			int newPort = Integer.parseInt(newcSplit[2]);
			
			this.entite.setIpNextMachine(newIp);
			this.entite.setLportNextMachine(newPort);
			
			pw.write("ACKC\n");
			pw.flush();
			this.sock.close();			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}	
}