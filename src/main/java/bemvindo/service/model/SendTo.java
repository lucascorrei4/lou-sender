package bemvindo.service.model;

public class SendTo {
	public String name;
	public String destination;
	public String sex;
	
	public Status status;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"" + name + "\",");
		sb.append("\"destination\":\"" + destination + "\",");
		sb.append("\"sex\":\"" + sex + "\",");
		sb.append("\"status\": ");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new SendTo().toString());
	}
}
