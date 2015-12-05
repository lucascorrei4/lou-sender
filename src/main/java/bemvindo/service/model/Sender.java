package bemvindo.service.model;

public class Sender {
	public String key;
	public String company;
	public String from;
	public String postedAt;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"key\":\"" + key + "\",");
		sb.append("\"company\":\"" + company + "\",");
		sb.append("\"from\":\"" + from + "\",");
		sb.append("\"postedAt\":\"" + postedAt + "\"");
		sb.append("}");

		return sb.toString();
	}
}
