import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**************************************************************
 * Boid class provides the single entity level for members of
 * the flocks.
 *
 * @author Steven.Hadfield
 */
class Boid {

    // instance attributes
    private MoveMode movement = MoveMode.WRAP;
    private Vector330Class location;
    private Vector330Class velocity;
    private Vector330Class newVelocity;  // new flocking motion is built here based on current velocities of others

    private Color color = Color.BLUE;
    private BufferedImage image = null; // provides an option for displaying an image for the Boid
    private int size = 10;
    private double speed = 5.0;

    // static attributes

    private static DrawingPanel panel = null;
    private static Graphics2D g = null;
    private static int screenWidth = 0;     // note, this is the width of the entire frame including borders
    private static int screenHeight = 0;    // and this is the height of the entire frame including borders and title
    private static Random rand = new Random( System.currentTimeMillis());

    // constructors

    /****************************************************************************************************************
     * Base zero-argument constructor that creates the Boid with a random location and a random 360 deg velocity
     *
     * @throws Exception Throws and exception if the DrawingPanel has not yet been set
     */
    Boid() throws Exception {

        // make sure the Boid class knows about the encompassing DrawingPanel

        if (panel == null) {
            throw new Exception("Boid Class Error: Must set static DrawingPanel attribute before creating Boids");
        } else {

            // set a random position vector based upon window size

            double x = ((screenWidth - (4 * this.speed)) * rand.nextDouble()) + (2 * this.speed);
            double y = ((screenHeight - (4 * this.speed)) * rand.nextDouble()) + (2 * this.speed);
            this.location = new Vector330Class( x, y );

            // set the velocity vector based upon species speed and a random direction

            double direction = 2.0 * Math.PI * rand.nextDouble();
            this.velocity = (new Vector330Class( Math.cos( direction ), Math.sin( direction ) )).scale(this.speed);
        }
    }

    // setters

    /*************************************************************************
     * Updates this Boid's location based upon the location vector passed in
     * @param l new location vector passed in
     */
    void setLocation( Vector330Class l ) {
        this.location.setX( l.getX() );
        this.location.setY( l.getY() );
    }

    /************************************************************************
     * Updates this Boid's velocity based upon the velocity vector passed in
     * @param v new velocity vector passed in
     */
    void setVelocity( Vector330Class v ) {
        this.velocity.setX( v.getX() );
        this.velocity.setY( v.getY() );
    }

    /************************************************************************
     * Sets the new velocity vector based upon the one provided (via alias)
     * @param nv new velocity vector passed in
     */
    void setNewVelocity( Vector330Class nv ) {
        this.newVelocity = nv;
    }

    /***********************************************************************
     * Sets the current velocity to the new velocity vector; this allows
     * the new velocity calculation to be based upon current velocities
     * of the other Boids.  Once all of the Boids have a new velocity,
     * we can use this method to make their current velocity the new velocity
     */
    void updateVelocity() {
        if ( this.newVelocity != null ) {
            this.velocity.setX( this.newVelocity.getX() );
            this.velocity.setY( this.newVelocity.getY() );
        }
    }

    /***************************
     * Set the Boid's color
     * @param color new color
     */
    void setColor( Color color ) {
        this.color = color;
    }

    /***************************************
     * Update the Boid's image for display
     * @param image new image for the Boid
     */
    void setImage( BufferedImage image ) {
        this.image = image;
    }

    /***************************
     * Change the Boid's size
     * @param size new size for the Boid
     */
    void setSize( int size ) {
        this.size = size;
    }

    /***************************
     * Change the Boid's speed
     * @param speed new speed
     */
    void setSpeed( double speed ) {
        this.speed = speed;
    }

    public void setMovementMode(MoveMode mode) {
        this.movement = mode;
    }

    // getters
    Vector330Class getLocation() {
        return this.location;
    }
    Vector330Class getVelocity() {
        return this.velocity;
    }
    double getSpeed() { return this.speed; }
    MoveMode getMovementMode() {
        return movement;
    }

    // static setters

    /******************************************************************************************************
     * Sets an association of the Boid class to the DrawingPanel that it will reside in
     * @param panel the new DrawingPanel object
     * @param width width of the DrawingPanel
     * @param height height of the DrawingPanel
     * @throws IllegalAccessException throws exception if a null pointer is passed in for the DrawingPanel
     */
    static void setDrawingPanel( DrawingPanel panel, int width, int height ) throws IllegalAccessException {
        if (panel == null) {
            throw new IllegalAccessException("Boid DrawingPanel cannot be null");
        } else {
            Boid.panel = panel;
            g = panel.getGraphics();
            screenWidth = width;
            screenHeight = height;
        }
    }

    // other public methods

    /*****************************************************************************
     * Moves the Boid per its current velocity vector with either bouncing or
     * wrapping behavior on the edges
     */
    void move() {

        // Update location using velocity

        this.location.sumTo( this.velocity );

        // wrapping behavior for the screen edges
        if (this.movement == MoveMode.WRAP) {
            if (this.location.getX() < 0.0) {
                this.location.setX(screenWidth);
            }
            if (this.location.getX() > screenWidth) {
                this.location.setX(0.0);
            }

            if (this.location.getY() < 0.0) {
                this.location.setY(screenHeight);
            }
            if (this.location.getY() > screenHeight) {
                this.location.setY(0.0);
            }
        }
        //Bounce
        else {
            // Bounce off of left and right
            if ((this.location.getX() < size / 2.0) || (this.location.getX() > (double) screenWidth - (size / 2.0))) {
                this.velocity.setX(-1.0 * this.velocity.getX());
            }
            // Bounce off of top and bottom
            if ((this.location.getY() < size / 2.0) || (this.location.getY() > (double) screenHeight - (size / 2.0))) {
                this.velocity.setY(-1.0 * this.velocity.getY());
            }
        }
    } // end move()

    /*****************************************************************************************************************
     * Move the Boid away from the (x,y) disruption position passed in so that the Boid is the evadeRadius away
     * from the disruption point.
     * @param x x coordinate of the disruption point
     * @param y y coordinate of the disruption point
     * @param evadeRadius distance used to detect if evasion is needed and to determine how far away to move
     */
    void evade( int x, int y, int evadeRadius ) {

        // create a vector for the disruption location

        Vector330Class disruptionLocationVector = new Vector330Class( x, y );

        // move directly away from the disruption point

        Vector330Class evadeVector = this.getLocation().subtract( disruptionLocationVector);

        // if this Boid is within the evadeRadius of the disruption, have it move directly away from the disruption
        // so that it is the evadeRadius away from the disruption.

        if ( evadeVector.magnitude() < evadeRadius ) {
            double distanceToDisruption = evadeVector.magnitude();
            this.setLocation( this.getLocation().add( evadeVector.normalize().scale(evadeRadius-distanceToDisruption)));

            // don't let the boid get scared off of the screen

            if (this.getLocation().getX() < 0) this.getLocation().setX(0);
            if (this.getLocation().getX() > Boid.screenWidth) this.getLocation().setX(Boid.screenWidth-1);
            if (this.getLocation().getY() < 0) this.getLocation().setY(0);
            if (this.getLocation().getY() > Boid.screenHeight) this.getLocation().setY(Boid.screenHeight-1);

        }
    }

    /***********************************************************************************************************
     * Draws the Boid at its current location on the DrawingPanel
     * @throws Exception throws and exception if the Boid class does not have a Graphics2D object to draw upon
     */
    void draw() throws Exception {

        // make sure we have a place (Graphics2D object) to draw on

        if (g == null) {
            throw new Exception("ERROR: Boid draw() does not have a DrawingPanel set.");
        } else {

            // if there is no image to draw, use a circle based upon the size and color attributes

            if (image == null) {
                g.setColor( color );
                g.fillOval( (int) this.location.getX() - (size/2),
                            (int) this.location.getY() - (size/2), size, size );

                // use the velocity to provide a heading for the Boid

                Vector330Class point = this.velocity.normalize().scale( size ).add( this.location );
                g.drawLine( this.location.getXint(), this.location.getYint(),
                            point.getXint(), point.getYint() );

            } else {
                // TBD: Add code to draw an image
            }

        }
    }
}