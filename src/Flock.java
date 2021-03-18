import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/*******************************************************************************************
 * The Flock class is an aggregation of Boid objects all with similar characteristics
 * to include those that dictate its flocking behaviors of alignment, cohesion, and
 * separation.
 *
 * @author Steven.Hadfield
 *
 */
public class Flock {

    // instance attributes

    private ArrayList<Boid> flock;
    private String name = "";

    private int radiusSeparation = 30;
    private int radiusAlignment = 50;
    private int radiusCohesion = 50;
    private int radiusEvasion = 150;

    private double weightCurrentVelocity = 0.4;
    private double weightSeparation = 0.2;
    private double weightAlignment = 0.2;
    private double weightCohesion = 0.2;

    // constructors

    /**************************************************************************************
     * Zero-argument flock constructor creates a Flock object with the given name and the
     * number of Boid objects controlled by the count argument.
     *
     * @param name - name to be associated with the new Flock object
     * @param count - number of Boid objects to create in the flock
     * @throws Exception - any exception is thrown to the caller
     */
    Flock( String name, int count ) throws Exception {

        // set main Flock attributes

        this.flock = new ArrayList<Boid>();
        this.name = name;

        // populate the flock ArrayList

        for( int i=0; i<count; i++ ) {
            this.flock.add( new Boid());
        }
    } // end Boid() constructor

    /************************************************************************************
     * Flock full-argument constructor setting flock name, count, color, size and speed.
     *
     * @param name  - name of the new flock
     * @param count - number of Boids in the new flock
     * @param color - color of each Boid in the flock
     * @param size  - size of each Boid in the new flock
     * @param speed - speed of each Boid in the new flock
     * @throws Exception    - all exceptions are thrown to the caller
     */
    Flock(String name, int count, Color color, int size, double speed ) throws Exception {

        // call the base constructor

        this( name, count );

        // update color, size, and speed for each member of the flock

        for( Boid b : this.flock) {
            b.setColor( color );
            b.setSize( size );
            b.setSpeed( speed );
        }
    }


    /****************************************************************************************************
     * Creates a new flock with the sepcified parameters.  Key difference here is that Boids are to be
     * represented with the 'image' BufferedImage object instead of a color.
     *
     * @param name String to name the flock
     * @param count number of boids in the flock
     * @param image image to render for each boid in the flock
     * @param size how large each boid is when rendered
     * @param speed how fast these boids move
     * @throws Exception - all exceptions thrown to the caller
     */
    public Flock(String name, int count, BufferedImage image, int size, double speed ) throws Exception {

        // call the base constructor

        this( name, count);

        // update image, size, and speed for each member of the flock

        for( Boid b : this.flock) {
            b.setImage( image );
            b.setSize( size );
            b.setSpeed( speed );
        }
    }

    /* Getter/Setter methods */

    public String getName() {
        return name;
    }

    void setRadiusSeparation( int radiusSeparation ) { this.radiusSeparation = radiusSeparation; }
    void setRadiusAlignment( int radiusAlignment ) { this.radiusAlignment = radiusAlignment; }
    void setRadiusCohesion( int radiusCohesion ) { this.radiusCohesion = radiusCohesion; }
    void setRadiusEvasion( int radiusEvasion ) { this.radiusEvasion = radiusEvasion; }

    void setWeightCurrentVelocity( double weightCurrentVelocity) { this.weightCurrentVelocity = weightCurrentVelocity; }
    void setWeightSeparation( double weightSeparation ) { this.weightSeparation = weightSeparation; }
    void setWeightAlignment( double weightAlignment ) { this.weightAlignment = weightAlignment; }
    void setWeightCohesion( double weightCohesion ) { this.weightCohesion = weightCohesion; }

    // other methods

    /*****************************************************************************************************
     * Moves the flock first by computing a new velocity for each boid in the flock.  Once all the boids
     * have their new velocity, update the velocity and apply it to move each boid.
     */
    void move() {

        // create unit vectors for the current velocity and the three flocking behaviors, summing
        // them by weights into the new velocity which is scaled by the flock's speed.

        for( Boid b : flock ) {
            Vector330Class newVelocity = b.getVelocity().normalize().scale( weightCurrentVelocity );
            newVelocity.sumTo( getSeparationVector( b ).scale( weightSeparation ) );
            newVelocity.sumTo( getAlignmentVector( b ).scale( weightAlignment ) );
            newVelocity.sumTo( getCohesionVector( b ).scale( weightCohesion ) );
            b.setNewVelocity( newVelocity.normalize().scale( b.getSpeed() ) );
        }

        for( Boid b : flock ) {
            b.updateVelocity();
            b.move();
        }
    }

    /**************************************************************************************************
     * Draws each of the Boids in the flock
     *
     * @throws Exception - any exceptions are thrown to the caller
     */
    void draw() throws Exception {

        for( Boid b : flock ) {
            b.draw();
        }
    }

    /**************************************************************************************************
     * Invokes the evade behavior moving each Boid in the flock that is close enough (radiusEvasion)
     * to the disruption point to move directly away from that point
     *
     * @param x - x coordinate of the disruption point
     * @param y - y coordinate of the disfuption point
     */
    void evade( int x, int y ) {

        for( Boid b : flock ) {
            b.evade( x, y, this.radiusEvasion );
        }
    }

    // private helper functions

    /*************************************************************************************
     * Calculates the separation unit vector so the current Boid does not encrotch upon
     * its nearest neighbors (those within radiusSeparation)
     *
     * @param b current boid
     * @return a normalized vector pointing away from the neighbors
     */
    private Vector330Class getSeparationVector( Boid b ) {

        Vector330Class sv = new Vector330Class();

        for( Boid other : this.flock ) {

            if ( distance( b, other ) < radiusSeparation ) {

                Vector330Class difVector = b.getLocation().subtract( other.getLocation()).normalize();

                // closer locations -> more repulsion

                sv.sumTo( difVector.scale( radiusSeparation - difVector.magnitude() ));
            }
        }
        return sv.normalize();
    }

    /*************************************************************************************
     * Calculates the alignment unit vector for Boid b to align itself with near
     * neighbors (within radiusAlignment) of itself
     *
     * @param b current Boid
     * @return a normalized vector in the average direction of Boid b's neighbors
     */
    private Vector330Class getAlignmentVector( Boid b ) {
        Vector330Class av = new Vector330Class();

         for( Boid other : this.flock ) {

            if ( distance( b, other) < radiusAlignment ) {
                av.sumTo( other.getVelocity() );
            }
        }
        return av.normalize();  // will never be <0,0> as b is its own neighbor
    }


    /**************************************************************************************
     * Calculates the average location of the near-neighbors (those within radiusCohesion)
     * and then creates a unit cohesion vector to that point
     *
     * @param b - the current Boid object being considered
     * @return the unit cohesion vector
     */
    private Vector330Class getCohesionVector( Boid b ) {

        Vector330Class cv = new Vector330Class();

        int neighbors = 0;

        for( Boid other : this.flock ) {

            if ( distance( b, other ) < radiusCohesion ) {
                cv.sumTo( other.getLocation() );
                neighbors++;
            }
        }

        Vector330Class avgLocation = cv.scale( 1.0 / (double) neighbors );

        return avgLocation.subtract( b.getLocation() ).normalize();
    }

    /*************************************************************************************
     * Calculates the Eucleadean distance between the locations of Boids a and b.
     *
     * @param a - first Boid
     * @param b - second Boid
     * @return the distance between the locations of Boids a and b
     */
    private double distance( Boid a, Boid b ) {
        return a.getLocation().subtract( b.getLocation() ).magnitude();
    }

    /**
     * chgEdgeMode() - support function for switchEdgeMode()
     */
    public void chgEdgeMode() {
        for (Boid b : this.flock) {
            if (b.getMovementMode() == MoveMode.BOUNCE) {
                b.setMovementMode(MoveMode.WRAP);
            }
            else {
                b.setMovementMode(MoveMode.BOUNCE);
            }
        }
    }

    /**
     * editThisFlock() - support function for editFlock()
     * @param c color
     * @param size size
     * @param speed speed
     * @param alignRad alignment radius
     * @param cohRad cohesion radius
     * @param sepRad separation radius
     */
    public void editThisFlock(Color c, int size, int speed, int alignRad, int cohRad, int sepRad) {
        setRadiusAlignment(alignRad);
        setRadiusCohesion(cohRad);
        setRadiusSeparation(sepRad);
        //Set the following properties for each boid the flock
        for(Boid b : this.flock) {
            b.setColor(c);
            b.setSize(size);
            b.setSpeed(speed);
        }
    }
}  // end of Flock class