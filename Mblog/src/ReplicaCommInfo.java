import java.net.InetAddress;
import java.net.UnknownHostException;


public class ReplicaCommInfo implements Comparable<ReplicaCommInfo>{
	int replicaId;
	int socketId;
	InetAddress replicaIP;
	
	public ReplicaCommInfo (String configurationLine )
	{
		String cofigurationParts [] = configurationLine.split(" ");
		this.replicaId = Integer.parseInt(cofigurationParts[0]);
		this.socketId= Integer.parseInt(cofigurationParts[2]);
		try {
			this.replicaIP = InetAddress.getByName(cofigurationParts[1]);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(ReplicaCommInfo o) {
		return this.replicaId-o.replicaId;
	}
	
}
