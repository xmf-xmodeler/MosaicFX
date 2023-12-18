package tool.clients.fmmlxdiagrams;

import java.util.Iterator;
import java.util.Vector;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.fmmlxdiagrams.dialogs.NoteCreationDialog;
import tool.clients.fmmlxdiagrams.graphics.GraphicalMappingInfo;
import tool.clients.fmmlxdiagrams.graphics.NodeBox;
import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;
import tool.clients.fmmlxdiagrams.menus.NoteContextMenu;
import xos.Value;

public class Note extends Node implements CanvasElement {

	private Color noteColor;
	private String content;
	private int id;
	private NodeBox selectionMarker;
	
	public Note() {
		super();
	}

	/**
	 * This constructor is called from Java-site. Afterwards the information is send
	 * to XMF. There the valid id is set and after the Instance comes back from XMF
	 * the note is added to a diagram.
	 * @param canvasPosition defines the position the note should be placed
	 * @param noteResult is the result of the noteCreationDialog and contains all info you need to add  a note
	 */
	public Note(Point2D canvasPosition, NoteCreationDialog.Result noteResult) {
		setId(-1);
		setX(canvasPosition.getX());
		setY(canvasPosition.getY());
		noteColor = noteResult.getColor();
		content = noteResult.getContent();
		hidden = false;
	}
	
	/**
	 * This constructor is used to create notes from data sent by the backend
	 */
	public Note(int noteId, String content, Color noteColor) {
		this.id = noteId;
		this.content = content;
		this.noteColor = noteColor;
		hidden = false;
	}

	@Override
	public void paintOn(GraphicsContext g, Affine currentTransform, DiagramViewPane view) {
		if (hidden) return;
		layout();
		// needs to be called before every paint because there is no mechanism to remove
		// the selectionMarker once it is added.
		removeSelectionMarker();
		if (view.getDiagram().getSelectedObjects().contains(this)) {
			rootNodeElement.addNodeElementAtFirst(selectionMarker);
		}
		rootNodeElement.paintOn(view, false);
	}

	/**
	 * Filter all NodeElements of rootNodeElement. Because there is no equal reference all NodeElements are checked against the attributes of
	 * the selection marker. If one element matches these attributes it is removed. So the note gets painted without black border that should show the selection of the note
	 */
	private void removeSelectionMarker() {
		Iterator<NodeElement> iterator = rootNodeElement.getNodeElements().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equals(selectionMarker)) {
				iterator.remove();
			}
		}
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
		checkForDiagramMapping();
		
		// layoutProperties, the values were determined experimentally and can be adapted to preferences
		final double MAX_WIDTH = 300;
		final double VERTICAL_OFFSET_CONTENT_TO_HEADER = 15;
		final Color BORDER_COLOR = Color.LIGHTGREY;

		NodeGroup group = null;
		if (rootNodeElement == null) {
			group = new NodeGroup(new Affine(1, 0, getX(), 0, 1, getY()));
			rootNodeElement = group;			
		} else {
			group = rootNodeElement;
		}
		
		NodeLabel header = layoutHeader();

		WrappedNodeLabelFactory factory = new WrappedNodeLabelFactory();
		NodeGroup wrappedNodeLabel = factory.create(content, MAX_WIDTH, false);
		wrappedNodeLabel.setPosition(0, VERTICAL_OFFSET_CONTENT_TO_HEADER);

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
		group.addNodeElement(wrappedNodeLabel);
	}

	/**
	 * If you use the constructor(int noteId, String content, Color noteColor) a default position is set for the note. Normally this is the first step in the creation logic.
	 * In XMF the note data is stored separate from the notes visible information. The diagramMappings are loaded later. This function should check if you have called the functions in the right order.
	 */
	private void checkForDiagramMapping() {
		if (x == 0 || y == 0) {
			//There could be the spare case, where a user added a note at position 0,0. Then ignore the error
	        throw new IllegalArgumentException("Please first get DiagramMapping from XMF");
	    }
	}

	private NodeLabel layoutHeader() {
		String headerText = "Note #" + id;
		NodeLabel header = new NodeLabel(headerText);
		header.setAlignment(Pos.TOP_LEFT);
		header.setPosition(0, 0);
		header.setFontWeight(FontWeight.BOLD);
		return header;
	}

	public int getId() {
		return id;
	}

	public void setNoteColor(Color noteColor) {
		this.noteColor = noteColor;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Color getNoteColor() {
		return noteColor;
	}

	public String getContent() {
		return content;
	}

	/**
	 * Removes note from diagram. Backendinformation is also deleted.
	 * @param diagram references the diagram the note should be deleted from
	 */
	public void remove(FmmlxDiagram diagram) {
		Value[] xmfParam = new Value[] {new Value(getId())};
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator(); 
		comm.xmfRequestAsync(comm.getHandle(), diagram.getID(), "removeNoteFromDiagram", (r)-> {}, xmfParam);
		diagram.updateDiagram();
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Note [noteColor=" + noteColor + ", content=" + content + ", id=" + id + ", x=" + x + ", y=" + y + ", hidden=" + hidden + "]";
	}
	
	public void sendCurrentNoteMappingToXMF(int diagramId, ReturnCall<Object> onNotePositionSet) {
		Value[] xmfParam = new Value[] {
				new Value(getId()),
				new Value((int)getX()),
				new Value((int)getY()),
				new Value(isHidden())};
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator(); 
		comm.xmfRequestAsync(comm.getHandle(), diagramId, "sendNoteMappingToXMF", r -> onNotePositionSet.run(null), xmfParam);
	}
	
	public void updateNoteMappingXMF(int diagramId, int x, int y, boolean hidden) {
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator(); 
		Value[] xmfParam = new Value[] {
				new Value(getId()),
				new Value(x),
				new Value(y),
				new Value(hidden)};
		comm.xmfRequestAsync(comm.getHandle(), diagramId, "sendNoteMappingToXMF", r -> {}, xmfParam);
	}
	
	/**
	 *Send boolean to backend, that note is now hidden. Afterward the diagram is updated so that the note is no longer visible on the diagram.
	 */
	public void hide(AbstractPackageViewer diagram) {
		updateNoteMappingXMF(diagram.getID(), (int)getX(), (int)getY(), true);
		diagram.updateDiagram();
	}
	
	/**
	 *Send boolean to backend, that note is now visible. Afterward the diagram is updated so that the note is visible on the diagram.
	 */
	public void unhide(AbstractPackageViewer diagram) {
		updateNoteMappingXMF(diagram.getID(), (int)getX(), (int)getY(), false);
		diagram.updateDiagram();
	}

	@Override
	protected void updatePositionInBackend(int diagramID) {
		sendCurrentNoteMappingToXMF(diagramID, r -> {});
		
	}
	
	/**
	 * Different signatures are needed because of different scenarios. This is used if a user creates a new note from the diagram view. 
	 * At this time there is a already a diagram instance which needs to be update after the adding process to see the note on the canvas.
	 * @param diagram the note should be added to
	 * @param onNoteIdReturned possible actions after a note is added. In this case it is used to update notes position afterwards 
	 */
	public void addNoteToDiagram(AbstractPackageViewer diagram, ReturnCall<Integer> onNoteIdReturned) {
		addNoteToDiagram(diagram.getID(), onNoteIdReturned, true);
	}
	
	/**
	 * This is used while loading a diagram from XML. At this time there is no diagram instance in Java. So data is only send to backend. There is no callback needed because there is not directly called a Java-operation, that uses this data.
	 * The backenddata is loaded to the new diagram-java-instance, when the diagram is opened.
	 * @param diagramId defines to which backend instance of diagram the data is added to.
	 */
	public void addNoteToDiagram(int diagramId) {
		addNoteToDiagram(diagramId, r -> {}, false);
	}
	
	/**
	 * Adds note to diagram. The values of the java instance are sent to the backend. There an instance of a note is created. This instances is sent back to 
	 * Java and added to the note-list of the diagram referenced by the diagram id.
	 * @param diagramId references the diagram the note should be added to
	 * @param onNoteIdReturned here action could be defined that should be performed after the note is returned from backend
	 * @param updateDiagram decides if the diagram is updated after the note was added
	 */
	public void addNoteToDiagram(int diagramId, ReturnCall<Integer> onNoteIdReturned, boolean updateDiagram) {
		ReturnCall<Vector<Object>> onNoteIdReturnedFromXMF = idVector ->{
			// 2. the returned vector contains at 0 the proper id. This is the highest current id of a note in a diagram incremented by one
			int properId = (Integer) idVector.get(0);
			// 3. set the id of the dummy note to the proper id to use this object to transfer the noteMapping to the backend
			this.setId(properId);
			sendCurrentNoteMappingToXMF(diagramId, r -> {});
		// 4. this will start the callback that could be defined for the method
		onNoteIdReturned.run(properId);
		// 5. the diagram is updated. Afterward the diagram contains the created note in its notes-list
		if (updateDiagram) {
			AbstractPackageViewer diagram = FmmlxDiagramCommunicator.getDiagram(diagramId);
			diagram.updateDiagram();
		}
		};
		Value[] xmfParam = new Value[] {
				new Value(getContent()),
				new Value(getNoteColor().toString()), };
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
		// 1. send request to backend to create new note
		comm.xmfRequestAsync(comm.getHandle(), diagramId, "addNoteToDiagram", onNoteIdReturnedFromXMF, xmfParam);
	}
	
	/**
	 * Returns all notes the backend stores to a specific diagram
	 * @param diagramID references the diagram the notes are requested for
	 * @param notesReturned a returncall, that can perform an action on all returned notes transformed back to java instances
	 */
	public static void getAllNotes(int diagramID, ReturnCall<Vector<Note>> notesReturned) {
		ReturnCall<Vector<Object>> notesVectorReturned = notesVector -> {
			//the size of the vector equals the amount of notes stored in the backend
			@SuppressWarnings("unchecked")
			Vector<Vector<Object>> list = (Vector<Vector<Object>>) notesVector.get(0);
			//Create a new vector to return a list of Java Note-Instances
			Vector<Note> notes = new Vector<>();
			//For each position in the backenddata a new note is created
			for(Vector<Object> item : list) {
				int noteId  = Integer.parseInt(String.valueOf(item.get(0)));
				String content = String.valueOf(item.get(1));
				Color noteColor = Color.valueOf((String) item.get(2));
				Note note = new Note(noteId, content, noteColor);
				notes.add(note);
			}
			//The callback from the inputParam gets started. A list of notes is provided as inputParam. Operation then can be done on the java note.instances
			notesReturned.run(notes);
		};
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
		comm.xmfRequestAsync(comm.getHandle(), diagramID, "getAllNotes", notesVectorReturned);
	}
	
	/**
	 * Ask the backend for DiagramMappingInfos of notes
	 * @param diagramID references diagram you would like to get backenddata for
	 * @param noteMappingRetturned a returncall, that can perform an action on all returned noteMappingInfos transformed back to java instances
	 */
	public static void getNotesMappings(int diagramID, ReturnCall<Vector<GraphicalMappingInfo>> noteMappingRetturned) {
		ReturnCall<Vector<Object>> noteMappingsVectorReturned = noteMappings -> {
			// List that contains the backenddata
			Vector<Vector<Object>> list = (Vector<Vector<Object>>) noteMappings.get(0);
			// new vector is used to provide java.instnaces of MappingInfo
			Vector<GraphicalMappingInfo> mappings = new Vector<>();
			// For each backendinstance on java instances is generated
			for (Vector<Object> item : list) {
				String mappingKey = String.valueOf(item.get(0));
				double xPosition = Double.parseDouble(String.valueOf(item.get(1)));
				double yPosition = Double.parseDouble(String.valueOf(item.get(2)));
				boolean hidden = Boolean.parseBoolean(String.valueOf(item.get(3)));
				GraphicalMappingInfo mapping = new GraphicalMappingInfo(mappingKey, xPosition, yPosition, hidden);
				mappings.add(mapping);
			}
			// retuncall is started with the mapping info
			noteMappingRetturned.run(mappings);
		};
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
		comm.xmfRequestAsync(comm.getHandle(), diagramID, "getNoteMappings", noteMappingsVectorReturned);
	}

	/**
	 * Update note data in the backend. Attention if you not update the diagram change is not visible.
	 * @param diagram references the diagram the note should be updated for
	 * @param result contains the new Color + new Content of the note
	 */
	public void updateNoteData(AbstractPackageViewer diagram, NoteCreationDialog.Result result) {
		Value[] xmfParam = new Value[] {
				new Value(getId()),
				new Value(result.getContent()),
				new Value(result.getColor().toString()), };
		FmmlxDiagramCommunicator comm = FmmlxDiagramCommunicator.getCommunicator();
		comm.xmfRequestAsync(comm.getHandle(), diagram.getID(), "updateNoteData", r -> {}, xmfParam);
		diagram.updateDiagram();
	}
}