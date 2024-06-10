package tool.xmodeler.tool_introduction;

import java.util.Stack;

public class TaskDescriptionHistory extends Stack<String> {
		
	private final TaskDescriptionViewer viewer; 
	/**
	 * If users goes backwards in description history, for every backwards step one element is pushed to this stack
	 */
	private final Stack<String> forwardStack = new Stack<String>();
	
	public TaskDescriptionHistory(TaskDescriptionViewer viewer) {
		this.viewer = viewer;
	}
	
	public boolean isBackwardNavigable() {
		return this.size() > 1;
	}
	
	public boolean isForwardNavigable() {
		return forwardStack.size() > 0 ;
	}

	public void navigateBack() {
		forwardStack.push(this.pop());
		viewer.loadHtmlContent(this.peek());
		viewer.updateGui();
	}

	public void navigateForward() {
		String forwardContent = forwardStack.pop();
		this.push(forwardContent);
		viewer.loadHtmlContent(forwardContent);
		viewer.updateGui();
	}
}