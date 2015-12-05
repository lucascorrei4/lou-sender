package bemvindo.service.model;

public class BodySMS {
	public String title;
	public String message;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"title\":\"" + title + "\",");
		sb.append("\"message\":\"" + message + "\"");
		sb.append("}");

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new BodySMS().toString());
	}
}
