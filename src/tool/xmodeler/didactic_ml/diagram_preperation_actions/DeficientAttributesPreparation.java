package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import java.util.ArrayList;
import java.util.List;

import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

public class DeficientAttributesPreparation extends DiagramPreparationActions {
	static String[][] classesList ={
				{"Customer", "lastName", "String", "firstName", "String", "phoneNumber","Integer", "age", "Integer"},
				{"Reservation", "reservationNumber", "Integer", "reservationDate", "Date", "numberOfTickets", "Integer", "totalPriceOfReservation", "Float"},
				{"Ticket", "seatNumber", "String", "ticketPrice", "Float", "isAvailable", "Boolean"},
				{"MovieShow", "date", "Date","extraCost","Float","availableSeats","Integer"},
				{"Movie", "title", "String", "movieLengthInMinutes","Integer","releaseDate", "Date", "inHD", "Boolean"},
				{"Hall", "numberOfSeats", "Integer"}
		};
	
	static Multiplicity oneToOne = new Multiplicity(1, 1, true, false, false);		
	static 	Multiplicity zeroToMany = new Multiplicity(0, 0, false, false, false);
	static 	Multiplicity oneToMany = new Multiplicity(1, 0, false, false, false);

//	static Object[][] associationList = {			//not really usable since assocs are to specific and cant all be done in a loop
//			{classesList[1][0],classesList[0][0],"belongs_to", targetToSourceMult00,sourceToTargetMult00}
//	};
			
	@Override
	public String[][] prepair(FmmlxDiagram diagram) {
		switch (SelfAssessmentTestTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 1:
			addClasses(diagram);
			addAssociations(diagram);
			return classesList;
		default:return classesList;	
		}
}
	
		private static void addClasses(FmmlxDiagram diagram) {
			for(int i = 0; i<classesList.length;i++) {
				createMetaClass(diagram,1,classesList[i][0], new int[] {i*300,0});
				for(int i2 = 1; i2<classesList[i].length;i2=i2+2) {
					createAttributeOnLevelNull(diagram, classesList[i][0], classesList[i][i2], classesList[i][i2+1]);
				}
			}
		}
		
		private static void addAssociations(FmmlxDiagram diagram) {
			addAssociationOnLevelNull(diagram, classesList[1][0], classesList[0][0], "belongs_to", zeroToMany, oneToOne);
			addAssociationOnLevelNull(diagram, classesList[2][0], classesList[1][0], "sold_in", oneToMany, oneToOne);
			addAssociationOnLevelNull(diagram, classesList[2][0], classesList[3][0], "valid_for", zeroToMany, oneToOne);
			addAssociationOnLevelNull(diagram, classesList[3][0], classesList[4][0], "shows", zeroToMany, oneToOne);
			addAssociationOnLevelNull(diagram, classesList[3][0], classesList[5][0], "shown_in", zeroToMany, oneToOne);
		}

}
