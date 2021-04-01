package cn.lanink.rankingapi;

import cn.lanink.rankingapi.task.AsyncUpdateTask;
import cn.lanink.rankingapi.task.UpdateTask;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;

/**
 * @author lt_name
 */
public class RankingAPI extends PluginBase implements Listener {

    private static RankingAPI rankingAPI;

    public static boolean debug = true;

    @Getter
    private AsyncUpdateTask asyncUpdateTask;
    @Getter
    private UpdateTask updateTask;

    public static RankingAPI getInstance() {
        return rankingAPI;
    }

    @Override
    public void onLoad() {
        rankingAPI = this;
    }

    @Override
    public void onEnable() {
        this.asyncUpdateTask = new AsyncUpdateTask();
        this.getServer().getScheduler().scheduleAsyncTask(this, this.asyncUpdateTask);

        this.updateTask = new UpdateTask();
        this.getServer().getScheduler().scheduleRepeatingTask(this, this.updateTask, 1);
    }

    @Override
    public void onDisable() {

    }

    public static Ranking createRanking(Plugin plugin, String name, Position position) {
        return new Ranking(plugin, name, position);
    }

}
