/*
 * ExceptionType.java
 *
 * Created on 23 August 2003, 16:37
 */

package uk.ac.derby.GameEngine2D;

/**
 * This will be thrown by any illegal action.
 *
 * @author  Dave Voorhis
 */
public class Violation extends RuntimeException {
	
	static final long serialVersionUID = 0;
	
    /** Creates a new instance of Violation */
    public Violation(String description) {
        super(description);
    }
    
}
