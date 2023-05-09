package tool.clients.fmmlxdiagrams;

import javafx.scene.paint.Color;

public abstract class LevelColorScheme {
	
	public abstract Color getLevelFgColor(int level, double opacity);
	public abstract Color getLevelBgColor(int level); 
	
	public String getLevelFgColorHex(int level, double opacity) {
		Color color = getLevelFgColor(level, opacity);
		return toHexString(color);
	}
	
	public String getLevelBgColorHex(int level) {
		Color color = getLevelBgColor(level);
		return toHexString(color);
	}

//	
//	public static final class DefaultLevelColorScheme extends LevelColorScheme {
//		
//		@Override
//		public Color getLevelFgColor(int level, double opacity) {
//			int opacInt = (int)(255*opacity+.5);
//			String opacHex = Integer.toHexString(opacInt);
//			if(opacHex.length() == 1) opacHex = "0" + opacHex;
//			return Color.valueOf((new Vector<>(Arrays.asList(2, 3, 4, 5)).contains(level) ? "#ffffff" : "000000")+opacHex);
//		}
//
//		@Override
//		public Color getLevelBgColor(int level) {
//			if(level == 0) return Color.valueOf("#8C8C8C");
//			if(level == 1) return Color.valueOf("#eeeeee");
//			if(level == 2) return Color.valueOf("#000000");
//			if(level == 3) return Color.valueOf("#3111DB");
//			if(level == 4) return Color.valueOf("#BB1133");
//			if(level == 5) return Color.valueOf("#119955");
////			if(level == 6) return new LinearGradient(0, 0, 20, 10, false, CycleMethod.REPEAT,
////					new Stop(.24, Color.valueOf("#22cc55")),
////					new Stop(.26, Color.valueOf("#ffdd00")),
////					new Stop(.74, Color.valueOf("#ffdd00")),
////					new Stop(.76, Color.valueOf("#22cc55")));
////			if(level == 7) return new LinearGradient(0, 0, 60, 25, false, CycleMethod.REPEAT,
////					new Stop(0. / 6, Color.valueOf("#ff4444")),
////					new Stop(0.8 / 6, Color.valueOf("#ffff00")),
////					new Stop(1.2 / 6, Color.valueOf("#ffff00")),
////					new Stop(2. / 6, Color.valueOf("#44ff44")),
////					new Stop(2.8 / 6, Color.valueOf("#00ffff")),
////					new Stop(3.2 / 6, Color.valueOf("#00ffff")),
////					new Stop(4. / 6, Color.valueOf("#6666ff")),
////					new Stop(4.8 / 6, Color.valueOf("#ff22ff")),
////					new Stop(5.2 / 6, Color.valueOf("#ff22ff")),
////					new Stop(6. / 6, Color.valueOf("#ff4444")));
//			return Color.valueOf("#ffaa00");
//		}
//	}
//	
//	public static final class GrayLevelColorScheme extends LevelColorScheme {
//		private final int min;
//		private final int max;
//		
//		public GrayLevelColorScheme(Vector<FmmlxObject> objects) {
//			int MIN = Integer.MAX_VALUE;
//			int MAX = Integer.MIN_VALUE;
//			for(FmmlxObject o : objects) {
//				if(o.level < MIN) MIN = o.level;
//				if(o.level > MAX) MAX = o.level;
//			}
//			if(MAX-MIN < 0) throw new IllegalArgumentException();
//			if(MAX-MIN == 0) {MAX++; MIN--;}
//			min = MIN;
//			max = MAX;
//		}
//
//		@Override
//		public Color getLevelFgColor(int level, double opacity) {
//			if(level > max) level = max;
//			if(level < min) level = min;
//			double b = (1-(1. * (level-min)) / (max-min) > 1./2) ? 0 : 1;
//			return new Color(b, b, b, 1);
//		}
//
//		@Override
//		public Color getLevelBgColor(int level) {
//			if(level > max) level = max;
//			if(level < min) level = min;
//			double b = 1-(1. * (level-min)) / (max-min);
//			return new Color(b, b, b, 1);
//		}
//	}

	public static final int LEVEL_CONTINGENT_CLASS = -1;
	public static final int LEVEL_AGNOSTIC_CLASS = -2;
	public static final int ENUM = -3;
	public static final int OBJECT_HAS_ISSUES = -4;
	
	public static final class FixedBlueLevelColorScheme extends LevelColorScheme {

		private final Color A = new Color(   .95,    .95,    .95, 1.);
		private final Color B = new Color(10./15, 13./15,     1., 1.);
		private final Color C = new Color( 6./15,  9./15,     1., 1.);
		private final Color D = new Color( 4./15,  5./15, 13./15, 1.);
		private final Color E = new Color( 5./15,  2./15, 10./15, 1.);
		private final Color F = new Color( 4./15,     0.,  3./15, 1.);
		private final Color G = new Color( 7./15,  7./15,  7./15, 1.);
		private final Color Z = new Color(    1., 10./15, 13./15, 1.);
		
		@Override
		public Color getLevelFgColor(int level, double opacity) {
			Color BG = getLevelBgColor(level);
			switch (level) {
				case 0: case 1: case 2: case LEVEL_AGNOSTIC_CLASS: return mix(Color.BLACK, BG, opacity);
				case 3: case 4: case 5: case 6: return mix(Color.WHITE, BG, opacity);
				case 7: return mix(B, BG, opacity);
				case 8: return mix(C, BG, opacity);
				case 9: return mix(D, BG, opacity);
				case OBJECT_HAS_ISSUES: return mix(Color.DARKRED, BG, opacity);
				case LEVEL_CONTINGENT_CLASS: return mix(Color.DARKRED, BG, opacity);
				case ENUM: return mix(Color.DARKGREEN, BG, opacity);
				default: return mix(G, BG, opacity);
			}
		}
		
		private Color mix(Color FG, Color BG, double opacity) {
			double r = FG.getRed()*opacity + BG.getRed()*(1-opacity);
			double g = FG.getGreen()*opacity + BG.getGreen()*(1-opacity);
			double b = FG.getBlue()*opacity + BG.getBlue()*(1-opacity);
			return  Color.color(r, g, b);
		}

		@Override
		public Color getLevelBgColor(int level) {
			switch (level) {
				case 0: case LEVEL_AGNOSTIC_CLASS: return A;
				case 1: return B;
				case 2: return C;
				case 3: return D;
				case 4: return E;
				case 5: return F;
				case LEVEL_CONTINGENT_CLASS: return Color.LIGHTYELLOW;
				case ENUM: return Color.LIGHTGREEN.brighter().brighter();
				case OBJECT_HAS_ISSUES: return Z;
				default: return Color.BLACK;
			}
		}
		
	}
//	
//	public static final class RedLevelColorScheme extends LevelColorScheme {
//		private final int min;
//		private final int max;
//		
//		public RedLevelColorScheme(Vector<FmmlxObject> objects) {
//			int MIN = Integer.MAX_VALUE;
//			int MAX = Integer.MIN_VALUE;
//			for(FmmlxObject o : objects) {
//				int level = o.level;
//				if(level == -1) level = 1;
//				if(level < MIN) MIN = level;
//				if(level > MAX) MAX = level;
//			}
//			if(MAX-MIN < 0) throw new IllegalArgumentException();
//			if(MAX-MIN == 0) {MAX++; MIN--;}
//			min = MIN;
//			max = MAX;
//		}
//
//		@Override
//		public Color getLevelFgColor(int level, double opacity) {
//			if(level == -1) level = 1;
//			if(level > max) level = max;
//			if(level < min) level = min;
//			double b = (1-(1. * (level-min)) / (max-min) > 1./2) ? 0 : 1;
//			return new Color(b, b, b, 1);
//		}
//
//		@Override
//		public Color getLevelBgColor(int level) {
//			if(level == -1) level = 1;
//			if(level > max) level = max;
//			if(level < min) level = min;
//			double b = 1-(1. * (level-min)) / (max-min);
//		    // high: 0 --- low 1;
//			return Color.hsb(
//					-40 + 120 * b, 
//					Math.min(1,4*(1-b)),
//					Math.min(0.95,b*2+.3));
////			return new Color(b, b, b, 1);
//		}
//	
//	}
	
	private static String format(double val) {
	    String in = Integer.toHexString((int) Math.round(val * 255));
	    return in.length() == 1 ? "0" + in : in;
	}

	public static String toHexString(Color value) {
	    return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
	            .toUpperCase();
	}
}