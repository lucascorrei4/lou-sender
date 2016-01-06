package bemvindo.service.listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SendJob implements Job {
	public static Logger logger = Logger.getLogger(SendJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateNow = formatter.format(currentDate.getTime());
		String triggerFeedUrl = context.getTrigger().getJobKey().toString();


		logger.warn("Starting : " + triggerFeedUrl + " Job at " + dateNow);
		System.out.println("Starting : " + triggerFeedUrl + " Job at " + dateNow);
		
		// SendMailController sendController = new SendMailController();
		// sendController.controlMailSending();
	}

}
