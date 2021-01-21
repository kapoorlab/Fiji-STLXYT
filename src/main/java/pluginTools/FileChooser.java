package pluginTools;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ij.ImagePlus;
import ij.WindowManager;
import io.scif.img.ImgIOException;
import listeners.ChooseOrigMap;
import listeners.ChooseSegMap;
import listeners.GoCellListener;
import listeners.GoFilamentListener;
import listeners.GoFreeFLListener;
import listeners.GoMaskFLListener;
import listeners.GoNoTMListener;
import listeners.GoTMListener;
import loadfile.CovistoOneChFileLoader;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.simplifiedio.SimplifiedIO;
import tracking.BCellobject;
import tracking.Cellobject;

public class FileChooser extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JFrame Cardframe = new JFrame("Our 2D + Time World Lab Plugin");
	public JPanel panelCont = new JPanel();
	public JPanel Panelfileoriginal = new JPanel();
	
	public JPanel PanelTM = new JPanel();
	public JPanel PanelFilament = new JPanel();
	
	public ImagePlus impOrig, impSeg, impMask;
	
	public File impOrigfile;
	
	public JPanel panelFirst = new JPanel();
	public JPanel Panelfile = new JPanel();
	public JPanel Paneldone = new JPanel();
	public JPanel Panelrun = new JPanel();
	public JPanel Microscope = new JPanel();
	
	public final Insets insets = new Insets(10, 10, 0, 10);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();
	
	public final String[] imageNames, blankimageNames;
	public JComboBox<String> ChooseImage;
	public JComboBox<String> ChoosesuperImage;
	public JComboBox<String> ChooseoriginalImage;
	public JButton Done = new JButton("Start Computing");
	public HashMap<Integer, ArrayList<Cellobject>> CSVFileInfo = new HashMap<Integer, ArrayList<Cellobject>>(); 
	
	public String chooseCellSegstring = "2D + time Segmentation Image";
	public Border chooseCellSeg = new CompoundBorder(new TitledBorder(chooseCellSegstring), new EmptyBorder(c.insets));
    
	public String chooseTMstring = "TrackMate or Non TrackMate Analysis";
	public Border chooseTM = new CompoundBorder(new TitledBorder(chooseTMstring), new EmptyBorder(c.insets));
	
	
	public String chooseMaskSegstring = "Segmentation for Cells and  Mask";
	public Border chooseMaskSeg = new CompoundBorder(new TitledBorder(chooseMaskSegstring), new EmptyBorder(c.insets));
	public JProgressBar jpb = new JProgressBar();

	public int thirdDimensionsliderInit = 1;
	public final int scrollbarSize = 1000;
	public JScrollBar Zslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	
	public RandomAccessibleInterval<FloatType> imageOrig;
	public RandomAccessibleInterval<IntType> imageSeg;
	
	
	public String chooseoriginalCellfilestring = "Computation is done inside a mask region/whole image";
	public Border chooseoriginalCellfile = new CompoundBorder(new TitledBorder(chooseoriginalCellfilestring),
			new EmptyBorder(c.insets));

	public String donestring = "Done Selection";
	public Border LoadBtrack = new CompoundBorder(new TitledBorder(donestring), new EmptyBorder(c.insets));

	public Label inputLabelcalX, inputLabelcalY, inputLabelcalT;
	public double calibrationX, calibrationY, FrameInterval, TimeTotal;
	
	public Label inputZ, inputT;
	public TextField inputFieldT;

	public TextField inputFieldcalX, inputFieldcalY, FieldinputLabelcalT;
	public Border microborder = new CompoundBorder(new TitledBorder("Microscope parameters"),
			new EmptyBorder(c.insets));
	public boolean DoMask = false;
	public boolean NoMask = true;
	
	public boolean DoTrackMate = true;
	public boolean DoNotTrackMate = false;
	
	public boolean FilamentTrackMate = true;
	public boolean NotFilamentTrackMate = false;
	
	public JButton Checkpointbutton = new JButton("Data from CSV");
	
	
	public CheckboxGroup cellmode = new CheckboxGroup();
	public Checkbox FreeMode = new Checkbox("No Mask", NoMask, cellmode);
	public Checkbox MaskMode = new Checkbox("With Mask", DoMask, cellmode);

	public CheckboxGroup trackmatemode = new CheckboxGroup();
	public Checkbox NonTrackMateMode = new Checkbox("Other Lab Specific Analysis", DoNotTrackMate, trackmatemode);
	
	public Checkbox FilamentTrackMateMode = new Checkbox("BTrackMate for Filaments", FilamentTrackMate, trackmatemode);
	public Checkbox NonFilamentTrackMateMode = new Checkbox("BTrackMate for Cells", NotFilamentTrackMate, trackmatemode);
	
	public FileChooser() {

		inputLabelcalX = new Label("Pixel calibration in X(um)");
		inputFieldcalX = new TextField(5);
		inputFieldcalX.setText("1");

		inputLabelcalY = new Label("Pixel calibration in Y(um)");
		inputFieldcalY = new TextField(5);
		inputFieldcalY.setText("1");

		inputLabelcalT = new Label("Pixel calibration in T (sec/min)");
		FieldinputLabelcalT = new TextField(5);
		FieldinputLabelcalT.setText("1");
		panelFirst.setLayout(layout);
		
		inputT = new Label("Total TimePoints");
		inputFieldT = new TextField(5);
		inputFieldT.setText("1");
		
		panelFirst.setLayout(layout);

		Paneldone.setLayout(layout);
		Microscope.setLayout(layout);
		CardLayout cl = new CardLayout();
		calibrationX = Float.parseFloat(inputFieldcalX.getText());
		calibrationY = Float.parseFloat(inputFieldcalY.getText());

		FrameInterval = Float.parseFloat(FieldinputLabelcalT.getText());

		panelCont.setLayout(cl);
		panelCont.add(panelFirst, "1");
		imageNames = WindowManager.getImageTitles();
		blankimageNames = new String[imageNames.length + 1];
		blankimageNames[0] = " ";

		for (int i = 0; i < imageNames.length; ++i)
			blankimageNames[i + 1] = imageNames[i];

		ChooseImage = new JComboBox<String>(blankimageNames);
		ChooseoriginalImage = new JComboBox<String>(blankimageNames);
		ChoosesuperImage = new JComboBox<String>(blankimageNames);

		CovistoOneChFileLoader original = new CovistoOneChFileLoader(chooseoriginalCellfilestring, blankimageNames);

		Panelfileoriginal = original.SingleChannelOption();

		PanelTM.add(FilamentTrackMateMode, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		PanelTM.add(NonFilamentTrackMateMode, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		PanelTM.add(NonTrackMateMode, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		PanelTM.setBorder(chooseTM);

		panelFirst.add(PanelTM, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		
		
		panelFirst.add(Panelfileoriginal, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		original.ChooseImage.addActionListener(new ChooseOrigMap(this, original.ChooseImage));

		panelFirst.add(FreeMode, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelFirst.add(MaskMode, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		CovistoOneChFileLoader segmentation = new CovistoOneChFileLoader(chooseCellSegstring, blankimageNames);
		Panelfile = segmentation.SingleChannelOption();

		panelFirst.add(Panelfile, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		
		Paneldone.add(Checkpointbutton, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Paneldone.add(Done, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		Paneldone.setBorder(LoadBtrack);
		
		
		
		panelFirst.add(Paneldone, new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputLabelcalX, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputFieldcalX, new GridBagConstraints(0, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(inputLabelcalY, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Microscope.add(inputFieldcalY, new GridBagConstraints(3, 1, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(inputLabelcalT, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.add(FieldinputLabelcalT, new GridBagConstraints(0, 5, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));
		
		//Microscope.add(inputT, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
		//		GridBagConstraints.RELATIVE, insets, 0, 0));

		//Microscope.add(inputFieldT, new GridBagConstraints(0, 9, 3, 1, 0.1, 0.0, GridBagConstraints.WEST,
		//		GridBagConstraints.RELATIVE, insets, 0, 0));

		Microscope.setBorder(microborder);
		
		
		
		panelFirst.add(Microscope, new GridBagConstraints(0, 9, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		// Listeneres

		FreeMode.addItemListener(new GoFreeFLListener(this));
		MaskMode.addItemListener(new GoMaskFLListener(this));
		
		NonTrackMateMode.addItemListener(new GoNoTMListener(this));
		
		FilamentTrackMateMode.addItemListener(new GoFilamentListener(this));
		NonFilamentTrackMateMode.addItemListener(new GoCellListener(this));
		
		
		segmentation.ChooseImage.addActionListener(new ChooseSegMap(this, segmentation.ChooseImage));

		inputFieldcalX.addTextListener(new CalXListener());
		inputFieldcalY.addTextListener(new CalYListener());
		FieldinputLabelcalT.addTextListener(new CalTListener());
		inputFieldT.addTextListener(new InputTListener());
		Done.addActionListener(new DoneListener());
		panelFirst.setVisible(true);
		cl.show(panelCont, "1");
		Cardframe.add(panelCont, "Center");
		ChoosesuperImage.setEnabled(true);

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public class CalXListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				calibrationX = Double.parseDouble(s);
		}

	}

	public class CalYListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				calibrationY = Double.parseDouble(s);
		}

	}


	public class CalTListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				FrameInterval = Float.parseFloat(s);

		}

	}
	
	public class InputTListener implements TextListener {

		@Override
		public void textValueChanged(TextEvent e) {
			final TextComponent tc = (TextComponent) e.getSource();
			String s = tc.getText();

			if (s.length() > 0)
				 TimeTotal = Float.parseFloat(s);

		}

	}


	public class DoneListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				DoneCurr(Cardframe);
			} catch (ImgIOException e1) {

			}
		}

	}

	public void DoneCurr(Frame parent) throws ImgIOException {

		
		
		WindowManager.closeAllWindows();
		Done.setEnabled(false);
		Checkpointbutton.setEnabled(false);
		String name = impOrig.getOriginalFileInfo().fileName;
		Cardframe.remove(jpb);
		
		if (imageOrig.numDimensions() > 4) {

			JOptionPane.showMessageDialog(new JFrame(),
					"This tracker is for 2D + time images only, your image has higher dimensionality, split the channels perhaps?");
		}
		imageSeg = SimplifiedIO.openImage(
      			impSeg.getOriginalFileInfo().directory + impSeg.getOriginalFileInfo().fileName,
				new IntType());

		
		if (DoMask) {

			
			RandomAccessibleInterval<IntType> imageMask = SimplifiedIO.openImage(
					impMask.getOriginalFileInfo().directory + impMask.getOriginalFileInfo().fileName, new IntType());
			
			
            if(imageSeg!=null) {				
            	
            	
            	ImageObjects ImagePairs  = 
    					KeepMaskRegion(imageOrig, imageMask, imageSeg);
			
			assert (imageOrig.numDimensions() == imageSeg.numDimensions());
			
			InteractiveAnalysis CellCollection = new InteractiveAnalysis(ImagePairs.imageOrig, ImagePairs.imageSeg,CSVFileInfo, new File(impOrig.getOriginalFileInfo().directory), 
					impOrig.getOriginalFileInfo().fileName, calibrationX, calibrationY,
					FrameInterval, name, DoTrackMate, FilamentTrackMate);
			
			

			CellCollection.run(null);
			
			jpb = CellCollection.jpb;
			
            }
            
            else if (CSVFileInfo!=null && CSVFileInfo.size() > 0) {
            	
            	
    			
    			InteractiveAnalysis CellCollection = new InteractiveAnalysis(imageOrig,null,CSVFileInfo, new File(impOrig.getOriginalFileInfo().directory), 
    					impOrig.getOriginalFileInfo().fileName, calibrationX, calibrationY,
    					FrameInterval, name, DoTrackMate, FilamentTrackMate);
    			

    			CellCollection.run(null);
    			
    			jpb = CellCollection.jpb;
            	
            	
            }
			
			
					
		}

		if (NoMask) {
			
			if(imageSeg!=null) {
				
			
			
			InteractiveAnalysis CellCollection = new InteractiveAnalysis(imageOrig, imageSeg,CSVFileInfo, new File(impOrig.getOriginalFileInfo().directory), impOrig.getOriginalFileInfo().fileName, calibrationX, calibrationY,
					FrameInterval, name, DoTrackMate, FilamentTrackMate);
			
			CellCollection.run(null);

			jpb = CellCollection.jpb;
			
			
			}
			
			else if (CSVFileInfo!=null && CSVFileInfo.size() > 0) {
				
				
				
				InteractiveAnalysis CellCollection = new InteractiveAnalysis(imageOrig, null, CSVFileInfo, new File(impOrig.getOriginalFileInfo().directory), impOrig.getOriginalFileInfo().fileName, calibrationX, calibrationY,
						FrameInterval, name, DoTrackMate, FilamentTrackMate);
				

				CellCollection.run(null);

				jpb = CellCollection.jpb;
				
				
				
			}
		
			
			
		}
		
		
		
		Cardframe.add(jpb, "Last");
		Cardframe.repaint();
		Cardframe.validate();

		calibrationX = Float.parseFloat(inputFieldcalX.getText());
		calibrationY = Float.parseFloat(inputFieldcalY.getText());
		FrameInterval = Float.parseFloat(FieldinputLabelcalT.getText());

	}

	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

	}

	
	
	public class ImageObjects{
	
		final RandomAccessibleInterval<FloatType> imageOrig;
		final RandomAccessibleInterval<IntType> imageBigMask;
		final RandomAccessibleInterval<IntType> imageSeg;
	
	public ImageObjects(RandomAccessibleInterval<FloatType> imageOrig, RandomAccessibleInterval<IntType> imageBigMask,
			RandomAccessibleInterval<IntType>imageSeg) {
		
		this.imageOrig = imageOrig;
		this.imageBigMask = imageBigMask;
		this.imageSeg = imageSeg;
		
	}
		
		
		
		
	}

	@SuppressWarnings("deprecation")
	protected RandomAccessibleInterval<IntType> CreateBorderMask(RandomAccessibleInterval<FloatType> imageOrig) {

		RandomAccessibleInterval<IntType> imageSegB = new CellImgFactory<IntType>().create(imageOrig, new IntType());

		RandomAccess<IntType> ranac = imageSegB.randomAccess();

		Cursor<FloatType> cur = Views.iterable(imageOrig).localizingCursor();

		while (cur.hasNext()) {

			cur.fwd();

			if (cur.getDoublePosition(0) <= imageOrig.dimension(0) - 1 && cur.getDoublePosition(0) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

			if (cur.getDoublePosition(1) <= imageOrig.dimension(1) - 1 && cur.getDoublePosition(1) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

			if (cur.getDoublePosition(2) <= imageOrig.dimension(2) - 1 && cur.getDoublePosition(2) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

			if (cur.getDoublePosition(3) <= imageOrig.dimension(3) - 1 && cur.getDoublePosition(3) > 1) {

				ranac.setPosition(cur);

				ranac.get().setOne();
			}

		}

		return imageSegB;

	}
	  
	    public  RandomAccessibleInterval< IntType > copyImage( final RandomAccessibleInterval<IntType > input )
	    {
	        // create a new Image with the same properties
	        // note that the input provides the size for the new image as it implements
	        // the Interval interface
	    	
	    	RandomAccessibleInterval< IntType > output = new CellImgFactory<IntType>().create( input,
					new IntType() );
	 
	        // create a cursor for both images
	        Cursor< IntType > cursorInput = Views.iterable(input).cursor();
	        RandomAccess< IntType > randomAccess = output.randomAccess();
	 
	        // iterate over the input
	        while ( cursorInput.hasNext())
	        {
	            // move both cursors forward by one pixel
	            cursorInput.fwd();
	            randomAccess.setPosition( cursorInput );
	 
	            // set the value of this pixel of the output image to the same as the input,
	            // every Type supports T.set( T type )
	            randomAccess.get().set( cursorInput.get() );
	        }
	 
	        // return the copy
	        return output;
	    }
	
		protected ImageObjects KeepMaskRegion(RandomAccessibleInterval<FloatType> imageOrig,
				RandomAccessibleInterval<IntType> imageMask,RandomAccessibleInterval<IntType> imageSegA) {

			
			RandomAccessibleInterval<IntType> imageBigSeg = copyImage(imageSegA);

			
			Cursor<IntType> Bigcursor = Views.iterable(imageMask).localizingCursor();
			
			
			RandomAccess<IntType> segimage = imageBigSeg.randomAccess();
			
			while(Bigcursor.hasNext()) {
				
				Bigcursor.fwd();
				segimage.setPosition(Bigcursor);
				if(Bigcursor.get().get() == 0 ) {
					
							
					segimage.get().setZero();
				}
				
			}

			ImageObjects images = new ImageObjects(imageOrig, imageMask, imageBigSeg);
			return images;
		}  
	    
	 
		
	    
}
