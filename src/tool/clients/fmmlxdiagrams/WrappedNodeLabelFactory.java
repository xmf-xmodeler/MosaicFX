package tool.clients.fmmlxdiagrams;

import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.NodeBox;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;

class WrappedNodeLabelFactory {
	
	private NodeGroup group = new NodeGroup();
	private double latestY = -1;
	private Color boxBackgroundColor;
	private Color boxForegroundColor;
	private NodeBox box;

	
	 public WrappedNodeLabelFactory() {
		    super();		    
		  }

	/**
	 * Creates a NodeGroup, that contains a wrapped Text
	 * 
	 * @param text that should be wrapped
	 * @param maxWidth limits the width of the text and the surrounding NodeBox
	 * @param enableBox decides if a NodeBox is added to the Group. Please use setters of the box to define the color.
	 * @return a assembled NodeGrope with wrapped text
	 */
	public NodeGroup create(String text, double maxWidth, boolean enableBox) {
		appendWrapedTextToGroup(text, maxWidth);		
		if (enableBox) {
			appendNodeBox(maxWidth);
		}
		return group;			
	}
	
	private void appendNodeBox(double maxWidth) {
		box = new NodeBox();
		box.setPosition(0, - FmmlxDiagram.getFont().getSize());
		box.setSize(maxWidth, getTextHeight());
		group.addNodeElementAtFirst(box);
	}

	//This function will cause problems if the text contains many whitespace-characters
	//Can not handle line-breaks
	private void appendWrapedTextToGroup(String text, double maxWidth) {
		// defines the vertical difference between two lines
		double lineShot = FmmlxDiagram.getFont().getSize();
		String[] words = text.split(" ");

		// initialize the current positions. The positions will be adapted within this
		// function.
		// Right now the assumption is, that the text starts at 0,0. This is because the
		// position is relative to the NodeGroup this Group is added.
		double currentX = 0;
		double currentY = 0;

		for (String word : words) {
			NodeLabel newWord = new NodeLabel(word);
			// Check if the current x position + the new word would write over the max
			// width.
			// will give problem with very long words
			if (currentX + newWord.getWidth() > maxWidth) {
				// break line
				currentX = 0;
				currentY += lineShot;
			}
			newWord.setPosition(currentX, currentY);
			group.addNodeElement(newWord);
			// update position for the next length check.
			currentX += newWord.getWidth() + new NodeLabel(" ").getWidth();
			latestY = currentY;
		}
	}

	public double getTextHeight() {
		if (latestY == -1) {
			throw new IllegalArgumentException("No WrapedNodeLabel was created. Pleas call WrapedNodeLabelFactory.create() before requesting latest y");
		}
		return latestY + FmmlxDiagram.getFont().getSize();
	}

	public void setBoxBackgroundColor(Color boxBackgroundColor) {
		this.boxBackgroundColor = boxBackgroundColor;
	}

	public void setBoxForegroundColor(Color boxForegroundColor) {
		this.boxForegroundColor = boxForegroundColor;
	}

	public NodeBox getBox() {
		return box;
	}
}