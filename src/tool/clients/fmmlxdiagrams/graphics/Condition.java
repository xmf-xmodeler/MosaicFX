package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public interface Condition {

	public boolean eval(FmmlxObject object) throws SlotNotFoundException;
	public String evalText(FmmlxObject object) throws SlotNotFoundException;
//	public boolean evalString(FmmlxObject object) throws SlotNotFoundException;
	
	@SuppressWarnings("serial")
	public static class SlotNotFoundException extends RuntimeException{
		
	}
	
	public static class BooleanSlotCondition implements Condition{
		
//		private FmmlxObject object;
		private String slotName;
//		private boolean value;		
		
		public BooleanSlotCondition(String slotName) {
			super();
			this.slotName = slotName;
//			this.value = value;
		}

		@Override
		public boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
//				throw new SlotNotFoundException();
				return false;
			}
			return "true".equals(slot.getValue());
		}

		@Override
		public String evalText(FmmlxObject object) throws SlotNotFoundException {
			return "";
		}
	}
	
	public static class StringMatchSlotCondition implements Condition{
		

		private String slotName;
		private String match;

		public StringMatchSlotCondition(String slotName, String match) {
			super();
			this.slotName = slotName;
			this.match = match;
			
		}
		@Override
		public boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
				return false;
			}
			return match.equals(slot.getValue());
		}

		@Override
		public String evalText(FmmlxObject object) throws SlotNotFoundException {
			return "";
		}
	}
	
	public static class BooleanOpValCondition implements Condition{
		
		private String opName;
		private boolean value;		
		
		@Override
		public boolean eval(FmmlxObject object) throws SlotNotFoundException {
			FmmlxOperationValue opVal = object.getOperationValue(opName);
			if (opVal == null) {
				throw new SlotNotFoundException();
			}
			return value=="true".equals(opVal.getValue());
		}

		@Override
		public String evalText(FmmlxObject object) throws SlotNotFoundException {
			return "";
		}		
	}
	
	public static class ReadFromSlotCondition implements Condition{
		
		private final String slotName;	
		
		public ReadFromSlotCondition(String slotName) {
			super();
			this.slotName = slotName;
		}

		public String evalText(FmmlxObject object) throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
				return "!SLOT NOT FOUND!";
//				throw new SlotNotFoundException();
			}
			return slot.getValue();
		}
		
		@Override
		public boolean eval(FmmlxObject object) throws SlotNotFoundException {
			return true;
		}		

	}
	
public static class ReadFromOpValCondition implements Condition{
		
		private String opName;		
		
		public ReadFromOpValCondition(String opName) {
			super();
			this.opName = opName;
		}

		@Override
		public boolean eval(FmmlxObject object) throws SlotNotFoundException {
			return true;
		}
		
		public String evalText(FmmlxObject object) throws SlotNotFoundException {
			FmmlxOperationValue opVal = object.getOperationValue(opName);
			if (opVal == null) {
				return "Operation NOT FOUND!";
			}
			return opVal.getValue();
		}	
	}
	

	
}
