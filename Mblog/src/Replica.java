import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class Replica {

	int replicaId;
	ArrayList<Paxos> paxosEntries;
	String logFilePath;
	String configFilePath;
	String indexFilePath;
	ArrayList<ReplicaCommInfo> replicas;
	int logIndex;
	boolean isFailed;
	Queue<MessageHandler> clientMessages;
	public Replica(int replicaId, String logFilePath,String configFilePath)
	{
		// replica ID will be read from the first line in the configuration file
		this.logFilePath = logFilePath;
		this.configFilePath = configFilePath;
		replicas = new ArrayList<ReplicaCommInfo>(10);
		clientMessages = new LinkedList<MessageHandler>() ;
		logIndex = -1;
		this.isFailed = false;
		readConfiguration();
		startInstance();
		Receiver clientReceiver = new Receiver();
		clientReceiver.start();
		
	}
	private void readConfiguration()
	{
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
		ServerSocket listener = null;
        try {
        	listener = new ServerSocket(this.replicas.get(replicaId).socketId);
            while (true) {
            	 Socket connectionSocket = listener.accept();
                 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                 String clientMessage = inFromClient.readLine();
                 MessageHandler handler = new MessageHandler(this, clientMessage, connectionSocket);
                 handler.start();
           }
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
        finally {
        	if (listener != null)
				try {
					listener.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
	 
	}
}
