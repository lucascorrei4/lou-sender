package bemvindo.service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import bemvindo.service.model.BodySMS;
import bemvindo.service.model.JsonBody;
import bemvindo.service.model.JsonBodySMS;
import bemvindo.service.model.SendTo;
import bemvindo.service.model.Sender;
import bemvindo.service.model.Status;
import bemvindo.service.redis.JedisConectionPool;
import bemvindo.service.redis.JedisManager;
import bemvindo.service.sender.SMS;
import bemvindo.service.utils.Utils;

import com.google.gson.Gson;

public class SendSMSController {
	public static Logger logger = Logger.getLogger(SendSMSController.class);

	public void controlSMSsending() {
		try {
			JedisConectionPool redisPool = JedisConectionPool.getInstance();
			JedisManager redis = new JedisManager(redisPool);
			/*
			 * Connect in redis and list keys that are waiting to be sent with
			 * clausure 'sms*waiting'
			 */
			Set<String> keys = redis.getKeysByPattern("KEYS sms*waiting");
			JsonBody jsonBody = new JsonBody();
			for (String key : keys) {
				String json = redis.getKey(key);
				if (!Utils.isNullOrEmpty(json)) {
					Gson gson = Utils.getGsonBuilder();
					JsonBodySMS jsonBodySMS = jsonBody.jsonBodySMS;
					jsonBodySMS = gson.fromJson(json, JsonBodySMS.class);
					Sender sender = jsonBodySMS.sender;
					BodySMS bodySMS = jsonBodySMS.bodySMS;
					List<SendTo> smsList = new ArrayList<SendTo>();
					smsList = jsonBodySMS.sendTo;
					if (!smsList.isEmpty() || smsList != null) {
						for (int attempt = 1; attempt == 3; attempt++) {
							int smssToSend = smsList.size();
							/*
							 * Send sms and return a list of unsuccessful sent
							 */
							logger.info("Institution: " + sender.company + ". Attempt " + attempt + ". Trying to send " + smssToSend);
							try {
								List<SendTo> exceptionList = sendSMS(smsList, sender, bodySMS);
								/*
								 * If the list is empty, this is a good signal
								 * and the key will be renamed from 'waiting' to
								 * 'sent'
								 */
								if ((exceptionList.isEmpty() || exceptionList == null)) {
									renameKeySent(redis, key, sender);
									break;
								} else {
									/* Try again */
									logger.info("Institution: " + sender.company + " trying again to send SMS's");
									smsList = new ArrayList<SendTo>();
									smsList = exceptionList;
								}
								if (attempt == 3) {
									/* Create a new key with send failure */
									setSendFailureOnRedis(redis, jsonBodySMS, smsList);
									break;
								}
							} catch (Exception e) {
								e.getStackTrace();
							}
							attempt++;
						}
					}
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	private void setSendFailureOnRedis(JedisManager redis, JsonBodySMS jsonBodySMS, List<SendTo> smsList) {
		jsonBodySMS.sendTo = new ArrayList<SendTo>();
		jsonBodySMS.sendTo = smsList;
		String postedAt = Utils.stringToDate(jsonBodySMS.sender.postedAt, Utils.DEFAULT_DATE_FORMAT);
		String key = "sms" + ":" + jsonBodySMS.sender.key + ":" + postedAt + ":failure";
		logger.info("Trying to set send failure key: " + key);
		redis.set(key, jsonBodySMS.toString());
		logger.info("Trying to get key: " + key);
		if (redis.getKey(key) != null) {
			logger.info("Success! Key: " + key + " found!");
		}
	}

	private void renameKeySent(JedisManager redis, String key, Sender sender) {
		String newKey = Utils.replace(key, "waiting", "sent");
		logger.info("Institution: " + sender.company + " trying to rename key from: " + key + " to " + newKey);
		String status = redis.renameKey(key, newKey);
		if ("OK".equals(status)) {
			logger.info("Successful! Institution: " + sender.company + " trying to rename key from: " + key + " to " + newKey);
		}
	}

	private List<SendTo> sendSMS(List<SendTo> listSentTo, Sender sender, BodySMS bodySMS) {
		List<SendTo> exceptionList = new ArrayList<SendTo>();
		for (SendTo sendTo : listSentTo) {
			logger.info("Institution: " + sender.company + " trying to send to: " + sendTo.destination);
			try {
				Status status = sendTo.status;
				if (!status.sent) {
					SMS sms = new SMS();
					sms.sendSMS(sendTo, sender, bodySMS);
					status.sent = true;
					status.sendDate = Utils.getCurrentDateTime();
				}
			} catch (Exception e) {
				logger.error("Institution: " + sender.company + ". Error trying to send sms to: " + sendTo.destination);
				logger.error("SMS added to exception list");
				exceptionList.add(sendTo);
				continue;
			}
		}
		return exceptionList;
	}

}
