package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import ij.IJ;

/**
 * save and load props on user.home/RadiomicsJ_IJ_Plugin.properties
 * @author tatsunidas
 *
 */
public class ConfigManager {

	private static final String CONFIG_FILE_NAME = "RadiomicsJ_IJ_Plugin.properties";

	private static File getConfigFile() {
		String userHome = System.getProperty("user.home");
		return new File(userHome, CONFIG_FILE_NAME);
	}

	public static void saveProp(Properties config) {
		File file = getConfigFile();
		try (FileOutputStream out = new FileOutputStream(file)) {
			config.store(out, "RadiomicsJ Configuration");
			IJ.log("Saved config to: " + file.getAbsolutePath());
		} catch (IOException e) {
			IJ.log("Failed to save config: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Properties loadProp() {
		Properties config = new Properties();
		File file = getConfigFile();
		if (file.exists()) {
			try (FileInputStream in = new FileInputStream(file)) {
				config.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return config;
	}
}
