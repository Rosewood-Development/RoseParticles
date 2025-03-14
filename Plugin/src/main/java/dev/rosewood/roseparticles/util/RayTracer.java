package dev.rosewood.roseparticles.util;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;

public final class RayTracer {

    private RayTracer() {

    }

    public record RayTraceOutput(Location hitPosition,
                                 Block hitBlock,
                                 BoundingBox collidedBox,
                                 BlockFace hitFace) { }

    public static RayTraceOutput rayTrace(Location start, Vector direction, double maxDistance, double radius) {
        direction = direction.clone().normalize();
        World world = start.getWorld();
        Vector startVec = start.toVector();

        RayTraceOutput closestHit = null;
        double closestDistance = Double.MAX_VALUE;

        double stepSize = 0.1;
        Vector step = direction.clone().multiply(stepSize);
        Location current = start.clone();

        for (double distance = 0; distance <= maxDistance; distance += stepSize) {
            int xMin = (int) Math.floor(current.getX() - radius);
            int xMax = (int) Math.ceil(current.getX() + radius);
            int yMin = (int) Math.floor(current.getY() - radius);
            int yMax = (int) Math.ceil(current.getY() + radius);
            int zMin = (int) Math.floor(current.getZ() - radius);
            int zMax = (int) Math.ceil(current.getZ() + radius);

            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        Block block = world.getBlockAt(x, y, z);

                        VoxelShape collisionShape = block.getCollisionShape();
                        Collection<BoundingBox> boxes = collisionShape.getBoundingBoxes();
                        if (boxes.isEmpty() || block.isLiquid())
                            continue;

                        for (BoundingBox localBox : boxes) {
                            BoundingBox worldBox = localBox.shift(block.getLocation().toVector());

                            if (intersectsSphere(worldBox, current.toVector(), radius)) {
                                RayTraceOutput hit = rayTraceBox(start, direction, maxDistance, worldBox, block);

                                if (hit != null) {
                                    double hitDistance = hit.hitPosition().toVector().distanceSquared(startVec);
                                    if (hitDistance < closestDistance) {
                                        closestHit = hit;
                                        closestDistance = hitDistance;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (closestHit != null)
                return closestHit;

            current.add(step);
        }

        return null;
    }

    private static boolean intersectsSphere(BoundingBox box, Vector center, double radius) {
        double radiusSquared = radius * radius;
        Vector closest = new Vector(
                Math.max(box.getMinX(), Math.min(center.getX(), box.getMaxX())),
                Math.max(box.getMinY(), Math.min(center.getY(), box.getMaxY())),
                Math.max(box.getMinZ(), Math.min(center.getZ(), box.getMaxZ()))
        );
        return closest.distanceSquared(center) <= radiusSquared;
    }

    private static RayTraceOutput rayTraceBox(Location start, Vector direction, double maxDistance, BoundingBox box, Block block) {
        Vector startVec = start.toVector();
        Vector dir = direction.clone().normalize();

        double tMin = 0.0;
        double tMax = maxDistance;
        int hitAxis = -1;
        boolean hitMin = false;

        // X-axis
        double invD = 1.0 / dir.getX();
        double t0 = (box.getMinX() - startVec.getX()) * invD;
        double t1 = (box.getMaxX() - startVec.getX()) * invD;
        boolean xIsMin = invD >= 0;
        if (invD < 0.0) {
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }
        double prevTMin = tMin;
        tMin = Math.max(tMin, t0);
        if (tMin != prevTMin) {
            hitAxis = 0;
            hitMin = xIsMin;
        }
        tMax = Math.min(tMax, t1);
        if (tMax <= tMin)
            return null;

        // Y-axis
        invD = 1.0 / dir.getY();
        t0 = (box.getMinY() - startVec.getY()) * invD;
        t1 = (box.getMaxY() - startVec.getY()) * invD;
        boolean yIsMin = invD >= 0;
        if (invD < 0.0) {
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }
        prevTMin = tMin;
        tMin = Math.max(tMin, t0);
        if (tMin != prevTMin) {
            hitAxis = 1;
            hitMin = yIsMin;
        }
        tMax = Math.min(tMax, t1);
        if (tMax <= tMin)
            return null;

        // Z-axis
        invD = 1.0 / dir.getZ();
        t0 = (box.getMinZ() - startVec.getZ()) * invD;
        t1 = (box.getMaxZ() - startVec.getZ()) * invD;
        boolean zIsMin = invD >= 0;
        if (invD < 0.0) {
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }
        prevTMin = tMin;
        tMin = Math.max(tMin, t0);
        if (tMin != prevTMin) {
            hitAxis = 2;
            hitMin = zIsMin;
        }
        tMax = Math.min(tMax, t1);
        if (tMax <= tMin)
            return null;

        if (tMin > 0 && tMin <= maxDistance) {
            Location hitPos = startVec.clone().add(dir.clone().multiply(tMin)).toLocation(start.getWorld());
            BlockFace face = determineHitFace(hitAxis, hitMin);
            return new RayTraceOutput(hitPos, block, box, face);
        }

        return null;
    }

    private static BlockFace determineHitFace(int axis, boolean isMin) {
        return switch (axis) {
            case 0 -> isMin ? BlockFace.WEST : BlockFace.EAST;
            case 1 -> isMin ? BlockFace.DOWN : BlockFace.UP;
            case 2 -> isMin ? BlockFace.NORTH : BlockFace.SOUTH;
            default -> BlockFace.SELF;
        };
    }

}
