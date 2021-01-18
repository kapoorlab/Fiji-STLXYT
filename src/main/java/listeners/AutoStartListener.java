package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveAnalysis;



public class AutoStartListener implements TextListener {
	
	final InteractiveAnalysis parent;
	
	public AutoStartListener(final InteractiveAnalysis parent) {
		
		this.parent = parent;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		

		final TextComponent tc = (TextComponent) e.getSource();
		String s = tc.getText();
		
		if (s.length() > 0)
			parent.AutostartTime = Integer.parseInt(s);
		
		
	}

}

