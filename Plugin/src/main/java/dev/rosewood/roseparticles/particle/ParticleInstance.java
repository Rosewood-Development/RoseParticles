package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector2;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.component.model.Vector2;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.datapack.StitchedTexture;
import dev.rosewood.roseparticles.nms.hologram.Hologram;
import dev.rosewood.roseparticles.nms.hologram.HologramProperties;
import dev.rosewood.roseparticles.nms.hologram.HologramProperty;
import dev.rosewood.roseparticles.particle.color.ExpressionParticleColor;
import dev.rosewood.roseparticles.particle.color.GradientParticleColor;
import dev.rosewood.roseparticles.particle.color.ParticleColor;
import dev.rosewood.roseparticles.particle.curve.Curve;
import dev.rosewood.roseparticles.util.ParticleUtils;
import dev.rosewood.roseparticles.util.RayTracer;
import java.awt.Color;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleInstance extends ParticleEffect {

    private static final float COLLISION_OFFSET = 0.001F;

    private final ParticleSystem particleSystem;
    private final Map<String, Curve> curves;
    private final StitchedTexture texture;
    private final MolangExpression expirationExpression;
    private final MolangExpressionVector2 sizeExpression;
    private final Display.Billboard displayBillboard;
    private final MolangExpressionVector3 accelerationExpression;
    private final MolangExpression dragExpression;
    private final MolangExpression rotationAccelerationExpression;
    private final MolangExpression rotationDragExpression;
    private final boolean worldLighting;
    private final Boolean worldCollision;
    private final float collisionDrag;
    private final float coefficientOfRestitution;
    private final float collisionRadius;
    private final boolean expireOnContact;
    private final MolangExpression perRenderExpression;

    private Vector position;
    private Vector velocity;
    private Hologram hologram;
    private int currentTextureIndex;
    private ParticleColor color;
    private Color currentColor;
    private Vector3f currentScale;
    private float rotation;
    private float rotationRate;
    private boolean contactExpired;

    public ParticleInstance(ParticleSystem particleSystem) {
        this.particleSystem = particleSystem;
        ExpressionBindingContext context = this.particleSystem.getMolangContext();
        this.curves = particleSystem.getCurves().entrySet().stream()
                .map(x -> Map.entry(x.getKey(), x.getValue().bind(context, this)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.texture = particleSystem.getTexture();

        // Init variables
        this.set("age", 0);
        for (int i = 1; i <= 4; i++)
            this.set("random_" + i, ParticleUtils.RANDOM.nextFloat());

        var lifetimeComponent = particleSystem.getComponent(ComponentType.PARTICLE_LIFETIME_EXPRESSION);
        if (lifetimeComponent != null) {
            this.expirationExpression = lifetimeComponent.expirationExpression().bind(context, this, this.particleSystem.getEmitter());

            MolangExpression maxLifetimeExpression = lifetimeComponent.maxLifetime().bind(context, this, this.particleSystem.getEmitter());
            float lifetime = maxLifetimeExpression.evaluate();
            this.set("lifetime", lifetime);
        } else {
            this.expirationExpression = null;
        }

        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());

        var appearanceTinting = particleSystem.getComponent(ComponentType.PARTICLE_APPEARANCE_TINTING);
        if (appearanceTinting != null) {
            if (appearanceTinting.colorExpression() != null) {
                this.color = new ExpressionParticleColor(appearanceTinting.colorExpression().bind(context, this, this.particleSystem.getEmitter()));
            } else {
                this.color = new GradientParticleColor(appearanceTinting.gradientInterpolant().bind(context, this, this.particleSystem.getEmitter()), appearanceTinting.gradientMap());
            }
        }

        var appearanceBillboard = particleSystem.getComponent(ComponentType.PARTICLE_APPEARANCE_BILLBOARD);
        if (appearanceBillboard != null) {
            this.sizeExpression = appearanceBillboard.size().bind(context, this, this.particleSystem.getEmitter());
            switch (appearanceBillboard.faceCameraMode()) { // TODO: Finish camera modes
                default -> this.displayBillboard = Display.Billboard.CENTER;
            }
        } else {
            this.sizeExpression = new MolangExpressionVector2(new ConstantExpression(1), new ConstantExpression(1));
            this.displayBillboard = Display.Billboard.CENTER;
        }

        var initialSpin = particleSystem.getComponent(ComponentType.PARTICLE_INITIAL_SPIN);
        if (initialSpin != null) {
            this.rotation = initialSpin.rotation().bind(context, this, this.particleSystem.getEmitter()).evaluate();
            this.rotationRate = initialSpin.rotationRate().bind(context, this, this.particleSystem.getEmitter()).evaluate();
        }

        var motionDynamic = particleSystem.getComponent(ComponentType.PARTICLE_MOTION_DYNAMIC);
        if (motionDynamic != null) {
            this.accelerationExpression = motionDynamic.linearAcceleration().bind(context, this, this.particleSystem.getEmitter());
            this.dragExpression = motionDynamic.linearDragCoefficient().bind(context, this, this.particleSystem.getEmitter());
            this.rotationAccelerationExpression = motionDynamic.rotationAcceleration().bind(context, this, this.particleSystem.getEmitter());
            this.rotationDragExpression = motionDynamic.rotationDragCoefficient().bind(context, this, this.particleSystem.getEmitter());
        } else {
            this.accelerationExpression = null;
            this.dragExpression = null;
            this.rotationAccelerationExpression = null;
            this.rotationDragExpression = null;
        }

        var appearanceLighting = particleSystem.getComponent(ComponentType.PARTICLE_APPEARANCE_LIGHTING);
        this.worldLighting = appearanceLighting != null;

        var motionCollision = particleSystem.getComponent(ComponentType.PARTICLE_MOTION_COLLISION);
        if (motionCollision != null) {
            this.worldCollision = motionCollision.enabled();
            this.collisionDrag = motionCollision.collisionDrag();
            this.coefficientOfRestitution = motionCollision.coefficientOfRestitution();
            this.collisionRadius = motionCollision.collisionRadius();
            this.expireOnContact = motionCollision.expireOnContact();
        } else {
            this.worldCollision = null;
            this.collisionDrag = 0;
            this.coefficientOfRestitution = 0;
            this.collisionRadius = 0;
            this.expireOnContact = false;
        }

        var particleInitialization = particleSystem.getComponent(ComponentType.PARTICLE_INITIALIZATION);
        if (particleInitialization != null) {
            this.perRenderExpression = particleInitialization.perRenderExpression().bind(context, this, this.particleSystem.getEmitter());
        } else {
            this.perRenderExpression = null;
        }
    }

    /**
     * Sets the position and direction vectors, needed so these vectors can be calculated after the particle
     * variable state is already initialized.
     *
     * @param position The initial position
     * @param direction The initial direction, to be multiplied by the initial speed
     */
    public void init(Vector position, Vector direction) {
        this.position = position;

        var initialSpeedComponent = this.particleSystem.getComponent(ComponentType.PARTICLE_INITIAL_SPEED);
        Vector speed;
        if (initialSpeedComponent != null) {
            speed = initialSpeedComponent.initialSpeedVector().bind(this.particleSystem.getMolangContext(), this, this.particleSystem.getEmitter()).evaluate();
        } else {
            speed = new Vector();
        }

        Vector2 scale = this.sizeExpression.evaluate();
        this.currentScale = new Vector3f(scale.x() * 10, scale.y() * 10, 0);

        if (speed.lengthSquared() == 0 || direction.lengthSquared() == 0) {
            this.velocity = new Vector();
        } else {
            this.velocity = direction.normalize().multiply(speed);
        }

        this.updateTextDisplay();
    }

    public void update(float deltaTime) {
        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());

        if (this.perRenderExpression != null)
            this.perRenderExpression.evaluate();

        if (this.accelerationExpression != null) {
            Vector acceleration = this.accelerationExpression.evaluate();
            float drag = this.dragExpression.evaluate();
            this.velocity.add(acceleration.add(this.velocity.clone().multiply(-drag)).multiply(deltaTime));
        }

        if (this.rotationAccelerationExpression != null) {
            float rotationAcceleration = this.rotationAccelerationExpression.evaluate();
            float rotationDrag = this.rotationDragExpression.evaluate();
            this.rotationRate += (rotationAcceleration + this.rotationRate * -rotationDrag) * deltaTime;
        }

        // Handles collision and position updates
        this.collide(deltaTime, 0);

        this.set("age", this.get("age") + deltaTime);

        float nextRotation = this.rotation + this.rotationRate * deltaTime;

        // Use texture linearly relative to age
        boolean updateDisplay = false;
        int nextTextureIndex = (int) Math.min(Math.floor((this.get("age") / (double) this.get("lifetime")) * this.texture.symbols().size()), this.texture.symbols().size() - 1);
        if (this.currentTextureIndex != nextTextureIndex) {
            this.currentTextureIndex = nextTextureIndex;
            updateDisplay = true;
        }

        // Update color tinting
        if (this.color != null) {
            Color nextColor = this.color.get();
            if (!nextColor.equals(this.currentColor)) {
                this.currentColor = nextColor;
                updateDisplay = true;
            }
        }

        // Update scale and rotation if needed
        Vector2 nextScale = this.sizeExpression.evaluate();
        Vector3f nextScaleVec3f = new Vector3f(nextScale.x() * 10, nextScale.y() * 10, 0);
        if (!nextScaleVec3f.equals(this.currentScale) || this.rotation != nextRotation) {
            this.currentScale = nextScaleVec3f;
            this.rotation = nextRotation;
            Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(), new Quaternionf());
            transformation.getScale().set(this.currentScale);
            transformation.getRightRotation().rotateZ(this.rotation);
            this.hologram.getProperties().set(HologramProperty.TRANSFORMATION, transformation);
        }

        if (updateDisplay)
            this.updateTextDisplay();

        Location origin = this.particleSystem.getOrigin();
        Location location = origin.add(this.position);
        this.hologram.getProperties().set(HologramProperty.LOCATION, location);

        this.hologram.update();
    }

    private void collide(float deltaTime, int depth) {
        Location origin = this.particleSystem.getOrigin();
        World world = origin.getWorld();
        if (this.worldCollision != null) {
            Location startLocation = origin.clone().add(this.position);
            RayTracer.RayTraceOutput hit = RayTracer.rayTrace(
                    startLocation,
                    this.velocity.clone().normalize(),
                    this.velocity.length() * deltaTime,
                    this.collisionRadius
            );

            if (hit != null) {
                if (this.expireOnContact) {
                    this.contactExpired = true;
                    return;
                }

                if (!this.worldCollision)
                    return;

                // Restore relativity
                hit.hitPosition().subtract(origin);

                Vector normal = hit.hitFace().getDirection();
                Vector hitPosition = hit.hitPosition().toVector();
                Vector toCenter = this.position.clone().subtract(hitPosition);
                double penetration = this.collisionRadius - toCenter.dot(normal);

                // Position correction to prevent overlap
                if (penetration > 0)
                    this.position.add(normal.clone().multiply(penetration + COLLISION_OFFSET));

                // Reflect velocity
                double dot = this.velocity.dot(normal);
                Vector reflected = this.velocity.clone().subtract(normal.multiply(2 * dot * this.coefficientOfRestitution));

                // Apply friction parallel to surface
                Vector tangentVelocity = this.velocity.clone().subtract(normal.clone().multiply(this.velocity.dot(normal)));
                this.velocity = reflected.add(tangentVelocity.multiply(-this.collisionDrag));

                // Calculate remaining movement time
                float collisionTime = (float) (hitPosition.distance(startLocation.toVector()) / this.velocity.length());
                float remainingTime = deltaTime - collisionTime;

                // Recursive collision check with remaining time up to 2 extra times
                if (remainingTime > 0 && depth < 2)
                    this.collide(remainingTime, depth + 1);
            } else {
                this.position.add(this.velocity.clone().multiply(deltaTime));
            }
        } else {
            this.position.add(this.velocity.clone().multiply(deltaTime));
        }
    }

    public boolean expired() {
        if (this.contactExpired)
            return true;

        if (this.expirationExpression != null) {
            float value = this.expirationExpression.evaluate();
            if (value != 0)
                return true;
        }

        if (this.has("lifetime") && this.get("age") >= this.get("lifetime"))
            return true;

        return false;
    }

    private void updateTextDisplay() {
        Location location = this.particleSystem.getOrigin().add(this.position);

        if (this.hologram == null) {
            this.hologram = this.particleSystem.createHologram(hologram -> {
                HologramProperties properties = hologram.getProperties();
                properties.set(HologramProperty.LOCATION, location);
                if (!this.worldLighting)
                    properties.set(HologramProperty.BRIGHTNESS, new Display.Brightness(15, 15));
                properties.set(HologramProperty.BILLBOARD, this.displayBillboard);
                properties.set(HologramProperty.BACKGROUND_COLOR, org.bukkit.Color.fromARGB(0));
                int delay = SettingKey.UPDATE_FREQUENCY.get().intValue();
                properties.set(HologramProperty.INTERPOLATION_DELAY, delay);
                properties.set(HologramProperty.TRANSFORMATION_DELAY, delay);
                properties.set(HologramProperty.POSITION_ROTATION_DELAY, delay);

                if (this.color != null)
                    this.currentColor = this.color.get();

                if (this.currentScale != null) {
                    Transformation transformation = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(), new Quaternionf());
                    transformation.getScale().set(this.currentScale);
                    transformation.getRightRotation().rotateZ(this.rotation);
                    hologram.getProperties().set(HologramProperty.TRANSFORMATION, transformation);
                }

                this.updateTextDisplayText(hologram);
            });
        } else {
            this.updateTextDisplayText(this.hologram);
        }
    }

    @SuppressWarnings("deprecation")
    private void updateTextDisplayText(Hologram hologram) {
        if (NMSUtil.isPaper()) {
            Component component = Component.text(this.texture.symbols().get(this.currentTextureIndex)).font(Key.key("roseparticles:sprites"));
            if (this.currentColor != null)
                component = component.color(TextColor.color(this.currentColor.getRGB()));
            hologram.getProperties().set(HologramProperty.TEXT_JSON, JSONComponentSerializer.json().serialize(component));
        } else {
            TextComponent component = new TextComponent();
            component.setText(String.valueOf(this.texture.symbols().get(this.currentTextureIndex)));
            component.setFont("roseparticles:sprites");
            if (this.currentColor != null)
                component.setColor(ChatColor.of(this.currentColor));

            hologram.getProperties().set(HologramProperty.TEXT_JSON, ComponentSerializer.toString(component));
        }
    }

    public void remove() {
        if (this.hologram != null) {
            this.particleSystem.deleteHologram(this.hologram);
            this.hologram = null;
        }
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

    private static String mapIdentifier(String identifier) {
        if (identifier.startsWith("particle_"))
            return identifier.substring(9);
        return identifier;
    }

}
