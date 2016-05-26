package Ringo;

import java.net.DatagramSocket;

public abstract class Application 
{
	public abstract String getId();
	public abstract void exec(String idm,Entite entite,DatagramSocket dso);
	public abstract void traiter(String mess,Entite entite,DatagramSocket dso);
}
