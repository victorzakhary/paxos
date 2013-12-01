import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;


public class Replica {

	int replicaId;
	ArrayList<Paxos> paxosEntries;
	String logFilePath;
	String configFilePath;
	ArrayList<ReplicaCommInfo> replicas;
	boolean isFailed;
	Queue<ClientMessageDetails> clientMessages;
	
	public Replica(String logFilePath,String configFilePath)
	{
		// replica ID will be read from the first line in the configuration file
		this.logFilePath = logFilePath;
		this.configFilePath = configFilePath;
		replicas = new ArrayList<ReplicaCommInfo>(10);
		clientMessages = new LinkedList<ClientMessageDetails>() ;
		this.isFailed = false;
		readConfiguration();
		startInstance();
		start();
	}
	private void readConfiguration()
	{
		System.out.println("Read Configuration");
		System.out.println(configFilePath);
		String current = "";
		try {
			current = new java.io.File( "." ).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Current dir:"+current);
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(FileSystems.getDefault().getPath(configFilePath), charset)) {
		    String line = null;
		    
		    line = reader.readLine();
		    this.replicaId = Integer.parseInt(line.trim());
		    while ((line = reader.readLine()) != null) {
		        replicas.add(new ReplicaCommInfo(line));
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		    System.out.println("Failed to read configuration");
		}
		Collections.sort(replicas);
	}
	
	private void startInstance()
	{
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(FileSystems.getDefault().getPath(logFilePath), charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	//paxosEntries.add(line);
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	private void start() 
	{
		ClientReceiver clientReceiver = new ClientReceiver(this);
		clientReceiver.start();
		ClientMessageHandler handler = new ClientMessageHandler(this);
		handler.start();
	}
}
