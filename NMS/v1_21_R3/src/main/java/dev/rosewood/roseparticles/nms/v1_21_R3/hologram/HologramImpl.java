package dev.rosewood.roseparticles.nms.v1_21_R3.hologram;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.rosewood.roseparticles.nms.hologram.Hologram;
import dev.rosewood.roseparticles.nms.hologram.HologramProperty;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public class HologramImpl extends Hologram {

    private static final LoadingCache<String, Component> textJsonComponentCache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.of(1, ChronoUnit.MINUTES))
            .build(new CacheLoader<>() {
                @Override
                public Component load(String textJson) {
                    return CraftChatMessage.fromJSON(textJson);
                }
            });

    public HologramImpl(World world, int entityId, Consumer<Hologram> init) {
        super(world, entityId, init);
    }

    @Override
    protected void create(Player player) {
        Vector location = this.properties.get(HologramProperty.POSITION);

        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
                this.entityId,
                UUID.randomUUID(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0,
                0,
                EntityType.TEXT_DISPLAY,
                0,
                Vec3.ZERO,
                0
        );

        List<Packet<? super ClientGamePacketListener>> packets = this.createMetadataPacket(this.properties.getAvailable(), false);
        if (packets.isEmpty()) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        } else {
            packets.addFirst(packet);
            ((CraftPlayer) player).getHandle().connection.send(new ClientboundBundlePacket(packets));
        }
    }

    @Override
    protected void update(Collection<Player> players, boolean force) {
        Set<HologramProperty<?>> propertiesSet = force ? this.properties.getAvailable() : this.properties.getDirty();
        if (propertiesSet.isEmpty())
            return;

        List<Packet<? super ClientGamePacketListener>> packets = this.createMetadataPacket(propertiesSet, true);
        if (packets.size() == 1) {
            Packet<?> packet = packets.getFirst();
            for (Player player : players) {
                var connection = ((CraftPlayer) player).getHandle().connection;
                connection.send(packet);
            }
        } else if (packets.size() > 1) {
            for (Player player : players) {
                var connection = ((CraftPlayer) player).getHandle().connection;
                connection.send(new ClientboundBundlePacket(packets));
            }
        }

        if (!force)
            this.properties.clearDirty();
    }

    private List<Packet<? super ClientGamePacketListener>> createMetadataPacket(Set<HologramProperty<?>> propertiesSet, boolean includeLocation) {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();

        if (propertiesSet.contains(HologramProperty.BILLBOARD)) {
            Display.Billboard billboard = this.properties.get(HologramProperty.BILLBOARD);
            byte value = switch (billboard) {
                case FIXED -> 0;
                case VERTICAL -> 1;
                case HORIZONTAL -> 2;
                case CENTER -> 3;
            };
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.BYTE.createAccessor(15), value));
        }

        if (propertiesSet.contains(HologramProperty.TEXT_JSON)) {
            String textJson = this.properties.get(HologramProperty.TEXT_JSON);
            Component chatMessage = textJsonComponentCache.getUnchecked(textJson);
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.COMPONENT.createAccessor(23), chatMessage));
        }

        if (propertiesSet.contains(HologramProperty.TRANSFORMATION)) {
            Transformation transformation = this.properties.get(HologramProperty.TRANSFORMATION);
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.VECTOR3.createAccessor(11), transformation.getTranslation()));
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.VECTOR3.createAccessor(12), transformation.getScale()));
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.QUATERNION.createAccessor(13), transformation.getLeftRotation()));
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.QUATERNION.createAccessor(14), transformation.getRightRotation()));
        }

        if (propertiesSet.contains(HologramProperty.INTERPOLATION_DELAY)) {
            int interpolationDelay = this.properties.get(HologramProperty.INTERPOLATION_DELAY);
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(8), interpolationDelay));
        }

        if (propertiesSet.contains(HologramProperty.TRANSFORMATION_DELAY)) {
            int transformationDelay = this.properties.get(HologramProperty.TRANSFORMATION_DELAY);
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(9), transformationDelay));
        }

        if (propertiesSet.contains(HologramProperty.POSITION_ROTATION_DELAY)) {
            int positionRotationDelay = this.properties.get(HologramProperty.POSITION_ROTATION_DELAY);
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(10), positionRotationDelay));
        }

        if (propertiesSet.contains(HologramProperty.BRIGHTNESS)) {
            Display.Brightness brightness = this.properties.get(HologramProperty.BRIGHTNESS);
            int value = brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20;
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(16), value));
        }

        if (propertiesSet.contains(HologramProperty.BACKGROUND_COLOR)) {
            Color backgroundColor = this.properties.get(HologramProperty.BACKGROUND_COLOR);
            int value = backgroundColor.asARGB();
            dataValues.add(SynchedEntityData.DataValue.create(EntityDataSerializers.INT.createAccessor(25), value));
        }

        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(4);

        if (!dataValues.isEmpty())
            packets.add(new ClientboundSetEntityDataPacket(this.entityId, dataValues));

        if (includeLocation && propertiesSet.contains(HologramProperty.POSITION)) {
            Vector previous = this.properties.getPreviousPosition();
            Vector current = this.properties.get(HologramProperty.POSITION);
            if (Math.abs(previous.distanceSquared(current)) > 7.5) {
                Vec3 position = new Vec3(current.getX(), current.getY(), current.getZ());
                packets.add(new ClientboundTeleportEntityPacket(this.entityId, new PositionMoveRotation(position, Vec3.ZERO, 0, 0), Set.of(), false));
            } else {
                short deltaX = (short) (Math.round(current.getX() * 4096) - Math.round(previous.getX() * 4096));
                short deltaY = (short) (Math.round(current.getY() * 4096) - Math.round(previous.getY() * 4096));
                short deltaZ = (short) (Math.round(current.getZ() * 4096) - Math.round(previous.getZ() * 4096));
                packets.add(new ClientboundMoveEntityPacket.Pos(this.entityId, deltaX, deltaY, deltaZ, false));
            }
        }

        return packets;
    }

    @Override
    protected void delete(Player player) {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(this.entityId);

        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

}
