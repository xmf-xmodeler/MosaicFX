package tool.clients.fmmlxdiagrams.graphics;

import java.awt.geom.AffineTransform;

import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.TransformListParser;

import javafx.scene.transform.Affine;

public class TransformReader {

	public static Affine getTransform(String transformString) {
		TransformListParser p = new TransformListParser();
        AWTTransformProducer tp = new AWTTransformProducer();
        p.setTransformListHandler(tp);
        p.parse(transformString);
        AffineTransform m1 = tp.getAffineTransform();
        double[] m = new double[6];
        m1.getMatrix(m);
        
        return new Affine(m[0], m[2], m[4], m[1], m[3], m[5]);
		
		
//		
//		int i = transformString.indexOf("(");
//		String type=transformString.substring(0,i);
//		String value=transformString.substring(i+1,transformString.length()-1);
//		String[] values = value.split(",");
//		if("matrix".equals(type)) {
//			return new Affine(
//					Double.parseDouble(values[0]), // xx 
//					Double.parseDouble(values[2]), // xy
//					Double.parseDouble(values[4]), // tx
//					Double.parseDouble(values[1]), // yx
//					Double.parseDouble(values[3]), // yy
//					Double.parseDouble(values[5])  // ty
//					); 
//		}
//		if("scale".equals(type)) {
//			if(values.length==1) {
//				return new Affine(Transform.scale(Double.parseDouble(values[0]), Double.parseDouble(values[0])));
//			} else {
//				return new Affine(Transform.scale(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
//			}
//	
//		}
//		if("rotate".equals(type)) {
//			if(values.length==1) {
//				return new Affine(Transform.rotate(Double.parseDouble(values[0]),0,0));
//			} else {
//				return new Affine(Transform.rotate(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])));
//			}
//		}
//		if("translate".equals(type)) {
//			if (values.length==1) {
//				return new Affine(Transform.translate(Double.parseDouble(values[0]), 0));
//			} else {
//				return new Affine(Transform.translate(Double.parseDouble(values[0]), Double.parseDouble(values[1])));
//			}
//		}
//		//System.err.println("transform: " + type);
//		return new Affine();
//		//throw new IllegalArgumentException("Could not read transform!");
	}
	
	
	
}
