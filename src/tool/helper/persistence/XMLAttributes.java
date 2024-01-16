package tool.helper.persistence;

/**
 * Enum that defines all attributes used in the xml representation of an package  
 */
public enum XMLAttributes {

	//for better human readability the text of this tag should also be named export version. Because the distribution of models long time used 'version' it was decided to go on using this to not have problems with backwards compatibility
	EXPORT_VERSION("version"),
	XMODELER_VERSION("xModelerVersion"),
	
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
