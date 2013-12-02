import java.io.DataOutputStream;
import java.io.IOException;

public class ClientMessageHandler extends Thread {

	Replica replica;

	public ClientMessageHandler(Replica replica) {
		this.replica = replica;
	}

	public void run() {
		while (true) {
			ClientMessageDetails currentMessage = null;
			synchronized (replica.clientMessages) {
				currentMessage = replica.clientMessages.poll();
			}
			if (currentMessage != null) {
				System.out.println(currentMessage.message);
				
				if (!replica.isFailed
						|| (currentMessage.message.startsWith("unfail") 
								&& replica.isFailed)) {
					handleClientMessage(currentMessage);
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

	// Message parts are "fail" or "unfail" or "read" or "post|Message Body"
	public void handleClientMessage(ClientMessageDetails currentMessage) {
		String[] messageParts = new String[1];
		if(currentMessage.message.contains("|"))
		{
			messageParts = currentMessage.message.split("|");
		}
		else
		{
			messageParts[0] = currentMessage.message;
		}

		switch (messageParts[0]) {
		case "post":
			String valueToPost = messageParts[1];
			//Check for next log position from log arraylist
			//check in hashmap for active paxos instance with this id
			//if not found create a new paxos instance with this id
			Paxos testPaxos;
			int numEntries = this.replica.paxosEntries.size();
			if (numEntries == 0 || (this.replica.paxosEntries.get(numEntries - 1)).isDecided){
				this.replica.paxosEntries.add(new Paxos(numEntries,this.replica.replicaId,valueToPost,this.replica.logger));
				testPaxos = this.replica.paxosEntries.get(numEntries);
			}
			else {
				testPaxos = this.replica.paxosEntries.get(numEntries -1);
			}
			
		    
			//set this.proposer = true; iamProposer()
		    testPaxos.iamProposer();
		    if(testPaxos.sendPrepare() == 0) 
		    	replyToClient(currentMessage,"Failed to post");
			//sendprepare message
			//if send prepare returns 1 go for next round
			//else reply back with a fail message to client
			break;
		case "read":
			String logEntries = getLog();
			replyToClient(currentMessage, logEntries);
			break;
		case "fail":
			replica.isFailed = true;
			replyToClient(currentMessage, "Server is down");
			break;
		case "unfail":
			replica.isFailed = false;
			replyToClient(currentMessage, "Server is up back");
			break;
		default:
			break;

		}
	}
/*
	public String getLog() {
		StringBuilder logEntries = new StringBuilder();
		for (Paxos paxos : replica.paxosEntries) {
			if (paxos.acceptedValue != null)
				logEntries.append(paxos.acceptedValue + "\n");
		}
		return logEntries.toString();
	}
*/
	public String getLog() {
		StringBuilder logEntries = new StringBuilder();
		for(Paxos paxos : this.replica.paxosEntries) {
			if(paxos.isDecided) 
				logEntries.append(paxos.valueWritten + "\n");

		}
		return logEntries.toString();
	}
	
	public void replyToClient(ClientMessageDetails clientMessage,
			String replyMessage) {
		try {
			DataOutputStream outToClient = new DataOutputStream(
					clientMessage.clientSocket.getOutputStream());

			outToClient.writeBytes(replyMessage + "\n");
			System.out.println("client reply = " + replyMessage);
			clientMessage.clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
