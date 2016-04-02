package bemvindo.service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import bemvindo.service.model.BodyMail;
import bemvindo.service.model.JsonBody;
import bemvindo.service.model.JsonBodyMail;
import bemvindo.service.model.SendTo;
import bemvindo.service.model.Sender;
import bemvindo.service.model.Status;
import bemvindo.service.redis.JedisConectionPool;
import bemvindo.service.redis.JedisManager;
import bemvindo.service.redis.JedisPersister;
import bemvindo.service.sender.Mail;
import bemvindo.service.utils.Utils;

import com.google.gson.Gson;

public class SendMailController {
	public static Logger logger = Logger.getLogger(SendMailController.class);

	public void mailSendingControl() {
		try {
			JedisManager redis = getRegisManager();
			Set<String> keys = null;
			/*
			 * Connect in redis and list keys that are waiting to be sent with
			 * clausure 'mail*waiting'
			 */
			keys = redis.getKeysByPattern("mail*waiting");
			if (!keys.isEmpty()) {
				JsonBody jsonBody = new JsonBody();
				for (String key : keys) {
					String json = redis.getKey(key);
					if (!Utils.isNullOrEmpty(json)) {
						Gson gson = Utils.getGsonBuilder();
						JsonBodyMail jsonBodyMail = jsonBody.jsonBodyMail;
						jsonBodyMail = gson.fromJson(json, JsonBodyMail.class);
						Sender sender = jsonBodyMail.sender;
						/* Validate sender key */
						if (new JedisPersister().validateSenderAuthorization(sender.key, redis)) {
							BodyMail bodyMail = jsonBodyMail.bodyMail;
							List<SendTo> mailList = new ArrayList<SendTo>();
							mailList = jsonBodyMail.sendTo;
							if (!mailList.isEmpty() || mailList != null) {
								for (int attempt = 1; attempt <= 3; attempt++) {
									int mailsToSend = mailList.size();
									/*
									 * Send e-mails and return a list of
									 * unsuccessful sent
									 */
									logger.info("Institution: " + sender.company + ". Attempt " + attempt + ". Trying to send " + mailsToSend);
									try {
										List<SendTo> exceptionList = sendMail(mailList, sender, bodyMail);
										/*
										 * If the list is empty, this is a good
										 * signal and the key will be renamed
										 * from 'waiting' to 'sent'
										 */
										if ((exceptionList.isEmpty() || exceptionList == null)) {
											logger.info("Trying to set key: " + key);
											redis.set(key, jsonBodyMail.toString());
											renameKeySent(redis, key, sender);
											boolean renamedKey = false;
											while (!renamedKey) {
												if (redis.getKey(key) == null) {
													renamedKey = true;
												}
											}
											break;
										} else {
											/* Try again */
											logger.info("Institution: " + sender.company + " trying again to send e-mails");
											mailList = new ArrayList<SendTo>();
											mailList = exceptionList;
										}
										if (attempt == 3) {
											/*
											 * Create a new key with send
											 * failure
											 */
											setSendFailureOnRedis(redis, jsonBodyMail, mailList);
											break;
										}
										attempt++;
									} catch (Exception e) {
										e.getStackTrace();
									} finally {
										keys = null;
									}
								}
							}
						}
					}
				}
			} else {
				logger.info("No e-mails to send at " + Utils.dateNow() + ".");
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	private void setSendFailureOnRedis(JedisManager redis, JsonBodyMail jsonBodyMail, List<SendTo> mailList) {
		jsonBodyMail.sendTo = new ArrayList<SendTo>();
		jsonBodyMail.sendTo = mailList;
		String receivedAt = Utils.stringToDate(jsonBodyMail.sender.receivedAt, Utils.DEFAULT_DATE_FORMAT);
		String key = "mail" + ":" + jsonBodyMail.sender.key + ":" + receivedAt + ":failure";
		logger.info("Trying to set send failure key: " + key);
		redis.set(key, jsonBodyMail.toString());
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

	private List<SendTo> sendMail(List<SendTo> listSentTo, Sender sender, BodyMail bodyMail) {
		List<SendTo> exceptionList = new ArrayList<SendTo>();
		for (SendTo sendTo : listSentTo) {
			logger.info("Institution: " + sender.company + " trying to send to: " + sendTo.destination);
			try {
				Status status = sendTo.status;
				if (!status.sent) {
					Mail mail = new Mail();
					if (mail.sendHTMLMail(sendTo, sender, bodyMail)) {
						status.sent = true;
						status.sendDate = Utils.getCurrentDateTime();
					} else {
						exceptionList.add(sendTo);
					}
				}
			} catch (Exception e) {
				logger.error("Institution: " + sender.company + ". Error trying to send mail to: " + sendTo.destination);
				logger.error("E-mail added to exception list");
				exceptionList.add(sendTo);
				continue;
			}
		}
		return exceptionList;
	}
	
	public JedisManager getRegisManager() {
		JedisConectionPool redisPool = JedisConectionPool.getInstance();
		JedisManager redis = new JedisManager(redisPool);
		return redis;
	}

}
