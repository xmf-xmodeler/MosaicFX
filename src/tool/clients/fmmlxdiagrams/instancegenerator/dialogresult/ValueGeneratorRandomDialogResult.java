package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.List;

public class ValueGeneratorRandomDialogResult extends DialogResult {

		private String attributeType;
		private String scenario;
		private List<String> parameter;

		public ValueGeneratorRandomDialogResult(String attributeType, String scenario, List<String> parameter) {
			this.scenario = scenario;
			this.attributeType = attributeType;
			this.parameter = parameter;
		}

		public String getSelectedScenario() {
			return scenario;
		}
		public List<String> getParameter(){
			return parameter;
		}

		public String getAttributeType() {
			return attributeType;
		}
}
