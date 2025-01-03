package dev.rosewood.roseparticles.particle;

import dev.rosewood.roseparticles.RoseParticles;
import java.awt.Color;
import java.util.List;
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
    private final List<String> sprites;
    private final Color color;
    private int currentSpriteIndex;
    private int age;
    private TextDisplay textDisplay;

    public ParticleInstance(int lifetime, Vector velocity, Vector acceleration, String font, List<String> sprites) {
        this.position = new Vector();
        this.velocity = velocity.clone();
        this.acceleration = acceleration.clone();
        this.lifetime = lifetime;
        this.font = font;
        this.sprites = sprites;
        if (this.sprites.isEmpty())
            throw new IllegalArgumentException("Must have at least one symbol");

        int colorVal = hue;
        hue = (hue + 5) % 360;
        float hsb = colorVal / 360.0F;
        this.color = Color.getHSBColor(hsb, 1, 1);
    }

    @Override
    public void update(ParticleSystem system) {
        this.age++;
        this.position.add(this.velocity);
        this.velocity.add(this.acceleration);
        Location location = system.getOrigin().add(this.position);

        if (this.textDisplay == null) {
            World world = location.getWorld();
            this.textDisplay = world.spawn(location, TextDisplay.class, entity -> {
                entity.setPersistent(false);
                entity.getPersistentDataContainer().set(DISPLAY_KEY, PersistentDataType.BOOLEAN, true);

                entity.setBillboard(Display.Billboard.CENTER);
                entity.setBackgroundColor(org.bukkit.Color.fromARGB(0));
                entity.setDefaultBackground(false);
                entity.setTeleportDuration(1);
                Transformation transformation = entity.getTransformation();
                Vector3f scale = transformation.getScale();
                scale.set(2, 2, 2);
                entity.setTransformation(transformation);
                entity.text(Component.text(this.sprites.get(this.currentSpriteIndex)).font(Key.key(this.font)).color(TextColor.color(this.color.getRGB())));
            });
            return;
        }

        // Use texture linearly relative to age
        int nextSpriteIndex = (int) Math.min(Math.floor((this.age / (double) this.lifetime) * this.sprites.size()), this.sprites.size() - 1);
        if (this.currentSpriteIndex != nextSpriteIndex) {
            this.currentSpriteIndex = nextSpriteIndex;
            this.textDisplay.text(Component.text(this.sprites.get(this.currentSpriteIndex)).font(Key.key(this.font)).color(TextColor.color(this.color.getRGB())));
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
        return this.age >= this.lifetime;
    }

}
