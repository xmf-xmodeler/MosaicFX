package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.dialogs.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.menus.AssociationContextMenu;

import java.util.Optional;
import java.util.Vector;

public class FmmlxAssociation extends Edge<FmmlxObject> implements FmmlxProperty {

	private final PropertyType propertyType = PropertyType.Association;
	private String name;
	private final String typePath;
	private String accessNameStartToEnd;
	private String accessNameEndToStart;
	private Integer levelStart;
	private Integer levelEnd;
	private final String parentAssociationId;
	private Multiplicity multiplicityStartToEnd;
	private Multiplicity multiplicityEndToStart;
	private boolean sourceFromTargetVisible;
	private boolean targetFromSourceVisible;
	private boolean symmetric;
	private boolean transitive;

//	HeadStyle sourceHead; 
//	HeadStyle targetHead;
	private final static Color TRANSPARENT = null;//new Color(0, 0, 0, 0);
	private final static Color BLACK = new Color(0, 0, 0, 1);
	private final static Color WHITE = new Color(1, 1, 1, 1);

	FmmlxAssociation(
			String path,
			String startPath,
			String endPath,
			String parentAssociationId,
			Vector<Point2D> points,
			PortRegion startPortRegion, PortRegion endPortRegion,
			String name,
			String typePath,
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
		this.typePath = typePath;
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

	public String getParentAssociationId() {
		return parentAssociationId;
	}

	@Override public void layoutLabels(FmmlxDiagram diagram) {
		String text = name;
		if(parentAssociationId != null && !"".equals(parentAssociationId)) {
			text += " depends on " + parentAssociationId;
		}
		if( sourceNode == targetNode) {
			createLabel(text, 0, Anchor.CENTRE_SELFASSOCIATION, showChangeFwNameDialog, BLACK, TRANSPARENT, diagram);
		}else {
			createLabel(text, 0, Anchor.CENTRE_MOVABLE, showChangeFwNameDialog, BLACK, TRANSPARENT, diagram);
		}
//		if(reverseName != null) 
//	    createLabel(reverseName, 1, Anchor.CENTRE, showChangeRvNameDialog, -20, BLACK, TRANSPARENT);
		
		if(!diagram.umlMode) {	//Have to be hidden for uml Diagrams
		createLabel(""+levelEnd, 2, Anchor.TARGET_LEVEL, showChangeS2ELevelDialog, WHITE, BLACK,diagram);
		createLabel(""+levelStart, 3, Anchor.SOURCE_LEVEL, showChangeE2SLevelDialog, WHITE, BLACK, diagram); 
		}
		createLabel(multiplicityStartToEnd.toString(), 4, Anchor.TARGET_MULTI, showChangeS2EMultDialog, BLACK, TRANSPARENT, diagram);
		createLabel(multiplicityEndToStart.toString(), 5, Anchor.SOURCE_MULTI, showChangeE2SMultDialog, BLACK, TRANSPARENT, diagram);
		layoutingFinishedSuccesfully = true;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public AssociationType getAssociationType() {
		for(AssociationType type : diagram.associationTypes) {
			if(this.typePath.equals(type.path)) return type;
		}
		return null;
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
	protected Color getPrimaryColor() {
		try{
			AssociationType type = getAssociationType();
			String s = type.color;
			return Color.web(s);
		} catch (Exception e) {
//		  System.err.println("getPrimaryColor FAIL: " + e.getMessage());
		}
		return Color.BLACK;
	}

	@Override
	protected double getStrokeWidth() {
		try{
			AssociationType type = getAssociationType();
			return 1. * type.strokeWidth;
		} catch (Exception e) {
//		  System.err.println("getStrokeWidth FAIL: " + e.getMessage());
		}
		return 1.;
	}

	@Override
	protected double[] getLineDashes() {
		try{
			AssociationType type = getAssociationType();
			if("".equals(type.dashArray)) return new double[]{};
			String[] dashesS = type.dashArray.split(",");
			double[] dashes = new double[dashesS.length];
			for(int i = 0; i < dashesS.length; i++) {
				dashes[i] = Double.parseDouble(dashesS[i]);
			}
			return dashes;
		} catch (Exception e) {
//			System.err.println("getLineDashes FAIL: " + e.getMessage());
			return new double[]{};
		}
	}

	private final Runnable showChangeFwNameDialog = () -> {
		TextInputDialog td = new TextInputDialog(name);
		td.setHeaderText("Change Forward Association Name");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().changeAssociationForwardName(diagram.getID(), this, result.get());
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
				diagram.getComm().changeAssociationStart2EndLevel(diagram.getID(), this, level);
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
			diagram.getComm().changeAssociationStart2EndAccessName(diagram.getID(), this, result.get());
			diagram.updateDiagram();
		}
	};
	
	private final Runnable showChangeS2EMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityStartToEnd);
		Optional<Multiplicity> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.getComm().changeAssociationStart2EndMultiplicity(diagram.getID(), this, mr.get());
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
				diagram.getComm().changeAssociationEnd2StartLevel(diagram.getID(), this, level);
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
			diagram.getComm().changeAssociationEnd2StartAccessName(diagram.getID(), this, result.get());
			diagram.updateDiagram();
		}
	};
	
	private final Runnable showChangeE2SMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityEndToStart);
		Optional<Multiplicity> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram.getID(), this, mr.get());
			diagram.updateDiagram();
		}
	};
	
	@Override
	public HeadStyle getTargetDecoration() {
		if(diagram.umlMode) {
		if(targetFromSourceVisible & sourceFromTargetVisible) {
			return HeadStyle.NO_ARROW;	//No Arrows for Bidirctional associations
		}}
		return targetFromSourceVisible?HeadStyle.ARROW:HeadStyle.NO_ARROW;
	}
	
	@Override
	public HeadStyle getSourceDecoration() {
		if(diagram.umlMode) {
		if(targetFromSourceVisible & sourceFromTargetVisible) {
			return HeadStyle.NO_ARROW;	//No Arrows for Bidirctional associations
		}}
		return sourceFromTargetVisible?HeadStyle.ARROW:HeadStyle.NO_ARROW;
	}

	public boolean isSymmetric() {return symmetric;}
	public boolean isTransitive() {return transitive;}

	@Override
	public String toString() {
		return "FmmlxAssociation [name=" + name + "]";
	}
	
	public static FmmlxAssociation getFmmlxAssociation(FmmlxDiagram diagram, String source, String target, String name) {
		Vector<FmmlxAssociation> associations = diagram.getFmmlxAssociations();

		for (FmmlxAssociation assoc : associations) {
			if (assoc.sourceNode.name.equals(source)
					&& (assoc.getTargetNode().name.equals(target)) &&
						assoc.name.equals(name)) {
				return assoc;
			}
		}
		return null;
	}

	public boolean isDependent() {
		// TODO Auto-generated method stub
		return !(parentAssociationId == null || "".equals(parentAssociationId));
	}
}
