package cn.lanink.rankingapi;

import cn.lanink.rankingapi.task.AsyncUpdateTask;
import cn.lanink.rankingapi.task.UpdateTask;
import cn.lanink.rankingapi.utils.MetricsLite;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public class RankingAPI extends PluginBase {

    public static final String VERSION = "?";

    private static RankingAPI rankingAPI;

    public static boolean debug = false;

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

        try {
            new MetricsLite(this, 11362);
        } catch (Exception ignored) {
        
        }
    
        this.getLogger().info("§eRankingAPI §aEnabled！ Version：" + VERSION);
        this.getServer().getScheduler().scheduleTask(this,
                () -> this.getLogger().warning("§e RankingAPI §a是一款§e免费§a插件，开源链接:§e https://github.com/lt-name/RankingAPI")
        );
    }

    @Override
    public void onDisable() {
        for (Ranking ranking : this.asyncUpdateTask.getRankings()) {
            ranking.close();
        }
        for (Ranking ranking : this.updateTask.getRankings()) {
            ranking.close();
        }
    }

    /**
     * 创建一个新的排行榜
     *
     * @param plugin 插件主类
     * @param name 排行榜名称
     * @param position 排行榜位置
     * @return 排行榜实例
     */
    public static Ranking createRanking(@NotNull Plugin plugin, @NotNull String name, @NotNull Position position) {
        return new Ranking(plugin, name, position);
    }

}
