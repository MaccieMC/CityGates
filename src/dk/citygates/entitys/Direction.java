
package dk.citygates.entitys;

/**
 *
 * @author Steven Hermans
 */
public enum Direction {

    SOUTH(new String[]{"s", "south", "zuid", "1"}), 
    NORTH(new String[]{"n", "north", "noord", "2"}), 
    EAST(new String[]{"e", "east", "oost", "3"}), 
    WEST(new String[]{"w", "west", "4"}), 
    UP(new String[]{"u", "up", "omhoog", "5"}), 
    DOWN(new String[]{"d", "down", "omlaag", "6"});
    
    private String[] cases;
    
    private Direction(String[] cases){
        this.cases = cases;
    }
    
    public static Direction getDirection(String dir){
        for(Direction d : values()){
            for(String cse : d.cases){
                if(cse.equalsIgnoreCase(dir)){
                    return d;
                }
            }
        }
        return null;
    }
    
}
