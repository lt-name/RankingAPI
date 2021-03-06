package cn.lanink.rankingapi.task;

import cn.lanink.rankingapi.Ranking;
import cn.lanink.rankingapi.RankingAPI;
import cn.nukkit.scheduler.AsyncTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class AsyncUpdateTask extends AsyncTask implements IRankingAPITask {

    private int tick = 0;

    private final Set<Ranking> updateRankings = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    @Override
    public Set<Ranking> getRankings() {
        return this.updateRankings;
    }
    
    @Override
    public boolean addRanking(@NotNull Ranking ranking) {
        return this.updateRankings.add(ranking);
    }

    @Override
    public void removeRanking(@NotNull Ranking ranking) {
        this.updateRankings.remove(ranking);
    }

    @Override
    public void onRun() {
        long startTime;
        while(RankingAPI.getInstance().isEnabled()) {
            startTime = System.currentTimeMillis();

            try {
                this.work(this.tick);
            } catch (Exception e) {
                RankingAPI.getInstance().getLogger().error("AsyncUpdateTask遍历Ranking时出错：", e);
            }

            long duration = System.currentTimeMillis() - startTime;
            try {
                Thread.sleep(Math.max(50L - duration, 1));
            } catch (Exception e) {
                RankingAPI.getInstance().getLogger().error("AsyncUpdateTask尝试休眠时出错：", e);
            }

            this.tick++;
        }
    
        for (Ranking ranking : new HashSet<>(this.updateRankings)) {
            ranking.close();
        }
    }

    private void work(int tick) {
        for (Ranking ranking : this.updateRankings) {
            ranking.onAsyncTick(tick);
        }
    }

}
