package de.mojah.maze;

/**
 * Created on 14.10.2015.
 */
public class item {

    private int healthPerUse = 0;
    private boolean consumable = false;
    private String name = "";

    public item(String name) {
        this(name, 0, false);
    }
    public item(String name, int health, boolean consumable) {
        healthPerUse = health;
        this.consumable = consumable;
        this.name = name;
    }
    public int getHealthPerUse() {
        return(healthPerUse);
    }
    public boolean isConsumable() {
        return(consumable);
    }

    @Override
    public String toString() {
        return(
                "Item         : "+name                  +"\n"+
                "Consumable   : "+isConsumable()        +"\n"+
                "HealthPerUse : "+getHealthPerUse()     +"\n"+
                ""
        );
    }
}
