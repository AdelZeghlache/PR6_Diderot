package Ringo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class entite4
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Entite e4 = new Entite(7272,5558); 
		Ring r1 = new Ring("235.255.255.255",9998);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
		
		e4.insert(r1,InetAddress.getLocalHost().getHostAddress(), 5557);
		
		e4.envoiUDP();
		e4.recvUDP();
		
		e4.listenMulticast();
		
		e4.listenTCP();
	}
}
