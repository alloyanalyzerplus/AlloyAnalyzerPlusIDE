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

package edu.mit.csail.sdg.alloy4whole;

import static aunit.gui.OurUtil.menu;
import static aunit.gui.OurUtil.menuItem;
import static edu.mit.csail.sdg.alloy4.A4Preferences.AnalyzerHeight;
import static edu.mit.csail.sdg.alloy4.A4Preferences.AnalyzerWidth;
import static edu.mit.csail.sdg.alloy4.A4Preferences.AnalyzerX;
import static edu.mit.csail.sdg.alloy4.A4Preferences.AnalyzerY;
import static edu.mit.csail.sdg.alloy4.A4Preferences.AntiAlias;
import static edu.mit.csail.sdg.alloy4.A4Preferences.AutoVisualize;
import static edu.mit.csail.sdg.alloy4.A4Preferences.CoreGranularity;
import static edu.mit.csail.sdg.alloy4.A4Preferences.CoreMinimization;
import static edu.mit.csail.sdg.alloy4.A4Preferences.FLWeight;
import static edu.mit.csail.sdg.alloy4.A4Preferences.FontName;
import static edu.mit.csail.sdg.alloy4.A4Preferences.FontSize;
import static edu.mit.csail.sdg.alloy4.A4Preferences.ImplicitThis;
import static edu.mit.csail.sdg.alloy4.A4Preferences.InferPartialInstance;
import static edu.mit.csail.sdg.alloy4.A4Preferences.LAF;
import static edu.mit.csail.sdg.alloy4.A4Preferences.MaxTriesPerDepth;
import static edu.mit.csail.sdg.alloy4.A4Preferences.MaxTriesPerHole;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Model0;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Model1;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Model2;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Model3;
import static edu.mit.csail.sdg.alloy4.A4Preferences.NoOverflow;
import static edu.mit.csail.sdg.alloy4.A4Preferences.NumberOfPartitions;
import static edu.mit.csail.sdg.alloy4.A4Preferences.RecordKodkod;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SavePatch;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SaveTestSuite;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SearchStratPref;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SkolemDepth;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Solver;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SubMemory;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SubStack;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SusFormulaPref;
import static edu.mit.csail.sdg.alloy4.A4Preferences.SyntaxDisabled;
import static edu.mit.csail.sdg.alloy4.A4Preferences.TabSize;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Unrolls;
import static edu.mit.csail.sdg.alloy4.A4Preferences.VerbosityPref;
import static edu.mit.csail.sdg.alloy4.A4Preferences.WarningNonfatal;
import static edu.mit.csail.sdg.alloy4.A4Preferences.Welcome;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_SHIFT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.alloytools.alloy.core.AlloyCore;

import aunit.gui.ARepairTreeNode;
import aunit.gui.ARepairTreeRender;
import aunit.gui.AUnitGuiUtil;
import aunit.gui.AUnitTreeNode;
import aunit.gui.AlloyFLHighlightNode;
import aunit.gui.AlloyFLTreeRender;
import aunit.gui.CompoundIcon;
import aunit.gui.CoverageInformation;
import aunit.gui.CoverageTreeRenderer;
import aunit.gui.MuAlloyGuiUtil;
import aunit.gui.MuAlloyTreeNode;
import aunit.gui.MuAlloyTreeRender;
import aunit.gui.PassFailRenderer;
import aunit.gui.PassFailTreeNode;
import aunit.gui.VizMutationGUI;
import aunit.gui.VizValuationGUI;
import edu.mit.csail.sdg.alloy4.A4Preferences;
import edu.mit.csail.sdg.alloy4.A4Preferences.BooleanPref;
import edu.mit.csail.sdg.alloy4.A4Preferences.ChoicePref;
import edu.mit.csail.sdg.alloy4.A4Preferences.IntPref;
import edu.mit.csail.sdg.alloy4.A4Preferences.StringPref;
import edu.mit.csail.sdg.alloy4.A4Preferences.Verbosity;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Computer;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorFatal;
import edu.mit.csail.sdg.alloy4.ErrorType;
import edu.mit.csail.sdg.alloy4.Listener;
import edu.mit.csail.sdg.alloy4.MailBug;
import edu.mit.csail.sdg.alloy4.OurAntiAlias;
import edu.mit.csail.sdg.alloy4.OurBorder;
import edu.mit.csail.sdg.alloy4.OurCombobox;
import edu.mit.csail.sdg.alloy4.OurDialog;
import edu.mit.csail.sdg.alloy4.OurSyntaxWidget;
import edu.mit.csail.sdg.alloy4.OurTabbedSyntaxWidget;
import edu.mit.csail.sdg.alloy4.OurTree;
import edu.mit.csail.sdg.alloy4.OurUtil;
import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.alloy4.Runner;
import edu.mit.csail.sdg.alloy4.TabPanel;
import edu.mit.csail.sdg.alloy4.ToolbarPanel;
import edu.mit.csail.sdg.alloy4.Util;
import edu.mit.csail.sdg.alloy4.Version;
import edu.mit.csail.sdg.alloy4.WorkerEngine;
import edu.mit.csail.sdg.alloy4.XMLNode;
import edu.mit.csail.sdg.alloy4viz.VizGUI;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.ARepairExecutionTask;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.AlloyFLExecutionTask;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.AunitExecutionTask;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.MuAlloyExecutionTask;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.SimpleCallback1;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.SimpleTask1;
import edu.mit.csail.sdg.alloy4whole.SimpleReporter.SimpleTask2;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.ast.Sig.Field;
import edu.mit.csail.sdg.ast.Test;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.sim.SimInstance;
import edu.mit.csail.sdg.sim.SimTuple;
import edu.mit.csail.sdg.sim.SimTupleset;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Options.SatSolver;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.A4SolutionReader;
import edu.mit.csail.sdg.translator.A4Tuple;
import edu.mit.csail.sdg.translator.A4TupleSet;
import kodkod.engine.fol2sat.HigherOrderDeclException;
import parser.util.FileUtil;


/**
 * Simple graphical interface for accessing various features of the analyzer.
 * <p>
 * Except noted below, methods in this class can only be called by the AWT event
 * thread.
 * <p>
 * The methods that might get called from other threads are: <br>
 * (1) the run() method in SatRunner is launched from a fresh thread <br>
 * (2) the run() method in the instance watcher (in constructor) is launched
 * from a fresh thread
 */
public final class SimpleGUI implements ComponentListener, Listener {

    MacUtil macUtil;

    /**
     * The latest welcome screen; each time we update the welcome screen, we
     * increment this number.
     */
    // private static final int welcomeLevel = 2;

    // Verify that the graphics environment is set up
    static {
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        } catch (Throwable ex) {
            System.err.println("Unable to start the graphical environment.");
            System.err.println("If you're on Mac OS X:");
            System.err.println("   Please make sure you are running as the current local user.");
            System.err.println("If you're on Linux or FreeBSD:");
            System.err.println("   Please make sure your X Windows is configured.");
            System.err.println("   You can verify this by typing \"xhost\"; it should not give an error message.");
            System.err.flush();
            System.exit(1);
        }
    }

    /** The JFrame for the main window. */
    private JFrame                frame;

    /** The JFrame for the visualizer window. */
    private VizGUI                viz;

    /**
     * The "File", "Edit", "Run", "Option", "Window", and "Help" menus.
     */
    private JMenu                 filemenu, editmenu, runmenu, optmenu, windowmenu, windowmenu2, helpmenu;

    /** The toolbar. */
    private JToolBar              toolbar;

    /** The various toolbar buttons. */
    private JButton               runbutton, stopbutton, showbutton;

    /** The Splitpane. */
    private JSplitPane            splitpane;

    /**
     * The JLabel that displays the current line/column position, etc.
     */
    private JLabel                status;

    /**
     * Whether the editor has the focus, or the log window has the focus.
     */
    private boolean               lastFocusIsOnEditor    = true;

    /** The text editor. */
    private OurTabbedSyntaxWidget text;

    /** The "message panel" on the right. */
    private SwingLogPanel         log;

    /** The scrollpane containing the "message panel". */
    private JScrollPane           logpane;

    /** The last "find" that the user issued. */
    private String                lastFind               = "";

    /** The last find is case-sensitive or not. */
    private boolean               lastFindCaseSensitive  = true;

    /** The last find is forward or not. */
    private boolean               lastFindForward        = true;

    /** The icon for a "checked" menu item. */
    private static final Icon     iconYes                = OurUtil.loadIcon("images/menu1.gif");

    /** The icon for an "unchecked" menu item. */
    private static final Icon     iconNo                 = OurUtil.loadIcon("images/menu0.gif");

    /**
     * The system-specific file separator (forward-slash on UNIX, back-slash on
     * Windows, etc.)
     */
    private static final String   fs                     = System.getProperty("file.separator");

    /**
     * The darker background color (for the MessageLog window and the Toolbar and
     * the Status Bar, etc.)
     */
    private static final Color    background             = new Color(0.9f, 0.9f, 0.9f);

    /**
     * If subrunning==true: 0 means SAT solving; 1 means metamodel; 2 means
     * enumeration.
     */
    private int                   subrunningTask         = 0;

    /**
     * The amount of memory (in MB) currently allocated for this.subprocess
     */
    private int                   subMemoryNow           = 0;

    /**
     * The amount of stack (in KB) currently allocated for this.subprocess
     */
    private int                   subStackNow            = 0;

    /**
     * The list of commands (this field will be cleared to null when the text buffer
     * is edited).
     */
    private List<Command>         commands               = null;

    /** The latest executed command. */
    private int                   latestCommand          = 0;

    /**
     * The most recent Alloy version (as queried from alloy.mit.edu); -1 if
     * alloy.mit.edu has not replied yet.
     */
    private int                   latestAlloyVersion     = (-1);

    /**
     * The most recent Alloy version name (as queried from alloy.mit.edu); "unknown"
     * if alloy.mit.edu has not replied yet.
     */
    private String                latestAlloyVersionName = "unknown";

    /**
     * If it's not "", then it is the XML filename for the latest satisfying
     * instance or the latest metamodel.
     */
    private String                latestInstance         = "";

    /**
     * If it's not "", then it is the latest instance or metamodel during the most
     * recent click of "Execute".
     */
    private String                latestAutoInstance     = "";

    /**
     * If true, that means the event handlers should return a Runner encapsulating
     * them, rather than perform the actual work.
     */
    private boolean               wrap                   = false;

    /** The preferences dialog. */
    private PreferencesDialog     prefDialog;


    /** Sullivan **/ /** AUnit Variables added **/
    /** The AUnit Menu **/
    private JMenu                 aunitMenu;
    private JButton               aunitbutton;
    private JButton               aunitStop;
    private JMenu                 mualloyMenu;
    private JButton               mualloy;
    private JMenu                 alloyFLMenu;
    private JButton               alloyFL;
    private JMenu                 arepairMenu;
    private JButton               arepairbutton;
    private JButton               arepairStop;


    /** Track if should display different information */
    private static final IntPref  CoverageDisplay        = new IntPref("Calculate Coverage", 0, 0, 1000);
    private static final IntPref  HighlightCoverage      = new IntPref("Highlight Coverage", 0, 0, 1000);
    private static final int      covDisplayLevel        = 2;
    private static final int      highlightLevel         = 2;

    private static final IntPref  RepairDisplay          = new IntPref("Search Strat", 0, 0, 1000);
    private static final int      repairDisplayLevel     = 2;


    /** The list of tests **/
    private List<Test>            tests                  = null;

    /** For GUI display **/
    JPanel                        resultsPanel;
    private JPanel                tabBar;
    private TabPanel              logTab;
    private JPanel                lefthalf;
    private JScrollPane           tabBarScroller;
    private VizValuationGUI       viz_val;
    private Font                  currFont;
    private Color                 medium_gray            = new Color(143, 143, 143);
    private Color                 border_gray            = new Color(173, 173, 173);
    private Color                 coverageGray           = new Color(210, 210, 210);

    private JScrollPane           aunitDisplay;
    private JPanel                aunitText;
    private TabPanel              aunitTab;
    private JLabel                aunitLabel;


    private JScrollPane           coverageDisplay;
    private JPanel                coverageText;
    private TabPanel              coverageTab;
    private JLabel                cov_header_label;
    private JPanel                coverageDetails;

    private JScrollPane           mualloyDisplay;
    private JPanel                mualloyText;
    private TabPanel              mualloyTab;
    private VizMutationGUI        viz_mualloy;

    private JScrollPane           alloyflDisplay;
    private JPanel                alloyflText;
    private TabPanel              alloyflTab;
    private ArrayList<Pos>        latest_highlight_pos;
    private ArrayList<String>     latest_highlight_colors;

    private JScrollPane           arepairDisplay;
    private JPanel                arepairText;
    private TabPanel              arepairTab;

    private double                aaplus_version         = 1.0;

    // ====== helper methods =================================================//

    /**
     * Inserts "filename" into the "recently opened file list".
     */
    private void addHistory(String filename) {
        String name0 = Model0.get(), name1 = Model1.get(), name2 = Model2.get();
        if (name0.equals(filename))
            return;
        else {
            Model0.set(filename);
            Model1.set(name0);
        }
        if (name1.equals(filename))
            return;
        else
            Model2.set(name1);
        if (name2.equals(filename))
            return;
        else
            Model3.set(name2);
    }

    /** Sets the flag "lastFocusIsOnEditor" to be true. */
    private Runner notifyFocusGained() {
        if (wrap)
            return wrapMe();
        lastFocusIsOnEditor = true;
        return null;
    }

    /** Sets the flag "lastFocusIsOnEditor" to be false. */
    public void notifyFocusLost() {
        lastFocusIsOnEditor = false;
    }

    /** Updates the status bar at the bottom of the screen. */
    private Runner notifyChange() {
        if (wrap)
            return wrapMe();
        commands = null;
        if (text == null)
            return null; // If this was called prior to the "text" being fully
                        // initialized
        OurSyntaxWidget t = text.get();
        if (Util.onMac())
            frame.getRootPane().putClientProperty("windowModified", Boolean.valueOf(t.modified()));
        if (t.isFile())
            frame.setTitle(t.getFilename());
        else
            frame.setTitle("Alloy Analyzer Plus " + aaplus_version);
        toolbar.setBorder(new OurBorder(false, false, text.count() <= 1, false));
        int c = t.getCaret();
        int y = t.getLineOfOffset(c) + 1;
        int x = c - t.getLineStartOffset(y - 1) + 1;
        status.setText("<html>&nbsp; Line " + y + ", Column " + x + (t.modified() ? " <b style=\"color:#B43333;\">[modified]</b></html>" : "</html>"));
        return null;
    }

    /**
     * Helper method that returns a hopefully very short name for a file name.
     */
    public static String slightlyShorterFilename(String name) {
        if (name.toLowerCase(Locale.US).endsWith(".als")) {
            int i = name.lastIndexOf('/');
            if (i >= 0)
                name = name.substring(i + 1);
            i = name.lastIndexOf('\\');
            if (i >= 0)
                name = name.substring(i + 1);
            return name.substring(0, name.length() - 4);
        } else if (name.toLowerCase(Locale.US).endsWith(".xml")) {
            int i = name.lastIndexOf('/');
            if (i > 0)
                i = name.lastIndexOf('/', i - 1);
            if (i >= 0)
                name = name.substring(i + 1);
            i = name.lastIndexOf('\\');
            if (i > 0)
                i = name.lastIndexOf('\\', i - 1);
            if (i >= 0)
                name = name.substring(i + 1);
            return name.substring(0, name.length() - 4);
        }
        return name;
    }

    /**
     * Copy the required files from the JAR into a temporary directory.
     */
    private void copyFromJAR() {
        // Compute the appropriate platform
        String os = System.getProperty("os.name").toLowerCase(Locale.US).replace(' ', '-');
        if (os.startsWith("mac-"))
            os = "mac";
        else if (os.startsWith("windows-"))
            os = "windows";
        String arch = System.getProperty("os.arch").toLowerCase(Locale.US).replace(' ', '-');
        if (arch.equals("powerpc"))
            arch = "ppc-" + os;
        else
            arch = arch.replaceAll("\\Ai[3456]86\\z", "x86") + "-" + os;
        if (os.equals("mac"))
            arch = "x86-mac"; // our pre-compiled binaries are all universal
                             // binaries
                             // Find out the appropriate Alloy directory
                             //arch = "x86-windows";
        final String platformBinary = alloyHome() + fs + "binary";
        // Write a few test files
        try {
            (new File(platformBinary)).mkdirs();
            Util.writeAll(platformBinary + fs + "tmp.cnf", "p cnf 3 1\n1 0\n");
        } catch (Err er) {
            // The error will be caught later by the "berkmin" or "spear" test
        }
        // Copy the platform-dependent binaries
        Util.copy(true, false, platformBinary, arch + "/libminisat.so", arch + "/libminisatx1.so", arch + "/libminisat.jnilib", arch + "/libminisat.dylib", arch + "/libminisatprover.so", arch + "/libminisatproverx1.so", arch + "/libminisatprover.jnilib", arch + "/libminisatprover.dylib", arch + "/libzchaff.so", arch + "/libzchaffmincost.so", arch + "/libzchaffx1.so", arch + "/libzchaff.jnilib", arch + "/liblingeling.so", arch + "/liblingeling.dylib", arch + "/liblingeling.jnilib", arch + "/plingeling", arch + "/libglucose.so", arch + "/libglucose.dylib", arch + "/libglucose.jnilib", arch + "/libcryptominisat.so", arch + "/libcryptominisat.la", arch + "/libcryptominisat.dylib", arch + "/libcryptominisat.jnilib", arch + "/berkmin", arch + "/spear", arch + "/cryptominisat");
        Util.copy(false, false, platformBinary, arch + "/minisat.dll", arch + "/cygminisat.dll", arch + "/libminisat.dll.a", arch + "/minisatprover.dll", arch + "/cygminisatprover.dll", arch + "/libminisatprover.dll.a", arch + "/glucose.dll", arch + "/cygglucose.dll", arch + "/libglucose.dll.a", arch + "/zchaff.dll", arch + "/berkmin.exe", arch + "/spear.exe");
        // Copy the model files
        Util.copy(false, true, alloyHome(), "models/book/appendixA/addressBook1.als", "models/book/appendixA/addressBook2.als", "models/book/appendixA/barbers.als", "models/book/appendixA/closure.als", "models/book/appendixA/distribution.als", "models/book/appendixA/phones.als", "models/book/appendixA/prison.als", "models/book/appendixA/properties.als", "models/book/appendixA/ring.als", "models/book/appendixA/spanning.als", "models/book/appendixA/tree.als", "models/book/appendixA/tube.als", "models/book/appendixA/undirected.als", "models/book/appendixE/hotel.thm", "models/book/appendixE/p300-hotel.als", "models/book/appendixE/p303-hotel.als", "models/book/appendixE/p306-hotel.als", "models/book/chapter2/addressBook1a.als", "models/book/chapter2/addressBook1b.als", "models/book/chapter2/addressBook1c.als", "models/book/chapter2/addressBook1d.als", "models/book/chapter2/addressBook1e.als", "models/book/chapter2/addressBook1f.als", "models/book/chapter2/addressBook1g.als", "models/book/chapter2/addressBook1h.als", "models/book/chapter2/addressBook2a.als", "models/book/chapter2/addressBook2b.als", "models/book/chapter2/addressBook2c.als", "models/book/chapter2/addressBook2d.als", "models/book/chapter2/addressBook2e.als", "models/book/chapter2/addressBook3a.als", "models/book/chapter2/addressBook3b.als", "models/book/chapter2/addressBook3c.als", "models/book/chapter2/addressBook3d.als", "models/book/chapter2/theme.thm", "models/book/chapter4/filesystem.als", "models/book/chapter4/grandpa1.als", "models/book/chapter4/grandpa2.als", "models/book/chapter4/grandpa3.als", "models/book/chapter4/lights.als", "models/book/chapter5/addressBook.als", "models/book/chapter5/lists.als", "models/book/chapter5/sets1.als", "models/book/chapter5/sets2.als", "models/book/chapter6/hotel.thm", "models/book/chapter6/hotel1.als", "models/book/chapter6/hotel2.als", "models/book/chapter6/hotel3.als", "models/book/chapter6/hotel4.als", "models/book/chapter6/mediaAssets.als", "models/book/chapter6/memory/abstractMemory.als", "models/book/chapter6/memory/cacheMemory.als", "models/book/chapter6/memory/checkCache.als", "models/book/chapter6/memory/checkFixedSize.als", "models/book/chapter6/memory/fixedSizeMemory.als", "models/book/chapter6/memory/fixedSizeMemory_H.als", "models/book/chapter6/ringElection.thm", "models/book/chapter6/ringElection1.als", "models/book/chapter6/ringElection2.als", "models/examples/algorithms/dijkstra.als", "models/examples/algorithms/dijkstra.thm", "models/examples/algorithms/messaging.als", "models/examples/algorithms/messaging.thm", "models/examples/algorithms/opt_spantree.als", "models/examples/algorithms/opt_spantree.thm", "models/examples/algorithms/peterson.als", "models/examples/algorithms/ringlead.als", "models/examples/algorithms/ringlead.thm", "models/examples/algorithms/s_ringlead.als", "models/examples/algorithms/stable_mutex_ring.als", "models/examples/algorithms/stable_mutex_ring.thm", "models/examples/algorithms/stable_orient_ring.als", "models/examples/algorithms/stable_orient_ring.thm", "models/examples/algorithms/stable_ringlead.als", "models/examples/algorithms/stable_ringlead.thm", "models/examples/case_studies/INSLabel.als", "models/examples/case_studies/chord.als", "models/examples/case_studies/chord2.als", "models/examples/case_studies/chordbugmodel.als", "models/examples/case_studies/com.als", "models/examples/case_studies/firewire.als", "models/examples/case_studies/firewire.thm", "models/examples/case_studies/ins.als", "models/examples/case_studies/iolus.als", "models/examples/case_studies/sync.als", "models/examples/case_studies/syncimpl.als", "models/examples/puzzles/farmer.als", "models/examples/puzzles/farmer.thm", "models/examples/puzzles/handshake.als", "models/examples/puzzles/handshake.thm", "models/examples/puzzles/hanoi.als", "models/examples/puzzles/hanoi.thm", "models/examples/systems/file_system.als", "models/examples/systems/file_system.thm", "models/examples/systems/javatypes_soundness.als", "models/examples/systems/lists.als", "models/examples/systems/lists.thm", "models/examples/systems/marksweepgc.als", "models/examples/systems/views.als", "models/examples/toys/birthday.als", "models/examples/toys/birthday.thm", "models/examples/toys/ceilingsAndFloors.als", "models/examples/toys/ceilingsAndFloors.thm", "models/examples/toys/genealogy.als", "models/examples/toys/genealogy.thm", "models/examples/toys/grandpa.als", "models/examples/toys/grandpa.thm", "models/examples/toys/javatypes.als", "models/examples/toys/life.als", "models/examples/toys/life.thm", "models/examples/toys/numbering.als", "models/examples/toys/railway.als", "models/examples/toys/railway.thm", "models/examples/toys/trivial.als", "models/examples/tutorial/farmer.als", "models/util/boolean.als", "models/util/graph.als", "models/util/integer.als", "models/util/natural.als", "models/util/ordering.als", "models/util/relation.als", "models/util/seqrel.als", "models/util/sequence.als", "models/util/sequniv.als", "models/util/ternary.als", "models/util/time.als");
        // Record the locations
        System.setProperty("alloy.theme0", alloyHome() + fs + "models");
        System.setProperty("alloy.home", alloyHome());
    }

    /** Called when this window is resized. */
    @Override
    public void componentResized(ComponentEvent e) {
        componentMoved(e);
    }

    /** Called when this window is moved. */
    @Override
    public void componentMoved(ComponentEvent e) {
        AnalyzerWidth.set(frame.getWidth());
        AnalyzerHeight.set(frame.getHeight());
        AnalyzerX.set(frame.getX());
        AnalyzerY.set(frame.getY());
    }

    /** Called when this window is shown. */
    @Override
    public void componentShown(ComponentEvent e) {
    }

    /** Called when this window is hidden. */
    @Override
    public void componentHidden(ComponentEvent e) {
    }

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
                    method.invoke(SimpleGUI.this, new Object[] {});
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
                    method.invoke(SimpleGUI.this, new Object[] {
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
     * This variable caches the result of alloyHome() function call.
     */
    private static String alloyHome = null;

    /**
     * Find a temporary directory to store Alloy files; it's guaranteed to be a
     * canonical absolute path.
     */
    private static synchronized String alloyHome() {
        if (alloyHome != null)
            return alloyHome;
        String temp = System.getProperty("java.io.tmpdir");
        if (temp == null || temp.length() == 0)
            OurDialog.fatal("Error. JVM need to specify a temporary directory using java.io.tmpdir property.");
        String username = System.getProperty("user.name");
        File tempfile = new File(temp + File.separatorChar + "alloy4tmp40-" + (username == null ? "" : username));
        tempfile.mkdirs();
        String ans = Util.canon(tempfile.getPath());
        if (!tempfile.isDirectory()) {
            OurDialog.fatal("Error. Cannot create the temporary directory " + ans);
        }
        if (!Util.onWindows()) {
            String[] args = {
                             "chmod", "700", ans
            };
            try {
                Runtime.getRuntime().exec(args).waitFor();
            } catch (Throwable ex) {
            } // We only intend to make a best effort.
        }
        return alloyHome = ans;
    }

    /**
     * Create an empty temporary directory for use, designate it "deleteOnExit",
     * then return it. It is guaranteed to be a canonical absolute path.
     */
    private static String maketemp() {
        Random r = new Random(new Date().getTime());
        while (true) {
            int i = r.nextInt(1000000);
            String dest = alloyHome() + File.separatorChar + "tmp" + File.separatorChar + i;
            File f = new File(dest);
            if (f.mkdirs()) {
                f.deleteOnExit();
                return Util.canon(dest);
            }
        }
    }

    /**
     * Return the number of bytes used by the Temporary Directory (or return -1 if
     * the answer exceeds "long")
     */
    private static long computeTemporarySpaceUsed() {
        long ans = iterateTemp(null, false);
        if (ans < 0)
            return -1;
        else
            return ans;
    }

    /** Delete every file in the Temporary Directory. */
    private static void clearTemporarySpace() {
        iterateTemp(null, true);
        // Also clear the temp dir from previous versions of Alloy4
        String temp = System.getProperty("java.io.tmpdir");
        if (temp == null || temp.length() == 0)
            return;
        String username = System.getProperty("user.name");
        if (username == null)
            username = "";
        for (int i = 1; i < 40; i++)
            iterateTemp(temp + File.separatorChar + "alloy4tmp" + i + "-" + username, true);
    }

    /**
     * Helper method for performing either computeTemporarySpaceUsed() or
     * clearTemporarySpace()
     */
    private static long iterateTemp(String filename, boolean delete) {
        long ans = 0;
        if (filename == null)
            filename = alloyHome() + File.separatorChar + "tmp";
        File x = new File(filename);
        if (x.isDirectory()) {
            for (String subfile : x.list()) {
                long tmp = iterateTemp(filename + File.separatorChar + subfile, delete);
                if (ans >= 0)
                    ans = ans + tmp;
            }
        } else if (x.isFile()) {
            long tmp = x.length();
            if (ans >= 0)
                ans = ans + tmp;
        }
        if (delete)
            x.delete();
        return ans;
    }

    // ===============================================================================================================//

    /** This method refreshes the "file" menu. */
    private Runner doRefreshFile() {
        if (wrap)
            return wrapMe();
        try {
            wrap = true;
            filemenu.removeAll();
            menuItem(filemenu, "New", 'N', 'N', doNew());
            menuItem(filemenu, "Open...", 'O', 'O', doOpen());
            if (!Util.onMac())
                menuItem(filemenu, "Open Sample Models...", VK_ALT, 'O', doBuiltin());
            else
                menuItem(filemenu, "Open Sample Models...", doBuiltin());
            JMenu recentmenu;
            filemenu.add(recentmenu = new JMenu("Open Recent"));
            menuItem(filemenu, "Reload all", 'R', 'R', doReloadAll());
            menuItem(filemenu, "Save", 'S', 'S', doSave());
            if (Util.onMac())
                menuItem(filemenu, "Save As...", VK_SHIFT, 'S', doSaveAs());
            else
                menuItem(filemenu, "Save As...", 'A', doSaveAs());
            menuItem(filemenu, "Close", 'W', 'W', doClose());
            menuItem(filemenu, "Clear Temporary Directory", doClearTemp());
            menuItem(filemenu, "Quit", 'Q', (Util.onMac() ? -1 : 'Q'), doQuit());
            boolean found = false;
            for (StringPref p : new StringPref[] {
                                                  Model0, Model1, Model2, Model3
            }) {
                String name = p.get();
                if (name.length() > 0) {
                    found = true;
                    menuItem(recentmenu, name, doOpenFile(name));
                }
            }
            recentmenu.addSeparator();
            menuItem(recentmenu, "Clear Menu", doClearRecent());
            recentmenu.setEnabled(found);
        } finally {
            wrap = false;
        }
        return null;
    }

    /** This method performs File->New. */
    private Runner doNew() {
        if (!wrap) {
            text.newtab(null);
            notifyChange();
            doShow();
        }
        return wrapMe();
    }

    /** This method performs File->Open. */
    private Runner doOpen() {
        if (wrap)
            return wrapMe();
        File file = getFile(null);
        if (file != null) {
            Util.setCurrentDirectory(file.getParentFile());
            doOpenFile(file.getPath());
        }
        return null;
    }

    /** This method performs File->OpenBuiltinModels. */
    private Runner doBuiltin() {
        if (wrap)
            return wrapMe();
        File file = getFile(alloyHome() + fs + "models");
        if (file != null) {
            doOpenFile(file.getPath());
        }
        return null;
    }

    private File getFile(String home) {
        File file = OurDialog.askFile(true, home, new String[] {
                                                                ".als", ".md", "*"
        }, "Alloy (.als) or Markdown (.md) files");
        return file;
    }

    /** This method performs File->ReloadAll. */
    private Runner doReloadAll() {
        if (!wrap)
            text.reloadAll();
        return wrapMe();
    }

    /** This method performs File->ClearRecentFiles. */
    private Runner doClearRecent() {
        if (!wrap) {
            Model0.set("");
            Model1.set("");
            Model2.set("");
            Model3.set("");
        }
        return wrapMe();
    }

    /** This method performs File->Save. */
    private Runner doSave() {
        if (!wrap) {
            String ans = text.save(false);
            if (ans == null)
                return null;
            notifyChange();
            addHistory(ans);
            log.clearError();
        }
        return wrapMe();
    }

    /** This method performs File->SaveAs. */
    private Runner doSaveAs() {
        if (!wrap) {
            String ans = text.save(true);
            if (ans == null)
                return null;
            notifyChange();
            addHistory(ans);
            log.clearError();
        }
        return wrapMe();
    }

    /**
     * This method clears the temporary files and then reinitialize the temporary
     * directory.
     */
    private Runner doClearTemp() {
        if (!wrap) {
            clearTemporarySpace();
            copyFromJAR();
            log.logBold("Temporary directory has been cleared.\n\n");
            log.logDivider();
            log.flush();
        }
        return wrapMe();
    }

    /** This method performs File->Close. */
    private Runner doClose() {
        if (!wrap)
            text.close();
        return wrapMe();
    }

    /** This method performs File->Quit. */
    public Runner doQuit() {
        if (!wrap)
            if (text.closeAll()) {
                try {
                    WorkerEngine.stop();
                } finally {
                    System.exit(0);
                }
            }
        return wrapMe();
    }

    // ===============================================================================================================//

    /** This method refreshes the "edit" menu. */
    private Runner doRefreshEdit() {
        if (wrap)
            return wrapMe();
        try {
            wrap = true;
            boolean canUndo = text.get().canUndo();
            boolean canRedo = text.get().canRedo();
            editmenu.removeAll();
            menuItem(editmenu, "Undo", 'Z', 'Z', doUndo(), canUndo);
            if (Util.onMac())
                menuItem(editmenu, "Redo", VK_SHIFT, 'Z', doRedo(), canRedo);
            else
                menuItem(editmenu, "Redo", 'Y', 'Y', doRedo(), canRedo);
            editmenu.addSeparator();
            menuItem(editmenu, "Cut", 'X', 'X', doCut());
            menuItem(editmenu, "Copy", 'C', 'C', doCopy());
            menuItem(editmenu, "Paste", 'V', 'V', doPaste());
            editmenu.addSeparator();
            menuItem(editmenu, "Go To...", 'T', 'T', doGoto());
            menuItem(editmenu, "Previous File", VK_PAGE_UP, VK_PAGE_UP, doGotoPrevFile(), text.count() > 1);
            menuItem(editmenu, "Next File", VK_PAGE_DOWN, VK_PAGE_DOWN, doGotoNextFile(), text.count() > 1);
            editmenu.addSeparator();
            menuItem(editmenu, "Find...", 'F', 'F', doFind());
            menuItem(editmenu, "Find Next", 'G', 'G', doFindNext());
            editmenu.addSeparator();
            if (!Util.onMac())
                menuItem(editmenu, "Preferences", 'P', 'P', doPreferences());
        } finally {
            wrap = false;
        }
        return null;
    }

    /** This method performs Edit->Undo. */
    private Runner doUndo() {
        if (!wrap)
            text.get().undo();
        return wrapMe();
    }

    /** This method performs Edit->Redo. */
    private Runner doRedo() {
        if (!wrap)
            text.get().redo();
        return wrapMe();
    }

    /** This method performs Edit->Copy. */
    private Runner doCopy() {
        if (!wrap) {
            if (lastFocusIsOnEditor)
                text.get().copy();
            else
                log.copy();
        }
        return wrapMe();
    }

    /** This method performs Edit->Cut. */
    private Runner doCut() {
        if (!wrap && lastFocusIsOnEditor)
            text.get().cut();
        return wrapMe();
    }

    /** This method performs Edit->Paste. */
    private Runner doPaste() {
        if (!wrap && lastFocusIsOnEditor)
            text.get().paste();
        return wrapMe();
    }

    /** This method performs Edit->Find. */
    private Runner doFind() {
        if (wrap)
            return wrapMe();
        JTextField x = OurUtil.textfield(lastFind, 30);
        x.selectAll();
        JCheckBox c = new JCheckBox("Case Sensitive?", lastFindCaseSensitive);
        c.setMnemonic('c');
        JCheckBox b = new JCheckBox("Search Backward?", !lastFindForward);
        b.setMnemonic('b');
        if (!OurDialog.getInput("Find", "Text:", x, " ", c, b))
            return null;
        if (x.getText().length() == 0)
            return null;
        lastFind = x.getText();
        lastFindCaseSensitive = c.getModel().isSelected();
        lastFindForward = !b.getModel().isSelected();
        doFindNext();
        return null;
    }

    /** This method performs Edit->FindNext. */
    private Runner doFindNext() {
        if (wrap)
            return wrapMe();
        if (lastFind.length() == 0)
            return null;
        OurSyntaxWidget t = text.get();
        String all = t.getText();
        int i = Util.indexOf(all, lastFind, t.getCaret() + (lastFindForward ? 0 : -1), lastFindForward, lastFindCaseSensitive);
        if (i < 0) {
            i = Util.indexOf(all, lastFind, lastFindForward ? 0 : (all.length() - 1), lastFindForward, lastFindCaseSensitive);
            if (i < 0) {
                log.logRed("The specified search string cannot be found.");
                return null;
            }
            log.logRed("Search wrapped.");
        } else {
            log.clearError();
        }
        if (lastFindForward)
            t.moveCaret(i, i + lastFind.length());
        else
            t.moveCaret(i + lastFind.length(), i);
        t.requestFocusInWindow();
        return null;
    }

    /** This method performs Edit->Preferences. */
    public Runner doPreferences() {
        if (wrap)
            return wrapMe();
        prefDialog.setVisible(true);
        return null;
    }

    /**
     * This method applies the look and feel stored in a user preference. Default
     * look and feel for Mac and Windows computers is "Native", and for other is
     * "Cross-platform".
     */
    private Runner doLookAndFeel() {
        if (wrap)
            return wrapMe();
        try {
            if ("Native".equals(LAF.get())) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            SwingUtilities.updateComponentTreeUI(frame);
            SwingUtilities.updateComponentTreeUI(prefDialog);
            SwingUtilities.updateComponentTreeUI(viz.getFrame());
        } catch (Throwable e) {
        }
        return null;
    }

    /** This method performs Edit->Goto. */
    private Runner doGoto() {
        if (wrap)
            return wrapMe();
        JTextField y = OurUtil.textfield("", 10);
        JTextField x = OurUtil.textfield("", 10);
        if (!OurDialog.getInput("Go To", "Line Number:", y, "Column Number (optional):", x))
            return null;
        try {
            OurSyntaxWidget t = text.get();
            int xx = 1, yy = Integer.parseInt(y.getText()), lineCount = t.getLineCount();
            if (yy < 1)
                return null;
            if (yy > lineCount) {
                log.logRed("This file only has " + lineCount + " line(s).");
                return null;
            }
            if (x.getText().length() != 0)
                xx = Integer.parseInt(x.getText());
            if (xx < 1) {
                log.logRed("If the column number is specified, it must be 1 or greater.");
                return null;
            }
            int caret = t.getLineStartOffset(yy - 1);
            int len = (yy == lineCount ? t.getText().length() + 1 : t.getLineStartOffset(yy)) - caret;
            if (xx > len)
                xx = len;
            if (xx < 1)
                xx = 1;
            t.moveCaret(caret + xx - 1, caret + xx - 1);
            t.requestFocusInWindow();
        } catch (NumberFormatException ex) {
            log.logRed("The number must be 1 or greater.");
        } catch (Throwable ex) {
            // This error is not important
        }
        return null;
    }

    /** This method performs Edit->GotoPrevFile. */
    private Runner doGotoPrevFile() {
        if (wrap)
            return wrapMe();
        else {
            text.prev();
            return null;
        }
    }

    /** This method performs Edit->GotoNextFile. */
    private Runner doGotoNextFile() {
        if (wrap)
            return wrapMe();
        else {
            text.next();
            return null;
        }
    }

    // ===============================================================================================================//

    /** This method refreshes the "run" menu. */
    private Runner doRefreshRun() {
        if (wrap)
            return wrapMe();
        KeyStroke ac = KeyStroke.getKeyStroke(VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        try {
            wrap = true;
            runmenu.removeAll();
            menuItem(runmenu, "Execute Latest Command", 'E', 'E', doExecuteLatest());
            runmenu.add(new JSeparator());
            menuItem(runmenu, "Show Latest Instance", 'L', 'L', doShowLatest(), latestInstance.length() > 0);
            menuItem(runmenu, "Show Metamodel", 'M', 'M', doShowMetaModel());
            if (Version.experimental)
                menuItem(runmenu, "Show Parse Tree", 'P', doShowParseTree());
            menuItem(runmenu, "Open Evaluator", 'V', doLoadEvaluator());
        } finally {
            wrap = false;
        }
        List<Command> cp = commands;
        if (cp == null) {
            try {
                String source = text.get().getText();
                cp = CompUtil.parseOneModule_fromString(source);
            } catch (Err e) {
                commands = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if (AlloyCore.isDebug() && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                commands = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            commands = cp;
        }
        text.clearShade();
        log.clearError(); // To clear any residual error message
        if (cp == null) {
            runmenu.getItem(0).setEnabled(false);
            runmenu.getItem(3).setEnabled(false);
            return null;
        }
        if (cp.size() == 0) {
            runmenu.getItem(0).setEnabled(false);
            return null;
        }
        if (latestCommand >= cp.size())
            latestCommand = cp.size() - 1;
        runmenu.remove(0);
        try {
            wrap = true;
            for (int i = 0; i < cp.size(); i++) {
                JMenuItem y = new JMenuItem(cp.get(i).toString(), null);
                y.addActionListener(doRun(i));
                if (i == latestCommand) {
                    y.setMnemonic(VK_E);
                    y.setAccelerator(ac);
                }
                runmenu.add(y, i);
            }
            if (cp.size() >= 2) {
                JMenuItem y = new JMenuItem("Execute All", null);
                y.setMnemonic(VK_A);
                y.addActionListener(doRun(-1));
                runmenu.add(y, 0);
                runmenu.add(new JSeparator(), 1);
            }
        } finally {
            wrap = false;
        }
        return null;
    }

    /**
     * This method executes a particular RUN or CHECK command.
     */
    private Runner doRun(Integer commandIndex) {
        if (wrap)
            return wrapMe(commandIndex);
        final int index = commandIndex;
        if (WorkerEngine.isBusy())
            return null;
        if (index == (-2))
            subrunningTask = 1;
        else
            subrunningTask = 0;
        latestAutoInstance = "";
        if (index >= 0)
            latestCommand = index;
        if (index == -1 && commands != null) {
            latestCommand = commands.size() - 1;
            if (latestCommand < 0)
                latestCommand = 0;
        }
        // To update the accelerator to point to the command actually chosen
        doRefreshRun();
        aunitText.removeAll();
        aunitText.updateUI();
        coverageText.removeAll();
        coverageText.updateUI();
        mualloyText.removeAll();
        mualloyText.updateUI();

        OurUtil.enableAll(runmenu);
        if (commands == null)
            return null;
        if (commands.size() == 0 && index != -2 && index != -3) {
            log.logRed("There are no commands to execute.\n\n");
            return null;
        }
        int i = index;
        if (i >= commands.size())
            i = commands.size() - 1;
        SimpleCallback1 cb = new SimpleCallback1(this, null, log, VerbosityPref.get().ordinal(), latestAlloyVersionName, latestAlloyVersion);
        SimpleTask1 task = new SimpleTask1();
        A4Options opt = new A4Options();
        opt.tempDirectory = alloyHome() + fs + "tmp";
        opt.solverDirectory = alloyHome() + fs + "binary";
        opt.recordKodkod = RecordKodkod.get();
        opt.noOverflow = NoOverflow.get();
        opt.unrolls = Version.experimental ? Unrolls.get() : (-1);
        opt.skolemDepth = SkolemDepth.get();
        opt.coreMinimization = CoreMinimization.get();
        opt.inferPartialInstance = InferPartialInstance.get();
        opt.coreGranularity = CoreGranularity.get();
        opt.originalFilename = Util.canon(text.get().getFilename());
        opt.solver = Solver.get();
        task.bundleIndex = i;
        task.bundleWarningNonFatal = WarningNonfatal.get();
        task.map = text.takeSnapshot();
        task.options = opt.dup();
        task.resolutionMode = (Version.experimental && ImplicitThis.get()) ? 2 : 1;
        task.tempdir = maketemp();
        try {
            runmenu.setEnabled(false);
            runbutton.setVisible(false);
            showbutton.setEnabled(false);
            stopbutton.setVisible(true);
            int newmem = SubMemory.get(), newstack = SubStack.get();
            if (newmem != subMemoryNow || newstack != subStackNow)
                WorkerEngine.stop();
            if (AlloyCore.isDebug() && VerbosityPref.get() == Verbosity.FULLDEBUG)
                WorkerEngine.runLocally(task, cb);
            else
                WorkerEngine.run(task, newmem, newstack, alloyHome() + fs + "binary", "", cb);
            subMemoryNow = newmem;
            subStackNow = newstack;
        } catch (Throwable ex) {
            WorkerEngine.stop();
            log.logBold("Fatal Error: Solver failed due to unknown reason.\n" + "One possible cause is that, in the Options menu, your specified\n" + "memory size is larger than the amount allowed by your OS.\n" + "Also, please make sure \"java\" is in your program path.\n");
            log.logDivider();
            log.flush();
            doStop(2);
        }

        return null;
    }

    /**
     * This method stops the current run or check (how==0 means DONE, how==1 means
     * FAIL, how==2 means STOP).
     */
    public Runner doStop(Integer how) {
        if (wrap)
            return wrapMe(how);
        int h = how;
        if (h != 0) {
            if (h == 2 && WorkerEngine.isBusy()) {
                WorkerEngine.stop();
                log.logBold("\nSolving Stopped.\n");
                log.logDivider();
            }
            WorkerEngine.stop();
        }
        runmenu.setEnabled(true);
        runbutton.setVisible(true);
        showbutton.setEnabled(true);
        stopbutton.setVisible(false);
        aunitbutton.setVisible(true);
        aunitStop.setVisible(false);
        mualloy.setVisible(true);
        alloyFL.setVisible(true);
        if (latestAutoInstance.length() > 0) {
            String f = latestAutoInstance;
            latestAutoInstance = "";
            if (subrunningTask == 2)
                viz.loadXML(f, true);
            else if (AutoVisualize.get() || subrunningTask == 1)
                doVisualize("XML: " + f);
        }
        return null;
    }

    /** This method executes the latest command. */
    private Runner doExecuteLatest() {
        if (wrap)
            return wrapMe();
        doRefreshRun();
        OurUtil.enableAll(runmenu);
        if (commands == null)
            return null;
        int n = commands.size();
        if (n <= 0) {
            log.logRed("There are no commands to execute.\n\n");
            return null;
        }
        if (latestCommand >= n)
            latestCommand = n - 1;
        if (latestCommand < 0)
            latestCommand = 0;
        return doRun(latestCommand);
    }

    /** Sullivan **/
    /** This method refreshes the "aunit" menu. */
    private Runner doRefreshAUnit() {
        if (wrap)
            return wrapMe();
        KeyStroke ac = KeyStroke.getKeyStroke(VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        try {
            wrap = true;
            aunitMenu.removeAll();
            menuItem(aunitMenu, "Execute Test Suite", doAUnitExecute());
            aunitMenu.add(new JSeparator());
            menuItem(aunitMenu, "Calculate coverage: " + (CoverageDisplay.get() < covDisplayLevel ? "Yes" : "No"), doOptCoverageDisplay());
            menuItem(aunitMenu, "Highlight coverage: " + (HighlightCoverage.get() < highlightLevel ? "Yes" : "No"), doOptHighlightCoverage());
            aunitMenu.addSeparator();
            menuItem(aunitMenu, "AUnit Tutorial", doAUnitHelp());
        } finally {
            wrap = false;
        }
        List<Command> cp = commands;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromString(text.get().getText());
            } catch (Err e) {
                commands = null;
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                commands = null;

                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            commands = cp;
        }
        return null;
    }

    public void doDisplayRun() {
        //Clear results and set focus to the logging tab for basic alloy execution
        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(logpane, BorderLayout.CENTER);
        logTab.setActive();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();
    }

    public void doDisplayAUnitExetensionStart() {
        //aunitText.removeAll();
        //coverageText.removeAll();
        //mualloyText.removeAll();
        //alloyflText.removeAll();
    }

    ArrayList<JTree> aunitTrees = new ArrayList<JTree>();

    public void doDisplayAUnitTree(ArrayList<JTree> trees, ArrayList<DefaultMutableTreeNode> cmdNodes) {
        int yloc = 4;
        JPanel testDetails = new JPanel();
        testDetails.setLayout(new BoxLayout(testDetails, BoxLayout.Y_AXIS));
        testDetails.setBackground(Color.white);
        Border border = testDetails.getBorder();
        Border margin = new EmptyBorder(0, 15, 0, 0);
        testDetails.setBorder(new CompoundBorder(border, margin));

        for (JTree tree : trees) {
            JPanel displayTree = new JPanel();
            displayTree.setLayout(new BorderLayout());
            displayTree.setBorder(new EmptyBorder(2, 2, 2, 2));

            tree.setShowsRootHandles(true);
            tree.setRootVisible(true);

            PassFailRenderer passFailRenderer = new PassFailRenderer();
            tree.setCellRenderer(passFailRenderer);
            tree.addMouseListener(new MouseListener() {

                public final void mousePressed(MouseEvent e) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        if (selPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                            Object o = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
                            if (o instanceof AUnitTreeNode) {
                                AUnitTreeNode node = (AUnitTreeNode) o;
                                if (node.linkDestination != null) {
                                    viz_val.setTestName(node.test_name);
                                    doVisualize("VAL: " + node.linkDestination);
                                }
                            }
                        }
                    }
                }

                public final void mouseClicked(MouseEvent e) {
                }

                public final void mouseReleased(MouseEvent e) {
                }

                public final void mouseEntered(MouseEvent e) {
                }

                public final void mouseExited(MouseEvent e) {
                }
            });

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

            for (DefaultMutableTreeNode node : cmdNodes) {
                tree.expandPath(new TreePath(node.getPath()));
            }

            if (root.getUserObject() instanceof PassFailTreeNode) {
                PassFailTreeNode passFail = (PassFailTreeNode) root.getUserObject();
                if (passFail.result.equals("pass")) {
                    tree.collapseRow(0);
                }
            }

            tree.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            tree.setRowHeight(0);
            aunitTrees.add(tree);
            displayTree.add(aunitTrees.get(aunitTrees.size() - 1));
            displayTree.setBackground(Color.white);
            testDetails.add(displayTree);
        }

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yloc;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy = yloc;
        aunitText.add(testDetails, c);

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = yloc + 1;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        aunitText.add(new JLabel(" "), pushTopLeft);

        //Clear display and set the focus to the aunit results tab
        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(aunitDisplay, BorderLayout.CENTER);
        aunitTab.setActive();
        logTab.deactivate();
        aunitLabel.setForeground(Color.black);
        aunitLabel.setIcon(OurUtil.loadIcon("images/aunitresults.png"));
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();
        /*
         * if (aunitTabClosed) { tabBar.add(aunitTab); aunitTabClosed = false; }
         */
    }

    public void doDisplayCoverageTable(ArrayList<CoverageInformation> covInfos, HashMap<String,JTree> cov_per_test) {
        coverageText.removeAll();
        int panels = 0;
        /** Header for results **/
        cov_header_label = new JLabel("Coverage Results");
        cov_header_label.setFont(new Font(FontName.get(), Font.BOLD, FontSize.get() + 4));
        GridBagConstraints addLabel = new GridBagConstraints();
        addLabel.gridx = 0;
        addLabel.gridy = panels;
        addLabel.anchor = GridBagConstraints.NORTHWEST;
        panels++;
        Border border = cov_header_label.getBorder();
        Border margin = new EmptyBorder(5, 5, 5, 5);
        cov_header_label.setBorder(new CompoundBorder(border, margin));
        coverageText.add(cov_header_label, addLabel);

        for (CoverageInformation covInfo : covInfos) {
            /*
             * For each paragraph: - Find and display the amount of coverage - Display the
             * breakdown of coverage
             */

            /* Blank slate for coverage trees */
            coverageDetails = new JPanel();
            coverageDetails.setLayout(new BoxLayout(coverageDetails, BoxLayout.PAGE_AXIS));
            coverageDetails.setMinimumSize(coverageText.getPreferredSize());

            /*
             * Display paragraph information Start with: Displaying the name and coverage
             * percentage bar
             */
            JPanel paragraph = new JPanel();
            paragraph.setLayout(new BorderLayout());
            JLabel para_name = new JLabel("<html><font color = white><b>Paragraph:</b> " + covInfo.paragraph, JLabel.LEFT);
            para_name.setBorder(new EmptyBorder(5, 5, 5, 5));
            para_name.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));

            int loopPassing = (20 * covInfo.covered) / covInfo.totalCriteria;

            //Build coverage percentage bar icon
            ArrayList<Icon> icons = new ArrayList<Icon>();
            //int loopPassing = (20 * covInfo.covered) / covInfo.totalCriteria ;
            int loopFailing = 20 - loopPassing;
            icons.add(OurUtil.loadIcon("images/black_small.png"));
            for (int i = 0; i < loopPassing; i++) {
                icons.add(OurUtil.loadIcon("images/green_small.png"));
            }
            for (int i = 0; i < loopFailing; i++) {
                icons.add(OurUtil.loadIcon("images/red_small.png"));
            }
            icons.add(OurUtil.loadIcon("images/black_small.png"));
            CompoundIcon compoundIcon = new CompoundIcon(icons.toArray(new Icon[icons.size()]));

            JLabel icon = new JLabel("", compoundIcon, JLabel.RIGHT);
            icon.setBorder(new EmptyBorder(5, 5, 5, 5));

            //Add name and icon to paragraph container
            paragraph.add(para_name, BorderLayout.WEST);
            paragraph.add(icon, BorderLayout.EAST);
            paragraph.setBorder(new EmptyBorder(5, 5, 5, 5));
            border = coverageDetails.getBorder();
            margin = new MatteBorder(1, 0, 1, 0, Color.black);
            paragraph.setBorder(new CompoundBorder(border, margin));
            paragraph.setBackground(Color.DARK_GRAY);

            coverageDetails.add(paragraph);
            boolean white = true;
            for (JTree covTree : covInfo.coverageTrees) {

                JPanel displayTree = new JPanel();
                displayTree.setLayout(new BorderLayout());
                covTree.setShowsRootHandles(true);
                covTree.setRootVisible(true);
                CoverageTreeRenderer coverageTreeRenderer = new CoverageTreeRenderer();
                covTree.setCellRenderer(coverageTreeRenderer);
                covTree.collapseRow(0);
                if (!white) {
                    displayTree.setBackground(coverageGray);
                    covTree.setBackground(coverageGray);
                    white = true;
                } else {
                    displayTree.setBackground(Color.WHITE);
                    white = false;
                }
                covTree.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
                covTree.setRowHeight(0);
                displayTree.setBorder(new EmptyBorder(2, 2, 2, 2));
                displayTree.add(covTree);
                coverageDetails.add(displayTree);
            }

            GridBagConstraints addCovDetails = new GridBagConstraints();
            addCovDetails.gridx = 0;
            addCovDetails.gridy = panels;
            addCovDetails.anchor = GridBagConstraints.NORTHWEST;
            addCovDetails.fill = GridBagConstraints.HORIZONTAL;
            panels++;
            coverageDetails.setBorder(new MatteBorder(0, 1, 1, 1, Color.black));
            border = coverageDetails.getBorder();
            margin = new EmptyBorder(5, 5, 5, 5);
            coverageDetails.setBorder(new CompoundBorder(margin, border));
            coverageDetails.setBackground(Color.white);
            coverageText.add(coverageDetails, addCovDetails);

        }

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = panels;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        coverageText.add(new JLabel(" "), pushTopLeft);

        /*
         * if (coverageTabClosed) { tabBar.add(coverageTab); coverageTabClosed = false;
         * }
         */
    }

    public void doDisplaySummaryBar(int numP, int numF, int numE, String name, int totalTime) {

        int width = resultsPanel.getWidth() - 12;

        JPanel summary = new JPanel();
        summary.setBackground(Color.WHITE);
        summary.setLayout(new GridBagLayout());

        JPanel results = AUnitGuiUtil.makeSummary(numP, numF, numE, totalTime, new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        JPanel summaryBar = AUnitGuiUtil.makeSummaryBarDisplay(width, numP, numF, numE, totalTime, new Font(FontName.get(), Font.PLAIN, FontSize.get()));

        GridBagConstraints barConstraint = new GridBagConstraints();
        barConstraint.fill = GridBagConstraints.HORIZONTAL;
        barConstraint.gridx = 0;
        barConstraint.gridy = 0;
        barConstraint.gridwidth = 4;
        barConstraint.insets = new Insets(5, 5, 5, 5);
        barConstraint.anchor = GridBagConstraints.CENTER;
        summary.add(summaryBar, barConstraint);

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = 2;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        summary.add(new JLabel(""), pushTopLeft);

        pushTopLeft.gridx = 1;
        pushTopLeft.gridy = 3;
        summary.add(new JLabel(""), pushTopLeft);

        pushTopLeft.gridx = 2;
        pushTopLeft.gridy = 3;
        summary.add(new JLabel(""), pushTopLeft);

        GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.gridx = 0;
        textConstraints.gridy = 0;
        textConstraints.fill = GridBagConstraints.HORIZONTAL;
        textConstraints.anchor = GridBagConstraints.NORTHWEST;
        textConstraints.weightx = 1.0;
        textConstraints.ipadx = 10;
        textConstraints.ipady = 5;
        aunitText.add(summary, textConstraints);
        textConstraints.gridy = 1;
        aunitText.add(results, textConstraints);
        textConstraints.gridy = 2;
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(coverageGray);
        textConstraints.insets = new Insets(6, 5, 5, 5);
        aunitText.add(sep, textConstraints);
        textConstraints.gridy = 3;
        textConstraints.insets = new Insets(0, 5, 0, 5);
        JLabel label = new JLabel("<html><b>\"" + name + "\"</b> Test Results:");
        label.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        aunitText.add(label, textConstraints);
    }

    int mualloy_yloc = 0;

    public void doDisplaySummaryMuAlloyTest(int num_killed, int num_not_killed, long time_gen, long time_mutation, long time_testgen, String name) {
        int num_total = num_killed + num_not_killed;
        int width = resultsPanel.getWidth() - 12;

        long total_time = time_gen + time_mutation + time_testgen;
        total_time = total_time / 1000000;

        time_gen = time_gen / 1000000;
        double time_gen_seconds = time_gen / 1000.0;
        time_mutation = time_mutation / 1000000;
        double time_mutation_seconds = time_mutation / 1000.0;
        time_testgen = time_testgen / 1000000;
        double time_testgen_seconds = time_testgen / 1000.0;


        JPanel summary = new JPanel();
        summary.setBackground(Color.WHITE);
        summary.setLayout(new GridBagLayout());

        JPanel results = MuAlloyGuiUtil.makeSummary(num_killed, num_not_killed, total_time, new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        JPanel summaryBar = MuAlloyGuiUtil.makeSummaryBarDisplay(width, num_killed, num_not_killed, total_time, new Font(FontName.get(), Font.PLAIN, FontSize.get()));

        GridBagConstraints barConstraint = new GridBagConstraints();
        barConstraint.fill = GridBagConstraints.HORIZONTAL;
        barConstraint.gridx = 0;
        barConstraint.gridy = 0;
        barConstraint.gridwidth = 4;
        barConstraint.insets = new Insets(5, 5, 5, 5);
        barConstraint.anchor = GridBagConstraints.CENTER;
        summary.add(summaryBar, barConstraint);

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = 2;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        summary.add(new JLabel(""), pushTopLeft);

        pushTopLeft.gridx = 1;
        pushTopLeft.gridy = 3;
        summary.add(new JLabel(""), pushTopLeft);

        pushTopLeft.gridx = 2;
        pushTopLeft.gridy = 3;
        summary.add(new JLabel(""), pushTopLeft);

        GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.gridx = 0;
        textConstraints.gridy = 0;
        textConstraints.fill = GridBagConstraints.HORIZONTAL;
        textConstraints.anchor = GridBagConstraints.NORTHWEST;
        textConstraints.weightx = 1.0;
        textConstraints.ipadx = 10;
        textConstraints.ipady = 5;
        mualloyText.add(summary, textConstraints);
        textConstraints.gridy = 1;
        mualloyText.add(results, textConstraints);
        textConstraints.gridy = 2;
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(coverageGray);
        textConstraints.insets = new Insets(6, 5, 5, 5);
        mualloyText.add(sep, textConstraints);
        textConstraints.gridy = 3;
        textConstraints.insets = new Insets(0, 5, 0, 5);

        JLabel title = new JLabel("<html><b>" + name + "</b> Mutation Testing Results:");
        title.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        mualloyText.add(title, textConstraints);

        JLabel mutant_score = new JLabel("<html><ul><li><b>Mutation Score:</b> " + num_killed + "/" + num_total + "</li><li><b>Time Generating Mutants:</b> " + time_gen_seconds + "s</li><li><b>Time Performing Mutation Testing:</b> " + time_mutation_seconds + "s </li></ul>");
        textConstraints.gridy = 4;
        mutant_score.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        mualloyText.add(mutant_score, textConstraints);

        if (num_killed == num_total) {
            JLabel results_cont = new JLabel("<html><b>All mutants were killed.</b> No new tests recommended.");
            textConstraints.gridy = 6;
            results_cont.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            mualloyText.add(results_cont, textConstraints);
        } else {
            JLabel results_cont = new JLabel("<html><b>" + num_not_killed + " mutants were not killed.</b> Below lists each non-killed mutant and a suggested mutant-killing test case.");
            textConstraints.gridy = 6;
            results_cont.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            mualloyText.add(results_cont, textConstraints);
        }
        mualloy_yloc = 7;
        if (!SaveTestSuite.get()) {
            File delete_creations = new File("test.als");
            delete_creations.delete();
        }
    }


    ArrayList<JTree> mualloyTrees = new ArrayList<JTree>();


    public void doDisplayMuAlloy(ArrayList<JTree> trees) {
        int yloc = mualloy_yloc;
        JPanel mualloyDetails = new JPanel();
        mualloyDetails.setLayout(new BoxLayout(mualloyDetails, BoxLayout.Y_AXIS));
        mualloyDetails.setBackground(Color.white);
        Border border = mualloyDetails.getBorder();
        Border margin = new EmptyBorder(0, 15, 0, 0);
        mualloyDetails.setBorder(new CompoundBorder(border, margin));

        for (JTree tree : trees) {
            JPanel displayTree = new JPanel();
            displayTree.setLayout(new BorderLayout());
            displayTree.setBorder(new EmptyBorder(2, 2, 2, 2));

            tree.setShowsRootHandles(true);
            tree.setRootVisible(true);

            MuAlloyTreeRender mualloyRender = new MuAlloyTreeRender();
            tree.setCellRenderer(mualloyRender);
            tree.addMouseListener(new MouseListener() {

                public final void mousePressed(MouseEvent e) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        if (selPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                            Object o = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
                            if (o instanceof MuAlloyTreeNode) {
                                MuAlloyTreeNode node = (MuAlloyTreeNode) o;
                                viz_mualloy.setMutantName(node.name);
                                viz_mualloy.setCommand(node.command);
                                viz_mualloy.setValuation(node.valuation);
                                doVisualize("MUT: " + node.mutant + "," + node.testcase);
                            }
                        }
                    }
                }

                public final void mouseClicked(MouseEvent e) {
                }

                public final void mouseReleased(MouseEvent e) {
                }

                public final void mouseEntered(MouseEvent e) {
                }

                public final void mouseExited(MouseEvent e) {
                }
            });

            tree.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            tree.setRowHeight(0);
            mualloyTrees.add(tree);
            displayTree.add(mualloyTrees.get(mualloyTrees.size() - 1));
            //displayTree.add(tree);
            displayTree.setBackground(Color.white);
            mualloyDetails.add(displayTree);
        }


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yloc;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy = yloc;
        mualloyText.add(mualloyDetails, c);

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = yloc + 1;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        mualloyText.add(new JLabel(" "), pushTopLeft);

        //Clear display and switch focus to the mutation results tab
        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(mualloyDisplay, BorderLayout.CENTER);

        logTab.deactivate();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.setActive();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();
    }

    ArrayList<JTree> alloyflTrees = new ArrayList<JTree>();

    public void doDisplayAlloyFL(ArrayList<JTree> trees) {
        int yloc = 0;

        JPanel alloyflDetails = new JPanel();
        alloyflDetails.setLayout(new BoxLayout(alloyflDetails, BoxLayout.Y_AXIS));
        alloyflDetails.setBackground(Color.white);
        Border border = alloyflDetails.getBorder();
        Border margin = new EmptyBorder(0, 15, 0, 0);
        alloyflDetails.setBorder(new CompoundBorder(border, margin));

        for (JTree tree : trees) {
            JPanel displayTree = new JPanel();
            displayTree.setLayout(new BorderLayout());
            displayTree.setBorder(new EmptyBorder(2, 2, 2, 2));

            tree.setShowsRootHandles(true);
            tree.setRootVisible(true);

            AlloyFLTreeRender alloyflRender = new AlloyFLTreeRender();
            tree.setCellRenderer(alloyflRender);

            tree.addMouseListener(new MouseListener() {

                public final void mousePressed(MouseEvent e) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        if (selPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                            Object o = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
                            if (o instanceof AlloyFLHighlightNode) {
                                AlloyFLHighlightNode node = (AlloyFLHighlightNode) o;
                                ArrayList<Pos> pos = new ArrayList<Pos>();
                                pos.add(node.pos);
                                ArrayList<String> color = new ArrayList<String>();
                                color.add(node.color);
                                doVisualizeHighlights(pos, color, false);
                            }
                        }
                    }
                }

                public final void mouseClicked(MouseEvent e) {
                }

                public final void mouseReleased(MouseEvent e) {
                }

                public final void mouseEntered(MouseEvent e) {
                }

                public final void mouseExited(MouseEvent e) {
                }
            });

            tree.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            tree.setRowHeight(0);
            alloyflTrees.add(tree);
            displayTree.add(tree);
            displayTree.setBackground(Color.white);
            alloyflDetails.add(displayTree);
        }

        GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.gridx = 0;
        textConstraints.gridy = yloc++;
        textConstraints.fill = GridBagConstraints.HORIZONTAL;
        textConstraints.anchor = GridBagConstraints.NORTHWEST;
        textConstraints.weightx = 1.0;
        textConstraints.ipadx = 10;
        textConstraints.ipady = 5;

        String summary_text_info = "<html>AlloyFL found <b>" + trees.size() + "</b> suspicious locations. These are displayed below in order from most to least suspicious.";
        if (SusFormulaPref.get().name().equals("DSTAR")) {
            summary_text_info += "<br>Due to the way the DStar suspiciousness formula works, highlight colors are based on a node's suspicious with respect to the other suspicious AST nodes.";
        }

        JLabel summary_text = new JLabel(summary_text_info);
        summary_text.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        alloyflText.add(summary_text, textConstraints);

        textConstraints.gridy = yloc++;

        if (trees.size() != 0) {
            JLabel highlight_all = new JLabel("<html><b><font color = 3E9FF4> Highlight all locations </html>");
            highlight_all.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            alloyflText.add(highlight_all, textConstraints);

            highlight_all.addMouseListener(new MouseListener() {

                public final void mousePressed(MouseEvent e) {
                    doVisualizeHighlights(latest_highlight_pos, latest_highlight_colors, false);
                }

                public final void mouseClicked(MouseEvent e) {
                }

                public final void mouseReleased(MouseEvent e) {
                }

                public final void mouseEntered(MouseEvent e) {
                }

                public final void mouseExited(MouseEvent e) {
                }
            });
        } else {
            JLabel highlight_all = new JLabel("<html>There are <b>no</b> locations to highlight.");
            highlight_all.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            alloyflText.add(highlight_all, textConstraints);
        }



        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yloc;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 1;


        c.gridy = yloc++;
        alloyflText.add(alloyflDetails, c);

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = yloc++;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        alloyflText.add(new JLabel(" "), pushTopLeft);

        //Clear display and switch focus to the mutation results tab
        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(alloyflDisplay, BorderLayout.CENTER);

        logTab.deactivate();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.setActive();
        arepairTab.deactivate();
        resultsPanel.updateUI();
    }

    String patchedModel;

    public void doDisplayARepair(String fixedModel, ArrayList<JTree> trees, ArrayList<DefaultMutableTreeNode> cmdNodes) {
        int yloc = 0;
        JPanel arepairDetails = new JPanel();
        GridBagConstraints repair_c = new GridBagConstraints();
        repair_c.gridx = 0;
        repair_c.gridy = yloc;
        repair_c.fill = GridBagConstraints.HORIZONTAL;
        repair_c.anchor = GridBagConstraints.NORTHWEST;
        repair_c.weightx = 1;
        repair_c.weighty = 1;
        GridBagLayout reapirGridbag = new GridBagLayout();

        arepairDetails.setLayout(reapirGridbag);
        arepairDetails.setBackground(Color.white);
        Border border = arepairDetails.getBorder();
        Border margin = new EmptyBorder(0, 15, 0, 0);
        arepairDetails.setBorder(new CompoundBorder(border, margin));

        // OurSyntaxWidget val_text = new OurSyntaxWidget(true, "", OurUtil.getVizFont().getName(), OurUtil.getVizFont().getSize(), 4, null, null);

        OurTabbedSyntaxWidget val_text = new OurTabbedSyntaxWidget(OurUtil.getVizFont().getName(), OurUtil.getVizFont().getSize(), TabSize.get());
        val_text.enableSyntax(!SyntaxDisabled.get());
        fixedModel = fixedModel.trim();
        String[] patch = fixedModel.split("Diff");
        fixedModel = "";
        ArrayList<Pos> inserts = new ArrayList<Pos>();
        ArrayList<Pos> deletes = new ArrayList<Pos>();
        int line_num = 0;

        for (int i = 1; i < patch.length; i++) {
            String[] temp = patch[i].split(",");
            String type = temp[0].substring(1); // remove (
            String diff = temp[1].substring(0, temp[1].length() - 1); // remove )
            diff = diff.substring(1, diff.length() - 1); //remove " " 

            String[] count = diff.split("\\P{Print}");
            if (count.length != 1)
                line_num += count.length;

            if (type.equals("EQUAL")) {

            } else if (type.equals("INSERT")) {
                diff = "[insert: " + diff + "]";
                String[] insert = fixedModel.split("\n");
                if (count.length == 1)
                    inserts.add(new Pos(val_text.get().getFilename(), insert[insert.length - 1].length() + 1, line_num, insert[insert.length - 1].length() + diff.length(), line_num));
                else
                    inserts.add(new Pos(val_text.get().getFilename(), insert[insert.length - 1].length() + 1, line_num - (count.length), count[count.length - 1].length(), line_num));

            } else { //Type equals delete
                diff = "[delete: " + diff + "]";
                String[] delete = fixedModel.split("\n");
                deletes.add(new Pos(val_text.get().getFilename(), delete[delete.length - 1].length() + 1, line_num, delete[delete.length - 1].length() + diff.length(), line_num));
                if (count.length == 1)
                    inserts.add(new Pos(val_text.get().getFilename(), delete[delete.length - 1].length() + 1, line_num, delete[delete.length - 1].length() + diff.length(), line_num));
                else
                    inserts.add(new Pos(val_text.get().getFilename(), delete[delete.length - 1].length() + 1, line_num - (count.length), count[count.length - 1].length(), line_num));

            }
            fixedModel += diff.replaceAll("\\P{Print}", "\n");
        }
        val_text.clearShade();
        val_text.get().setText(fixedModel);
        String[] line_length = fixedModel.split("\n");
        val_text.shade(inserts, covGreen, false);
        val_text.shade(deletes, covRed, false);
        val_text.get().setUnEditable();
        JPanel val_holder = new JPanel();
        val_holder.setLayout(new BorderLayout());
        val_text.addTo(val_holder, BorderLayout.CENTER);

        JLabel repair_label = new JLabel("<html><b>Fully fixed model can be viewed here:</b> ");
        //JLabel repair_label = new JLabel("<html><b>Fully fixed model can be viewed here (and is stored locally in fix.als):</b> ");
        arepairDetails.add(repair_label, repair_c);
        repair_c.gridy = ++yloc;

        patchedModel = FileUtil.readText("fix.als");
        patchedModel = patchedModel.trim();
        if (!SavePatch.get()) {
            File delete_creations = new File("fix.als");
            delete_creations.delete();
        }

        DefaultMutableTreeNode patchModelNode = new DefaultMutableTreeNode(new ARepairTreeNode("fix.als"));
        JTree repairTree = new JTree(patchModelNode);

        JPanel repairDisplayTree = new JPanel();
        repairDisplayTree.setLayout(new BorderLayout());
        repairDisplayTree.setBorder(new EmptyBorder(2, 2, 2, 2));

        repairTree.setShowsRootHandles(true);
        repairTree.setRootVisible(true);

        ARepairTreeRender arepairRender = new ARepairTreeRender();
        repairTree.setCellRenderer(arepairRender);
        repairTree.addMouseListener(new MouseListener() {

            public final void mousePressed(MouseEvent e) {
                TreePath selPath = repairTree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    if (selPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                        Object o = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
                        if (o instanceof ARepairTreeNode) {
                            ARepairTreeNode node = (ARepairTreeNode) o;
                            doVisualize("REP: " + node.model_path);
                        }
                    }
                }
            }

            public final void mouseClicked(MouseEvent e) {
            }

            public final void mouseReleased(MouseEvent e) {
            }

            public final void mouseEntered(MouseEvent e) {
            }

            public final void mouseExited(MouseEvent e) {
            }
        });

        repairTree.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
        repairTree.setRowHeight(0);
        repairDisplayTree.add(repairTree);
        repairDisplayTree.setBackground(Color.white);
        arepairDetails.add(repairDisplayTree, repair_c);
        repair_c.gridy = ++yloc;

        repair_label = new JLabel("<html><b>Diff of Patched Paragraphs:</b> ");
        arepairDetails.add(repair_label, repair_c);
        repair_c.gridy = ++yloc;
        arepairDetails.add(val_holder, repair_c);
        repair_c.gridy = ++yloc;
        JLabel test_label = new JLabel("<html><b><br>Previously Failing Tests That Now Pass:</b> ");
        arepairDetails.add(test_label, repair_c);

        JPanel testDetails = new JPanel();
        testDetails.setLayout(new BoxLayout(testDetails, BoxLayout.Y_AXIS));
        testDetails.setBackground(Color.white);
        border = testDetails.getBorder();
        margin = new EmptyBorder(0, 15, 0, 0);
        testDetails.setBorder(new CompoundBorder(border, margin));

        for (JTree tree : trees) {
            JPanel displayTree = new JPanel();
            displayTree.setLayout(new BorderLayout());
            displayTree.setBorder(new EmptyBorder(2, 2, 2, 2));

            tree.setShowsRootHandles(true);
            tree.setRootVisible(true);

            PassFailRenderer passFailRenderer = new PassFailRenderer();
            tree.setCellRenderer(passFailRenderer);
            tree.addMouseListener(new MouseListener() {

                public final void mousePressed(MouseEvent e) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        if (selPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                            Object o = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
                            if (o instanceof AUnitTreeNode) {
                                AUnitTreeNode node = (AUnitTreeNode) o;
                                if (node.linkDestination != null) {
                                    viz_val.setTestName(node.test_name);
                                    doVisualize("VAL: " + node.linkDestination);
                                }
                            }
                        }
                    }
                }

                public final void mouseClicked(MouseEvent e) {
                }

                public final void mouseReleased(MouseEvent e) {
                }

                public final void mouseEntered(MouseEvent e) {
                }

                public final void mouseExited(MouseEvent e) {
                }
            });

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();

            for (DefaultMutableTreeNode node : cmdNodes) {
                tree.expandPath(new TreePath(node.getPath()));
            }

            if (root.getUserObject() instanceof PassFailTreeNode) {
                PassFailTreeNode passFail = (PassFailTreeNode) root.getUserObject();
                if (passFail.result.equals("pass")) {
                    tree.collapseRow(0);
                }
            }

            tree.setFont(new Font(FontName.get(), Font.PLAIN, FontSize.get()));
            tree.setRowHeight(0);
            arepairTrees.add(tree);
            displayTree.add(arepairTrees.get(arepairTrees.size() - 1));
            displayTree.setBackground(Color.white);
            testDetails.add(displayTree);
        }

        repair_c.gridy = ++yloc;
        arepairDetails.add(testDetails, repair_c);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = yloc;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 1;
        arepairText.add(arepairDetails, c);

        GridBagConstraints pushTopLeft = new GridBagConstraints();
        pushTopLeft.gridx = 0;
        pushTopLeft.gridy = yloc + 1;
        pushTopLeft.weightx = 1;
        pushTopLeft.weighty = 1;
        arepairText.add(new JLabel(" "), pushTopLeft);

        //Clear display and switch focus to the mutation results tab
        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(arepairDisplay, BorderLayout.CENTER);

        logTab.deactivate();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.setActive();
        resultsPanel.updateUI();
    }

    ArrayList<JTree> arepairTrees = new ArrayList<JTree>();

    /** Sullivan **/
    /** This method toggles the "calcluate coverage" checkbox. */
    private Runner doOptCoverageDisplay() {
        if (!wrap)
            CoverageDisplay.set(CoverageDisplay.get() < covDisplayLevel ? covDisplayLevel : 0);
        return wrapMe();
    }

    private Runner doOptHighlightCoverage() {
        if (!wrap)
            HighlightCoverage.set(HighlightCoverage.get() < highlightLevel ? highlightLevel : 0);
        return wrapMe();
    }

    /** This method runs all tests **/
    private Runner doAUnitExecute() {
        if (wrap)
            return wrapMe();
        doRefreshAUnit();
        // OurUtil.enableAll(runmenu);
        text.clearShade();
        log.clearError(); // To clear any residual error message
        //Get current commands

        List<Test> cp = tests;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromStringTests(text.get().getText());
            } catch (Err e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            tests = cp;
        }
        text.clearShade();
        log.clearError(); // To clear any residual error message

        return doRunAUnit();
    }

    /** This method executes a particular RUN or CHECK command. */
    private Runner doRunAUnit() {

        if (WorkerEngine.isBusy())
            return null;

        latestAutoInstance = "";
        // To update the accelerator to point to the command actually chosen
        doRefreshAUnit();
        OurUtil.enableAll(runmenu);
        aunitText.removeAll();
        aunitText.updateUI();
        coverageText.removeAll();
        coverageText.updateUI();
        mualloyText.removeAll();
        mualloyText.updateUI();
        alloyflText.removeAll();
        alloyflText.updateUI();
        arepairText.removeAll();
        arepairText.updateUI();

        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(logpane, BorderLayout.CENTER);
        aunitTab.deactivate();
        logTab.setActive();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();

        if (tests == null)
            return null;
        if (tests.size() == 0) {
            resultsPanel.removeAll();
            resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
            resultsPanel.add(logpane, BorderLayout.CENTER);
            aunitTab.deactivate();
            logTab.setActive();
            coverageTab.deactivate();
            mualloyTab.deactivate();
            alloyflTab.deactivate();
            arepairTab.deactivate();
            resultsPanel.updateUI();
            log.logRed("There are no tests to execute.\n\n");
            return null;
        } else {
            HashSet<String> test_names = new HashSet<String>();
            for (Test test : tests) {
                if (!test_names.add(test.id)) {
                    resultsPanel.removeAll();
                    resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                    resultsPanel.add(logpane, BorderLayout.CENTER);
                    aunitTab.deactivate();
                    logTab.setActive();
                    coverageTab.deactivate();
                    mualloyTab.deactivate();
                    alloyflTab.deactivate();
                    arepairTab.deactivate();
                    resultsPanel.updateUI();
                    text.clearShade();
                    text.shade(new Pos(text.get().getFilename(), test.getTestCmd().pos.x, test.getTestCmd().pos.y, test.getTestCmd().pos.x + test.id.length(), test.getTestCmd().pos.y));
                    log.logRed("Test labels must be unique.\n\n");
                    return null;
                }
            }
        }

        SimpleReporter.SimpleCallback1 cb = new SimpleReporter.SimpleCallback1(this, null, log, VerbosityPref.get().ordinal(), latestAlloyVersionName, latestAlloyVersion);
        AunitExecutionTask task = new AunitExecutionTask();

        A4Options opt = new A4Options();
        opt.tempDirectory = alloyHome() + fs + "tmp";
        opt.solverDirectory = alloyHome() + fs + "binary";
        opt.recordKodkod = RecordKodkod.get();
        opt.noOverflow = NoOverflow.get();
        opt.unrolls = Version.experimental ? Unrolls.get() : (-1);
        opt.skolemDepth = SkolemDepth.get();
        opt.coreMinimization = CoreMinimization.get();
        opt.coreGranularity = CoreGranularity.get();
        opt.originalFilename = Util.canon(text.get().getFilename());
        opt.solver = Solver.get();
        task.bundleWarningNonFatal = WarningNonfatal.get();
        task.map = text.takeSnapshot();
        task.options = opt.dup();
        task.resolutionMode = (Version.experimental && ImplicitThis.get()) ? 2 : 1;
        task.tempdir = maketemp();
        if (CoverageDisplay.get() > 0) {
            task.coverage = false;
        } else {
            task.coverage = true;
        }
        if (HighlightCoverage.get() > 0) {
            task.highlight = false;
        } else {
            task.highlight = true;
        }
        text.clearShade();
        log.clearError(); // To clear any residual error message
        try {
            aunitbutton.setVisible(false);
            showbutton.setEnabled(false);
            aunitStop.setVisible(true);
            int newmem = SubMemory.get(), newstack = SubStack.get();
            if (newmem != subMemoryNow || newstack != subStackNow)
                WorkerEngine.stop();
            if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                WorkerEngine.runLocally(task, cb);
            else
                WorkerEngine.run(task, newmem, newstack, alloyHome() + fs + "binary", "", cb);
            subMemoryNow = newmem;
            subStackNow = newstack;
        } catch (Throwable ex) {
            WorkerEngine.stop();
            log.logBold("Fatal Error: Solver failed due to unknown reason.\n" + "One possible cause is that, in the Options menu, your specified\n" + "memory size is larger than the amount allowed by your OS.\n" + "Also, please make sure \"java\" is in your program path.\n");
            log.logDivider();
            log.flush();
            doStop(2);
        }
        return null;
    }

    /** This method runs all tests **/
    private Runner doMuAlloyExecute() {
        if (wrap)
            return wrapMe();
        doRefreshMuAlloy();
        text.clearShade();
        log.clearError(); // To clear any residual error message

        List<Test> cp = tests;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromStringTests(text.get().getText());
            } catch (Err e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            tests = cp;
        }
        text.clearShade();
        log.clearError(); // To clear any residual error message

        return doRunMuAlloy();
    }

    /** This method refreshes the "aunit" menu. */
    private Runner doRefreshMuAlloy() {
        if (wrap)
            return wrapMe();
        KeyStroke ac = KeyStroke.getKeyStroke(VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        try {
            wrap = true;
            mualloyMenu.removeAll();
            menuItem(mualloyMenu, "Execute Mutation Testing", doMuAlloyExecute());
            addToMenu(arepairMenu, SaveTestSuite);

        } finally {
            wrap = false;
        }
        List<Command> cp = commands;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromString(text.get().getText());
            } catch (Err e) {
                commands = null;
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                commands = null;

                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            commands = cp;
        }
        return null;
    }

    /** This method executes mutation testing for Alloy. */
    private Runner doRunMuAlloy() {

        if (WorkerEngine.isBusy())
            return null;

        latestAutoInstance = "";
        // To update the accelerator to point to the command actually chosen
        doRefreshMuAlloy();
        OurUtil.enableAll(runmenu);
        aunitText.removeAll();
        aunitText.updateUI();
        coverageText.removeAll();
        coverageText.updateUI();
        mualloyText.removeAll();
        mualloyText.updateUI();
        alloyflText.removeAll();
        alloyflText.updateUI();
        arepairText.removeAll();
        arepairText.updateUI();

        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(logpane, BorderLayout.CENTER);

        logTab.setActive();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();

        if (tests == null)
            return null;
        if (tests.size() == 0) {
            resultsPanel.removeAll();
            resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
            resultsPanel.add(logpane, BorderLayout.CENTER);
            aunitTab.deactivate();
            logTab.setActive();
            coverageTab.deactivate();
            mualloyTab.deactivate();
            alloyflTab.deactivate();
            arepairTab.deactivate();
            resultsPanel.updateUI();
            log.logRed("There are no tests to utilize for mutation testing.\n\n");
            return null;
        } else {
            HashSet<String> test_names = new HashSet<String>();
            for (Test test : tests) {
                if (!test_names.add(test.id)) {
                    resultsPanel.removeAll();
                    resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                    resultsPanel.add(logpane, BorderLayout.CENTER);
                    aunitTab.deactivate();
                    logTab.setActive();
                    coverageTab.deactivate();
                    mualloyTab.deactivate();
                    alloyflTab.deactivate();
                    arepairTab.deactivate();
                    resultsPanel.updateUI();
                    text.clearShade();
                    text.shade(new Pos(text.get().getFilename(), test.getTestCmd().pos.x, test.getTestCmd().pos.y, test.getTestCmd().pos.x + test.id.length(), test.getTestCmd().pos.y));
                    log.logRed("Test labels must be unique.\n\n");
                    return null;
                }
            }
        }

        SimpleReporter.SimpleCallback1 cb = new SimpleReporter.SimpleCallback1(this, null, log, VerbosityPref.get().ordinal(), latestAlloyVersionName, latestAlloyVersion);
        MuAlloyExecutionTask task = new MuAlloyExecutionTask();

        A4Options opt = new A4Options();
        opt.tempDirectory = alloyHome() + fs + "tmp";
        opt.solverDirectory = alloyHome() + fs + "binary";
        opt.recordKodkod = RecordKodkod.get();
        opt.noOverflow = NoOverflow.get();
        opt.unrolls = Version.experimental ? Unrolls.get() : (-1);
        opt.skolemDepth = SkolemDepth.get();
        opt.coreMinimization = CoreMinimization.get();
        opt.coreGranularity = CoreGranularity.get();
        opt.originalFilename = Util.canon(text.get().getFilename());
        opt.solver = Solver.get();
        task.bundleWarningNonFatal = WarningNonfatal.get();
        task.map = text.takeSnapshot();
        task.options = opt.dup();
        task.resolutionMode = (Version.experimental && ImplicitThis.get()) ? 2 : 1;
        task.tempdir = maketemp();

        text.clearShade();
        log.clearError(); // To clear any residual error message
        try {
            int newmem = SubMemory.get(), newstack = SubStack.get();
            if (newmem != subMemoryNow || newstack != subStackNow)
                WorkerEngine.stop();
            if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                WorkerEngine.runLocally(task, cb);
            else
                WorkerEngine.run(task, newmem, newstack, alloyHome() + fs + "binary", "", cb);
            subMemoryNow = newmem;
            subStackNow = newstack;
        } catch (Throwable ex) {
            WorkerEngine.stop();
            log.logBold("Fatal Error: Solver failed due to unknown reason.\n" + "One possible cause is that, in the Options menu, your specified\n" + "memory size is larger than the amount allowed by your OS.\n" + "Also, please make sure \"java\" is in your program path.\n");
            log.logDivider();
            log.flush();
            doStop(2);
        }
        return null;
    }

    /** This method runs all tests **/
    private Runner doAlloyFLExecute() {
        if (wrap)
            return wrapMe();
        doRefreshAlloyFL();
        text.clearShade();
        log.clearError(); // To clear any residual error message

        List<Test> cp = tests;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromStringTests(text.get().getText());
            } catch (Err e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            tests = cp;
        }
        text.clearShade();
        log.clearError(); // To clear any residual error message

        return doRunAlloyFL();
    }

    /** This method refreshes the "aunit" menu. */
    private Runner doRefreshAlloyFL() {
        if (wrap)
            return wrapMe();
        KeyStroke ac = KeyStroke.getKeyStroke(VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        try {
            wrap = true;
            alloyFLMenu.removeAll();
            menuItem(alloyFLMenu, "Execute AlloyFL", doAlloyFLExecute());
            alloyFLMenu.add(new JSeparator());
            addToMenu(alloyFLMenu, FLWeight);
            addToMenu(alloyFLMenu, SusFormulaPref);
        } finally {
            wrap = false;
        }
        List<Command> cp = commands;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromString(text.get().getText());
            } catch (Err e) {
                commands = null;
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                commands = null;

                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            commands = cp;
        }
        return null;
    }

    /** This method executes a particular RUN or CHECK command. */
    private Runner doRunAlloyFL() {

        if (WorkerEngine.isBusy())
            return null;

        latestAutoInstance = "";
        // To update the accelerator to point to the command actually chosen
        doRefreshAlloyFL();
        OurUtil.enableAll(runmenu);

        aunitText.removeAll();
        aunitText.updateUI();
        coverageText.removeAll();
        coverageText.updateUI();
        mualloyText.removeAll();
        mualloyText.updateUI();
        alloyflText.removeAll();
        alloyflText.updateUI();
        arepairText.removeAll();
        arepairText.updateUI();

        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(logpane, BorderLayout.CENTER);

        logTab.setActive();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();

        if (tests == null)
            return null;
        if (tests.size() == 0) {
            resultsPanel.removeAll();
            resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
            resultsPanel.add(logpane, BorderLayout.CENTER);
            aunitTab.deactivate();
            logTab.setActive();
            coverageTab.deactivate();
            mualloyTab.deactivate();
            alloyflTab.deactivate();
            arepairTab.deactivate();
            resultsPanel.updateUI();
            log.logRed("There are no tests to execute.\n\n");
            return null;
        } else {
            HashSet<String> test_names = new HashSet<String>();
            for (Test test : tests) {
                if (!test_names.add(test.id)) {
                    resultsPanel.removeAll();
                    resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                    resultsPanel.add(logpane, BorderLayout.CENTER);
                    aunitTab.deactivate();
                    logTab.setActive();
                    coverageTab.deactivate();
                    mualloyTab.deactivate();
                    alloyflTab.deactivate();
                    arepairTab.deactivate();
                    resultsPanel.updateUI();
                    text.clearShade();
                    text.shade(new Pos(text.get().getFilename(), test.getTestCmd().pos.x, test.getTestCmd().pos.y, test.getTestCmd().pos.x + test.id.length(), test.getTestCmd().pos.y));
                    log.logRed("Test labels must be unique.\n\n");
                    return null;
                }
            }
        }

        SimpleReporter.SimpleCallback1 cb = new SimpleReporter.SimpleCallback1(this, null, log, VerbosityPref.get().ordinal(), latestAlloyVersionName, latestAlloyVersion);
        AlloyFLExecutionTask task = new AlloyFLExecutionTask();

        A4Options opt = new A4Options();
        opt.tempDirectory = alloyHome() + fs + "tmp";
        opt.solverDirectory = alloyHome() + fs + "binary";
        opt.recordKodkod = RecordKodkod.get();
        opt.noOverflow = NoOverflow.get();
        opt.unrolls = Version.experimental ? Unrolls.get() : (-1);
        opt.skolemDepth = SkolemDepth.get();
        opt.coreMinimization = CoreMinimization.get();
        opt.coreGranularity = CoreGranularity.get();
        opt.originalFilename = Util.canon(text.get().getFilename());
        opt.solver = Solver.get();
        task.bundleWarningNonFatal = WarningNonfatal.get();
        task.map = text.takeSnapshot();
        task.options = opt.dup();
        task.resolutionMode = (Version.experimental && ImplicitThis.get()) ? 2 : 1;
        task.tempdir = maketemp();
        task.susformula = SusFormulaPref.get();
        task.flweight = FLWeight.get();

        text.clearShade();
        log.clearError(); // To clear any residual error message
        try {
            int newmem = SubMemory.get(), newstack = SubStack.get();
            if (newmem != subMemoryNow || newstack != subStackNow)
                WorkerEngine.stop();
            if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                WorkerEngine.runLocally(task, cb);
            else
                WorkerEngine.run(task, newmem, newstack, alloyHome() + fs + "binary", "", cb);
            subMemoryNow = newmem;
            subStackNow = newstack;
        } catch (Throwable ex) {
            WorkerEngine.stop();
            log.logBold("Fatal Error: Solver failed due to unknown reason.\n" + "One possible cause is that, in the Options menu, your specified\n" + "memory size is larger than the amount allowed by your OS.\n" + "Also, please make sure \"java\" is in your program path.\n");
            log.logDivider();
            log.flush();
            doStop(2);
        }
        return null;
    }

    private Runner doOptRepairDisplay() {
        if (!wrap)
            RepairDisplay.set(RepairDisplay.get() < repairDisplayLevel ? repairDisplayLevel : 0);
        return wrapMe();
    }

    /** Sullivan **/
    /** This method refreshes the "arepair" menu. */
    private Runner doRefreshARepair() {
        if (wrap)
            return wrapMe();
        KeyStroke ac = KeyStroke.getKeyStroke(VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        try {
            wrap = true;
            arepairMenu.removeAll();
            menuItem(arepairMenu, "Execute ARepair", doARepairExecute());
            arepairMenu.add(new JSeparator());
            addToMenu(arepairMenu, SearchStratPref);
            addToMenu(arepairMenu, MaxTriesPerHole);
            addToMenu(arepairMenu, NumberOfPartitions);
            addToMenu(arepairMenu, MaxTriesPerDepth);
            addToMenu(arepairMenu, SavePatch);
        } finally {
            wrap = false;
        }
        List<Command> cp = commands;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromString(text.get().getText());
            } catch (Err e) {
                commands = null;
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                commands = null;

                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            commands = cp;
        }
        return null;
    }

    /** This method executes a particular RUN or CHECK command. */
    private Runner doRunARepair() {

        if (WorkerEngine.isBusy())
            return null;

        latestAutoInstance = "";
        // To update the accelerator to point to the command actually chosen
        doRefreshAlloyFL();
        OurUtil.enableAll(runmenu);

        aunitText.removeAll();
        aunitText.updateUI();
        coverageText.removeAll();
        coverageText.updateUI();
        mualloyText.removeAll();
        mualloyText.updateUI();
        alloyflText.removeAll();
        alloyflText.updateUI();
        arepairText.removeAll();
        arepairText.updateUI();

        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(logpane, BorderLayout.CENTER);

        logTab.setActive();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();
        resultsPanel.updateUI();

        if (tests == null)
            return null;
        if (tests.size() == 0) {
            resultsPanel.removeAll();
            resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
            resultsPanel.add(logpane, BorderLayout.CENTER);
            aunitTab.deactivate();
            logTab.setActive();
            coverageTab.deactivate();
            mualloyTab.deactivate();
            alloyflTab.deactivate();
            arepairTab.deactivate();
            resultsPanel.updateUI();
            log.logRed("There are no tests to execute.\n\n");
            return null;
        } else {
            HashSet<String> test_names = new HashSet<String>();
            for (Test test : tests) {
                if (!test_names.add(test.id)) {
                    resultsPanel.removeAll();
                    resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                    resultsPanel.add(logpane, BorderLayout.CENTER);
                    aunitTab.deactivate();
                    logTab.setActive();
                    coverageTab.deactivate();
                    mualloyTab.deactivate();
                    alloyflTab.deactivate();
                    arepairTab.deactivate();
                    resultsPanel.updateUI();
                    text.clearShade();
                    text.shade(new Pos(text.get().getFilename(), test.getTestCmd().pos.x, test.getTestCmd().pos.y, test.getTestCmd().pos.x + test.id.length(), test.getTestCmd().pos.y));
                    log.logRed("Test labels must be unique.\n\n");
                    return null;
                }
            }
        }

        SimpleReporter.SimpleCallback1 cb = new SimpleReporter.SimpleCallback1(this, null, log, VerbosityPref.get().ordinal(), latestAlloyVersionName, latestAlloyVersion);
        ARepairExecutionTask task = new ARepairExecutionTask();

        A4Options opt = new A4Options();
        opt.tempDirectory = alloyHome() + fs + "tmp";
        opt.solverDirectory = alloyHome() + fs + "binary";
        opt.recordKodkod = RecordKodkod.get();
        opt.noOverflow = NoOverflow.get();
        opt.unrolls = Version.experimental ? Unrolls.get() : (-1);
        opt.skolemDepth = SkolemDepth.get();
        opt.coreMinimization = CoreMinimization.get();
        opt.coreGranularity = CoreGranularity.get();
        opt.originalFilename = Util.canon(text.get().getFilename());
        opt.solver = Solver.get();
        task.bundleWarningNonFatal = WarningNonfatal.get();
        task.map = text.takeSnapshot();
        task.options = opt.dup();
        task.resolutionMode = (Version.experimental && ImplicitThis.get()) ? 2 : 1;
        task.tempdir = maketemp();
        if (RepairDisplay.get() > 0) {
            task.searchstrat = false;
        } else {
            task.searchstrat = true;
        }
        task.maxtriesperhole = MaxTriesPerHole.get();
        task.maxtriesperdepth = MaxTriesPerDepth.get();
        task.numpartitions = NumberOfPartitions.get();

        text.clearShade();
        log.clearError(); // To clear any residual error message
        try {
            int newmem = SubMemory.get(), newstack = SubStack.get();
            if (newmem != subMemoryNow || newstack != subStackNow)
                WorkerEngine.stop();
            if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                WorkerEngine.runLocally(task, cb);
            else
                WorkerEngine.run(task, newmem, newstack, alloyHome() + fs + "binary", "", cb);
            subMemoryNow = newmem;
            subStackNow = newstack;
        } catch (Throwable ex) {
            WorkerEngine.stop();
            log.logBold("Fatal Error: Solver failed due to unknown reason.\n" + "One possible cause is that, in the Options menu, your specified\n" + "memory size is larger than the amount allowed by your OS.\n" + "Also, please make sure \"java\" is in your program path.\n");
            log.logDivider();
            log.flush();
            doStop(2);
        }
        return null;
    }

    /** This method runs all tests **/
    private Runner doARepairExecute() {
        if (wrap)
            return wrapMe();
        doRefreshARepair();
        text.clearShade();
        log.clearError(); // To clear any residual error message

        List<Test> cp = tests;
        if (cp == null) {
            try {
                cp = CompUtil.parseOneModule_fromStringTests(text.get().getText());
            } catch (Err e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                text.shade(new Pos(text.get().getFilename(), e.pos.x, e.pos.y, e.pos.x2, e.pos.y2));
                if ("yes".equals(System.getProperty("debug")) && VerbosityPref.get() == Verbosity.FULLDEBUG)
                    log.logRed("Fatal Exception!" + e.dump() + "\n\n");
                else
                    log.logRed(e.toString() + "\n\n");
                return null;
            } catch (Throwable e) {
                tests = null;
                runmenu.getItem(0).setEnabled(false);
                runmenu.getItem(3).setEnabled(false);
                log.logRed("Cannot parse the model.\n" + e.toString() + "\n\n");
                return null;
            }
            tests = cp;
        }
        text.clearShade();
        log.clearError(); // To clear any residual error message

        return doRunARepair();
    }

    /**
     * This method stops the current run or check (how==0 means DONE, how==1 means
     * FAIL, how==2 means STOP).
     */
    public Runner doARepairStop(Integer how) {
        if (wrap)
            return wrapMe(how);
        int h = how;
        if (h != 0) {
            if (h == 2 && WorkerEngine.isBusy()) {
                WorkerEngine.stop();
                log.logBold("\nSolving Stopped.\n");
                log.logDivider();
            }
            WorkerEngine.stop();
        }
        runmenu.setEnabled(true);
        runbutton.setVisible(true);
        showbutton.setEnabled(true);
        stopbutton.setVisible(false);
        aunitbutton.setVisible(true);
        aunitStop.setVisible(false);
        mualloy.setVisible(true);
        alloyFL.setVisible(true);
        arepairbutton.setVisible(true);
        arepairStop.setVisible(false);
        if (latestAutoInstance.length() > 0) {
            String f = latestAutoInstance;
            latestAutoInstance = "";
            if (subrunningTask == 2)
                viz.loadXML(f, true);
            else if (AutoVisualize.get() || subrunningTask == 1)
                doVisualize("XML: " + f);
        }
        return null;
    }


    public void doPrepToDisplayError() {
        resultsPanel.removeAll();

        logTab.setActive();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();

        aunitText.removeAll();
        coverageText.removeAll();
        mualloyText.removeAll();
        alloyflText.removeAll();
        arepairText.removeAll();

        resultsPanel.removeAll();
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
        resultsPanel.add(logpane, BorderLayout.CENTER);
        resultsPanel.updateUI();
    }

    public void doHighlightError(Pos pos) {
        text.clearShade();
        text.shade(new Pos(text.get().getFilename(), pos.x, pos.y, pos.x2, pos.y2));
    }

    /** This method displays the parse tree. */
    private Runner doShowParseTree() {
        if (wrap)
            return wrapMe();
        doRefreshRun();
        OurUtil.enableAll(runmenu);
        if (commands != null) {
            Module world = null;
            try {
                int resolutionMode = (Version.experimental && ImplicitThis.get()) ? 2 : 1;
                A4Options opt = new A4Options();
                opt.tempDirectory = alloyHome() + fs + "tmp";
                opt.solverDirectory = alloyHome() + fs + "binary";
                opt.originalFilename = Util.canon(text.get().getFilename());
                world = CompUtil.parseEverything_fromFile(A4Reporter.NOP, text.takeSnapshot(), opt.originalFilename, resolutionMode);
            } catch (Err er) {
                text.shade(er.pos);
                log.logRed(er.toString() + "\n\n");
                return null;
            }
            world.showAsTree(this);
        }
        return null;
    }

    /** This method displays the meta model. */
    private Runner doShowMetaModel() {
        if (wrap)
            return wrapMe();
        doRefreshRun();
        OurUtil.enableAll(runmenu);
        if (commands != null)
            doRun(-2);
        return null;
    }

    /** This method displays the latest instance. */
    private Runner doShowLatest() {
        if (wrap)
            return wrapMe();
        if (latestInstance.length() == 0)
            log.logRed("No previous instances are available for viewing.\n\n");
        else
            doVisualize("XML: " + latestInstance);
        return null;
    }

    /**
     * This method happens when the user tries to load the evaluator from the main
     * GUI.
     */
    private Runner doLoadEvaluator() {
        if (wrap)
            return wrapMe();
        log.logRed("Note: the evaluator is now in the visualizer.\n" + "Just click the \"Evaluator\" toolbar button\n" + "when an instance is shown in the visualizer.\n");
        log.flush();
        return null;
    }

    // ===============================================================================================================//

    /**
     * This method refreshes the "Window" menu for either the SimpleGUI window
     * (isViz==false) or the VizGUI window (isViz==true).
     */
    private Runner doRefreshWindow(Boolean isViz) {
        if (wrap)
            return wrapMe(isViz);
        try {
            wrap = true;
            JMenu w = (isViz ? windowmenu2 : windowmenu);
            w.removeAll();
            if (isViz) {
                viz.addMinMaxActions(w);
            } else {
                menuItem(w, "Minimize", 'M', doMinimize(), iconNo);
                menuItem(w, "Zoom", doZoom(), iconNo);
            }
            w.addSeparator();
            int i = 0;
            for (String f : text.getFilenames()) {
                JMenuItem it = new JMenuItem("Model: " + slightlyShorterFilename(f) + (text.modified(i) ? " *" : ""), null);
                it.setIcon((f.equals(text.get().getFilename()) && !isViz) ? iconYes : iconNo);
                it.addActionListener(f.equals(text.get().getFilename()) ? doShow() : doOpenFile(f));
                w.add(it);
                i++;
            }
            if (viz != null)
                for (String f : viz.getInstances()) {
                    JMenuItem it = new JMenuItem("Instance: " + viz.getInstanceTitle(f), null);
                    it.setIcon((isViz && f.equals(viz.getXMLfilename())) ? iconYes : iconNo);
                    it.addActionListener(doVisualize("XML: " + f));
                    w.add(it);
                }
        } finally {
            wrap = false;
        }
        return null;
    }

    /** This method minimizes the window. */
    private Runner doMinimize() {
        if (wrap)
            return wrapMe();
        else {
            OurUtil.minimize(frame);
            return null;
        }
    }

    /**
     * This method alternatingly maximizes or restores the window.
     */
    private Runner doZoom() {
        if (wrap)
            return wrapMe();
        else {
            OurUtil.zoom(frame);
            return null;
        }
    }

    /** This method bring this window to the foreground. */
    private Runner doShow() {
        if (wrap)
            return wrapMe();
        OurUtil.show(frame);
        text.get().requestFocusInWindow();
        return null;
    }

    // ===============================================================================================================//

    /** This method refreshes the "Option" menu. */
    private Runner doRefreshOption() {
        if (wrap)
            return wrapMe();
        try {
            wrap = true;
            optmenu.removeAll();
            addToMenu(optmenu, Welcome);

            optmenu.addSeparator();

            addToMenu(optmenu, WarningNonfatal);
            addToMenu(optmenu, SubMemory, SubStack, VerbosityPref);

            optmenu.addSeparator();

            addToMenu(optmenu, SyntaxDisabled);
            addToMenu(optmenu, FontSize);
            menuItem(optmenu, "Font: " + FontName.get() + "...", doOptFontname());
            addToMenu(optmenu, TabSize);
            if (Util.onMac() || Util.onWindows())
                menuItem(optmenu, "Use anti-aliasing: Yes", false);
            else
                addToMenu(optmenu, AntiAlias);
            addToMenu(optmenu, A4Preferences.LAF);

            optmenu.addSeparator();

            addToMenu(optmenu, Solver);
            addToMenu(optmenu, SkolemDepth);
            JMenu cmMenu = addToMenu(optmenu, CoreMinimization);
            cmMenu.setEnabled(Solver.get() == SatSolver.MiniSatProverJNI);
            JMenu cgMenu = addToMenu(optmenu, CoreGranularity);
            cgMenu.setEnabled(Solver.get() == SatSolver.MiniSatProverJNI);

            addToMenu(optmenu, AutoVisualize, RecordKodkod);

            if (Version.experimental) {
                addToMenu(optmenu, Unrolls);
                addToMenu(optmenu, ImplicitThis, NoOverflow, InferPartialInstance);
            }

        } finally {
            wrap = false;
        }
        return null;
    }

    private Runner doOptFontname() {
        if (wrap)
            return wrapMe();
        int size = FontSize.get();
        String f = OurDialog.askFont();
        if (f.length() > 0) {
            FontName.set(f);
            currFont = new Font(f, Font.PLAIN, size);
            //coverageText.setFont(currFont);
            //coverageDetails.setFont(currFont);
            text.setFont(f, size, TabSize.get());
            status.setFont(new Font(f, Font.PLAIN, size));
            log.setFontName(f);
            changeFont(aunitText);
            changeFont(coverageText);
            changeFont(mualloyText);
            changeFont(alloyflText);
            changeFont(arepairText);
            changeFont(text.getComponent());
            if (cov_header_label != null)
                cov_header_label.setFont(new Font(f, Font.BOLD, size + 3));
        }
        return null;
    }

    /**
     * This method applies the changes to the font-related settings.
     */
    private Runner doOptRefreshFont() {
        if (wrap)
            return wrapMe();
        String f = FontName.get();
        int n = FontSize.get();
        text.setFont(f, n, TabSize.get());
        currFont = new Font(f, Font.PLAIN, n);
        status.setFont(new Font(f, Font.PLAIN, n));
        log.setFontSize(n);
        viz.doSetFontSize(n);
        changeFont(aunitText);
        changeFont(coverageText);
        changeFont(mualloyText);
        changeFont(alloyflText);
        changeFont(arepairText);
        changeFont(text.getComponent());
        if (cov_header_label != null)
            cov_header_label.setFont(new Font(f, Font.BOLD, n + 3));
        return null;
    }

    /** This method toggles the "antialias" checkbox. */
    private Runner doOptAntiAlias() {
        if (!wrap) {
            OurAntiAlias.enableAntiAlias(AntiAlias.get());
        }
        return wrapMe();
    }

    /**
     * This method toggles the "syntax highlighting" checkbox.
     */
    private Runner doOptSyntaxHighlighting() {
        if (!wrap) {
            text.enableSyntax(!SyntaxDisabled.get());
        }
        return wrapMe();
    }

    /** This method recursively updates the font for all components. **/
    public void changeFont(Component component) {
        component.setFont(currFont);
        component.repaint();
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                changeFont(child);
            }
        }
    }

    // ===============================================================================================================//

    /** This method displays the about box. */
    public Runner doAbout() {
        if (wrap)
            return wrapMe();

        // Old about message
        // OurDialog.showmsg("About Alloy Analyzer " + Version.version(), OurUtil.loadIcon("images/logo.gif"), "Alloy Analyzer " + Version.version(), "Build date: " + " git: " + Version.commit, " ", "Lead developer: Felix Chang", "Engine developer: Emina Torlak", "Graphic design: Julie Pelaez", "Project lead: Daniel Jackson", " ", "Please post comments and questions to the Alloy Community Forum at http://alloy.mit.edu/", " ", "Thanks to: Ilya Shlyakhter, Manu Sridharan, Derek Rayside, Jonathan Edwards, Gregory Dennis,", "Robert Seater, Edmond Lau, Vincent Yeung, Sam Daitch, Andrew Yip, Jongmin Baek, Ning Song,", "Arturo Arizpe, Li-kuo (Brian) Lin, Joseph Cohen, Jesse Pavel, Ian Schechter, and Uriel Schafer.");

        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:Verdana, Trebuchet MS,Geneva, sans-serif; font-size: 10px; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color: #C0C0C0; padding: 4px; margin: 4px; }");
        styleSheet.addRule("th {text-align:left;}");

        JTextPane ta = new JTextPane();
        ta.setEditorKit(kit);
        ta.setContentType("text/html");
        ta.setBackground(null);
        ta.setBorder(null);
        ta.setFont(new JLabel().getFont());
        // @formatter:off
        ta.setText("<html><h1>Alloy Analyzer Plus" + Version.getShortversion() + "</h1>"
        +"<br/>"
        +"<html>"
        + "<tr><th>Base Alloy Analyzer: Project Lead</th><td>Daniel Jackson</td></tr>"
        + "<tr><th>Base Alloy Analyzer: Chief Developer</th><td>Aleksandar Milicevic</td></tr>"
        + "<tr><th>Base Alloy Analyzer: Kodkod Engine</th><td>Emina Torlak</td></tr>"
        + "<tr><th>Base Alloy Analyzer: Open Source</th><td>Peter Kriens</td></tr>"
        + "<tr><th>Alloy Analyzer Plus: Project Lead</th><td>Allison Sullivan, Kaiyuan Wang</td></tr>"
        + "<tr><th>Alloy Analyzer Plus: Extension Development</th><td>Allison Sullivan, Kaiyuan Wang</td></tr>"
        + "</table><br/>"
        +"<p>Alloy Analyzer Plus IDE is an extension built of top fo the official Alloy Analyzer" + Version.getShortversion()  +".</p>"
        +"<p>Alloy Analyzer Plus IDE provides verification frameworks for Alloy users.</p>"
        +"<p>For more information about Alloy, <a href='http://alloytools.org'>http://alloytools.org</a></p>"
        +"<p>Questions and comments about Alloy are welcome at the community forum:</p>"
        +"<p>Alloy Community Forum: <a href='https://groups.google.com/forum/#!forum/alloytools'>https://groups.google.com/forum/#!forum/alloytools</a></p>"
        +"<p>Alloy experts also respond to <a href='https://stackoverflow.com/questions/tagged/alloy'>https://stackoverflow.com</a> questions tagged <code>alloy</code>.</p>"
        +"<p>Major contributions to earlier versions of Alloy were made by: Felix Chang (v4);<br/>"
        +"Jonathan Edwards, Eunsuk Kang, Joe Near, Robert Seater, Derek Rayside, Greg Dennis,<br/>"
        +"Ilya Shlyakhter, Mana Taghdiri, Mandana Vaziri, Sarfraz Khurshid (v3); Manu Sridharan<br/>"
        +"(v2); Edmond Lau, Vincent Yeung, Sam Daitch, Andrew Yip, Jongmin Baek, Ning Song,<br/>"
        +"Arturo Arizpe, Li-kuo (Brian) Lin, Joseph Cohen, Jesse Pavel, Ian Schechter, Uriel<br/>"
        +"Schafer (v1).</p>"
        +"<p>The development of Alloy was funded by part by the National Science Foundation under<br/>"
        +"Grant Nos. 0325283, 0541183, 0438897 and 0707612; by the Air Force Research Laboratory<br/>"
        +"(AFRL/IF) and the Disruptive Technology Office (DTO) in the National Intelligence<br/>"
        +"Community Information Assurance Research (NICIAR) Program; and by the Nokia<br/>"
        +"Corporation as part of a collaboration between Nokia Research and MIT CSAIL.</p>"
        +"<br/><pre>"
        +"Build Date: " + Version.buildDate()+"<br/>"
        +"Git Commit: " + Version.commit
        +"</pre>");
        // @formatter:on
        ta.setEditable(false);
        ta.addHyperlinkListener((e) -> {
            if (e.getEventType() == EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                        // ignore
                    }
                }
            }
        });
        OurDialog.showmsg("About Alloy Analyzer Plus 1.0", ta);

        return null;
    }

    /** This method displays the help html. */
    private Runner doHelp() {
        if (wrap)
            return wrapMe();
        try {
            int w = OurUtil.getScreenWidth(), h = OurUtil.getScreenHeight();
            final JFrame frame = new JFrame();
            final JEditorPane html1 = new JEditorPane("text/html", "");
            final JEditorPane html2 = new JEditorPane("text/html", "");
            final HTMLDocument doc1 = (HTMLDocument) (html1.getDocument());
            doc1.setAsynchronousLoadPriority(-1);
            final HTMLDocument doc2 = (HTMLDocument) (html2.getDocument());
            doc2.setAsynchronousLoadPriority(-1);
            html1.setPage(this.getClass().getResource("/help/Nav.html"));
            html2.setPage(this.getClass().getResource("/help/index.html"));
            HyperlinkListener hl = new HyperlinkListener() {

                @Override
                public final void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                            return;
                        if (e.getURL().getPath().endsWith("quit.htm")) {
                            frame.dispose();
                            return;
                        }
                        HTMLDocument doc = (HTMLDocument) (html2.getDocument());
                        doc.setAsynchronousLoadPriority(-1); // So that we can
                                                            // catch any
                                                            // exception
                                                            // that may
                                                            // occur
                        html2.setPage(e.getURL());
                        html2.requestFocusInWindow();
                    } catch (Throwable ex) {
                    }
                }
            };
            html1.setEditable(false);
            html1.setBorder(new EmptyBorder(3, 3, 3, 3));
            html1.addHyperlinkListener(hl);
            html2.setEditable(false);
            html2.setBorder(new EmptyBorder(3, 3, 3, 3));
            html2.addHyperlinkListener(hl);
            JScrollPane scroll1 = OurUtil.scrollpane(html1);
            JScrollPane scroll2 = OurUtil.scrollpane(html2);
            JSplitPane split = OurUtil.splitpane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2, 150);
            split.setResizeWeight(0d);
            frame.setTitle("Alloy Analyzer Online Guide");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(split, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(w - w / 10, h - h / 10);
            frame.setLocation(w / 20, h / 20);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            html2.requestFocusInWindow();
        } catch (Throwable ex) {
            return null;
        }
        return null;
    }

    private Runner doAUnitHelp() {
        if (wrap)
            return wrapMe();
        try {
            int w = OurUtil.getScreenWidth(), h = OurUtil.getScreenHeight();
            final JFrame frame = new JFrame();
            final JEditorPane html1 = new JEditorPane("text/html", "");
            final JEditorPane html2 = new JEditorPane("text/html", "");
            final HTMLDocument doc1 = (HTMLDocument) (html1.getDocument());
            doc1.setAsynchronousLoadPriority(-1);
            final HTMLDocument doc2 = (HTMLDocument) (html2.getDocument());
            doc2.setAsynchronousLoadPriority(-1);

            File doc = new File("help/AUnitNav.html");
            html1.setPage(doc.toURI().toURL());
            File doc3 = new File("help/aunitindex.html");
            html2.setPage(doc3.toURI().toURL());
            HyperlinkListener hl = new HyperlinkListener() {

                public final void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                            return;
                        if (e.getURL().getPath().endsWith("quit.htm")) {
                            frame.dispose();
                            return;
                        }
                        HTMLDocument doc = (HTMLDocument) (html2.getDocument());
                        doc.setAsynchronousLoadPriority(-1); // So that we can catch any exception that may occur
                        html2.setPage(e.getURL());
                        html2.requestFocusInWindow();
                    } catch (Throwable ex) {
                    }
                }
            };
            html1.setEditable(false);
            html1.setBorder(new EmptyBorder(3, 3, 3, 3));
            html1.addHyperlinkListener(hl);
            html2.setEditable(false);
            html2.setBorder(new EmptyBorder(3, 3, 3, 3));
            html2.addHyperlinkListener(hl);
            JScrollPane scroll1 = OurUtil.scrollpane(html1);
            JScrollPane scroll2 = OurUtil.scrollpane(html2);
            JSplitPane split = OurUtil.splitpane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2, 150);
            split.setResizeWeight(0d);
            frame.setTitle("Alloy Analyzer AUnit Guide");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(split, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, h - h / 10);
            frame.setLocation(w / 20, h / 20);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            html2.requestFocusInWindow();
        } catch (Throwable ex) {
            return null;
        }
        return null;
    }

    private Runner doMuAlloyHelp() {
        if (wrap)
            return wrapMe();
        try {
            int w = OurUtil.getScreenWidth(), h = OurUtil.getScreenHeight();
            final JFrame frame = new JFrame();
            final JEditorPane html1 = new JEditorPane("text/html", "");
            final JEditorPane html2 = new JEditorPane("text/html", "");
            final HTMLDocument doc1 = (HTMLDocument) (html1.getDocument());
            doc1.setAsynchronousLoadPriority(-1);
            final HTMLDocument doc2 = (HTMLDocument) (html2.getDocument());
            doc2.setAsynchronousLoadPriority(-1);

            File doc = new File("help/AUnitNav.html");
            html1.setPage(doc.toURI().toURL());
            File doc3 = new File("help/aunitindex.html");
            html2.setPage(doc3.toURI().toURL());
            HyperlinkListener hl = new HyperlinkListener() {

                public final void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                            return;
                        if (e.getURL().getPath().endsWith("quit.htm")) {
                            frame.dispose();
                            return;
                        }
                        HTMLDocument doc = (HTMLDocument) (html2.getDocument());
                        doc.setAsynchronousLoadPriority(-1); // So that we can catch any exception that may occur
                        html2.setPage(e.getURL());
                        html2.requestFocusInWindow();
                    } catch (Throwable ex) {
                    }
                }
            };
            html1.setEditable(false);
            html1.setBorder(new EmptyBorder(3, 3, 3, 3));
            html1.addHyperlinkListener(hl);
            html2.setEditable(false);
            html2.setBorder(new EmptyBorder(3, 3, 3, 3));
            html2.addHyperlinkListener(hl);
            JScrollPane scroll1 = OurUtil.scrollpane(html1);
            JScrollPane scroll2 = OurUtil.scrollpane(html2);
            JSplitPane split = OurUtil.splitpane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2, 150);
            split.setResizeWeight(0d);
            frame.setTitle("Alloy Analyzer AUnit Guide");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(split, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, h - h / 10);
            frame.setLocation(w / 20, h / 20);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            html2.requestFocusInWindow();
        } catch (Throwable ex) {
            return null;
        }
        return null;
    }

    private Runner doAlloyFLHelp() {
        if (wrap)
            return wrapMe();
        try {
            int w = OurUtil.getScreenWidth(), h = OurUtil.getScreenHeight();
            final JFrame frame = new JFrame();
            final JEditorPane html1 = new JEditorPane("text/html", "");
            final JEditorPane html2 = new JEditorPane("text/html", "");
            final HTMLDocument doc1 = (HTMLDocument) (html1.getDocument());
            doc1.setAsynchronousLoadPriority(-1);
            final HTMLDocument doc2 = (HTMLDocument) (html2.getDocument());
            doc2.setAsynchronousLoadPriority(-1);

            File doc = new File("help/AUnitNav.html");
            html1.setPage(doc.toURI().toURL());
            File doc3 = new File("help/aunitindex.html");
            html2.setPage(doc3.toURI().toURL());
            HyperlinkListener hl = new HyperlinkListener() {

                public final void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                            return;
                        if (e.getURL().getPath().endsWith("quit.htm")) {
                            frame.dispose();
                            return;
                        }
                        HTMLDocument doc = (HTMLDocument) (html2.getDocument());
                        doc.setAsynchronousLoadPriority(-1); // So that we can catch any exception that may occur
                        html2.setPage(e.getURL());
                        html2.requestFocusInWindow();
                    } catch (Throwable ex) {
                    }
                }
            };
            html1.setEditable(false);
            html1.setBorder(new EmptyBorder(3, 3, 3, 3));
            html1.addHyperlinkListener(hl);
            html2.setEditable(false);
            html2.setBorder(new EmptyBorder(3, 3, 3, 3));
            html2.addHyperlinkListener(hl);
            JScrollPane scroll1 = OurUtil.scrollpane(html1);
            JScrollPane scroll2 = OurUtil.scrollpane(html2);
            JSplitPane split = OurUtil.splitpane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2, 150);
            split.setResizeWeight(0d);
            frame.setTitle("Alloy Analyzer AUnit Guide");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(split, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, h - h / 10);
            frame.setLocation(w / 20, h / 20);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            html2.requestFocusInWindow();
        } catch (Throwable ex) {
            return null;
        }
        return null;
    }


    private Runner doARepairHelp() {
        if (wrap)
            return wrapMe();
        try {
            int w = OurUtil.getScreenWidth(), h = OurUtil.getScreenHeight();
            final JFrame frame = new JFrame();
            final JEditorPane html1 = new JEditorPane("text/html", "");
            final JEditorPane html2 = new JEditorPane("text/html", "");
            final HTMLDocument doc1 = (HTMLDocument) (html1.getDocument());
            doc1.setAsynchronousLoadPriority(-1);
            final HTMLDocument doc2 = (HTMLDocument) (html2.getDocument());
            doc2.setAsynchronousLoadPriority(-1);

            File doc = new File("help/AUnitNav.html");
            html1.setPage(doc.toURI().toURL());
            File doc3 = new File("help/aunitindex.html");
            html2.setPage(doc3.toURI().toURL());
            HyperlinkListener hl = new HyperlinkListener() {

                public final void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
                            return;
                        if (e.getURL().getPath().endsWith("quit.htm")) {
                            frame.dispose();
                            return;
                        }
                        HTMLDocument doc = (HTMLDocument) (html2.getDocument());
                        doc.setAsynchronousLoadPriority(-1); // So that we can catch any exception that may occur
                        html2.setPage(e.getURL());
                        html2.requestFocusInWindow();
                    } catch (Throwable ex) {
                    }
                }
            };
            html1.setEditable(false);
            html1.setBorder(new EmptyBorder(3, 3, 3, 3));
            html1.addHyperlinkListener(hl);
            html2.setEditable(false);
            html2.setBorder(new EmptyBorder(3, 3, 3, 3));
            html2.addHyperlinkListener(hl);
            JScrollPane scroll1 = OurUtil.scrollpane(html1);
            JScrollPane scroll2 = OurUtil.scrollpane(html2);
            JSplitPane split = OurUtil.splitpane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2, 150);
            split.setResizeWeight(0d);
            frame.setTitle("Alloy Analyzer AUnit Guide");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(split, BorderLayout.CENTER);
            frame.pack();
            frame.setSize(1200, h - h / 10);
            frame.setLocation(w / 20, h / 20);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            html2.requestFocusInWindow();
        } catch (Throwable ex) {
            return null;
        }
        return null;
    }



    /** This method displays the license box. */
    private Runner doLicense() {
        if (wrap)
            return wrapMe();
        final String JAR = Util.jarPrefix();
        String alloytxt;
        try {
            alloytxt = Util.readAll(JAR + "LICENSES" + File.separator + "Alloy.txt");
        } catch (IOException ex) {
            return null;
        }
        final JTextArea text = OurUtil.textarea(alloytxt, 15, 85, false, false, new EmptyBorder(2, 2, 2, 2), new Font("Monospaced", Font.PLAIN, 12));
        final JScrollPane scroll = OurUtil.scrollpane(text, new LineBorder(Color.DARK_GRAY, 1));
        final JComboBox combo = new OurCombobox(new String[] {
                                                              "Alloy", "Kodkod", "JavaCup", "SAT4J", "ZChaff", "MiniSat"
        }) {

            private static final long serialVersionUID = 0;

            @Override
            public void do_changed(Object value) {
                if (value instanceof String) {
                    try {
                        String content = Util.readAll(JAR + "LICENSES" + File.separator + value + ".txt");
                        text.setText(content);
                    } catch (IOException ex) {
                        text.setText("Sorry: an error has occurred in displaying the license file.");
                    }
                }
                text.setCaretPosition(0);
            }
        };
        OurDialog.showmsg("Copyright Notices", "The source code for the Alloy Analyzer is available under the MIT license.", " ", "The Alloy Analyzer utilizes several third-party packages whose code may", "be distributed under a different license. We are extremely grateful to", "the authors of these packages for making their source code freely available.", " ", OurUtil.makeH(null, "See the copyright notice for: ", combo, null), " ", scroll);
        return null;
    }

    /** This method changes the latest instance. */
    public void doSetLatest(String arg) {
        latestInstance = arg;
        latestAutoInstance = arg;
    }

    /** Set up colors for marking coverage **/
    final Color covRed          = new Color(255, 111, 111);
    final Color covYellow       = new Color(255, 243, 111);
    final Color covGreen        = new Color(91, 214, 105);

    /**
     * The color to use for functions/predicate/paragraphs that contains part of the
     * unsat core.
     */
    final Color supCoreColor    = new Color(0.95f, 0.1f, 0.1f);

    /** The color to use for the unsat core. */
    final Color coreColor       = new Color(0.9f, 0.4f, 0.4f);

    /**
     * The color to use for functions/predicate used by the Unsat core.
     */
    final Color subCoreColor    = new Color(0.9f, 0.7f, 0.7f);

    final Color fl_veryred      = new Color(199, 46, 37);
    final Color fl_red          = new Color(231, 76, 60);
    final Color fl_lightred     = new Color(236, 112, 99);
    final Color fl_redorange    = new Color(235, 152, 78);
    final Color fl_veryorange   = new Color(248, 196, 113);
    final Color fl_orange       = new Color(247, 220, 111);
    final Color fl_orangeyellow = new Color(249, 231, 159);
    final Color fl_veryyellow   = new Color(252, 243, 207);
    final Color fl_yellow       = new Color(254, 249, 231);

    public Runner doVisualizeHighlights(ArrayList<Pos> highlight_locs, ArrayList<String> colors, boolean new_run) {
        text.clearShade();

        if (new_run) {
            latest_highlight_pos = new ArrayList<Pos>();
            latest_highlight_colors = new ArrayList<String>();

            latest_highlight_pos.addAll(highlight_locs);
            latest_highlight_colors.addAll(colors);
        }

        Pos criteria;
        String color;

        //Color from least to most sus
        HashMap<String,Color> color_mapping = new HashMap<String,Color>();
        color_mapping.put("0.1", fl_yellow);
        color_mapping.put("0.2", fl_veryyellow);
        color_mapping.put("0.3", fl_orangeyellow);
        color_mapping.put("0.4", fl_orange);
        color_mapping.put("0.5", fl_veryorange);
        color_mapping.put("0.6", fl_redorange);
        color_mapping.put("0.7", fl_lightred);
        color_mapping.put("0.8", fl_red);
        color_mapping.put("0.9", fl_veryred);

        HashMap<String,ArrayList<Integer>> sort = new HashMap<String,ArrayList<Integer>>();
        sort.put("0.1", new ArrayList<Integer>());
        sort.put("0.2", new ArrayList<Integer>());
        sort.put("0.3", new ArrayList<Integer>());
        sort.put("0.4", new ArrayList<Integer>());
        sort.put("0.5", new ArrayList<Integer>());
        sort.put("0.6", new ArrayList<Integer>());
        sort.put("0.7", new ArrayList<Integer>());
        sort.put("0.8", new ArrayList<Integer>());
        sort.put("0.9", new ArrayList<Integer>());

        ArrayList<String> highlight_order = new ArrayList<String>();
        highlight_order.add("0.1");
        highlight_order.add("0.2");
        highlight_order.add("0.3");
        highlight_order.add("0.4");
        highlight_order.add("0.5");
        highlight_order.add("0.6");
        highlight_order.add("0.7");
        highlight_order.add("0.8");
        highlight_order.add("0.9");

        for (int i = 0; i < colors.size(); i++) {
            if (!colors.get(i).equals("0.0"))
                sort.get(colors.get(i)).add(i);
        }

        for (String level : highlight_order) {
            for (Integer i : sort.get(level)) {
                HashSet<Pos> temp = new HashSet<Pos>();
                temp.add(highlight_locs.get(i));
                text.shade(temp, color_mapping.get(level), false);
            }
        }

        return null;
    }

    /**
     * This method displays a particular instance or message.
     */
    JFrame repairFrame;

    @SuppressWarnings("unchecked" )
    public Runner doVisualize(String arg) {
        if (wrap)
            return wrapMe(arg);
        text.clearShade();
        if (arg.startsWith("COV")) {
            text.clearShade();
            InputStream is = null;
            ObjectInputStream ois = null;
            Pos criteria;
            String color;
            try {
                is = new FileInputStream("model.cov");
                ois = new ObjectInputStream(is);
                Object obj = null;
                while ((obj = ois.readObject()) != null) {
                    criteria = (Pos) obj;
                    HashSet<Pos> temp = new HashSet<Pos>();
                    temp.add(criteria);
                    color = (String) ois.readObject();
                    if (color.equals("green")) {
                        text.shade(temp, covGreen, false);
                    } else if (color.equals("yellow")) {
                        text.shade(temp, covYellow, false);
                    } else if (color.equals("red")) {
                        text.shade(temp, covRed, false);
                    }
                }


            } catch (Throwable ex) {
                return null;
            } finally {
                Util.close(ois);
                Util.close(is);
                File delete_creations = new File("model.cov");
                delete_creations.delete();
            }
        }
        if (arg.startsWith("MSG: ")) { // MSG: message
            OurDialog.showtext("Detailed Message", arg.substring(5));
        }
        if (arg.startsWith("CORE: ")) { // CORE: filename
            String filename = Util.canon(arg.substring(6));
            Pair<Set<Pos>,Set<Pos>> hCore;
            // Set<Pos> lCore;
            InputStream is = null;
            ObjectInputStream ois = null;
            try {
                is = new FileInputStream(filename);
                ois = new ObjectInputStream(is);
                hCore = (Pair<Set<Pos>,Set<Pos>>) ois.readObject();
                // lCore = (Set<Pos>) ois.readObject();
            } catch (Throwable ex) {
                log.logRed("Error reading or parsing the core \"" + filename + "\"\n");
                return null;
            } finally {
                Util.close(ois);
                Util.close(is);
            }
            text.clearShade();
            text.shade(hCore.b, subCoreColor, false);
            text.shade(hCore.a, coreColor, false);
            // shade again, because if not all files were open, some shadings
            // will have no effect
            text.shade(hCore.b, subCoreColor, false);
            text.shade(hCore.a, coreColor, false);
        }
        if (arg.startsWith("POS: ")) { // POS: x1 y1 x2 y2 filename
            Scanner s = new Scanner(arg.substring(5));
            int x1 = s.nextInt(), y1 = s.nextInt(), x2 = s.nextInt(), y2 = s.nextInt();
            String f = s.nextLine();
            if (f.length() > 0 && f.charAt(0) == ' ')
                f = f.substring(1); // Get rid of the space after Y2
            Pos p = new Pos(Util.canon(f), x1, y1, x2, y2);
            text.shade(p);
        }
        if (arg.startsWith("CNF: ")) { // CNF: filename
            String filename = Util.canon(arg.substring(5));
            try {
                String text = Util.readAll(filename);
                OurDialog.showtext("Text Viewer", text);
            } catch (IOException ex) {
                log.logRed("Error reading the file \"" + filename + "\"\n");
            }
        }
        if (arg.startsWith("XML: ")) { // XML: filename
            viz.loadXML(Util.canon(arg.substring(5)), false);
        }
        if (arg.startsWith("VAL: ")) { // XML: filename
            viz_val.loadXML(Util.canon(arg.substring(5)), false);
        }
        if (arg.startsWith("MUT: ")) { // XML: filename
            String[] text = Util.canon(arg.substring(5)).split(",");
            viz_mualloy.loadXML(text[0], false, text[1]);
        }
        if (arg.startsWith("REP: ")) {
            OurSyntaxWidget text = new OurSyntaxWidget(true, "", OurUtil.getVizFont().getName(), OurUtil.getVizFont().getSize(), 4, null, null);
            //String model = FileUtil.readText(arg.substring(5));
            //model = model.trim();

            text.setText(patchedModel);
            text.setUnEditable();
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            text.addTo(panel, BorderLayout.CENTER);
            JScrollPane ans = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            ans.setBackground(Color.WHITE);
            repairFrame = new JFrame("Patched Model");
            Container all = repairFrame.getContentPane();
            BoxLayout boxLayout = new BoxLayout(all, BoxLayout.Y_AXIS);
            all.setLayout(boxLayout);
            all.add(ans);
            int screenWidth = OurUtil.getScreenWidth(), screenHeight = OurUtil.getScreenHeight();
            repairFrame.setBackground(Color.WHITE);
            if (repairFrame != null) {
                repairFrame.setSize(screenWidth / 2, screenHeight / 2);
                repairFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                // repairFrame.addWindowListener(doClose());

                repairFrame.addComponentListener(this);
                repairFrame.setVisible(true);
                OurUtil.show(repairFrame);
            }
        }
        return null;
    }

    /** This method opens a particular file. */
    private Runner doOpenFile(String arg) {
        if (wrap)
            return wrapMe(arg);
        String f = Util.canon(arg);
        if (!text.newtab(f))
            return null;
        if (text.get().isFile())
            addHistory(f);
        doShow();
        text.get().requestFocusInWindow();
        log.clearError();
        return null;
    }

    /** This object performs solution enumeration. */
    private final Computer enumerator = new Computer() {

        @Override
        public String compute(Object input) {
            final String arg = (String) input;
            OurUtil.show(frame);
            if (WorkerEngine.isBusy())
                throw new RuntimeException("Alloy4 is currently executing a SAT solver command. Please wait until that command has finished.");
            SimpleCallback1 cb = new SimpleCallback1(SimpleGUI.this, viz, log, VerbosityPref.get().ordinal(), latestAlloyVersionName, latestAlloyVersion);
            SimpleTask2 task = new SimpleTask2();
            task.filename = arg;
            try {
                if (AlloyCore.isDebug())
                    WorkerEngine.runLocally(task, cb);
                else
                    WorkerEngine.run(task, SubMemory.get(), SubStack.get(), alloyHome() + fs + "binary", "", cb);
                // task.run(cb);
            } catch (Throwable ex) {
                WorkerEngine.stop();
                log.logBold("Fatal Error: Solver failed due to unknown reason.\n" + "One possible cause is that, in the Options menu, your specified\n" + "memory size is larger than the amount allowed by your OS.\n" + "Also, please make sure \"java\" is in your program path.\n");
                log.logDivider();
                log.flush();
                doStop(2);
                return arg;
            }
            subrunningTask = 2;
            runmenu.setEnabled(false);
            runbutton.setVisible(false);
            showbutton.setEnabled(false);
            stopbutton.setVisible(true);
            return arg;
        }
    };

    /** Converts an A4TupleSet into a SimTupleset object. */
    private static SimTupleset convert(Object object) throws Err {
        if (!(object instanceof A4TupleSet))
            throw new ErrorFatal("Unexpected type error: expecting an A4TupleSet.");
        A4TupleSet s = (A4TupleSet) object;
        if (s.size() == 0)
            return SimTupleset.EMPTY;
        List<SimTuple> list = new ArrayList<SimTuple>(s.size());
        int arity = s.arity();
        for (A4Tuple t : s) {
            String[] array = new String[arity];
            for (int i = 0; i < t.arity(); i++)
                array[i] = t.atom(i);
            list.add(SimTuple.make(array));
        }
        return SimTupleset.make(list);
    }

    /** Converts an A4Solution into a SimInstance object. */
    private static SimInstance convert(Module root, A4Solution ans) throws Err {
        SimInstance ct = new SimInstance(root, ans.getBitwidth(), ans.getMaxSeq());
        for (Sig s : ans.getAllReachableSigs()) {
            if (!s.builtin)
                ct.init(s, convert(ans.eval(s)));
            for (Field f : s.getFields())
                if (!f.defined)
                    ct.init(f, convert(ans.eval(f)));
        }
        for (ExprVar a : ans.getAllAtoms())
            ct.init(a, convert(ans.eval(a)));
        for (ExprVar a : ans.getAllSkolems())
            ct.init(a, convert(ans.eval(a)));
        return ct;
    }

    /** This object performs expression evaluation. */
    private static Computer evaluator = new Computer() {

        private String filename = null;

        @Override
        public final Object compute(final Object input) throws Exception {
            if (input instanceof File) {
                filename = ((File) input).getAbsolutePath();
                return "";
            }
            if (!(input instanceof String))
                return "";
            final String str = (String) input;
            if (str.trim().length() == 0)
                return ""; // Empty line
            Module root = null;
            A4Solution ans = null;
            try {
                Map<String,String> fc = new LinkedHashMap<String,String>();
                XMLNode x = new XMLNode(new File(filename));
                if (!x.is("alloy"))
                    throw new Exception();
                String mainname = null;
                for (XMLNode sub : x)
                    if (sub.is("instance")) {
                        mainname = sub.getAttribute("filename");
                        break;
                    }
                if (mainname == null)
                    throw new Exception();
                for (XMLNode sub : x)
                    if (sub.is("source")) {
                        String name = sub.getAttribute("filename");
                        String content = sub.getAttribute("content");
                        fc.put(name, content);
                    }
                root = CompUtil.parseEverything_fromFile(A4Reporter.NOP, fc, mainname, (Version.experimental && ImplicitThis.get()) ? 2 : 1);
                ans = A4SolutionReader.read(root.getAllReachableSigs(), x);
                for (ExprVar a : ans.getAllAtoms()) {
                    root.addGlobal(a.label, a);
                }
                for (ExprVar a : ans.getAllSkolems()) {
                    root.addGlobal(a.label, a);
                }
            } catch (Throwable ex) {
                throw new ErrorFatal("Failed to read or parse the XML file.");
            }
            try {
                Expr e = CompUtil.parseOneExpression_fromString(root, str);
                if (AlloyCore.isDebug() && VerbosityPref.get() == Verbosity.FULLDEBUG) {
                    SimInstance simInst = convert(root, ans);
                    if (simInst.wasOverflow())
                        return simInst.visitThis(e).toString() + " (OF)";
                }
                return ans.eval(e);
            } catch (HigherOrderDeclException ex) {
                throw new ErrorType("Higher-order quantification is not allowed in the evaluator.");
            }
        }
    };

    // ====== Main Method ====================================================//

    /**
     * Main method that launches the program; this method might be called by an
     * arbitrary thread.
     */
    public static void main(final String[] args) throws Exception {

        List<String> remainingArgs = new ArrayList<>();

        if (args.length > 0) {
            boolean help = false;
            boolean quit = false;

            for (String cmd : args) {
                switch (cmd) {

                    case "--worker" :
                    case "-w" :
                        WorkerEngine.main(args);
                        break;

                    case "--version" :
                    case "-v" :
                        System.out.println(Version.version());
                        break;

                    case "--help" :
                    case "-h" :
                    case "-?" :
                        help = true;
                        break;

                    case "--debug" :
                    case "-d" :
                        System.setProperty("debug", "yes");
                        break;

                    case "--quit" :
                    case "-q" :
                        quit = true;
                        break;

                    default :
                        if (cmd.endsWith(".als"))
                            remainingArgs.add(cmd);
                        else {
                            System.out.println("Unknown cmd " + cmd);
                            help = true;
                        }
                        break;
                }
            }

            if (help)
                System.out.println("Usage: alloy [options] file ...\n" + "  //" + "  -d/--debug                  set debug mode\n" + "  -h/--help                   show this help\n" + "  -q/--quit                   do not continue with GUI\n" + "  -v/--version                show version\n");

            if (quit)
                return;
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SimpleGUI(args);
            }
        });
    }

    // ====== Constructor ====================================================//

    // /** Create a dummy task object for testing purpose. */
    // private static final WorkerEngine.WorkerTask dummyTask = new
    // WorkerEngine.WorkerTask() {
    // private static final long serialVersionUID = 0;
    // public void run(WorkerCallback out) { }
    // };

    /**
     * The constructor; this method will be called by the AWT event thread, using
     * the "invokeLater" method.
     */
    private SimpleGUI(final String[] args) {

        UIManager.put("ToolTip.font", new FontUIResource("Courier New", Font.PLAIN, 14));

        // Register an exception handler for uncaught exceptions
        MailBug.setup();

        // Enable better look-and-feel
        if (Util.onMac() || Util.onWindows()) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Alloy");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        if (Util.onMac()) {
            try {
                macUtil = new MacUtil();
                macUtil.addMenus(this);
            } catch (NoClassDefFoundError e) {
                // ignore
            }
        }

        doLookAndFeel();

        // Figure out the desired x, y, width, and height
        int screenWidth = OurUtil.getScreenWidth(), screenHeight = OurUtil.getScreenHeight();
        int width = AnalyzerWidth.get();
        if (width <= 0)
            width = screenWidth / 10 * 8;
        else if (width < 100)
            width = 100;
        if (width > screenWidth)
            width = screenWidth;
        int height = AnalyzerHeight.get();
        if (height <= 0)
            height = screenHeight / 10 * 8;
        else if (height < 100)
            height = 100;
        if (height > screenHeight)
            height = screenHeight;
        int x = AnalyzerX.get();
        if (x < 0)
            x = screenWidth / 10;
        if (x > screenWidth - 100)
            x = screenWidth - 100;
        int y = AnalyzerY.get();
        if (y < 0)
            y = screenHeight / 10;
        if (y > screenHeight - 100)
            y = screenHeight - 100;

        // Put up a slash screen
        final JFrame frame = new JFrame("Alloy Analyzer");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.pack();
        if (!Util.onMac() && !Util.onWindows()) {
            String gravity = System.getenv("_JAVA_AWT_WM_STATIC_GRAVITY");
            if (gravity == null || gravity.length() == 0) {
                // many Window managers do not respect ICCCM2; this should help
                // avoid the Title Bar being shifted "off screen"
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
            }
        }

        if (width < 500)
            width = 500;
        if (height < 500)
            height = 500;

        frame.setSize(width, height);
        frame.setLocation(x, y);
        frame.setVisible(true);
        frame.setTitle("Alloy Analyzer " + Version.version() + " loading... please wait...");
        final int windowWidth = width;
        // We intentionally call setVisible(true) first before settings the
        // "please wait" title,
        // since we want the minimized window title on Linux/FreeBSD to just say
        // Alloy Analyzer

        // Test the allowed memory sizes
        // final WorkerEngine.WorkerCallback c = new
        // WorkerEngine.WorkerCallback() {
        // private final List<Integer> allowed = new ArrayList<Integer>();
        // private final List<Integer> toTry = new
        // ArrayList<Integer>(Arrays.asList(256,512,768,1024,1536,2048,2560,3072,3584,4096));
        // private int mem;
        // public synchronized void callback(Object msg) {
        // if (toTry.size()==0) {
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() { SimpleGUI.this.frame=frame;
        // SimpleGUI.this.finishInit(args, windowWidth); }
        // });
        // return;
        // }
        // try { mem=toTry.remove(0); WorkerEngine.stop();
        // WorkerEngine.run(dummyTask, mem, 128, "", "", this); return; }
        // catch(IOException ex) { fail(); }
        // }
        // public synchronized void done() {
        // //System.out.println("Alloy4 can use "+mem+"M"); System.out.flush();
        // allowed.add(mem);
        // callback(null);
        // }
        // public synchronized void fail() {
        // //System.out.println("Alloy4 cannot use "+mem+"M");
        // System.out.flush();
        // callback(null);
        // }
        // };
        // c.callback(null);

        SimpleGUI.this.frame = frame;
        finishInit(args, windowWidth);
    }

    private void finishInit(String[] args, int width) {

        // Add the listeners
        try {
            wrap = true;
            frame.addWindowListener(doQuit());
        } finally {
            wrap = false;
        }
        frame.addComponentListener(this);

        latest_highlight_pos = new ArrayList<Pos>();
        latest_highlight_colors = new ArrayList<String>();

        // initialize the "allowed memory sizes" array
        // allowedMemorySizes = new
        // ArrayList<Integer>(initialAllowedMemorySizes);
        // int newmem = SubMemory.get();
        // if (!allowedMemorySizes.contains(newmem)) {
        // int newmemlen = allowedMemorySizes.size();
        // if (allowedMemorySizes.contains(768) || newmemlen==0)
        // SubMemory.set(768); // a nice default value
        // else
        // SubMemory.set(allowedMemorySizes.get(newmemlen-1));
        // }

        // Choose the appropriate font
        int fontSize = FontSize.get();
        String fontName = OurDialog.getProperFontName(FontName.get(), "Courier New", "Lucidia", "Courier", "Monospaced");
        FontName.set(fontName);

        // Copy required files from the JAR
        copyFromJAR();
        final String binary = alloyHome() + fs + "binary";

        // Create the menu bar
        JMenuBar bar = new JMenuBar();
        try {
            wrap = true;
            filemenu = menu(bar, "&File", doRefreshFile());
            editmenu = menu(bar, "&Edit", doRefreshEdit());
            runmenu = menu(bar, "E&xecute", doRefreshRun());
            optmenu = menu(bar, "&Options", doRefreshOption());
            aunitMenu = menu(bar, "AUnit", doRefreshAUnit()); /** Sullivan **/
            mualloyMenu = menu(bar, "MuAlloy", doRefreshMuAlloy()); /** Sullivan **/
            alloyFLMenu = menu(bar, "AlloyFL", doRefreshAlloyFL()); /** Sullivan **/
            arepairMenu = menu(bar, "ARepair", doRefreshARepair());
            windowmenu = menu(bar, "&Window", doRefreshWindow(false));
            windowmenu2 = menu(null, "&Window", doRefreshWindow(true));
            helpmenu = menu(bar, "&Help", null);
            if (!Util.onMac())
                menuItem(helpmenu, "About Alloy...", 'A', doAbout());
            menuItem(helpmenu, "Quick Guide", 'Q', doHelp());
            menuItem(helpmenu, "See the Copyright Notices...", 'L', doLicense());
        } finally {
            wrap = false;
        }

        // Pre-load the visualizer
        viz = new VizGUI(false, "", windowmenu2, enumerator, evaluator);
        viz.doSetFontSize(FontSize.get());
        viz_val = new VizValuationGUI(false, "", windowmenu2, enumerator, evaluator);
        viz_val.doSetFontSize(FontSize.get());
        viz_mualloy = new VizMutationGUI(false, "", windowmenu2, enumerator, evaluator);
        viz_val.doSetFontSize(FontSize.get());
        Container all = frame.getContentPane();

        // Create the toolbar
        JPanel storeTB = new JPanel();
        storeTB.setLayout(new GridLayout(0, 1));
        storeTB.setBorder(new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        try {
            wrap = true;
            toolbar = new ToolbarPanel();
            toolbar.setFloatable(false);
            toolbar.setBackground(background);
            toolbar.add(OurUtil.button("New", "Starts a new blank model", "images/newpage.png", doNew(), background));
            toolbar.add(OurUtil.button("Open", "Opens an existing model", "images/open.png", doOpen(), background));
            toolbar.add(OurUtil.button("Reload", "Reload all the models from disk", "images/reload.png", doReloadAll(), background));
            toolbar.add(OurUtil.button("Save", "Saves the current model", "images/save.png", doSave(), background));
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
            toolbar.add(runbutton = OurUtil.button("Execute", "Executes the latest command", "images/execute.png", doExecuteLatest(), background));
            toolbar.add(stopbutton = OurUtil.button("  Stop  ", "Stops the current analysis", "images/executeabort.png", doStop(2), background));
            stopbutton.setVisible(false);
            toolbar.add(showbutton = OurUtil.button("Show", "Shows the latest instance", "images/show.png", doShowLatest(), background));
            /** Add AUnit execution button **/
            divider = OurUtil.makeH(new Dimension(1, 40), Color.LIGHT_GRAY);
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
            toolbar.add(aunitbutton = OurUtil.button("AUnit", "Run all AUnit tests", "images/aunitdone.png", doAUnitExecute(), background));
            toolbar.add(aunitStop = OurUtil.button("Stop", "Stops the current analysis", "images/aunitabort.png", doStop(2), background));
            toolbar.add(mualloy = OurUtil.button("MuAlloy", "Run mutation testing", "images/mutation.png", doMuAlloyExecute(), background));
            toolbar.add(alloyFL = OurUtil.button("AlloyFL", "Run fault localization", "images/fl.png", doAlloyFLExecute(), background));
            toolbar.add(arepairbutton = OurUtil.button("AReapir", "Run ARepair", "images/arepairexecute.png", doARepairExecute(), background));
            toolbar.add(arepairStop = OurUtil.button("Stop", "Stops ARepair attempt", "images/arepairexecuteabort.png", doARepairStop(2), background));
            arepairStop.setVisible(false);
            aunitStop.setVisible(false);
            toolbar.setBorder(null);
        } finally {
            wrap = false;
        }
        storeTB.add(toolbar);

        // Choose the antiAlias setting
        OurAntiAlias.enableAntiAlias(AntiAlias.get());

        // Create the message area
        logpane = OurUtil.scrollpane(null);
        log = new SwingLogPanel(logpane, fontName, fontSize, Color.WHITE, Color.BLACK, new Color(.7f, .2f, .2f), this);

        // Create the text area
        text = new OurTabbedSyntaxWidget(fontName, fontSize, TabSize.get());
        text.listeners.add(this);
        text.enableSyntax(!SyntaxDisabled.get());

        final JPopupMenu popup = new JPopupMenu();
        // New project menu item
        JMenuItem menuItem = new JMenuItem("Save                         ");
        menuItem.setMnemonic('S');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doSave();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("New");
        menuItem.setMnemonic('N');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doNew();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("Open");
        menuItem.setMnemonic('O');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doOpen();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("Close");
        menuItem.setMnemonic('W');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doClose();
            }
        });
        popup.add(menuItem);

        popup.addSeparator();

        menuItem = new JMenuItem("Copy");
        menuItem.setMnemonic('C');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doCopy();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("Cut");
        menuItem.setMnemonic('X');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doCut();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("Paste");
        menuItem.setMnemonic('V');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doPaste();
            }
        });
        popup.add(menuItem);

        popup.addSeparator();

        menuItem = new JMenuItem("Undo");
        menuItem.setMnemonic('Z');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doUndo();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("Redo");
        menuItem.setMnemonic('Y');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doRedo();
            }
        });
        popup.add(menuItem);

        menuItem = new JMenuItem("Find");
        menuItem.setMnemonic('F');
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doFind();
            }
        });
        popup.add(menuItem);


        //create jpane for reporting aunit execution results.
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        tabBar = new JPanel();
        BoxLayout hor = new BoxLayout(tabBar, BoxLayout.X_AXIS);
        tabBar.setLayout(hor);
        tabBarScroller = new JScrollPane(tabBar, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsPanel.add(tabBarScroller, BorderLayout.NORTH);

        //make tabs
        JLabel logLabel = AUnitGuiUtil.makeLabel("Log  ", "images/log.png");
        logTab = AUnitGuiUtil.makeTab(logLabel, OurUtil.loadIcon("images/log.png"), OurUtil.loadIcon("images/logfaded.png"));

        aunitLabel = AUnitGuiUtil.makeLabel("AUnit Results  ", "images/aunitresultsfaded.png");
        aunitLabel.setForeground(medium_gray);
        aunitTab = AUnitGuiUtil.makeTab(aunitLabel, OurUtil.loadIcon("images/aunitresults.png"), OurUtil.loadIcon("images/aunitresultsfaded.png"));

        JLabel coverageLabel = AUnitGuiUtil.makeLabel("Coverage Results  ", "images/coverage.png");
        coverageTab = AUnitGuiUtil.makeTab(coverageLabel, OurUtil.loadIcon("images/coverage.png"), OurUtil.loadIcon("images/coveragefaded.png"));
        coverageLabel.setForeground(medium_gray);

        JLabel muAlloyLabel = AUnitGuiUtil.makeLabel("MuAlloy Results  ", "images/mutationtab.png");
        mualloyTab = AUnitGuiUtil.makeTab(muAlloyLabel, OurUtil.loadIcon("images/mutationtab.png"), OurUtil.loadIcon("images/mutationtabfaded.png"));
        muAlloyLabel.setForeground(medium_gray);

        JLabel alloyFLLabel = AUnitGuiUtil.makeLabel("AlloyFL Results  ", "images/fltab.png");
        alloyflTab = AUnitGuiUtil.makeTab(alloyFLLabel, OurUtil.loadIcon("images/fltab.png"), OurUtil.loadIcon("images/fltabfaded.png"));
        alloyFLLabel.setForeground(medium_gray);

        JLabel aRepairLabel = AUnitGuiUtil.makeLabel("ARepair Results  ", "images/arepair.png");
        arepairTab = AUnitGuiUtil.makeTab(aRepairLabel, OurUtil.loadIcon("images/arepair.png"), OurUtil.loadIcon("images/arepairtabfaded.png"));
        aRepairLabel.setForeground(medium_gray);

        // Add everything to the frame, then display the frame

        BoxLayout boxLayout = new BoxLayout(all, BoxLayout.Y_AXIS);
        all.setLayout(boxLayout);
        all.removeAll();
        lefthalf = new JPanel();
        lefthalf.setLayout(new BorderLayout());

        text.addTo(lefthalf, BorderLayout.CENTER);
        splitpane = OurUtil.splitpane(JSplitPane.HORIZONTAL_SPLIT, lefthalf, resultsPanel, width / 2);
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
                        g.setColor(border_gray);
                        g.fillRect(getSize().width - 1, 0, 1, getSize().height);
                        super.paint(g);
                    }
                };
            }
        });
        splitpane.setBorder(new MatteBorder(0, 1, 1, 1, border_gray));
        status = OurUtil.make(OurAntiAlias.label(" "), new Font(fontName, Font.PLAIN, fontSize), Color.BLACK, background);
        status.setBorder(new OurBorder(true, false, false, false));

        // add mouse listener
        text.addMouseListener(popup);

        GridBagLayout gridbag = new GridBagLayout();
        aunitText = new JPanel();
        aunitText.setLayout(gridbag);
        aunitText.setBackground(Color.WHITE);
        aunitDisplay = new JScrollPane(aunitText);
        aunitDisplay.getVerticalScrollBar().setUnitIncrement(16);
        aunitDisplay.setBackground(Color.WHITE);

        GridBagLayout covGridbag = new GridBagLayout();
        coverageText = new JPanel();
        coverageText.setBackground(Color.WHITE);
        coverageText.setLayout(covGridbag);
        coverageDisplay = new JScrollPane(coverageText);
        coverageDisplay.getVerticalScrollBar().setUnitIncrement(16);
        coverageDisplay.setBackground(Color.WHITE);

        GridBagLayout mualloyGridbag = new GridBagLayout();
        mualloyText = new JPanel();
        mualloyText.setBackground(Color.WHITE);
        mualloyText.setLayout(mualloyGridbag);
        mualloyDisplay = new JScrollPane(mualloyText);
        mualloyDisplay.getVerticalScrollBar().setUnitIncrement(16);
        mualloyDisplay.setBackground(Color.WHITE);

        GridBagLayout flGridbag = new GridBagLayout();
        alloyflText = new JPanel();
        alloyflText.setBackground(Color.WHITE);
        alloyflText.setLayout(flGridbag);
        alloyflDisplay = new JScrollPane(alloyflText);
        alloyflDisplay.getVerticalScrollBar().setUnitIncrement(16);
        alloyflDisplay.setBackground(Color.WHITE);

        GridBagLayout reapirGridbag = new GridBagLayout();
        arepairText = new JPanel();
        arepairText.setBackground(Color.WHITE);
        arepairText.setLayout(reapirGridbag);
        arepairDisplay = new JScrollPane(arepairText);
        arepairDisplay.getVerticalScrollBar().setUnitIncrement(16);
        arepairDisplay.setBackground(Color.WHITE);

        storeTB.setAlignmentX(0);
        all.add(storeTB);
        all.add(splitpane);
        all.add(status);

        resultsPanel.add(logpane, BorderLayout.CENTER);

        logTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(logpane, BorderLayout.CENTER);
                logTab.setActive();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logTab.hover();
                logTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logTab.unhover();
                logTab.repaint();
            }
        });

        logLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(logpane, BorderLayout.CENTER);
                logTab.setActive();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logTab.hover();
                logTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logTab.unhover();
                logTab.repaint();
            }
        });

        aunitTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(aunitDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.setActive();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                aunitTab.hover();
                aunitTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                aunitTab.unhover();
                aunitTab.repaint();
            }
        });

        aunitLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(aunitDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.setActive();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                aunitTab.hover();
                aunitTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                aunitTab.unhover();
                aunitTab.repaint();
            }
        });

        coverageTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(coverageDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.setActive();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                coverageTab.hover();
                coverageTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                coverageTab.unhover();
                coverageTab.repaint();
            }
        });

        coverageLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(coverageDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.setActive();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                coverageTab.hover();
                coverageTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                coverageTab.unhover();
                coverageTab.repaint();
            }
        });
        mualloyTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(mualloyDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.setActive();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mualloyTab.hover();
                mualloyTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mualloyTab.unhover();
                mualloyTab.repaint();
            }
        });

        muAlloyLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(mualloyDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.setActive();
                alloyflTab.deactivate();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mualloyTab.hover();
                mualloyTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mualloyTab.unhover();
                mualloyTab.repaint();
            }
        });

        alloyflTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(alloyflDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.setActive();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                alloyflTab.hover();
                alloyflTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                alloyflTab.unhover();
                alloyflTab.repaint();
            }
        });

        alloyFLLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(alloyflDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.setActive();
                arepairTab.deactivate();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                alloyflTab.hover();
                alloyflTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                alloyflTab.unhover();
                alloyflTab.repaint();
            }
        });

        arepairTab.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(arepairDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.setActive();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                arepairTab.hover();
                arepairTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                arepairTab.unhover();
                arepairTab.repaint();
            }
        });

        aRepairLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                resultsPanel.removeAll();
                resultsPanel.add(tabBarScroller, BorderLayout.NORTH);
                resultsPanel.add(arepairDisplay, BorderLayout.CENTER);
                logTab.deactivate();
                aunitTab.deactivate();
                coverageTab.deactivate();
                mualloyTab.deactivate();
                alloyflTab.deactivate();
                arepairTab.setActive();
                resultsPanel.updateUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                arepairTab.hover();
                arepairTab.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                arepairTab.unhover();
                arepairTab.repaint();
            }
        });

        tabBar.setAlignmentX(1.0f);
        tabBar.setAlignmentY(1.0f);

        tabBar.add(logTab);
        tabBar.add(aunitTab);
        tabBar.add(coverageTab);
        tabBar.add(mualloyTab);
        tabBar.add(alloyflTab);
        tabBar.add(arepairTab);

        tabBarScroller.setBorder(new MatteBorder(1, 0, 1, 0, border_gray));
        logTab.setActive();
        aunitTab.deactivate();
        coverageTab.deactivate();
        mualloyTab.deactivate();
        alloyflTab.deactivate();
        arepairTab.deactivate();

        aunitDisplay.setBorder(null);
        coverageDisplay.setBorder(null);
        mualloyDisplay.setBorder(null);
        alloyflDisplay.setBorder(null);
        arepairDisplay.setBorder(null);

        // Generate some informative log messages
        log.logBold("Alloy Analyzer Plus IDE v.1.0\n\n");


        // If on Mac, then register an application listener
        try {
            wrap = true;
            if (Util.onMac()) {
                macUtil.registerApplicationListener(doShow(), doAbout(), doOpenFile(""), doQuit());
            }
        } catch (Throwable t) {
            System.out.println("Mac classes not there");
        } finally {
            wrap = false;
        }

        // Add the new JNI location to the java.library.path
        try {
            System.setProperty("java.library.path", binary);
            // The above line is actually useless on Sun JDK/JRE (see Sun's bug
            // ID 4280189)
            // The following 4 lines should work for Sun's JDK/JRE (though they
            // probably won't work for others)
            String[] newarray = new String[] {
                                              binary
            };
            java.lang.reflect.Field old = ClassLoader.class.getDeclaredField("usr_paths");
            old.setAccessible(true);
            old.set(null, newarray);
        } catch (Throwable ex) {
        }

        // Pre-load the preferences dialog
        prefDialog = new PreferencesDialog(log, binary);
        prefDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        try {
            wrap = true;
            prefDialog.addChangeListener(wrapToChangeListener(doOptRefreshFont()), FontName, FontSize, TabSize);
            prefDialog.addChangeListener(wrapToChangeListener(doOptAntiAlias()), AntiAlias);
            prefDialog.addChangeListener(wrapToChangeListener(doOptSyntaxHighlighting()), SyntaxDisabled);
            prefDialog.addChangeListener(wrapToChangeListener(doLookAndFeel()), LAF);
        } finally {
            wrap = false;
        }

        // If the temporary directory has become too big, then tell the user
        // they can "clear temporary directory".
        long space = computeTemporarySpaceUsed();
        if (space < 0 || space >= 20 * 1024768) {
            if (space < 0)
                log.logBold("Warning: Alloy4's temporary directory has exceeded 1024M.\n");
            else
                log.logBold("Warning: Alloy4's temporary directory now uses " + (space / 1024768) + "M.\n");
            log.log("To clear the temporary directory,\n" + "go to the File menu and click \"Clear Temporary Directory\"\n");
            log.logDivider();
            log.flush();
        }

        // Refreshes all the menu items
        doRefreshFile();
        OurUtil.enableAll(filemenu);
        doRefreshEdit();
        OurUtil.enableAll(editmenu);
        doRefreshRun();
        OurUtil.enableAll(runmenu);
        doRefreshOption();
        doRefreshWindow(false);
        OurUtil.enableAll(windowmenu);
        doRefreshAUnit();
        OurUtil.enableAll(aunitMenu); /** AUnit **/
        doRefreshMuAlloy();
        OurUtil.enableAll(mualloyMenu); /** MuAlloy **/
        doRefreshAlloyFL();
        OurUtil.enableAll(alloyFLMenu); /** AlloyFL **/
        doRefreshARepair();
        OurUtil.enableAll(arepairMenu); /** ARepair **/
        frame.setJMenuBar(bar);

        // Open the given file, if a filename is given in the command line
        for (String f : args)
            if (f.toLowerCase(Locale.US).endsWith(".als")) {
                File file = new File(f);
                if (file.exists() && file.isFile())
                    doOpenFile(file.getPath());
            }

        // Update the title and status bar
        notifyChange();
        text.get().requestFocusInWindow();
        changeFont(text.getComponent());

        // Launch the welcome screen if needed
        if (!AlloyCore.isDebug() && Welcome.get()) {
            JCheckBox again = new JCheckBox("Show this message every time you start the Alloy Analyzer Plus");
            again.setSelected(true);
            OurDialog.showmsg("Welcome", "Thank you for using the Alloy Analyzer Plus IDE with AUnit verification features. This is an extension of the base Alloy Analyzer available at alloytools.org.", again);
            doShow();
            Welcome.set(again.isSelected());
        }

    }

    /** {@inheritDoc} */
    @Override
    public Object do_action(Object sender, Event e) {
        if (sender instanceof OurTabbedSyntaxWidget)
            switch (e) {
                case FOCUSED :
                    notifyFocusGained();
                    break;
                case STATUS_CHANGE :
                    notifyChange();
                    break;
                default :
                    break;
            }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Object do_action(Object sender, Event e, Object arg) {
        if (sender instanceof OurTree && e == Event.CLICK && arg instanceof Browsable) {
            Pos p = ((Browsable) arg).pos();
            if (p == Pos.UNKNOWN)
                p = ((Browsable) arg).span();
            text.shade(p);
        }
        return true;
    }

    /**
     * Creates menu items from boolean preferences (<code>prefs</code>) and adds
     * them to a given parent menu (<code>parent</code>).
     */
    private void addToMenu(JMenu parent, BooleanPref... prefs) {
        for (BooleanPref pref : prefs) {
            Action action = pref.getTitleAction();
            Object name = action.getValue(Action.NAME);
            menuItem(parent, name + ": " + (pref.get() ? "Yes" : "No"), action);
        }
    }

    /**
     * Creates a menu item for each choice preference (from <code>prefs</code>) and
     * adds it to a given parent menu (<code>parent</code>).
     */
    @SuppressWarnings({
                       "rawtypes", "unchecked"
    } )
    private JMenu addToMenu(JMenu parent, ChoicePref... prefs) {
        JMenu last = null;
        for (ChoicePref pref : prefs) {
            last = new JMenu(pref.title + ": " + pref.renderValueShort(pref.get()));
            addSubmenuItems(last, pref);
            parent.add(last);
        }
        return last;
    }

    /**
     * Creates a sub-menu item for each choice of a given preference
     * (<code>pref</code>) and adds it to a given parent menu (<code>parent</code>).
     */
    @SuppressWarnings({
                       "rawtypes", "unchecked"
    } )
    private void addSubmenuItems(JMenu parent, ChoicePref pref) {
        Object selected = pref.get();
        for (Object item : pref.validChoices()) {
            Action action = pref.getAction(item);
            menuItem(parent, pref.renderValueLong(item).toString(), action, item == selected ? iconYes : iconNo);
        }
    }

    /**
     * Takes a <code>Runner</code> and wraps it into a <code>ChangeListener</code>
     */
    private static ChangeListener wrapToChangeListener(final Runner r) {
        assert r != null;
        return new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                r.run();
            }
        };
    }
}
