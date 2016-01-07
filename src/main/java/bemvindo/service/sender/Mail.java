package bemvindo.service.sender;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import bemvindo.service.configuration.ApplicationConfiguration;
import bemvindo.service.model.BodyMail;
import bemvindo.service.model.SendTo;
import bemvindo.service.model.Sender;
import bemvindo.service.utils.Utils;

public class Mail {

	public static Logger logger = Logger.getLogger(Mail.class);

	public void sendSimpleMail() {
		try {
			SimpleEmail email = new SimpleEmail();
			System.out.println("alterando hostname...");
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(465);
			email.addTo("xxx@xxx.com", "Jose");
			email.setFrom("seuemail@seuprovedor.com", "Seu nome");
			email.setSubject("Test message");
			email.setMsg("This is a simple test of commons-email");
			System.out.println("autenticando...");
			email.setAuthentication("username", "senha");
			System.out.println("enviando...");
			email.send();
			System.out.println("Email enviado!");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendHTMLMail(SendTo sendTo, Sender sender, BodyMail bodyMail) {
		try {
			String userName = ApplicationConfiguration.getInstance().getMailUserName();
			String password = ApplicationConfiguration.getInstance().getMailPassword();
			String hostName = ApplicationConfiguration.getInstance().getMailHostName();
			if (validateMailCredentials(userName, password, hostName)) {
				HtmlEmail email = new HtmlEmail();
				email.setAuthentication(userName, password);
				email.setHostName(hostName);
				email.addTo(sendTo.destination, sendTo.name);
				email.setFrom(sender.from, sender.company);
				email.setSubject(bodyMail.title1);
				String url = "http://wgvjavap.javaprovider.net/public/templatepublic/images/logo-small.png";
				email.setHtmlMsg("<html>The apache logo - <img src=\"" + url + "\"></html>");
				email.setTextMsg("Seu servidor de e-mail não suporta mensagem HTML");
				email.send();
			} else {
				logger.error("Problem validate mail credentials (username, password, hostname). Verify project configuration properties.");
			}
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}

	private boolean validateMailCredentials(String userName, String password, String hostName) {
		if (Utils.isNullOrEmpty(userName) || Utils.isNullOrEmpty(password) || Utils.isNullOrEmpty(hostName)) {
			return false;
		}
		return true;
	}

}
