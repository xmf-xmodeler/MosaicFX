package tool.clients.fmmlxdiagrams.graphics;

import java.util.Arrays;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Modification {
	
	private Condition condition;
	private Consequence consequence;
	private String affectedId;
	
	public static enum Consequence{
		SHOW_ALWAYS, SHOW_NEVER,
		SHOW_IF, SHOW_IF_NOT,
		READ_FROM_SLOT}		
	
	public Modification(Element modElement) {
		Element conditionElement = null;
		Element consequenceElement = null;
		Element affectedIdElement = null;
		
		NodeList nl = modElement.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if("Condition".equals(n.getNodeName())) {
				if(conditionElement != null) throw new IllegalArgumentException("<Modification> element ambiguous, requires oexactly one <Condition>.");
				conditionElement = (Element) n;
			}	
			if("Consequence".equals(n.getNodeName())) {
				if(consequenceElement != null) throw new IllegalArgumentException("<Modification> element ambiguous, requires oexactly one <Consequence>.");
				consequenceElement = (Element) n;
			}	
			if("Affected".equals(n.getNodeName())) {
				if(affectedIdElement != null) throw new IllegalArgumentException("<Modification> element ambiguous, requires oexactly one <Affected>.");
				affectedIdElement = (Element) n;
			}	
		}
		
		if(conditionElement == null || consequenceElement == null || affectedIdElement == null) {
			throw new IllegalArgumentException("<Modification> element incomplete, requires one <Condition>, <Consequence> and <Affected> each.");
		}
		
		try{
			consequence = Consequence.valueOf(consequenceElement.getAttribute("type"));
		} catch (Exception e) {
			throw new IllegalArgumentException("<Consequence type=...> not recognized. Try " + Arrays.toString(Consequence.values()));
		}
		
		try{
			String conditionType = conditionElement.getAttribute("type");
			if("ReadFromSlot".equals(conditionType)) {
				String slotName =  conditionElement.getAttribute("slotName");
				condition = new Condition.ReadFromSlotCondition(slotName);
			} else {
				throw new RuntimeException("not yet implemented");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("<Condition...> not readable", e);
			//throw new IllegalArgumentException("<Consequence type=...> not recognized. Try " + Arrays.toString(Consequence.values()));
		}
	}
	
	public String getID() {return affectedId;} 
	public Consequence getConsequence() {return consequence;} 
	public Condition getCondition() {return condition;} 
		
	
}
