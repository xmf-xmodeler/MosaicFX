package tool.clients.editors;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public class NumberWordRule extends WordRule {

	public NumberWordRule() {
		super(null, new Color(Display.getCurrent(), 255, 0, 128));
	}

	public void writeXML(PrintStream out) {
		out.print("");
	}

	public StyleRange match(String s, int i, int prevChar) {
		if (!Character.isLetterOrDigit(prevChar)) { // If previous is neither letter nor number
			String number = parseNumber(s, i);
			if (number != null) {
				int length = number.length();
				StyleRange style = new StyleRange();
				style.start = i;
				style.length = length;
				style.fontStyle = SWT.BOLD;
				style.foreground = number.contains(".") || number.contains("e") || number.contains("E")
						? new Color(Display.getCurrent(), 100, 50, 0)
					    : new Color(Display.getCurrent(), 200, 0, 100);
				return style;
			} else
				return null;
		} else
			return null;
	}

	private String parseNumber(String s, int start) {
		final int START = 0;
		final int DIGIT_READ_AFTER_START = 1;
		final int DOT_READ = 2;
		final int DIGIT_READ_AFTER_DOT = 4;
		final int EXP_READ = 5;
		final int SIGN_READ_AFTER_EXP = 6;
		final int DIGIT_READ_AFTER_EXP = 7;
		
		int i = start;
		int state = 0;
		int currendEndOfNumber = i-1;
		boolean finished = false;
		while (!finished) {
			if(s.length() < i) {
				finished = true;
				break;
			}
			char c = s.charAt(i);
			switch (state) {
			case START: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_START;
					currendEndOfNumber = i;
				} else {
					finished = true;
				}
				break;
			}
			case DIGIT_READ_AFTER_START: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_START;
					currendEndOfNumber = i;
				} else if ('.' == c) {
					state = DOT_READ;
				} else if ('e' == c || c == 'E') {
					state = EXP_READ;
				} else {
					finished = true;
				}
				break;
			}
			case DOT_READ: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_DOT;
					currendEndOfNumber = i;
				} else {
					finished = true;
				}
				break;
			}
			case DIGIT_READ_AFTER_DOT: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_DOT;
					currendEndOfNumber = i;
				} else if ('e' == c || c == 'E') {
					state = EXP_READ;
				} else {
					finished = true;
				}
				break;
			}
			case EXP_READ: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_EXP;
					currendEndOfNumber = i;
				} else if ('-' <= c) {
					state = SIGN_READ_AFTER_EXP;
				} else {
					finished = true;
				}
				break;
			}
			case SIGN_READ_AFTER_EXP: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_EXP;
					currendEndOfNumber = i;
				} else {
					finished = true;
				}
				break;
			}
			case DIGIT_READ_AFTER_EXP: {
				if ('0' <= c && c <= '9') {
					state = DIGIT_READ_AFTER_EXP;
					currendEndOfNumber = i;
				} else {
					finished = true;
				}
				break;
			}
			}
			i++;
		}
		if(currendEndOfNumber < start) return null;
	return s.substring(start, currendEndOfNumber+1);
}

	public boolean canStartKeyword(int prevChar, int keyChar) {
		return !(Character.isLetterOrDigit(prevChar) && Character.isLetterOrDigit(keyChar));
	}

	public String toString() {
		return "NumberWordRule()";
	}

	public boolean starts(char c) {
		return word.charAt(0) == c;
	}

}
