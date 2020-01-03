package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.Edge.HeadStyle;
import tool.clients.fmmlxdiagrams.dialogs.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.menus.AssociationContextMenu;

import java.util.Optional;
import java.util.Vector;

public class FmmlxAssociation extends Edge implements FmmlxProperty {

	private final PropertyType propertyType = PropertyType.Association;
	private String name;
	private String reverseName;
	private String accessNameStartToEnd;
	private String accessNameEndToStart;
	private Integer levelStartToEnd;
	private Integer levelEndToStart;
	private Multiplicity multiplicityStartToEnd;
	private Multiplicity multiplicityEndToStart;
	protected boolean sourceVisible;
	protected boolean targetVisible;

	HeadStyle sourceHead; 
	HeadStyle targetHead;
	private final static Color TRANSPARENT = new Color(0, 0, 0, 0);
	private final static Color BLACK = new Color(0, 0, 0, 1);
	private final static Color WHITE = new Color(1, 1, 1, 1);

	FmmlxAssociation(
			Integer id,
			Integer startId,
			Integer endId,
			Integer parentAssociationId,
			Vector<Point2D> points,
			PortRegion startPortRegion, PortRegion endPortRegion,
			String name,
			String reverseName,
			String accessNameStartToEnd,
			String accessNameEndToStart,
			int levelStartToEnd,
			int levelEndToStart,
			Multiplicity multiplicityStartToEnd,
			Multiplicity multiplicityEndToStart,
			Vector<Object> labelPositions,
			FmmlxDiagram diagram) {

		super(id, diagram.getObjectById(startId), diagram.getObjectById(endId), points, startPortRegion, endPortRegion, labelPositions, diagram);

		this.name = name;
		this.reverseName = reverseName;
		this.accessNameStartToEnd = accessNameStartToEnd;
		this.accessNameEndToStart = accessNameEndToStart;
		this.levelStartToEnd = levelStartToEnd;
		this.levelEndToStart = levelEndToStart;
		this.multiplicityStartToEnd = multiplicityStartToEnd;
		this.multiplicityEndToStart = multiplicityEndToStart;
		
//		layout();
	}
	
	private enum Anchor {SOURCE,CENTRE,TARGET};

	@Override protected void layoutLabels() {
		createLabel(name, 0, Anchor.CENTRE, showChangeFwNameDialog, 0, BLACK, TRANSPARENT);
		if(reverseName != null) 
	    createLabel(reverseName, 1, Anchor.CENTRE, showChangeRvNameDialog, -20, BLACK, TRANSPARENT);
		createLabel(accessNameStartToEnd, 2, Anchor.TARGET, showChangeS2ENameDialog, 0, BLACK, TRANSPARENT);
		createLabel(accessNameEndToStart, 3, Anchor.SOURCE, showChangeE2SNameDialog, 0, BLACK, TRANSPARENT);
		createLabel(""+levelStartToEnd, 4, Anchor.TARGET, showChangeS2ELevelDialog, -20, WHITE, BLACK);
		createLabel(""+levelEndToStart, 5, Anchor.SOURCE, showChangeE2SLevelDialog, -20, WHITE, BLACK);
		createLabel(multiplicityStartToEnd.toString(), 6, Anchor.TARGET, showChangeS2EMultDialog, -40, BLACK, TRANSPARENT);
		createLabel(multiplicityEndToStart.toString(), 7, Anchor.SOURCE, showChangeE2SMultDialog, -40, BLACK, TRANSPARENT);
		layoutingFinishedSuccesfully = true;
	}
	
	

	private void createLabel(String value, int localId, Anchor anchor, Runnable action, int yDiff, Color textColor, Color bgColor) {
		Point2D storedPostion = getLabelPosition(localId);
		
		double w = FmmlxDiagram.calculateTextWidth(value);
		double h = FmmlxDiagram.calculateTextHeight();
		
		Vector<FmmlxObject> anchors = new Vector<>();
		if(anchor!=Anchor.TARGET) anchors.add(getSourceNode());
		if(anchor!=Anchor.SOURCE) anchors.add(getTargetNode());
		
		if(storedPostion != null) {
			diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, storedPostion.getX(), storedPostion.getY(), w, h, textColor, bgColor));
		} else {
			double boxHeight = anchor==Anchor.CENTRE?-20:
				(anchor==Anchor.SOURCE?sourceNode:targetNode).getHeight()/2;
			diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, 50, -boxHeight-30+yDiff, w, h, textColor, bgColor));
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	private void createAssociationInformationLabels(String value, int localId, Runnable action) {
		Point2D storedPostion = getLabelPosition(localId);
		
		double w = FmmlxDiagram.calculateTextWidth(value);
		double h = FmmlxDiagram.calculateTextHeight();
		
		if(storedPostion != null) {
			//TODO create information container ([informationen])
			
		} else {
			//TODO
		}
	}
	///////////////////////////////////////////////////////////////////////////


	@Override
	public String getName() {
		return name;
	}

	public String getReverseName() {
		return reverseName;
	}

	@Override
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public int getId() {
		return id;
	}

	public FmmlxObject getSourceNode() {
		return sourceNode;
	}

	public FmmlxObject getTargetNode() {
		return targetNode;
	}

	public Integer getLevelStartToEnd() {
		return levelStartToEnd;
	}

	public Integer getLevelEndToStart() {
		return levelEndToStart;
	}

	public String getAccessNameStartToEnd() {
		return accessNameStartToEnd;
	}

	public String getAccessNameEndToStart() {
		return accessNameEndToStart;
	}

	public Multiplicity getMultiplicityStartToEnd() {
		return multiplicityStartToEnd;
	}

	public Multiplicity getMultiplicityEndToStart() {
		return multiplicityEndToStart;
	}

	public boolean getTargetVisible() {
		return targetVisible;
	}

	public boolean getSourceVisible() {
		return sourceVisible;
	}

	public String toPair() {
		String firstString = this.getSourceNode().getName();
		String seconString = this.getTargetNode().getName();
		return "( " + firstString + " ; " + seconString + " )";
	}
	
	public Vector<FmmlxLink> getInstance(){
		return diagram.getAssociationInstance();
	}	
	
	public boolean doObjectsFit(FmmlxObject source, FmmlxObject target) {
		if(source==null || target == null) return false;
		if (source.isInstanceOf(getSourceNode(), levelEndToStart) && target.isInstanceOf(getTargetNode(), levelStartToEnd))
			return true;
		if (target.isInstanceOf(getSourceNode(), levelEndToStart) && source.isInstanceOf(getTargetNode(), levelStartToEnd))
			return true;
		return false;
	}

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new AssociationContextMenu(this, actions);
	}

	@Override
	protected Double getLineDashes() {
		return new Double(0);
	}

	private Runnable showChangeFwNameDialog = () -> { 
		TextInputDialog td = new TextInputDialog(name);
		td.setHeaderText("Change Forward Association Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationForwardName(diagram, this.id, result.get());
			diagram.updateDiagram();
		}
	};

	private Runnable showChangeRvNameDialog = null;
	
	private Runnable showChangeS2ELevelDialog = () -> {
		TextInputDialog td = new TextInputDialog(levelStartToEnd+"");
		td.setHeaderText("Change Start to End Level");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			try {
				Integer level = Integer.parseInt(result.get());
				diagram.getComm().changeAssociationStart2EndLevel(diagram, this.id, level);
				diagram.updateDiagram();
			} catch (Exception e) {
				System.err.println("Number not readable. Change Nothing.");
			}
		}
	};
	
	private Runnable showChangeS2ENameDialog = () -> { 
		TextInputDialog td = new TextInputDialog(accessNameStartToEnd);
		td.setHeaderText("Change Start to End Access Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationStart2EndAccessName(diagram, this.id, result.get());
			diagram.updateDiagram();
		}
	};
	
	private Runnable showChangeS2EMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityStartToEnd);
		Optional<MultiplicityDialogResult> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.getComm().changeAssociationStart2EndMultiplicity(diagram, this.id, mr.get().convertToMultiplicity());
			diagram.updateDiagram();
		}
	};
	
	private Runnable showChangeE2SLevelDialog = () -> {
		TextInputDialog td = new TextInputDialog(levelEndToStart+"");
		td.setHeaderText("Change End to Start Level");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			try {
				Integer level = Integer.parseInt(result.get());
				diagram.getComm().changeAssociationEnd2StartLevel(diagram, this.id, level);
				diagram.updateDiagram();
			} catch (Exception e) {
				System.err.println("Number not readable. Change Nothing.");
			}
		}
	};
	
	private Runnable showChangeE2SNameDialog = () -> { 
		TextInputDialog td = new TextInputDialog(accessNameEndToStart);
		td.setHeaderText("Change End to Start Access Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationEnd2StartAccessName(diagram, this.id, result.get());
			diagram.updateDiagram();
		}
	};
	
	private Runnable showChangeE2SMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityEndToStart);
		Optional<MultiplicityDialogResult> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram, this.id, mr.get().convertToMultiplicity());
			diagram.updateDiagram();
		}
	};
	
	@Override
	public HeadStyle getTargetDecoration() {
		
		return HeadStyle.ARROW;
	}
}
