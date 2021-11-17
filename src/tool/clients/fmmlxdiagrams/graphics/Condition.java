package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperationValue;
import tool.clients.fmmlxdiagrams.FmmlxSlot;

public interface Condition {

	public boolean eval() throws SlotNotFoundException;
	
	@SuppressWarnings("serial")
	public static class SlotNotFoundException extends Exception{
		
	}
	
	public static class BooleanSlotCondition implements Condition{
		
		private FmmlxObject object;
		private String slotName;
		private boolean value;		
		
		@Override
		public boolean eval() throws SlotNotFoundException {
			FmmlxSlot slot = object.getSlot(slotName);
			if (slot == null) {
				throw new SlotNotFoundException();
			}
			return value=="true".equals(slot.getValue());
		}		
	}
	
	public static class BooleanOpValCondition implements Condition{
		
		private FmmlxObject object;
		private String opName;
		private boolean value;		
		
		@Override
		public boolean eval() throws SlotNotFoundException {
			FmmlxOperationValue opVal = object.getOperationValue(opName);
			if (opVal == null) {
				throw new SlotNotFoundException();
			}
			return value=="true".equals(opVal.getValue());
		}		
	}
	

	
}
