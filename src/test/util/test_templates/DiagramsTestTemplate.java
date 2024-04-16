package test.util.test_templates;

import org.junit.jupiter.api.BeforeEach;

import tool.clients.fmmlxdiagrams.FmmlxDiagram;

/**
 * Template class that created for each test a new diagram.
 * You can request the current diagram with the getCurrentDiagram function.
 */
public class DiagramsTestTemplate extends XModelerTestTemplate {
	
	private FmmlxDiagram fmmlxDiagram;

	@BeforeEach
	public void setupDiagram() {
		fmmlxDiagram = controlCenterTestUtils.createRandomDiagram();
	}

	/**
	 * Use this function to return the current diagram
	 * @return
	 */
	public FmmlxDiagram getCurrentDiagram() {
		return fmmlxDiagram;
	}

}