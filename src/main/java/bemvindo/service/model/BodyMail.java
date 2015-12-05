package bemvindo.service.model;

public class BodyMail {
	public String template;
	public String title1;
	public String subTitle1;
	public String title2;
	public String paragraph1;
	public String paragraph2;
	public String paragraph3;
	public String paragraph4;
	public String paragraph5;
	public String title3;
	public String paragraph6;
	public String paragraph7;
	public String paragraph8;
	public String paragraph9;
	public String paragraph10;
	public String footer1;
	public String footer2;
	public String footer3;
	public String image1;
	public String image2;
	public String image3;
	public String image4;
	public String attachment;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"template\":\"" + template + "\",");
		sb.append("\"title1\":\"" + title1 + "\",");
		sb.append("\"subTitle1\":\"" + subTitle1 + "\",");
		sb.append("\"title2\":\"" + title2 + "\",");
		sb.append("\"paragraph1\":\"" + paragraph1 + "\",");
		sb.append("\"paragraph2\":\"" + paragraph2 + "\",");
		sb.append("\"paragraph3\":\"" + paragraph3 + "\",");
		sb.append("\"paragraph4\":\"" + paragraph4 + "\",");
		sb.append("\"paragraph5\":\"" + paragraph5 + "\",");
		sb.append("\"title3\":\"" + title3 + "\",");
		sb.append("\"paragraph6\":\"" + paragraph6 + "\",");
		sb.append("\"paragraph7\":\"" + paragraph7 + "\",");
		sb.append("\"paragraph8\":\"" + paragraph8 + "\",");
		sb.append("\"paragraph9\":\"" + paragraph9 + "\",");
		sb.append("\"paragraph10\":\"" + paragraph10 + "\",");
		sb.append("\"footer1\":\"" + footer1 + "\",");
		sb.append("\"footer2\":\"" + footer2 + "\",");
		sb.append("\"footer3\":\"" + footer3 + "\",");
		sb.append("\"image1\":\"" + image1 + "\",");
		sb.append("\"image2\":\"" + image2 + "\",");
		sb.append("\"image3\":\"" + image3 + "\",");
		sb.append("\"image4\":\"" + image4 + "\",");
		sb.append("\"attachment\":\"" + attachment + "\"");
		sb.append("}");

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new BodyMail().toString());
	}
}
