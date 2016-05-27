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
			
			e3.insert(r1,ip, port);
			
			if(choix == 1)
			{
				e3.setVerbeux(true);
			}
			
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
