package tool.xmodeler.tool_introduction;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class SucessCondition {

	private FmmlxDiagram diagram;

	public SucessCondition(FmmlxDiagram diagram) {
		super();
		this.diagram = diagram;
	}

	public boolean checkSucessCondition(FmmlxDiagram diagram) {
		switch (diagram.getViewPane().getDiagramViewState().getPrecedence()) {
		case 1:
			return isClassMovieCreated();
		case 2:
			return areAttributesAddedToMovie();
		case 3:
			return isMovieShowingCreated();
		case 4:
			return containsShownInAssoc();
		case 5:
			return hasLinkBetweenMovieAndMovieShowing();
		case 6:
			return true;

		default:
			return false;
		}
	}
	
	private boolean hasLinkBetweenMovieAndMovieShowing() {
		FmmlxLink link = FmmlxLink.getFmmlxLink(diagram, "movie1", "movieShowing1", "shown_in");
		if (link == null) {
			return false;
		}
		return true;
	}

	private boolean containsShownInAssoc() {
		FmmlxAssociation assoc = FmmlxAssociation.getFmmlxAssociation(diagram, "Movie", "MovieShowing", "shown_in");
		if (assoc == null) {
			return false;
		}
		boolean cardinalityOfMovieIsRight = assoc.getMultiplicityStartToEnd().checkForEquality(0, 2147483647);
		boolean cardinalityOfMovieShowingIsRight = assoc.getMultiplicityEndToStart().checkForEquality(1, 1);

		return cardinalityOfMovieIsRight && cardinalityOfMovieShowingIsRight;
	}

	private boolean isMovieShowingCreated() {
		if (!DiagramsConditionChecks.containsClass(diagram, "MovieShowing")) {
			return false;
		}
		Vector<FmmlxAttribute> ownAttributes = diagram.getObjectByName("MovieShowing").getOwnAttributes();
		boolean containsShowDate = false;
		for (FmmlxAttribute fmmlxAttribute : ownAttributes) {
			if (!containsShowDate) {
				containsShowDate = fmmlxAttribute.hasNameAndType("showDate", "Date");
			}
		}
		return containsShowDate;
	}

	private boolean areAttributesAddedToMovie() {
		FmmlxObject obj = diagram.getObjectByName("Movie");
		Vector<FmmlxAttribute> ownAttributes = obj.getOwnAttributes();
		boolean containsTitle = false;
		boolean containsDurationInMinutes = false;
		for (FmmlxAttribute fmmlxAttribute : ownAttributes) {
			if (!containsDurationInMinutes) {
				containsDurationInMinutes = fmmlxAttribute.hasNameAndType("durationInMinutes", "Integer");
			}
			if (!containsTitle) {
				containsTitle = fmmlxAttribute.hasNameAndType("title", "String");
			}
		}
		return containsTitle && containsDurationInMinutes;
	}

	private boolean isClassMovieCreated() {
		return DiagramsConditionChecks.containsClass(diagram, "Movie");
	}



	
}