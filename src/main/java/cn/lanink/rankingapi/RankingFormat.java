package cn.lanink.rankingapi;

import lombok.*;

/**
 * @author lt_name
 */
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RankingFormat {

    private String Top = "§b<<§a[§e%name%§a]§b>>";
    private String Line = "§bTop[%ranking%] §a%player% §c- §b%score%";
    private String LineSelf = "§bTop[%ranking%] §e%player%(me) §c- §b%score%";
    private String Bottom = "§b<<§a[§e%name%§a]§b>>";

    private SortOrder sortOrder = SortOrder.ASCENDING;
    private int showLine = 10;

    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

}
