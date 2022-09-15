package de.saschat.createcomputing.tiles;

import com.simibubi.create.content.logistics.block.redstone.RedstoneLinkFrequencySlot;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.linked.LinkBehaviour;
import dan200.computercraft.shared.Capabilities;
import de.saschat.createcomputing.Registries;
import de.saschat.createcomputing.Utils;
import de.saschat.createcomputing.config.CreateComputingConfigServer;
import de.saschat.createcomputing.peripherals.ComputerizedRedstoneLinkPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ComputerizedRedstoneLinkTile extends SmartTileEntity {
    public ConcurrentHashMap<UUID, LinkPair> pairs = new ConcurrentHashMap<>();
    public List<LinkPair> deferred = new LinkedList<>();
    public Queue<Runnable> tasks = new ArrayBlockingQueue<>(32);

    public LazyOptional<ComputerizedRedstoneLinkPeripheral> PERIPHERAL = LazyOptional.of(
        () -> new ComputerizedRedstoneLinkPeripheral(this)
    );

    public ComputerizedRedstoneLinkTile(BlockPos p_155229_, BlockState p_155230_) {
        super(Registries.COMPUTERIZED_REDSTONE_LINK_TILE.get(), p_155229_, p_155230_);
    }


    int i = 0;

    @Override
    public void tick() {
        super.tick();
        if (!getLevel().isClientSide()) {
            Runnable a;
            while ((a = tasks.poll()) != null) {
                a.run();
            }
        }
        pairs.values().forEach(linkPair -> linkPair.transmit.tick());
        pairs.values().forEach(linkPair -> linkPair.receive.tick());
    }

    public UUID add(LinkPair pair) {
        if (pairs.size() >= CreateComputingConfigServer.get().MAXIMUM_CONCURRENT_LINKS.get())
            return null;
        UUID uuid = UUID.randomUUID();
        pair.index = uuid;
        tasks.add(() -> {
            pair.transmit.initialize();
            pair.receive.initialize();
        });
        pairs.put(uuid, pair);
        return uuid;
    }

    public void addDeferred(LinkPair pair) {
        deferred.add(pair);
    }

    public void remove(LinkPair pair) {
        if (PERIPHERAL.isPresent())
            PERIPHERAL.resolve().get().killHandles(pair.index);
        pairs.remove(pair.index, pair);
        pair.sendSignal = 0;
        pair.transmit.notifySignalChange();
        pair.transmit.remove();
        pair.receive.remove();
    }


    /*
        @Override
        protected void write(CompoundTag tag, boolean clientPacket) {
            super.write(tag, clientPacket);
            System.out.println("WRITING... " + clientPacket);
            ListTag behaviours = new ListTag();
            for (LinkPair value : pairs.values()) {
                behaviours.add(value.toTag());
            }
            tag.put("transceivers", behaviours);
            System.out.println(Utils.blowNBT(tag));
        }

        @Override
        protected void read(CompoundTag tag, boolean clientPacket) {
            super.read(tag, clientPacket);
            System.out.println("READING... " + clientPacket);
            System.out.println(Utils.blowNBT(tag));
            ListTag transceivers = tag.getList("transceivers", Tag.TAG_COMPOUND);
            for (Tag transceiver : transceivers) {
                LinkPair behaviour = LinkPair.fromTag(this, (CompoundTag) transceiver);
                addDeferred(behaviour);
                PERIPHERAL.resolve().get().handles.put(behaviour.index, new RedstoneHandle(
                    PERIPHERAL.resolve().get(),
                    behaviour.index
                ));
            }

        }
    */
    @Override
    public void addBehavioursDeferred(List<TileEntityBehaviour> behaviours) {
        super.addBehavioursDeferred(behaviours);
        for (LinkPair linkPair : deferred) {
            pairs.put(linkPair.index, linkPair);
            linkPair.registered = true;
            linkPair.setFrequency(linkPair.items);
        }
    }

    public static boolean checkItem(Item stack) {
        for (String loc : CreateComputingConfigServer.get().BANNED_LINK_ITEMS.get()) {
            if (loc.equals(ForgeRegistries.ITEMS.getKey(stack).toString()))
                return false;
        }
        return true;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> list) {
    }

    private static boolean firstRun = true;

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (side == Direction.DOWN && cap == Capabilities.CAPABILITY_PERIPHERAL) {
            return PERIPHERAL.cast();
        }
        return super.getCapability(cap, side);
    }

    public static class LinkPair {
        private final LinkBehaviour transmit;
        private final LinkBehaviour receive;
        public UUID index;

        public boolean registered = false;

        public int sendSignal = 0;
        public int recvSignal = 0;
        ComputerizedRedstoneLinkTile parent;

        public Item[] items;

        public List<Utils.Receiver<Integer>> listeners = new ArrayList<>();

        public LinkPair(ComputerizedRedstoneLinkTile parent, Item[] items) {
            this.parent = parent;
            Pair<ValueBoxTransform, ValueBoxTransform> slots =
                ValueBoxTransform.Dual.makeSlots(RedstoneLinkFrequencySlot::new);
            transmit = LinkBehaviour.transmitter(parent, slots, this::getSignal);
            receive = LinkBehaviour.receiver(parent, slots, this::setSignal);
            this.items = items;
            parent.setChanged();
        }

        /*public CompoundTag toTag() {
            CompoundTag us = new CompoundTag();
            us.putUUID("index", index);
            us.putByte("send", (byte) sendSignal);
            us.putByte("receive", (byte) recvSignal);
            us.putString("frequency.0", items[0].getRegistryName().toString());
            us.putString("frequency.1", items[1].getRegistryName().toString());
            return us;
        }*/

        public static LinkPair fromTag(ComputerizedRedstoneLinkTile parent, CompoundTag tag) {
            Item[] items = new Item[]{
                Utils.getByName(new ResourceLocation(tag.getString("frequency.0"))),
                Utils.getByName(new ResourceLocation(tag.getString("frequency.1"))),
            };
            LinkPair pair = new LinkPair(parent, items);
            pair.index = tag.getUUID("index");
            pair.setSignal(tag.getByte("send"));
            pair.recvSignal = tag.getByte("receive");
            return pair;
        }

        public void setFrequency(Item[] items) {
            parent.tasks.add(() -> {
                transmit.setFrequency(true, new ItemStack(items[0]));
                transmit.setFrequency(false, new ItemStack(items[1]));
                receive.setFrequency(true, new ItemStack(items[0]));
                receive.setFrequency(false, new ItemStack(items[1]));
                this.items = items;
            });
            dirty();
        }

        public void provideSignal(int strength) {
            sendSignal = strength;
            dirty();
        }

        public int retrieveSignal() {
            return recvSignal;
        }

        private void dirty() {
            if (parent.getLevel().isClientSide())
                return;
            parent.tasks.add(this::_dirty);
            parent.setChanged();
        }

        private void _dirty() {
            transmit.notifySignalChange();
            receive.notifySignalChange();
        }

        private void setSignal(int i) {
            recvSignal = i;
            listeners.forEach(a -> a.receive(i));
        }

        private int getSignal() {
            return sendSignal;
        }
    }
}
