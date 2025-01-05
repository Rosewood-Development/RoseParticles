package dev.rosewood.roseparticles.particle;

import dev.rosewood.roseparticles.util.ParticleUtils;

public class ParticleInstance implements ParticleEffect {

    private final ParticleSystem particleSystem;
    private int age;
    private final float[] randoms;

//    public static final NamespacedKey DISPLAY_KEY = new NamespacedKey(RoseParticles.getInstance(), "particle");
//    private static int hue = 0;

//    private final Vector position;
//    private final Vector velocity;
//    private final Vector acceleration;
//    private final int lifetime;
//    private final String font;
//    private final List<String> sprites;
//    private final Color color;
//    private int currentSpriteIndex;
//    private int age;
//    private TextDisplay textDisplay;

    public ParticleInstance(ParticleSystem particleSystem/*, int lifetime, Vector velocity, Vector acceleration, String font, List<String> sprites*/) {
        this.particleSystem = particleSystem;
        this.randoms = new float[]{ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat(), ParticleUtils.RANDOM.nextFloat()};
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
    public void update() {
        this.age++;
    }

    public float getLifetime() {
        return 0;
    }

    public float getAge() {
        return this.age;
    }

    public float[] getRandoms() {
        return this.randoms;
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
//
//    @Override
//    public void remove() {
//        if (this.textDisplay != null) {
//            this.textDisplay.remove();
//            this.textDisplay = null;
//        }
//    }
//
//    @Override
//    public boolean expired() {
//        return this.age >= this.lifetime;
//    }

}
