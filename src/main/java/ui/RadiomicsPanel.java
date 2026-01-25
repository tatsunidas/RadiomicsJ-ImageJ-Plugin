package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import common.RadiomicsPipeline;
import common.RadiomicsSettings;
import common.SettingsContext;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import ij.io.SaveDialog;
import ij.plugin.frame.RoiManager;
import ij.util.DicomTools;
import weka.classifiers.Classifier;
import weka.core.SerializationHelper;
import weka.gui.GUIChooserApp;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyPanel;

/**
 * 
 * @author tatsunidas
 *
 */
public class RadiomicsPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//model
	JButton trainModelBtn;
	JCheckBox autoImputation;
	JCheckBox balance;
	JCheckBox autoFeatureSelect;
	//inference
	JButton predBtn;
	JButton showResultsBtn;
	JButton saveResultsBtn;
	//model config
	JButton saveConfigBtn;
	JButton loadConfigBtn;
	
	JPanel classListPanel;
	JButton addClassBtn;
	JButton deleteClassBtn;
	
	GenericObjectEditor m_ClassifierEditor;
	
	/**
	 * weka, to manipulate dataset csv.
	 */
	JButton wekaBtn;
	
	RoiManager rm = RoiManager.getInstance();
	
	//command names
	private final String SAVE_CONFIG = "Save Configurations";
	private final String LOAD_CONFIG = "Load Configurations";
	private final String TRAIN_MODEL = "Train model";
	private final String IMPUTE = SettingsContext.IMPUTE;
	private final String BALANCE = SettingsContext.BALANCE;
	private final String FEATURE_SELECT = SettingsContext.FEATURE_SELECT;
	private final String PREDICTION = "Prediction";
	private final String SHOW_RESULTS = "Show Results";
	private final String SAVE_RESULTS = "Save Results";
	private final String WEKA = "WEKA";
	private final String ADD_CLASS = "Add New Class";
	private final String DELETE_CLASS = "Delete Class";
	
	final String[] defaultClasses = new String[] {"class1","class2"};
	List<ClassPanel> classes = new ArrayList<>();
	
	final RadiomicsWindow radWin;
	RadiomicsPipeline pipeline;
	
	ImagePlus pred;
	
	public RadiomicsPanel(RadiomicsWindow radW) {
		this.radWin = radW;
		setPipeline(radW.getPipeline());
		initBtns();
		buildGUI();
	}
	
	private void initBtns() {
		
		trainModelBtn = new JButton(TRAIN_MODEL);
		trainModelBtn.setActionCommand(TRAIN_MODEL);
		setAction(trainModelBtn);
		
		predBtn = new JButton(PREDICTION);
		predBtn.setActionCommand(PREDICTION);
		showResultsBtn = new JButton(SHOW_RESULTS);
		showResultsBtn.setActionCommand(SHOW_RESULTS);
		saveResultsBtn = new JButton(SAVE_RESULTS);
		saveResultsBtn.setActionCommand(SAVE_RESULTS);
		setAction(predBtn);
		setAction(showResultsBtn);
		setAction(saveResultsBtn);
		
		saveConfigBtn = new JButton(SAVE_CONFIG);
		saveConfigBtn.setActionCommand(SAVE_CONFIG);
		loadConfigBtn = new JButton(LOAD_CONFIG);
		loadConfigBtn.setActionCommand(LOAD_CONFIG);
		setAction(saveConfigBtn);
		setAction(loadConfigBtn);
		
		wekaBtn = new JButton(WEKA);
		wekaBtn.setActionCommand(WEKA);
		setAction(wekaBtn);
		
	}

	private void buildGUI() {
		setLayout(new BorderLayout());
		//functions
		JPanel func = buidFunctionPanel();
		JPanel trainds = buildTrainingDataPanel();
		JPanel center = new JPanel(new GridLayout(0, 2, 3, 3));
		center.add(func);
		center.add(trainds);
		
		add(center, BorderLayout.CENTER);
		setPreferredSize(new Dimension(730, 500));
	}
	
	private JPanel buidFunctionPanel() {
		JPanel westPanel = new JPanel();
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
		
		Border b = BorderFactory.createSoftBevelBorder(BevelBorder.RAISED, Color.ORANGE, Color.GRAY);
		
		JPanel model = new JPanel();
		model.setLayout(new GridLayout(3, 1));
		model.add(trainModelBtn);
		JPanel modelNameP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		modelNameP.add(new JLabel("Model:"));
		// Add Weka panel for selecting the classifier and its options
		m_ClassifierEditor = new GenericObjectEditor();
		m_ClassifierEditor.setClassType(Classifier.class);
		m_ClassifierEditor.setValue(pipeline.getClassifier());
		m_ClassifierEditor.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				initClassifier();
			}
		});
		PropertyPanel m_CEPanel = new PropertyPanel(m_ClassifierEditor);
		modelNameP.add(m_CEPanel);
		model.add(modelNameP);
		
		//
		autoImputation = new JCheckBox("IMPUTE");//here, just set it, without RadiomicsJ. prefix.
		autoImputation.setToolTipText("Impute by mean");
		balance = new JCheckBox("BALANCE");
		balance.setToolTipText("Do balancing (to be even number of instances in classes)");
		autoFeatureSelect = new JCheckBox("FEATURE_SELECT");
		autoFeatureSelect.setToolTipText("Drop useless&multicorr, then, will select by using LASSO");
		autoImputation.setSelected(true);
		balance.setSelected(true);
		autoFeatureSelect.setSelected(true);
		JPanel modelSettingP = new JPanel(new GridLayout(1, 3));
		modelSettingP.add(autoImputation);
		modelSettingP.add(balance);
		modelSettingP.add(autoFeatureSelect);
		model.add(modelSettingP);
		
		model.setBorder(BorderFactory.createTitledBorder(b, "Model", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
		westPanel.add(model);
		
		JPanel inference = new JPanel();
		inference.setLayout(new GridLayout(0, 1, 0, 5));
		inference.setBorder(BorderFactory.createTitledBorder(b, "Inference", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
		inference.add(predBtn);
		inference.add(showResultsBtn);
		inference.add(saveResultsBtn);
		westPanel.add(inference);
		
		JPanel settings = new JPanel();
		settings.setLayout(new GridLayout(0, 1, 0, 5));
		settings.setBorder(BorderFactory.createTitledBorder(b, "Settings", TitledBorder.CENTER,
				TitledBorder.DEFAULT_JUSTIFICATION));
		settings.add(loadConfigBtn);
		settings.add(saveConfigBtn);
		westPanel.add(settings);
		
		JPanel wekaP = new JPanel();
		wekaP.setLayout(new GridLayout(0, 1, 0, 5));
		wekaP.setBorder(BorderFactory.createTitledBorder(b, "Data science", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION));
		wekaP.add(wekaBtn);
		westPanel.add(wekaP);
		
		return westPanel;
	}
	
	private JPanel buildTrainingDataPanel() {
		JPanel classListPanelBase = new JPanel(new BorderLayout());
		classListPanel = new JPanel();
		classListPanel.setLayout(new BoxLayout(classListPanel, BoxLayout.Y_AXIS));
		JScrollPane classScroll = new JScrollPane(classListPanel);
		Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.cyan, Color.DARK_GRAY);
		classScroll.setBorder(BorderFactory.createTitledBorder(b, "Training dataset", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		
		for(String name: defaultClasses) {
			ClassPanel cp = (ClassPanel) createNewClass(name);
			classListPanel.add(cp);
			classes.add(cp);
		}
		
		classListPanelBase.setMinimumSize(new Dimension(50, 50));
		classListPanel.setPreferredSize(new Dimension(210, 500));
		classListPanelBase.add(classScroll, BorderLayout.CENTER);
		
		//buttons
		addClassBtn = new JButton(ADD_CLASS);
		addClassBtn.setActionCommand(ADD_CLASS);
		setAction(addClassBtn);
		deleteClassBtn = new JButton(DELETE_CLASS);
		deleteClassBtn.setActionCommand(DELETE_CLASS);
		setAction(deleteClassBtn);
		JPanel btnPanel = new JPanel(new GridLayout(1, 2));
		btnPanel.add(addClassBtn);
		btnPanel.add(deleteClassBtn);
		classListPanelBase.add(btnPanel, BorderLayout.SOUTH);
		
		return classListPanelBase;
	}
	
	private ClassPanel createNewClass(String name) {
		int new_index = classes.size();
		return new ClassPanel(new_index, name);
	}
	
	public void addNewClass(String name) {
		if(isDuplicateName(name)) {
			JOptionPane.showConfirmDialog(null, "This class already exists !");
			return;
		}
		ClassPanel cp = (ClassPanel) createNewClass(name);
		classListPanel.add(cp);
		classListPanel.revalidate();
		classListPanel.repaint();
		classes.add(cp);
	}
	
	private ClassPanel getClassPanel(String name) {
		for(ClassPanel cp : classes) {
			if(cp.name().equals(name)) {
				return cp;
			}
		}
		return null;
	}
	
	public void deleteClass(String name) {
		ClassPanel cp = getClassPanel(name);
		classListPanel.remove(cp);
		classListPanel.revalidate();
		classListPanel.repaint();
		classes.remove(cp);
	}
	
	private String[] getClassNames() {
		String[] names = new String[classes.size()];
		int i = 0;
		for(ClassPanel cp : classes) {
			names[i] = cp.name();
			i++;
		}
		return names;
	}
	
	public HashMap<String/*className*/, List<Roi>> getRois(){
		HashMap<String, List<Roi>> rois = new HashMap<>();
		for(ClassPanel cp:classes) {
			rois.put(cp.name(), cp.getRois());
		}
		return rois;
	}
	
	public void setPipeline(RadiomicsPipeline pipe) {
		this.pipeline = pipe;
	}
	
	public void initClassifier() {
		Object clf = m_ClassifierEditor.getValue();
		pipeline = pipeline.modelIs((Classifier)clf);
	}
	
	/**
	 * Operation specific information.
	 * @param prop
	 * @return
	 */
	private Properties addModelConfiguration(Properties prop) {
		prop.setProperty(BALANCE, String.valueOf(balance.isSelected()));
		prop.setProperty(FEATURE_SELECT, String.valueOf(autoFeatureSelect.isSelected()));
		prop.setProperty(IMPUTE, String.valueOf(autoImputation.isSelected()));
		return prop;
	}
	
	public void saveConfigration() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select folder");
		int userSelection = chooser.showOpenDialog(this);
		// ユーザーが「開く」ボタンを押したかどうかをチェック
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File selectedDirectory = chooser.getSelectedFile();
			saveRois(getRois(), selectedDirectory);
			// feature calculation settings
			Properties prop = radWin.getRadiomicsSettingsAsProp();
			// pipeline settings
			prop = addModelConfiguration(prop);
			// save to ImageJ.Prefs.
			saveProp(prop);
			pipeline.saveModel(selectedDirectory.getAbsolutePath() + File.separator + "model");
			/**
			 * train_dataset is used to build a instance for prediction.
			 */
			pipeline.saveDatasetARFF(selectedDirectory.getAbsolutePath() + File.separator + "traindataset");
		} else {
			System.out.println("フォルダ選択がキャンセルされました。");
		}
	}
	
	/**
	 * Save rois as .roi.
	 * @param roiset
	 * @param dir
	 */
	public void saveRois(HashMap<String, List<Roi>> roiset, File dir) {
		File saveTo = new File(dir.getAbsolutePath()+File.separator+"ROI");
		for(String className : roiset.keySet()) {
			File saveTo_ = new File(saveTo.getAbsolutePath()+File.separator+className);
			saveTo_.mkdirs();
			List<Roi> rois = roiset.get(className);
			int itr = 1;
			for(Roi ro : rois) {
				if(ro !=null) {
					RoiEncoder.save(ro, saveTo_.getAbsolutePath()+File.separator+ro.getName()+"_"+className+"_"+itr+".roi");
				}
				itr++;
			}
		}
	}
	
	public void saveProp(Properties config) {
		for(Object key : config.keySet()) {
			String k = (String)key;
			String v = config.getProperty(k);
			Prefs.set(k, v);
		}
		Prefs.savePreferences();
	}
	
	
	public void loadConfiguration() {
		
		//load context from Prefs.
		loadConfigurationSettingProps();
		
		//load others.
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select folder");
		int userSelection = chooser.showOpenDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File selectedDirectory = chooser.getSelectedFile();
			File[] files = selectedDirectory.listFiles();
			for (File f : files) {
				if (f.getName().equals("ROI")) {
					/**
					 * 現バージョンではtrainWith()が必要になる。
					 * ROIのロードは一旦取りやめる。
					 */
//					loadRois(f);
				} else if (f.getName().endsWith(".model")) {
					loadModel(f);
				}else if (f.getName().endsWith(".arff")) {
					loadDatasetARFF(f);
				}
			}
		} else {
			System.out.println("Cancel loading setting files(.model etc).");
		}
	}
	
	/**
	 * 関連のあるシリーズが解析対象だった場合にのみ、
	 * クラスパネルへROIをロードする。
	 * 
	 * @param roiDir
	 */
	public void loadRois(File roiDir) {
//		System.out.println("Start LOAD ROI...");
		if (!pipeline.isImageReady()) {
			System.out.println("ImagePlus is not ready, can not load rois.");
			return;
		}
		
		ImagePlus prap = pipeline.getImagePlus();
		
		// init classPanels ? 
		//initClassPanels();
		/*
		 * load rois
		 * DOES NOT import to graphy even if not exists.
		 */
		File classes[] = roiDir.listFiles();
		if(classes == null || classes.length == 0) {
			return;
		}
		int choice = Integer.MAX_VALUE;
		for (File c : classes) {
			if (choice != Integer.MAX_VALUE && choice != JOptionPane.YES_OPTION) {
				break;
			}
			String className = c.getName();
			//add classpanel if not exists.
			addNewClass(className);
			File rois[] = c.listFiles();
			for (File r : rois) {
				if (r.getName().endsWith(".roi")) {
					try {
						Roi r_ = new RoiDecoder(r.getAbsolutePath()).getRoi();
						if(r_ == null) {
							System.out.println("Null ROI... skip. :"+r.getAbsolutePath());
							continue;
						}
						if (isRoiBelongingTo(r_, prap)) {
							//already done add new class
							getClassPanel(className).add(r_);
						} else {
							System.out.println("This roi does not match to current series.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private boolean isRoiBelongingTo(Roi r, ImagePlus prap) {
		int pos = r.getPosition();
		prap.setSlice(pos);
		String patID = DicomTools.getTag(prap, "0010,0010");
		String studyUID = DicomTools.getTag(prap, "0020,000D");
		String seriesUID = DicomTools.getTag(prap, "0020,000E");
		String sopUID = DicomTools.getTag(prap, "0008,0018");
		String pid_ = r.getProperty("PatientID");
		String studyUID_ = r.getProperty("StudyInstanceUID");
		String seriesUID_ = r.getProperty("SeriesInstanceUID");
		String sopUID_ = r.getProperty("SOPInstanceUID");
		
		if(patID == null || studyUID==null||seriesUID == null || sopUID==null) {
			return false;
		}
		
		if(pid_ == null || studyUID_==null||seriesUID_ == null || sopUID_==null) {
			return false;
		}
		
		if(patID.equals(pid_)&&studyUID.equals(studyUID_)&&seriesUID.equals(seriesUID_)&&sopUID.equals(sopUID_)) {
			return true;
		}
		return false;
	}
	
	public void loadConfigurationSettingProps() {
		radWin.loadRadiomicsSettings();
		loadModelSettings();
	}
	
	public void loadModelSettings() {
		String v = Prefs.getString(BALANCE);
		if(v !=null) {
			balance.setSelected(Boolean.valueOf(v));
		}
		v = Prefs.getString(FEATURE_SELECT);
		if(v !=null) {
			autoFeatureSelect.setSelected(Boolean.valueOf(v));
		}
		v = Prefs.getString(IMPUTE);
		if(v !=null) {
			autoImputation.setSelected(Boolean.valueOf(v));
		}
	}
	
	public void loadModel(File f) {
		try {
			Classifier clf = (Classifier) SerializationHelper.read(f.getAbsolutePath());
			m_ClassifierEditor.setValue(clf);//be careful, this is a reason for locating loadModel() in this class.
			pipeline.modelIs(clf);
			System.out.println("Model is loaded successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadDatasetARFF(File f) {
		pipeline.loadDatasetARFF(f);
	}
	
	public void initClassPanels() {
		classes = new ArrayList<>();
		for(Component com : classListPanel.getComponents()) {
			if(com instanceof ClassPanel) {
				classListPanel.remove(com);
			}
		}
		classListPanel.revalidate();
		classListPanel.repaint();
	}
	
	private void setAction(JComponent con) {
		if(con instanceof JButton) {
			JButton btn = (JButton)con;
			String name = btn.getActionCommand();
			if(name.equals("")) {
				
			}else if(name.equals(TRAIN_MODEL)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						initClassifier();//set model
						RadiomicsSettings rs = radWin.getRadiomicsSettings();
						HashMap<String, List<Roi>> ds = getRois();
						ImagePlus pp = WindowManager.getCurrentImage();
						pipeline = pipeline.trainWith(rs, ds, pp);
						pipeline.train(autoImputation.isSelected(), balance.isSelected(), autoFeatureSelect.isSelected());
					}
				});	
			}else if(name.equals(PREDICTION)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						RadiomicsSettings rs = radWin.getRadiomicsSettings();
						HashMap<String, List<Roi>> ds = getRois();
						ImagePlus pp = WindowManager.getCurrentImage();
						pipeline = pipeline.trainWith(rs, ds, pp);
						/* pred imageplus
						 * slice 0 : label image
						 * slice 1 : proba image
						 */
						pred = pipeline.predict(pp.getCurrentSlice());
						System.out.println("PREDICTION was done !");
					}
				});
			}else if(name.equals(SHOW_RESULTS)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(pred != null) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									pred.show();
								}
							}).start();
						}
					}
				});
			}else if(name.equals(SAVE_RESULTS)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(pred != null) {
							SaveDialog sd = new SaveDialog("Save prediction results", "pred_seg", ".tif");
							if (sd.getFileName() == null) {
					            //("キャンセルされました。");
					            return; // 処理を中断
					        }
							// 3. ディレクトリとファイル名を取得
					        String directory = sd.getDirectory();
					        String fileName = sd.getFileName();
					        String savePath = directory + fileName;

					        // 結果をログに表示
					        System.out.println("選択された保存パス: " + savePath);
					        
					        // 4. 取得したパスを使って画像を保存
					        // このsaveAsメソッドが実際の保存処理を行います
					        IJ.saveAs(pred, "tiff", savePath);
					        System.out.println("完了:"+savePath + " に画像を保存しました。");
						}
					}
				});
			}else if(name.equals(LOAD_CONFIG)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadConfiguration();
					}
				});
			}else if(name.equals(SAVE_CONFIG)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						saveConfigration();
					}
				});
			}else if(name.equals(WEKA)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						launch_weka();
					}
				});
			}else if(name.equals(ADD_CLASS)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String className = JOptionPane.showInputDialog("Please input new class name:", null);
						if (className != null) {
							if (!className.trim().isEmpty()) {
								System.out.println("入力されたクラス名: " + className);
								addNewClass(className);
							} else {
								// 空白のみ、または何も入力せずにOKを押した場合
								System.out.println("クラス名が入力されませんでした。");
							}
						} else {
							System.out.println("入力がキャンセルされました。");
						}
					}
				});
			}else if(name.equals(DELETE_CLASS)) {
				btn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
				        String[] options = getClassNames();
				        JComboBox<String> comboBox = new JComboBox<>(options);
				        JPanel panel = new JPanel();
				        panel.add(new JLabel("Select a class:"));
				        panel.add(comboBox);
				        // JOptionPane.showOptionDialog() を使用してカスタムダイアログを表示
				        int result = JOptionPane.showOptionDialog(
				            null,                       // 親フレーム (nullで画面中央)
				            panel,                      // 表示するカスタムコンポーネント（JPanel）
				            "Select Class to Delete",            // ダイアログのタイトル
				            JOptionPane.OK_CANCEL_OPTION, // OKとCancelボタンを表示
				            JOptionPane.QUESTION_MESSAGE, // メッセージの種類 (アイコン表示)
				            null,                       // アイコン (nullでデフォルト)
				            null,                       // オプションボタンの配列 (nullでデフォルトのOK/Cancel)
				            null                        // デフォルトで選択されるオプション (nullで最初のボタン)
				        );

				        // ユーザーの選択に応じた処理
				        if (result == JOptionPane.OK_OPTION) {
				            // OKボタンが押された場合、選択されたアイテムを取得
				            String selectedOption = (String) comboBox.getSelectedItem();
				            System.out.println("選択されたクラス: " + selectedOption);
				            deleteClass(selectedOption);
				        } else {
				            // Cancelボタンが押されたか、ダイアログが閉じられた場合
				            System.out.println("選択がキャンセルされました。");
				            JOptionPane.showMessageDialog(null, "選択がキャンセルされました。", "キャンセル", JOptionPane.INFORMATION_MESSAGE);
				        }
					}
				});
			}
		}
	}
	
	public void launch_weka() {
		GUIChooserApp chooser = new GUIChooserApp();
		for (WindowListener wl : chooser.getWindowListeners()) {
			chooser.removeWindowListener(wl);
		}
		chooser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
	}
	
	boolean isDuplicateName(String newClassName) {
		for(ClassPanel cp : classes) {
			if(cp.name().equals(newClassName)) {
				return true;
			}
		}
		return false;
	}
	
	class ClassPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		final int ind;
		final String name;
		JList<Roi> roiList;
		DefaultListModel<Roi> listModel = new DefaultListModel<>();
		JButton addBtn = new JButton("Add");
		JButton deleteBtn = new JButton("Delete");
		ClassPanel(int index, String name){
			ind = index;
			this.name = name;
			roiList = new JList<>(listModel);
			roiList.setCellRenderer(new RoiListCellRenderer());
			setLayout(new BorderLayout());
			JPanel btnP = new JPanel(new GridLayout(1, 2));
			btnP.add(addBtn);
			btnP.add(deleteBtn);
			add(btnP, BorderLayout.NORTH);
			addBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ImagePlus im = WindowManager.getCurrentImage();
					Roi r = im.getRoi();
					if(r != null) {
						add(r);
					}
				}
			});
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<Roi> selected = roiList.getSelectedValuesList();
					if(selected == null || selected.size()==0) {
						return;
					}
					for(Roi r : selected) {
						if(r != null) {
							delete(r);
						}
					}
				}
			});
			add(roiList, BorderLayout.CENTER);
			Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.cyan, Color.DARK_GRAY);
			setBorder(BorderFactory.createTitledBorder(b, name, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
			setPreferredSize(new Dimension(200, 200));
			setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
		}
		int index() {
			return ind;
		}
		String name() {
			return name;
		}
		
		void add(Roi r) {
			if(listModel.contains(r)) {
				System.out.println(r.getName() + " is already listed.");
				return;
			}
			String name = r.getName();
			for(int i=0; i<listModel.size(); i++) {
				Roi r2 = listModel.get(i);
				String roiName2 = r2.getName();
				if(name.equals(roiName2)) {
					//already in. skip.
					return;
				}
			}
			listModel.add(listModel.getSize(), r);
		}
		
		void delete(Roi r) {
			int pos = listModel.indexOf(r);
			if(pos >= 0) {
				listModel.remove(listModel.indexOf(r));
			}
		}
		
		void updateOrReplace(int row/*0 to n-1*/, Roi r) {
			if(row < 0 || row > listModel.getSize()) {
				System.out.println("RadiomicsPanel.ClassPanle:updateOrReplace:: Out Of Range ! "+row);
				return;
			}
			if(r == null) {
				System.out.println("RadiomicsPanel.ClassPanle:updateOrReplace:: Rois is NULL ! "+row);
				return;
			}
			listModel.set(row, r);
		}
		
		List<Roi> getRois(){
			List<Roi> rois = new ArrayList<>();
			int size = listModel.getSize();
			for(int i=0; i<size; i++) {
				rois.add(listModel.get(i));
			}
			return rois;
		}
	}
	
	class RoiListCellRenderer extends DefaultListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, // RoiObj
				int index, 
				boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Roi) {
				Roi roi = (Roi) value;
				setText(roi.getName());
			} else {
				setText((value == null) ? "" : value.toString());
			}
			return this;
		}
	}
	
}
