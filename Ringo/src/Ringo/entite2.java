package Ringo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class entite2 
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Entite e2 = new Entite(7070,5556); 
		Ring r1 = new Ring("235.255.255.255",9998);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
		
		e2.insert(r1,InetAddress.getLocalHost().getHostAddress(), 5555);
		
		e2.envoiUDP();
		e2.recvUDP();
		
		e2.listenMulticast();
		
		e2.listenTCP();
	}
}

