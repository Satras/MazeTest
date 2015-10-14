package de.mojah.maze;

public class Main {

    public static void main(String[] args) {
        maze myMaze = new maze(80,40, 1000);
        //System.out.println(myMaze.rand.nextLong());

        myMaze.addRoom(15,15,true);

        for(int i=0;i<160;i++) {

            int tries=0;
            do {
                tries++;
            } while(tries<20 && !myMaze.addRoom());

        }
        myMaze.cleanup();
        //System.out.println();
        //System.out.println(myMaze);


        character c1 = new character("Mojah","Warrior");
        character c11 = new character("Mojah","Warrior");
        character c2 = new character("Mojah","Ranger");
        character c3 = new character("Mojah","Wizard");

        c3.levelUp();

        System.out.println(c1);
        for(int i=0;i<25;i++) {
            c1.levelUp();
        }
        System.out.println(c1);

        int dcount = 0;
        int mcount = 0;
        int count = 0;
        int damage = 0;
        for(int i=0;i<1000;i++) {
            int r = c11.attackMelee(c1);
            if(r==-1) {
                dcount++;
            } else if(r==-2) {
                mcount++;
            } else {
                count++;
                damage += r;
            }
        }
        System.out.println("dodged: "+dcount);
        System.out.println("missed: "+mcount);
        System.out.println("hit: "+count);
        System.out.println("dmg :"+damage*1D/count);
        /*
        c1.attackMelee(c2);
        c1.attackMelee(c3);

        c2.attackMelee(c1);
        c2.attackMelee(c2);
        c2.attackMelee(c3);

        c3.attackMelee(c1);
        c3.attackMelee(c2);
        c3.attackMelee(c3);
        */

    }
}
