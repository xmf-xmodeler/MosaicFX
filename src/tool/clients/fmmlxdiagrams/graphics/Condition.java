package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public interface Condition {

	public boolean eval(FmmlxObject object) throws SlotNotFoundException;
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
	}
	
	public static class BooleanOpValCondition implements Condition{
		
		private FmmlxObject object;
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
	

	
}
