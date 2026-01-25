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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * to use load/save radiomics settings.
 * 
 * @author tatsunidas
 *
 */
public class SettingsContext {
	
	//operationals
	public static final String OPERATIONAL = "Operational";
	public static final String DIAGNOSTICS = "Diagnostics";
	
	/**
	 * Feature names
	 */
	public static final String MORPHOLOGICAL = "Morphological";
	public static final String LOCALINTENSITY = "LocalIntensity";
	public static final String INTENSITYSTATS = "IntensityStats";
	public static final String INTENSITYHISTOGRAM = "IntensityHistogram";
	public static final String INTENSITYVOLUMEHISTOGRAM = "VolumeHistogram";
	public static final String GLCM = "GLCM";
	public static final String GLRLM = "GLRLM";
	public static final String GLSZM = "GLSZM";
	public static final String GLDZM = "GLDZM";
	public static final String NGTDM = "NGTDM";
	public static final String NGLDM = "NGLDM";
	public static final String FRACTAL = "Fractal";
	public static final String SHAPE2D = "Shape2D";
	
	public static final String EXCLUSION_PREFIX = "RadiomicsJ.EXCLUSION";
	
	public static final String IMPUTE = "RadiomicsJ.MODEL_Impute";
	public static final String BALANCE = "RadiomicsJ.MODEL_Balance";
	public static final String FEATURE_SELECT = "RadiomicsJ.MODEL_FeatureSelect";
	
	/**
	 * calculation
	 */
	public static final String CLASSIFICATION = "RadiomicsJ.CLASSIFICATION_STRING";
	public static final String REGRESSION = "RadiomicsJ.REGRESSION_STRING";
	public static final String D3Basis = "RadiomicsJ.3DBASIS_BOOL";
	
	/**
	 * mask settings
	 */
	public static final String MASK_LABEL = "RadiomicsJ.MASK_LABEL_INT";
	public static final String RemoveOutliers = "RadiomicsJ.RemoveOutliers_BOOL";
	public static final String RemoveOutliersSigma = "RadiomicsJ.Sigma_INT";
	public static final String RangeFiltering = "RadiomicsJ.RangeFiltering_BOOL";
	public static final String RangeFilteringMin = "RadiomicsJ.ResamplingMin_DOUBLE";
	public static final String RangeFilteringMax = "RadiomicsJ.ResamplingMax_DOUBLE";
	public static final String Resampling = "RadiomicsJ.Resampling_BOOL";
	public static final String ResamplingX = "RadiomicsJ.ResamplingX_DOUBLE";
	public static final String ResamplingY = "RadiomicsJ.ResamplingY_DOUBLE";
	public static final String ResamplingZ = "RadiomicsJ.ResamplingZ_DOUBLE";
	
	//TODO
	public static final String UseBinCountHISTOGRAM = "RadiomicsJ.BINCOUNT_HIST_BOOL";
	public static final String BinCountHISTOGRAM = "RadiomicsJ.BINCOUNT_HIST_INT";
	public static final String BinWidthHISTOGRAM = "RadiomicsJ.BINWIDTH_HIST_DOUBLE";
	
	public static final String UseOriginalIVH = "RadiomicsJ.USEORIGINAL_IVH_BOOL";
	public static final String UseBinCountIVH = "RadiomicsJ.BINCOUNT_IVH_BOOL";
	public static final String BinCountIVH = "RadiomicsJ.BINCOUNT_IVH_INT";
	public static final String BinWidthIVH = "RadiomicsJ.BINWIDTH_IVH_DOUBLE";
	
	public static final String UseBinCountGLCM = "RadiomicsJ.BINCOUNT_GLCM_BOOL";
	public static final String BinCountGLCM = "RadiomicsJ.BINCOUNT_GLCM_INT";
	public static final String BinWidthGLCM = "RadiomicsJ.BINWIDTH_GLCM_DOUBLE";
	public static final String DeltaGLCM = "RadiomicsJ.DELTA_GLCM_DOUBLE";
	//public static final String NormGLCM = "NORM_GLCM_STRING";
	
	public static final String UseBinCountGLRLM = "RadiomicsJ.BINCOUNT_GLRLM_BOOL";
	public static final String BinCountGLRLM = "RadiomicsJ.BINCOUNT_GLRLM_INT";
	public static final String BinWidthGLRLM = "RadiomicsJ.BINWIDTH_GLRLM_DOUBLE";
//	public static final String NormGLRLM = "NORM_GLRLM_STRING";
	
	public static final String UseBinCountGLSZM = "RadiomicsJ.BINCOUNT_GLSZM_BOOL";
	public static final String BinCountGLSZM = "RadiomicsJ.BINCOUNT_GLSZM_INT";
	public static final String BinWidthGLSZM = "RadiomicsJ.BINWIDTH_GLSZM_DOUBLE";
//	public static final String NormGLSZM = "NORM_GLSZM_STRING";
	
	public static final String UseBinCountGLDZM = "RadiomicsJ.BINCOUNT_GLDZM_BOOL";
	public static final String BinCountGLDZM = "RadiomicsJ.BINCOUNT_GLDZM_INT";
	public static final String BinWidthGLDZM = "RadiomicsJ.BINWIDTH_GLDZM_DOUBLE";
//	public static final String NormGLDZM = "NORM_GLDZM_STRING";
	
	public static final String UseBinCountNGTDM = "RadiomicsJ.BINCOUNT_NGTDM_BOOL";
	public static final String BinCountNGTDM = "RadiomicsJ.BINCOUNT_NGTDM_INT";
	public static final String BinWidthNGTDM = "RadiomicsJ.BINWIDTH_NGTDM_DOUBLE";
	public static final String DeltaNGTDM = "RadiomicsJ.DELTA_NGTDM_DOUBLE";
//	public static final String NormNGTDM = "NORM_NGTDM_STRING";
	
	public static final String UseBinCountNGLDM = "RadiomicsJ.BINCOUNT_NGLDM_BOOL";
	public static final String BinCountNGLDM = "RadiomicsJ.BINCOUNT_NGLDM_INT";
	public static final String BinWidthNGLDM = "RadiomicsJ.BINWIDTH_NGLDM_DOUBLE";
	public static final String AlphaNGLDM = "RadiomicsJ.ALPHA_NGLDM_DOUBLE";
	public static final String DeltaNGLDM = "RadiomicsJ.DELTA_NGLDM_DOUBLE";
//	public static final String NormNGLDM = "NORM_NGLDM_STRING";
	
	public static final String BoxSizesFRACTAL = "RadiomicsJ.BOXSIZES_FRACTAL";
	
	/**
     * 指定されたオブジェクトからString型のインスタンスフィールドの値をリストとして取得します。
     *
     * @param obj フィールド値を取得したいオブジェクト
     * @return Stringフィールドの値のリスト
     * @throws IllegalAccessException privateフィールドへのアクセスに失敗した場合
     */
	public static List<String> getStringFieldValues() throws IllegalAccessException {
		List<String> values = new ArrayList<>();
		// クラスに定義されているすべてのフィールドを取得
		Field[] fields = SettingsContext.class.getDeclaredFields();
		for (Field field : fields) {
			// フィールドがString型であり、かつstaticであることを確認
			if (field.getType() == String.class && Modifier.isStatic(field.getModifiers())) {
				// privateなフィールドにもアクセスできるように設定
				field.setAccessible(true);
				// フィールドの値を取得してリストに追加
				Object value = field.get(SettingsContext.class);
				if (value != null) {
					values.add((String) value);
				}
			}
		}
		return values;
	}
}
