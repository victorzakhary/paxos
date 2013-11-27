
public class MessageCommunication {

	public MessageCommunication() {
		
	}
	
	public void sendPrepareMsg(int paxosId, BallotPair proposedBallotNumPair) {
		//BroadCast this message to everybody.
		//only broadcast a string with a delimtetr
	}
	
	public void sendAckToPrepare(int paxosId, BallotPair ownBallotNumPair, BallotPair acceptedBallotNumPair, String acceptedValue) {
		
	}
	
	public void sendNAckToPrepare(int paxosId, BallotPair ownBallotNumPair, BallotPair acceptedBallotNumPair, String acceptedValue) {
		
	}
	
	public void sendAccept(int paxosId, BallotPair proposeBallotNumPair, String ValueToWrite) {
		
	}

	public void sendDecide(int paxosId, String acceptedValue) {
		//keep sending it periodically Ask_Victor would it block the call
		//put it in a queue of messages which have to be periodically broadcasted.
	}
}
