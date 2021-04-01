package cn.lanink.rankingapi.task;

import cn.lanink.rankingapi.Ranking;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public interface IRankingAPITask {

    boolean addRanking(@NotNull Ranking ranking);

    void removeRanking(@NotNull Ranking ranking);

}
