package TrackBuddy.plugin.trackmate.io;


/**
 * Contains the key string used for xml marshaling, for TrackMate xml files
 * version prior to 1.3
 * @author Jean-Yves Tinevez &lt;jeanyves.tinevez@gmail.com&gt;  2010-2012
  */
class TmXmlKeys_v12 {
	
	/*
	 * GENERIC ATTRIBUTES
	 */
	
	public static final String FRAME_ATTRIBUTE_NAME_v12 				= "frame"; 
	public static final String BCellobject_ID_ATTRIBUTE_NAME_v12 				= "ID";
	public static final String BCellobject_NAME_v12_ATTRIBUTE_NAME_v12 			= "name";
	
	/*
	 * ROOT ELEMENT
	 */

	public static final String ROOT_ELEMENT_KEY_v12 					= "TrackMate";
	public static final String PLUGIN_VERSION_ATTRIBUTE_NAME_v12		= "version";
	
	
	/*
	 * SETTINGS elements
	 */
	
	public static final String SETTINGS_ELEMENT_KEY_v12 				= "BasicSettings";
	public static final String SETTINGS_XSTART_ATTRIBUTE_NAME_v12 		= "xstart";
	public static final String SETTINGS_YSTART_ATTRIBUTE_NAME_v12 		= "ystart";
	public static final String SETTINGS_ZSTART_ATTRIBUTE_NAME_v12 		= "zstart";
	public static final String SETTINGS_TSTART_ATTRIBUTE_NAME_v12 		= "tstart";
	public static final String SETTINGS_XEND_ATTRIBUTE_NAME_v12 		= "xend";
	public static final String SETTINGS_YEND_ATTRIBUTE_NAME_v12 		= "yend";
	public static final String SETTINGS_ZEND_ATTRIBUTE_NAME_v12 		= "zend";
	public static final String SETTINGS_TEND_ATTRIBUTE_NAME_v12 		= "tend";
	public static final String SETTINGS_SEGMENTATION_CHANNEL_ATTRIBUTE_NAME_v12 		= "segmentationchannel";

	/*
	 * SEGMENTER SETTINGS
	 */
	
	public static final String SEGMENTER_SETTINGS_ELEMENT_KEY_v12 			= "SegmenterSettings";
	public static final String SEGMENTER_SETTINGS_CLASS_ATTRIBUTE_NAME_v12 	= "segmentersettingsclass";
	public static final String SEGMENTER_CLASS_ATTRIBUTE_NAME_v12 			= "segmenterclass";

	/*
	 * TRACKER SETTINGS
	 */
	
	public static final String TRACKER_SETTINGS_ELEMENT_KEY_v12				= "TrackerSettings";
	public static final String TRACKER_SETTINGS_CLASS_ATTRIBUTE_NAME_v12	= "trackersettingsclass";
	public static final String TRACKER_CLASS_ATTRIBUTE_NAME_v12				= "trackerclass";
	
	/*
	 * IMAGE element
	 */
	
	public static final String IMAGE_ELEMENT_KEY_v12 					= "ImageData";
	public static final String IMAGE_FILENAME_v12_ATTRIBUTE_NAME_v12 		= "filename";
	public static final String IMAGE_FOLDER_ATTRIBUTE_NAME_v12 			= "folder";
	public static final String IMAGE_PIXEL_WIDTH_ATTRIBUTE_NAME_v12		= "pixelwidth";
	public static final String IMAGE_WIDTH_ATTRIBUTE_NAME_v12 			= "width";
	public static final String IMAGE_PIXEL_HEIGHT_ATTRIBUTE_NAME_v12 	= "pixelheight";
	public static final String IMAGE_HEIGHT_ATTRIBUTE_NAME_v12 			= "height";
	public static final String IMAGE_VOXEL_DEPTH_ATTRIBUTE_NAME_v12 	= "voxeldepth";
	public static final String IMAGE_NSLICES_ATTRIBUTE_NAME_v12 		= "nslices";
	public static final String IMAGE_TIME_INTERVAL_ATTRIBUTE_NAME_v12 	= "timeinterval";
	public static final String IMAGE_NFRAMES_ATTRIBUTE_NAME_v12 		= "nframes";
	public static final String IMAGE_SPATIAL_UNITS_ATTRIBUTE_NAME_v12 	= "spatialunits";
	public static final String IMAGE_TIME_UNITS_ATTRIBUTE_NAME_v12 		= "timeunits";
	
	/*
	 * ALL BCellobjectS element
	 */

	public static final String BCellobject_COLLECTION_ELEMENT_KEY_v12 			= "AllBCellobjects";
	public static final String BCellobject_FRAME_COLLECTION_ELEMENT_KEY_v12 	= "BCellobjectsInFrame"; 
	public static final String BCellobject_ELEMENT_KEY_v12 					= "BCellobject"; 

	/*
	 * INITIAL BCellobject FILTER element
	 */
	
	public static final String INITIAL_BCellobject_FILTER_ELEMENT_KEY_v12		= "InitialBCellobjectFilter";
	
	/*
	 * FILTERS element for BCellobjectS and TRACKS
	 */
	
	public static final String BCellobject_FILTER_COLLECTION_ELEMENT_KEY_v12		= "BCellobjectFilterCollection";
	public static final String TRACK_FILTER_COLLECTION_ELEMENT_KEY_v12		= "TrackFilterCollection";
	public static final String FILTER_ELEMENT_KEY_v12						= "Filter";
	public static final String FILTER_FEATURE_ATTRIBUTE_NAME_v12 			= "feature";
	public static final String FILTER_VALUE_ATTRIBUTE_NAME_v12 				= "value";
	public static final String FILTER_ABOVE_ATTRIBUTE_NAME_v12 				= "isabove";
	
	/*
	 * BCellobject FILTERED elements
	 */
	
	public static final String FILTERED_BCellobject_ELEMENT_KEY_v12 				= "FilteredBCellobjects";
	public static final String FILTERED_BCellobject_COLLECTION_ELEMENT_KEY_v12 	= "FilteredBCellobjectsInFrame";
	public static final String BCellobject_ID_ELEMENT_KEY_v12 						= "BCellobjectID";

	/*
	 * TRACK elements
	 */
	
	public static final String TRACK_COLLECTION_ELEMENT_KEY_v12			= "AllTracks";
	public static final String TRACK_ELEMENT_KEY_v12 					= "Track";
	public static final String TRACK_ID_ATTRIBUTE_NAME_v12 				= "trackID";

	public static final String TRACK_EDGE_ELEMENT_KEY_v12				= "Edge";
	public static final String TRACK_EDGE_SOURCE_ATTRIBUTE_NAME_v12	 	= "sourceID";
	public static final String TRACK_EDGE_TARGET_ATTRIBUTE_NAME_v12	 	= "targetID";
	public static final String TRACK_EDGE_WEIGHT_ATTRIBUTE_NAME_v12	 	= "weight";

	
	/*
	 * TRACK FILTERED elements
	 */
	
	public static final String FILTERED_TRACK_ELEMENT_KEY_v12 				= "FilteredTracks";
	public static final String TRACK_ID_ELEMENT_KEY_v12 					= "TrackID";
	
}
