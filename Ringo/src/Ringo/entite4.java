package Ringo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class entite4
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		try
		{
			Entite e4 = new Entite(7272,5558); 
			Ring r1 = new Ring("235.255.255.255",9998);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
			
			Scanner sc = new Scanner(System.in);
			System.out.println("l'IP à laquelle se connecter pour s'insérer : ");
			String ip = sc.nextLine();
			
			e4.insert(r1,ip, 5557);
			
			if(e4.isVerbeux())
				System.out.println(e4.toString());
			
			e4.envoiUDP();
			e4.recvUDP();
			
			e4.listenMulticast();
			
			e4.listenTCP();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
