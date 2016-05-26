package Ringo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class entite3
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		try
		{
			Entite e3 = new Entite(7171,5557); 
			Ring r1 = new Ring("235.255.255.255",9998);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
			
			Scanner sc = new Scanner(System.in);
			System.out.println("l'IP à laquelle se connecter pour s'insérer : ");
			String ip = sc.nextLine();
			
			e3.insert(r1,ip, 5556);
			
			if(e3.isVerbeux())
				System.out.println(e3.toString());
			
			e3.envoiUDP();
			e3.recvUDP();
			
			e3.listenMulticast();
			
			e3.listenTCP();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
