package dev.Volatile.Chopper.Utils;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import org.osbot.rs07.script.MethodProvider;

public class MouseCursor {

    private final int size;
    private long angle;
    private final BasicStroke cursorStroke;
    private final Color cursorColor;
    private final MethodProvider api;

    public MouseCursor(int size, int thickness, Color color, MethodProvider api) {
        this.size = size;
        this.cursorStroke = new BasicStroke(thickness);
        this.cursorColor = color;
        this.api = api;
    }

    public void paint(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();
        int mX = api.getMouse().getPosition().x;
        int mY = api.getMouse().getPosition().y;

        if (mX != -1) {
            g.setStroke(cursorStroke);
            g.setColor(cursorColor);
            g.drawLine(mX - (size / 8), mY - (size / 8), mX + (size / 12), mY + (size / 12));
            g.drawLine(mX - (size / 8), mY + (size / 12), mX + (size / 12), mY - (size / 8));

            g.rotate(Math.toRadians(angle += 6), mX, mY);

            g.draw(new Arc2D.Double(mX - (size / 2), mY - (size / 2), size, size, 330, 60, Arc2D.OPEN));
            g.draw(new Arc2D.Double(mX - (size / 2), mY - (size / 2), size, size, 151, 60, Arc2D.OPEN));

            g.setTransform(oldTransform);
        }
    }

}
