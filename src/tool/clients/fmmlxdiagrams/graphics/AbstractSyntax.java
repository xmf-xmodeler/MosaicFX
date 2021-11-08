package tool.clients.fmmlxdiagrams.graphics;

import java.util.Vector;

public class AbstractSyntax extends NodeGroup{
	
	public void save() {
		throw new RuntimeException("Not yet implemented!");
	}
	
	public static AbstractSyntax load(Object arg) {
		if(0==1) {
			return ConcreteSyntax.load(arg);
		}
		throw new RuntimeException("Not yet implemented!");
	}
}
