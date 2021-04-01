package cn.lanink.rankingapi.entity;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.Map;

/**
 * @author lt_name
 */
public interface IEntityRanking {

    Map<Player, String> getShowTextMap();

    void setPos(Vector3 vector3);

    void setLevel(Level level);

    default boolean needTick() {
        return true;
    }

    default boolean needAsyncTick() {
        return true;
    }

    void onTick(int i);

    void onAsyncTick(int i);

    void spawnTo(Player player);

    void despawnFrom(Player player);

    void close();

}
