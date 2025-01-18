package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector2;
import dev.rosewood.roseparticles.component.model.MolangExpressionVector3;
import dev.rosewood.roseparticles.component.model.Vector2;
import dev.rosewood.roseparticles.datapack.StitchedTexture;
import dev.rosewood.roseparticles.particle.color.ExpressionParticleColor;
import dev.rosewood.roseparticles.particle.color.GradientParticleColor;
import dev.rosewood.roseparticles.particle.color.ParticleColor;
import dev.rosewood.roseparticles.particle.curve.Curve;
import dev.rosewood.roseparticles.util.ParticleUtils;
import java.awt.Color;
import java.util.Map;
import java.util.stream.Collectors;
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

public class ParticleInstance extends ParticleEffect {

    public static final NamespacedKey DISPLAY_KEY = new NamespacedKey(RoseParticles.getInstance(), "particle");

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

    private Vector position;
    private Vector velocity;
    private TextDisplay textDisplay;
    private int currentTextureIndex;
    private ParticleColor color;
    private Color currentColor;
    private Vector3f currentScale;
    private float rotation;
    private float rotationRate;

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
        this.velocity = direction.multiply(speed);
        this.updateTextDisplay();
    }

    public void update(float deltaTime) {
        for (var curveEntry : this.curves.entrySet())
            this.set(curveEntry.getKey(), curveEntry.getValue().evaluate());

        Vector acceleration;
        float drag, rotationAcceleration, rotationDrag;
        if (this.accelerationExpression != null) {
            acceleration = this.accelerationExpression.evaluate();
            drag = this.dragExpression.evaluate();
            rotationAcceleration = this.rotationAccelerationExpression.evaluate();
            rotationDrag = this.rotationDragExpression.evaluate();

            this.velocity.add(acceleration.add(this.velocity.clone().multiply(-drag)).multiply(deltaTime));
            this.rotationRate += (rotationAcceleration + this.rotationRate * -rotationDrag) * deltaTime;
        }

        this.position.add(this.velocity.clone().multiply(deltaTime));
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
            Transformation transformation = this.textDisplay.getTransformation();
            transformation.getScale().set(this.currentScale);
            transformation.getRightRotation().set(0, 0, 0, 1);
            transformation.getRightRotation().rotateZ(this.rotation);
            this.textDisplay.setTransformation(transformation);
        }

        if (updateDisplay)
            this.updateTextDisplay();

        Location location = this.particleSystem.getOrigin().add(this.position);
        this.textDisplay.teleport(location);
    }

    public boolean expired() {
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

        if (this.textDisplay == null) {
            World world = location.getWorld();
            this.textDisplay = world.spawn(location, TextDisplay.class, entity -> {
                entity.setPersistent(false);
                entity.getPersistentDataContainer().set(DISPLAY_KEY, PersistentDataType.BOOLEAN, true);

                entity.setBillboard(this.displayBillboard);
                entity.setBackgroundColor(org.bukkit.Color.fromARGB(0));
                entity.setDefaultBackground(false);
                entity.setTeleportDuration(1);

                if (this.color != null)
                    this.currentColor = this.color.get();

                if (this.currentScale != null) {
                    Transformation transformation = entity.getTransformation();
                    transformation.getScale().set(this.currentScale);
                    //transformation.getTranslation().set(0, -this.currentScale.y / 2, 0);
                    transformation.getRightRotation().set(0, 0, 0, 1);
                    transformation.getRightRotation().rotateZ(this.rotation);
                    entity.setTransformation(transformation);
                }

                this.updateTextDisplayText(entity);
            });
        } else {
            this.updateTextDisplayText(this.textDisplay);
        }
    }

    private void updateTextDisplayText(TextDisplay textDisplay) {
        Component component = Component.text(this.texture.symbols().get(this.currentTextureIndex)).font(Key.key("roseparticles:sprites"));
        if (this.currentColor != null)
            component = component.color(TextColor.color(this.currentColor.getRGB()));
        textDisplay.text(component);
    }

    public void remove() {
        if (this.textDisplay != null) {
            this.textDisplay.remove();
            this.textDisplay = null;
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
            return identifier.substring("particle_".length());
        return identifier;
    }

}
