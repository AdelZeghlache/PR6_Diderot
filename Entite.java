import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Entite
{
	public static int id_entite = 0;
	
	private ArrayList<String> idmMem;
	
	private Ring ring;
	private int id;
	private int lportRecvMess;
	private int portTcp;
	private String ipNextMachine;
	private int lportNextMachine;
	
	public Entite(int lportRecvMess,int portTcp)
	{
		this.idmMem = new ArrayList<String>();
		
		this.ring = null;
		this.id = Entite.id_entite++;
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
		this.ipNextMachine = null;
		this.lportNextMachine = 0;
	}
	
	public Entite(Ring ring,int lportRecvMess,int portTcp,String ipNextMachine,int lportNextMachine)
	{
		this.idmMem = new ArrayList<String>();
		
		this.ring = ring;
		
		this.id = Entite.id_entite++;
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
		this.ipNextMachine = ipNextMachine;
		this.lportNextMachine = lportNextMachine;
	}
	
	public ArrayList<String> getidmMem(){
		return this.idmMem;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLportRecvMess() {
		return lportRecvMess;
	}

	public void setLportRecvMess(int lportRecvMess) {
		this.lportRecvMess = lportRecvMess;
	}

	public int getPortTcp() {
		return portTcp;
	}

	public void setPortTcp(int portTcp) {
		this.portTcp = portTcp;
	}

	public String getIpNextMachine() {
		return ipNextMachine;
	}

	public void setIpNextMachine(String ipNextMachine) {
		this.ipNextMachine = ipNextMachine;
	}

	public int getLportNextMachine() {
		return lportNextMachine;
	}

	public void setLportNextMachine(int lportNextMachine) {
		this.lportNextMachine = lportNextMachine;
	}
	
	public Ring getRing() {
		return ring;
	}

	public void setRing(Ring ring) {
		this.ring = ring;
	}
	
	public void insert(Ring ring, String ipPrecMachine, int portPrecMachine) throws UnknownHostException, IOException
	{
		//tester si c'est déja insérer...
		Socket s = new Socket(ipPrecMachine, portPrecMachine);
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		String mess = br.readLine();
		System.out.println(mess);
		
		//Pareil, le message est envoyé automatiquement donc de la bonne forme
		String messSplit[] = mess.split(" ");
		String ip = messSplit[1];
		int port = Integer.parseInt(messSplit[2]);
		
		this.setRing(ring);
		this.setIpNextMachine(ip);
		this.setLportNextMachine(port);
		
		mess = "NEWC" + " " + "127.0.0.1" + " " + this.getLportRecvMess() + "\n";
		pw.write(mess);
		pw.flush();
		
		mess = br.readLine();
		System.out.println(mess);
		
		s.close();
	}
	
	public void envoiUDP() throws SocketException
	{
		DatagramSocket dso;
		dso = new DatagramSocket();
		ServiceEnvoiUDP env = new ServiceEnvoiUDP(dso,this);
		Thread t = new Thread(env);
		t.start();
	}
	
	public void recvUDP() throws SocketException
	{
		DatagramSocket dso;
		dso = new DatagramSocket(this.getLportRecvMess());
		ServiceRecvUDP recv = new ServiceRecvUDP(dso,this);
		Thread t2 = new Thread(recv);
		t2.start();
	}
	
	public void listenTCP() throws IOException
	{
		ServerSocket server = new ServerSocket(this.getPortTcp());
		while(true)
		{
			Socket sock = server.accept();
			ServiceTCP servTCP = new ServiceTCP(sock,this);
			Thread t3 = new Thread(servTCP);
			t3.start();
		}
	}
}
