/* Alloy Analyzer 4 -- Copyright (c) 2006-2009, Felix Chang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package aunit.gui;

import static edu.mit.csail.sdg.alloy4.OurUtil.menuItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import aunit.coverage.TestCase;
import aunit.gui.alloy4viz.AlloyInstance;
import aunit.gui.alloy4viz.AlloyType;
import aunit.gui.alloy4viz.StaticInstanceReader;
import aunit.gui.alloy4viz.VizCustomizationPanel;
import aunit.gui.alloy4viz.VizGraphPanel;
import aunit.gui.alloy4viz.VizState;
import aunit.gui.alloy4viz.VizTree;
import edu.mit.csail.sdg.alloy4.A4Preferences.IntPref;
import edu.mit.csail.sdg.alloy4.A4Preferences.StringPref;
import edu.mit.csail.sdg.alloy4.Computer;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.OurBorder;
import edu.mit.csail.sdg.alloy4.OurCheckbox;
import edu.mit.csail.sdg.alloy4.OurDialog;
import edu.mit.csail.sdg.alloy4.OurSyntaxWidget;
import edu.mit.csail.sdg.alloy4.Runner;
import edu.mit.csail.sdg.alloy4.ToolbarPanel;
import edu.mit.csail.sdg.alloy4.Util;
import edu.mit.csail.sdg.alloy4.Version;
import edu.mit.csail.sdg.alloy4graph.GraphViewer;

/**
 * GUI main window for the visualizer.
 *
 * <p>
 * <b>Thread Safety:</b> Can be called only by the AWT event thread.
 */

public final class VizMutationGUI implements ComponentListener {

    ArrayList<TestCase>         suite           = new ArrayList<TestCase>();
    /** The background color for the toolbar. */
    private static final Color  background      = new Color(0.9f, 0.9f, 0.9f);

    /** The icon for a "checked" menu item. */
    private static final Icon   iconYes         = OurUtil.loadIcon("images/menu1.gif");

    /** The icon for an "unchecked" menu item. */
    private static final Icon   iconNo          = OurUtil.loadIcon("images/menu0.gif");

    /** Whether the JVM should shutdown after the last file is closed. */
    private final boolean       standalone;

    /** The current display mode. */
    private VisualizerMode      currentMode     = VisualizerMode.get();

    /**
     * The JFrame for the main GUI window; or null if we intend to display the graph
     * inside a user-given JPanel instead.
     */
    private final JFrame        frame;

    /** The toolbar. */
    private final JToolBar      toolbar;
    private JPanel              toolbar_holder;

    /** The projection popup menu. */
    private final JPopupMenu    projectionPopup;

    /** The buttons on the toolbar. */
    private final JButton       projectionButton, vizButton, treeButton, txtButton;

    /**
     * This list must contain all the display mode buttons (that is, vizButton,
     * xmlButton...)
     */
    private final List<JButton> solutionButtons = new ArrayList<JButton>();


    /** Current font size. */
    private int                 fontSize        = 12;

    /**
     * 0: theme and evaluator are both invisible; 1: theme is visible; 2: evaluator
     * is visible.
     */
    private int                 settingsOpen    = 0;

    /** The current instance and visualization settings; null if none is loaded. */
    private VizState            myState         = null;

    /**
     * Returns the current visualization settings (and you can call
     * getOriginalInstance() on it to get the current instance). If you make changes
     * to the state, you should call doApply() on the VizGUI object to refresh the
     * screen.
     */
    public VizState getVizState() {
        return myState;
    }

    /** The customization panel to the left; null if it is not yet loaded. */
    private VizCustomizationPanel myCustomPanel = null;

    /** The graphical panel to the right; null if it is not yet loaded. */
    private VizGraphPanel         myGraphPanel  = null;

    /** The splitpane between the customization panel and the graph panel. */
    private final JSplitPane      splitpane;

    /** The tree or graph or text being displayed on the right hand side. */
    private JComponent            content       = null;

    /**
     * Returns the JSplitPane containing the customization/evaluator panel in the
     * left and the graph on the right.
     */
    public JSplitPane getPanel() {
        return splitpane;
    }

    /**
     * The last known divider position between the customization panel and the graph
     * panel.
     */
    private int            lastDividerPosition = 0;

    private String         mutant_name;
    private String         file;
    private String         test_cmd;
    private String         valuation;

    /** If nonnull, you can pass in an XML file to find the next solution. */
    private final Computer enumerator;


    //==============================================================================================//

    /** The current theme file; "" if there is no theme file loaded. */
    private String thmFileName = "";

    /**
     * Returns the current THM filename; "" if no theme file is currently loaded.
     */
    public String getThemeFilename() {
        return thmFileName;
    }

    //==============================================================================================//

    /** The current XML file; "" if there is no XML file loaded. */
    private String xmlFileName = "";

    /** Returns the current XML filename; "" if no file is currently loaded. */
    public String getXMLfilename() {
        return xmlFileName;
    }

    //==============================================================================================//

    /** The list of XML files loaded in this session so far. */
    private final List<String> xmlLoaded = new ArrayList<String>();

    /** Return the list of XML files loaded in this session so far. */
    public ConstList<String> getInstances() {
        return ConstList.make(xmlLoaded);
    }

    //==============================================================================================//

    /** This maps each XML filename to a descriptive title. */
    private Map<String,String> xml2title = new LinkedHashMap<String,String>();

    /** Returns a short descriptive title associated with an XML file. */
    public String getInstanceTitle(String xmlFileName) {
        String answer = xml2title.get(Util.canon(xmlFileName));
        return (answer == null) ? "(unknown)" : answer;
    }

    //==============================================================================================//

    /** Add a vertical divider to the toolbar. */
    private void addDivider() {
        JPanel divider = OurUtil.makeH(new Dimension(1, 40), Color.LIGHT_GRAY);
        divider.setAlignmentY(0.5f);
        if (!Util.onMac())
            toolbar.add(OurUtil.makeH(5, background));
        else
            toolbar.add(OurUtil.makeH(5));
        toolbar.add(divider);
        if (!Util.onMac())
            toolbar.add(OurUtil.makeH(5, background));
        else
            toolbar.add(OurUtil.makeH(5));
    }

    //======== The Preferences ======================================================================================//
    //======== Note: you must make sure each preference has a unique key ============================================//

    /** This enum defines the set of possible visualizer modes. */
    private enum VisualizerMode {

                                 /** Visualize using graphviz's dot. */
                                 Viz("graphviz"),
                                 //      /** See the DOT content. */             DOT("dot"),
                                 //      /** See the XML content. */             XML("xml"),
                                 /** See the instance as text. */
                                 TEXT("txt"),
                                 /** See the instance as a tree. */
                                 Tree("tree");

        /**
         * This is a unique String for this value; it should be kept consistent in
         * future versions.
         */
        private final String id;

        /** Constructs a new VisualizerMode value with the given id. */
        private VisualizerMode(String id) {
            this.id = id;
        }

        /**
         * Given an id, return the enum value corresponding to it (if there's no match,
         * then return Viz).
         */
        private static VisualizerMode parse(String id) {
            for (VisualizerMode vm : values())
                if (vm.id.equals(id))
                    return vm;
            return Viz;
        }

        /** Saves this value into the Java preference object. */
        public void set() {
            Preferences.userNodeForPackage(Util.class).put("VisualizerMode", id);
        }

        /**
         * Reads the current value of the Java preference object (if it's not set, then
         * return Viz).
         */
        public static VisualizerMode get() {
            return parse(Preferences.userNodeForPackage(Util.class).get("VisualizerMode", ""));
        }
    };

    /** The latest X corrdinate of the Alloy Visualizer window. */
    private static final IntPref    VizX      = new IntPref("VizX", 0, -1, 65535);

    /** The latest Y corrdinate of the Alloy Visualizer window. */
    private static final IntPref    VizY      = new IntPref("VizY", 0, -1, 65535);

    /** The latest width of the Alloy Visualizer window. */
    private static final IntPref    VizWidth  = new IntPref("VizWidth", 0, -1, 65535);

    /** The latest height of the Alloy Visualizer window. */
    private static final IntPref    VizHeight = new IntPref("VizHeight", 0, -1, 65535);

    /** The first file in Alloy Visualizer's "open recent theme" list. */
    private static final StringPref Theme0    = new StringPref("Theme0");

    /** The second file in Alloy Visualizer's "open recent theme" list. */
    private static final StringPref Theme1    = new StringPref("Theme1");

    /** The third file in Alloy Visualizer's "open recent theme" list. */
    private static final StringPref Theme2    = new StringPref("Theme2");

    /** The fourth file in Alloy Visualizer's "open recent theme" list. */
    private static final StringPref Theme3    = new StringPref("Theme3");

    //==============================================================================================//

    /**
     * If true, that means the event handlers should return a Runner encapsulating
     * them, rather than perform the actual work.
     */
    private boolean                 wrap      = false;

    /**
     * Wraps the calling method into a Runnable whose run() will call the calling
     * method with (false) as the only argument.
     */
    private Runner wrapMe() {
        final String name;
        try {
            throw new Exception();
        } catch (Exception ex) {
            name = ex.getStackTrace()[1].getMethodName();
        }
        Method[] methods = getClass().getDeclaredMethods();
        Method m = null;
        for (int i = 0; i < methods.length; i++)
            if (methods[i].getName().equals(name)) {
                m = methods[i];
                break;
            }
        final Method method = m;
        return new Runner() {

            private static final long serialVersionUID = 0;

            @Override
            public void run() {
                try {
                    method.setAccessible(true);
                    method.invoke(VizMutationGUI.this, new Object[] {});
                } catch (Throwable ex) {
                    ex = new IllegalArgumentException("Failed call to " + name + "()", ex);
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
                }
            }

            @Override
            public void run(Object arg) {
                run();
            }
        };
    }

    /**
     * Wraps the calling method into a Runnable whose run() will call the calling
     * method with (false,argument) as the two arguments.
     */
    private Runner wrapMe(final Object argument) {
        final String name;
        try {
            throw new Exception();
        } catch (Exception ex) {
            name = ex.getStackTrace()[1].getMethodName();
        }
        Method[] methods = getClass().getDeclaredMethods();
        Method m = null;
        for (int i = 0; i < methods.length; i++)
            if (methods[i].getName().equals(name)) {
                m = methods[i];
                break;
            }
        final Method method = m;
        return new Runner() {

            private static final long serialVersionUID = 0;

            @Override
            public void run(Object arg) {
                try {
                    method.setAccessible(true);
                    method.invoke(VizMutationGUI.this, new Object[] {
                                                                     arg
                    });
                } catch (Throwable ex) {
                    ex = new IllegalArgumentException("Failed call to " + name + "(" + arg + ")", ex);
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
                }
            }

            @Override
            public void run() {
                run(argument);
            }
        };
    }

    /**
     * Creates a new visualization GUI window; this method can only be called by the
     * AWT event thread.
     *
     * @param standalone - whether the JVM should shutdown after the last file is
     *            closed
     * @param xmlFileName - the filename of the incoming XML file; "" if there's no
     *            file to open
     * @param windowmenu - if standalone==false and windowmenu!=null, then this will
     *            be added as a menu on the menubar
     *
     *            <p>
     *            Note: if standalone==false and xmlFileName.length()==0, then we
     *            will initially hide the window.
     */
    public VizMutationGUI(boolean standalone, String xmlFileName, JMenu windowmenu) {
        this(standalone, xmlFileName, windowmenu, null, null);
    }

    /**
     * Creates a new visualization GUI window; this method can only be called by the
     * AWT event thread.
     *
     * @param standalone - whether the JVM should shutdown after the last file is
     *            closed
     * @param xmlFileName - the filename of the incoming XML file; "" if there's no
     *            file to open
     * @param windowmenu - if standalone==false and windowmenu!=null, then this will
     *            be added as a menu on the menubar
     * @param enumerator - if it's not null, it provides solution enumeration
     *            ability
     * @param evaluator - if it's not null, it provides solution evaluation ability
     *
     *            <p>
     *            Note: if standalone==false and xmlFileName.length()==0, then we
     *            will initially hide the window.
     */
    public VizMutationGUI(boolean standalone, String xmlFileName, JMenu windowmenu, Computer enumerator, Computer evaluator) {
        this(standalone, xmlFileName, windowmenu, enumerator, evaluator, true);
    }

    /**
     * Creates a new visualization GUI window; this method can only be called by the
     * AWT event thread.
     *
     * @param standalone - whether the JVM should shutdown after the last file is
     *            closed
     * @param xmlFileName - the filename of the incoming XML file; "" if there's no
     *            file to open
     * @param windowmenu - if standalone==false and windowmenu!=null, then this will
     *            be added as a menu on the menubar
     * @param enumerator - if it's not null, it provides solution enumeration
     *            ability
     * @param evaluator - if it's not null, it provides solution evaluation ability
     * @param makeWindow - if false, then we will only construct the JSplitPane,
     *            without making the window
     *
     *            <p>
     *            Note: if standalone==false and xmlFileName.length()==0 and
     *            makeWindow==true, then we will initially hide the window.
     */
    public VizMutationGUI(boolean standalone, String xmlFileName, JMenu windowmenu, Computer enumerator, Computer evaluator, boolean makeWindow) {

        this.standalone = standalone;
        this.frame = makeWindow ? new JFrame("Alloy Visualizer") : null;
        this.enumerator = enumerator;
        // Figure out the desired x, y, width, and height
        int screenWidth = OurUtil.getScreenWidth(), screenHeight = OurUtil.getScreenHeight();
        int width = VizWidth.get();
        if (width < 0)
            width = screenWidth - 150;
        else if (width < 100)
            width = 100;
        if (width > screenWidth)
            width = screenWidth;
        int height = VizHeight.get();
        if (height < 0)
            height = screenHeight - 150;
        else if (height < 100)
            height = 100;
        if (height > screenHeight)
            height = screenHeight;
        int x = VizX.get();
        if (x < 0 || x > screenWidth - 10)
            x = 0;
        int y = VizY.get();
        if (y < 0 || y > screenHeight - 10)
            y = 0;

        // Create the menubar
        windowmenu.setEnabled(false);

        // Create the toolbar
        projectionPopup = new JPopupMenu();
        projectionButton = new JButton("Projection: none");
        projectionButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                repopulateProjectionPopup();
                if (projectionPopup.getComponentCount() > 0)
                    projectionPopup.show(projectionButton, 10, 10);
            }
        });
        projectionButton.setBorder(new OurBorder(true, true, true, true));
        Border margin = new EmptyBorder(2, 5, 2, 5);
        projectionButton.setBorder(new CompoundBorder(projectionButton.getBorder(), margin));
        repopulateProjectionPopup();
        toolbar = new ToolbarPanel();
        toolbar.setVisible(false);
        toolbar.setFloatable(false);
        toolbar.setBorder(null);

        try {
            wrap = true;
            vizButton = makeSolutionButton("Viz", "Show Visualization", "images/show.png", doShowViz());
            txtButton = makeSolutionButton("Txt", "Show the textual output for the Graph", "images/text.png", doShowTxt());
            treeButton = makeSolutionButton("Tree", "Show Tree", "images/tree.png", doShowTree());
        } finally {
            wrap = false;
        }
        settingsOpen = 0;
        toolbar_holder = new JPanel();
        toolbar_holder.setLayout(new GridLayout(0, 1));
        toolbar_holder.setBorder(new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        toolbar_holder.add(toolbar);

        // Create the horizontal split pane
        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitpane.setOneTouchExpandable(false);
        splitpane.setContinuousLayout(true);
        splitpane.setResizeWeight(0.5D);
        splitpane.setDividerSize(6);
        splitpane.setUI(new BasicSplitPaneUI() {

            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {

                    @Override
                    public void setBorder(Border b) {

                    }

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(220, 220, 220));
                        g.fillRect(1, 0, getSize().width - 2, getSize().height);
                        g.setColor(new Color(232, 232, 232));
                        g.fillRect(0, 0, 1, getSize().height);
                        g.setColor(new Color(173, 173, 173));
                        g.fillRect(getSize().width - 1, 0, 1, getSize().height);
                        super.paint(g);
                    }
                };
            }
        });
        // Display the window, then proceed to load the input file
        if (frame != null) {
            frame.pack();
            if (!Util.onMac() && !Util.onWindows()) {
                // many Window managers do not respect ICCCM2; this should help avoid the Title Bar being shifted "off screen"
                if (x < 30) {
                    if (x < 0)
                        x = 0;
                    width = width - (30 - x);
                    x = 30;
                }
                if (y < 30) {
                    if (y < 0)
                        y = 0;
                    height = height - (30 - y);
                    y = 30;
                }
                if (width < 100)
                    width = 100;
                if (height < 100)
                    height = 100;
            }
            frame.setSize(width, height);
            frame.setLocation(x, y);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            try {
                wrap = true;
                frame.addWindowListener(doClose());
            } finally {
                wrap = false;
            }
            frame.addComponentListener(this);
        }
    }

    /** Invoked when the Visualizationwindow is resized. */
    public void componentResized(ComponentEvent e) {
        componentMoved(e);
    }

    /** Invoked when the Visualizationwindow is moved. */
    public void componentMoved(ComponentEvent e) {
        if (frame != null) {
            VizWidth.set(frame.getWidth());
            VizHeight.set(frame.getHeight());
            VizX.set(frame.getX());
            VizY.set(frame.getY());
        }
    }

    /** Invoked when the Visualizationwindow is shown. */
    public void componentShown(ComponentEvent e) {
    }

    /** Invoked when the Visualizationwindow is hidden. */
    public void componentHidden(ComponentEvent e) {
    }

    /** Helper method that repopulates the Porjection popup menu. */
    private void repopulateProjectionPopup() {
        int num = 0;
        String label = "Projection: none";
        if (myState == null) {
            projectionButton.setEnabled(false);
            return;
        }
        projectionButton.setEnabled(true);
        projectionPopup.removeAll();
        final Set<AlloyType> projected = myState.getProjectedTypes();
        for (final AlloyType t : myState.getOriginalModel().getTypes())
            if (myState.canProject(t)) {
                final boolean on = projected.contains(t);
                final JMenuItem m = new JMenuItem(t.getName(), on ? OurCheckbox.ON : OurCheckbox.OFF);
                m.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (on)
                            myState.deproject(t);
                        else
                            myState.project(t);
                        updateDisplay();
                    }
                });
                projectionPopup.add(m);
                if (on) {
                    num++;
                    if (num == 1)
                        label = "Projected over " + t.getName();
                }
            }
        projectionButton.setText(num > 1 ? ("Projected over " + num + " sigs") : label);
    }

    /**
     * Helper method that refreshes the right-side visualization panel with the
     * latest settings.
     */
    private void updateDisplay() {
        if (myState == null)
            return;
        // First, update the toolbar
        currentMode.set();
        for (JButton button : solutionButtons)
            button.setEnabled(settingsOpen != 1);
        switch (currentMode) {
            case Tree :
                treeButton.setEnabled(false);
                break;
            case TEXT :
                txtButton.setEnabled(false);
                break;
            default :
                vizButton.setEnabled(false);
        }
        final boolean isMeta = myState.getOriginalInstance().isMetamodel;
        vizButton.setVisible(frame != null);
        treeButton.setVisible(frame != null);
        txtButton.setVisible(frame != null);
        projectionButton.setVisible((settingsOpen == 0 || settingsOpen == 1) && currentMode == VisualizerMode.Viz);

        toolbar.setVisible(true);
        toolbar_holder.setVisible(true);
        toolbar_holder.setBorder(new OurBorder(false, true, false, false));
        toolbar.setBackground(background);
        // Now, generate the graph or tree or textarea that we want to display on the right
        if (frame != null)
            frame.setTitle("Displaying Mutant Killing Test For: " + mutant_name);
        switch (currentMode) {
            case Tree : {
                final VizTree t = new VizTree(myState.getOriginalInstance().originalA4, makeVizTitle(), fontSize);
                final JScrollPane scroll = OurUtil.scrollpane(t, Color.BLACK, Color.WHITE, new OurBorder(false, false, false, false));
                scroll.addFocusListener(new FocusListener() {

                    public final void focusGained(FocusEvent e) {
                        t.requestFocusInWindow();
                    }

                    public final void focusLost(FocusEvent e) {
                    }
                });
                content = scroll;
                break;
            }
            case TEXT : {
                String textualOutput = myState.getOriginalInstance().originalA4.toString();
                content = getTextComponent(textualOutput);
                break;
            }
            default : {
                if (myGraphPanel == null) {
                    myGraphPanel = new VizGraphPanel(myState, false);
                } else {
                    myGraphPanel.seeDot(false);
                    myGraphPanel.remakeAll();

                }
            }
                content = myGraphPanel;
        }
        // Now that we've re-constructed "content", let's set its font size
        if (currentMode != VisualizerMode.Tree) {
            content.setFont(OurUtil.getVizFont().deriveFont((float) fontSize));
            content.invalidate();
            content.repaint();
            content.validate();
        }
        content.setBorder(null);
        // Now, display them!
        Font bold = new Font(OurUtil.getVizFont().getName(), Font.BOLD, OurUtil.getVizFont().getSize());
        //JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        //sep.setForeground(new Color(173, 173, 173));
        //sep.setBackground(new Color(220, 220, 220));

        final Box instanceTopBox = Box.createHorizontalBox();
        instanceTopBox.add(toolbar_holder);
        instanceTopBox.setBorder(new OurBorder(false, false, true, false));
        final JPanel instanceArea = new JPanel(new BorderLayout());

        JPanel valArea = new JPanel();
        valArea.setBackground(Color.white);
        valArea.setLayout(new BorderLayout());
        valArea.add(instanceTopBox, BorderLayout.NORTH);

        instanceArea.add(valArea, BorderLayout.NORTH);
        instanceArea.setVisible(true);
        if (!Util.onMac()) {
            instanceTopBox.setBackground(background);
            instanceArea.setBackground(Color.white);
        }
        instanceArea.add(content, BorderLayout.CENTER);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.white);

        JPanel test_details = new JPanel();
        test_details.setLayout(new GridLayout(2, 1));
        test_details.setBackground(Color.white);
        TitledBorder titleb = BorderFactory.createTitledBorder(new MatteBorder(1, 1, 1, 1, new Color(205, 196, 196)), "Test Details");
        titleb.setTitleFont(new Font(OurUtil.getVizFont().getName(), Font.ITALIC, OurUtil.getVizFont().getSize()));
        test_details.setBorder(titleb);
        test_details.setBorder(new CompoundBorder(test_details.getBorder(), new EmptyBorder(5, 5, 5, 5)));
        test_details.setBorder(new CompoundBorder(new EmptyBorder(5, 10, 5, 10), test_details.getBorder()));
        JLabel val = new JLabel("<html><b>Valuation:</b> Valuation displayed below.");
        val.setFont(OurUtil.getVizFont());
        JLabel cmd = new JLabel("<html><b>Command:</b> " + test_cmd);
        cmd.setFont(OurUtil.getVizFont());
        test_details.add(val);
        test_details.add(cmd);
        test_details.setAlignmentX(0);

        top.add(test_details);

        JPanel store = new JPanel();
        store.setLayout(new BorderLayout());
        store.add(top, BorderLayout.NORTH);
        store.add(splitpane, BorderLayout.CENTER);

        JPanel mutant_display = new JPanel();
        mutant_display.setLayout(new BorderLayout());

        JLabel title = new JLabel("Mutant Location Tree");
        title.setFont(new Font(OurUtil.getVizFont().getName(), Font.BOLD, OurUtil.getVizFont().getSize() + 1));
        title.setBackground(Color.white);
        title.setBorder(new EmptyBorder(5, 10, 0, 0));//top,left,bottom,right
        mutant_display.add(title, BorderLayout.NORTH);

        OurSyntaxWidget text = new OurSyntaxWidget(true, "", OurUtil.getVizFont().getName(), OurUtil.getVizFont().getSize(), 4, null, null);
        String x = file;
        // try {
        //    x = Util.readAll(file);
        //} catch (FileNotFoundException e) {
        //   e.printStackTrace();
        //} catch (IOException e) {
        //   e.printStackTrace();
        // }
        text.setText(x);
        text.setUnEditable();
        text.addTo(mutant_display, BorderLayout.CENTER);

        OurSyntaxWidget val_text = new OurSyntaxWidget(true, "", OurUtil.getVizFont().getName(), OurUtil.getVizFont().getSize(), 4, null, null);
        val_text.setText(valuation);
        val_text.setUnEditable();
        JPanel val_holder = new JPanel();
        val_holder.setLayout(new BorderLayout());
        val_text.addTo(val_holder, BorderLayout.CENTER);

        JSplitPane test_displays = new JSplitPane();
        test_displays = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        test_displays.setOneTouchExpandable(false);
        test_displays.setContinuousLayout(true);
        test_displays.setResizeWeight(0.5D);
        test_displays.setDividerSize(6);
        test_displays.setUI(new BasicSplitPaneUI() {

            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {

                    @Override
                    public void setBorder(Border b) {

                    }

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(220, 220, 220));
                        g.fillRect(1, 0, getSize().width - 2, getSize().height);
                        g.setColor(new Color(232, 232, 232));
                        g.fillRect(0, 0, 1, getSize().height);
                        g.setColor(new Color(173, 173, 173));
                        g.fillRect(getSize().width - 1, 0, 1, getSize().height);
                        super.paint(g);
                    }
                };
            }
        });

        lastDividerPosition = frame.getHeight() / 2;
        test_displays.setDividerLocation(lastDividerPosition);
        test_displays.setTopComponent(instanceArea);
        test_displays.setBottomComponent(val_holder);

        lastDividerPosition = frame.getWidth() / 2;
        splitpane.setDividerLocation(lastDividerPosition);
        splitpane.setLeftComponent(test_displays);
        splitpane.setRightComponent(mutant_display);
        if (frame != null)
            frame.setContentPane(store);
        repopulateProjectionPopup();
        if (frame != null)
            frame.validate();
        else
            splitpane.validate();
    }

    /**
     * Helper method that creates a button and add it to both the "SolutionButtons"
     * list, as well as the toolbar.
     */
    private JButton makeSolutionButton(String label, String toolTip, String image, ActionListener mode) {
        JButton button = OurUtil.button(label, toolTip, image, mode);
        solutionButtons.add(button);
        toolbar.add(button);
        return button;
    }

    /**
     * Helper method that returns a concise description of the instance currently
     * being displayed.
     */
    private String makeVizTitle() {
        String filename = (myState != null ? myState.getOriginalInstance().filename : "");
        String commandname = (myState != null ? myState.getOriginalInstance().commandname : "");
        int i = filename.lastIndexOf('/');
        if (i >= 0)
            filename = filename.substring(i + 1);
        i = filename.lastIndexOf('\\');
        if (i >= 0)
            filename = filename.substring(i + 1);
        int n = filename.length();
        if (n > 4 && filename.substring(n - 4).equalsIgnoreCase(".als"))
            filename = filename.substring(0, n - 4);
        if (filename.length() > 0)
            return "(" + filename + ") " + commandname;
        else
            return commandname;
    }

    public void doNothing() {

    }

    /**
     * Helper method that inserts "filename" into the "recently opened THEME file
     * list".
     */
    private void addThemeHistory(String filename) {
        String name0 = Theme0.get(), name1 = Theme1.get(), name2 = Theme2.get();
        if (name0.equals(filename))
            return;
        else {
            Theme0.set(filename);
            Theme1.set(name0);
        }
        if (name1.equals(filename))
            return;
        else
            Theme2.set(name1);
        if (name2.equals(filename))
            return;
        else
            Theme3.set(name2);
    }

    /** Helper method returns a JTextArea containing the given text. */
    private JComponent getTextComponent(String text) {
        final JTextArea ta = OurUtil.textarea(text, 10, 10, false, true);
        final JScrollPane ans = new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) {

            private static final long serialVersionUID = 0;

            @Override
            public void setFont(Font font) {
                ta.setFont(font);
            }
        };
        ans.setBorder(new OurBorder(false, false, false, false));
        return ans;
    }

    //   /** Helper method that reads a file and then return a JTextArea containing it. */
    //   private JComponent getTextComponentFromFile(String filename) {
    //      String text = "";
    //      try { text="<!-- "+filename+" -->\n"+Util.readAll(filename); } catch(IOException ex) { text="# Error reading from "+filename; }
    //      return getTextComponent(text);
    //   }

    /**
     * Returns the GraphViewer that contains the graph; can be null if the graph
     * hasn't been loaded yet.
     */
    public GraphViewer getViewer() {
        if (null == myGraphPanel)
            return null;
        return myGraphPanel.alloyGetViewer();
    }

    public void loadNextXML(final String fileName, boolean forcefully) {
        final String xmlFileName = Util.canon(fileName);
        File f = new File(xmlFileName);
        if (forcefully || !xmlFileName.equals(this.xmlFileName)) {
            AlloyInstance myInstance;
            try {
                if (!f.exists())
                    throw new IOException("File " + xmlFileName + " does not exist.");
                myInstance = StaticInstanceReader.parseInstance(f);
            } catch (Throwable e) {
                xmlLoaded.remove(fileName);
                xmlLoaded.remove(xmlFileName);
                OurDialog.alert("Cannot read or parse Alloy instance: " + xmlFileName + "\n\nError: " + e.getMessage());
                //  if (xmlLoaded.size()>0) { loadXML(xmlLoaded.get(xmlLoaded.size()-1), false); return; }
                doCloseAll();
                return;
            }
            if (myState == null)
                myState = new VizState(myInstance);
            else
                myState.loadInstance(myInstance);
            repopulateProjectionPopup();
            xml2title.put(xmlFileName, makeVizTitle());
            this.xmlFileName = xmlFileName;
        }
        if (!xmlLoaded.contains(xmlFileName))
            xmlLoaded.add(xmlFileName);
        toolbar.setEnabled(true);
        settingsOpen = 0;

        if (frame != null) {
            frame.setVisible(true);
            frame.setTitle("Alloy Visualizer " + Version.version() + " loading... Please wait...");
            OurUtil.show(frame);
        }
        updateDisplay();
    }

    public void loadXML(final String fileName, boolean forcefully, String file) {
        this.file = file;
        final String xmlFileName = Util.canon(fileName);
        File f = new File(xmlFileName);
        if (forcefully || !xmlFileName.equals(this.xmlFileName)) {
            AlloyInstance myInstance;
            try {
                if (!f.exists())
                    throw new IOException("File " + xmlFileName + " does not exist.");
                myInstance = StaticInstanceReader.parseInstance(f);
            } catch (Throwable e) {
                xmlLoaded.remove(fileName);
                xmlLoaded.remove(xmlFileName);
                OurDialog.alert("Cannot read or parse aunit test case: " + xmlFileName + "\n\nError: " + e.getMessage());
                //  if (xmlLoaded.size()>0) { loadXML(xmlLoaded.get(xmlLoaded.size()-1), false); return; }
                doCloseAll();
                return;
            }
            if (myState == null)
                myState = new VizState(myInstance);
            else
                myState.loadInstance(myInstance);
            repopulateProjectionPopup();
            xml2title.put(xmlFileName, makeVizTitle());
            this.xmlFileName = xmlFileName;
        }
        if (!xmlLoaded.contains(xmlFileName))
            xmlLoaded.add(xmlFileName);
        toolbar.setEnabled(true);
        settingsOpen = 0;

        if (frame != null) {
            frame.setVisible(true);
            frame.setTitle("Mutation Visualizer " + Version.version() + " loading... Please wait...");
            OurUtil.show(frame);
        }
        updateDisplay();
    }

    /** This method loads a specific theme file. */
    public boolean loadThemeFile(String filename) {
        if (myState == null)
            return false; // Can only load if there is a VizState loaded
        filename = Util.canon(filename);
        try {
            myState.loadPaletteXML(filename);
        } catch (IOException ex) {
            OurDialog.alert("Error: " + ex.getMessage());
            return false;
        }
        repopulateProjectionPopup();
        if (myCustomPanel != null)
            myCustomPanel.remakeAll();
        if (myGraphPanel != null)
            myGraphPanel.remakeAll();
        addThemeHistory(filename);
        thmFileName = filename;
        updateDisplay();
        return true;
    }

    /**
     * This method saves a specific current theme (if filename==null, it asks the
     * user); returns true if it succeeded.
     */
    public boolean saveThemeFile(String filename) {
        if (myState == null)
            return false; // Can only save if there is a VizState loaded
        if (filename == null) {
            File file = OurDialog.askFile(false, null, ".thm", ".thm theme files");
            if (file == null)
                return false;
            if (file.exists())
                if (!OurDialog.askOverwrite(Util.canon(file.getPath())))
                    return false;
            Util.setCurrentDirectory(file.getParentFile());
            filename = file.getPath();
        }
        filename = Util.canon(filename);
        try {
            myState.savePaletteXML(filename);
            filename = Util.canon(filename); // Since the canon name may have changed
            addThemeHistory(filename);
        } catch (Throwable er) {
            OurDialog.alert("Error saving the theme.\n\nError: " + er.getMessage());
            return false;
        }
        thmFileName = filename;
        return true;
    }

    //========================================= EVENTS ============================================================================================

    /** This method changes the font size for everything (except the graph) */
    public void doSetFontSize(int fontSize) {
        this.fontSize = fontSize;
        if (!(content instanceof VizGraphPanel))
            updateDisplay();
        else
            content.setFont(OurUtil.getVizFont().deriveFont((float) fontSize));
    }

    /**
     * This method closes the current instance; if there are previously loaded
     * files, we will load one of them; otherwise, this window will set itself as
     * invisible (if not in standalone mode), or it will terminate the entire
     * application (if in standalone mode).
     */
    private Runner doClose() {
        if (wrap)
            return wrapMe();
        xmlLoaded.remove(xmlFileName);
        if (standalone)
            System.exit(0);
        else if (frame != null)
            frame.setVisible(false);
        return null;
    }

    /**
     * This method closes every XML file. If in standalone mode, the JVM will then
     * shutdown, otherwise it will just set the window invisible.
     */
    private Runner doCloseAll() {
        if (wrap)
            return wrapMe();
        xmlLoaded.clear();
        xmlFileName = "";
        if (standalone)
            System.exit(0);
        else if (frame != null)
            frame.setVisible(false);
        return null;
    }

    /** This method inserts "Minimize" and "Maximize" entries into a JMenu. */
    public void addMinMaxActions(JMenu menu) {
        try {
            wrap = true;
            menuItem(menu, "Minimize", 'M', doMinimize(), iconNo);
            menuItem(menu, "Zoom", doZoom(), iconNo);
        } finally {
            wrap = false;
        }
    }

    /** This method minimizes the window. */
    private Runner doMinimize() {
        if (!wrap && frame != null)
            OurUtil.minimize(frame);
        return wrapMe();
    }

    /** This method alternatingly maximizes or restores the window. */
    private Runner doZoom() {
        if (!wrap && frame != null)
            OurUtil.zoom(frame);
        return wrapMe();
    }



    /**
     * This method changes the display mode to show the instance as a graph (the
     * return value is always null).
     */
    public Runner doShowViz() {
        if (!wrap) {
            currentMode = VisualizerMode.Viz;
            updateDisplay();
            return null;
        }
        return wrapMe();
    }

    /**
     * This method changes the display mode to show the instance as a tree (the
     * return value is always null).
     */
    public Runner doShowTree() {
        if (!wrap) {
            currentMode = VisualizerMode.Tree;
            updateDisplay();
            return null;
        }
        return wrapMe();
    }

    /**
     * This method changes the display mode to show the equivalent dot text (the
     * return value is always null).
     */
    public Runner doShowTxt() {
        if (!wrap) {
            currentMode = VisualizerMode.TEXT;
            updateDisplay();
            return null;
        }
        return wrapMe();
    }

    public void setMutantName(String mutant_name) {
        this.mutant_name = mutant_name;
    }

    public void setCommand(String test_cmd) {
        this.test_cmd = test_cmd;
        if (test_cmd.length() == 0)
            this.test_cmd = "{}";
    }

    public void setValuation(String valuation) {
        this.valuation = valuation;
    }

    public ArrayList<TestCase> getTestSuite() {
        return suite;
    }
}
