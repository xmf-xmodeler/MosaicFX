package tool.clients.fmmlxdiagrams.graphics;

import org.w3c.dom.Element;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public abstract class Condition<ReturnType>{

	public abstract ReturnType eval(FmmlxObject object) throws SlotNotFoundException;
	public boolean evalBool(FmmlxObject object) throws SlotNotFoundException {
		return Boolean.TRUE.equals(eval(object));
	}
	public String evalString(FmmlxObject object) throws SlotNotFoundException {
		return eval(object).toString();
	}
	public abstract void save(Element conditionElement);
	
	
	@SuppressWarnings("serial")
	public static class SlotNotFoundException extends RuntimeException{}
	
	public static class BooleanSlotCondition extends Condition<Boolean>{
		private String slotName;
		
		public BooleanSlotCondition(String slotName) {
			super();
			this.slotName = slotName;
		}
		
		@Override
		public Boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
				return false;
			}
			return "true".equals(slot.getValue());
		}

		@Override
		public void save(Element conditionElement) {
			 conditionElement.setAttribute("type", "BooleanSlotCondition");
			 conditionElement.setAttribute("slotName", slotName);		
		}
	}
	
	public static class StringMatchSlotCondition extends Condition<Boolean>{
		

		private String slotName;
		private String match;

		public StringMatchSlotCondition(String slotName, String match) {
			super();
			this.slotName = slotName;
			this.match = match;			
		}
		
		@Override
		public Boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
				return false;
			}
			return match.equals(slot.getValue());
		}
		
		@Override
		public void save(Element conditionElement) {
			conditionElement.setAttribute("type", "StringMatchSlotCondition");
			conditionElement.setAttribute("slotName", slotName);
			conditionElement.setAttribute("match", match);			
		}
	}

	public static class SlotNumCompareCondition extends Condition<Boolean> {
		private String slotName;
		private Double low;
		private Double high;
		
		public SlotNumCompareCondition(String slotName, Double low, Double high) {
			super();
			this.slotName = slotName;
			this.low = low;
			this.high = high;
		}

		@Override
		public Boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if(slot == null) {
				return false;
			}
			try{
				double value = Double.parseDouble(slot.getValue());
				return this.low < value && value < this.high;
			} catch(Exception e) {}
			return false;
		}

		@Override
		public void save(Element conditionElement) {
			 conditionElement.setAttribute("type", "BooleanSlotCondition");
			 conditionElement.setAttribute("slotName", slotName);		
		}
	}
	
	public static class BooleanOpValCondition extends Condition<Boolean>{
		
		private String opName;
		private boolean value;		
		
		@Override
		public Boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxOperationValue opVal = object.getOperationValue(opName);
			if (opVal == null) {
				throw new SlotNotFoundException();
			}
			return value=="true".equals(opVal.getValue());
		}

		@Override
		public void save(Element conditionElement) {
			conditionElement.setAttribute("type", "BooleanOpValCondition");
			conditionElement.setAttribute("opName", opName);			
		}		
	}
	
	public static class StringMatchOpValCondition extends Condition<Boolean>{
		private String opName;
		private String match;

		public StringMatchOpValCondition(String opName, String match) {
			super();
			this.opName = opName;
			this.match = match;
			
		}
		@Override
		public Boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxOperationValue val = object.getOperationValue(opName);
			if (val == null) {
				return false;
			}
			return match.equals(val.getValue());
		}
		
		@Override
		public void save(Element conditionElement) {
			conditionElement.setAttribute("type", "StringMatchOpValCondition");
			conditionElement.setAttribute("opName", opName);
			conditionElement.setAttribute("match", match );			
		}
	}
	
	public static class ReadFromSlotCondition extends Condition<String>{
		
		private final String slotName;	
		
		public ReadFromSlotCondition(String slotName) {
			super();
			this.slotName = slotName;
		}

		public String eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
				return "!SLOT NOT FOUND!";
			}
			return slot.getValue();
		}
		
		@Override
		public void save(Element conditionElement) {
			conditionElement.setAttribute("type", "ReadFromSlot");
			conditionElement.setAttribute("slotName", slotName);
		}	
	}
	
public static class ReadFromOpValCondition extends Condition<String>{
		
		private String opName;		
		
		public ReadFromOpValCondition(String opName) {
			super();
			this.opName = opName;
		}
		
		public String eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxOperationValue opVal = object.getOperationValue(opName);
			if (opVal == null) {
				return "Operation NOT FOUND!";
			}
			return opVal.getValue();
		}

		@Override
		public void save(Element conditionElement) {
			conditionElement.setAttribute("type", "ReadFromOpValCondition");
			conditionElement.setAttribute("opName", opName);			
		}	
	}

	
public static class ReadClassName extends Condition<String>{

    private String className;

    public ReadClassName(String className) {
        super();
        this.className=className;
    }

    @Override
    public String eval(FmmlxObject object) throws SlotNotFoundException {
        String name = object.getName();
        if (name == null) {
            return "!NAME NOT FOUND!";
//           throw new SlotNotFoundException();
        }
        return name;
    }

    @Override
    public void save(Element conditionElement) {
        conditionElement.setAttribute("type", "ReadClassName");
        conditionElement.setAttribute("name", className);
    }


}

	
}
