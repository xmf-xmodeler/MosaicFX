package tool.clients.fmmlxdiagrams.graphics;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public interface Condition {

	public boolean eval() throws SlotNotFoundException;
	
	@SuppressWarnings("serial")
	public static class SlotNotFoundException extends Exception{
		
	}
	
	public static class BooleanCondition implements Condition{
		
		private FmmlxObject object;
		private String slotName;
		private boolean value;
		
		
		
		@Override
		public boolean eval() throws SlotNotFoundException {
			if (object.attributeExists(slotName)) {
				throw new SlotNotFoundException();
			}
			return value=="true".equals(object.getSlot(slotName).getValue());
		}
		
	}
	

	
}
