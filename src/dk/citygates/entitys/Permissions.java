
package dk.citygates.entitys;

/**
 *
 * @author Steven Hermans
 */
public class Permissions {

    private boolean permOpen = false;
    private boolean permClose = false;
    private boolean permKill = false;
    private boolean permButton = false;

    public boolean isPermOpen() {
        return permOpen;
    }

    public void setPermOpen(boolean permOpen) {
        this.permOpen = permOpen;
    }

    public boolean isPermClose() {
        return permClose;
    }

    public void setPermClose(boolean permClose) {
        this.permClose = permClose;
    }

    public boolean isPermKill() {
        return permKill;
    }

    public void setPermKill(boolean permKill) {
        this.permKill = permKill;
    }

    public boolean isPermButton() {
        return permButton;
    }

    public void setPermButton(boolean permButton) {
        this.permButton = permButton;
    }

    public void setAll(boolean b) {
        this.permOpen = b;
        this.permClose = b;
        this.permKill = b;
        this.permButton = b;
    }
    
}
