package cn.lanink.rankingapi;

import cn.lanink.rankingapi.entity.EntityRankingText;
import cn.lanink.rankingapi.entity.IEntityRanking;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lt_name
 */
public class Ranking {

    private final RankingAPI rankingAPI = RankingAPI.getInstance();
    @Getter
    private final Plugin plugin;
    @Setter
    @Getter
    private String name;
    @Setter
    private RankingFormat rankingFormat = new RankingFormat();
    private LinkedHashMap<String, String> list = new LinkedHashMap<>();
    @Setter
    private Position position;
    private IEntityRanking entityRanking;
    @Getter
    private boolean closed = false;

    public Ranking(Plugin plugin, String name, Position position) {
        this.plugin = plugin;
        this.setName(name);
        this.setPosition(position);
        this.setRankingEntity(EntityRankingText.class);
        this.schedulerTask();
    }

    public void onTick(int i) {
        if (this.isClosed()) {
            return;
        }
        if (this.entityRanking.needTick()) {
            this.entityRanking.onTick(i);
        }
    }

    public void onAsyncTick(int i) {
        if (this.isClosed()) {
            return;
        }
        if (i%100 == 0) {
            this.updateShowText();
        }
        if (this.entityRanking.needAsyncTick()) {
            this.entityRanking.onAsyncTick(i);
        }
    }

    private void updateShowText() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            StringBuilder showText = new StringBuilder(this.rankingFormat.getTop()
                    .replace("%name%", this.getName())).append("\n");

            int line = 0;
            for (Map.Entry<String, String> entry : this.list.entrySet()) {
                line++;

                String lineText;
                if (line > this.rankingFormat.getShowLine()) {
                    break;
                }
                if (player.getName().equals(entry.getKey())) {
                    lineText = this.rankingFormat.getLineSelf()
                            .replace("%ranking%", line + "")
                            .replace("%player%", entry.getKey())
                            .replace("%score%", entry.getValue());
                }else {
                    lineText = this.rankingFormat.getLine()
                            .replace("%ranking%", line + "")
                            .replace("%player%", entry.getKey())
                            .replace("%score%", entry.getValue());
                }
                showText.append(lineText).append("\n");
            }

            showText.append(this.rankingFormat.getBottom().replace("%name%", this.getName()));
            this.entityRanking.getShowTextMap().put(player, showText.toString());
        }
    }

    public void setRankingEntity(Class<? extends IEntityRanking> newEntityRanking) {
        if (newEntityRanking == null) {
            try {
                this.entityRanking = newEntityRanking.getConstructor(Position.class).newInstance(this.position);
            } catch (Exception e) {
                RankingAPI.getInstance().getLogger().error("创建实体时出现错误：", e);
            }
        }
        if (this.entityRanking != null) {
            this.entityRanking.close();
        }
        try {
            this.entityRanking = newEntityRanking.getConstructor().newInstance();
            this.entityRanking.setPos(this.position);
            this.entityRanking.setLevel(this.position.getLevel());
        } catch (Exception e) {
            RankingAPI.getInstance().getLogger().error("创建实体时出现错误：", e);
        }
    }

    public void setRankingList(Map<String, Number> newList) {
        this.clearRankingList();
        List<Map.Entry<String, Number>> list = new ArrayList<>(newList.entrySet());
        if (this.rankingFormat.getSortOrder() == RankingFormat.SortOrder.ASCENDING) {
            list.sort((o1, o2) -> (int) Math.round(o2.getValue().doubleValue() - o1.getValue().doubleValue()));
        }else {
            list.sort((o1, o2) -> (int) Math.round(o1.getValue().doubleValue() - o2.getValue().doubleValue()));
        }
        for(Map.Entry<String, Number> entry : list) {
            this.list.put(entry.getKey(), entry.getValue().toString());
        }
        this.updateShowText();
    }

    private void schedulerTask() {
        this.rankingAPI.getUpdateTask().addRanking(this);
        this.rankingAPI.getAsyncUpdateTask().addRanking(this);
    }

    public void clearRankingList() {
       this.list.clear();
       if (this.entityRanking != null) {
           this.entityRanking.getShowTextMap().clear();
       }
    }

    public void close() {
        this.closed = true;
        this.clearRankingList();
        if (this.entityRanking != null) {
            this.entityRanking.close();
        }
    }

}
