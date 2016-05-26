package Ringo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.UUID;

/**
 * 
 * @author ATTYE Camille, ZEGHLACHE Adel, ROUILLARD Charles
 *
 */
public class Entite
{
	public static int nbTest = 0;//permet de savoir combien de test sont envoyé, pour savoir combien sont revenu
	public static ArrayList<Ring> alRing = new ArrayList<Ring>(); //permet de savoir dans quel anneau le test ne s'est pas renvoyé
	
	//private MulticastSocket mso; //Permet de pouvoir joindre plusieurs groupes de multi diffusion pour les entités doubleurs
	
	private String fichierDemande; //permet de garder en mémoire le fichier demandé dans l'application de transfert
	private ArrayList<String> idmMem; //Permet de garder en mémoire les id de message pour ne pas les retransmettre
	private ArrayList<Dests> alDests; //contient la liste des detinataire a qui envoyer un message, est de taille 2 pour les entités doubleurs, 1 sinon
	private ArrayList<Application> alApp; //Contient la liste des application installé pour chaque entité
	private LinkedList<Ring> listRing; //Contient la liste des anneaux sur lequel on est, est de taille 2 pour les entités doubleurs
	private String id; //L'id d'une entité
	private int lportRecvMess; //Son pour d'écoute UDP
	private int portTcp; //Son port découte TCP
	private boolean sendRequest = true; //Permet de ne pas retransmettre le message REQ quand on a le fichier demandé par l'application TRANS
	private boolean app = false; //Permet de savoir si le message est envoyé est APPL ou non
	private boolean verbeux = false;

	/**
	 * Un constructeur avec 2 paramètre pour les entités qui vont s'insérer sur un autre entité
	 * @param lportRecvMess
	 * @param portTcp
	 */
	public Entite(int lportRecvMess,int portTcp)
	{
		this.fichierDemande = null;
		this.idmMem = new ArrayList<String>();
		this.listRing = new LinkedList<Ring>();
		this.alApp = new ArrayList<Application>();
		this.alDests = new ArrayList<Dests>();

		this.id = UUID.randomUUID().toString().substring(0, 8);
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
	}

	/**
	 * Un constructeur pour créer une entité qui est directement sur un anneau 
	 * @param ring
	 * @param lportRecvMess
	 * @param portTcp
	 * @param ipNextMachine
	 * @param lportNextMachine
	 */
	public Entite(Ring ring,int lportRecvMess,int portTcp,String ipNextMachine,int lportNextMachine)
	{
		this.fichierDemande = null;
		this.idmMem = new ArrayList<String>();
		this.alDests = new ArrayList<Dests>();
		this.alApp = new ArrayList<Application>();
		this.listRing = new LinkedList<Ring>();
		this.listRing.add(ring);
		
		this.id = UUID.randomUUID().toString().substring(0, 8);
		this.lportRecvMess = lportRecvMess;
		this.portTcp = portTcp;
		
		Dests d = new Dests(ipNextMachine,lportNextMachine);
		this.alDests.add(d);
	}
	
	//Getters and setters
	
	/**
	 * 
	 * @return fichierDemande
	 */
	public String getFichierDemande() {
		return fichierDemande;
	}

	/**
	 * 
	 * @param fichierDemande
	 */
	public void setFichierDemande(String fichierDemande) {
		this.fichierDemande = fichierDemande;
	}

	/**
	 * 
	 * @return idmMem
	 */
	public ArrayList<String> getidmMem(){
		return this.idmMem;
	}
	
	/**
	 * 
	 * @return alDests
	 */
	public ArrayList<Dests> getAlDests() {
		return alDests;
	}

	/**
	 * 
	 * @return alApp
	 */
	public ArrayList<Application> getAlApp() {
		return alApp;
	}

	/**
	 * 
	 * @return listRing
	 */
	public LinkedList<Ring> getRing() {
		return listRing;
	}
	
	/**
	 * 
	 * @return id
	 */
	public String getId(){
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return lportRecvMess
	 */
	public int getLportRecvMess() {
		return lportRecvMess;
	}

	/**
	 * 
	 * @param lportRecvMess
	 */
	public void setLportRecvMess(int lportRecvMess) {
		this.lportRecvMess = lportRecvMess;
	}

	/**
	 * 
	 * @return portTcp
	 */
	public int getPortTcp() {
		return portTcp;
	}

	/**
	 * 
	 * @param portTcp
	 */
	public void setPortTcp(int portTcp) {
		this.portTcp = portTcp;
	}
	
	/**
	 * 
	 * @return mso
	 */
//	public MulticastSocket getMso()
//	{
//		return this.mso;
//	}
	
	/**
	 * 
	 * @return sendRequest
	 */
	public boolean isSendRequest() {
		return sendRequest;
	}

	/**
	 * 
	 * @param sendFile
	 */
	public void setSendRequest(boolean sendFile) {
		this.sendRequest = sendFile;
	}
	
	/**
	 * 
	 * @return app
	 */
	public boolean isApp() {
		return app;
	}

	/**
	 * 
	 * @param app
	 */
	public void setApp(boolean app) {
		this.app = app;
	}
	
	/**
	 * 
	 * @return verbeux
	 */
	public boolean isVerbeux() {
		return verbeux;
	}

	/**
	 * 
	 * @param verbeux
	 */
	public void setVerbeux(boolean verbeux) {
		this.verbeux = verbeux;
	}
	
	public String convertIpIn15Bytes(String ip)
	{
		return null;
	}
	
	/**
	 * Permet de générer un id unique
	 * @return
	 */
	public String generateUniqId()
	{
		return UUID.randomUUID().toString().substring(0, 8);
	}
	
	/**
	 * Permet de récuperer la première addresse ip qui n'est pas une addresse de loopback
	 * @return
	 * @throws SocketException
	 */
	public String getFirstNonLoopbackAddress() throws SocketException
	{
		Enumeration en = NetworkInterface.getNetworkInterfaces();
		while(en.hasMoreElements()){
			NetworkInterface i = (NetworkInterface)en.nextElement();
			for(Enumeration en2 = i.getInetAddresses();en2.hasMoreElements();)
			{
				InetAddress addr = (InetAddress) en2.nextElement();
				if(!addr.isLoopbackAddress()){
					if(addr instanceof Inet4Address){
						return addr.getHostAddress();
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Permet a une entité de s'insérer sur un anneau en se connectant en TCP a la machine ipPrecMachine et sur le port portPrecMachine
	 * @param ring
	 * @param ipPrecMachine
	 * @param portPrecMachine
	 * @throws UnknownHostException
	 * @throws IOException
	 */
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
		
		mess = "NEWC" + " " + this.getFirstNonLoopbackAddress() + " " + this.getLportRecvMess() + "\n";
		pw.write(mess);
		pw.flush();
		
		mess = br.readLine();
		System.out.println(mess);
		
		s.close();
	}
	
	
	/**
	 * Permet de dumpliquer un anneau et de faire de la machine ipPrecMachine sur le port portPrecMachine (TCP) une entité doubleur
	 * @param ring
	 * @param ipPrecMachine
	 * @param portPrecMachine
	 * @throws IOException
	 */
	public void dupl(Ring ring, String ipPrecMachine, int portPrecMachine) throws IOException
	{
		try
		{
			//tester si c'est déja insérer...
			Socket s = new Socket(ipPrecMachine, portPrecMachine);
			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String mess = br.readLine();
			System.out.println(mess);
			
			//Pareil, le message est envoyé automatiquement donc de la bonne forme
			String messSplit[] = mess.split(" ");
			
			mess = "DUPL" + " " + this.getFirstNonLoopbackAddress() + " " + this.getLportRecvMess() + " " + ring.getIpMulticast() + " " + ring.getPortMulticast() + "\n";
			pw.write(mess);
			pw.flush();
			
			mess = br.readLine();
			
			this.getRing().add(ring);
			
			Dests d = new Dests(ipPrecMachine,Integer.parseInt(mess.split(" ")[1]));
			this.getAlDests().add(d);
			
			System.out.println(mess);
			
			s.close();
		}
		catch(UnknownHostException e)
		{
			System.out.println("Impossible de se connecter à cette addresse");
			System.exit(-1);
		}
		catch(ConnectException e)
		{
			System.out.println("Addresse inacessible. Temps de connexion écoulé");
			System.exit(-1);
		}
	}
	

	/**
	 * Permet de lancer le service d'envoi de paquet UDP dans un nouveau Thread
	 * @throws SocketException
	 */
	public void envoiUDP() throws SocketException
	{
		DatagramSocket dso;
		dso = new DatagramSocket();
		ServiceEnvoiUDP env = new ServiceEnvoiUDP(dso,this);
		Thread t = new Thread(env);
		t.start();
	}
	
	/**
	 * Permet re lancer le service re deception de paquet UDP dans un nouveau Thread
	 * @throws SocketException
	 */
	public void recvUDP() throws SocketException
	{
		DatagramSocket dso;
		dso = new DatagramSocket(this.getLportRecvMess());
		ServiceRecvUDP recv = new ServiceRecvUDP(dso,this);
		Thread t2 = new Thread(recv);
		t2.start();
	}
	
	/**
	 * Permet de lancer le service d'écoute de connecion TCP dans un nouveau Thread
	 * @throws IOException
	 */
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
	
	/**
	 * Perlet d'écouter et d'envoyer des messages de lulti diffusion dans un nouveau Thread
	 * @throws IOException
	 */
	public void listenMulticast() throws IOException
	{
		MulticastSocket mso = new MulticastSocket(this.listRing.getFirst().getPortMulticast());
		ServiceMulticast sm = new ServiceMulticast(mso,this,InetAddress.getByName(this.getRing().getFirst().getIpMulticast()));
		Thread t4 = new Thread(sm);
		t4.start(); 
	}
	
	public String toString()
	{
		String ret = "";
		try {
			ret += "Entité " + this.id + "\nAddresse IP : " + this.getFirstNonLoopbackAddress() + "\nMon port d'écoute UDP est " + this.lportRecvMess + "\n";
		} catch (SocketException e){
			e.printStackTrace();
		}
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

