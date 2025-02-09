package dev.rosewood.roseparticles.particle;

import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.curve.CurveDefinition;
import dev.rosewood.roseparticles.component.event.EventDefinition;
import dev.rosewood.roseparticles.config.SettingKey;
import dev.rosewood.roseparticles.datapack.StitchedTexture;
import dev.rosewood.roseparticles.manager.HologramManager;
import dev.rosewood.roseparticles.nms.hologram.Hologram;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import dev.rosewood.roseparticles.particle.curve.BezierChainCurve;
import dev.rosewood.roseparticles.particle.curve.BezierCurve;
import dev.rosewood.roseparticles.particle.curve.CatmullRomCurve;
import dev.rosewood.roseparticles.particle.curve.Curve;
import dev.rosewood.roseparticles.particle.curve.LinearCurve;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ParticleSystem {

    private final HologramManager hologramManager;
    private final Entity attachedTo;
    private final Location origin;
    private final ParticleFile particleFile;
    private final StitchedTexture texture;

    private final ExpressionBindingContext molangContext;
    private final EmitterInstance emitter;
    private final List<ParticleInstance> particles;
    private final List<ParticleInstance> newParticles;
    private final Map<String, Curve> curves;
    private final Map<String, EventDefinition> events;
    private final float deltaTime;
    private boolean doneEmitting;

    public ParticleSystem(HologramManager hologramManager, Entity entity, ParticleFile particleFile, StitchedTexture texture) {
        this(hologramManager, entity, null, particleFile, texture);
    }

    public ParticleSystem(HologramManager hologramManager, Location origin, ParticleFile particleFile, StitchedTexture texture) {
        this(hologramManager, null, origin, particleFile, texture);
    }

    private ParticleSystem(HologramManager hologramManager, Entity entity, Location origin, ParticleFile particleFile, StitchedTexture texture) {
        this.hologramManager = hologramManager;
        this.attachedTo = entity;
        this.origin = origin;
        this.particleFile = particleFile;
        this.texture = texture;
        this.molangContext = ExpressionBindingContext.create();
        this.particles = new LinkedList<>();
        this.newParticles = new ArrayList<>();
        this.curves = new HashMap<>();
        this.events = particleFile.events();
        this.deltaTime = SettingKey.UPDATE_FREQUENCY.get() / 20F;

        List<CurveDefinition> curveDefinitions = this.particleFile.curves();
        for (CurveDefinition curveDefinition : curveDefinitions) {
            String variableName = curveDefinition.name();
            int dotIndex = variableName.indexOf('.');
            if (dotIndex == -1) {
                RoseParticles.getInstance().getLogger().info("Ignoring curve with invalid variable name " + variableName);
                continue;
            }

            String query = variableName.substring(0, dotIndex);
            switch (query) {
                case "variable", "v", "temp", "t" -> {}
                default -> {
                    RoseParticles.getInstance().getLogger().info("Ignoring curve with invalid variable name " + variableName);
                    continue;
                }
            }

            String name = variableName.substring(dotIndex + 1);

            Curve curve = switch (curveDefinition.type()) {
                case LINEAR -> new LinearCurve(curveDefinition.input(), curveDefinition.horizontalRange(), curveDefinition.nodes());
                case BEZIER -> new BezierCurve(curveDefinition.input(), curveDefinition.horizontalRange(), curveDefinition.nodes());
                case BEZIER_CHAIN -> new BezierChainCurve(curveDefinition.input(), curveDefinition.horizontalRange(), curveDefinition.bezierChainNodes());
                case CATMULL_ROM -> new CatmullRomCurve(curveDefinition.input(), curveDefinition.horizontalRange(), curveDefinition.nodes());
            };
            this.curves.put(name, curve);
        }

        this.emitter = new EmitterInstance(this);
    }

    public String getIdentifier() {
        return this.particleFile.description().identifier();
    }

    public EmitterInstance getEmitter() {
        return this.emitter;
    }

    public Map<String, Curve> getCurves() {
        return this.curves;
    }

    public ExpressionBindingContext getMolangContext() {
        return this.molangContext;
    }

    public StitchedTexture getTexture() {
        return this.texture;
    }

    protected Hologram createHologram(Consumer<Hologram> init) {
        return this.hologramManager.createHologram(this.getOrigin().getWorld(), init);
    }

    protected void deleteHologram(Hologram hologram) {
        this.hologramManager.deleteHologram(hologram);
    }

    public void update() {
        if (!this.doneEmitting) {
            this.newParticles.addAll(this.emitter.update(this.deltaTime));
            if (this.emitter.expired())
                this.doneEmitting = true;
        }

        this.particles.addAll(this.newParticles);
        this.newParticles.clear();

        this.particles.removeIf(particle -> {
            particle.update(this.deltaTime);
            if (particle.expired()) {
                particle.remove();
                return true;
            } else {
                return false;
            }
        });
    }

    protected void playEvent(String event, ParticleInstance particleInstance) {
        EventDefinition eventDefinition = this.events.get(event.toLowerCase());
        if (eventDefinition != null)
            eventDefinition.play(this, particleInstance);
    }

    protected void addParticle(ParticleInstance particleInstance) {
        this.newParticles.add(particleInstance);
    }

    public void remove() {
        this.doneEmitting = true;
        this.particles.forEach(ParticleInstance::remove);
        this.particles.clear();
        this.newParticles.forEach(ParticleInstance::remove);
        this.newParticles.clear();
    }

    @Nullable
    public <T> T getComponent(ComponentType<T> componentType) {
        return this.particleFile.getComponent(componentType);
    }

    public Entity getAttachedTo() {
        return this.attachedTo;
    }

    public Location getOrigin() {
        if (this.attachedTo != null) {
            return this.attachedTo.getLocation();
        } else if (this.origin != null) {
            return this.origin.clone();
        }
        throw new IllegalStateException();
    }

    public boolean isFinished() {
        return this.doneEmitting && this.particles.isEmpty();
    }

    public int getParticleCount() {
        return this.particles.size();
    }

}
