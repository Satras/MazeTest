package de.mojah.maze;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created on 07.10.2015.
 */
public class character {

    // Explanations: https://en.wikipedia.org/wiki/Attribute_(role-playing_games)

    int strength;           // aka Body, Might, Brawn, ...
    /*
        A measure of how physically strong a character is.
        Strength often controls the maximum weight the character
        can carry, melee attack and/or damage, and sometimes
        hit points. Armor and weapons might also have a Strength
        requirement.
     */

    int constitution;       // aka Stamina, Endurance, Vitality, ...
    /*
        A measure of how sturdy a character is. Constitution often
         influences hit points, resistances for special types of
         damage (poisons, illness, heat etc.) and fatigue.
     */

    int dexterity;          // aka Agility, Reflexes, Quickness, ...
    /*
        A measure of how agile a character is. Dexterity controls
        attack and movement speed and accuracy, as well as evading
        an opponent's attack (see Armor Class).
     */

    int intelligence;       // aka Intellect, Mind, Knowledge, ...
    /*
        A measure of a character's problem-solving ability.
        Intelligence often controls a character's ability to
        comprehend foreign languages and their skill in magic.
        In some cases, intelligence controls how many skill points
        the character gets at "level up". In some games, it controls
        the rate at which experience points are earned, or the
        amount needed to level up. This is sometimes combined with
        wisdom and/or willpower.
     */

    int wisdom;             // aka Spirit, Wits, Psyche, Sense, ...
    /*
        A measure of a character's common sense and/or spirituality.
        Wisdom often controls a character's ability to cast certain
        spells, communicate to mystical entities, or discern other
        characters' motives or feelings.
     */

    int charisma;           // aka Presence, Charm, Social, ...
    /*
        A measure of a character's social skills, and sometimes
        their physical appearance. Charisma generally influences
        prices while trading, and NPC reactions.
     */

    int armor;
    int weapon;
    int level;
    String className;
    String name;

    int hitpoints;

    boolean isFlying;

    private static boolean initialized=false;
    public static HashMap<String, Object> Classes = new HashMap<String, Object>();

    private static void init() {
        if(initialized) {
            return;
        }
        initialized = true;

        HashMap<String, Integer> values;

        /*
            Level 0
                min 3
                max 15
            on levelup +startvalue/5+1

            Hitpoints :
                strength/2 +
                constitution/3+
                dexterity/6+
                intelligence/6

                => Warrior(0)=10, Ranger(0)=8, Wizard(0)=6

         */

        // Warrior
        values = new HashMap<String, Integer>();
        values.put("str", 15);
        values.put("con", 10);
        values.put("dex", 8);
        values.put("int", 3);
        values.put("wis", 3);
        values.put("cha", 5);
        Classes.put("Warrior", values);

        // Ranger
        values = new HashMap<String, Integer>();
        values.put("str", 8);
        values.put("con", 8);
        values.put("dex", 15);
        values.put("int", 3);
        values.put("wis", 3);
        values.put("cha", 8);
        Classes.put("Ranger", values);

        // Wizard
        values = new HashMap<String, Integer>();
        values.put("str", 3);
        values.put("con", 8);
        values.put("dex", 8);
        values.put("int", 15);
        values.put("wis", 8);
        values.put("cha", 8);
        Classes.put("Wizard", values);

    }

    public character(String _name) {
        this(_name, "Warrior");
    }

    public character(String _name, String _cla) {
        if(!initialized) {
            init();
        }

        name = _name;
        if(Classes.containsKey(_cla)) {
            className = _cla;
        } else {
            className = (String)(Classes.keySet().toArray()[0]);
        }

        HashMap<String, Integer> stats = (HashMap<String, Integer>) Classes.get(className);

        strength = stats.get("str");
        constitution = stats.get("con");
        dexterity = stats.get("dex");
        intelligence = stats.get("int");
        wisdom = stats.get("wis");
        charisma = stats.get("cha");

        // Interge division!!!!
        hitpoints = (int) (strength/2+ constitution/3+ dexterity/6+ intelligence/6);

        isFlying = false;

        level = 0;
        armor = 0;
        weapon = 0;
    }

    /**
     *
     * @param opponent
     * @return
     *      -1 : dodge
     *      -2 : miss
     *      0 - n : damage
     */
    public int attackMelee(character opponent) {

        /*

            TODO
            Simpliefy: roll 1 - 20

            Evade 15 - 20

            Miss  1 - 5
            Normal 6 - 18 => damage * roll-1/18 +1
                Warrior(0) => 1~5
                Ranger(0)  => 1~3
                Wizard(0)  => 1~2
            Krit 19 - 20 => MaxDamage * 1.5

            modifiers ^= dexterity????
            dex/5 + rand()*(20-dex/5)

            hm, modifier may greater than 20?

            damage reducion... after defence...

         */

        int modifierAttack = (int) (dexterity/5 + Math.random()*(20.0-dexterity/5));
        int modifierDefence= (int) (opponent.dexterity/5 + Math.random()*(20.0*opponent.dexterity/5));
        int maxDamage = strength/3;

        System.out.println(className+"("+level+")" +" vs. "+opponent.className+"("+opponent.level+")");
        System.out.println("maxDamage "+maxDamage);
        System.out.println("modAtt    "+modifierAttack);
        System.out.println("modDef    "+modifierDefence);
        System.out.println();

        // Miss
        if(modifierAttack <=5 ) {
            return(-2);
        }

        // Dodge
        if(modifierDefence>=15) {
            return(-1);
        }



        //modifierAttack = 1;
        //modifierDefence= 1;
        /*
        int maxDamage = strength/3;
        int damage = maxDamage;
        int attack = (dexterity/2 +  strength) * ((level+1)*modifierAttack);
        int evade = (opponent.dexterity/2 + opponent.constitution) * ((opponent.level+1)*modifierDefence);

        if(attack <= evade) {
            damage = (int)(1.0D*damage * (attack/(evade*(opponent.level+2))));
        } else {
            damage = (int)(1.0D*damage * ((attack*(level+2)/evade)));
        }

        if(damage > maxDamage) {
            damage = maxDamage;
        }

        if(damage == 0) {
            ret = -1;
        } else {
            ret = damage;
        }

        System.out.println(className+"("+level+")" +" vs. "+opponent.className+"("+opponent.level+")");
        System.out.println("maxDamage "+maxDamage);
        System.out.println("attack    "+attack);
        System.out.println("evade     "+evade);
        System.out.println("damage r  "+damage);
        System.out.println();
        */
        return(ret);
    }

    public void levelUp() {
        level++;

        HashMap<String, Integer> stats = (HashMap<String, Integer>) Classes.get(className);

        strength = stats.get("str") + level* (stats.get("str")+1);
        constitution = stats.get("con") + level* (stats.get("con")+1);
        dexterity = stats.get("dex") + level* (stats.get("dex")+1);
        intelligence = stats.get("int") + level* (stats.get("int")+1);
        wisdom = stats.get("wis") + level* (stats.get("wis")+1);
        charisma = stats.get("cha") + level* (stats.get("cha")+1);

    }

    @Override
    public String toString() {
        return( ""+
                "Name         : "+name              +"\n"+
                "Class        : "+className         +"\n"+
                "Level        : "+level             +"\n"+
                "Hitpoints    : "+hitpoints         +"\n"+
                "=================================="+"\n"+
                "Strength     : "+strength          +"\n"+
                "Constitution : "+constitution      +"\n"+
                "Dexterity    : "+dexterity         +"\n"+
                "Intelligence : "+intelligence      +"\n"+
                "Wisdom       : "+wisdom            +"\n"+
                "Charisma     : "+charisma          +"\n"+
                ""
        );
    }

}
