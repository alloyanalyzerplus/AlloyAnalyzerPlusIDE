package aunit.gui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class SummaryBarPanel  extends JPanel{
	int num_passing;
	int num_error;
	int num_failing;
	
	public SummaryBarPanel(int num_passing, int num_failing, int num_error){
		super();
		this.num_passing = num_passing;
		this.num_failing = num_failing;
		this.num_error = num_error;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Dimension size = this.getSize();
		ImageIcon green = (ImageIcon) OurUtil.loadIcon("images/green.png");
		ImageIcon red = (ImageIcon) OurUtil.loadIcon("images/red.png");
		ImageIcon maroon = (ImageIcon) OurUtil.loadIcon("images/maroon.png"); 
		int total = num_passing + num_failing + num_error;
		int loopPassing = (num_passing * size.width ) / total;
		int loopFailing = (num_failing * size.width) / total;
		int loopError = (num_error * size.width ) / total;
		
		for(int i = 0; i < loopPassing; i++) { 
		   g.drawImage(green.getImage(), i, 0, 1, size.height, this); }
		for(int i = loopPassing; i < loopFailing + loopPassing; i++) { 
		   g.drawImage(red.getImage(), i, 0, 1, size.height, this);}
		for(int i = loopPassing + loopFailing; i < (loopError + loopPassing + loopError); i++) { 
		   g.drawImage(maroon.getImage(), i, 0, 1, size.height, this); }

	}
}