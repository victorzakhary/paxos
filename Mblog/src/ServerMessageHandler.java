import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerMessageHandler extends Thread {
	Replica replica;

	public ServerMessageHandler(Replica replica) {
		this.replica = replica;
	}

	public void run() {
		while (true) {
			String serverMessage = null;
			synchronized (replica.serverMessages) {
				serverMessage = replica.serverMessages.poll();
			}
			if (serverMessage != null) {
				System.out.println(serverMessage);

				if (!replica.isFailed) {
					handleClientMessage(serverMessage);
				}
			} else {
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public void handleClientMessage(String currentMessage) {
		String[] messageParts = currentMessage.split("|");

		switch (Integer.parseInt(messageParts[0])) {
		case 1:

			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		default:
			break;

		}
	}

	/*
	 * public String getLog() { StringBuilder logEntries = new StringBuilder();
	 * for (Paxos paxos : replica.paxosEntries) { if (paxos.acceptedValue !=
	 * null) logEntries.append(paxos.acceptedValue + "\n"); } return
	 * logEntries.toString(); }
	 */

	public void replyToServer(String replyMessage, int replicaId) {
		try {
			
			ReplicaCommInfo communicationInfo = replica.replicas.get(replicaId);
			
			Socket clientSocket = new Socket(communicationInfo.replicaIP, communicationInfo.serverSocketId);
	        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	        outToServer.writeBytes(replyMessage + "\n");
	        clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
