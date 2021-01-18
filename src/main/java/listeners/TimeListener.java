package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import kalmanGUI.CovistoKalmanPanel;
import pluginTools.InteractiveAnalysis;
import pluginTools.InteractiveAnalysis.ValueChange;
import utility.ShowView;

public class TimeListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveAnalysis parent;
	final float min, max;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public TimeListener(final InteractiveAnalysis parent, final Label label, final String string, final float min, final float max,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		this.max = max;
		this.scrollbarSize = scrollbarSize;

		this.deltaScrollbar = deltaScrollbar;
		//deltaScrollbar.addMouseMotionListener(new BudMouseListener(parent, ValueChange.THIRDDIMmouse));
		deltaScrollbar.addMouseListener(new StandardMouseListener(parent, ValueChange.THIRDDIMmouse));
		deltaScrollbar.setBlockIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
		deltaScrollbar.setUnitIncrement(utility.Slicer.computeScrollbarPositionFromValue(2, min, max, scrollbarSize));
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		if(CovistoKalmanPanel.Skeletontime.isEnabled()) {
			parent.imp.getOverlay().clear();
		    parent.imp.updateAndDraw();	
		}
		
		parent.thirdDimension = (int) Math.round(utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize));


		deltaScrollbar
		.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension, min, max, scrollbarSize));
		
		label.setText(string +  " = "  + parent.thirdDimension);

		parent.inputFieldT.setText(Integer.toString((int)parent.thirdDimension));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		ShowView show = new ShowView(parent);
		show.shownewT();

	}
	


}