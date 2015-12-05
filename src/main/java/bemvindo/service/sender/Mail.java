package bemvindo.service.sender;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

import bemvindo.service.configuration.ApplicationConfiguration;
import bemvindo.service.model.BodyMail;
import bemvindo.service.model.SendTo;
import bemvindo.service.model.Sender;

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
			HtmlEmail email = new HtmlEmail();
			email.setAuthentication(ApplicationConfiguration.getInstance().getMailUserName(), ApplicationConfiguration.getInstance().getMailPassword());
			email.setHostName(ApplicationConfiguration.getInstance().getMailHostName());
			email.addTo(sendTo.destination, sendTo.name);
			email.setFrom(sender.from, sender.company);
			email.setSubject(bodyMail.title1);
			URL url = new URL("http://jsonlint.com/c/images/kindling.png");
			String cid = email.embed(url, "logo");
			email.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid + "\"></html>");
			email.setTextMsg("Seu servidor de e-mail não suporta mensagem HTML");
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
