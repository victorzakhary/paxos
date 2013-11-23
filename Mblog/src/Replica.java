import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;


public class Replica {

	int replicaId;
	ArrayList<Paxos> paxosEntries;
	String logFilePath;
	String configFilePath;
	ArrayList<ReplicaCommInfo> replicas;
	public Replica(int replicaId, String logFilePath,String configFilePath)
	{
		this.replicaId =replicaId;
		this.logFilePath = logFilePath;
		this.configFilePath = configFilePath;
		replicas = new ArrayList<ReplicaCommInfo>(10);
		readConfiguration();
		startInstance();
	}
	private void readConfiguration()
	{
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(FileSystems.getDefault().getPath(configFilePath), charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        paxosEntries.add(new Paxos(line));
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		Collections.sort(replicas);
		for(ReplicaCommInfo x : replicas)
		{
			System.out.println(x.replicaId+ " " + x.socketId+ " "+ x.replicaIP);
		}
	}
	
	private void startInstance()
	{
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(FileSystems.getDefault().getPath(configFilePath), charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	//paxosEntries.add(Integer.parseInt(line.split(" ")[0]),new ReplicaCommInfo(line));
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		Collections.sort(replicas);
		for(ReplicaCommInfo x : replicas)
		{
			System.out.println(x.replicaId+ " " + x.socketId+ " "+ x.replicaIP);
		}
	}
}
