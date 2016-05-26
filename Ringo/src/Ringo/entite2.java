package Ringo;

import java.io.IOException;
import java.net.InetAddress;
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
			System.out.println("l'IP à laquelle se connecter pour s'insérer : ");
			String ip = sc.nextLine();
			
			e2.insert(r1,ip, 5555);
			
			AppDiff ad = new AppDiff();
			AppTransfert at = new AppTransfert();
			
			e2.getAlApp().add(ad);
			e2.getAlApp().add(at);
			
			e2.envoiUDP();
			e2.recvUDP();
			
			e2.listenMulticast();
			
			e2.listenTCP();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

