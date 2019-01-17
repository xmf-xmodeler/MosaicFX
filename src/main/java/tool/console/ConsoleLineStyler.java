package tool.console;

/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/

public class ConsoleLineStyler  {
//	JavaScanner scanner = new JavaScanner();
//
//	int[] tokenColors;
//
//	Color[] colors;
//
//	Vector blockComments = new Vector();
//
//	public static final int EOF = -1;
//
//	public static final int EOL = 10;
//
//	public static final int WORD = 0;
//
//	public static final int WHITE = 1;
//
//	public static final int KEY = 2;
//
//	public static final int COMMENT = 3;
//
//	public static final int STRING = 5;
//
//	public static final int OTHER = 6;
//
//	public static final int NUMBER = 7;
//
//	public static final int MAXIMUM_TOKEN = 8;
//
//	public ConsoleLineStyler() {
//		initializeColors();
//		scanner = new JavaScanner();
//	}
//
//	Color getColor(int type) {
//		if (type < 0 || type >= tokenColors.length) {
//			return null;
//		}
//		return colors[tokenColors[type]];
//	}
//
//	boolean inBlockComment(int start, int end) {
//		for (int i = 0; i < blockComments.size(); i++) {
//			int[] offsets = (int[]) blockComments.elementAt(i);
//			// start of comment in the line
//			if ((offsets[0] >= start) && (offsets[0] <= end))
//				return true;
//			// end of comment in the line
//			if ((offsets[1] >= start) && (offsets[1] <= end))
//				return true;
//			if ((offsets[0] <= start) && (offsets[1] >= end))
//				return true;
//		}
//		return false;
//	}
//
//	void initializeColors() {
//		Display display = Display.getDefault();
//		colors = new Color[] { new Color(display, new RGB(0, 0, 0)), // black
//				new Color(display, new RGB(255, 0, 0)), // red
//				new Color(display, new RGB(0, 55, 0)), // green
//				new Color(display, new RGB(0, 0, 255)) // blue
//		};
//		tokenColors = new int[MAXIMUM_TOKEN];
//		tokenColors[WORD] = 0;
//		tokenColors[WHITE] = 0;
//		tokenColors[KEY] = 3;
//		tokenColors[COMMENT] = 1;
//		tokenColors[STRING] = 2;
//		tokenColors[OTHER] = 0;
//		tokenColors[NUMBER] = 0;
//	}
//
//	void disposeColors() {
//		for (int i = 0; i < colors.length; i++) {
//			colors[i].dispose();
//		}
//	}
//
//	/**
//	 * Event.detail line start offset (input) Event.text line text (input)
//	 * LineStyleEvent.styles Enumeration of StyleRanges, need to be in order.
//	 * (output) LineStyleEvent.background line background color (output)
//	 */
//	public void lineGetStyle(LineStyleEvent event) {
//		Vector styles = new Vector();
//		int token;
//		StyleRange lastStyle;
//		// If the line is part of a block comment, create one style for the
//		// entire line.
//		if (inBlockComment(event.lineOffset,
//				event.lineOffset + event.lineText.length())) {
//			styles.addElement(new StyleRange(event.lineOffset, event.lineText
//					.length(), getColor(COMMENT), null));
//			event.styles = new StyleRange[styles.size()];
//			styles.copyInto(event.styles);
//			return;
//		}
//		Color defaultFgColor = ((Control) event.widget).getForeground();
//		scanner.setRange(event.lineText);
//		token = scanner.nextToken();
//		while (token != EOF) {
//			if (token == OTHER) {
//				// do nothing for non-colored tokens
//			} else if (token != WHITE) {
//				Color color = getColor(token);
//				// Only create a style if the token color is different than the
//				// widget's default foreground color and the token's style is
//				// not
//				// bold. Keywords are bolded.
//				if ((!color.equals(defaultFgColor)) || (token == KEY)) {
//					StyleRange style = new StyleRange(scanner.getStartOffset()
//							+ event.lineOffset, scanner.getLength(), color,
//							null);
//					if (token == KEY) {
//						style.fontStyle = SWT.BOLD;
//					}
//					if (styles.isEmpty()) {
//						styles.addElement(style);
//					} else {
//						// Merge similar styles. Doing so will improve
//						// performance.
//						lastStyle = (StyleRange) styles.lastElement();
//						if (lastStyle.similarTo(style)
//								&& (lastStyle.start + lastStyle.length == style.start)) {
//							lastStyle.length += style.length;
//						} else {
//							styles.addElement(style);
//						}
//					}
//				}
//			} else if ((!styles.isEmpty())
//					&& ((lastStyle = (StyleRange) styles.lastElement()).fontStyle == SWT.BOLD)) {
//				int start = scanner.getStartOffset() + event.lineOffset;
//				lastStyle = (StyleRange) styles.lastElement();
//				// A font style of SWT.BOLD implies that the last style
//				// represents a java keyword.
//				if (lastStyle.start + lastStyle.length == start) {
//					// Have the white space take on the style before it to
//					// minimize the number of style ranges created and the
//					// number of font style changes during rendering.
//					lastStyle.length += scanner.getLength();
//				}
//			}
//			token = scanner.nextToken();
//		}
//		event.styles = new StyleRange[styles.size()];
//		styles.copyInto(event.styles);
//	}
//
//	public void parseBlockComments(String text) {
//		blockComments = new Vector();
//		StringReader buffer = new StringReader(text);
//		int ch;
//		boolean blkComment = false;
//		int cnt = 0;
//		int[] offsets = new int[2];
//		boolean done = false;
//
//		try {
//			while (!done) {
//				switch (ch = buffer.read()) {
//				case -1: {
//					if (blkComment) {
//						offsets[1] = cnt;
//						blockComments.addElement(offsets);
//					}
//					done = true;
//					break;
//				}
//				case '/': {
//					ch = buffer.read();
//					if ((ch == '*') && (!blkComment)) {
//						offsets = new int[2];
//						offsets[0] = cnt;
//						blkComment = true;
//						cnt++;
//					} else {
//						cnt++;
//					}
//					cnt++;
//					break;
//				}
//				case '@': {
//					ch = buffer.read();
//					if ((ch == 'D') && (!blkComment)) {
//						offsets = new int[2];
//						offsets[0] = cnt;
//						blkComment = true;
//						cnt++;
//					} else {
//						cnt++;
//					}
//					cnt++;
//					break;
//				}
//				case 'n': {
//					if (blkComment) {
//						ch = buffer.read();
//						cnt++;
//						if (ch == 'd') {
//							blkComment = false;
//							offsets[1] = cnt;
//							blockComments.addElement(offsets);
//						}
//					}
//					cnt++;
//					break;
//				}
//				case '*': {
//					if (blkComment) {
//						ch = buffer.read();
//						cnt++;
//						if (ch == '/') {
//							blkComment = false;
//							offsets[1] = cnt;
//							blockComments.addElement(offsets);
//						}
//					}
//					cnt++;
//					break;
//				}
//				default: {
//					cnt++;
//					break;
//				}
//				}
//			}
//		} catch (IOException e) {
//			// ignore errors
//		}
//	}
//
//	/**
//	 * A simple fuzzy scanner for Java
//	 */
//	public static class JavaScanner {
//
//		protected static Hashtable fgKeys = null;
//
//		protected StringBuffer fBuffer = new StringBuffer();
//
//		protected String fDoc;
//
//		protected int fPos;
//
//		protected int fEnd;
//
//		protected int fStartToken;
//
//		protected boolean fEofSeen = false;
//
//		private static String[] fgs = { "Seq{", "=", "end", "elseif", "then",
//				"else", ",", "if", "/", "*", "+", "orelse", "or", "implies",
//				"andthen", "and", ".", ":=", "<>", ">=", ">", "<=", "<", "->",
//				"false", "true", "@", "!", "parserImport", "try", "catch",
//				"context", "<|", "|>", "let", ":", "throw", "[|", "|]", "|",
//				"when", "[", "not", "-", "in", "self", "(", "]", "Set{", "}",
//				")", "import", ";", "::", "XML", "Event", "Command", "Handler",
//				"EventManager", "Grammar", "ToXML", "Doc", "Operation",
//				"Search", "Handler", "Find", "FindBound", "ManifestEntry",
//				"ManifestAction", "File", "XML", "Ref", "Manifest", "Sync",
//				"AddAction", "AddRight", "DeleteRight", "SyncContainers",
//				"DeleteAction", "SlotValueChanged", "AddLeft", "DeleteLeft",
//				"GraphicalTool", "Perspective", "PlaceHolder",
//				"DeployedToolDescriptor", "BrandingProperty", "Extend",
//				"LabelChangeHandler", "Delete", "LabelChangeGuard",
//				"OperationRefSyntax", "Interface", "Image", "ChildSortMode",
//				"EditableTextOp", "Select", "Menu", "Label", "Property",
//				"Remove", "Child", "InheritChildren", "MenuRoot",
//				"ClipboardRule", "Grammar", "Diagram", "Java", "HTML",
//				"ManifestCode", "Table", "Loop", "Aspect", "Class",
//				"ElementManager", "Observer", "EventHandler", "ToolBar",
//				"ToolButton", "LabelExp", "NodeType", "ToolType", "ToolGroup",
//				"Label", "EdgeType", "DisplayType", "Box", "Or", "Point",
//				"PaddedText", "Exp", "PaddedBox", "Image", "Shape", "Text",
//				"Ellipse", "Star", "Ref", "ToolDescriptor", "Box", "Nodes",
//				"Or", "List", "NodeChoice", "Button", "ToolType", "TreeNode",
//				"TextBox", "CheckBox", "Tree", "TextField", "Label",
//				"ComboBox", "Star", "FormElement", "Ref", "ToolDescriptor",
//				"WithEvents", "WithoutEvents", "MenuAction", "MenuItems",
//				"Menu", "OCL", "Command", "ExpEditor", "End", "Association",
//				"Constructor", "Undoable", "TypeCase", "Record",
//				"ColorObjectNode", "Bind", "Theme", "ExtendClass",
//				"AbstractOp", "Character", "Clause", "Imports", "Enum",
//				"Command", "WithoutDaemons", "Service", "Count", "CaseInt",
//				"CaseObj", "WithOpenFile", "For", "Class", "Include", "Case",
//				"WithoutSourcePos", "Package", "Stats", "WithoutLocalNames",
//				"Derived", "When", "Letrec", "IconizeNode", "While", "Repeat",
//				"NameSpace", "Find", "TableGet", "CallCommand", "Attribute",
//				"Fork", "Menu", "NotNull", "SendCommand", "Todo", "Doc",
//				"Snapshot", "Type", "Constraint", "AsPacket",
//				"WithoutRendering", "Busy", "Map", "Index", "Select", "Start",
//				"Result", "Cmp", "Ecore", "Env", "OCL", "Pattern",
//				"MonitorCalls", "Java", "Statement", "JExp", "Builtin",
//				"Rules", "Rule", "Relate", "WithRules", "@", "do", };
//		private static final ArrayList<String> fgKeywords = new ArrayList<String>();
//
//		public JavaScanner() {
//			initialize();
//			for (String s : fgs) {
//				setKeywords(s, "blue");
//			}
//		}
//
//		public static void setKeywords(String word, String color) {
//			fgKeywords.add(word);
//
//		}
//
//		/**
//		 * Returns the ending location of the current token in the document.
//		 */
//		public final int getLength() {
//			return fPos - fStartToken;
//		}
//
//		/**
//		 * Initialize the lookup table.
//		 */
//		static void initialize() {
//			fgKeys = new Hashtable();
//			Integer k = new Integer(KEY);
//			for (int i = 0; i < fgKeywords.size(); i++)
//				fgKeys.put(fgKeywords.get(i), k);
//		}
//
//		/**
//		 * Returns the starting location of the current token in the document.
//		 */
//		public final int getStartOffset() {
//			return fStartToken;
//		}
//
//		/**
//		 * Returns the next lexical token in the document.
//		 */
//		public int nextToken() {
//			int c;
//			fStartToken = fPos;
//			while (true) {
//				switch (c = read()) {
//				case EOF:
//					return EOF;
//				case '/': // comment
//					c = read();
//					if (c == '/') {
//						while (true) {
//							c = read();
//							if ((c == EOF) || (c == EOL)) {
//								unread(c);
//								return COMMENT;
//							}
//						}
//					} else {
//						unread(c);
//					}
//					return OTHER;
//				case '\'': // char const
//					character: for (;;) {
//						c = read();
//						switch (c) {
//						case '\'':
//							return STRING;
//						case EOF:
//							unread(c);
//							return STRING;
//						case '\\':
//							c = read();
//							break;
//						}
//					}
//
//				case '"': // string
//					string: for (;;) {
//						c = read();
//						switch (c) {
//						case '"':
//							return STRING;
//						case EOF:
//							unread(c);
//							return STRING;
//						case '\\':
//							c = read();
//							break;
//						}
//					}
//
//				case '0':
//				case '1':
//				case '2':
//				case '3':
//				case '4':
//				case '5':
//				case '6':
//				case '7':
//				case '8':
//				case '9':
//					do {
//						c = read();
//					} while (Character.isDigit((char) c));
//					unread(c);
//					return NUMBER;
//				default:
//					if (Character.isWhitespace((char) c)) {
//						do {
//							c = read();
//						} while (Character.isWhitespace((char) c));
//						unread(c);
//						return WHITE;
//					}
//					if (Character.isJavaIdentifierStart((char) c)) {
//						fBuffer.setLength(0);
//						do {
//							fBuffer.append((char) c);
//							c = read();
//						} while (Character.isJavaIdentifierPart((char) c));
//						unread(c);
//						Integer i = (Integer) fgKeys.get(fBuffer.toString());
//						if (i != null)
//							return i.intValue();
//						return WORD;
//					}
//					// if (Character.isJavaIdentifierStart((char) c))
//					{
//						fBuffer.setLength(0);
//						// do
//						{
//							fBuffer.append((char) c);
//							c = read();
//						}
//						// while (Character.isSpace((char) c));
//						unread(c);
//						Integer i = (Integer) fgKeys.get(fBuffer.toString());
//						if (i != null) {
//							return i.intValue();
//							// return WORD;
//						}
//					}
//					// return OTHER;
//
//					// return OTHER;
//				}
//			}
//		}
//
//		/**
//		 * Returns next character.
//		 */
//		protected int read() {
//			if (fPos <= fEnd) {
//				return fDoc.charAt(fPos++);
//			}
//			return EOF;
//		}
//
//		public void setRange(String text) {
//			fDoc = text;
//			fPos = 0;
//			fEnd = fDoc.length() - 1;
//		}
//
//		protected void unread(int c) {
//			if (c != EOF)
//				fPos--;
//		}
//	}

}
