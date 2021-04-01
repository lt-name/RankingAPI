# RankingAPI
排行榜API，提供简单创建排行榜方法

示例：
```java
Ranking ranking=createRanking(this,"测试排行榜",pos);
HashMap<String, Number> map = new HashMap<>();
map.put("ltname",100);
map.put("ltname2",99);
map.put("ltname3",98);
map.put("ltname4",10);
ranking.setRankingList(map);
```
