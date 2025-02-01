package dev.rosewood.roseparticles.component;

import com.google.gson.JsonElement;
import dev.rosewood.roseparticles.component.emitter.init.EmitterInitializationComponent;
import dev.rosewood.roseparticles.component.emitter.init.EmitterLocalSpaceComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeEventsComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeExpressionComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeLoopingComponent;
import dev.rosewood.roseparticles.component.emitter.lifetime.EmitterLifetimeOnceComponent;
import dev.rosewood.roseparticles.component.emitter.rate.EmitterRateInstantComponent;
import dev.rosewood.roseparticles.component.emitter.rate.EmitterRateManualComponent;
import dev.rosewood.roseparticles.component.emitter.rate.EmitterRateSteadyComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeBoxComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeCustomComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeDiscComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeEntityAABBComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapePointComponent;
import dev.rosewood.roseparticles.component.emitter.shape.EmitterShapeSphereComponent;
import dev.rosewood.roseparticles.component.particle.appearance.ParticleAppearanceBillboardComponent;
import dev.rosewood.roseparticles.component.particle.appearance.ParticleAppearanceLightingComponent;
import dev.rosewood.roseparticles.component.particle.appearance.ParticleAppearanceTintingComponent;
import dev.rosewood.roseparticles.component.particle.init.ParticleInitialSpeedComponent;
import dev.rosewood.roseparticles.component.particle.init.ParticleInitialSpinComponent;
import dev.rosewood.roseparticles.component.particle.init.ParticleInitializationComponent;
import dev.rosewood.roseparticles.component.particle.lifetime.ParticleExpireIfInBlocksComponent;
import dev.rosewood.roseparticles.component.particle.lifetime.ParticleExpireIfNotInBlocksComponent;
import dev.rosewood.roseparticles.component.particle.lifetime.ParticleKillPlaneComponent;
import dev.rosewood.roseparticles.component.particle.lifetime.ParticleLifetimeEventsComponent;
import dev.rosewood.roseparticles.component.particle.lifetime.ParticleLifetimeExpressionComponent;
import dev.rosewood.roseparticles.component.particle.motion.ParticleMotionCollisionComponent;
import dev.rosewood.roseparticles.component.particle.motion.ParticleMotionDynamicComponent;
import dev.rosewood.roseparticles.component.particle.motion.ParticleMotionParametricComponent;
import java.util.HashMap;
import java.util.Map;

public final class ComponentType<T> {

    private static final Map<String, ComponentType<?>> REGISTRY = new HashMap<>();

    /** [ ] */ public static final ComponentType<EmitterLocalSpaceComponent> EMITTER_LOCAL_SPACE = create("minecraft:emitter_local_space", EmitterLocalSpaceComponent::parse);
    /** [X] */ public static final ComponentType<EmitterInitializationComponent> EMITTER_INITIALIZATION = create("minecraft:emitter_initialization", EmitterInitializationComponent::parse);
    /** [X] */ public static final ComponentType<EmitterRateInstantComponent> EMITTER_RATE_INSTANT = create("minecraft:emitter_rate_instant", EmitterRateInstantComponent::parse);
    /** [X] */ public static final ComponentType<EmitterRateSteadyComponent> EMITTER_RATE_STEADY = create("minecraft:emitter_rate_steady", EmitterRateSteadyComponent::parse);
    /** [ ] */ public static final ComponentType<EmitterRateManualComponent> EMITTER_RATE_MANUAL = create("minecraft:emitter_rate_manual", EmitterRateManualComponent::parse);
    /** [X] */ public static final ComponentType<EmitterLifetimeLoopingComponent> EMITTER_LIFETIME_LOOPING = create("minecraft:emitter_lifetime_looping", EmitterLifetimeLoopingComponent::parse);
    /** [X] */ public static final ComponentType<EmitterLifetimeOnceComponent> EMITTER_LIFETIME_ONCE = create("minecraft:emitter_lifetime_once", EmitterLifetimeOnceComponent::parse);
    /** [X] */ public static final ComponentType<EmitterLifetimeExpressionComponent> EMITTER_LIFETIME_EXPRESSION = create("minecraft:emitter_lifetime_expression", EmitterLifetimeExpressionComponent::parse);
    /** [ ] */ public static final ComponentType<EmitterLifetimeEventsComponent> EMITTER_LIFETIME_EVENTS = create("minecraft:emitter_lifetime_events", EmitterLifetimeEventsComponent::parse);
    /** [X] */ public static final ComponentType<EmitterShapePointComponent> EMITTER_SHAPE_POINT = create("minecraft:emitter_shape_point", EmitterShapePointComponent::parse);
    /** [ ] */ public static final ComponentType<EmitterShapeSphereComponent> EMITTER_SHAPE_SPHERE = create("minecraft:emitter_shape_sphere", EmitterShapeSphereComponent::parse);
    /** [ ] */ public static final ComponentType<EmitterShapeBoxComponent> EMITTER_SHAPE_BOX = create("minecraft:emitter_shape_box", EmitterShapeBoxComponent::parse);
    /** [ ] */ public static final ComponentType<EmitterShapeCustomComponent> EMITTER_SHAPE_CUSTOM = create("minecraft:emitter_shape_custom", EmitterShapeCustomComponent::parse);
    /** [ ] */ public static final ComponentType<EmitterShapeEntityAABBComponent> EMITTER_SHAPE_ENTITY_AABB = create("minecraft:emitter_shape_entity_aabb", EmitterShapeEntityAABBComponent::parse);
    /** [X] */ public static final ComponentType<EmitterShapeDiscComponent> EMITTER_SHAPE_DISC = create("minecraft:emitter_shape_disc", EmitterShapeDiscComponent::parse);
    /** [X] */ public static final ComponentType<ParticleInitialSpeedComponent> PARTICLE_INITIAL_SPEED = create("minecraft:particle_initial_speed", ParticleInitialSpeedComponent::parse);
    /** [X] */ public static final ComponentType<ParticleInitialSpinComponent> PARTICLE_INITIAL_SPIN = create("minecraft:particle_initial_spin", ParticleInitialSpinComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleInitializationComponent> PARTICLE_INITIALIZATION = create("minecraft:partial_initialization", ParticleInitializationComponent::parse);
    /** [X] */ public static final ComponentType<ParticleMotionDynamicComponent> PARTICLE_MOTION_DYNAMIC = create("minecraft:particle_motion_dynamic", ParticleMotionDynamicComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleMotionParametricComponent> PARTICLE_MOTION_PARAMETRIC = create("minecraft:particle_motion_parametric", ParticleMotionParametricComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleMotionCollisionComponent> PARTICLE_MOTION_COLLISION = create("minecraft:particle_motion_collision", ParticleMotionCollisionComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleAppearanceBillboardComponent> PARTICLE_APPEARANCE_BILLBOARD = create("minecraft:particle_appearance_billboard", ParticleAppearanceBillboardComponent::parse);
    /** [X] */ public static final ComponentType<ParticleAppearanceTintingComponent> PARTICLE_APPEARANCE_TINTING = create("minecraft:particle_appearance_tinting", ParticleAppearanceTintingComponent::parse);
    /** [X] */ public static final ComponentType<ParticleAppearanceLightingComponent> PARTICLE_APPEARANCE_LIGHTING = create("minecraft:particle_appearance_lighting", ParticleAppearanceLightingComponent::parse);
    /** [X] */ public static final ComponentType<ParticleLifetimeExpressionComponent> PARTICLE_LIFETIME_EXPRESSION = create("minecraft:particle_lifetime_expression", ParticleLifetimeExpressionComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleLifetimeEventsComponent> PARTICLE_LIFETIME_EVENTS = create("minecraft:particle_lifetime_events", ParticleLifetimeEventsComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleKillPlaneComponent> PARTICLE_KILL_PLANE = create("minecraft:particle_kill_plane", ParticleKillPlaneComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleExpireIfInBlocksComponent> PARTICLE_EXPIRE_IF_IN_BLOCKS = create("minecraft:expire_if_in_blocks", ParticleExpireIfInBlocksComponent::parse);
    /** [ ] */ public static final ComponentType<ParticleExpireIfNotInBlocksComponent> PARTICLE_EXPIRE_IF_NOT_IN_BLOCKS = create("minecraft:expire_if_not_in_blocks", ParticleExpireIfNotInBlocksComponent::parse);

    private static <T> ComponentType<T> create(String identifier, ThrowingFunction<JsonElement, T> componentConstructor) {
        ComponentType<T> componentType = new ComponentType<>(identifier, componentConstructor);
        REGISTRY.put(identifier, componentType);
        return componentType;
    }

    public static Iterable<ComponentType<?>> values() {
        return REGISTRY.values();
    }

    private final String identifier;
    private final ThrowingFunction<JsonElement, T> componentConstructor;

    ComponentType(String identifier, ThrowingFunction<JsonElement, T> componentConstructor) {
        this.identifier = identifier;
        this.componentConstructor = componentConstructor;
    }

    public T createComponent(JsonElement jsonElement) throws Exception {
        return this.componentConstructor.apply(jsonElement);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "ComponentType{" +
                "identifier='" + this.identifier + '\'' +
                '}';
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

}
