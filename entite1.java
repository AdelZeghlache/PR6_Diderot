import java.io.IOException;

public class entite1
{
	public static void main(String[] args) throws IOException
	{
		//L'anneau sur le quel plusieurs entité pourront se connecter
		Ring r1 = new Ring("235.255.255.255",9998);
		
		Entite e1 = new Entite(r1,6969,5555,"127.0.0.1",6969);
		
		e1.envoiUDP();
		e1.recvUDP();
		e1.listenTCP();
	}
}
