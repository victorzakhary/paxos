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
		int senderReplicaId = Integer.parseInt(messageParts[1]);
		int paxosId = Integer.parseInt(messageParts[2]);
		
		switch (Integer.parseInt(messageParts[0])) {
		case 1:
			//receivePrepareMsg
			this.replica.logger.write("Received Prepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
			//Already present in Log
			if(paxosId <= this.replica.logEntries.size()) {
				
				Paxos tempPaxos = this.replica.paxosEntries.get(paxosId);
				InterServerMessage message = new InterServerMessage();
				message.add("5");
				message.add(Integer.toString(this.replica.replicaId));
				message.add(Integer.toString(paxosId));
				message.add(this.replica.logEntries.get(paxosId - 1));				
				MessageCommunication.unicastToServer(message.getMessage(),senderReplicaId);
				
			}
			//Check for ongoing paxos Instance for this log position
			else {
				Paxos currentPaxos = this.replica.paxosEntries.get(paxosId);
				if(currentPaxos == null) {
					//(int id, int replicaid, int num_total_servers, String write_req_from_client, Logging replicaLogger)
					this.replica.paxosEntries.put(paxosId, new Paxos(paxosId, this.replica.replicaId, "", this.replica.logger));
					currentPaxos = this.replica.paxosEntries.get(paxosId);
				}
			
				BallotPair receivedBallotNumPair = new BallotPair(Integer.parseInt(messageParts[4]),Integer.parseInt(messageParts[3]));
				currentPaxos.onreceivePrepare(receivedBallotNumPair,senderReplicaId);
				//create a new one otherwise			
			}
			
			break;
		case 2:
			//receiveAcktoPrepareMsg
			this.replica.logger.write("Received AckToPrepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
			//Already present in Log
			if(paxosId <= this.replica.logEntries.size()) {
				//Ignore it
				this.replica.logger.write("Ignoring AckToPrepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
			}	
			//Check for ongoing paxos Instance for this log position
			else {
				Paxos currentPaxos = this.replica.paxosEntries.get(paxosId);
				if(currentPaxos == null) {
					//(int id, int replicaid, int num_total_servers, String write_req_from_client, Logging replicaLogger)
					this.replica.paxosEntries.put(paxosId, new Paxos(paxosId, this.replica.replicaId, "", this.replica.logger));
					currentPaxos = this.replica.paxosEntries.get(paxosId);
				}
			
				BallotPair receivedBallotNumPair = new BallotPair(Integer.parseInt(messageParts[4]),Integer.parseInt(messageParts[3]));
				currentPaxos.onreceivePrepare(receivedBallotNumPair,senderReplicaId);
				//create a new one otherwise			
			}
			
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

}
