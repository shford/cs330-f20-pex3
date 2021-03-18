/**
 * DemoGUI demonstrates a reactive GUI that controls user actions
 *
 * @author C2C Hampton Ford
 *
 * Documentation:
 *  CS330 LSNS 17, 19, 20, 21, and 22 (both slides and template file)
 *  Java Concurrency Tutorial https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
 *  Cast array to set https://stackoverflow.com/questions/3064423/how-to-convert-an-array-to-a-set-in-java
 *  Provided code by my professor, Dr. Hadfield
 *  Using ComboBox https://www.codejava.net/java-se/swing/jcombobox-basic-tutorial-and-examples
 *  Access arrayList https://www.w3resource.com/java-tutorial/arraylist/arraylist_get.php
 *  https://stackoverflow.com/questions/27404579/java-jframe-access-an-object-instance-when-an-event-is-triggered
 *  Reading from JTextFields https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwj896Pg4tTsAhXydc0KHTnUBUMQFjABegQIBRAC&url=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F36936186%2Fhow-to-get-string-from-jtextfield-and-save-it-in-variable&usg=AOvVaw1oaQtCy5Fu8qsxaS3gMSKr
 */

/*
 * Known Issues:
 *  Cannot open both windows at once.
 *  *   Everything to worked when individually tested. I tried
 *  *   every combination of threading and executors I could
 *  *   think of and never could get both to open at once.
 *
 *  To Run:
 *      Run the program. It'll open the game. ****When you left click to exit
 *      the game, you'll see the GUI pop up****. From there you can adjust
 *      whatever you like.
 *
 *      Every button is bound to an event listener as you can see.
 *
 *      Note: AddFlock, EditFlock, and NonFlockOperation Buttons
 *            fail b/c the game is no longer running however their binding
 *            functions tested fine.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DemoGUI {
    //Create Object
    private static FlockingSimulation fs;

    //JPanel Setup
    private JPanel panelMain;
    private JPanel NonFlockOperations;
    private JPanel FlockOperations;
    private JPanel MenuAndActions;
    private JPanel AttrChanges;
    private JPanel AddEdit;
    private JPanel Weight;

    //NonFlockOperations
    private JButton EdgeModeBtn;
    private JButton PauseBtn;
    private JButton ResetBtn;
    private JButton ExitBtn;

    private JButton AddFlock;
    private JButton EditFlock;
    private JButton DeleteFlock;
    private JButton SetFlockWeights;

    //Flock Operations will require an id from FlockDropdown
    private String[] flockDropdownCurrStr = new String[1000]; //assume less than 1,000 flock names
    private JComboBox<String> FlockDropdown;

    //AddEdit
    private JTextField NameField;
    private JSlider CountSlider;
    private JSlider SizeSlider;
    private JSlider SpeedSlider;
    private JSlider AlignRadiusSlider;
    private JSlider CohRadiusSlider;
    private JSlider SepRadiusSlider;
    private JComboBox<String> ColorDropdown;

    //Weight
    private JSlider VelocityWeight;
    private JSlider SeparationWeight;
    private JSlider AlignmentWeight;
    private JSlider CohesionWeight;

    //Labels
    private JLabel SelectFlockLabel;
    private JLabel NonFlockTitle;
    private JLabel NameLabel;
    private JLabel CountLabel;
    private JLabel SizeLabel;
    private JLabel SpeedLabel;
    private JLabel AlignRadLabel;
    private JLabel CohRadLabel;
    private JLabel SepRadLabel;
    private JLabel ColorLabel;
    private JLabel AddEditContainerLabel;
    private JLabel FlockingContainerLabel;
    private JLabel VelocityWLabel;
    private JLabel SeparationWLabel;
    private JLabel AlignmentWLabel;
    private JLabel CohesionWLabel;
    private JLabel labelResults;

    /**
     * Constructor that sets up button listeners and their actions
     */
    public DemoGUI() {
        /* Non Flock Button Constructors first */
        EdgeModeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fs.switchEdgeMode();
            }
        });

        PauseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //pause game until btn pushed again
                fs.setPause(!fs.getPause());
            }
        });

        ResetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //reset game w/ new random Boid locations and rand velocities (incl rand directions)
                //easiest way is to closeWindow() and rerun game returning the new object
                //as this will reset everything w/ the default constructor
                fs.close();
                fs.activate();
            }
        });

        ExitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //exit cleanly
                fs.close();
            }
        });

        /*
         * Flock Menu And Actions
         * All these functions will 1) read in values and 2) modify existing attributes
         */
        AddFlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Read Required Data */
                String name = NameField.getText();
                String strColor = (String) ColorDropdown.getSelectedItem();
                Color c = castColor(strColor);
                int size = SizeSlider.getValue();
                int speed = SpeedSlider.getValue();
                int count = CountSlider.getValue();
                /* Create new flock AND Update FlockDropdown list*/
                if (hasUniqueName(name)) {
                    fs.addFlock(name, count, c, size, speed);
                    FlockDropdown.addItem(name);
                }
                //else do nothing w/out notifying the user
            }
        });

        EditFlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Read in Data */
                int flockId = FlockDropdown.getSelectedIndex();
                String strColor = (String) ColorDropdown.getSelectedItem();
                Color c = castColor(strColor);
                int size = SizeSlider.getValue();
                int speed = SpeedSlider.getValue();
                int alignRadius = AlignRadiusSlider.getValue();
                int cohRadius = CohRadiusSlider.getValue();
                int sepRadius = SepRadiusSlider.getValue();
                /* Edit the specified flock */
                fs.editFlock(flockId, c, size, speed, alignRadius, cohRadius, sepRadius);
            }
        });

        DeleteFlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Read Required Data */
                int flockId = FlockDropdown.getSelectedIndex();
                //Delete selected flock
                fs.delFlock(flockId);
                /* Update FlockDropdown list */
                FlockDropdown.removeItemAt(flockId);
            }
        });

        SetFlockWeights.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Read in weights */
                int flockId = FlockDropdown.getSelectedIndex();
                double velocityWeight = (double)VelocityWeight.getValue()/10;
                double separationWeight = (double)SeparationWeight.getValue()/10;
                double alignmentWeight = (double)AlignmentWeight.getValue()/10;
                double cohesionWeight = (double)CohesionWeight.getValue()/10;
                /* Set new weight values */
                fs.setNewWeights(flockId, velocityWeight, separationWeight, alignmentWeight, cohesionWeight);
            }
        });
    }

    /**
     * main() creates the JFrame, instantiates a DemoGUI, sets up the JFrame,
     * enables the initial controls, and makes the GUI visible.
     *
     * @param args default args to main
     */
    public static void main(String[] args) {
//Concurrency Attempt 1
        fs = new FlockingSimulation();
        fs.activate();
        fs.waitUntilInitialized();

        JFrame frame = new JFrame("Flock Simulation GUI");
        DemoGUI thisGUI = new DemoGUI();
        frame.setContentPane(thisGUI.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 650);
        thisGUI.initializeDropdownBoxes();
        frame.setVisible(true);

//Concurrency Attempt 2
/*      fs = new FlockingSimulation();
        fs.activate();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Flock Simulation GUI");
                DemoGUI thisGUI = new DemoGUI();
                frame.setContentPane(thisGUI.panelMain);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setSize(900, 650);
                thisGUI.initializeDropdownBoxes();
                frame.setVisible(true);
            }
        } );*/

//Concurrency Attempt 3
/*        Runnable runGame = () -> {
            fs = new FlockingSimulation();
            fs.activate();
        };
        runGame.run();

        Runnable runGUI = () -> {
            JFrame frame = new JFrame("Flock Simulation GUI");
            DemoGUI thisGUI = new DemoGUI();
            frame.setContentPane(thisGUI.panelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setSize(900, 650);
            thisGUI.initializeDropdownBoxes();
            frame.setVisible(true);
        };
        runGUI.run();*/

//Concurrency Attempt 4
        /*        Runnable runGame = () -> {
            fs = new FlockingSimulation();
            fs.activate();
        };
        runGame.run();

        Runnable runGUI = () -> {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Flock Simulation GUI");
                DemoGUI thisGUI = new DemoGUI();
                frame.setContentPane(thisGUI.panelMain);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setSize(900, 650);
                thisGUI.initializeDropdownBoxes();
                frame.setVisible(true);
            }
        } );
        };
        runGUI.run();*/
    }

    /**
     * castColor() - convert return strings to colors
     * @param color the color as a string
     * @return the color as a Color
     */
    public Color castColor(String color) {
        if ("Red".equals(color)) {
            return Color.RED;
        }
        else if ("Blue".equals(color)) {
            return Color.BLUE;
        }
        else if ("Black".equals(color)) {
            return Color.BLACK;
        }
        else if ("White".equals(color)) {
            return Color.WHITE;
        }
        else {
            return Color.CYAN; //in case something really weird happens
        }
    }

    /**
     * hasUniqueName() - checks for a unique flock name
     * @param name attempted flock name
     * @return boolean true if attempted flock name is unique
     */
    public boolean hasUniqueName(String name) {
        Set<String> currentNames = Set.of(flockDropdownCurrStr);
        return !currentNames.contains(name);
    }

    /**
     * initialFlockDropdownSetup() - add items to dropdown boxes (ComboBoxes)
     */
    public void initializeDropdownBoxes() {
        /* Initialize FlockDropdown */
        String[] names = fs.getFlockNames();
        for (String s : names) {
            FlockDropdown.addItem(s);
        }

        /* Initialize ColorDropdown */
        String[] colors = new String[] {"Red", "Blue", "Black", "White"};
        for (String c : colors) {
            ColorDropdown.addItem(c);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
