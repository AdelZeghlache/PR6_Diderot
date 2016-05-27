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
			
			Scanner sc = new Scanner(System.in);
			
			System.out.println("Activer le mode verbeux ?\n0. Non\n1. Oui\n");
			int choix = sc.nextInt();
			while(choix != 0 && choix != 1)
			{
				
				System.out.println("Choix invalide, taper 0 pour Non ou 1 pour Oui : ");
				choix = sc.nextInt();
			}
			
			sc = new Scanner(System.in);
			System.out.println("l'IP à laquelle se connecter pour s'insérer : ");
			String ip = sc.nextLine();
			
			sc = new Scanner(System.in);
			System.out.println("Le port ? : ");
			int port = sc.nextInt();
			
			e4.insert(ip, port);
			
			if(choix == 1)
				e4.setVerbeux(true);
			
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
