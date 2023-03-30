package tool.helper.FXAuxilary;

import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class JavaFxTooltipAuxilary {

	public static Tooltip addTooltip(Control control, String text) {
		Tooltip tooltip = new Tooltip(text);
//		tooltip.setShowDelay(Duration.millis(100));
		control.setTooltip(tooltip);
		return tooltip;
	}
}