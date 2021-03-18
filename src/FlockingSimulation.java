import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/*******************************************************************************************************************
 * Top level class for the Flocking Simulation with the activate() method to run the simulation
 *
 * @author Steve Hadfield
 */
class FlockingSimulation {

    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final int SCREEN_WIDTH = 1000;
    private final int SCREEN_HEIGHT = 700;
    private DrawingPanel panel;

    private boolean pause = false;
    ArrayList<Flock> flocks;

    /**
     * Zero argument constructor for the FlockingSimulation
     */
    FlockingSimulation() {
    }

    /***************************************************************************************************************
     * Sets up the DrawingPanel, creates the flocks, and runs the animation to include pause/resume via spacebar,
     * disruptions (via a left mouse click), and exits with a right mouse click
     */
    void activate() {
        try {

            // set up the DrawingPanel
            panel = new DrawingPanel(SCREEN_WIDTH, SCREEN_HEIGHT);

            Boid.setDrawingPanel(panel, SCREEN_WIDTH, SCREEN_HEIGHT);
            Graphics2D g = panel.getGraphics();

            panel.setWindowTitle("Flocking Simulation - Spacebar to pause/resume, " +
                "Left Click to Disrupt, Right Click to Terminate - ");

            panel.setBackground(BACKGROUND_COLOR);

            // Use an ArrayList of flocks so that it can flex to however many flocks there are

            flocks = new ArrayList<>();

            flocks.add(new Flock("Birds", 30));
            flocks.add(new Flock("Raptors", 10, Color.RED, 15, 15));

            for (Flock f : flocks) {   // draw the initial locations of the flocks
                f.draw();
            }

            panel.copyGraphicsToScreen();   // show the initial window with flocks

            // the animation loop

            while (!panel.mouseClickHasOccurred(DrawingPanel.RIGHT_BUTTON)) {

                panel.setWindowTitle("Flocking Simulation - Spacebar to pause/resume, " +
                    "Left Click to Disrupt, Right Click to Terminate - ");

                // if the spacebar is pressed, toggle the pause between pause and resume

                if (panel.keyHasBeenHit((int) ' ')) {
                    pause = !pause;
                }

                // if not paused, step the animation

                if (!pause) {
                    panel.setBackground(BACKGROUND_COLOR);  // clear the window

                    // check for a disruption

                    if (panel.mouseClickHasOccurred(DrawingPanel.LEFT_BUTTON)) {
                        int x = panel.getMouseClickX(DrawingPanel.LEFT_BUTTON);
                        int y = panel.getMouseClickY(DrawingPanel.LEFT_BUTTON);

                        // evade the flocks and then draw them

                        for (Flock f : flocks) {
                            f.evade(x, y);
                            f.draw();
                        }
                    } else {

                        // otherwise just move and draw the flocks

                        for (Flock f : flocks) {
                            f.move();
                            f.draw();
                        }
                    }
                    panel.copyGraphicsToScreen();  // update the animation display
                }
                panel.sleep(100); // delay between the animation steps

            } // end while loop

            panel.closeWindow();  // all done, close the window

        } catch (Exception e) { // handle any exceptions that might occur
            System.out.println("FlockingSimulation Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /* DemoGUI support function */

    /**
     * getPause() - get the boolean value of pause
     * @return boolean the current value of pause
     */
    public boolean getPause() {
        return pause;
    }

    /**
     * setPause() - set the boolean value of pause
     * @param pause boolean pause value
     */
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    /**
     * close() - close the window
     */
    public void close() {
        panel.closeWindow();
    }

    /**
     * switchEdgeMode() - Change the edge mode for each boid
     */
    public void switchEdgeMode() {
        //for each Flock, for each Boid, toggle the edge mode
        for (Flock f : flocks) {
            f.chgEdgeMode();
        }
    }

    /**
     * addFlock() - add a flock
     * @param name name of flock
     * @param count number in flock
     * @param c color of flock
     * @param size size of each member of flock
     * @param speed speed of flock
     */
    public void addFlock(String name, int count, Color c, int size, int speed) {
        try {
            flocks.add(new Flock(name, count, c, size, speed));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * editFlock() - Updates the following attributes for the specified flock
     * @param flockId flock to be editted
     * @param c color
     * @param size size
     * @param speed speed
     * @param alignRad alignment radius
     * @param cohRad cohesion radius
     * @param sepRad separation radius
     */
    public void editFlock(int flockId, Color c, int size, int speed, int alignRad, int cohRad, int sepRad) {
        flocks.get(flockId).editThisFlock(c, size, speed, alignRad, cohRad, sepRad);
    }
    /**
     * delFlock() - remove a flock
     * @param flockIndex Index of a specified flock
     */
    public void delFlock(int flockIndex) {
        flocks.remove(flockIndex);
    }

    /**
     * setNewWeights() - sets the new Weighted Flock values
     * @param flockIndex flock to set
     * @param velW current velocity weight
     * @param sepW separation weight
     * @param alignW alignment weight
     * @param cohW cohesion weight
     */
    public void setNewWeights(int flockIndex, double velW, double sepW, double alignW, double cohW) {
        flocks.get(flockIndex).setWeightCurrentVelocity(velW);
        flocks.get(flockIndex).setWeightSeparation(sepW);
        flocks.get(flockIndex).setWeightAlignment(alignW);
        flocks.get(flockIndex).setWeightCohesion(cohW);
    }

    /**
     * getFlockNames() - gets the names of all current flocks
     * @return String[] names of the flocks
     */
    public String[] getFlockNames() {
        String[] names = new String[1000]; //assume less than 1,000 flocks
        for (int i = 0; i < flocks.size(); ++i) {
            names[i] = flocks.get(i).getName(); //add the name of each flock to an array
        }
        return names;
    }

    /**
     * waitUntilInitialized() - wait for initialization
     */
    public void waitUntilInitialized() {
        panel.sleep(100);
    }
}
