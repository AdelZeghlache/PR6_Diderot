package Ringo;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class entite1
{
	public static void main(String[] args) throws IOException
	{
		//L'anneau sur le quel plusieurs entité pourront se connecter
		Ring r1 = new Ring("235.255.255.255",9998);
		
		Entite e1 = new Entite(r1,6969,5555,InetAddress.getLocalHost().getHostAddress(),6969);
		
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

