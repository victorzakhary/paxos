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
    public void sendMessage() {
    	
    }
	public void handleClientMessage(String currentMessage) {
		String[] messageParts = currentMessage.split("|");
		int senderReplicaId = Integer.parseInt(messageParts[1]);
		int paxosId = Integer.parseInt(messageParts[2]);
		int numEntries = this.replica.paxosEntries.size();
		Paxos currentPaxos;
		
		switch (Integer.parseInt(messageParts[0])) {
		case 1:
			BallotPair proposedBallotNumPair = new BallotPair(Integer.parseInt(messageParts[4]), Integer.parseInt(messageParts[3]));
			//receivePrepareMsg
			this.replica.logger.write("Received Prepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
			if(paxosId > numEntries - 1) {
				//Create a new one
				this.replica.paxosEntries.add(new Paxos(paxosId,this.replica.replicaId,"",this.replica.logger));
				currentPaxos = this.replica.paxosEntries.get(numEntries);
				currentPaxos.onreceivePrepare(proposedBallotNumPair, senderReplicaId);
			}
			else if( paxosId == numEntries - 1) {
				//check if it is active or not
				currentPaxos = this.replica.paxosEntries.get(numEntries - 1);
				//active and join it - else already decided and send decide
				if(!currentPaxos.isDecided) {
					currentPaxos.onreceivePrepare(proposedBallotNumPair, senderReplicaId);
				}
				else {
					MessageCommunication.sendDecideUnicast(this.replica.replicaId,senderReplicaId, paxosId, currentPaxos.valueWritten);
				}
			}
			else {
				//already decided
				currentPaxos = this.replica.paxosEntries.get(numEntries - 1);
				MessageCommunication.sendDecideUnicast(this.replica.replicaId,senderReplicaId, paxosId, currentPaxos.valueWritten);
			}
			break;
		case 2:
			//receiveAcktoPrepareMsg
			this.replica.logger.write("Received AckToPrepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
			//Ongoing paxos Instance
			if(paxosId == numEntries - 1 && !this.replica.paxosEntries.get(paxosId).isDecided) {
				currentPaxos = this.replica.paxosEntries.get(paxosId);
				BallotPair ownBallotNumPair = new BallotPair(Integer.parseInt(messageParts[4]), Integer.parseInt(messageParts[3]));
				BallotPair acceptedBallotNumPair = new BallotPair(Integer.parseInt(messageParts[6]), Integer.parseInt(messageParts[5]));
				currentPaxos.onreceiveAckToPrepare(ownBallotNumPair, acceptedBallotNumPair,messageParts[7]);
			}
			else {
				if(paxosId < numEntries - 1) {
					MessageCommunication.sendDecideUnicast(this.replica.replicaId, senderReplicaId, paxosId, this.replica.paxosEntries.get(paxosId).valueWritten);
					this.replica.logger.write("Responded to  AckToPrepare Msg with Decide Msg for Paxos Instance: " + String.valueOf(paxosId) );
				}
				else {
					this.replica.logger.write("Ignoring AckToPrepare Msg for Paxos Instance: " + String.valueOf(paxosId) );
				}
			}			
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			break;
		case 6:
			break;
		case 7:
			
			break;
		case 8:
			break;
		default:
			break;

		}
	}

}
