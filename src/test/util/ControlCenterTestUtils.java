package test.util;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.xmodeler.ControlCenterClient;

/**
 * Helper function to offer standard ControlCenter interactions.
 * 
 * Please make sure to only use this class for the diagram creation otherwise it would lead to errors in the diagram returns.
 */
public class ControlCenterTestUtils {
	private ControlCenterClient controlCenterClient = new ControlCenterClient();
	static int diagramCounter = 0;
	
	/**
	 * Creates new project and updates ControlCenter to show all available projects in backend 
	 * @param name of project to be created 
	 */
	public void createProject(String projectName) {
		controlCenterClient.createProject(projectName);
		controlCenterClient.getAllProjects();
		TestUtils.waitWithoutCatch(3000);
	}
	
	/**
	 * Create project with random 6 digits id
	 * @return projectName
	 */
	public String createRandomProject() {
		String projectName = TestUtils.getShortRandomId();
		createProject(projectName);
		return projectName;
	}
	
	/**
	 * creates random FmmlxDiagram with 6 digits name in a random project
	 * @return diagram that was created
	 */
	public FmmlxDiagram createRandomDiagram() {
		String projectName = createRandomProject();
		return createFmmlxDiagram(projectName, TestUtils.getShortRandomId());
	}
	

		
	/**
	 * Interface offered to the testClasse.
	 * 
	 * @param the boolean value decides if first a project is created.
	 */
	public FmmlxDiagram createFmmlxDiagram(String projectName, String diagramName, boolean projectAlreadyCreated) {
		if (!projectAlreadyCreated) {
			createProject(projectName);
		}
		return createFmmlxDiagram(projectName, diagramName);
	}

	/**
	 * Created diagram in XMF. For the specification of the diagram watch the backendCall.
	 * 
	 * @param projectName
	 * @param diagramName
	 * @return diagram to be created
	 */
	private FmmlxDiagram createFmmlxDiagram(String projectName, String diagramName) {
		FmmlxDiagramCommunicator.getCommunicator().createDiagram(projectName, diagramName, "",
				FmmlxDiagramCommunicator.DiagramType.ClassDiagram, false,(i) -> {});
		//it could happen, that if there are several diagrams the wait time needs to be longer.
		//we should may calculate it on the diagram counter
		TestUtils.waitWithoutCatch(3000);
		int diagramId = diagramCounter;
		diagramCounter++;
		//A diagram is only represented in the Java if it was first time opened.
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);
		TestUtils.waitWithoutCatch(3000);
		return FmmlxDiagramCommunicator.getCommunicator().getDiagram(diagramId);
	}
}