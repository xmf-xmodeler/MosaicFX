package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.Vector;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import tool.clients.editors.MultiLineRule;
import tool.clients.editors.WordRule;

public class CodeBox {

	private InlineCssTextArea textArea;
	private transient boolean syntaxBusy;
	private transient int syntaxDirty = 0;
	private Vector<WordRule> wordRules = new Vector<WordRule>();
	public VirtualizedScrollPane<InlineCssTextArea> virtualizedScrollPane;

	public CodeBox(int fontsize, boolean editable, String s) {
		
		initWordRules();

		textArea = new InlineCssTextArea(s);
		virtualizedScrollPane = new VirtualizedScrollPane<InlineCssTextArea>(textArea);
		textArea.setEditable(editable);
		textArea.setStyle("-fx-font-size:" + fontsize + "pt;");
		textArea.setStyle("-fx-font-family: 'DejaVu Sans Mono'");

		textArea.plainTextChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(change -> {
			int start = change.getPosition();
			int length = change.getNetLength();
			if (length > 0)
				try{
					addStylesQueueRequest(start, length, textArea.getText());
				} catch(IndexOutOfBoundsException e) {
					
				}
			syntaxDirty++;
			Platform.runLater(() -> {
				syntaxDirty--;
				if (syntaxDirty == 0) {
					addStyles();
				}
			});
		});
	}

	private void initWordRules() {
		addMultilineRule("//", "\n", 120, 120, 120);
	    addMultilineRule("/*","*/",102,0,204);
	    addMultilineRule("@Doc","end",180,0,0);
	    addMultilineRule("\"","\"",0,180,0);
	    
	    String[] literals = new String[]{
	        "do",
	    	"=",";","import",",",
	    	"Seq{","self","false","true","?","<-",
	    	"|]","[|","!","parserImport",
	    	"let","try","catch","|>","<|","context",
	    	":","throw","when","->","not","<","<=",">",">=","<>",":=",".",
	    	"and","andthen","implies","or","orelse","+","-","*","/","in",
	    	"(","Set{","if",")","[","]","else","then","elseif","end","::","|","{","}"};
	    
	    for(String s : literals) {
	    	addWordRule(s,0,0,222);
	    }
	    
        addWordRule("@",255,0,127);
		
	}

	private void addStylesQueueRequest(int start, int length, String s) {
		int startNew = backupToPossibleStyleStart(start);
		length = length + start - startNew;
		start = startNew;
		if (syntaxBusy) {
			styleQueue.add(new StyleQueueItem(start, length, s));
		} else {
			if (styleQueue.size() != 0)
				System.err.println("A request got stuck");
			syntaxBusy = true;
			addStylesNew(start, length, s);
		}
	}

	private class StyleQueueItem {
		final int start;
		final int length;
		final String s;

		public StyleQueueItem(int start, int length, String s) {
			super();
			this.start = start;
			this.length = length;
			this.s = s;
		}
	}

	private Vector<StyleQueueItem> styleQueue = new Vector<StyleQueueItem>();

	private void addStylesNew(final int start, final int length, final String s) {
		if (textArea.getText().length() > 0) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final StyleSpans<String> styleSpans = styleRange(start, start + length, s);
					if (styleSpans != null && styleSpans.length() > 0) {
						Platform.runLater(() -> {
							try{
								textArea.setStyleSpans(0, styleSpans);
							} catch (Exception e) {
								System.err.println("Style Problem:" + e.getMessage());
							}
						});
					}
					if (styleQueue.size() != 0) {
						final StyleQueueItem next = styleQueue.remove(0);
						// System.err.println("request unqueued");
						Platform.runLater(() -> {
							addStylesNew(next.start, next.length, next.s);
						});
					} else {
						syntaxBusy = false;
					}
				}
			});

			thread.start();
		} else {
			syntaxBusy = false;
		}
	}

	private StyleSpans<String> styleRange(int start, int end, String s) {
		StyleSpansBuilder<String> sb = new StyleSpansBuilder<String>();
		StyleSpan<String> defaultStyle = new StyleSpan<String>("", 1);

		int prevChar = -1;
		for (int i = start; i < end; i++) {
			StyleSpan<String> style = null;
			for (WordRule wordRule : wordRules) {
				style = wordRule.match(s, i, prevChar);
				if (style != null) {
					sb.add(style);
					// ranges.add(style);
					i = i + style.getLength() - 1;
					break;
				}
			}
			if (style == null) {
				sb.add(defaultStyle);
			}
			prevChar = s.charAt(i);
		}
		return sb.create();
	}

	private int backupToPossibleStyleStart(int start) {
		start -= 10;
		if (start < 0)
			return 0;
		int checkStart = start - 1;
		StyleSpans<String> spans = textArea.getStyleSpans(start, start);
		spans.getStyleSpan(0);

		if (spans.getStyleSpan(0) == textArea.getStyleSpans(checkStart, checkStart).getStyleSpan(0)) {
			while (spans.getStyleSpan(0) == textArea.getStyleSpans(checkStart, checkStart).getStyleSpan(0)) {
				checkStart--;
			}
			return checkStart + 1;
		}

		String s = textArea.getText();
		while (start > 0 && s.charAt(start) != ' ') // isKeywordChar2(s.charAt(start)))
			start--;
		return start;
	}

	private void addStyles() {
		addStylesQueueRequest(0, textArea.getText().length(), textArea.getText());
	}
	
  public void addMultilineRule(String start, String end, int red, int green, int blue) {
//	    if (getId().equals(id)) {
	      wordRules.add(new MultiLineRule(start, end, Color.rgb(red, green, blue)));
//	    }
	  }


	public void addWordRule(String text, int red, int green, int blue) {
//		if (getId().equals(id))
			wordRules.add(new WordRule(text, Color.rgb(red, green, blue)));
	}

	public void setText(String string) {
		try{textArea.replaceText(string);}
		catch (Exception e) {
			System.err.println("setText failed");
		}
	}

	public String getText() {
		return textArea.getText();
	}
}
