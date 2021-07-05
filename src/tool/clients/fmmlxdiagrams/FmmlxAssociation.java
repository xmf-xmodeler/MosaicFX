package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.association.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.MultiplicityDialogResult;
import tool.clients.fmmlxdiagrams.menus.AssociationContextMenu;
import tool.clients.xmlManipulator.XmlHandler;

import java.util.Optional;
import java.util.Vector;

public class FmmlxAssociation extends Edge<FmmlxObject> implements FmmlxProperty {

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
	private boolean sourceFromTargetVisible;
	private boolean targetFromSourceVisible;
	private boolean symmetric;
	private boolean transitive;

//	HeadStyle sourceHead; 
//	HeadStyle targetHead;
	private final static Color TRANSPARENT = new Color(0, 0, 0, 0);
	private final static Color BLACK = new Color(0, 0, 0, 1);
	private final static Color WHITE = new Color(1, 1, 1, 1);

	FmmlxAssociation(
			String path,
			String startPath,
			String endPath,
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
			AbstractPackageViewer diagram) {

		super(path, diagram.getObjectByPath(startPath), diagram.getObjectByPath(endPath), points, startPortRegion, endPortRegion, labelPositions, diagram);

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
	}

	public Integer getParentAssociationId() {
		return parentAssociationId;
	}

	@Override protected void layoutLabels(FmmlxDiagram diagram) {
		if( sourceNode == targetNode) {
			createLabel(name, 0, Anchor.CENTRE_SELFASSOCIATION, showChangeFwNameDialog, BLACK, TRANSPARENT, diagram);
		}else {
		createLabel(name, 0, Anchor.CENTRE_MOVABLE, showChangeFwNameDialog, BLACK, TRANSPARENT, diagram);
		}
//		if(reverseName != null) 
//	    createLabel(reverseName, 1, Anchor.CENTRE, showChangeRvNameDialog, -20, BLACK, TRANSPARENT);
		
		createLabel(""+levelEnd, 2, Anchor.TARGET_LEVEL, showChangeS2ELevelDialog, WHITE, BLACK,diagram);
		createLabel(""+levelStart, 3, Anchor.SOURCE_LEVEL, showChangeE2SLevelDialog, WHITE, BLACK, diagram); 
		createLabel(multiplicityStartToEnd.toString(), 4, Anchor.TARGET_MULTI, showChangeS2EMultDialog, BLACK, TRANSPARENT, diagram);
		createLabel(multiplicityEndToStart.toString(), 5, Anchor.SOURCE_MULTI, showChangeE2SMultDialog, BLACK, TRANSPARENT, diagram);
		layoutingFinishedSuccesfully = true;
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
	
	public boolean doObjectsFit(FmmlxObject source, FmmlxObject target) {
		if ((source==null || source.isInstanceOf(getSourceNode(), levelStart)) && (target==null || target.isInstanceOf(getTargetNode(), levelEnd)))
			return true;
		return (target==null || target.isInstanceOf(getSourceNode(), levelStart)) && (source==null || source.isInstanceOf(getTargetNode(), levelEnd));
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
				diagram.getComm().changeAssociationStart2EndLevel(diagram.getID(), this.getName(), level);
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
			diagram.getComm().changeAssociationStart2EndAccessName(diagram.getID(), this.getName(), result.get());
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
				diagram.getComm().changeAssociationEnd2StartLevel(diagram.getID(), this.getName(), level);
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
			diagram.getComm().changeAssociationEnd2StartAccessName(diagram.getID(), this.getName(), result.get());
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
