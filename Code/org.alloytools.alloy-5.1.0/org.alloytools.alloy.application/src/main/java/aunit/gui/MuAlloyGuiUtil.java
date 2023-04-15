package aunit.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import edu.mit.csail.sdg.alloy4.TabPanel;

public class MuAlloyGuiUtil {

    public MuAlloyGuiUtil() {
    }

    public static JLabel makeLabel(String title, String imageLocation) {
        return new JLabel(title, new ImageIcon(imageLocation), JLabel.LEFT);
    }

    public static TabPanel makeTab(JLabel label, JLabel labelClose, Icon active, Icon inactive) {
        JLabel space = new JLabel(" ");
        JLabel black = new JLabel("", OurUtil.loadIcon("images/black.png"), JLabel.LEFT);
        label.setHorizontalAlignment(JLabel.LEFT);
        TabPanel tab = new TabPanel(label, labelClose, active, inactive);
        tab.add(space);
        tab.add(label);
        tab.add(labelClose);
        tab.add(space);
        tab.add(black);
        BoxLayout boxLayoutAUnit = new BoxLayout(tab, BoxLayout.X_AXIS);
        tab.setLayout(boxLayoutAUnit);
        tab.setAlignmentX(1.0f);
        tab.setAlignmentY(1.0f);
        return tab;
    }

    public static TabPanel makeTab(JLabel label, Icon active, Icon inactive) {
        JLabel space = new JLabel(" ");
        JLabel black = new JLabel("", OurUtil.loadIcon("images/black.png"), JLabel.LEFT);
        label.setHorizontalAlignment(JLabel.LEFT);
        TabPanel tab = new TabPanel(label, new JLabel(), active, inactive);
        tab.add(space);
        tab.add(label);
        tab.add(space);
        tab.add(black);
        BoxLayout boxLayoutAUnit = new BoxLayout(tab, BoxLayout.X_AXIS);
        tab.setLayout(boxLayoutAUnit);
        tab.setAlignmentX(1.0f);
        tab.setAlignmentY(1.0f);
        return tab;
    }

    public static JPanel makeSummary(int numP, int numF, long totalTime, Font font) {

        JPanel summary = new JPanel();
        summary.setBackground(Color.WHITE);
        GridLayout grid = new GridLayout(2, 4, 0, 0);
        summary.setLayout(grid);

        summary.add(new JLabel("", OurUtil.loadIcon("images/passing.png"), JLabel.CENTER));
        summary.add(new JLabel("", OurUtil.loadIcon("images/failing.png"), JLabel.CENTER));
        summary.add(new JLabel("", OurUtil.loadIcon("images/clocking.png"), JLabel.CENTER));

        JLabel passingLabel = new JLabel("<html># Killed: <b><font color = green>" + numP, OurUtil.loadIcon("two.png"), JLabel.CENTER);
        passingLabel.setHorizontalTextPosition(JLabel.CENTER);
        passingLabel.setFont(font);
        summary.add(passingLabel);
        JLabel failingLabel = new JLabel("<html># Not Killed: <b><font color = red>" + numF, OurUtil.loadIcon("five.png"), JLabel.CENTER);
        failingLabel.setHorizontalTextPosition(JLabel.CENTER);
        failingLabel.setFont(font);
        summary.add(failingLabel);

        if (totalTime < 1000) {
            JLabel durationLabel = new JLabel("<html>Time: <b><font color = 5A5A5A>" + totalTime + " ms", OurUtil.loadIcon("four.png"), JLabel.CENTER);
            durationLabel.setHorizontalTextPosition(JLabel.CENTER);
            durationLabel.setFont(font);
            summary.add(durationLabel);
        } else {
            Double seconds = totalTime / 1000.0;
            String timeS = seconds.toString();
            timeS = timeS.substring(0, timeS.indexOf(".") + 2);
            JLabel durationLabel = new JLabel("<html>Time: <b><font color = 5A5A5A>" + timeS + " s", OurUtil.loadIcon("four.png"), JLabel.CENTER);
            durationLabel.setHorizontalTextPosition(JLabel.CENTER);
            durationLabel.setFont(font);
            summary.add(durationLabel);
        }
        return summary;
    }

    public static JPanel makeSummaryBarDisplay(int width, int numP, int numF, long totalTime, Font font) {
        int total = numP + numF;
        SummaryBarPanelMuAlloy summaryBar = new SummaryBarPanelMuAlloy(numP, numF);
        String passingPercent = "0.0";
        if (numP > 0) {
            Double passing = (numP * 100.0) / total;
            String temp = passing.toString();
            passingPercent = temp.substring(0, temp.indexOf(".") + 2);
        }
        JLabel summaryContent = new JLabel("<html><b><font color = black>" + passingPercent + "%", JLabel.LEFT);
        summaryContent.setHorizontalTextPosition(JLabel.CENTER);
        summaryContent.setFont(font);
        summaryBar.add(summaryContent);
        summaryBar.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
        return summaryBar;
    }
}
