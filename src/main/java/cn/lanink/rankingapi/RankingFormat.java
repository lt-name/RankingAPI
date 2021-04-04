package cn.lanink.rankingapi;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lt_name
 */
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class RankingFormat implements Cloneable {

    private String top;
    private String line;
    private String lineSelf;
    private String bottom;

    private SortOrder sortOrder;
    private int showLine;

    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    public static RankingFormat getDefaultFormat() {
        return new RankingFormat(
                "§b<<§a[§e%name%§a]§b>>",
                "§bTop[%ranking%] §a%player% §c- §b%score%",
                "§bTop[%ranking%] §e%player%(me) §c- §b%score%",
                "§b<<§a[§e%name%§a]§b>>",
                SortOrder.ASCENDING,
                10);
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
