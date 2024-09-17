package tool.clients.fmmlxdiagrams.graphics;

import java.util.NoSuchElementException;

/**
 *This class the the java equivalent to the XMF-class Clients::FmmlxDiagrams::MappingInfo 
 */
public class GraphicalMappingInfo {
	
	String mappingKey;
	double xPosition;
	double yPosition;
	boolean hidden;
	
	public GraphicalMappingInfo(String mappingKey, double xPosition, double yPosition, boolean hidden) {
		super();
		this.mappingKey = mappingKey;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.hidden = hidden;
	}

	public String getMappingKey() {
		return mappingKey;
	}

	public double getxPosition() {
		return xPosition;
	}

	public double getyPosition() {
		return yPosition;
	}

	public boolean isHidden() {
		return hidden;
	}
	
	public int getNoteIdFromMappingKey() {
		if (!mappingKey.contains("NoteMapping")) {
			throw new NoSuchElementException("You asked for a note-mapping. " + getMappingKey() + " does not match the Format of a note-mapping"   );
		}
		String idString = mappingKey.split("NoteMapping")[1]; 
		return Integer.valueOf(idString);
	}

	@Override
	public String toString() {
		return "GraphicalMappingInfo [mappingKey=" + mappingKey + ", xPosition=" + xPosition + ", yPosition="
				+ yPosition + ", hidden=" + hidden + "]";
	}
}