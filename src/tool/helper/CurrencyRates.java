package tool.helper;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CurrencyRates {
	private static boolean success = false;
	private static boolean trying = false;
	private static HashMap<String, Float> rates = new HashMap<>();
	
	public static void init() {
		new Thread(new Runnable() {
			@Override public void run() {
				trying = true;
				try {
					String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(new URL(url).openStream());
					Node root = doc.getDocumentElement();
					for(int i = 0; i < root.getChildNodes().getLength(); i++) {
						Node cube1 = root.getChildNodes().item(i);

						if(cube1.getNodeName().equals("Cube")) {
							for(int i2 = 0; i2 < cube1.getChildNodes().getLength(); i2++) {
								Node cube2 = cube1.getChildNodes().item(i2);
								if("Cube".equals(cube2.getNodeName())) {
									for (int j = 0; j < cube2.getChildNodes().getLength(); j++) {
										Node curr = cube2.getChildNodes().item(j);
					    				if("Cube".equals(curr.getNodeName())) {
					    					rates.put(
				    							curr.getAttributes().getNamedItem("currency").getNodeValue(), 
				    							Float.parseFloat(curr.getAttributes().getNamedItem("rate").getNodeValue()));
					    				}
									}
								}
							}
						}
					}
					success = true;
				} catch (Exception e) {
					System.err.println("Unable to get exchange rates: " + e.getMessage());
				}				
				trying = false;
			}
		}).start();
	}
	
	public static float getPriceOf1EUR(String otherCurrency) {
		if(trying) return 1.f; else
		if(success) {
			try{
				return rates.get(otherCurrency);
			} catch (Exception e) {
				return 1.f;
			}
		} else
		init();
		return 1.f;
	}
}
