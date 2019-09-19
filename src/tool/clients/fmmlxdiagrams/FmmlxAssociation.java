package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextInputDialog;
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

	FmmlxAssociation(
			Integer id,
			Integer startId,
			Integer endId,
			Integer parentAssociationId,
			Vector<Point2D> points,
			String name,
			String reverseName,
			String accessNameStartToEnd,
			String accessNameEndToStart,
			int levelStartToEnd,
			int levelEndToStart,
			Multiplicity multiplicityStartToEnd,
			Multiplicity multiplicityEndToStart,
			FmmlxDiagram diagram) {

		super(id, diagram.getObjectById(startId), diagram.getObjectById(endId), points, diagram);

		this.name = name;
		this.reverseName = reverseName;
		this.accessNameStartToEnd = accessNameStartToEnd;
		this.accessNameEndToStart = accessNameEndToStart;
		this.levelStartToEnd = levelStartToEnd;
		this.levelEndToStart = levelEndToStart;
		this.multiplicityStartToEnd = multiplicityStartToEnd;
		this.multiplicityEndToStart = multiplicityEndToStart;
		
		layout();
	}
	
	private enum Anchor {SOURCE,CENTRE,TARGET};

	private void layout() {
		createLabel(name, Anchor.CENTRE, showChangeFwNameDialog, 0);
		if(reverseName != null) 
	    createLabel(reverseName, Anchor.CENTRE, showChangeRvNameDialog, 20);
		createLabel(accessNameStartToEnd, Anchor.TARGET, showChangeS2ENameDialog, 0);
		createLabel(accessNameEndToStart, Anchor.SOURCE, showChangeE2SNameDialog, 0);
		createLabel(""+levelStartToEnd, Anchor.TARGET, showChangeS2ELevelDialog, 20);
		createLabel(""+levelEndToStart, Anchor.SOURCE, showChangeE2SLevelDialog, 20);
		createLabel(multiplicityStartToEnd.toString(), Anchor.TARGET, showChangeS2EMultDialog, 40);
		createLabel(multiplicityEndToStart.toString(), Anchor.SOURCE, showChangeE2SMultDialog, 40);
	}

	private void createLabel(String value, Anchor anchor, Runnable action, int yDiff) {
		double w = Math.max(20, diagram.calculateTextWidth(value));
		double h = diagram.calculateTextHeight();
		Vector<FmmlxObject> anchors = new Vector<>();
		if(anchor!=Anchor.TARGET) anchors.add(getSourceNode());
		if(anchor!=Anchor.SOURCE) anchors.add(getTargetNode());
		diagram.addLabel(new DiagramLabel(this, action, null, anchors, value, 50, -100+yDiff, w, h));
	}

	@Override
	public void paintOn(GraphicsContext g, int xOffset, int yOffset, FmmlxDiagram fmmlxDiagram) {
		super.paintOn(g, xOffset, yOffset, fmmlxDiagram);
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

	public int getId() {
		return id;
	}

	public FmmlxObject getSourceNode() {
		return startNode;
	}

	public FmmlxObject getTargetNode() {
		return endNode;
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


	public String toPair() {
		String firstString = this.getSourceNode().getName();
		String seconString = this.getTargetNode().getName();
		return "( " + firstString + " ; " + seconString + " )";
	}
	
	public Vector<FmmlxAssociationInstance> getInstance(){
		return diagram.getAssociationInstance();
	}	
	
	public boolean doObjectsFit(FmmlxObject source, FmmlxObject target) {
		if (source.isInstanceOf(getSourceNode(), levelEndToStart) && target.isInstanceOf(getTargetNode(), levelStartToEnd))
			return true;
		if (target.isInstanceOf(getSourceNode(), levelEndToStart) && source.isInstanceOf(getTargetNode(), levelStartToEnd))
			return true;
		return false;
	}

	@Override
	public ContextMenu getContextMenu(DiagramActions actions) {
		return new AssociationContextMenu(this, actions); //temporary
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
			diagram.changeAssociationForwardName(this.id, result.get());
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
				diagram.changeAssociationStart2EndLevel(this.id, level);
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
			diagram.changeAssociationStart2EndAccessName(this.id, result.get());
		}
	};
	
	private Runnable showChangeS2EMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityStartToEnd);
		Optional<MultiplicityDialogResult> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.changeAssociationStart2EndMultiplicity(this.id, mr.get().convertToMultiplicity());
		}
	};
	
	private Runnable showChangeE2SLevelDialog = () -> {
		TextInputDialog td = new TextInputDialog(levelEndToStart+"");
		td.setHeaderText("Change End to Start Level");
		Optional<String> result = td.showAndWait();
		if(result.isPresent()) {
			try {
				Integer level = Integer.parseInt(result.get());
				diagram.changeAssociationEnd2StartLevel(this.id, level);
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
			diagram.changeAssociationEnd2StartAccessName(this.id, result.get());
		}
	};
	
	private Runnable showChangeE2SMultDialog = () -> {
		MultiplicityDialog md = new MultiplicityDialog(multiplicityEndToStart);
		Optional<MultiplicityDialogResult> mr = md.showAndWait();
		if(mr.isPresent()) {
			diagram.changeAssociationEnd2StartMultiplicity(this.id, mr.get().convertToMultiplicity());
		}
	};
}
