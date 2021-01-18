package listeners;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pluginTools.InteractiveAnalysis;


public class AddKeyListener implements KeyListener {

	
	InteractiveAnalysis parent;
	
	public AddKeyListener(InteractiveAnalysis parent) {

		this.parent = parent;
	
	}
	
	@Override
	public void keyTyped(KeyEvent e) {

		if (e.getKeyChar() == 'a')
		
			parent.AddDot = "a";
		
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyChar() == 'a')
		
			parent.AddDot = "a";
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		if (e.getKeyChar() == 'a')
		
			parent.AddDot = "a";
			
	}

}
