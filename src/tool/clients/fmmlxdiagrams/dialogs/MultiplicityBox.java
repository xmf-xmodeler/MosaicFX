package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Optional;

import javafx.scene.control.Button;
import tool.clients.fmmlxdiagrams.Multiplicity;

public class MultiplicityBox extends Button {
	private Multiplicity multi;
	
	public MultiplicityBox(Multiplicity multi) {
		super("");
		setMultiplicity(multi);
		setOnAction(e -> {
			Multiplicity newMulti = showMultiplicityDialog(getMultiplicity());
			if(newMulti != null) setMultiplicity(newMulti);
		});
	}
	
	public MultiplicityBox() {
		super("");
		setOnAction(e -> {
			Multiplicity newMulti = showMultiplicityDialog(getMultiplicity());
			if(newMulti != null) setMultiplicity(newMulti);
		});
	}

	public final Multiplicity getMultiplicity() { return multi; }
	public final void setMultiplicity(Multiplicity multi) { this.multi = multi; setText(multi.toString());}
	
	private Multiplicity showMultiplicityDialog(Multiplicity multiplicity) {
		MultiplicityDialog dlg = new MultiplicityDialog(multiplicity);
		Optional<Multiplicity> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			Multiplicity result = opt.get();

			return result;
		}
		
		return null;
	}
}