package org.main.jobdispatchplatform.util;

//距离计算工具类
public class DistanceUtil {
//    地球半径（公里）
    private static final double EARTH_RADIUS = 6371;

    /**
     * 计算两个坐标点之间的距离（单位：米）
     * 使用 Haversine 公式
     *
     * @param lng1 点1经度
     * @param lat1 点1纬度
     * @param lng2 点2经度
     * @param lat2 点2纬度
     * @return 距离（米）
     */

    public static double calculateDistance(Double lng1, Double lat1, Double lng2, Double lat2){
//        参数校验
        if(lng1 == null || lng2 == null || lat1 == null || lat2 == null) {
            return Double.MAX_VALUE;//返回最大值，表示无法计算
        }

        double lat1Ra = Math.toRadians(lat1);
        double lat2Ra = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Ra) * Math.cos(lat2Ra) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //距离（公里）转换成米
        return EARTH_RADIUS * c * 1000;
    }

    /**
     * 格式化距离显示
     *
     * @param meters 距离（米）
     * @return 格式化字符串，如 "1.5公里" 或 "500米"
     */

    public static String formatDistance(double meters){
        if(meters >= 1000){
            return String.format("%.1f公里", meters/1000);
        }else {
            return String.format("%.0f米", meters);
        }
    }
}
