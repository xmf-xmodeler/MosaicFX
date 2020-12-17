package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.association.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.menus.AssociationContextMenu;

import java.util.Optional;
import java.util.Vector;

public class FmmlxAssociation extends Edge implements FmmlxProperty {

	private final PropertyType propertyType = PropertyType.Association;
	private String name;
	private final String reverseName;
	private String accessNameStartToEnd;
	private String accessNameEndToStart;
	private Integer levelStart;
	private Integer levelEnd;
	private final Integer parentAssociationId;
	private Multiplicity multiplicityStartToEnd;
	private Multiplicity multiplicityEndToStart;
	protected boolean sourceFromTargetVisible;
	protected boolean targetFromSourceVisible;
	protected boolean symmetric;
	protected boolean transitive;

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
			int levelStart,
			int levelEnd,
			Multiplicity multiplicityStartToEnd,
			Multiplicity multiplicityEndToStart,
			boolean sourceFromTargetVisible,
			boolean targetFromSourceVisible,
			boolean symmetric,
			boolean transitive,
			Vector<Object> labelPositions,
			FmmlxDiagram diagram) {

		super(id, diagram.getObjectById(startId), diagram.getObjectById(endId), points, startPortRegion, endPortRegion, labelPositions, diagram);

		this.name = name;
		this.parentAssociationId = parentAssociationId;
		this.reverseName = reverseName;
		this.accessNameStartToEnd = accessNameStartToEnd;
		this.accessNameEndToStart = accessNameEndToStart;
		this.levelStart = levelStart;
		this.levelEnd = levelEnd;
		this.multiplicityStartToEnd = multiplicityStartToEnd;
		this.multiplicityEndToStart = multiplicityEndToStart;
		this.sourceFromTargetVisible = sourceFromTargetVisible;
		this.targetFromSourceVisible = targetFromSourceVisible;
		this.symmetric = symmetric;
		this.transitive = transitive;
				
//		layout();
	}

	public Integer getParentAssociationId() {
		return parentAssociationId;
	}

	public String getParentAssociationName() {
		return getParentAssociationNameWithId();
	}

	private String getParentAssociationNameWithId() {
		FmmlxAssociation parent = diagram.getAssociationById(parentAssociationId);
		if(parent==null){
			return "-1";
		}
		return parent.getName();
	}

	private enum Anchor {CENTRE_MOVABLE, SOURCE_LEVEL, TARGET_LEVEL, SOURCE_MULTI, TARGET_MULTI,CENTRE_SELFASSOCIATION}

	@Override protected void layoutLabels() {
		if( sourceNode == targetNode) {
			createLabel(name, 0, Anchor.CENTRE_SELFASSOCIATION, showChangeFwNameDialog, BLACK, TRANSPARENT);
		}else {
		createLabel(name, 0, Anchor.CENTRE_MOVABLE, showChangeFwNameDialog, BLACK, TRANSPARENT);
		}
//		if(reverseName != null) 
//	    createLabel(reverseName, 1, Anchor.CENTRE, showChangeRvNameDialog, -20, BLACK, TRANSPARENT);
		
		createLabel(""+levelEnd, 2, Anchor.TARGET_LEVEL, showChangeS2ELevelDialog, WHITE, BLACK);
		createLabel(""+levelStart, 3, Anchor.SOURCE_LEVEL, showChangeE2SLevelDialog, WHITE, BLACK); 
		createLabel(multiplicityStartToEnd.toString(), 4, Anchor.TARGET_MULTI, showChangeS2EMultDialog, BLACK, TRANSPARENT);
		createLabel(multiplicityEndToStart.toString(), 5, Anchor.SOURCE_MULTI, showChangeE2SMultDialog, BLACK, TRANSPARENT);
		layoutingFinishedSuccesfully = true;
	}
	
	private void createLabel(String value, int localId, Anchor anchor, Runnable action, Color textColor, Color bgColor) {
		double w = FmmlxDiagram.calculateTextWidth(value);
		double h = FmmlxDiagram.calculateTextHeight();
		
		if(Anchor.CENTRE_MOVABLE == anchor) {
			Point2D storedPostion = getLabelPosition(localId);
			Vector<FmmlxObject> anchors = new Vector<>();
			anchors.add(getSourceNode());
			anchors.add(getTargetNode());
			if(storedPostion != null) {
				diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, storedPostion.getX(), storedPostion.getY(), w, h, textColor, bgColor));
			} else {
				diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, 0, -h*1.5, w, h, textColor, bgColor));
			}
		} else if (Anchor.CENTRE_SELFASSOCIATION==anchor) {
			Point2D storedPostion = getLabelPosition(localId);
			Vector<FmmlxObject> anchors = new Vector<>();
			anchors.add(getSourceNode());
			anchors.add(getTargetNode());
			if(storedPostion != null) {
				diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, storedPostion.getX(), storedPostion.getY(), w, h, textColor, bgColor));
			} else {
				diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, sourceNode.getWidth()/2, -4*h-0.5*sourceNode.getHeight(), w, h, textColor, bgColor));
			}
		} else {
			Vector<FmmlxObject> anchors = new Vector<>();
			double x,y;
			Point2D p;
			PortRegion dir;
			if(anchor == Anchor.SOURCE_LEVEL || anchor == Anchor.SOURCE_MULTI) {
				p = getSourceNode().getPointForEdge(sourceEnd, true);
				dir = getSourceNode().getDirectionForEdge(sourceEnd, true);
				anchors.add(getSourceNode());
			} else {
				p = getTargetNode().getPointForEdge(targetEnd, false);
				dir = getTargetNode().getDirectionForEdge(targetEnd, false); 
				anchors.add(getTargetNode());
			}
			FmmlxObject node = anchors.firstElement();

			final double TEXT_X_DIFF = 10;
			final double TEXT_Y_DIFF = 10;
			switch(anchor) {
			case SOURCE_LEVEL:
			case TARGET_LEVEL: {
				if(dir == PortRegion.SOUTH) {
					y = TEXT_Y_DIFF;
				} else {
					y = -TEXT_Y_DIFF-h;
				}
				if(dir == PortRegion.EAST) {
					x = TEXT_X_DIFF;
				} else {
					x = -TEXT_X_DIFF - w;
				}
				break;}
			case SOURCE_MULTI: 
			case TARGET_MULTI: {
				if(dir == PortRegion.NORTH) {
					y = -TEXT_Y_DIFF-h;
				} else {
					y = TEXT_Y_DIFF;
				}
				if(dir == PortRegion.WEST) {
					x = -TEXT_X_DIFF - w;
				} else {
					x = TEXT_X_DIFF;
				}
				break;}
			default: {x=0;y=0;break;}
			}
			diagram.addLabel(new DiagramEdgeLabel(this, localId, action, null, anchors, value, 
					p.getX() - node.getCenterX() + x, 
					p.getY() - node.getCenterY() + y, 
					w, h, textColor, bgColor));
		}
	}
	
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

	@Override
	public void setIntermediatePoints(Vector<Point2D> intermediatePoints) {
		super.intermediatePoints = intermediatePoints;
	}

	public Integer getLevelSource() {
		return levelStart;
	}

	public Integer getLevelTarget() {
		return levelEnd;
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

	public boolean isTargetVisible() {
		return targetFromSourceVisible;
	}

	public boolean isSourceVisible() {
		return sourceFromTargetVisible;
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
		if (source.isInstanceOf(getSourceNode(), levelStart) && target.isInstanceOf(getTargetNode(), levelEnd))
			return true;
		return target.isInstanceOf(getSourceNode(), levelStart) && source.isInstanceOf(getTargetNode(), levelEnd);
	}

	@Override
	public ContextMenu getContextMenuLocal(DiagramActions actions) {
		return new AssociationContextMenu(this, actions);
	}

	@Override
	protected Double getLineDashes() {
		return (double) 0;
	}

	private final Runnable showChangeFwNameDialog = () -> {
		TextInputDialog td = new TextInputDialog(name);
		td.setHeaderText("Change Forward Association Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationForwardName(diagram.getID(), this.getName(), result.get());
			diagram.updateDiagram();
		}
	};
	
	private final Runnable showChangeS2ELevelDialog = () -> {
		TextInputDialog td = new TextInputDialog(levelEnd+"");
		td.setHeaderText("Change Start to End Level");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			try {
				Integer level = Integer.parseInt(result.get());
				diagram.getComm().changeAssociationStart2EndLevel(diagram.getID(), this.id, level);
				diagram.updateDiagram();
			} catch (Exception e) {
				System.err.println("Number not readable. Change Nothing.");
			}
		}
	};
	
	private final Runnable showChangeS2ENameDialog = () -> {
		TextInputDialog td = new TextInputDialog(accessNameStartToEnd);
		td.setHeaderText("Change Start to End Access Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationStart2EndAccessName(diagram.getID(), this.id, result.get());
			diagram.updateDiagram();
		}
	};
	
	private final Runnable showChangeS2EMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityStartToEnd);
		Optional<MultiplicityDialogResult> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.getComm().changeAssociationStart2EndMultiplicity(diagram.getID(), this.getName(), mr.get().convertToMultiplicity());
			diagram.updateDiagram();
		}
	};
	
	private final Runnable showChangeE2SLevelDialog = () -> {
		TextInputDialog td = new TextInputDialog(levelStart+"");
		td.setHeaderText("Change End to Start Level");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			try {
				Integer level = Integer.parseInt(result.get());
				diagram.getComm().changeAssociationEnd2StartLevel(diagram.getID(), this.id, level);
				diagram.updateDiagram();
			} catch (Exception e) {
				System.err.println("Number not readable. Change Nothing.");
			}
		}
	};
	
	private final Runnable showChangeE2SNameDialog = () -> {
		TextInputDialog td = new TextInputDialog(accessNameEndToStart);
		td.setHeaderText("Change End to Start Access Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationEnd2StartAccessName(diagram.getID(), this.id, result.get());
			diagram.updateDiagram();
		}
	};
	
	private final Runnable showChangeE2SMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityEndToStart);
		Optional<MultiplicityDialogResult> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram.getID(), this.getName(), mr.get().convertToMultiplicity());
			diagram.updateDiagram();
		}
	};
	
	@Override
	public HeadStyle getTargetDecoration() {
		return targetFromSourceVisible?HeadStyle.ARROW:HeadStyle.NO_ARROW;
	}
	
	@Override
	public HeadStyle getSourceDecoration() {
		return sourceFromTargetVisible?HeadStyle.ARROW:HeadStyle.NO_ARROW;
	}

	public boolean isSymmetric() {return symmetric;}
	public boolean isTransitive() {return transitive;}

	@Override
	public String toString() {
		return "FmmlxAssociation [name=" + name + "]";
	}
}
