package bemvindo.service.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

@WebListener
public class QuartzInitializer extends QuartzInitializerListener {
	/* Method initialized cause is referenced in listener node in web.xml file */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		ServletContext context = sce.getServletContext();
		/* Adding rootPah variable */
		System.setProperty("rootPath", context.getRealPath("/"));
		ServletContext ctx = sce.getServletContext();
		StdSchedulerFactory factory = (StdSchedulerFactory) ctx.getAttribute(QUARTZ_FACTORY_KEY);
		try {
			Scheduler scheduler = factory.getScheduler();
			JobDetail jobDetail = JobBuilder.newJob(SendJob.class).build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("simple").withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *")).startNow().build();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
		} catch (Exception e) {
			ctx.log("There was an error scheduling the job.", e);
		}
	}
}
