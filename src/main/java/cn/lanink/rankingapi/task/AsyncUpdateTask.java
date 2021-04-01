package cn.lanink.rankingapi.task;

import cn.lanink.rankingapi.Ranking;
import cn.lanink.rankingapi.RankingAPI;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class AsyncUpdateTask extends AsyncTask implements IRankingAPITask {

    private int tick = 0;

    private final Set<Ranking> updateRankings = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public boolean addRanking(Ranking ranking) {
        return this.updateRankings.add(ranking);
    }

    @Override
    public void removeRanking(Ranking ranking) {
        this.updateRankings.remove(ranking);
    }

    @Override
    public void onRun() {
        long startTime;
        while(true) {
            startTime = System.currentTimeMillis();
            if (RankingAPI.getInstance().isDisabled()) {
                return;
            }

            try {
                this.work();
            } catch (Exception e) {
                e.printStackTrace();
            }

            long duration = System.currentTimeMillis() - startTime;
            if (duration < 50L) {
                try {
                    Thread.sleep(50L - duration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.tick++;
        }
    }

    private void work() {
        for (Ranking ranking : this.updateRankings) {
            ranking.onAsyncTick(this.tick);
        }
    }

}
