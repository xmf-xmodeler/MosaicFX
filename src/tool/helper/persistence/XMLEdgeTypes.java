package tool.helper.persistence;

public enum XMLEdgeTypes {

	INHERITANCE("Inheritance"),
	DELEGATION("Delegation"),
	ROLEFILLER("RoleFiller"),
	ASSOCIATIONLINK("AssociationLink");

	public final String name;

	private XMLEdgeTypes(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static boolean contains(String type) {
		for (XMLEdgeTypes xmlTyp : XMLEdgeTypes.values()) {
			if (type.equals(xmlTyp.getName())) {
				return true;
			}
		}
		return false;
	}
}
