package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

public class ConcreteSyntax extends AbstractSyntax{
	
	public String classPath;
	public int level;
	public Vector<Condition> conditions;

	public static ConcreteSyntax load(Object arg) {
		throw new RuntimeException("Not yet implemented!");
	}
}
