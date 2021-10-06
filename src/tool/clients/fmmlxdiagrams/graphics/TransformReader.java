package tool.clients.fmmlxdiagrams.graphics;

import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class TransformReader {

	public static Affine getTransform(String transformString) {
		
		int i = transformString.indexOf("(");
		String type=transformString.substring(0,i);
		String value=transformString.substring(i+1,transformString.length()-1);
		String[] values = value.split(",");
		if("matrix".equals(type)) {
			return new Affine(
					Double.parseDouble(values[0]), // xx 
					Double.parseDouble(values[2]), // xy
					Double.parseDouble(values[4]), // tx
					Double.parseDouble(values[1]), // yx
					Double.parseDouble(values[3]), // yy
					Double.parseDouble(values[5])  // ty
					); 
		}
		if("scale".equals(type)) {
			if(values.length==1) {
				return new Affine(Transform.scale(Double.parseDouble(values[0]), Double.parseDouble(values[0])));
			} else {
				return new Affine(Transform.scale(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
			}
	
		}
		if("rotate".equals(type)) {
			if(values.length==1) {
				return new Affine(Transform.rotate(Double.parseDouble(values[0]),0,0));
			} else {
				return new Affine(Transform.rotate(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])));
			}
		}
		if("translate".equals(type)) {
			if (values.length==1) {
				return new Affine(Transform.translate(Double.parseDouble(values[0]), 0));
			} else {
				return new Affine(Transform.translate(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
			}
		}
		//System.err.println("transform: " + type);
		return new Affine();
		//throw new IllegalArgumentException("Could not read transform!");
	}
	
	
	
}
