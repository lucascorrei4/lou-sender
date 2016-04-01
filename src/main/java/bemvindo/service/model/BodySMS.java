package bemvindo.service.model;

import bemvindo.service.utils.Utils;

public class BodySMS {
	public String title;
	public String message;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"title\":\"" + Utils.removeAccentuation(title) + "\",");
		sb.append("\"message\":\"" + Utils.removeAccentuation(message) + "\"");
		sb.append("}");

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new BodySMS().toString());
	}
}
