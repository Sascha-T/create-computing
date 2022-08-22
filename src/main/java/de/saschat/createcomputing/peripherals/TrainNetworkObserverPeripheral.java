package de.saschat.createcomputing.peripherals;

import com.google.gson.Gson;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.item.filter.AttributeFilterContainer;
import com.simibubi.create.content.logistics.item.filter.FilterItem;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.TrackNodeLocation;
import com.simibubi.create.content.logistics.trains.entity.Carriage;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.display.GlobalTrainDisplayData;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.observer.TrackObserver;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBoundary;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalTileEntity;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.schedule.ScheduleEntry;
import com.simibubi.create.content.logistics.trains.management.schedule.condition.ScheduleWaitCondition;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.saschat.createcomputing.CreateComputingMod;
import de.saschat.createcomputing.Utils;
import de.saschat.createcomputing.api.SmartPeripheral;
import de.saschat.createcomputing.tiles.TrainNetworkObserverTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TrainNetworkObserverPeripheral extends SmartPeripheral {
    TrainNetworkObserverTile parent;

    public List<Train> getTrains() {
        List<Train> trainList = new LinkedList<>();
        GraphLocation graphLocation = parent.getGraphLocation();
        for (Train train : Create.RAILWAYS.trains.values()) {
            if (train.graph.id.equals(graphLocation.graph.id))
                trainList.add(train);
        }
        return trainList;
    }

    public Collection<GlobalStation> getStations() {
        return parent.getGraphLocation().graph.getPoints(EdgePointType.STATION);
    }

    private Collection<SignalBoundary> getSignals() {
        return parent.getGraphLocation().graph.getPoints(EdgePointType.SIGNAL);
    }

    private Collection<TrackObserver> getObservers() {
        return parent.getGraphLocation().graph.getPoints(EdgePointType.OBSERVER);
    }

    public TrainNetworkObserverPeripheral(TrainNetworkObserverTile tile) {
        this.parent = tile;
        // TRAINS
        addMethod("getTrains", (iComputerAccess, iLuaContext, iArguments) -> {
            List<Train> trainList = getTrains();
            return MethodResult.of(trainList.stream().map(a -> a.id.toString()).collect(Collectors.toList()));
        });
        addMethod("getTrainName", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<Train> first = getTrains().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            Train train = first.get();
            return MethodResult.of(new Gson().fromJson(Component.Serializer.toJson(train.name), Map.class));
        });
        addMethod("getTrainSchedule", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<Train> first = getTrains().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            Train train = first.get();
            Map<Object, Object> map = new HashMap<>();
            if (train.runtime.getSchedule() == null)
                return MethodResult.of(null);
            int bidx = 1;
            for (ScheduleEntry entry : train.runtime.getSchedule().entries) {
                Map<Object, Object> data = new HashMap<>();
                data.put("type", entry.instruction.getId().toString());
                data.put("data", blowNBT(entry.instruction.getData()));
                int sidx = 1;
                for (List<ScheduleWaitCondition> conditionLayer : entry.conditions) {
                    Map<Object, Object> conditionL = new HashMap<>();
                    int idx = 1;
                    for (ScheduleWaitCondition cond : conditionLayer) {
                        Map<Object, Object> condition = new HashMap<>();

                        condition.put("type", cond.getId().toString());
                        condition.put("data", blowNBT(cond.getData()));

                        conditionL.put(idx, condition);
                        idx++;
                    }
                    data.put(sidx, conditionL);
                    sidx++;
                }
                map.put(bidx, data);
                bidx++;
            }
            return MethodResult.of(map);
        });
        addMethod("getTrainWorldPosition", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<Train> first = getTrains().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            Train train = first.get();

            Optional<Carriage.DimensionalCarriageEntity> carr = Optional.empty();
            Carriage.DimensionalCarriageEntity a2 = train.carriages.get(0).getDimensionalIfPresent(Level.NETHER);
            if (a2 != null)
                carr = Optional.of(a2);
            Carriage.DimensionalCarriageEntity a1 = train.carriages.get(0).getDimensionalIfPresent(Level.OVERWORLD);
            if (a1 != null)
                carr = Optional.of(a1);

            if (carr.isEmpty())
                return MethodResult.of(null);
            Carriage.DimensionalCarriageEntity car = carr.get();
            CarriageContraptionEntity ent = car.entity.get();
            return MethodResult.of(ent.getX(), ent.getY(), ent.getZ(), ent.getLevel().dimension().getRegistryName().toString());
        });
        addMethod("getTrainSpeed", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<Train> first = getTrains().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            Train train = first.get();
            return MethodResult.of(train.speed);
        });
        addMethod("getTrainStopped", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<Train> first = getTrains().stream().filter(a -> a.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            Train train = first.get();
            GlobalStation currentStation = train.getCurrentStation();
            if (currentStation != null)
                return MethodResult.of(currentStation.getId().toString(), currentStation.name);
            return MethodResult.of(null);
        });
        // STOPS
        addMethod("getStops", ((iComputerAccess, iLuaContext, iArguments) -> {
            Collection<GlobalStation> stations = getStations();
            return MethodResult.of(stations.stream().map(a -> a.id.toString()).collect(Collectors.toList()));
        }));
        addMethod("getStopName", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<GlobalStation> first = getStations().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            GlobalStation station = first.get();
            return MethodResult.of(station.name);
        });
        addMethod("getStopWorldPosition", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<GlobalStation> first = getStations().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            GlobalStation station = first.get();
            return MethodResult.of(station.tilePos.getX(), station.tilePos.getY(), station.tilePos.getZ());
        });
        addMethod("getStopExpectedTrain", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<GlobalStation> first = getStations().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            GlobalStation station = first.get();
            List<GlobalTrainDisplayData.TrainDeparturePrediction> prepare = GlobalTrainDisplayData.prepare(station.name, 200);
            Map<Integer, Map<String, Object>> data = new HashMap();
            int idx = 1;
            for (GlobalTrainDisplayData.TrainDeparturePrediction dep : prepare) {
                Map<String, Object> subData = new HashMap<>();
                subData.put("destination", dep.destination); // obvious lol
                subData.put("ticks", dep.ticks);
                subData.put("scheduleName", new Gson().fromJson(Component.Serializer.toJson(dep.scheduleTitle), Map.class));
                subData.put("train", dep.train.id.toString());
                data.put(idx, subData);
                idx++;
            }
            return MethodResult.of(data);
        });
        // SIGNALS
        addMethod("getSignals", ((iComputerAccess, iLuaContext, iArguments) -> {
            Collection<SignalBoundary> stations = getSignals();
            return MethodResult.of(stations.stream().map(a -> a.id.toString()).collect(Collectors.toList()));
        }));
        addMethod("getSignalWorldPositions", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<SignalBoundary> first = getSignals().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            SignalBoundary signal = first.get();
            Map<Integer, Map<Integer, Integer>> returned = new HashMap<>();
            int idx = 1;
            // fix shitcode and implications
            for (Map.Entry<BlockPos, Boolean> map : signal.blockEntities.get(true).entrySet()) {
                Map<Integer, Integer> pos = new HashMap<>();
                pos.put(1, map.getKey().getX());
                pos.put(2, map.getKey().getY());
                pos.put(3, map.getKey().getZ());
                returned.put(idx, pos);
                idx++;
            }
            for (Map.Entry<BlockPos, Boolean> map : signal.blockEntities.get(false).entrySet()) {
                Map<Integer, Integer> pos = new HashMap<>();
                pos.put(1, map.getKey().getX());
                pos.put(2, map.getKey().getY());
                pos.put(3, map.getKey().getZ());
                returned.put(idx, pos);
                idx++;
            }
            return MethodResult.of(returned);
        });
        addMethod("getSignalState", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            boolean toPos = iArguments.getBoolean(1);
            Optional<SignalBoundary> first = getSignals().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            SignalBoundary signal = first.get();
            SignalTileEntity.SignalState stateFor = signal.cachedStates.get(toPos);
            return switch (stateFor) {
                case RED -> MethodResult.of(0);
                case YELLOW -> MethodResult.of(1);
                case GREEN -> MethodResult.of(2);
                case INVALID -> MethodResult.of(-1);
            };
        });
        // OBSERVERS
        addMethod("getObservers", ((iComputerAccess, iLuaContext, iArguments) -> {
            Collection<TrackObserver> stations = getObservers();
            return MethodResult.of(stations.stream().map(a -> a.id.toString()).collect(Collectors.toList()));
        }));
        addMethod("getObserverWorldPosition", (iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<TrackObserver> first = getObservers().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            TrackObserver obs = first.get();
            return MethodResult.of(obs.tilePos.getX(), obs.tilePos.getY(), obs.tilePos.getZ());
        });
        addMethod("getObserverFilter", ((iComputerAccess, iLuaContext, iArguments) -> {
            String b = iArguments.getString(0);
            Optional<TrackObserver> first = getObservers().stream().filter(a -> a.id.toString().equals(b)).findFirst();
            if (first.isEmpty())
                return MethodResult.of(null);
            TrackObserver obs = first.get();
            return MethodResult.of(blowFilter(obs.getFilter()));
        }));
        // GRAPH
        addMethod("getGraph", ((iComputerAccess, iLuaContext, iArguments) -> {
            Set<TrackNodeLocation> nodes = parent.getGraphLocation().graph.getNodes();
            List<Object> map = new LinkedList<>();
            int index = 0;
            for (TrackNodeLocation location : nodes) {
                Map<Object, Object> map2 = new HashMap<>();
                map2.put("x", (Number) location.getX());
                map2.put("y", (Number) location.getY());
                map2.put("z", (Number) location.getZ());
                map2.put("dimension", location.dimension.location().toString());
                map2.put("bezier", false);
                if (location instanceof TrackNodeLocation.DiscoveredLocation disc) {
                    BezierConnection turn = disc.getTurn();
                    if (turn != null) {
                        map2.put("bezier", true);
                        map2.put("girder", turn.hasGirder);
                        map2.put("primary", turn.hasGirder);
                        map2.put("positions", List.of(
                            List.of(turn.tePositions.get(true).getX(), turn.tePositions.get(true).getY(), turn.tePositions.get(true).getZ()),
                            List.of(turn.tePositions.get(false).getX(), turn.tePositions.get(false).getY(), turn.tePositions.get(false).getZ())
                        ));
                        map2.put("starts", List.of(
                            List.of(turn.starts.get(true).x, turn.starts.get(true).y, turn.starts.get(true).z),
                            List.of(turn.starts.get(false).x, turn.starts.get(false).y, turn.starts.get(false).z)
                        ));
                        map2.put("axes", List.of(
                            List.of(turn.axes.get(true).x, turn.axes.get(true).y, turn.axes.get(true).z),
                            List.of(turn.axes.get(false).x, turn.axes.get(false).y, turn.axes.get(false).z)
                        ));
                        map2.put("normals", List.of(
                            List.of(turn.normals.get(true).x, turn.normals.get(true).y, turn.normals.get(true).z),
                            List.of(turn.normals.get(false).x, turn.normals.get(false).y, turn.normals.get(false).z)
                        ));
                    }
                }
                map.add(map2);
            }
            return MethodResult.of(map);
        }));
    }

    private static Map<Object, Object> blowFilter(ItemStack filter) {
        Map<Object, Object> ret = new HashMap<>();
        if (filter.is(AllItems.FILTER.get())) {
            ItemStackHandler filterItems = FilterItem.getFilterItems(filter);
            ret.put("type", "filter");
            ret.put("whitelist", !filter.getTag().getBoolean("Blacklist"));
            ret.put("respectnbt", !filter.getTag().getBoolean("RespectNBT"));
            for (int i = 1; i < filterItems.getSlots() + 1; i++) {
                ret.put(i - 1, blowFilter(filterItems.getStackInSlot(i)));
            }
            return ret;
        }
        if (filter.is(AllItems.ATTRIBUTE_FILTER.get())) {
            ret.put("type", "attribute");
            AttributeFilterContainer.WhitelistMode whitelistMode = AttributeFilterContainer.WhitelistMode.values()[filter.getTag().getInt("WhitelistMode")];
            ret.put("allowmode", whitelistMode.toString());
            ListTag tag = filter.getTag().getList("MatchedAttributes", Tag.TAG_COMPOUND);
            int idx = 1;
            for (Tag inb : tag) {
                CompoundTag tg = (CompoundTag) inb;
                ret.put(idx, blowNBT(tg));
                idx++;
            }
            return ret;
        }
        ret.put("type", "item");
        ret.put("count", filter.getCount());
        ret.put("id", filter.getItem().getRegistryName().toString());
        ret.put("nbt", blowNBT(filter.getTag()));
        return ret;
    }

    public static final boolean DEBUG_NBT = false;
    private static Object blowNBT(Tag tag) {
        Object b = Utils.blowNBT(tag);
        if(DEBUG_NBT) {
            System.out.println("======================");
            System.out.println("in: " + tag.getClass().getName());
            System.out.println("out: " + b.getClass().getName());
        }
        return b;
    }

    @NotNull
    @Override
    public String getType() {
        return new ResourceLocation(CreateComputingMod.MOD_ID, "train_network_observer").toString();
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }
}
