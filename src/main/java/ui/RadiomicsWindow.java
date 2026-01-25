package ui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import common.RadiomicsPipeline;
import common.RadiomicsSettings;
import ij.WindowManager;


import io.github.tatsunidas.radiomics.main.RadiomicsJ;

/**
 * 
 * 1. radiomics classifier - dataset is built-up by roi by roi.
 * 
 * 2. radiomics segmentation - dataset is built-up by pixel by pixel.
 * 
 * @author tatsunidas
 *
 */
public class RadiomicsWindow extends JFrame {

	private static final long serialVersionUID = -8494940884028066246L;

	static RadiomicsJ radiomics = new RadiomicsJ();
	RadiomicsPanel panel;
	RadiomicsSettings textureParams;
	RadiomicsBatchModePanel batchPanel;
	RadiomicsPipeline pipeline;

	RadiomicsVisualizationPanel visPanel;

	public static void main(String[] args) {
		new RadiomicsWindow();
	}

	public RadiomicsWindow() {
		pipeline = new RadiomicsPipeline();
		buildGUI();
	}

	private void buildGUI() {
		JTabbedPane tabPane = new JTabbedPane();
		panel = new RadiomicsPanel(this);
		tabPane.addTab("Operation", panel);
		textureParams = new RadiomicsSettings();
		tabPane.addTab("TextureParams", textureParams);
		batchPanel = new RadiomicsBatchModePanel(textureParams);
		tabPane.addTab("Batch Execution", batchPanel);
		visPanel = new RadiomicsVisualizationPanel(textureParams);
		tabPane.addTab("Visualization Map", visPanel);
		add(tabPane, BorderLayout.CENTER);
		pack();
		if (WindowManager.getCurrentWindow() == null) {
			setLocationRelativeTo(null);
		} else {
			setLocationRelativeTo(WindowManager.getCurrentWindow());
		}
		setTitle("Machine Learning & Radimics Feature Calculator");

		URL url = getClass().getResource("/RadiomicsJ_icon.png");

		// ファイルが見つからない場合のチェック（重要）
		if (url == null) {
			System.err.println("Icon image file not found. :" + "/RadiomicsJ_icon.png");
			return;
		}

		try {
			// 画像を読み込む
			BufferedImage image = ImageIO.read(url);
			setIconImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSize(900, 600);
		setVisible(true);
		textureParams.adjustDividerLocation();
	}

	public RadiomicsPipeline getPipeline() {
		return pipeline;
	}

	public Properties getRadiomicsSettingsAsProp() {
		return textureParams.currentSettings();
	}

	public RadiomicsSettings getRadiomicsSettings() {
		return textureParams;
	}

	public void loadRadiomicsSettings() {
		textureParams.loadSettings();
	}
}
