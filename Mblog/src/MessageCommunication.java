import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessageCommunication {

	ArrayList<ReplicaCommInfo> receivers;

	public MessageCommunication(ArrayList<ReplicaCommInfo> receivers) {
		this.receivers = receivers;
	}

	// prepare message type = 1
	public void sendPrepareMsg(int paxosId, BallotPair proposedBallotNumPair) {
		InterServerMessage message = new InterServerMessage();
		message.add("1");
		message.add(Integer.toString(paxosId));
		message.add(Integer.toString(proposedBallotNumPair.processId));
		message.add(Integer.toString(proposedBallotNumPair.ballotNum));
		broadCast(message.getMessage());
	}

	// ack message type = 2
	public void sendAckToPrepare(int paxosId, BallotPair ownBallotNumPair,
			BallotPair acceptedBallotNumPair, String acceptedValue) {
		InterServerMessage message = new InterServerMessage();
		message.add("2");
		message.add(Integer.toString(paxosId));
		message.add(Integer.toString(ownBallotNumPair.processId));
		message.add(Integer.toString(ownBallotNumPair.ballotNum));
		message.add(Integer.toString(acceptedBallotNumPair.processId));
		message.add(Integer.toString(acceptedBallotNumPair.ballotNum));
		message.add(acceptedValue);
		broadCast(message.getMessage());
	}

	// nAck message type = 3
	public void sendNAckToPrepare(int paxosId, BallotPair ownBallotNumPair,
			BallotPair acceptedBallotNumPair, String acceptedValue) {
		InterServerMessage message = new InterServerMessage();
		message.add("3");
		message.add(Integer.toString(paxosId));
		message.add(Integer.toString(ownBallotNumPair.processId));
		message.add(Integer.toString(ownBallotNumPair.ballotNum));
		message.add(Integer.toString(acceptedBallotNumPair.processId));
		message.add(Integer.toString(acceptedBallotNumPair.ballotNum));
		message.add(acceptedValue);
		broadCast(message.getMessage());
	}

	// accept message type = 4
	public void sendAccept(int paxosId, BallotPair proposeBallotNumPair,
			String ValueToWrite) {
		InterServerMessage message = new InterServerMessage();
		message.add("4");
		message.add(Integer.toString(paxosId));
		message.add(Integer.toString(proposeBallotNumPair.processId));
		message.add(Integer.toString(proposeBallotNumPair.ballotNum));
		message.add(ValueToWrite);
		broadCast(message.getMessage());

	}
	
	// Decide message type = 5
	public void sendDecide(int paxosId, String acceptedValue) {
		InterServerMessage message = new InterServerMessage();
		message.add("5");
		message.add(Integer.toString(paxosId));
		message.add(acceptedValue);
		broadCast(message.getMessage());
	}

	public void broadCast(String message) {
		for (ReplicaCommInfo receiver : receivers) {
			Socket clientSocket;
			try {
				// if (receiver.replicaId != replicaId) {
				clientSocket = new Socket(receiver.replicaIP, receiver.socketId);
				DataOutputStream outToServer = new DataOutputStream(
						clientSocket.getOutputStream());
				outToServer.writeBytes(message + "\n");
				clientSocket.close();
				// }

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
