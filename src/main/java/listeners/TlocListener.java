package listeners;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import LabPluginutility.ShowView;
import ij.IJ;
import kalmanGUI.CovistoKalmanPanel;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import pluginTools.InteractiveAnalysis;
import pluginTools.InteractiveAnalysis.ValueChange;

public class TlocListener implements TextListener {
	
	
	final InteractiveAnalysis parent;
	
	boolean pressed;
	public TlocListener(final InteractiveAnalysis parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	   
		 tc.addKeyListener(new KeyListener(){
			 @Override
			    public void keyTyped(KeyEvent arg0) {
				   
			    }

			    @Override
			    public void keyReleased(KeyEvent arg0) {
			    	
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER ) {
						
						
						pressed = false;
						
					}

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			    	String s = tc.getText();
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER&& !pressed) {
						pressed = true;
			    		if (parent.thirdDimension > parent.thirdDimensionSize) {
							IJ.log("Max frame number exceeded, moving to last frame instead");
							parent.thirdDimension = parent.thirdDimensionSize;
						} else
							parent.thirdDimension = Integer.parseInt(s);
			    		ShowView show = new ShowView(parent);
					show.shownewT();
					parent.timeText.setText("Current T = " + parent.thirdDimension);
					parent.updatePreview(ValueChange.THIRDDIMmouse);
					
					parent.timeslider.setValue(LabPluginutility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
					parent.timeslider.repaint();
					parent.timeslider.validate();
					
					if(CovistoKalmanPanel.Skeletontime.isEnabled()) {
						parent.imp.getOverlay().clear();
					    parent.imp.updateAndDraw();	
					}
					
			    		
					 }

			    }
			});
	

	

}

}
