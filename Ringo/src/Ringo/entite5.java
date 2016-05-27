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
			
			Ring r2 = new Ring("236.255.255.255",9999);//Le nouvel anneua pour la duplication
			
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
			
			e5.dupl(r2,ip,port);
			
			if(choix == 1)
				e5.setVerbeux(true);
			
			if(e5.isVerbeux())
				System.out.println(e5.toString());
			
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
