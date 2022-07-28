package de.saschat.createcomputing;

import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static VoxelShape rotate(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    public static Object blowNBT(Tag tag) {

        Map<Object, Object> ret = new HashMap<>();
        if (tag == null)
            return ret;
        if (tag instanceof CompoundTag com) {
            for (String key : com.getAllKeys()) {
                ret.put(key, blowNBT(com.get(key)));
            }
            return ret;
        }
        if (tag instanceof CollectionTag lis) {
            int idx = 1;
            for (Object t : lis) {
                ret.put(idx, t instanceof Tag ? blowNBT((Tag) t) : t);
                idx++;
            }
            return ret;
        }
        if (tag instanceof IntTag t) {
            return t.getAsNumber();
        }
        if (tag instanceof ByteTag t) {
            return t.getAsNumber();
        }
        if (tag instanceof ShortTag t) {
            return t.getAsNumber();
        }
        if (tag instanceof LongTag t) {
            return t.getAsNumber();
        }
        if (tag instanceof FloatTag t) {
            return t.getAsNumber();
        }
        if (tag instanceof DoubleTag t) {
            return t.getAsNumber();
        }
        if (tag instanceof StringTag t) {
            return t.getAsString();
        }
        System.err.println("Invalid tag type: " + tag.getClass().getName());
        return null;
    }
}
