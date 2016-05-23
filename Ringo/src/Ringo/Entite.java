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
	public static boolean ttl = false;
	
	private ArrayList<String> idmMem;
	private ArrayList<Dests> alDests;
	private LinkedList<Ring> listRing;
	private String id;
	private int lportRecvMess;
	private int portTcp;
//	private String ipNextMachine;
//	private int lportNextMachine;
	
	public Entite(int lportRecvMess,int portTcp)
	{
		this.idmMem = new ArrayList<String>();
		this.listRing = new LinkedList<Ring>();
		this.alDests = new ArrayList<Dests>();

		this.id = UUID.randomUUID().toString().substring(0, 8);
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
		//this.ipNextMachine = null;
		//this.lportNextMachine = 0;
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
		
//		this.ipNextMachine = ipNextMachine;
//		this.lportNextMachine = lportNextMachine;
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
	
	

//	public String getIpNextMachine() {
//		return ipNextMachine;
//	}
//
//	public void setIpNextMachine(String ipNextMachine) {
//		this.ipNextMachine = ipNextMachine;
//	}

//	public int getLportNextMachine() {
//		return lportNextMachine;
//	}
//
//	public void setLportNextMachine(int lportNextMachine) {
//		this.lportNextMachine = lportNextMachine;
//	}
	
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
//		this.setIpNextMachine(ip);
//		this.setLportNextMachine(port);
		
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
		String ip = messSplit[1];
		
//		this.setIpNextMachine(ip);
		
		mess = "DUPL" + " " + InetAddress.getLocalHost().getHostAddress() + " " + this.getLportRecvMess() + " " + ring.getIpMulticast() + " " + ring.getPortMulticast() + "\n";
		pw.write(mess);
		pw.flush();
		
		mess = br.readLine();
//		this.setLportNextMachine(Integer.parseInt(mess.split(" ")[1]));
		
		Dests d = new Dests(ip,Integer.parseInt(mess.split(" ")[1]));
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
		MulticastSocket mso = new MulticastSocket(this.listRing.getFirst().getPortMulticast());
		ServiceMulticast sm = new ServiceMulticast(mso,this);
		Thread t4 = new Thread(sm);
		t4.start(); 
	}
	
	public String toString()
	{
		String ret = "";
		ret += "Entité " + this.id + "\nMon port d'écoute UDP est " + this.lportRecvMess + "\n";
		if(this.getAlDests().size() == 1)
			ret += "J'envoi mes messages a l'adresse " + this.getAlDests().get(0).getIp() + " et sur le port " + this.getAlDests().get(0).getPort();
		else
		{
			ret += "J'envoi mes messages aux destinaires suivants : \n";
			for(int i = 0;i<this.getAlDests().size();i++)
			{
				ret += this.getAlDests().get(i).getIp() + " " + this.getAlDests().get(i).getPort() + "\n";
			}
		}
		return ret;
	}
}

