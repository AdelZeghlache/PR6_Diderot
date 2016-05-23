package Ringo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class entite3
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Entite e3 = new Entite(7171,5557); 
		Ring r1 = new Ring("235.255.255.255",9998);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
		
		e3.insert(r1,InetAddress.getLocalHost().getHostAddress(), 5556);
		
		e3.envoiUDP();
		e3.recvUDP();
		
		e3.listenMulticast();
		
		e3.listenTCP();
	}
}
