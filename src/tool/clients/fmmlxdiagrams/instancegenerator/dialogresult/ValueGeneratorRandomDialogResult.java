package tool.clients.fmmlxdiagrams.instancegenerator.dialogresult;

import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

import java.util.ArrayList;
import java.util.List;

public class ValueGeneratorRandomDialogResult extends DialogResult {

		private String type;
		private String scenario;
		private List<String> parameter;

		public ValueGeneratorRandomDialogResult(String type, String scenario, List<String> parameter) {
			this.scenario = scenario;
			this.type = type;
			this.parameter = parameter;
		}

		public String getSelectedScenario() {
			return scenario;
		}

		public List<String> getParameter(){
			return parameter;
		}

}
