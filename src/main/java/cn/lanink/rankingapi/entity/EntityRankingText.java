package cn.lanink.rankingapi.entity;

import cn.lanink.rankingapi.RankingAPI;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class EntityRankingText implements IEntityRanking {

    @Getter
    protected final long id;

    @Setter
    @Getter
    private Position position;

    public static final EntityMetadata entityMetadata;

    static {
        //使用反射获取，保证数据是最新的
        entityMetadata = new EntityMetadata()
                .putLong(getField("DATA_FLAGS", 0), 0)
                .putByte(getField("DATA_COLOR", 3), 0)
                .putString(getField("DATA_NAMETAG", 4), "")
                .putLong(getField("DATA_LEAD_HOLDER_EID", 37), -1L)
                .putFloat(getField("DATA_SCALE", 38), 1F)
                .putBoolean(getField("DATA_ALWAYS_SHOW_NAMETAG", 81), true);
        long flags = entityMetadata.getLong(getField("DATA_FLAGS", 0));
        flags ^= 1L << getField("DATA_FLAG_IMMOBILE", 16);
        entityMetadata.put(new LongEntityData(getField("DATA_FLAGS", 0), flags));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Number> T getField(String name, T defaultValue) {
        try {
            Field field = Entity.class.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (Exception e) {
            RankingAPI.getInstance().getLogger().error("反射获取数据时出现错误！", e);
        }
        return defaultValue;
    }

    @Setter
    @Getter
    private boolean allPlayerCanSee = true;

    private final Set<Player> hasSpawned = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter
    protected Map<Player, String> showTextMap = new ConcurrentHashMap<>();

    public EntityRankingText() {
        this.id = Entity.entityCount++;
    }

    public void setShowText(@NotNull Player player, @NotNull String showText) {
        this.showTextMap.put(player, showText);
    }

    @Override
    public boolean needTick() {
        return false;
    }

    @Override
    public void onTick(int i) {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            RankingAPI.getInstance().getLogger().error("错误调用！", e);
        }
    }

    @Override
    public void onAsyncTick(int i) {
        if (i%20 != 0) {
            return;
        }
        for (Map.Entry<Player, String> entry : this.getShowTextMap().entrySet()) {
            if (entry.getKey().getLevel() == this.getPosition().getLevel()) {
                if (!this.hasSpawned.contains(entry.getKey())) {
                    this.spawnTo(entry.getKey());
                }
                this.sendText(entry.getKey(), entry.getValue());
            }
        }
        for (Player player : this.hasSpawned) {
            if (!this.getShowTextMap().containsKey(player) ||
                    !player.isOnline() ||
                    player.getLevel() != this.getPosition().getLevel()) {
                this.despawnFrom(player);
            }
        }
    }

    @Override
    public void spawnTo(@NotNull Player player) {
        if (this.hasSpawned.contains(player)) {
            this.despawnFrom(player);
        }
        this.hasSpawned.add(player);
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityRuntimeId = this.getId();
        pk.entityUniqueId = this.getId();
        pk.type = 64;
        pk.yaw = 0;
        pk.headYaw = 0;
        pk.pitch = 0;
        pk.x = (float) this.position.getX();
        pk.y = (float) this.position.getY();
        pk.z = (float) this.position.getZ();
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.metadata = entityMetadata;
        player.dataPacket(pk);
    }

    @Override
    public void despawnFrom(@NotNull Player player) {
        if (this.hasSpawned.contains(player)) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player);
        }
    }

    @Override
    public void close() {
        this.getShowTextMap().clear();
        for (Player player : this.hasSpawned) {
            this.despawnFrom(player);
        }
    }

    private void sendText(@NotNull Player player, @NotNull String string) {
        this.sendData(new Player[] {player},
                (new EntityMetadata()).putString(Entity.DATA_NAMETAG, string));
    }

    private void sendData(@NotNull Player[] players, @NotNull EntityMetadata data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.metadata = data;

        for (Player player : players) {
            if (this.hasSpawned.contains(player)) {
                player.dataPacket(pk.clone());
            }
        }
    }

}
