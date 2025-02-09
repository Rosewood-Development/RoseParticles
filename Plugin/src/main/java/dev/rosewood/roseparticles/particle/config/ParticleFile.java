package dev.rosewood.roseparticles.particle.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.curve.CurveDefinition;
import dev.rosewood.roseparticles.component.event.EventDefinition;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public record ParticleFile(File file,
                           String formatVersion,
                           ParticleDescription description,
                           List<CurveDefinition> curves,
                           Map<String, EventDefinition> events,
                           Map<ComponentType<?>, Object> components) {

    public static final String FORMAT_VERSION = "1.10.0";

    /**
     * Parses a Minecraft Bedrock Edition particle json file into a java object tree
     *
     * @param file The file to read
     * @return A created ParticleFile instance, or null if the file failed to load
     */
    public static ParticleFile parse(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();

            String formatVersion = jsonObject.get("format_version").getAsString();
            if (!formatVersion.equals(FORMAT_VERSION))
                RoseParticles.getInstance().getLogger().warning("Unsupported particle file format_version %s in file %s, only %s is supported; will try to parse anyway".formatted(formatVersion, file.getName(), FORMAT_VERSION));

            JsonObject particleEffectObject = jsonObject.get("particle_effect").getAsJsonObject();

            ParticleDescription description = ParticleDescription.parse(particleEffectObject.get("description").getAsJsonObject());

            List<CurveDefinition> curves;
            if (particleEffectObject.has("curves")) {
                JsonObject curvesObject = particleEffectObject.get("curves").getAsJsonObject();
                Set<String> curveIdentifiers = curvesObject.keySet();
                curves = new ArrayList<>(curveIdentifiers.size());
                for (String identifier : curveIdentifiers) {
                    JsonObject curveObject = curvesObject.get(identifier).getAsJsonObject();
                    CurveDefinition curveDefinition = CurveDefinition.parse(identifier, curveObject);
                    curves.add(curveDefinition);
                }
            } else {
                curves = List.of();
            }

            Map<String, EventDefinition> events;
            if (particleEffectObject.has("events")) {
                JsonObject eventsObject = particleEffectObject.get("events").getAsJsonObject();
                Set<String> eventIdentifiers = eventsObject.keySet();
                events = new HashMap<>();
                for (String identifier : eventIdentifiers) {
                    JsonObject eventObject = eventsObject.get(identifier).getAsJsonObject();
                    EventDefinition eventDefinition = EventDefinition.parse(eventObject);
                    events.put(identifier.toLowerCase(), eventDefinition);
                }
            } else {
                events = Map.of();
            }

            JsonObject componentsObject = particleEffectObject.get("components").getAsJsonObject();

            Map<ComponentType<?>, Object> components = new HashMap<>();
            for (ComponentType<?> componentType : ComponentType.values()) {
                String identifier = componentType.getIdentifier();
                JsonElement componentElement = componentsObject.get(identifier);
                if (componentElement != null) {
                    try {
                        Object component = componentType.createComponent(componentElement);
                        components.put(componentType, component);
                    } catch (Exception e) {
                        RoseParticles.getInstance().getLogger().warning("Invalid particle_effect component \"%s\" in file %s, ignoring it and trying to continue".formatted(identifier, file.getName()));
                        e.printStackTrace();
                    }
                }
            }

            return new ParticleFile(file, formatVersion, description, curves, events, components);
        } catch (Exception e) {
            RoseParticles.getInstance().getLogger().warning("Invalid particle file: %s".formatted(file.getName()));
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getComponent(ComponentType<T> componentType) {
        return (T) this.components.get(componentType);
    }

}
