package cn.lanink.rankingapi.task;

import cn.lanink.rankingapi.Ranking;

/**
 * @author lt_name
 */
public interface IRankingAPITask {

    boolean addRanking(Ranking ranking);

    void removeRanking(Ranking ranking);

}
