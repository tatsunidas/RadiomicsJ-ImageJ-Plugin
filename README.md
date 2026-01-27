# RadiomicsJ ImageJ Plugin README
## Concepts

This plugin was developed for educational and research purposes. It provides three main functionalities:
- Machine Learning: Create a simple segmentation model trained on Radiomics features using the WEKA library.
- Radiomics Feature Calculation: Calculate features based on image and mask pairs.
- Visualization: Generate and visualize Radiomics feature maps calculated via a raster scan (sliding window) approach.
    
## Installation

    Download the RadiomicsJ_ImageJPlugin-***.jar file(*** means version) and radiomics_lib.zip
    unzip radiomics_lib.zip to as "radiomics_lib" folder.
    Place the both files into the plugins folder of your ImageJ or Fiji installation.
    Restart ImageJ/Fiji.

## Usage
### Simple Machine Learning (Segmentation)

This module allows you to train a segmentation model using interactive ROI selection.

    Open an image in ImageJ.
    Create ROIs for each target class (e.g., background, tissue, lesion).
    Click the Add button in the plugin to register the current ROI to the corresponding class.
    Follow the workflow: Train -> Prediction -> Show Results.
    Observe the segmentation results.

Note: The execution of the Prediction mode may take some time depending on the image size and feature settings.

### Texture Settings

Configure the parameters for Radiomics feature calculation in this section.

    Default Settings: The default values are generally sufficient for most use cases.
    Calculation Mode:
        2D Mode: Calculates features slice by slice.
        3D Mode: Calculates volumetric features.

### Batch Mode

This mode allows for the automated processing of multiple datasets simultaneously. It is useful for calculating features for a large number of image-mask pairs at once.

### Visualization

Visualize specific Radiomics features on the image.

    The plugin calculates the specified features using a patch-based raster scan on the image-mask pair.
    The calculation parameters are based on the configuration in Texture Settings.
    
### Configuration properties

This plugin automatically save properties files to user.home/RadiomicsJ_IJ_Plugin.properties.
If you would like to uninstall, do just remove this file.

### Requirements

    ImageJ 1.53 or Fiji
    Java 8 or higher

## License

MIT License
