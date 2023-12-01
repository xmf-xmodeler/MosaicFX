package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.fmmlxdiagrams.dialogs.NoteCreationDialog;
import tool.clients.fmmlxdiagrams.graphics.NodeBox;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;
import tool.clients.fmmlxdiagrams.menus.NoteContextMenu;

public class Note extends Node implements CanvasElement {

	private Color noteColor;
	private String content;
	private int id;
	private NodeBox selectionMarker;

	public Note(FmmlxDiagram diagram, Point2D canvasPosition, NoteCreationDialog.Result noteResult) {
		setId(diagram);
		setX(canvasPosition.getX());
		setY(canvasPosition.getY());
		noteColor = noteResult.getColor();
		content = noteResult.getContent();
		layout();
	}

	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, DiagramViewPane view) {
		// needs to be called before every paint because there is no mechanism to remove
		// the selectionMarker once it is added.
		rootNodeElement.getNodeElements().remove(selectionMarker);
		if (view.getDiagram().getSelectedObjects().contains(this)) {
			rootNodeElement.addNodeElementAtFirst(selectionMarker);
		}
		rootNodeElement.paintOn(view, false);
	}

	@Override
	public ContextMenu getContextMenu(DiagramViewPane fmmlxDiagram, Point2D absolutePoint) {
		return new NoteContextMenu(fmmlxDiagram.getDiagram(), this);
	}

	@Override
	protected void layout(FmmlxDiagram diagram) {
		throw new AbstractMethodError("Not implemented for this class");
	}

	private void layout() {
		// layoutProperties, the values were determined experimentally and can be adapted to preferences
		final double MAX_WIDTH = 300;
		final double VERTICAL_OFFSET_CONTENT_TO_HEADER = 15;
		final Color BORDER_COLOR = Color.BLACK;

		NodeGroup group = new NodeGroup(new Affine(1, 0, getX(), 0, 1, getY()));
		rootNodeElement = group;

		NodeLabel header = layoutHeader();

		WrapedNodeLabelFactory factory = new WrapedNodeLabelFactory();
		NodeGroup wrapedNodeLabel = factory.create(content, MAX_WIDTH, false);
		wrapedNodeLabel.setPosition(0, VERTICAL_OFFSET_CONTENT_TO_HEADER);

		// build nodeBox, that serves as frame for the note
		NodeBox box = new NodeBox();
		box.setPosition(0, -(header.getTextHeight() + VERTICAL_OFFSET_CONTENT_TO_HEADER));
		box.setBgColor(noteColor);
		box.setFgColor(BORDER_COLOR);
		double totalHeight = factory.getTextHeight() + header.getTextHeight() + VERTICAL_OFFSET_CONTENT_TO_HEADER;
		box.setSize(MAX_WIDTH, totalHeight);

		// This NodeBox is only added to the group in the case that the Note is
		// selected. The object is created at this place because all needed data is in
		// the scope of this function.
		selectionMarker = new NodeBox();
		double markerLineWidth = 4;
		selectionMarker.setPosition(0 - (markerLineWidth / 2), -(header.getTextHeight() + VERTICAL_OFFSET_CONTENT_TO_HEADER) - (markerLineWidth / 2));
		selectionMarker.setBgColor(Color.BLACK);
		selectionMarker.setFgColor(BORDER_COLOR);
		selectionMarker.setSize(MAX_WIDTH + markerLineWidth, totalHeight + markerLineWidth);

		// The direction in which the elements are added to the group is crucial.
		// Elements that are added later are printed over the Elements that have been
		// added before
		group.addNodeElement(box);
		group.addNodeElement(header);
		group.addNodeElement(wrapedNodeLabel);
	}

	private NodeLabel layoutHeader() {
		String headerText = "Note #" + id;
		NodeLabel header = new NodeLabel(headerText);
		header.setAlignment(Pos.TOP_LEFT);
		header.setPosition(0, 0);
		header.setFontWeight(FontWeight.BOLD);
		return header;
	}

	public static void addNoteToDiagram(FmmlxDiagram diagram, Note note) {
		diagram.getNotes().add(note);
		// in XMF version vielleicht ändern zu update()
		diagram.redraw();
	}

	private static void removeNoteFromDiagram(FmmlxDiagram diagram, Note note) {
		diagram.getNotes().remove(note);
	}

	public int getId() {
		return id;
	}

	public void setNoteColor(Color noteColor) {
		this.noteColor = noteColor;
		layout();
	}

	public void setContent(String content) {
		this.content = content;
		layout();
	}

	public Color getNoteColor() {
		return noteColor;
	}

	public String getContent() {
		return content;
	}

	public void delete(FmmlxDiagram diagram) {
		removeNoteFromDiagram(diagram, this);
		diagram.updateDiagram();
	}

	public void setId(FmmlxDiagram diagram) {
		if (diagram.getNotes().isEmpty()) {
			id = 0;
		} else {
			int size = diagram.getNotes().size();
			// the Id should be the highest Id in the diagram + 1
			id = diagram.getNotes().get(size - 1).getId() + 1;
		}
	}
}