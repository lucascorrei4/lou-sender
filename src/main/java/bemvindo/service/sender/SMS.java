package bemvindo.service.sender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import bemvindo.service.configuration.ApplicationConfiguration;
import bemvindo.service.model.BodySMS;
import bemvindo.service.model.SendTo;
import bemvindo.service.model.Sender;

public class SMS {
	public String STR_URL_SEND_SMS = "https://www.experttexting.com/exptapi/exptsms.asmx/SendSMS?";
	public String STR_URL_STATUS_SMS = "https://www.experttexting.com/exptapi/exptsms.asmx/MsgStatus?";
	public String STR_URL_QUERY_BALANCE = "https://www.experttexting.com/exptapi/exptsms.asmx/QueryBalance?";
	public String STR_SMS_USER_ID = ApplicationConfiguration.getInstance().getSMSUserId();
	public String STR_SMS_PWD = ApplicationConfiguration.getInstance().getSMSPwd();
	public String STR_SMS_API_KEY = ApplicationConfiguration.getInstance().getSMSApiKey();

	public void sendSMS(SendTo sendTo, Sender sender, BodySMS bodySMS) {
		try {
			String from = sender.from;
			String to = sendTo.destination;
			String msg = bodySMS.message;
			URL url = new URL(STR_URL_SEND_SMS + "Userid=" + STR_SMS_USER_ID + "&pwd=" + STR_SMS_PWD + "&APIKEY=" + STR_SMS_API_KEY + "&FROM=" + from + "&To=" + to + "&MSG=" + msg);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	public void getStatusSMS(int msgId) {
		try {
			URL url = new URL(STR_URL_STATUS_SMS + "Userid=" + STR_SMS_USER_ID + "&pwd=" + STR_SMS_PWD + "&APIKEY=" + STR_SMS_API_KEY + "&MSGID=" + msgId);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	public void getQueryBalance() {
		try {
			URL url = new URL(STR_URL_QUERY_BALANCE + "Userid=" + STR_SMS_USER_ID + "&pwd=" + STR_SMS_PWD + "&APIKEY=" + STR_SMS_API_KEY);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// 64018048 22h30 64018065 22H37
		// new SendSMSController().getStatusSMS(64016211);
		// new SendSMSController().getQueryBalance();
	}
}
