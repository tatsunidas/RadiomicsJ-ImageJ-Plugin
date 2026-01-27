import javax.swing.SwingUtilities;

import ij.ImagePlus;
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
	
	public static void debug() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Class<?> clazz = RadiomicsJ_ImageJPlugin.class;
				new ij.ImageJ();
				ImagePlus image = ij.IJ.openImage("http://imagej.net/images/t1-head.zip");
		        if (image != null) {
		            image.show();
		        }
		        // 4. プラグインを即座に実行する
		        //    IJ.runPlugIn("クラスの完全修飾名", "引数(あれば)");
		        ij.IJ.runPlugIn(clazz.getName(), "");
//				new RadiomicsJ_ImageJPlugin().run(null);
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
