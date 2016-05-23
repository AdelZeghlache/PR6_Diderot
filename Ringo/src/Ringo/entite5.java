package Ringo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class entite5 
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Entite e5 = new Entite(7373,5559);
		
		Ring r2 = new Ring("236.255.255.255",9999);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
		
		e5.dupl(r2,InetAddress.getLocalHost().getHostAddress(),5555);
		System.out.println(e5.toString());
		e5.envoiUDP();
		e5.recvUDP();
		
		//e5.listenMulticast();
			
		e5.listenTCP();
	}
}
