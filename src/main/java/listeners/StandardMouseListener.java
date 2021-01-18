package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import pluginTools.InteractiveAnalysis;
import pluginTools.InteractiveAnalysis.ValueChange;


public class StandardMouseListener implements MouseListener
{
	final InteractiveAnalysis parent;
	final ValueChange change;

	public StandardMouseListener( final InteractiveAnalysis parent, final ValueChange change)
	{
		this.parent = parent;
		this.change = change;
	}
	
	

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		
		parent.updatePreview(change); 

		
	}

	@Override
	public void mousePressed( MouseEvent arg0 ){
		
		
	}

	@Override
	public void mouseExited( MouseEvent arg0 ) {
	
	}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {
	}

	@Override
	public void mouseClicked( MouseEvent arg0 ) {
		
		

	}
	}



