package de.mojah.maze;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

/**
 * Created by bm1-bayerth on 05.10.2015.
 */

/**
 * TODO:    VisitedMap, Objects
 */
public class maze {

    int sizeX;
    int sizeY;
    boolean isStart=false;
    boolean isEnd=false;

    long seed;
    Random rand;

    byte[][] mazeBuffer;

    HashMap levelData = new HashMap();

    private static final byte EMPTY = '*';
    private static final byte REACHABLE = '1';
    private static final byte WALL = '#';
    private static final byte SPACE = ' ';
    private static final byte DOOR = 'D';

    public maze(int x, int y) {
        this(x, y, System.currentTimeMillis());
    }

    public maze(int x, int y, long seed) {

        levelData.put("seed",seed);
        levelData.put("sizeX", x);
        levelData.put("sizeY", y);

        this.seed = seed;
        rand = new Random(seed);
        sizeX = x;
        sizeY = y;

        init();
    }

    /*
        Initialize empty map
     */
    public void init() {
        mazeBuffer = new byte[sizeX][sizeY];
        for(int y=0;y<sizeY;y++) {
            for(int x=0;x<sizeX;x++) {
                mazeBuffer[x][y] = SPACE;
            }
        }
    }

    /*
        Add a standard empty room
     */
    public boolean addRoom() {
        return(addRoom(7,5,false));
    }

    /*
        Add a room, may set starter room
     */
    public boolean addRoom(int X, int Y, boolean isStart) {

        int rX = (int)(rand.nextDouble()*(sizeX-X));
        int rY = (int)(rand.nextDouble()*(sizeY-Y));

        //System.out.println(rX+":"+rY);

        // Test
        for(int y=rY;y<rY+Y;y++) {
            for(int x=rX;x<rX+X;x++) {
                if(mazeBuffer[x][y]!=SPACE) {
                    return(false);
                }
            }
        }

        // Set
        for(int y=rY;y<rY+Y;y++) {
            for(int x=rX;x<rX+X;x++) {
                if(y==rY || y==rY+Y-1 || x==rX || x==rX+X-1) {
                    mazeBuffer[x][y]=WALL;
                } else {
                    if(isStart && !this.isStart) {
                        levelData.put("startX", x);
                        levelData.put("startY", y);
                        this.isStart = true;
                    }

                    mazeBuffer[x][y] = EMPTY;
                }

            }
        }

        return(true);
    }

    /*
        Merge touching rooms (if posible)
     */
    public void mergeRooms() {
        // mergeRooms
        for(int y=0;y<sizeY;y++) {
            for(int x=0;x<sizeX;x++){
                // Horizontal
                try {
                    if(mazeBuffer[x][y] == WALL && mazeBuffer[x+1][y]==WALL && mazeBuffer[x-1][y]==EMPTY && mazeBuffer[x+2][y]==EMPTY) {
                        mazeBuffer[x][y] = EMPTY;
                        mazeBuffer[x+1][y] = EMPTY;
                    }
                } catch(Exception e) {}

                // Vertical
                try {
                    if(mazeBuffer[x][y] == WALL && mazeBuffer[x][y+1]==WALL && mazeBuffer[x][y-1]==EMPTY && mazeBuffer[x][y+2]==EMPTY) {
                        mazeBuffer[x][y] = EMPTY;
                        mazeBuffer[x][y+1] = EMPTY;
                    }
                } catch(Exception e) {}
            }
        }
    }

    /*
        Scan which rooms are reachable
     */
    private void scanToMarkRoom() {

        boolean changed= false;
        boolean bridge = false;

        for(int y=0;y<sizeY;y++) {
            for(int x=0;x<sizeX;x++) {

                // Mark reachable

                // right
                try {
                    if((mazeBuffer[x][y]==REACHABLE||mazeBuffer[x][y]==DOOR) && mazeBuffer[x+1][y]==EMPTY) {
                        mazeBuffer[x+1][y]=REACHABLE;
                        changed = true;
                    }
                } catch(Exception e){}

                // down
                try {
                    if((mazeBuffer[x][y]==REACHABLE||mazeBuffer[x][y]==DOOR) && mazeBuffer[x][y+1]==EMPTY) {
                        mazeBuffer[x][y+1]=REACHABLE;
                        changed = true;
                    }
                } catch(Exception e){}

                // left
                try {
                    if((mazeBuffer[x][y]==REACHABLE||mazeBuffer[x][y]==DOOR) && mazeBuffer[x-1][y]==EMPTY) {
                        mazeBuffer[x-1][y]=REACHABLE;
                        changed = true;
                    }
                } catch(Exception e){}

                // up
                try {
                    if((mazeBuffer[x][y]==REACHABLE||mazeBuffer[x][y]==DOOR) && mazeBuffer[x][y-1]==EMPTY) {
                        mazeBuffer[x][y-1]=REACHABLE;
                        changed = true;
                    }
                } catch(Exception e){}
            }
        }

        //System.out.println(this.toString());

        if(changed) {
            scanToMarkRoom();
        }
    }

    /*
        Build a hallway between rooms
     */
    private void buildConnect(int x, int y, int dx, int dy) {

        int step = 0;
        do {
            if(step == 0 && rand.nextDouble() <0.5) {
                mazeBuffer[x][y]=DOOR;
            } else {
                mazeBuffer[x][y] = EMPTY;
            }
            mazeBuffer[x-dy][y-dx]=WALL;
            mazeBuffer[x+dy][y+dx]=WALL;
            x+=dx;
            y+=dy;
            step++;
        } while(mazeBuffer[x][y]!=WALL);

        if(rand.nextDouble()<0.5) {
            mazeBuffer[x][y]=DOOR;
        } else {
            mazeBuffer[x][y] = EMPTY;
        }

    }

    /*
        Test if it's possible to build a hallway between rooms
     */
    private boolean testConnection(int x, int y, int dx, int dy) {

        if(x<1 || x>((Integer)levelData.get("sizeX")) - 1) {
            return(false);
        }
        if(y<1 || y>((Integer)levelData.get("sizeY")) - 1) {
            return(false);
        }

        if(mazeBuffer[x][y]==SPACE && mazeBuffer[x-dy][y-dx]==SPACE && mazeBuffer[x+dy][y+dx]==SPACE) {
            return(testConnection(x+dx,y+dy,dx,dy));
        } else if(mazeBuffer[x][y]==WALL && mazeBuffer[x-dy][y-dx]==WALL && mazeBuffer[x+dy][y+dx]==WALL) {
            if(mazeBuffer[x+dx][y+dy]==EMPTY) {
                return(true);
            }
        }
        return(false);
    }

    /*
        Check if there are connectable rooms
     */
    public void connectRooms() {

        for(int y=0;y<sizeY;y++) {
            for (int x = 0; x < sizeX; x++) {

                double r = rand.nextDouble();

                // right
                if(r<0.25) {
                    try {
                        if (mazeBuffer[x][y] == REACHABLE && mazeBuffer[x + 1][y] == WALL && mazeBuffer[x + 2][y] == SPACE && mazeBuffer[x + 1][y - 1] == WALL && mazeBuffer[x + 1][y + 1] == WALL) {
                            if (testConnection(x + 2, y, 1, 0)) {
                                buildConnect(x + 1, y, 1, 0);
                                markRooms();
                                connectRooms();
                                return;
                            }
                        }
                    } catch (Exception e) {}
                }

                // left
                if(r>0.25 && r<=0.5) {
                    try {
                        if (mazeBuffer[x][y] == REACHABLE && mazeBuffer[x - 1][y] == WALL && mazeBuffer[x - 2][y] == SPACE && mazeBuffer[x - 1][y - 1] == WALL && mazeBuffer[x - 1][y + 1] == WALL) {
                            if (testConnection(x - 2, y, -1, 0)) {
                                buildConnect(x - 1, y, -1, 0);
                                markRooms();
                                connectRooms();
                                return;
                            }
                        }
                    } catch (Exception e) {}
                }

                // Up
                if(r>0.5 && r<=0.75) {
                    try {
                        if (mazeBuffer[x][y] == REACHABLE && mazeBuffer[x][y-1] == WALL && mazeBuffer[x][y-2] == SPACE && mazeBuffer[x-1][y-1] == WALL && mazeBuffer[x+1][y-1] == WALL) {
                            if (testConnection(x, y-2, 0, -1)) {
                                buildConnect(x, y-1, 0, -1);
                                markRooms();
                                connectRooms();
                                return;
                            }
                        }
                    } catch (Exception e) {}
                }

                // Down
                if(r>0.75) {
                    try {
                        if (mazeBuffer[x][y] == REACHABLE && mazeBuffer[x][y+1] == WALL && mazeBuffer[x][y+2] == SPACE && mazeBuffer[x-1][y+1] == WALL && mazeBuffer[x+1][y+1] == WALL) {
                            if (testConnection(x, y+2, 0, 1)) {
                                buildConnect(x, y+1, 0, 1);
                                markRooms();
                                connectRooms();
                                return;
                            }
                        }
                    } catch (Exception e) {}
                }
            }
        }
    }

    /*
        Mark reachable rooms
     */
    public void markRooms() {
        int x = (Integer)levelData.get("startX");
        int y = (Integer)levelData.get("startY");

        mazeBuffer[x][y] = REACHABLE;

        scanToMarkRoom();

    }

    /*
        Cleanup the map
     */
    public void cleanup() {

        // Merge the rooms
        mergeRooms();

        // mark reachable
        markRooms();

        // try to connect unreachable rooms
        connectRooms();

        //System.out.println(levelData);

        // Clean map
        for(int y=0;y<sizeY;y++) {
            for(int x=0;x<sizeX;x++){
                if(mazeBuffer[x][y] == EMPTY) {
                    mazeBuffer[x][y] = WALL;
                } else if(mazeBuffer[x][y] == REACHABLE) {
                    mazeBuffer[x][y] = SPACE;
                } else if(mazeBuffer[x][y] == SPACE) {
                    mazeBuffer[x][y] = WALL;
                }
            }
        }



        // CleanRoom
        /*for(int y=0;y<sizeY;y++) {
            for(int x=0;x<sizeX;x++){
                if(mazeBuffer[x][y] == '*') {
                    mazeBuffer[x][y] = ' ';
                }
            }
        }*/
    }

    public void show() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for(int x=0;x<sizeX+2;x++){
            sb.append((char)WALL);
        }
        sb.append("\n");

        for(int y=0;y<sizeY;y++) {
            sb.append((char)WALL);
            for(int x=0;x<sizeX;x++){
                sb.append((char)mazeBuffer[x][y]);
            }
            sb.append((char)WALL+"\n");
        }

        for(int x=0;x<sizeX+2;x++){
            sb.append((char)WALL);
        }
        sb.append("\n");

        return(sb.toString());
    }

}
