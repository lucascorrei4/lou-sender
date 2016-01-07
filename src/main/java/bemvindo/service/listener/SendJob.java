package bemvindo.service.listener;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import bemvindo.service.controller.SendMailController;
import bemvindo.service.controller.SendSMSController;
import bemvindo.service.utils.Utils;

public class SendJob implements Job {
	public static Logger logger = Logger.getLogger(SendJob.class);

	public SendJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		String triggerFeedUrl = context.getTrigger().getJobKey().toString();

		logger.info("Starting SendMailController().controlMailSending(): " + triggerFeedUrl + " Job at " + Utils.dateNow());

		SendMailController sendController = new SendMailController();
		sendController.controlMailSending();

		logger.info("Finishing SendMailController().controlMailSending(): " + triggerFeedUrl + " Job at " + Utils.dateNow());

		logger.info("Starting SendSMSController().controlSMSSending(): " + triggerFeedUrl + " Job at " + Utils.dateNow());
		
		SendSMSController  sendSMSController = new SendSMSController();
		sendSMSController.controlSMSsending();
		
		logger.info("Finishing SendMailController().controlMailSending(): " + triggerFeedUrl + " Job at " + Utils.dateNow());
	}

}
