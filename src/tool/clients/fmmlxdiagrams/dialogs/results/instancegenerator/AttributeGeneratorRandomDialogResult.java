package tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator;

import tool.clients.fmmlxdiagrams.dialogs.results.DialogResult;

public class AttributeGeneratorRandomDialogResult extends DialogResult {
		//For Integer
		private Integer valueInt;
		//For Float
		private Float valueFloat;
		//For Boolean
		private Boolean valueBool;

		
		public AttributeGeneratorRandomDialogResult(String value, String type) {

			if (type.equals("Boolean")) {
				this.valueBool = Boolean.parseBoolean(value);
			} else if (type.equals("Integer")) {
				this.valueInt= Integer.parseInt(value);
			} else if (type.equals("Float")) {
				this.valueFloat = Float.parseFloat(value);
			}
		}
		
		public Integer getValueInt() {
			return valueInt;
		}

		public Float getValueFloat() {
			return valueFloat;
		}

		public Boolean getValueBool() {
			return valueBool;
		}

}
