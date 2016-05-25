package Ringo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class entite5 
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		try
		{
			Entite e5 = new Entite(7373,5559);
			
			Ring r2 = new Ring("236.255.255.255",9999);//On crée 2 fois le même objet avec les mêmes caractéristique, a changer
			
			Scanner sc = new Scanner(System.in);
			System.out.println("l'IP � laquelle se connecter pour s'ins�rer : ");
			String ip = sc.nextLine();
			
			e5.dupl(r2,ip,5555);
			e5.envoiUDP();
			e5.recvUDP();
			
			e5.listenMulticast();
				
			e5.listenTCP();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
