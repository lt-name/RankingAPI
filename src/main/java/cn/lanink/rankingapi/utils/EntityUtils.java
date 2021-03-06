package cn.lanink.rankingapi.utils;

import cn.lanink.rankingapi.RankingAPI;
import cn.nukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class EntityUtils {

    private static final ConcurrentHashMap<String, Number> fieldCache = new ConcurrentHashMap<>();

    private EntityUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T getEntityField(String name, T defaultValue) {
        if (!fieldCache.containsKey(name)) {
            try {
                Field field = Entity.class.getDeclaredField(name);
                field.setAccessible(true);
                fieldCache.put(name, (T) field.get(null));
            } catch (Exception e) {
                RankingAPI.getInstance().getLogger().error("反射获取数据时出现错误！", e);
            }
        }
        return (T) fieldCache.getOrDefault(name, defaultValue);
    }

}
