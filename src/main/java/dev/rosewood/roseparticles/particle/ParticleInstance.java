package dev.rosewood.roseparticles.particle;

import dev.rosewood.roseparticles.RoseParticles;
import java.awt.Color;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class ParticleInstance implements ParticleEffect {

    public static final NamespacedKey DISPLAY_KEY = new NamespacedKey(RoseParticles.getInstance(), "particle");
    private static int hue = 0;

    private final Vector position;
    private final Vector velocity;
    private final Vector acceleration;
    private final int lifetime;
    private final String font;
    private final String symbols;
    private int life;
    private TextDisplay textDisplay;

    public ParticleInstance(int lifetime, Vector velocity, Vector acceleration, String font, String symbols) {
        this.position = new Vector();
        this.velocity = velocity.clone();
        this.acceleration = acceleration.clone();
        this.lifetime = lifetime;
        this.font = font;
        this.symbols = symbols;
    }

    @Override
    public void update(ParticleSystem system) {
        this.life++;
        this.position.add(this.velocity);
        this.velocity.add(this.acceleration);
        Location location = system.getOrigin().add(this.position);

        if (this.textDisplay == null) {
            World world = location.getWorld();
            this.textDisplay = world.spawn(location, TextDisplay.class, entity -> {
                entity.setPersistent(false);
                entity.getPersistentDataContainer().set(DISPLAY_KEY, PersistentDataType.BOOLEAN, true);
                int colorVal = hue;
                hue = (hue + 5) % 360;
                float hsb = colorVal / 360.0F;
                Color color = Color.getHSBColor(hsb, 1, 1);
                entity.setBillboard(Display.Billboard.CENTER);
                entity.setBackgroundColor(org.bukkit.Color.fromARGB(0));
                entity.setDefaultBackground(false);
                entity.setTeleportDuration(1);
                Transformation transformation = entity.getTransformation();
                Vector3f scale = transformation.getScale();
                scale.set(2, 2, 2);
                entity.setTransformation(transformation);
                entity.text(Component.text(this.symbols).font(Key.key(this.font)).color(TextColor.color(color.getRGB())));
            });
            return;
        }

        this.textDisplay.teleport(location);
    }

    @Override
    public void remove() {
        if (this.textDisplay != null) {
            this.textDisplay.remove();
            this.textDisplay = null;
        }
    }

    @Override
    public boolean expired() {
        return this.life >= this.lifetime;
    }

}
