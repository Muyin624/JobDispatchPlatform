package org.main.jobdispatchplatform.service;

import org.main.jobdispatchplatform.entity.BookingOrder;
import org.main.jobdispatchplatform.entity.Photographer;
import org.main.jobdispatchplatform.entity.Spot;
import org.main.jobdispatchplatform.exception.OrderAssignException;
import org.main.jobdispatchplatform.mapper.BookingOrderMapper;
import org.main.jobdispatchplatform.mapper.PhotographerSpotMapper;
import org.main.jobdispatchplatform.mapper.SpotMapper;
import org.main.jobdispatchplatform.util.DistanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摄影师智能分配服务
 * 老开发经验：复杂的业务逻辑单独成Service，保持代码清晰
 */
@Service
public class PhotographerAssignService {

    private static final Logger log = LoggerFactory.getLogger(PhotographerAssignService.class);

    private final PhotographerSpotMapper photographerSpotMapper;
    private final BookingOrderMapper bookingOrderMapper;
    private final SpotMapper spotMapper;

    public PhotographerAssignService(PhotographerSpotMapper photographerSpotMapper,
                                     BookingOrderMapper bookingOrderMapper,
                                     SpotMapper spotMapper) {
        this.photographerSpotMapper = photographerSpotMapper;
        this.bookingOrderMapper = bookingOrderMapper;
        this.spotMapper = spotMapper;
    }

    /**
     * 智能推荐最优摄影师
     *
     * @param spotId 拍摄点位ID
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return 推荐的摄影师（如果没有合适的返回null）
     */
    public Photographer findBestPhotographer(int spotId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("开始智能分配：点位ID={}, 时间段={} ~ {}", spotId, startTime, endTime);

        // ============ 第一步：查询负责该点位的所有摄影师 ============
        List<Photographer> photographers = photographerSpotMapper.findPhotographers(spotId);

        if (photographers.isEmpty()) {
            log.warn("点位 {} 没有负责的摄影师", spotId);
            return null;
        }

        log.info("该点位共有 {} 名摄影师负责", photographers.size());

        // ============ 第二步：过滤掉时间冲突的摄影师 ============
        List<Photographer> availablePhotographers = photographers.stream()
            .filter(p -> !hasTimeConflict(p.getId(), startTime, endTime))
            .collect(Collectors.toList());

        if (availablePhotographers.isEmpty()) {
            log.warn("所有摄影师在该时间段都有订单，无法分配");
            return null;
        }

        log.info("过滤后有 {} 名摄影师可用", availablePhotographers.size());

        // ============ 第三步：获取点位坐标 ============
        Spot spot = spotMapper.findById(spotId);
        if (spot == null || spot.getLongitude() == null || spot.getLatitude() == null) {
            log.warn("点位 {} 坐标信息不完整", spotId);
            // 如果没有坐标，只能按订单数量排序
            return findByWorkload(availablePhotographers, startTime, endTime);
        }

        // ============ 第四步：计算距离并排序 ============
        return availablePhotographers.stream()
            // 计算每个摄影师的距离
            .peek(p -> {
                double distance = calculatePhotographerDistance(p, spot);
                // TODO: 这里可以把distance存到一个临时字段，避免重复计算
                log.debug("摄影师 {} 距离点位 {} 米", p.getName(), distance);
            })
            // 排序：先按距离，再按负载
            .sorted(Comparator
                .comparingDouble((Photographer p) -> calculatePhotographerDistance(p, spot))  // 距离优先
                .thenComparingInt(p -> getTodayOrderCount(p.getId(), startTime))              // 订单数次要
            )
            .findFirst()
            .orElse(null);
    }

    /**
     * 检查摄影师是否有时间冲突
     */
    private boolean hasTimeConflict(int photographerId, LocalDateTime startTime, LocalDateTime endTime) {
        List<BookingOrder> conflicts = bookingOrderMapper.findConflictOrders(
            photographerId, startTime, endTime
        );
        return !conflicts.isEmpty();
    }

    /**
     * 计算摄影师到点位的距离
     * 老开发经验：距离计算考虑摄影师当前状态
     */
    private double calculatePhotographerDistance(Photographer photographer, Spot spot) {
        Double lng, lat;

        // 如果摄影师在工作中，用工作地点；否则用家庭地址
        if (photographer.getStatus() != null && photographer.getStatus() == 2  // 2=工作中
            && photographer.getWorkLongitude() != null
            && photographer.getWorkLatitude() != null) {

            lng = photographer.getWorkLongitude();
            lat = photographer.getWorkLatitude();
            log.debug("摄影师 {} 当前在工作中，使用工作地点坐标", photographer.getName());
        } else {
            lng = photographer.getHomeLongitude();
            lat = photographer.getHomeLatitude();
            log.debug("摄影师 {} 未工作，使用家庭地址坐标", photographer.getName());
        }

        return DistanceUtil.calculateDistance(lng, lat, spot.getLongitude(), spot.getLatitude());
    }

    /**
     * 获取摄影师今天已接订单数
     */
    private int getTodayOrderCount(int photographerId, LocalDateTime currentTime) {
        // TODO: 需要在 BookingOrderMapper 添加查询今日订单数的方法
        long count = bookingOrderMapper.todayOrderCount(photographerId, currentTime);
        return (int) count;
    }

    /**
     * 按负载选择摄影师（没有坐标信息时的降级方案）
     */
    private Photographer findByWorkload(List<Photographer> photographers,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime) {
        log.info("使用负载均衡策略选择摄影师");
        return photographers.stream()
            .min(Comparator.comparingInt(p -> getTodayOrderCount(p.getId(), startTime)))
            .orElse(null);
    }
}
