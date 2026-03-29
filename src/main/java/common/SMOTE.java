package common;
/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of graphy, hosted at https://github.com/graphy.
 *
 * The Initial Developer of the Original Code is
 * Visionary Imaging Services, Inc.
 * Portions created by the Initial Developer are Copyright (C) 2015
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * SMOTE.java
 * 
 * Copyright (C) 2008 Ryan Lichtenwalter 
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import ij.IJ; // Added for ImageJ verbose logging

/**
 * Resamples a dataset by applying the Synthetic
 * Minority Oversampling Technique (SMOTE). The original dataset must fit
 * entirely in memory. The amount of SMOTE and number of nearest neighbors may
 * be specified. For more information, see <br/>
 * <br/>
 * Nitesh V. Chawla et. al. (2002). Synthetic Minority Over-sampling Technique.
 * Journal of Artificial Intelligence Research. 16:321-357.
 * <p/>
 * *
 * @author Ryan Lichtenwalter (rlichtenwalter@gmail.com)
 * @version $Revision$
 */
public class SMOTE extends Filter implements SupervisedFilter, TechnicalInformationHandler {

	/** for serialization. */
	static final long serialVersionUID = -1653880819059250364L;

	/** the number of neighbors to use. */
	protected int m_NearestNeighbors = 5;

	/** the random seed to use. */
	protected int m_RandomSeed = 1;

	/** the percentage of SMOTE instances to create. */
	protected double m_Percentage = 100.0;

	/** the index of the class value. */
	protected String m_ClassValueIndex = "0";

	/** whether to detect the minority class automatically. */
	protected boolean m_DetectMinorityClass = true;

	public String globalInfo() {
		return "Resamples a dataset by applying the Synthetic Minority Oversampling Technique (SMOTE)."
				+ " The original dataset must fit entirely in memory."
				+ " The amount of SMOTE and number of nearest neighbors may be specified."
				+ " For more information, see \n\n" + getTechnicalInformation().toString();
	}

	public TechnicalInformation getTechnicalInformation() {
		TechnicalInformation result = new TechnicalInformation(Type.ARTICLE);
		result.setValue(Field.AUTHOR, "Nitesh V. Chawla et. al.");
		result.setValue(Field.TITLE, "Synthetic Minority Over-sampling Technique");
		result.setValue(Field.JOURNAL, "Journal of Artificial Intelligence Research");
		result.setValue(Field.YEAR, "2002");
		result.setValue(Field.VOLUME, "16");
		result.setValue(Field.PAGES, "321-357");
		return result;
	}

	public String getRevision() {
		return RevisionUtils.extract("$Revision$");
	}

	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();
		result.enableAllAttributes();
		result.enable(Capability.MISSING_VALUES);
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration listOptions() {
		Vector newVector = new Vector();
		newVector.addElement(new Option("\tSpecifies the random number seed\n" + "\t(default 1)", "S", 1, "-S <num>"));
		newVector.addElement(new Option("\tSpecifies percentage of SMOTE instances to create.\n" + "\t(default 100.0)\n", "P", 1, "-P <percentage>"));
		newVector.addElement(new Option("\tSpecifies the number of nearest neighbors to use.\n" + "\t(default 5)\n", "K", 1, "-K <nearest-neighbors>"));
		newVector.addElement(new Option("\tSpecifies the index of the nominal class value to SMOTE\n" + "\t(default 0: auto-detect non-empty minority class)\n", "C", 1, "-C <value-index>"));
		return newVector.elements();
	}

	public void setOptions(String[] options) throws Exception {
		String seedStr = Utils.getOption('S', options);
		if (seedStr.length() != 0) {
			setRandomSeed(Integer.parseInt(seedStr));
		} else {
			setRandomSeed(1);
		}

		String percentageStr = Utils.getOption('P', options);
		if (percentageStr.length() != 0) {
			setPercentage(Double.parseDouble(percentageStr));
		} else {
			setPercentage(100.0);
		}

		String nnStr = Utils.getOption('K', options);
		if (nnStr.length() != 0) {
			setNearestNeighbors(Integer.parseInt(nnStr));
		} else {
			setNearestNeighbors(5);
		}

		String classValueIndexStr = Utils.getOption('C', options);
		if (classValueIndexStr.length() != 0) {
			setClassValue(classValueIndexStr);
		} else {
			m_DetectMinorityClass = true;
		}
	}

	public String[] getOptions() {
		Vector<String> result = new Vector<String>();
		result.add("-C");
		result.add(getClassValue());
		result.add("-K");
		result.add("" + getNearestNeighbors());
		result.add("-P");
		result.add("" + getPercentage());
		result.add("-S");
		result.add("" + getRandomSeed());
		return result.toArray(new String[result.size()]);
	}

	public String randomSeedTipText() {
		return "The seed used for random sampling.";
	}

	public int getRandomSeed() {
		return m_RandomSeed;
	}

	public void setRandomSeed(int value) {
		m_RandomSeed = value;
	}

	public String percentageTipText() {
		return "The percentage of SMOTE instances to create.";
	}

	public void setPercentage(double value) {
		if (value >= 0) {
			m_Percentage = value;
		} else {
			IJ.log("[ERROR] SMOTE: Percentage must be >= 0!");
			throw new IllegalArgumentException("Percentage must be >= 0!");
		}
	}

	public double getPercentage() {
		return m_Percentage;
	}

	public String nearestNeighborsTipText() {
		return "The number of nearest neighbors to use.";
	}

	public void setNearestNeighbors(int value) {
		if (value >= 1) {
			m_NearestNeighbors = value;
		} else {
			IJ.log("[ERROR] SMOTE: At least 1 neighbor is necessary!");
			throw new IllegalArgumentException("At least 1 neighbor is necessary for SMOTE!");
		}
	}

	public int getNearestNeighbors() {
		return m_NearestNeighbors;
	}

	public String classValueTipText() {
		return "The index of the class value to which SMOTE should be applied. "
				+ "Use a value of 0 to auto-detect the non-empty minority class.";
	}

	public void setClassValue(String value) {
		m_ClassValueIndex = value;
		if (m_ClassValueIndex.equals("0")) {
			m_DetectMinorityClass = true;
		} else {
			m_DetectMinorityClass = false;
		}
	}

	public String getClassValue() {
		return m_ClassValueIndex;
	}

	public boolean setInputFormat(Instances instanceInfo) throws Exception {
		super.setInputFormat(instanceInfo);
		super.setOutputFormat(instanceInfo);
		return true;
	}

	public boolean input(Instance instance) {
		if (getInputFormat() == null) {
			throw new IllegalStateException("No input instance format defined");
		}
		if (m_NewBatch) {
			resetQueue();
			m_NewBatch = false;
		}
		if (m_FirstBatchDone) {
			push(instance);
			return true;
		} else {
			bufferInput(instance);
			return false;
		}
	}

	public boolean batchFinished() throws Exception {
		if (getInputFormat() == null) {
			throw new IllegalStateException("No input instance format defined");
		}
		if (!m_FirstBatchDone) {
			doSMOTE();
		}
		flushInput();

		m_NewBatch = true;
		m_FirstBatchDone = true;
		return (numPendingOutput() != 0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doSMOTE() throws Exception {
		IJ.log("> [SMOTE] Starting SMOTE algorithm execution...");
		
		int minIndex = 0;
		int min = Integer.MAX_VALUE;
		
		if (m_DetectMinorityClass) {
			IJ.log("> [SMOTE] Auto-detecting minority class...");
			int[] classCounts = getInputFormat().attributeStats(getInputFormat().classIndex()).nominalCounts;
			for (int i = 0; i < classCounts.length; i++) {
				if (classCounts[i] != 0 && classCounts[i] < min) {
					min = classCounts[i];
					minIndex = i;
				}
			}
			IJ.log("    > Minority class detected at index: " + minIndex + " (Instance count: " + min + ")");
		} else {
			String classVal = getClassValue();
			if (classVal.equalsIgnoreCase("first")) {
				minIndex = 1;
			} else if (classVal.equalsIgnoreCase("last")) {
				minIndex = getInputFormat().numClasses();
			} else {
				minIndex = Integer.parseInt(classVal);
			}
			if (minIndex > getInputFormat().numClasses()) {
				IJ.log("[ERROR] SMOTE: Value index exceeds the number of classes.");
				throw new Exception("Value index must be less than or equal to the number of classes.");
			}
			minIndex--; // make it an index
			IJ.log("> [SMOTE] Using user-specified class index: " + minIndex);
		}

		int nearestNeighbors;
		if (min <= getNearestNeighbors()) {
			nearestNeighbors = min - 1;
		} else {
			nearestNeighbors = getNearestNeighbors();
		}
		
		if (nearestNeighbors < 1) {
			IJ.log("[ERROR] SMOTE: Insufficient instances to form neighbors.");
			throw new Exception("Cannot use 0 neighbors for SMOTE! (Minority class has too few instances)");
		}
			
		IJ.log("> [SMOTE] Proceeding with " + nearestNeighbors + " nearest neighbors.");

		Instances sample = getInputFormat().stringFreeStructure();
		Enumeration instanceEnum = getInputFormat().enumerateInstances();
		while (instanceEnum.hasMoreElements()) {
			Instance instance = (Instance) instanceEnum.nextElement();
			push((Instance) instance.copy());
			if ((int) instance.classValue() == minIndex) {
				sample.add(instance);
			}
		}

		IJ.log("> [SMOTE] Computing Value Distance Metric matrices for nominal features...");
		Map vdmMap = new HashMap();
		Enumeration attrEnum = getInputFormat().enumerateAttributes();
		while (attrEnum.hasMoreElements()) {
			Attribute attr = (Attribute) attrEnum.nextElement();
			if (!attr.equals(getInputFormat().classAttribute())) {
				if (attr.isNominal() || attr.isString()) {
					double[][] vdm = new double[attr.numValues()][attr.numValues()];
					vdmMap.put(attr, vdm);
					int[] featureValueCounts = new int[attr.numValues()];
					int[][] featureValueCountsByClass = new int[getInputFormat().classAttribute().numValues()][attr.numValues()];
					instanceEnum = getInputFormat().enumerateInstances();
					
					while (instanceEnum.hasMoreElements()) {
						Instance instance = (Instance) instanceEnum.nextElement();
						int value = (int) instance.value(attr);
						int classValue = (int) instance.classValue();
						featureValueCounts[value]++;
						featureValueCountsByClass[classValue][value]++;
					}
					for (int valueIndex1 = 0; valueIndex1 < attr.numValues(); valueIndex1++) {
						for (int valueIndex2 = 0; valueIndex2 < attr.numValues(); valueIndex2++) {
							double sum = 0;
							for (int classValueIndex = 0; classValueIndex < getInputFormat().numClasses(); classValueIndex++) {
								double c1i = (double) featureValueCountsByClass[classValueIndex][valueIndex1];
								double c2i = (double) featureValueCountsByClass[classValueIndex][valueIndex2];
								double c1 = (double) featureValueCounts[valueIndex1];
								double c2 = (double) featureValueCounts[valueIndex2];
								double term1 = c1i / c1;
								double term2 = c2i / c2;
								sum += Math.abs(term1 - term2);
							}
							vdm[valueIndex1][valueIndex2] = sum;
						}
					}
				}
			}
		}

		Random rand = new Random(getRandomSeed());
		List extraIndices = new LinkedList();
		double percentageRemainder = (getPercentage() / 100) - Math.floor(getPercentage() / 100.0);
		int extraIndicesCount = (int) (percentageRemainder * sample.numInstances());
		
		if (extraIndicesCount >= 1) {
			for (int i = 0; i < sample.numInstances(); i++) {
				extraIndices.add(i);
			}
		}
		
		Collections.shuffle(extraIndices, rand);
		extraIndices = extraIndices.subList(0, extraIndicesCount);
		Set extraIndexSet = new HashSet(extraIndices);

		Instance[] nnArray = new Instance[nearestNeighbors];
		IJ.log("> [SMOTE] Generating synthetic instances...");
		
		for (int i = 0; i < sample.numInstances(); i++) {
			Instance instanceI = sample.instance(i);
			List distanceToInstance = new LinkedList();
			
			for (int j = 0; j < sample.numInstances(); j++) {
				Instance instanceJ = sample.instance(j);
				if (i != j) {
					double distance = 0;
					attrEnum = getInputFormat().enumerateAttributes();
					while (attrEnum.hasMoreElements()) {
						Attribute attr = (Attribute) attrEnum.nextElement();
						if (!attr.equals(getInputFormat().classAttribute())) {
							double iVal = instanceI.value(attr);
							double jVal = instanceJ.value(attr);
							if (attr.isNumeric()) {
								distance += Math.pow(iVal - jVal, 2);
							} else {
								distance += ((double[][]) vdmMap.get(attr))[(int) iVal][(int) jVal];
							}
						}
					}
					distance = Math.pow(distance, .5);
					distanceToInstance.add(new Object[] { distance, instanceJ });
				}
			}

			Collections.sort(distanceToInstance, new Comparator() {
				public int compare(Object o1, Object o2) {
					double distance1 = (Double) ((Object[]) o1)[0];
					double distance2 = (Double) ((Object[]) o2)[0];
					return Double.compare(distance1, distance2);
				}
			});

			Iterator entryIterator = distanceToInstance.iterator();
			int j = 0;
			while (entryIterator.hasNext() && j < nearestNeighbors) {
				nnArray[j] = (Instance) ((Object[]) entryIterator.next())[1];
				j++;
			}

			int n = (int) Math.floor(getPercentage() / 100);
			while (n > 0 || extraIndexSet.remove(i)) {
				double[] values = new double[sample.numAttributes()];
				int nn = rand.nextInt(nearestNeighbors);
				attrEnum = getInputFormat().enumerateAttributes();
				
				while (attrEnum.hasMoreElements()) {
					Attribute attr = (Attribute) attrEnum.nextElement();
					if (!attr.equals(getInputFormat().classAttribute())) {
						if (attr.isNumeric()) {
							double dif = nnArray[nn].value(attr) - instanceI.value(attr);
							double gap = rand.nextDouble();
							values[attr.index()] = (double) (instanceI.value(attr) + gap * dif);
						} else if (attr.isDate()) {
							double dif = nnArray[nn].value(attr) - instanceI.value(attr);
							double gap = rand.nextDouble();
							values[attr.index()] = (long) (instanceI.value(attr) + gap * dif);
						} else {
							int[] valueCounts = new int[attr.numValues()];
							int iVal = (int) instanceI.value(attr);
							valueCounts[iVal]++;
							for (int nnEx = 0; nnEx < nearestNeighbors; nnEx++) {
								int val = (int) nnArray[nnEx].value(attr);
								valueCounts[val]++;
							}
							int maxIndex = 0;
							int max = Integer.MIN_VALUE;
							for (int index = 0; index < attr.numValues(); index++) {
								if (valueCounts[index] > max) {
									max = valueCounts[index];
									maxIndex = index;
								}
							}
							values[attr.index()] = maxIndex;
						}
					}
				}
				values[sample.classIndex()] = minIndex;
				Instance synthetic = new DenseInstance(1.0, values);
				push(synthetic);
				n--;
			}
		}
		
		IJ.log("> [SMOTE] Synthetic instances generation complete.");
	}

	public static void main(String[] args) {
		runFilter(new SMOTE(), args);
	}
}