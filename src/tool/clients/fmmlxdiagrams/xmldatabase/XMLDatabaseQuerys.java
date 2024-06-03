package tool.clients.fmmlxdiagrams.xmldatabase;

import java.util.List;
import java.util.Map;

import javafx.scene.control.CheckBox;

public class XMLDatabaseQuerys {
	
	public String updateMainDocumentQuery (String db_name,String mainDocumentName, String diagramName, int newVersionNumber)
	{
		return	"let $doc := db:open('" + db_name + "','" + mainDocumentName
				+ "')" + "return insert node <Version ref=\"" + diagramName + "_version_" + newVersionNumber
				+ ".xml\"/> into $doc//VersionsContainer";
	}
	
	public String countQuery (String db_name)
	{
		return "xquery count(for $doc in db:list('" + db_name + "') "
                + "where ends-with($doc, '_versions.xml') "
                + "return $doc)";
	}
	
	public String getProjectDocumentNamesQuery(String db_name) {
		return "xquery for $doc in db:list('" + db_name + "') "
                + "where ends-with($doc, '_versions.xml') "
                + "return $doc";
	}
	
	public String initialMainDocumentContentQuery(String diagramName) {
		return "<VersionsContainer name=\"" + diagramName + "\">" + "<Version ref=\""
				+ diagramName + "_version_0.xml\"/>" + "</VersionsContainer>";
	}
	
	public String highestVersionQuery(String db_name, String mainDocumentName) {
		return 	"let $doc := doc('" + db_name + "/" + mainDocumentName + "')" + // Öffnet das spezifische Hauptdokument
				"let $versions := $doc//VersionsContainer/Version/@ref " + // Selektiert alle Version-Referenzen im spezifischen Hauptdokument
				"let $numbers := for $v in $versions "
				+ "return xs:integer(substring-before(substring-after($v, 'version_'), '.xml')) " + // Extrahiert die Versionsnummern
				"return max($numbers)";
	}
	
	public String mainDocumentExistsQuery(String db_name ,String mainDocumentName)
	{
		return "db:exists('" + db_name + "', '" + mainDocumentName + "')";
	}
	
	public String getVersionDocsQuery(String db_name)
	{
		return "for $doc in db:open('" + db_name
				+ "') where ends-with(base-uri($doc), '_versions.xml') return base-uri($doc)";
	}
	

    /**
     * Constructs an XQuery to search for documents based on the provided search term and options.
     *
     * @param db_name        The name of the database.
     * @param searchTerm     The term to search for.
     * @param checkBoxMap    The map of CheckBox labels and their corresponding CheckBoxes.
     * @param filter         The filter to apply (if any).
     * @param elementMapping A map of CheckBox labels to corresponding XML element names.
     * @return The XQuery string.
     */
    public String searchDocumentsQuery(String db_name, String searchTerm, Map<String, CheckBox> checkBoxMap, String filter, Map<String, String> elementMapping) {
        StringBuilder query = new StringBuilder("for $doc in db:open('").append(db_name).append("') ");
        
        /**
         * for $doc in db:open('database') 
			where (
    		contains($doc//Model/@name, 'Challenge') or 
    		contains($doc//addMetaClass/@package, 'Challenge') or
    		contains($doc//addMetaClass/@name, 'Challenge')
			)
			return base-uri($doc)


         */
        // Adding search criteria
        if (checkBoxMap != null && !checkBoxMap.isEmpty()) {
            query.append("where (");
            boolean firstOption = true;
            for (String key : checkBoxMap.keySet()) {
                CheckBox checkBox = checkBoxMap.get(key);
                if (checkBox.isSelected()) {
                    String xmlElement = elementMapping.get(checkBox.getText());
                    if (xmlElement != null) {
                        if (!firstOption) {
                            query.append(" or ");
                        }
                        query.append("contains($doc//").append(xmlElement).append(", '").append(searchTerm).append("')");
                        firstOption = false;
                    }
                }
            }
            query.append(") ");
        } else {
            query.append("where contains($doc, '").append(searchTerm).append("') ");
        }

        // Adding filter
        if (filter != null && !filter.isEmpty()) {
            query.append("and contains($doc, '").append(filter).append("') ");
        }

        // Return document names
        query.append("return base-uri($doc)");

        return query.toString();
    }
}
	
	

