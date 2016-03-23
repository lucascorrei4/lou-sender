package bemvindo.service.rest;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import bemvindo.service.model.JsonBody;
import bemvindo.service.model.JsonBodyMail;
import bemvindo.service.model.JsonBodySMS;
import bemvindo.service.redis.JedisConectionPool;
import bemvindo.service.redis.JedisManager;
import bemvindo.service.redis.JedisPersister;
import bemvindo.service.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	public String registerSender(String mail) {
		String prefixSender = "sender:".concat(mail);
		String authKey = new JedisPersister().searchKey(prefixSender);
		String response = null;
		if (StringUtils.isNotBlank(authKey)) {
			response = authKey;
		} else {
			response = new JedisPersister().saveKey(prefixSender, Utils.randomKey());
		}
		return response;
	}

}
