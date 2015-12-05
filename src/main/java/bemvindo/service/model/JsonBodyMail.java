package bemvindo.service.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonBodyMail extends JsonBody {
	public Sender sender = new Sender();

	public BodyMail bodyMail = new BodyMail();

	public List<SendTo> sendTo = new ArrayList<SendTo>();
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"sender\": ");
		sb.append(sender + ",");
		sb.append("\"bodyMail\": ");
		sb.append(bodyMail + ",");
		sb.append("\"sendTo\": [");
		for (Iterator<SendTo> iterator = sendTo.iterator(); iterator.hasNext();) {
			sb.append(iterator.next());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new JsonBodyMail().toString());
	}
}
