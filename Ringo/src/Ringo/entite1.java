package Ringo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class entite1
{
	public static void main(String[] args) throws IOException
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Activer le mode verbeux ?\n0. Non\n1. Oui\n");
		int choix = sc.nextInt();
		while(choix != 0 && choix != 1)
		{
			
			System.out.println("Choix invalide, taper 0 pour Non ou 1 pour Oui : ");
			choix = sc.nextInt();
		}
		
		//L'anneau sur le quel plusieurs entité pourront se connecter
		Ring r1 = new Ring("235.255.255.255",9998);
		
		Entite e1 = new Entite(r1,6969,5555,InetAddress.getLocalHost().getHostAddress(),6969);
		
		if(choix == 1)
			e1.setVerbeux(true);
		
		if(e1.isVerbeux())
			System.out.println(e1.toString());
		
		//On créer et ajoute les applications
		AppDiff ad = new AppDiff();
		AppTransfert at = new AppTransfert();
		
		e1.getAlApp().add(ad);
		e1.getAlApp().add(at);
		
		e1.envoiUDP();
		e1.recvUDP();
		
		e1.listenMulticast();
		
		e1.listenTCP();
	}
	
}

