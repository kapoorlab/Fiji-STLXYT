package pluginTools;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import TrackBuddy.plugin.trackmate.BCellobjectCollection;
import coreFunction.BackgroundTMFunction;
import functionPanel.CovistoFunctionPanel;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.plugin.HyperStackConverter;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import kalmanGUI.CovistoKalmanPanel;
import listeners.AddKeyListener;
import listeners.AutoEndListener;
import listeners.AutoStartListener;
import listeners.TimeListener;
import listeners.TlocListener;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import tracking.Cellobject;
import tracking.Roiobject;
import tracking.BCellobject;

public class InteractiveAnalysis extends JPanel implements PlugIn {

	private static final long serialVersionUID = 1L;
	public String usefolder = IJ.getDirectory("imagej");
	public String addToName = "LabPlugin2DWorld_";
	public String inputstring;
	public final int scrollbarSize = 1000;
	public Set<Integer> pixellist;
	public NumberFormat nf;
	public RandomAccessibleInterval<FloatType> originalimg;
	public RandomAccessibleInterval<IntType> Segoriginalimg;
	
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RandomAccessibleInterval<IntType> CurrentViewInt;
	
	public HashMap<String, ArrayList<Roiobject>> OvalRois = new HashMap<String, ArrayList<Roiobject>>();
	public final String NameA;
	public int ndims;
	public MouseListener mvl;
	public KeyListener kvl;
	public MouseListener tvl;
	public String AddDot = "B";
	public MouseMotionListener tvml;
	public int borderexpand = 100;
	public int Dotsize = 2;
	public Color RemoveColor = new Color(255,0,0); //Color.RED;
	public Color Color = new Color(255,192,203);
	
	public int[] Clickedpoints;
	public HashMap<String, Integer> AccountedT;
	public HashMap<String, ArrayList<BCellobject>> Allcells;
	public Overlay overlay;
	public ImagePlus imp;
	public String selectedID;
	public int row;
	public int tablesize;
	public int thirdDimensionSize;
	public ImagePlus impA;
	public int rowchoice;
	public int thirdDimension = 1;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public JProgressBar jpb;
	public ArrayList<int[]> ZTRois;
	public HashMap<Integer, ArrayList<Cellobject>> CSVInfoFile = new HashMap<Integer, ArrayList<Cellobject>>();
	public MouseMotionListener ml;
	public ImageJ ij;
	public double calibrationX;
	public double calibrationY;
	public double timecal;
	public File saveFile;
	public final File defaultDirectory;
	public BCellobjectCollection cells = new BCellobjectCollection();
	public HashMap<Integer, Integer> IDlist = new HashMap<Integer, Integer>();
	public HashMap<String, BCellobject> Finalresult;
	public final Boolean TrackMate;
	public final Boolean FilamentMode;
	// Input  and its segmentation


	// Input RGB and one flourescent channel segmentation images
	public InteractiveAnalysis(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<IntType> Segoriginalimg,
			final File defaultDirectory, final String NameA, final double calibrationX,
			final double calibrationY, final double timecal, String inputstring, final Boolean TrackMate, final Boolean FilamentMode) {

		this.originalimg = originalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.NameA = NameA;
		this.calibrationX = calibrationX;
		this.calibrationY = calibrationY;
		this.defaultDirectory = defaultDirectory;
		this.timecal = timecal;
		this.ndims = originalimg.numDimensions();
		this.inputstring = inputstring;
		this.TrackMate = TrackMate;
        this.FilamentMode = FilamentMode;
	}

	// Input Image and one flourescent channel and mask images
	public InteractiveAnalysis(final RandomAccessibleInterval<FloatType> originalimg,
			final RandomAccessibleInterval<IntType> Segoriginalimg,
			final HashMap<Integer,ArrayList<Cellobject>> CSVInfoFile,
			final File defaultDirectory, final String NameA, final double calibrationX,
			final double calibrationY, final double timecal, String inputstring, final Boolean TrackMate, final Boolean FilamentMode) {

		this.originalimg = originalimg;
		this.Segoriginalimg = Segoriginalimg;
		this.NameA = NameA;
		this.CSVInfoFile = CSVInfoFile;
		this.calibrationX = calibrationX;
		this.calibrationY = calibrationY;
		this.defaultDirectory = defaultDirectory;
		this.timecal = timecal;
		this.ndims = originalimg.numDimensions();
		this.inputstring = inputstring;
        this.TrackMate = TrackMate;
        this.FilamentMode = FilamentMode;
	}

	
	public ImageStack prestack;
	public JTable table;
	public JTableHeader header;

	public static enum ValueChange {

		THIRDDIMmouse, All;

	}

	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}


	public int getTimeMax() {

		return thirdDimensionSize;
	}
	
	
	

	@Override
	public void run(String arg0) {

		Finalresult = new HashMap<String, BCellobject>();
		ZTRois = new ArrayList<int[]>();
		AccountedT = new HashMap<String, Integer>();
		jpb = new JProgressBar();
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		nf.setGroupingUsed(false);
		pixellist = new HashSet<Integer>();
		ij = new ImageJ();
		ij.ui().showUI();

			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			AutostartTime = thirdDimension;
			AutoendTime = thirdDimensionSize;

			setTime(thirdDimension);
			CurrentView = LabPluginutility.Slicer.getCurrentLabView(originalimg, thirdDimension, thirdDimensionSize);
			
			
			if (Segoriginalimg!=null){

				// Convert input binary image into integer labelled image
				
				CurrentViewInt = LabPluginutility.Slicer.getCurrentLabView(Segoriginalimg, thirdDimension, thirdDimensionSize);
				CurrentViewInt = LabelSegmentationImage(CurrentViewInt);
				
			}
			
			imp = ImageJFunctions.show(CurrentView, "Original Image");
			imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);


			// Switch to either start the Lab specific plugin Card Farame
			if(TrackMate == false) {
				//StartNonTMDisplayer();
				Card();
			}
			
			// Or to collect the cells/ filament end points and start the trackmate plugin
			else {
				
				StartTMDisplayer(FilamentMode);
			}


		
		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();
		saveFile = new java.io.File(".");

	}
	
	public RandomAccessibleInterval<IntType> LabelSegmentationImage(RandomAccessibleInterval<IntType> SegmentationImage) {
		
		   long[] dims = new long[SegmentationImage.numDimensions()];
		   SegmentationImage.dimensions(dims);
	       RandomAccessibleInterval<IntType> indexImg = ArrayImgs.ints(dims);
	       RandomAccessibleInterval<IntType> LabelImage =ArrayImgs.ints(dims);	
	       ImgLabeling<Integer, IntType> labeling = new ImgLabeling<>(indexImg);
	       Iterator<Integer> labels = new Iterator<Integer>()
	      {
	          private int i = 1;

	          @Override
	          public boolean hasNext()
	          {
	              return true;
	          }

	          @Override
	          public Integer next()
	          {
	              return i++;
	          }

	          @Override
	          public void remove()
	          {}
	      };
	      
	      ConnectedComponents.labelAllConnectedComponents(SegmentationImage, labeling, labels, StructuringElement.FOUR_CONNECTED);
	      
		  LabelImage = labeling.getIndexImg();
			
	      return LabelImage;		
			
		}

	public void updatePreview(final ValueChange change) {

		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);

		}

		if (change == ValueChange.THIRDDIMmouse) {
		
		       
		        // create a view on the source with this interval
		      
				imp.setTitle("Active Image" + " " + "time point : " + thirdDimension);
				String TID = Integer.toString(thirdDimension);
				AccountedT.put(TID, thirdDimension);
				CurrentView = LabPluginutility.Slicer.getCurrentLabView(originalimg, thirdDimension, thirdDimensionSize);
				if (Segoriginalimg!=null) {
				CurrentViewInt = LabPluginutility.Slicer.getCurrentLabView(Segoriginalimg, thirdDimension,
						thirdDimensionSize);
				CurrentViewInt = LabelSegmentationImage(CurrentViewInt);
				}
				
                else {
					
					CurrentViewInt = null;
				}
				
			  
				repaintView(CurrentView);
				if (TrackMate == false) {
					if (CovistoKalmanPanel.Skeletontime.isEnabled()) {
						imp.getOverlay().clear();
						imp.updateAndDraw();
						
						//StartNonTMDisplayer();

					}
				}

				else {

					imp.getOverlay().clear();
					imp.updateAndDraw();

				}
		}

	}

	public void StartTMDisplayer(final Boolean FilamentMode) {
		
		
		
		
		BackgroundTMFunction display = new BackgroundTMFunction(this, jpb);

		display.execute();
	}


	

	public void repaintView(RandomAccessibleInterval<FloatType> Activeimage) {
		
	
		imp.getCanvas().addKeyListener(new AddKeyListener(this));
		IJ.selectWindow(imp.getTitle());
		if (imp == null || !imp.isVisible()) {
			imp = ImageJFunctions.show(Activeimage);

		}

		else {
			
			     
				final float[] pixels = (float[]) imp.getProcessor().getPixels();

				final Cursor<FloatType> c = Views.iterable(Activeimage).cursor();

			
				for (int i = 0; i < pixels.length; ++i)
				
					pixels[i] = c.next().get();
			imp.updateAndDraw();

		}
		

	}

	public JFrame Cardframe = new JFrame("Cell Function Tracker");
	public JPanel panelFirst = new JPanel();
	public JPanel Original = new JPanel();
	public JPanel PanelSelectFile = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel FunctionPanel = new JPanel();
	public JPanel panelCont = new JPanel();
	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	public JScrollPane scrollPane;
	public JFileChooser chooserA = new JFileChooser();
	public String choosertitleA;
	int SizeX = 400;
	int SizeY = 200;
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public Label autoTstart, autoTend;
	public TextField startT, endT;
	public Label timeText = new Label("Current T = " + 1, Label.CENTER);

	public Label thirdexplain = new Label("text", Label.CENTER);
	public Label fourthexplain = new Label("Some text", Label.CENTER);
	public Label fifthexplain = new Label("Some more text", Label.CENTER);
	public String timestring = "Current T";
	
	int textwidth = 5;
	public int AutostartTime, AutoendTime;
	public TextField inputFieldT;
	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	
	public JScrollBar Zslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JButton Savebutton = new JButton("Save Track");
	public JButton Cellbutton = new JButton("Enter BTrackmate");
	public JButton Restartbutton = new JButton("Restart");
	public JButton Checkpointbutton = new JButton("Checkpoint Save");
	public JButton Batchbutton = new JButton("Save Parameters for batch mode and exit");
	public JButton SaveAllbutton = new JButton("Save All Tracks");
	public Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
	public Label inputtrackLabel;
	public TextField inputtrackField;
	public Border selectcell = new CompoundBorder(new TitledBorder("Select Cell"), new EmptyBorder(c.insets));
	public TextField inputField = new TextField();
	public Border origborder = new CompoundBorder(new TitledBorder("Results files"), new EmptyBorder(c.insets));
	public final JButton ChooseDirectory = new JButton("Choose Directory to save results in");

	public void Card() {

		Cellbutton.setEnabled(false);
		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);

		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");

		inputtrackLabel = new Label("Enter trackID to save");
		inputtrackField = new TextField(textwidth);

		Object[] colnames = new Object[] { "Track Id", "Location X", "Location Y", "Location T", "Growth Rate" };

		Object[][] rowvalues = new Object[0][colnames.length];
		if (Finalresult != null && Finalresult.size() > 0) {

			rowvalues = new Object[Finalresult.size()][colnames.length];

		}

		table = new JTable(rowvalues, colnames);
		header = table.getTableHeader();
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		autoTend = new Label("End time for tracking");
		endT = new TextField(textwidth);
		endT.setText(Integer.toString(AutoendTime));

		scrollPane = new JScrollPane(table);
		Original.setLayout(layout);
		scrollPane.getViewport().add(table);
		scrollPane.setAutoscrolls(true);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		PanelSelectFile.add(scrollPane, BorderLayout.CENTER);

		PanelSelectFile.setBorder(selectfile);
		int size = 100;
		table.getColumnModel().getColumn(0).setPreferredWidth(size);
		table.getColumnModel().getColumn(1).setPreferredWidth(size);
		table.getColumnModel().getColumn(2).setPreferredWidth(size);
		table.getColumnModel().getColumn(3).setPreferredWidth(size);
		table.getColumnModel().getColumn(4).setPreferredWidth(size);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setFillsViewportHeight(true);
		table.isOpaque();
		scrollPane.setMinimumSize(new Dimension(300, 200));
		scrollPane.setPreferredSize(new Dimension(300, 200));

		panelFirst.setLayout(layout);
		overlay = imp.getOverlay();
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);

		}

		autoTstart = new Label("Start time for computing");
		startT = new TextField(textwidth);
		startT.setText(Integer.toString(AutostartTime));

		autoTend = new Label("End time for computing");
		endT = new TextField(textwidth);
		endT.setText(Integer.toString(AutoendTime));

		Timeselect.setLayout(layout);
		inputFieldT = new TextField(textwidth);
		inputFieldT.setText(Integer.toString(thirdDimension));

		// Put time slider

		Timeselect.add(timeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(timeslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(inputFieldT, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		Timeselect.add(autoTstart, new GridBagConstraints(2, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Timeselect.add(startT, new GridBagConstraints(2, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		
		Timeselect.add(autoTend, new GridBagConstraints(2, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Timeselect.add(endT, new GridBagConstraints(2, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Timeselect.add(thirdexplain, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.add(fourthexplain, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.add(fifthexplain, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Timeselect.setBorder(timeborder);

		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		FunctionPanel = CovistoFunctionPanel.FunctionPanel();

		panelFirst.add(FunctionPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));


		panelFirst.add(PanelSelectFile, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputtrackLabel, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(inputtrackField, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(ChooseDirectory, new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Original.add(Savebutton, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.add(SaveAllbutton, new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		
		Original.add(Checkpointbutton, new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Original.setBorder(origborder);

		panelFirst.add(Original, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));


		inputFieldT.addTextListener(new TlocListener(this, false));

		timeslider.addAdjustmentListener(new TimeListener(this, timeText, timestring, thirdDimensionsliderInit,
				thirdDimensionSize, scrollbarSize, timeslider));
		
		
		
		endT.addTextListener(new AutoEndListener(this));
		startT.addTextListener(new AutoStartListener(this));
		
		FunctionPanel.validate();
		FunctionPanel.repaint();

		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);

		imp.getCanvas().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 27)
					EscapePressed = true;

			}

		});

	}

	public Boolean EscapePressed = false;

}