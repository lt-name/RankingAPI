package cn.lanink.rankingapi;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lt_name
 */
@Setter
@Getter
@EqualsAndHashCode
public class RankingFormat implements Cloneable {

    private String top;
    private String line;
    private String lineSelf;
    private String bottom;

    private SortOrder sortOrder;
    //distance : showLine
    private final Map<Integer, Integer> showLine = new TreeMap<>(Comparator.comparingInt(o -> o));

    public RankingFormat(String top, String line, String lineSelf, String bottom, SortOrder sortOrder, Map<Integer, Integer> showLine) {
        this.top = top;
        this.line = line;
        this.lineSelf = lineSelf;
        this.bottom = bottom;
        this.sortOrder = sortOrder;
        this.showLine.putAll(showLine);
    }


    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    public static RankingFormat getDefaultFormat() {
        HashMap<Integer, Integer> showLine = new HashMap<>();
        showLine.put(3, 15); //玩家距离小于等于3时 显示15行
        showLine.put(5, 10);
        showLine.put(15, 5);
        showLine.put(20, 3);

        return new RankingFormat(
                "§b<<§a[§e%name%§a]§b>>",
                "§bTop[%ranking%] §a%player% §c- §b%score%",
                "§bTop[%ranking%] §e%player%(me) §c- §b%score%",
                "§b<<§a[§e%name%§a]§b>>",
                SortOrder.ASCENDING,
                showLine);
    }

    @Override
    public RankingFormat clone() {
        try {
            return (RankingFormat) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

}
