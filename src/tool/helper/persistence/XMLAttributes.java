package tool.helper.persistence;

public enum XMLAttributes {

	VERSION("version"),
	TYPE("type"),
	SOURCE_PORT("sourcePort"),
	TARGET_PORT("targetPort"),
	X_COORDINATE("xCoordinate"),
	Y_COORDINATE("yCoordinate"),
	NAME("name"),
	LOCAL_ID("localID"),
	OWNER_ID("ownerID"),
	XX("xx"),
	TX("tx"),
	TY("ty"),
	HIDDEN("hidden"),
	DISPLAYNAME("displayName"),
	PATH("path") ;
	
		
    public final String name;

    private XMLAttributes(String name) {
    	this.name = name;
    }

	public String getName() {
		return name;
	}
}
