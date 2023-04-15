package edu.mit.csail.sdg.alloy4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TabPanel extends JPanel {
	
	public boolean active;
	boolean hover;
	
	JLabel content;	
	JLabel close_icon;
	Color active_txt_color = Color.black;
	Color inactive_txt_color = new Color(143, 143, 143);
	Icon close = OurUtil.loadIcon("images/close.png");
	Icon close_faded = OurUtil.loadIcon("images/closefaded.png");
	Icon act_tab_icon;
	Icon inact_tab_icon;
	
	public TabPanel(JLabel content, JLabel close_icon){
		super();
		this.content = content;
		this.close_icon = close_icon;
		active = true;
		hover = false;
	}
	
	public TabPanel(JLabel content, JLabel close_icon, Icon act_tab_icon, Icon inact_tab_icon){
		super();
		this.content = content;
		this.close_icon = close_icon;
		this.act_tab_icon = act_tab_icon;
		this.inact_tab_icon = inact_tab_icon;
		active = true;
		hover = false;
	}
	
	public void setActive() { 
		active = true;
		content.setForeground(active_txt_color);
		content.setIcon(act_tab_icon);
		close_icon.setIcon(close);
	}
	public void deactivate() { 
		active = false; 
		close_icon.setIcon(close_faded);
		content.setIcon(inact_tab_icon);
		content.setForeground(inactive_txt_color);
	}
	public void hover() { 
		hover = true;
		close_icon.setIcon(close);
		content.setIcon(act_tab_icon);
		content.setForeground(active_txt_color);
	}
	public void unhover() { 
		hover = false; 
		if(active) { 
			close_icon.setIcon(close);
			content.setIcon(act_tab_icon);
			content.setForeground(active_txt_color); 
		}
		else { 
			content.setForeground(inactive_txt_color); 
			content.setIcon(inact_tab_icon);
			close_icon.setIcon(close_faded);
		}
	}
	public boolean isHovered() { return hover; }
	
	@Override
	  protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension size = this.getSize();
		if (hover){
	        ImageIcon iCon = (ImageIcon) OurUtil.loadIcon("images/blue.png");
	        g.drawImage(iCon.getImage(), 0,0, size.width, size.height, this);
		}
		else if(active){
			 ImageIcon iCon = (ImageIcon) OurUtil.loadIcon("images/lightgray.png");
		        g.drawImage(iCon.getImage(), 0,0, size.width, size.height, this);
		}
		else{
			 ImageIcon iCon = (ImageIcon) OurUtil.loadIcon("images/gray.png");
		        g.drawImage(iCon.getImage(), 0,0, size.width, size.height, this);
		}
	}
}
