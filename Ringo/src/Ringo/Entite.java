package Ringo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.UUID;

public class Entite
{
	public static int nbTest = 0;//permet de savoir combien de test sont envoyé, pour savoir combien sont revenu
	public static ArrayList<Ring> alRing = new ArrayList<Ring>(); //permet de savoir dans quel anneau le test ne s'est pas renvoyé
	
	private MulticastSocket mso;
	
	private ArrayList<String> idmMem;
	private ArrayList<Dests> alDests;
	private LinkedList<Ring> listRing;
	private String id;
	private int lportRecvMess;
	private int portTcp;
	
	public Entite(int lportRecvMess,int portTcp)
	{
		this.idmMem = new ArrayList<String>();
		this.listRing = new LinkedList<Ring>();
		this.alDests = new ArrayList<Dests>();

		this.id = UUID.randomUUID().toString().substring(0, 8);
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
	}
	
	public Entite(Ring ring,int lportRecvMess,int portTcp,String ipNextMachine,int lportNextMachine)
	{
		this.idmMem = new ArrayList<String>();
		this.alDests = new ArrayList<Dests>();
		this.listRing = new LinkedList<Ring>();
		this.listRing.add(ring);
		
		this.id = UUID.randomUUID().toString().substring(0, 8);
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
		
		Dests d = new Dests(ipNextMachine,lportNextMachine);
		this.alDests.add(d);
	}
	
	public ArrayList<String> getidmMem(){
		return this.idmMem;
	}
	
	public ArrayList<Dests> getAlDests() {
		return alDests;
	}


	public LinkedList<Ring> getRing() {
		return listRing;
	}
	
	public String getId(){
		return id;
	}

	public void setId(String id) {
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
	
	public MulticastSocket getMso()
	{
		return this.mso;
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
		
		this.listRing.add(ring);
		
		Dests d = new Dests(ip,port);
		this.getAlDests().add(d);
		
		mess = "NEWC" + " " + InetAddress.getLocalHost().getHostAddress() + " " + this.getLportRecvMess() + "\n";
		pw.write(mess);
		pw.flush();
		
		mess = br.readLine();
		System.out.println(mess);
		
		s.close();
	}
	
	public void dupl(Ring ring, String ipPrecMachine, int portPrecMachine) throws UnknownHostException, IOException
	{
		//tester si c'est déja insérer...
		Socket s = new Socket(ipPrecMachine, portPrecMachine);
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		String mess = br.readLine();
		System.out.println(mess);
		
		//Pareil, le message est envoyé automatiquement donc de la bonne forme
		String messSplit[] = mess.split(" ");
		
		mess = "DUPL" + " " + InetAddress.getLocalHost().getHostAddress() + " " + this.getLportRecvMess() + " " + ring.getIpMulticast() + " " + ring.getPortMulticast() + "\n";
		pw.write(mess);
		pw.flush();
		
		mess = br.readLine();
		
		this.getRing().add(ring);
		
		Dests d = new Dests(ipPrecMachine,Integer.parseInt(mess.split(" ")[1]));
		this.getAlDests().add(d);
		
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
	
	public void listenMulticast() throws IOException
	{
		this.mso = new MulticastSocket(this.listRing.getFirst().getPortMulticast());
		ServiceMulticast sm = new ServiceMulticast(mso,this,InetAddress.getByName(this.getRing().getFirst().getIpMulticast()));
		Thread t4 = new Thread(sm);
		t4.start(); 
	}
	
	public String toString()
	{
		String ret = "";
		ret += "Entité " + this.id + "\nMon port d'écoute UDP est " + this.lportRecvMess + "\n";
		if(this.getAlDests().size() == 1){
			ret += "J'envoi mes messages a l'adresse " + this.getAlDests().get(0).getIp() + " et sur le port " + this.getAlDests().get(0).getPort() + "\n";
			ret += "Je suis sur un anneau dont l'IP de multi difussion est " + this.getRing().getFirst().getIpMulticast() + " et donc le port de multi difussion est " + this.getRing().getFirst().getPortMulticast();
		}
		else
		{
			ret += "J'envoi mes messages aux destinaires suivants : \n";
			for(int i = 0;i<this.getAlDests().size();i++)
			{
				ret += this.getAlDests().get(i).getIp() + " " + this.getAlDests().get(i).getPort() + "\n";
			}
			ret += "Et je suis sur les anneaux dont l'addresse et le port de multi diffusion sont les suivants : \n";
			for(int i = 0;i<this.getRing().size();i++)
			{
				ret += this.getRing().get(i).getIpMulticast() + " : " + this.getRing().get(i).getPortMulticast() + "\n";
			}
		}
		return ret;
	}
}

