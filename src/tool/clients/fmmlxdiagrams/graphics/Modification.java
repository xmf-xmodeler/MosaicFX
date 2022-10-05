package tool.clients.fmmlxdiagrams.graphics;

import java.util.Arrays;

import javax.management.RuntimeErrorException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Modification {
	
	private Condition condition;
	private Consequence consequence;
	private String affectedId;
	private String affectedParentId;
	
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
				if(conditionElement != null) throw new IllegalArgumentException("<Modification> element ambiguous, requires exactly one <Condition>.");
				conditionElement = (Element) n;
			}	
			if("Consequence".equals(n.getNodeName())) {
				if(consequenceElement != null) throw new IllegalArgumentException("<Modification> element ambiguous, requires exactly one <Consequence>.");
				consequenceElement = (Element) n;
			}	
			if("Affected".equals(n.getNodeName())) {
				if(affectedIdElement != null) throw new IllegalArgumentException("<Modification> element ambiguous, requires exactly one <Affected>.");
				affectedIdElement = (Element) n;
			}	
		}
		
		if(conditionElement == null || consequenceElement == null || affectedIdElement == null) {
			throw new IllegalArgumentException("<Modification> element incomplete, requires one <Condition>, <Consequence> and <Affected> each.");
		}
		
		try {
			consequence = Consequence.valueOf(consequenceElement.getAttribute("type"));
		} catch (Exception e) {
			throw new IllegalArgumentException("<Consequence type=...> not recognized. Try " + Arrays.toString(Consequence.values()));
		}
		
		try {
			String conditionType = conditionElement.getAttribute("type");
			if("ReadFromSlot".equals(conditionType)) {
				String slotName =  conditionElement.getAttribute("slotName");
				condition = new Condition.ReadFromSlotCondition(slotName);
			} else if("BooleanSlotCondition".equals(conditionType)) {
				String slotName =  conditionElement.getAttribute("slotName");
				condition = new Condition.BooleanSlotCondition(slotName);
			} else if("ReadFromOpValCondition".equals(conditionType)) {
				String opName = conditionElement.getAttribute("opName");
				condition = new Condition.ReadFromOpValCondition(opName);
			} else if("StringMatchSlotCondition".equals(conditionType)) {
				String slotName =  conditionElement.getAttribute("slotName");
				String match = conditionElement.getAttribute("match");
				condition = new Condition.StringMatchSlotCondition(slotName,match);
			} else {
				throw new RuntimeException("not yet implemented");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("<Condition...> not readable (" + e.getMessage() + ")", e);
			//throw new IllegalArgumentException("<Consequence type=...> not recognized. Try " + Arrays.toString(Consequence.values()));
		}
		
		try {
			affectedParentId = affectedIdElement.getAttribute("id");
			affectedId = affectedIdElement.getAttribute("localId");
			if(affectedParentId==null) throw new RuntimeException("id must be not null");
			if(affectedId==null) throw new RuntimeException("localId must be not null");
		} catch (Exception e) {
			throw new IllegalArgumentException("<Affected...>  IDs not readable (" + e.getMessage() + ")", e);
		}
		
	}

	public String getID() {return affectedId;} 
	public String getParentID() {return affectedParentId;} 
	public Consequence getConsequence() {return consequence;} 
	public Condition getCondition() {return condition;}

	
	public Node save(Document document) { Element modificationElement = document.createElement("Modification");
		 
	 
	 Element conditionElement = document.createElement("Condition");
	 modificationElement.appendChild(conditionElement);
	 condition.save(conditionElement);
	 
	 Element affectedElement = document.createElement("Affected");
	 modificationElement.appendChild(affectedElement);
	 affectedElement.setAttribute("id", affectedParentId);
	 affectedElement.setAttribute("localId", affectedId);
	 
	 
	 Element consequenceElement = document.createElement("Consequence");
	 modificationElement.appendChild(consequenceElement);
	 consequenceElement.setAttribute("type", consequence.toString());
	 return modificationElement;
	} 
		
	
}
