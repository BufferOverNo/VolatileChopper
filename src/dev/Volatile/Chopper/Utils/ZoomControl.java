package dev.Volatile.Chopper.Utils;

import org.osbot.rs07.Bot;
import org.osbot.rs07.api.Settings;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.input.mouse.PointDestination;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep2;

public final class ZoomControl extends Event {

    /**
     * Determines if the camera Z scale is within a requested Zoom
     *
     * @param currentZoom the current Z scale of the camera
     * @param requestedZoom the requested Z scale of the camera
     * @return true if we are within the range, otherwise false
     */
    public static boolean isInRange(int currentZoom, int requestedZoom) {
        int currentPos = (int) getPosition(currentZoom);
        int requestedPos = (int) getPosition(requestedZoom);
        return currentPos > requestedPos - 5 &&
                currentPos < requestedPos + 5;
    }

    /**
     * Public method to execute event
     *
     * @param bot the bot instance
     * @param requestedZoom the zoom value to set
     * @return true if the event finished successfully
     */
    public static boolean setZoom(Bot bot, int requestedZoom) {
        Event event = new ZoomControl(requestedZoom);
        bot.getEventExecutor().execute(event);
        return event.hasFinished();
    }

    private final int zoom;

    private ZoomControl(int zoom) {
        this.zoom = zoom;
    }

    /**
     * Generates the would-be X position of the slider if the Z scale was the given value
     *
     * @param zoom the camera Z scale
     * @return the X position of the slider
     */
    private static double getPosition(int zoom) {
        double a = 415.7837;
        double power = 0.07114964;

        return a * Math.pow(zoom, power);
    }

    /**
     * Creates a new Condition, representing a Mouse Drag from the current position
     * of the slider to the requested position to achieve the correct zoom
     *
     * @param slider the slider widget object
     * @return the Condition to pass to a continualClick method
     */
    private Condition moveMouse(RS2Widget slider) {
        return new Condition() {

            @Override
            public boolean evaluate() {
                // Generate the X position for the slider of the requested zoom
                double widgetDestX = getPosition(zoom);
                // Generate the mouse's end X position for moving the slider
                int mouseDestX = (int) widgetDestX +
                        (int) (getMouse().getPosition().getX() - slider.getBounds().getX());
                // Generate the mouse's end Y position for moving the slider
                int mouseDestY = (int) slider.getPosition().getY() + (int) (slider.getHeight() / 2.00);
                return getMouse().move(new PointDestination(getBot(), mouseDestX, mouseDestY));
            }
        };
    }

    /**
     * Execution method
     *
     * @return the sleep time in between loops
     */
    @Override
    public int execute() {
        if (isInRange(getCamera().getScaleZ(), zoom)) {
            setFinished();
            return 0;
        } else if (!getTabs().isOpen(Tab.SETTINGS)) {
            if (getSettings().open())
                return 0;
        } else if (getSettings().getCurrentBasicSettingsTab() != Settings.BasicSettingsTab.DISPLAY) {
            if (getSettings().open(Settings.BasicSettingsTab.DISPLAY)) {
                return 0;
            }
        } else {
            RS2Widget slider = getWidgets().getWidgetContainingSprite(116, 1201);
            if (slider != null) {
                if (getMouse().continualClick(new WidgetDestination(getBot(), slider, 3), moveMouse(slider)) &&
                        ConditionalSleep2.sleep(1000, () -> isInRange(getCamera().getScaleZ(), zoom))) {
                    return 0;
                }
            }
        }
        return 300;
    }

}