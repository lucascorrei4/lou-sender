package bemvindo.service.model;

public class Status {
	public boolean sent;
	public boolean read;

	public String sendDate;
	public int msgId;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"sent\":" + sent + ",");
		sb.append("\"read\":" + read + ",");
		sb.append("\"sendDate\":\"" + sendDate + "\",");
		sb.append("\"msgId\":" + msgId + "");
		sb.append("}");
		
		return sb.toString();
	}
}
