package tool.helper.persistence;

public enum XMLAttributes {

	VERSION("version"),
	REF("ref"),
	TYPE("type"),
	SOURCE_PORT("sourcePort"),
	TARGET_PORT("targetPort"),
	X_COORDINATE("xCoordinate"),
	Y_COORDINATE("yCoordinate"),
	NAME("name"),
	//TODO
	ATTRIBUTE_PARENT_ASSOCIATION("whatsWithThat"),
	LOCAL_ID("localID"),
	OWNER_ID("ownerID"),
	XX("xx"),
	TX("tx"),
	TY("ty"),
	HIDDEN("hidden"),
	PATH("path") ;
		
    public final String name;

    private XMLAttributes(String name) {
	this.name = name;
    }

	public String getName() {
		return name;
	}
}