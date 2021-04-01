package cn.lanink.rankingapi.task;

import cn.lanink.rankingapi.Ranking;
import cn.lanink.rankingapi.RankingAPI;
import cn.nukkit.scheduler.PluginTask;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class UpdateTask extends PluginTask<RankingAPI> implements IRankingAPITask {

    private final Set<Ranking> updateRankings = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public UpdateTask() {
        super(RankingAPI.getInstance());
    }

    @Override
    public void onRun(int i) {
        for (Ranking ranking : this.updateRankings) {
            ranking.onTick(i);
        }
    }

    @Override
    public boolean addRanking(Ranking ranking) {
        return this.updateRankings.add(ranking);
    }

    @Override
    public void removeRanking(Ranking ranking) {
        this.updateRankings.remove(ranking);
    }

}
