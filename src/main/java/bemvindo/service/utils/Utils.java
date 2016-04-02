package bemvindo.service.utils;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.rythmengine.Rythm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Utils {
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	public static final String FORMAT_1 = "dd/MMM/yyyy-HH:mm";
	public static final String FORMAT_2 = "yyyy-MMM-dd-HH:mm";

	public static Date parseDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	public static String stringToDate(String dateInString, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			Date date = formatter.parse(dateInString);
			return formatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void maisn(String[] args) {
		String date = "2015-05-03T18:13:17 +03:00";
		System.out.println(stringToDate(date, DEFAULT_DATE_FORMAT));
	}

	public static String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	public static final String formatDate(final Date date, final String dateFormat) {
		DateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
		return format.format(date);
	}

	public static final Date formatDate(final String date) throws ParseException {
		return formatDate(date, DEFAULT_DATE_FORMAT);
	}

	public static final Date formatDate(final String date, final String dateFormat) throws ParseException {
		DateFormat format = new SimpleDateFormat(dateFormat);
		return format.parse(date);
	}

	public static boolean isNullOrEmpty(String text) {
		if (text == null || text.equals(" ") || text.equals("")) {
			return true;
		}
		return false;
	}

	public static String renderStringsTemplate() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("title", "Mr.");
		args.put("name", "John");
		String result = Rythm.render("Hello @title @name", args);
		return result;
	}

	public static String replace(String text, String oldValue, String newValue) {
		String result = text.replace(oldValue, newValue);
		return result;
	}

	public static Gson getGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.disableHtmlEscaping();
		builder.serializeNulls();
		builder.serializeSpecialFloatingPointValues();
		return builder.create();
	}

	public static String dateNow() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	public static String removeHTML(String str) {
		str = str.replaceAll("\\<[^>]*>", "");
		return str;
	}

	public static String randomKey() {
		return UUID.randomUUID().toString();
	}

    public static String removeAccentuation(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
    public static JsonObject getJsonObject(String jsonContent, String objectName) {
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonContent).getAsJsonObject();
		JsonObject jsonObject = (JsonObject) obj.get(objectName);
		return jsonObject;
	}
    
    public static String getJsonElement(String jsonContent, String elementName) {
    	JsonParser parser = new JsonParser();
    	JsonObject obj = parser.parse(jsonContent).getAsJsonObject();
    	JsonElement element = obj.get(elementName);
    	return element.getAsString();
    }
    
    public static void main(String[] args) {
    	String contentKey = "{".concat("\"security-key\"").concat(":").concat("\"").concat(Utils.randomKey()).concat("\"}");
    	System.out.println(getJsonElement(contentKey, "security-key"));
	}

	public static JsonArray getJsonArray(String jsonContent, String arrayName) {
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonContent).getAsJsonObject();
		JsonArray jsonArray = (JsonArray) obj.get(arrayName);
		return jsonArray;
	}


}
