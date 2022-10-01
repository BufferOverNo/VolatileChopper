package dev.Volatile.Chopper.Utils;

import java.awt.Point;

public class MousePathPoint extends Point{

    private final long finishTime;

    public MousePathPoint(int x, int y, int lastingTime){
        super(x,y);
        finishTime= System.currentTimeMillis() + lastingTime;
    }

    public boolean isUp(){
        return System.currentTimeMillis() > finishTime;
    }
}