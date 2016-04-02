package bemvindo.service.rest;

import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import bemvindo.service.model.JsonBody;
import bemvindo.service.model.JsonBodyMail;
import bemvindo.service.model.JsonBodySMS;
import bemvindo.service.redis.JedisPersister;
import bemvindo.service.sender.SMS;
import bemvindo.service.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RestService {
	private static Logger logger = Logger.getLogger(RestService.class);

	public String parseJson(String type, String jsonContent) {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
		builder.setPrettyPrinting();
		builder.disableHtmlEscaping();
		builder.serializeNulls();
		builder.serializeSpecialFloatingPointValues();
		Gson gson = builder.create();
		String newJsonContent = null;
		JsonBody jsonBody = new JsonBody();
		newJsonContent = addSmsPropertiesToJsonContent(jsonContent, type);
		if ("mail".equals(type)) {
			logger.info("Parsing JsonBodyMail with json content");
			jsonBody.jsonBodyMail = gson.fromJson(newJsonContent, JsonBodyMail.class);
		}
		if ("sms".equals(type)) {
			logger.info("Parsing JsonBodySMS with json content");
			jsonBody.jsonBodySMS = gson.fromJson(newJsonContent, JsonBodySMS.class);
		}
		logger.info("Send object jsonBody to redis");
		return new JedisPersister().persist(jsonBody, type);
	}

	private String addSmsPropertiesToJsonContent(String jsonContent, String type) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(jsonContent).getAsJsonObject();
		/* Get "sender" json object and add "receivedAt" property */
		JsonObject senderObject = (JsonObject) jsonObject.get("sender");
		senderObject.addProperty("receivedAt", Utils.getCurrentDateTime());
		/* Add status properties to sendTo objects */
		JsonObject statusProperties = new JsonObject();
		statusProperties.addProperty("sent", false);
		statusProperties.addProperty("read", false);
		statusProperties.addProperty("sendDate", "not sent");
		if ("sms".equals(type)) {
			statusProperties.addProperty("msgId", 0);
		}
		/* Iterates sendTo objects adding status properties */
		JsonArray jsonArray = (JsonArray) jsonObject.get("sendTo");
		for (JsonElement jsonEl : jsonArray) {
			JsonObject jsonObj = jsonEl.getAsJsonObject();
			jsonObj.add("status", statusProperties);
		}
		return jsonObject.toString();
	}

	public String registerSender(String mail) {
		String prefixSender = "sender:".concat(mail);
		String authKey = new JedisPersister().searchKey(prefixSender);
		String response;
		if (StringUtils.isNotBlank(authKey)) {
			response = authKey;
		} else {
			String contentKey = "{".concat("\"security-key\"").concat(":").concat("\"").concat(Utils.randomKey()).concat("\"}");
			response = new JedisPersister().saveKey(prefixSender, contentKey);
		}
		return response;
	}

	public static void main(String[] args) {
		String contentKey = "{".concat("\"security-key\"").concat(":").concat("\"").concat(Utils.randomKey()).concat("\"}");
		System.out.println(contentKey);
	}

	public String loadBalance() {
		return "<h1>".concat("U$ ").concat(new SMS().getQueryBalance()).concat("</h1>");
	}

}
