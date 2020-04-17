package tool.clients.fmmlxdiagrams.dialogs.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.dialogs.results.instancegenerator.AttributeGeneratorRandomDialogResult;

public class RandomGenerator implements ValueGenerator{

//	private String value;
	private String type;
	private List<String> generatedValue;
	
	public RandomGenerator(String string) {
		this.type = string;
	}

//	public String getValue() {
//		return value;
//	}

	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return "RANDOM";
	}

	@Override
	public void openDialog() {
//		if (value!=null){
//			List<String> values = new ArrayList<String>();
//			values.add(value);
//			AttributeGeneratorRandomDialog dlg = new AttributeGeneratorRandomDialog(getName(), type, values);
//			dialogResult(dlg);
//		} else {
//			AttributeGeneratorRandomDialog dlg = new AttributeGeneratorRandomDialog(getName(), type);
//			dialogResult(dlg);
//		}
	}

//	private void dialogResult(AttributeGeneratorRandomDialog dlg) {
//		Optional<AttributeGeneratorRandomDialogResult> opt = dlg.showAndWait();
//
//		if (opt.isPresent()) {
//			AttributeGeneratorRandomDialogResult result = opt.get();
//			if (type.equals("Integer")) {
//				this.value =  result.getValueInt().toString();
//			} else if (type.equals("Float")) {
//				this.value =  result.getValueFloat().toString();
//			} else if (type.equals("Boolean")) {
//				this.value =  result.getValueBool().toString();
//			}
//		}
//	}

	@Override
	public List<String> generate(int numberOfInstance) {
		generatedValue = new ArrayList<>();
		for(int i = 0 ; i<numberOfInstance; i++){
			generatedValue.add(generateRandomValue(type));
		}
		return generatedValue;
	}

	protected String generateRandomValue(String attributeType) {
		switch(attributeType){
			case "Integer":
				Random rd = new Random();
				return rd.nextInt()+"";
			case "Float":
				Random rd1 = new Random();
				return rd1.nextFloat()+"";
			case "Boolean":
				Random rd2 = new Random();
				Boolean bool = rd2.nextBoolean();
				return bool.toString();
			default:
				return "";
		}
	}

	@Override
	public int possibleGeneratedInstance() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		if("Boolean".equals(type)) return true;
		if("String".equals(type)) return true;
		return false;
	}

	@Override
	public String getName2() {
		return getName();
	}

	@Override
	public List<String> getValues() {
		return generatedValue;
	}

}
