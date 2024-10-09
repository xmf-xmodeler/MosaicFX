package tool.clients.fmmlxdiagrams.fmmlxdiagram;

import tool.clients.fmmlxdiagrams.AssociationType;

public interface EdgeCreationType {
	
	public static class CreateAssociation implements EdgeCreationType {
		public final AssociationType assocType;

		public CreateAssociation(AssociationType assocType) {
			super();
			this.assocType = assocType;
		}		
	}
	
	public static EdgeCreationType LINK = new EdgeCreationType() {};
	public static EdgeCreationType DELEGATION = new EdgeCreationType() {};
	public static EdgeCreationType ROLEFILLER = new EdgeCreationType() {};
}
