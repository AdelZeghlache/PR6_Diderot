import java.io.IOException;
import java.net.UnknownHostException;

public class entite2 
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		Entite e2 = new Entite(7070,5556); 
		Ring r1 = new Ring("235.255.255.255",9998);
		
		e2.insert(r1,"127.0.0.1", 5555);
		
		e2.envoiUDP();
		e2.recvUDP();
		e2.listenTCP();
	}
}
