package bemvindo.service.rest;

import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import bemvindo.service.model.JsonBody;
import bemvindo.service.model.JsonBodyMail;
import bemvindo.service.model.JsonBodySMS;
import bemvindo.service.redis.JedisPersister;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BemVindoRestService {
	private static Logger logger = Logger.getLogger(BemVindoRestService.class);
	
	public String parseJson(String type, String jsonContent) {
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
		builder.setPrettyPrinting();
		builder.disableHtmlEscaping();
		builder.serializeNulls();
		builder.serializeSpecialFloatingPointValues();
	
		Gson gson = builder.create();

		JsonBody jsonBody = new JsonBody();

		if ("mail".equals(type)) {
			logger.info("Parsing JsonBodyMail with json content");
			jsonBody.jsonBodyMail = gson.fromJson(jsonContent, JsonBodyMail.class);
		}

		if ("sms".equals(type)) {
			logger.info("Parsing JsonBodySMS with json content");
			jsonBody.jsonBodySMS = gson.fromJson(jsonContent, JsonBodySMS.class);
		}

		logger.info("Send object jsonBody to redis");
		return new JedisPersister().persist(jsonBody, type);
	}

}
