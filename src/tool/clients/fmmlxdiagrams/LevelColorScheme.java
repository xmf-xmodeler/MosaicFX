package tool.clients.fmmlxdiagrams;

import java.util.Arrays;
import java.util.Vector;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public abstract class LevelColorScheme {
	
	public abstract Color getLevelFgColor(int level, double opacity);
	public abstract Paint getLevelBgColor(int level); 
	
	public static final class DefaultLevelColorScheme extends LevelColorScheme {
		
		@Override
		public Color getLevelFgColor(int level, double opacity) {
			int opacInt = (int)(255*opacity+.5);
			String opacHex = Integer.toHexString(opacInt);
			if(opacHex.length() == 1) opacHex = "0" + opacHex;
			return Color.valueOf((new Vector<>(Arrays.asList(2, 3, 4, 5)).contains(level) ? "#ffffff" : "000000")+opacHex);
		}

		@Override
		public Paint getLevelBgColor(int level) {
			if(level == 0) return Color.valueOf("#8C8C8C");
			if(level == 1) return Color.valueOf("#eeeeee");
			if(level == 2) return Color.valueOf("#000000");
			if(level == 3) return Color.valueOf("#3111DB");
			if(level == 4) return Color.valueOf("#BB1133");
			if(level == 5) return Color.valueOf("#119955");
			if(level == 6) return new LinearGradient(0, 0, 20, 10, false, CycleMethod.REPEAT,
					new Stop(.24, Color.valueOf("#22cc55")),
					new Stop(.26, Color.valueOf("#ffdd00")),
					new Stop(.74, Color.valueOf("#ffdd00")),
					new Stop(.76, Color.valueOf("#22cc55")));
			if(level == 7) return new LinearGradient(0, 0, 60, 25, false, CycleMethod.REPEAT,
					new Stop(0. / 6, Color.valueOf("#ff4444")),
					new Stop(0.8 / 6, Color.valueOf("#ffff00")),
					new Stop(1.2 / 6, Color.valueOf("#ffff00")),
					new Stop(2. / 6, Color.valueOf("#44ff44")),
					new Stop(2.8 / 6, Color.valueOf("#00ffff")),
					new Stop(3.2 / 6, Color.valueOf("#00ffff")),
					new Stop(4. / 6, Color.valueOf("#6666ff")),
					new Stop(4.8 / 6, Color.valueOf("#ff22ff")),
					new Stop(5.2 / 6, Color.valueOf("#ff22ff")),
					new Stop(6. / 6, Color.valueOf("#ff4444")));
			return Color.valueOf("#ffaa00");
		}
	}
	
	public static final class GrayLevelColorScheme extends LevelColorScheme {
		private final int min;
		private final int max;
		
		public GrayLevelColorScheme(Vector<FmmlxObject> objects) {
			int MIN = Integer.MAX_VALUE;
			int MAX = Integer.MIN_VALUE;
			for(FmmlxObject o : objects) {
				if(o.level < MIN) MIN = o.level;
				if(o.level > MAX) MAX = o.level;
			}
			if(MAX-MIN < 0) throw new IllegalArgumentException();
			if(MAX-MIN == 0) {MAX++; MIN--;}
			min = MIN;
			max = MAX;
		}

		@Override
		public Color getLevelFgColor(int level, double opacity) {
			if(level > max) level = max;
			if(level < min) level = min;
			double b = (1-(1. * (level-min)) / (max-min) > 1./2) ? 0 : 1;
			return new Color(b, b, b, 1);
		}

		@Override
		public Paint getLevelBgColor(int level) {
			if(level > max) level = max;
			if(level < min) level = min;
			double b = 1-(1. * (level-min)) / (max-min);
			return new Color(b, b, b, 1);
		}
	}
	
	public static final class RedLevelColorScheme extends LevelColorScheme {
		private final int min;
		private final int max;
		
		public RedLevelColorScheme(Vector<FmmlxObject> objects) {
			int MIN = Integer.MAX_VALUE;
			int MAX = Integer.MIN_VALUE;
			for(FmmlxObject o : objects) {
				if(o.level < MIN) MIN = o.level;
				if(o.level > MAX) MAX = o.level;
			}
			if(MAX-MIN < 0) throw new IllegalArgumentException();
			if(MAX-MIN == 0) {MAX++; MIN--;}
			min = MIN;
			max = MAX;
		}

		@Override
		public Color getLevelFgColor(int level, double opacity) {
			if(level > max) level = max;
			if(level < min) level = min;
			double b = (1-(1. * (level-min)) / (max-min) > 1./2) ? 0 : 1;
			return new Color(b, b, b, 1);
		}

		@Override
		public Paint getLevelBgColor(int level) {
			if(level > max) level = max;
			if(level < min) level = min;
			double b = 1-(1. * (level-min)) / (max-min);
		    // high: 0 --- low 1;
			return Color.hsb(
					-40 + 120 * b, 
					Math.min(1,4*(1-b)),
					Math.min(0.95,b*2+.3));
//			return new Color(b, b, b, 1);
		}
	
	}


}
