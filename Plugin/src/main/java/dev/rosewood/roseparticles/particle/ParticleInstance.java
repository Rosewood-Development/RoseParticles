package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.util.ParticleUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.TextDisplay;

public class ParticleInstance extends ParticleEffect {

    public static final NamespacedKey DISPLAY_KEY = new NamespacedKey(RoseParticles.getInstance(), "particle");

    private final ParticleSystem particleSystem;

    private final MolangExpression expirationExpression;

//    private static int hue = 0;

//    private final Vector position;
//    private final Vector velocity;
//    private final Vector acceleration;
//    private final int lifetime;
//    private final String font;
//    private final List<String> sprites;
//    private final Color color;
//    private int currentSpriteIndex;
    private TextDisplay textDisplay;

    public ParticleInstance(ParticleSystem particleSystem/*, int lifetime, Vector velocity, Vector acceleration, String font, List<String> sprites*/) {
        this.particleSystem = particleSystem;
        this.set("age", 0);

        ExpressionBindingContext molangContext = this.particleSystem.getMolangContext();
        var lifetimeComponent = particleSystem.getComponent(ComponentType.PARTICLE_LIFETIME_EXPRESSION);
        if (lifetimeComponent != null) {
            this.expirationExpression = lifetimeComponent.expirationExpression().bind(molangContext, this, this.particleSystem.getEmitter());

            MolangExpression maxLifetimeExpression = lifetimeComponent.maxLifetime().bind(molangContext, this, this.particleSystem.getEmitter());
            float lifetime = maxLifetimeExpression.evaluate();
            this.set("lifetime", lifetime);
        } else {
            this.expirationExpression = null;
        }

        for (int i = 1; i <= 4; i++)
            this.set("random_" + i, ParticleUtils.RANDOM.nextFloat());

//        this.position = new Vector();
//        this.velocity = velocity.clone();
//        this.acceleration = acceleration.clone();
//        this.lifetime = lifetime;
//        this.font = font;
//        this.sprites = sprites;
//        if (this.sprites.isEmpty())
//            throw new IllegalArgumentException("Must have at least one symbol");
//
//        int colorVal = hue;
//        hue = (hue + 5) % 360;
//        float hsb = colorVal / 360.0F;
//        this.color = Color.getHSBColor(hsb, 1, 1);
    }

    @Override
    public void update(float deltaTime) {
        this.set("age", this.get("age") + deltaTime);
    }

    @Override
    public float get(String identifier) {
        return super.get(mapIdentifier(identifier));
    }

    @Override
    public boolean has(String identifier) {
        return super.has(mapIdentifier(identifier));
    }

    @Override
    public void set(String identifier, float value) {
        super.set(mapIdentifier(identifier), value);
    }

    public boolean expired() {
        if (this.expirationExpression != null) {
            float value = this.expirationExpression.evaluate();
            if (value != 0)
                return true;
        }

        if (this.has("lifetime"))
            return this.get("age") >= this.get("lifetime");

        return false;
    }

    private static String mapIdentifier(String identifier) {
        if (identifier.startsWith("particle_"))
            return identifier.substring("particle_".length() + 1);
        return identifier;
    }

//    private void render() {
//        this.position.add(this.velocity);
//        this.velocity.add(this.acceleration);
//        Location location = this.particleSystem.getOrigin().add(this.position);
//
//        if (this.textDisplay == null) {
//            World world = location.getWorld();
//            this.textDisplay = world.spawn(location, TextDisplay.class, entity -> {
//                entity.setPersistent(false);
//                entity.getPersistentDataContainer().set(DISPLAY_KEY, PersistentDataType.BOOLEAN, true);
//
//                entity.setBillboard(Display.Billboard.CENTER);
//                entity.setBackgroundColor(org.bukkit.Color.fromARGB(0));
//                entity.setDefaultBackground(false);
//                entity.setTeleportDuration(1);
//                Transformation transformation = entity.getTransformation();
//                Vector3f scale = transformation.getScale();
//                scale.set(2, 2, 2);
//                entity.setTransformation(transformation);
//                entity.text(Component.text(this.sprites.get(this.currentSpriteIndex)).font(Key.key(this.font)).color(TextColor.color(this.color.getRGB())));
//            });
//            return;
//        }
//
//        // Use texture linearly relative to age
//        int nextSpriteIndex = (int) Math.min(Math.floor((this.age / (double) this.lifetime) * this.sprites.size()), this.sprites.size() - 1);
//        if (this.currentSpriteIndex != nextSpriteIndex) {
//            this.currentSpriteIndex = nextSpriteIndex;
//            this.textDisplay.text(Component.text(this.sprites.get(this.currentSpriteIndex)).font(Key.key(this.font)).color(TextColor.color(this.color.getRGB())));
//        }
//
//        this.textDisplay.teleport(location);
//    }

    public void remove() {
        if (this.textDisplay != null) {
            this.textDisplay.remove();
            this.textDisplay = null;
        }
    }

}
