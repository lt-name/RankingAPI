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
        //TODO 使用反射获取数据，以便于在nk更新时不需要再次编译
        entityMetadata = new EntityMetadata()
                .putLong(Entity.DATA_FLAGS, 0)
                .putByte(Entity.DATA_COLOR, 0)
                .putString(Entity.DATA_NAMETAG, "")
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putFloat(Entity.DATA_SCALE, 1f)
                .putBoolean(Entity.DATA_ALWAYS_SHOW_NAMETAG, true);
        long flags = entityMetadata.getLong(Entity.DATA_FLAGS);
        flags ^= 1 << Entity.DATA_FLAG_IMMOBILE;
        entityMetadata.put(new LongEntityData(Entity.DATA_FLAGS, flags));
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

    public void setShowText(Player player, String showText) {
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
    public void spawnTo(Player player) {
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
    public void despawnFrom(Player player) {
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

    public void sendText(Player player, String string) {
        this.sendData(new Player[] {player},
                (new EntityMetadata()).putString(Entity.DATA_NAMETAG, string));
    }

    public void sendData(Player[] players, EntityMetadata data) {
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
