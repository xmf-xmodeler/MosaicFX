package tool.clients.fmmlxdiagrams.fmmlxdiagram;

import tool.clients.fmmlxdiagrams.FmmlxObject;

public interface NodeCreationType {
	public static class CreateObject implements NodeCreationType {
		public final FmmlxObject metaClass;

		public CreateObject(FmmlxObject metaClass) {
			super();
			this.metaClass = metaClass;
		}		
	}

	public static NodeCreationType METACLASS = new NodeCreationType() {};
	public static NodeCreationType NOTE = new NodeCreationType() {};
}
