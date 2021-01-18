package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComboBox;
import ij.WindowManager;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.FileChooser;
import pluginTools.simplifiedio.SimplifiedIO;

public class ChooseOrigMap implements ActionListener {
	
	
	final FileChooser parent;
	final JComboBox<String> choice;
	
	
	public ChooseOrigMap(final FileChooser parent, final JComboBox<String> choice ) {
		
		
		this.parent = parent;
		this.choice = choice;
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		String imagename = (String) choice.getSelectedItem();
		
		
		
    	parent.impOrig = WindowManager.getImage(imagename);
    	if(parent.impOrig!=null) {
    		parent.imageOrig = SimplifiedIO.openImage(
    				parent.impOrig.getOriginalFileInfo().directory + parent.impOrig.getOriginalFileInfo().fileName,
    				new FloatType());
    		
    			
    		parent.calibrationX = parent.impOrig.getCalibration().pixelWidth;
			parent.calibrationY = parent.impOrig.getCalibration().pixelHeight;
			parent.calibrationZ = parent.impOrig.getCalibration().pixelDepth;
			parent.FrameInterval = parent.impOrig.getCalibration().frameInterval;
			parent.TimeTotal = parent.imageOrig.dimension(3);
			
			
			if (parent.FrameInterval == 0)
				parent.FrameInterval = 1;
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator(','); 
			DecimalFormat df = new DecimalFormat(("#.###"), otherSymbols);
			parent.FieldinputLabelcalT.setText(String.valueOf(df.format(parent.FrameInterval))); 
			parent.inputFieldcalX.setText(String.valueOf(df.format(parent.calibrationX)));
			parent.inputFieldcalY.setText(String.valueOf(df.format(parent.calibrationY)));
			parent.inputFieldcalZ.setText(String.valueOf(df.format(parent.calibrationZ)));
			parent.inputFieldT.setText(String.valueOf(df.format(parent.TimeTotal)));
	}
 
		
	}
	

}
