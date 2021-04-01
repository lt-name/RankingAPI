package cn.lanink.rankingapi.entity;

import cn.nukkit.Player;
import cn.nukkit.level.Position;

import java.util.Map;

/**
 * @author lt_name
 */
public interface IEntityRanking {

    Map<Player, String> getShowTextMap();

    void setPosition(Position position);

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
