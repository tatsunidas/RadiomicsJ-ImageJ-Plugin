import javax.swing.SwingUtilities;

import ij.plugin.PlugIn;
import ui.RadiomicsWindow;

/**
 * 
 * @author tatsunidas
 *
 */
public class RadiomicsJ_ImageJPlugin implements PlugIn{

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new RadiomicsJ_ImageJPlugin().run(null);
			}
		});
	}

	@Override
	public void run(String arg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new RadiomicsWindow();
			}
		});
	}

}
