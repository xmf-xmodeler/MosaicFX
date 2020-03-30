package tool.clients.fmmlxdiagrams.dialogs.instance;

public class IncrementGenerator implements ValueGenerator{
	
	int min;
	int max;
	int step;
	
	int counter;

	@Override
	public String getName() {
		return "Increment";
	}

	@Override
	public void openDialog() {
		// TODO Auto-generated method stub
		// min = dialog.result.min()...
		counter = min;
	}

	@Override
	public String generate() {
		String result = "" + counter;
		counter ++;
		return result;
	}

	@Override
	public boolean fitsType(String type) {
		if("Integer".equals(type)) return true;
		if("Float".equals(type)) return true;
		return false;
	}

}
