package dev.Volatile.Chopper;

import dev.Volatile.Chopper.AntiBan.AntiBan;
import dev.Volatile.Chopper.Utils.MouseCursor;
import dev.Volatile.Chopper.Utils.MouseTrail;
import dev.Volatile.Chopper.Utils.ZoomControl;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(name = "Volatile Chopper", author = "VolatileDesign", logo = "https://i.imgur.com/p7bgB4c.png", version = 1.07, info = "Woodcutting Script")
public class Main extends Script {
    private Area wcArea = new Area(3118, 3449, 3134, 3419);
    private final Area wcAreaWillow = new Area(3090, 3234, 3082, 3238);
    private final Area wcAreaWillowCamelot = new Area(2704, 3514, 2717, 3499);
    private final Area wcAreaMaple = new Area(2718, 3504, 2733, 3499);
    private final Area wcAreaYew = new Area(3202, 3505, 3211, 3501);

    /**
     * TODO: Add dynamic walking to nearest bank 10/2/2022
     */
    private final Area bankAreas[] = new Area[] {Banks.VARROCK_WEST, Banks.DRAYNOR, Banks.CAMELOT, Banks.GRAND_EXCHANGE};
    private final Area vBank = Banks.VARROCK_WEST;
    private final Area dBank = Banks.DRAYNOR;
    private final Area sBank = Banks.CAMELOT;
    private final Area gBank = Banks.GRAND_EXCHANGE;
    private Area bankArea;
    private final Gui gui = new Gui();
    private final AntiBan antiBan = new AntiBan();
    public String tree = "";
    public String location = "";
    public String axe = "";
    final Object lock = new Object();
    public boolean canStart = false;
    private final MouseTrail trail = new MouseTrail(0, 255, 255, 2000, this);
    private final MouseCursor cursor = new MouseCursor(52, 4, Color.white, this);
    ExperienceTracker xpTrack;
    private long startTime;
    private Configuration config;
    private Integer axeLvl;

    /**
     * Run GUI, track XP, and hide username on script start.
     */
    public void onStart() {
        log("Launching settings..");

        try {
            SwingUtilities.invokeAndWait(() -> {
                gui.run(this);
            });
        } catch(Exception e) {
            e.printStackTrace();
            stop();
        }

        xpTrack = getExperienceTracker();
        xpTrack.start(Skill.WOODCUTTING);
        startTime = System.currentTimeMillis();
        config = new Configuration();
        config.setHideUsername(true);
    }

    /**
     * Sets the Paint graphics, XP, Levels,
     * and time Ran, also hides player's username.
     * @param g Graphics2D
     */
    public void onPaint(Graphics2D g) {
        trail.paint(g);
        cursor.paint(g);

        final String sourceImage = "https://i.imgur.com/p7bgB4c.png";
        try {
            URL url = new URL(sourceImage);
            Image background = ImageIO.read(url);
            g.drawImage(background, 1, 60, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        g.setColor(Color.white);
        g.drawString("Volatile Chopper", 15, 85);
        g.drawString("Experience: " + xpTrack.getGainedXP(Skill.WOODCUTTING) + " (" + xpTrack.getGainedXPPerHour(Skill.WOODCUTTING) + "/HR)", 15, 101);
        g.drawString("Levels gained: " + skills.getDynamic(Skill.WOODCUTTING) + " (" + xpTrack.getGainedLevels(Skill.WOODCUTTING) + ")", 15, 115);

        long elapsed = System.currentTimeMillis() - startTime;
        g.drawString("Time: " + timeFormat(elapsed), 15, 136);

        if (config != null && getClient().isLoggedIn()) {
            if (config.isHideUsername()) {
                if (config.getWidget() == null) {
                    hideUsername(g);
                } else
                    g.fillRect(
                            config.getWidget().getAbsX() + config.getUsernameX(), (config.getWidget().getAbsY() + 2),
                            config.getUsernameLength(), (config.getWidget().getHeight() - 2)
                    );
            }
        }
    }

    @Override
    public int onLoop() throws InterruptedException {
        if (canStart) {
            setZoom();
            setWcSettings();

            if (axe != null) {
                switch (axe) {
                    case "Steel axe":
                        axeLvl = 5;
                        break;
                    case "Black axe":
                        axeLvl = 10;
                        break;
                    case "Mithril axe":
                        axeLvl = 20;
                        break;
                    case "Adamant axe":
                        axeLvl = 30;
                        break;
                    case "Rune axe":
                        axeLvl = 40;
                        break;
                    case "Dragon axe":
                        axeLvl = 60;
                        break;
                }
            }

            if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, axe) && axeLvl == skills.getDynamic(Skill.ATTACK) || !getInventory().contains(axe)) {
                checkItems();
            } else if (!getInventory().isFull()) {
                chopTrees(tree);
            } else {
                bankAll();
            }
        }

        return 602;
    }

    /**
     * Chops trees if in correct Area, and walks to correct area if not.
     * @param treeName String name of the Tree
     */
    private void chopTrees(String treeName) {
        RS2Object tree = getObjects().closest(treeName);
        if (bankArea == null) {
            if ("Willow".equalsIgnoreCase(treeName)) {
                wcArea = wcAreaWillow;
                bankArea = dBank;
            } else if ("Maple tree".equalsIgnoreCase(treeName)) {
                wcArea = wcAreaMaple;
                bankArea = sBank;
            } else if ("Yew".equalsIgnoreCase(treeName)) {
                wcArea = wcAreaYew;
                bankArea = gBank;
            } else {
                bankArea = vBank;
            }
        }

        if (!wcArea.contains(myPosition())) {
            log("Searching for " + treeName + "..");
            getCamera().toEntity(tree);
            log("Running anti-ban: MOUSE MOVEMENT");
            antiBan.run(this, "MOUSE");
            if (getWalking().webWalk(wcArea)) {
                new ConditionalSleep(1000, 2000) {
                    @Override
                    public boolean condition() {
                        return tree.isVisible();
                    }
                }.sleep();
            }
        } else if (!myPlayer().isAnimating() && tree.isVisible() && wcArea.contains(myPosition())) {
            if (random(15, 30) == 23) {
                log("Running anti-ban: MOUSE MISS CLICK");
                getMouse().click(tree.getX()-random(10,40), tree.getY()-random(12,45), false);
            } else if (tree.interact("Chop down")) {
                getMouse().moveOutsideScreen();
                new ConditionalSleep(2000, 5000) {
                    @Override
                    public boolean condition() {
                        return !myPlayer().isAnimating() || !tree.exists();
                    }
                }.sleep();
                log("Running anti-ban: KEYBOARD MOVEMENT");
                antiBan.run(this, "KEYBOARD");
            }
        }
    }

    /**
     * Banks all logs if in bank area, or walks to bank area if not.
     * @throws InterruptedException Throws exception if interrupted
     */
    private void bankAll() throws InterruptedException {
        if (!bankArea.contains(myPosition())) {
            log("Running anti-ban: KEYBOARD MOVEMENT");
            antiBan.run(this, "KEYBOARD");
            if (getWalking().webWalk(bankArea)) {
                new ConditionalSleep(5000, 250) {
                    @Override
                    public boolean condition() {
                        return false;
                    }
                }.sleep();
            }
        } else if (!bank.isOpen()) {
            log("Opening bank.");
            bank.open();
        } else {
            log("Depositing all.");
            bank.depositAllExcept(axe);
            log("Closing bank.");
            bank.close();
        }
    }

    /**
     * Checks player has the correct items to chop tree's,
     * if not, goes to bank area and grabs correct item.
     * @throws InterruptedException Throws exception if interrupted
     */
    private void checkItems() throws InterruptedException {
        if (!getEquipment().isWearingItem(EquipmentSlot.WEAPON, axe) && axeLvl == skills.getDynamic(Skill.ATTACK) || !getInventory().contains(axe)) {
            if (!bankArea.contains(myPosition())) {
                log("Running anti-ban: MOUSE MOVEMENT");
                antiBan.run(this, "MOUSE");
                if (getWalking().webWalk(bankArea)) {
                    new ConditionalSleep(5000, 250) {
                        @Override
                        public boolean condition() {
                            return false;
                        }
                    }.sleep();
                }
                log("Running anti-ban: KEYBOARD MOVEMENT");
                antiBan.run(this, "KEYBOARD");
            } else if (!bank.isOpen()) {
                bank.open();
            } else {
                bank.depositAll();
                bank.withdraw(axe, 1);
                bank.close();
                getEquipment().equip(EquipmentSlot.WEAPON, axe);
            }
        }
    }

    /**
     * Formats timestamp to human readable time.
     * @param time Time long
     * @return String
     */
    public static String timeFormat(long time) {
        StringBuilder t = new StringBuilder();
        long total_secs = time / 1000L;
        long total_mins = total_secs / 60L;
        long total_hrs = total_mins / 60L;
        long total_days = total_hrs / 24L;
        int secs = (int) total_secs % 60;
        int mins = (int) total_mins % 60;
        int hrs = (int) total_hrs % 24;
        int days = (int) total_days;
        if (days < 10) {
            t.append("0");
        }
        t.append(days).append(":");
        if (hrs < 10) {
            t.append("0");
        }
        t.append(hrs).append(":");
        if (mins < 10) {
            t.append("0");
        }
        t.append(mins).append(":");
        if (secs < 10) {
            t.append("0");
        }
        t.append(secs);
        return t.toString();
    }

    /**
     * Hides player's username.
     * @param g Graphics2D
     */
    private void hideUsername(Graphics2D g) {
        RS2Widget w = getWidgets().getWidgetContainingText(162, myPlayer().getName());

        if (w != null) {
            config.setWidget(w);
            config.setUsernameX(w.getMessage().contains("<img=") ? 12 : 0);
            config.setUsernameLength(g.getFontMetrics().stringWidth(myPlayer().getName()));
        }
    }

    private void setZoom() {
        if (!ZoomControl.isInRange(getCamera().getScaleZ(), 275)) {
            ZoomControl.setZoom(getBot(), 275);
        }
    }

    private void setWcSettings() {
        if (tree != null) {
            if ("Willow".equalsIgnoreCase(tree)) {
                if (!"Camelot".equalsIgnoreCase(location)) {
                    wcArea = wcAreaWillow;
                } else {
                    wcArea = wcAreaWillowCamelot;
                }
            } else if ("Maple tree".equalsIgnoreCase(tree)) {
                wcArea = wcAreaMaple;
            } else if ("Yew".equalsIgnoreCase(tree)) {
                wcArea = wcAreaYew;
            }
        }

        if (location != null && bankArea == null) {
            if ("Varrock".equalsIgnoreCase(location)) {
                bankArea = vBank;
            } else if ("Draynor".equalsIgnoreCase(location)) {
                bankArea = dBank;
            } else if ("Camelot".equalsIgnoreCase(location)) {
                bankArea = sBank;
            } else if ("Grand Exchange".equalsIgnoreCase(location)) {
                bankArea = gBank;
            }
        }
    }
}
