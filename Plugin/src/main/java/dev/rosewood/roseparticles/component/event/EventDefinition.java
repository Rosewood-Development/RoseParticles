package dev.rosewood.roseparticles.component.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.particle.ParticleInstance;
import dev.rosewood.roseparticles.particle.ParticleSystem;
import dev.rosewood.roseparticles.util.JsonHelper;

public record EventDefinition(ParticleEvent particle,
                              SoundEvent sound,
                              MolangExpression expression,
                              SequenceEvent sequence,
                              RandomizeEvent randomize) {

    public static EventDefinition parse(JsonObject jsonObject) throws MolangLexException, MolangParseException {
        ParticleEvent particle = null;
        SoundEvent sound = null;
        MolangExpression expression = null;
        SequenceEvent sequence = null;
        RandomizeEvent randomize = null;

        JsonElement particleElement = jsonObject.get("particle_effect");
        if (particleElement != null)
            particle = ParticleEvent.parse(particleElement.getAsJsonObject());

        JsonElement soundElement = jsonObject.get("sound_effect");
        if (soundElement != null)
            sound = SoundEvent.parse(soundElement.getAsJsonObject());

        expression = JsonHelper.parseMolang(jsonObject, "expression", null);

        JsonElement sequenceElement = jsonObject.get("sequence");
        if (sequenceElement != null)
            sequence = SequenceEvent.parse(sequenceElement.getAsJsonArray());

        JsonElement randomizeElement = jsonObject.get("randomize");
        if (randomizeElement != null)
            randomize = RandomizeEvent.parse(randomizeElement.getAsJsonArray());

        return new EventDefinition(particle, sound, expression, sequence, randomize);
    }

    public void play(ParticleSystem particleSystem, ParticleInstance particleInstance) {
        if (this.particle != null)
            this.particle.play(particleSystem, particleInstance);

        if (this.sound != null)
            this.sound.play(particleSystem, particleInstance);

        if (this.expression != null)
            this.expression.bind(particleSystem.getMolangContext(), particleSystem.getEmitter(), particleInstance);

        if (this.sequence != null)
            this.sequence.play(particleSystem, particleInstance);

        if (this.randomize != null)
            this.randomize.play(particleSystem, particleInstance);
    }

}
