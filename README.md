# Fiji-STLXYT
Standard Template Library for Fiji plugins for XYT shape image analysis.

Plenty of image analysis done in Fiji is done on XYT shape images, in this repository we create a skeleton framework that can be used for interactive, unfrozen GUI based image analysis tasks.

In this template we have Java swing based GUI for Fiji plugins doing analysis for XYT shape images. The analysis could be genric ranging from skeletonization, fitting cirlces to fitting Microtubule gaussian models. The template functions are based on swing workers creating background threads preventing freezing of GUI/images while performing computations enabling an interactive image analysis. 

The code is meant to be used as a skeleton on top of which further code can be added to create manual overrides for the automated analysis such as adjusting skeleton end points/ changing the microtubule end points found by the automated programs.

The GUI is based on the MasterPanel repository on this page.

For user specific cases just changing the function is required to create a seperate Lab plugin designed to carry out specific task related to the lab needs.
# Fiji-STLXYT
Standard Template Library for Fiji plugins for XYT shape image analysis 
