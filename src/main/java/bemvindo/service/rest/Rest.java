package bemvindo.service.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import bemvindo.service.redis.JedisPersister;
import bemvindo.service.utils.Utils;

@Path("/")
public class Rest {

	@GET
	@Path("/save-key")
	public Response saveKey(@QueryParam("key") String key, @QueryParam("value") String value) {
		try {
			if (!Utils.isNullOrEmpty(key) && !Utils.isNullOrEmpty(value)) {
				String status = new JedisPersister().saveKey(key, value);
				return Response.status(200).header("Content-Type", "text/html; charset=utf-8").entity(status).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(405).header("Content-Type", "text/html; charset=utf-8").entity("Acesso não permitido.").build();
	}

	@GET
	@Path("/get-key")
	public Response getKey(String key) {
		try {
			if (!Utils.isNullOrEmpty(key)) {
				String status = new JedisPersister().searchKey(key);
				return Response.status(200).header("Content-Type", "text/html; charset=utf-8").entity(status).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(405).header("Content-Type", "text/html; charset=utf-8").entity("Acesso não permitido.").build();
	}

	@POST
	@Path("/send-mail")
	public Response sendMail(String jsonContent) {
		try {
			if (!Utils.isNullOrEmpty(jsonContent)) {
				RestService restService = new RestService();
				String status = restService.parseJson("mail", jsonContent);
				return Response.status(200).header("Content-Type", "application/json; charset=utf-8").entity(status).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(405).header("Content-Type", "application/json; charset=utf-8").entity("Acesso não permitido.").build();
	}

	@POST
	@Path("/send-sms")
	public Response sendSMS(String jsonContent) {
		try {
			if (!Utils.isNullOrEmpty(jsonContent)) {
				RestService restService = new RestService();
				String status = restService.parseJson("sms", jsonContent);
				return Response.status(200).header("Content-Type", "application/json; charset=utf-8").entity(status).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(405).header("Content-Type", "application/json; charset=utf-8").entity("Acesso não permitido.").build();
	}
	
	@GET
	@Path("/register-sender")
	public Response registerSender(@QueryParam("mail") String mail) {
		try {
			if (!Utils.isNullOrEmpty(mail)) {
				RestService restService = new RestService();
				String status = restService.registerSender(mail);
				return Response.status(200).header("Content-Type", "text/html; charset=utf-8").entity(status).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(405).header("Content-Type", "text/html; charset=utf-8").entity("Acesso não permitido.").build();
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		try {
			String jsonContent = "";
			File jsonFile = new File("src\\main\\resources\\json_sms_example.json");
			InputStream is = new FileInputStream(jsonFile);
			String UTF8 = "utf8";
			int BUFFER_SIZE = 8192;

			BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF8), BUFFER_SIZE);
			String str;

			while ((str = br.readLine()) != null) {
				jsonContent += str;
			}

			RestService restService = new RestService();
			restService.parseJson("sms", jsonContent);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
