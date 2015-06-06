
package dk.citygates.entitys;

/**
 *
 * @author Steven Hermans
 */
public interface Animatable {
    
    /**
     * Open the gate
     */
    public void open();
    
    /**
     * Close the gate
     */
    public void close();
    
    /**
     * Check if the gate is open
     * @return true if the gate is open, otherwise false
     */
    public boolean isOpen();
    
    /**
     * Get the world the gate is in
     * @return World name
     */
    public String getWorld();
    
}
