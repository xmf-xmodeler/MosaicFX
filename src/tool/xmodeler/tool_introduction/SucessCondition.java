package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

public class SucessCondition {

	private FmmlxDiagram diagram;
	private static final String MOVIE_CLASS_NAME = "Movie";

	public SucessCondition(FmmlxDiagram diagram) {
		super();
		this.diagram = diagram;
	}

	public boolean checkSucessCondition() {
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
			return containsUserDefinedLinkForSecondTicket();
		case 7:
			return isRatingEnumAdded();
		case 8:
			return isEnumDefinedAsDataType();
		case 9:
			//No user task
			return true;

		default:
			return false;
		}
	}

	private boolean isEnumDefinedAsDataType() {
		FmmlxObject obj = diagram.getObjectByName(MOVIE_CLASS_NAME);
		return DiagramConditionChecks.hasAttributeOfType(obj, "rating", "RatingEnum");
	}

	private boolean containsUserDefinedLinkForSecondTicket() {
		//idea check for not having issues. Normally by correct set cardinalities there should be an issue if i can check for this it would be easier.
		//problem the creation of the other objects and links is not checked right now
		FmmlxAssociation assoc = FmmlxAssociation.getFmmlxAssociation(diagram, "Customer", "Ticket", "buys");
		boolean assocCardinalitRight = assoc.getMultiplicityStartToEnd().checkForEquality(0, 2147483647);
	
		String objectName = "ticket2";
		FmmlxObject obj = diagram.getObjectByName(objectName);
		boolean priceNotNull = !DiagramConditionChecks.hasMatchingSlotValue(diagram, objectName, "price", "0.0");
		
		return 
				assocCardinalitRight && 
					priceNotNull &&
						DiagramConditionChecks.containsLink(diagram, "customer1", "ticket2", "buys");
	}

	private boolean isRatingEnumAdded() {
		FmmlxEnum enumInst = diagram.getEnum("RatingEnum");
		if (enumInst == null) {
			return false;
		}
		boolean enumContainsG = enumInst.contains("G");
		boolean enumContainsPG13 = enumInst.contains("PG_13");
		boolean enumContainsR = enumInst.contains("R");
		return enumContainsG && enumContainsPG13 && enumContainsR;
	}

	private boolean hasLinkBetweenMovieAndMovieShowing() {
		//precondition
		if (hasMovieInstanceJoker() && 	hasMovieShowingInstance()) {
			return DiagramConditionChecks.containsLink(diagram, "movie1", "movieShowing1", "shown_in" );			
		}
		return false;
	}

	private boolean hasMovieShowingInstance() {
		return DiagramConditionChecks.hasMatchingSlotValue(diagram, "movieShowing1", "showDate", "07 Nov 2024" );
	}

	private boolean hasMovieInstanceJoker() {
		return DiagramConditionChecks.hasMatchingSlotValue(diagram, "movie1", "title", "Joker")
				&& DiagramConditionChecks.hasMatchingSlotValue(diagram, "movie1", "durationInMinutes", "122");
	}

	private boolean containsShownInAssoc() {
		FmmlxAssociation assoc = FmmlxAssociation.getFmmlxAssociation(diagram, MOVIE_CLASS_NAME, "MovieShowing", "shown_in");
		if (assoc == null) {
			return false;
		}
		boolean cardinalityOfMovieIsRight = assoc.getMultiplicityStartToEnd().checkForEquality(0, 2147483647);
		boolean cardinalityOfMovieShowingIsRight = assoc.getMultiplicityEndToStart().checkForEquality(1, 1);

		return cardinalityOfMovieIsRight && cardinalityOfMovieShowingIsRight;
	}

	private boolean isMovieShowingCreated() {
		FmmlxObject obj = diagram.getObjectByName("MovieShowing");
		return DiagramConditionChecks.containsClass(diagram, "MovieShowing") &&
				DiagramConditionChecks.hasAttributeOfType(obj, "showDate", "Date");
	}

	private boolean areAttributesAddedToMovie() {
		//create of movie has been checked in state before
		FmmlxObject obj = diagram.getObjectByName(MOVIE_CLASS_NAME);
		boolean containsTitle = DiagramConditionChecks.hasAttributeOfType(obj, "title", "String");
		boolean containsDurationInMinutes = DiagramConditionChecks.hasAttributeOfType(obj, "durationInMinutes", "Integer");
		return containsTitle && containsDurationInMinutes;
	}

	private boolean isClassMovieCreated() {
		return DiagramConditionChecks.containsClass(diagram, MOVIE_CLASS_NAME);
	}
}