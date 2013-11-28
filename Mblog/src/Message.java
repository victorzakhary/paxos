import java.util.ArrayList;
import java.util.List;


public class Message {

	
	ArrayList<String> parts ;
	
	public Message() {
		parts = new ArrayList<String>();
	}
	
	public void add (String x)
	{
		parts.add(x);
	}
	public String getMessage()
	{
		StringBuilder message = new StringBuilder();
		for (String x : parts)
		{
			message.append(x+"|");
		}
		return message.subSequence(0, message.length()-1).toString();
	}

}
