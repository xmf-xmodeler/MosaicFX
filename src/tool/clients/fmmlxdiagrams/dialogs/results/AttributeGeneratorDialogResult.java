package tool.clients.fmmlxdiagrams.dialogs.results;

import java.util.ArrayList;
import java.util.List;

import tool.clients.fmmlxdiagrams.dialogs.instance.InstanceGeneratorGenerateType;

public class AttributeGeneratorDialogResult extends DialogResult {
	
	private InstanceGeneratorGenerateType gType;
	
	//For List 
		//For Integer
		private List<Integer> intValues = new ArrayList<Integer>();
		private List<Float> floatValues = new ArrayList<Float>();
		private List<Boolean> boolValues = new ArrayList<Boolean>();
		private List<String> stringValues = new ArrayList<String>();
	
	//For Static and Random
		
		//For Integer
		private Integer valueInt;
		//For Float
		private Float valueFloat;
		//For Boolean
		private Boolean valueBool;
		//For String
	
	//For Increment
		
		private String valueStart;
		private String valueEnd;
		private String increment;

		
	//Value For String
		private String valueString;		
		
		//Generator Increment
		public AttributeGeneratorDialogResult(String value1, String value2, String increment, String type, InstanceGeneratorGenerateType gType) {
			this.gType = gType;		
			this.valueStart = value1;
			this.valueEnd = value2;
			this.increment = increment;
		}
		
		//Generator Static and Random
		public AttributeGeneratorDialogResult(String value, String type, InstanceGeneratorGenerateType gType) {
			this.gType = gType;
			if (type.equals("Boolean")) {
				this.valueBool = Boolean.parseBoolean(value);
			} else if (type.equals("String")) {
				this.valueString = value;
			} else if (type.equals("Integer")) {
				this.valueInt= Integer.parseInt(value);
			} else if (type.equals("Float")) {
				this.valueFloat = Float.parseFloat(value);
			}
		}
		
		//Generator List and Random
		public AttributeGeneratorDialogResult(List<String> values, String type, InstanceGeneratorGenerateType gType) {
			this.gType=gType;
			if(type.equals("Integer")) {
				for(String str : values) {
					intValues.add(Integer.parseInt(str));
				}
			} else if (type.equals("Float")) {
				for(String str : values) {
					floatValues.add(Float.parseFloat(str));
				}
			} else if (type.equals("Boolean")) {
				for(String str : values) {
					boolValues.add(Boolean.parseBoolean(str));
				}
			} else if (type.equals("String")) {
				stringValues = values;
			} 
		}

		public InstanceGeneratorGenerateType getgType() {
			return gType;
		}

		public String getValueStart() {
			return valueStart;
		}

		public String getValueEnd() {
			return valueEnd;
		}

		public String getIncrement() {
			return increment;
		}

		public Boolean getValueBool() {
			return valueBool;
		}

		public String getValueString() {
			return valueString;
		}

		public List<Integer> getIntValues() {
			return intValues;
		}

		public List<Float> getFloatValues() {
			return floatValues;
		}

		public List<Boolean> getBoolValues() {
			return boolValues;
		}

		public List<String> getStringValues() {
			return stringValues;
		}

		public Integer getValueInt() {
			return valueInt;
		}

		public Float getValueFloat() {
			return valueFloat;
		}
		
}
