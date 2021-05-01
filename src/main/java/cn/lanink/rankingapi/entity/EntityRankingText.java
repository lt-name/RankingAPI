package cn.lanink.rankingapi.entity;

import cn.lanink.rankingapi.RankingAPI;
import cn.lanink.rankingapi.utils.EntityUtils;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.MoveEntityAbsolutePacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class EntityRankingText extends Position implements IEntityRanking {

    @Getter
    protected final long id;

    public static final EntityMetadata entityMetadata;

    static {
        //使用反射获取，保证数据是最新的
        entityMetadata = new EntityMetadata()
                .putLong(EntityUtils.getEntityField("DATA_FLAGS", Entity.DATA_FLAGS), 0)
                .putByte(EntityUtils.getEntityField("DATA_COLOR", Entity.DATA_COLOR), 0)
                .putString(EntityUtils.getEntityField("DATA_NAMETAG", Entity.DATA_NAMETAG), "")
                .putLong(EntityUtils.getEntityField("DATA_LEAD_HOLDER_EID", Entity.DATA_LEAD_HOLDER_EID), -1L)
                .putFloat(EntityUtils.getEntityField("DATA_SCALE", Entity.DATA_SCALE), 1F)
                .putBoolean(EntityUtils.getEntityField("DATA_ALWAYS_SHOW_NAMETAG", Entity.DATA_ALWAYS_SHOW_NAMETAG), true);
        long flags = entityMetadata.getLong(EntityUtils.getEntityField("DATA_FLAGS", Entity.DATA_FLAGS));
        flags ^= 1L << EntityUtils.getEntityField("DATA_FLAG_IMMOBILE", Entity.DATA_FLAG_IMMOBILE);
        entityMetadata.put(new LongEntityData(EntityUtils.getEntityField("DATA_FLAGS", Entity.DATA_FLAGS), flags));
    }

    private final Set<Player> hasSpawned = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Getter
    protected final Map<Player, String> showTextMap = new ConcurrentHashMap<>();

    @Getter
    private int maxCanSeeDistance = 16 * 40;

    public EntityRankingText() {
        this.id = Entity.entityCount++;
    }

    @Override
    public void setPosition(@NotNull Position position) {
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
        this.setLevel(position.getLevel());

        MoveEntityAbsolutePacket pk = new MoveEntityAbsolutePacket();
        pk.eid = this.getId();
        pk.x = this.getX();
        pk.y = this.getY();
        pk.z = this.getZ();
        pk.yaw = 0D;
        pk.headYaw = 0D;
        pk.pitch = 0D;
        for (Player player : this.hasSpawned) {
            player.dataPacket(pk.clone());
        }
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public void setShowText(@NotNull Player player, @NotNull String showText) {
        this.showTextMap.put(player, showText);
    }

    public void setMaxCanSeeDistance(int maxCanSeeDistance) {
        if (maxCanSeeDistance < 1) {
            maxCanSeeDistance = 1;
        }
        this.maxCanSeeDistance = maxCanSeeDistance;
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
            if (entry.getKey().getLevel() == this.getLevel() &&
                    this.distance(entry.getKey()) <= this.getMaxCanSeeDistance()) {
                if (!this.hasSpawned.contains(entry.getKey())) {
                    this.spawnTo(entry.getKey());
                }
                this.sendText(entry.getKey(), entry.getValue());
            }
        }
        for (Player player : this.hasSpawned) {
            if (!this.getShowTextMap().containsKey(player) ||
                    !player.isOnline() ||
                    player.getLevel() != this.getLevel() ||
                    this.distance(player) > this.getMaxCanSeeDistance()) {
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
        pk.x = (float) this.getX();
        pk.y = (float) this.getY();
        pk.z = (float) this.getZ();
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
                (new EntityMetadata()).putString(EntityUtils.getEntityField("DATA_NAMETAG", Entity.DATA_NAMETAG), string));
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
