package dev.Volatile.Chopper.Utils;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

import org.osbot.rs07.script.MethodProvider;

public class MouseTrail {

    private int r;
    private int g;
    private int b;
    private final int duration;
    private final LinkedList<MousePathPoint> mousePath  = new LinkedList<>();
    private final MethodProvider api;


    public MouseTrail(int r, int g, int b, int duration, MethodProvider api) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.duration = duration;
        this.api = api;
    }

    public void paint(Graphics2D g){
        while (!mousePath.isEmpty() && mousePath.peek().isUp()) {
            mousePath.remove();
        }
        Point clientCursor = api.getMouse().getPosition();
        MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, duration);
        if (mousePath.isEmpty() || !mousePath.getLast().equals(mpp)) {
            mousePath.add(mpp);
        }
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : mousePath) {
            if (lastPoint != null) {
                g.setColor(Color.white);
                g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
            }
            lastPoint = a;
        }
    }
}