package Ringo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class entite2 
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		try
		{
			Entite e2 = new Entite(7070,5556); 
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
			
			e2.insert(r1,ip, port);
			
			if(choix == 1)
			{
				e2.setVerbeux(true);
			}
			
			if(e2.isVerbeux())
				System.out.println(e2.toString());
			
			AppDiff ad = new AppDiff();
			AppTransfert at = new AppTransfert();
			
			e2.getAlApp().add(ad);
			e2.getAlApp().add(at);
			
			e2.envoiUDP();
			e2.recvUDP();
			
			e2.listenMulticast();
			
			e2.listenTCP();
		}
		catch(UnknownHostException e)
		{
			System.out.println("Adresse IP inconnu ou invalide");
			System.exit(-1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

