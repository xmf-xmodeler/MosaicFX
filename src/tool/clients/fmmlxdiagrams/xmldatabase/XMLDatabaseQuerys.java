	package tool.clients.fmmlxdiagrams.xmldatabase;

import java.util.List;
import java.util.Map;

/**
 * @author Nicolas Engel
 */

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
     * @param db_name
     * @param path
     * @param searchTerm
     * @return
     */
    public static String searchDocumentsQuery(String db_name, String path, String searchTerm, String filter) {
        String query;
        if (filter == null || filter.isEmpty()) {
        	query = String.format(	"for $doc in db:open('%s')  where "
					+ "exists($doc//%s,'%s')])"
					+ " return base-uri($doc) ", db_name,path,searchTerm
					);
        }
        else
        {
        	filter = filter.trim();
        	query = String.format(
        	        "let $filterDoc := doc('%s/%s') " +
        	                "for $version in $filterDoc//Version " +
        	                "let $doc := doc('%s/' || $version/@ref) " +
        	                "where exists($doc//%s, '%s')]) " +
        	                "return base-uri($doc)",
        	                db_name, filter, db_name, path, searchTerm
        	                );
        	System.err.print(query);
        }
    	return query;
    }
}
	














	

