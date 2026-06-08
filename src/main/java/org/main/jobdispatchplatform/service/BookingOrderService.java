package org.main.jobdispatchplatform.service;

import org.main.jobdispatchplatform.entity.BookingOrder;
import org.main.jobdispatchplatform.entity.Channel;
import org.main.jobdispatchplatform.entity.OrderStatus;
import org.main.jobdispatchplatform.entity.Photographer;
import org.main.jobdispatchplatform.entity.Spot;
import org.main.jobdispatchplatform.exception.OrderAssignException;
import org.main.jobdispatchplatform.exception.ResourceNotFoundException;
import org.main.jobdispatchplatform.mapper.BookingOrderMapper;
import org.main.jobdispatchplatform.mapper.ChannelMapper;
import org.main.jobdispatchplatform.mapper.PhotographerMapper;
import org.main.jobdispatchplatform.mapper.PhotographerSpotMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 订单服务层
 * 老开发经验：
 * 1. Service层负责业务逻辑，不要写在Controller
 * 2. 使用构造器注入替代@Autowired字段注入
 * 3. 重要操作必须加@Transactional
 * 4. 记录关键操作日志
 */
@Service
public class BookingOrderService {

    private static final Logger log = LoggerFactory.getLogger(BookingOrderService.class);

    // 构造器注入（推荐方式）
    private final BookingOrderMapper bookingOrderMapper;
    private final ChannelMapper channelMapper;
    private final PhotographerMapper photographerMapper;
    private final PhotographerSpotMapper photographerSpotMapper;
    private final PhotographerAssignService photographerAssignService;

    private final ConcurrentHashMap<Integer, ReentrantLock> assignLocks = new ConcurrentHashMap<>();

    public BookingOrderService(BookingOrderMapper bookingOrderMapper,
                               ChannelMapper channelMapper,
                               PhotographerMapper photographerMapper,
                               PhotographerSpotMapper photographerSpotMapper,
                               PhotographerAssignService photographerAssignService) {
        this.bookingOrderMapper = bookingOrderMapper;
        this.channelMapper = channelMapper;
        this.photographerMapper = photographerMapper;
        this.photographerSpotMapper = photographerSpotMapper;
        this.photographerAssignService = photographerAssignService;
    }

    /**
     * 下单（带智能分配）
     */
    @Transactional(rollbackFor = Exception.class)
    public BookingOrder placeOrder(BookingOrder bookingOrder) {
        log.info("用户 {} 开始下单", bookingOrder.getUserId());

        // 检验渠道是否存在
        Channel channel = channelMapper.findById(bookingOrder.getChannelId());
        if (channel == null) {
            throw new ResourceNotFoundException("渠道不存在");
        }

        // ============ 智能分配摄影师 ============
        Photographer bestPhotographer = photographerAssignService.findBestPhotographer(
            bookingOrder.getSpotId(),
            bookingOrder.getStartTime(),
            bookingOrder.getEndTime()
        );

        if (bestPhotographer == null) {
            throw new OrderAssignException("该时间段暂无可用摄影师，请更换时间或地点");
        }

        // ===== 加锁 =====
        ReentrantLock lock = getLock(bestPhotographer.getId());
        lock.lock();
        try {
            // 锁内再查一次时间冲突（防止推荐完之后、加锁之前被别人占了）
            List<BookingOrder> conflicts = bookingOrderMapper.findConflictOrders(
                    bestPhotographer.getId(), bookingOrder.getStartTime(), bookingOrder.getEndTime());
            if (!conflicts.isEmpty()) {
                throw new OrderAssignException("该摄影师刚刚被分配，请重新下单");
            }

            bookingOrder.setPhotographerId(bestPhotographer.getId());
            bookingOrder.setStatus(OrderStatus.ASSIGNED.getCode());
            bookingOrder.setUpdateTime(LocalDateTime.now());
            bookingOrderMapper.insert(bookingOrder);

            log.info("订单创建成功，订单ID: {}，摄影师: {}", bookingOrder.getId(), bestPhotographer.getName());
            return bookingOrder;
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getLock(int photographerId) {
        return assignLocks.computeIfAbsent(photographerId, id -> new ReentrantLock());
    }
    /**
     * 分配订单给摄影师
     *
     * @param orderId 订单ID
     * @param photographerId 摄影师ID
     * @throws ResourceNotFoundException 订单或摄影师不存在
     * @throws OrderAssignException 订单状态不对或摄影师不符合条件
     */
    @Transactional(rollbackFor = Exception.class)  // 重点：加事务保证数据一致性
    public void assignOrder(int orderId, int photographerId) {
        log.info("开始分配订单：订单ID={}, 摄影师ID={}", orderId, photographerId);

        // ============ 第一步：基础校验 ============

        // 1. 订单是否存在
        BookingOrder order = bookingOrderMapper.findById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("订单不存在，订单ID: " + orderId);
        }

        // 如果传入的 photographerId = 0，表示需要智能推荐
        if (photographerId == 0) {
            Photographer best = photographerAssignService.findBestPhotographer(
                    order.getSpotId(),
                    order.getStartTime(),
                    order.getEndTime()
            );

            if (best == null) {
                throw new OrderAssignException("没有合适的摄影师，请稍后再试");
            }

            photographerId = best.getId();
            log.info("智能推荐摄影师：{}", best.getName());
        }

        ReentrantLock lock = getLock(photographerId);
        lock.lock();
        log.info("拿到锁：摄影师ID={}, 当前排队线程数={}", photographerId, lock.getQueueLength());
        try{
            // 2. 摄影师是否存在
            Photographer photographer = photographerMapper.findById(photographerId);
            if (photographer == null) {
                throw new ResourceNotFoundException("摄影师不存在，摄影师ID: " + photographerId);
            }

            // ============ 第二步：业务规则校验 ============

            // 3. 订单状态校验：只有"待分配"状态的订单才能分配
            if (order.getStatus() != OrderStatus.CREATED.getCode()) {
                String currentStatus = OrderStatus.fromCode(order.getStatus()).getDesc();
                throw new OrderAssignException(
                        String.format("订单当前状态为【%s】，无法分配。只有【待分配】状态的订单才能分配", currentStatus)
                );
            }

            // 4. 点位校验：摄影师是否负责该点位（这是你的业务特色）
            List<Spot> photographerSpots = photographerSpotMapper.findSpots(photographerId);
            boolean isResponsible = photographerSpots.stream()
                    .anyMatch(spot -> spot.getId() == order.getSpotId());

            if (!isResponsible) {
                log.warn("摄影师 {} 不负责点位 {}", photographerId, order.getSpotId());
                throw new OrderAssignException(
                        String.format("摄影师【%s】不负责该拍摄点位", photographer.getName())
                );
            }

            // TODO: 5. 时间冲突校验（重要！下一步实现）
            // 查询摄影师在同一时间段是否有其他订单
            List<BookingOrder> conflicts = bookingOrderMapper.findConflictOrders(photographerId,order.getStartTime(),order.getEndTime());
            if (!conflicts.isEmpty()) {
                throw new OrderAssignException("摄影师在该时间段已有订单，无法分配");
            }

            // ============ 第三步：执行分配 ============

            order.setStatus(OrderStatus.ASSIGNED.getCode());
            order.setPhotographerId(photographerId);
            order.setUpdateTime(LocalDateTime.now());
            bookingOrderMapper.assign(order);

            log.info("订单分配成功：订单ID={}, 分配给摄影师={}", orderId, photographer.getName());

            // TODO: 6. 发送通知（后续扩展）
            // - 通知摄影师
            // - 通知用户

        }finally {
            lock.unlock();
        }


    }


    @Transactional
    public void completeOrder(int orderId) {
        BookingOrder order = bookingOrderMapper.findById(orderId);

        // 更新订单状态为已完成
        order.setStatus(OrderStatus.COMPLETED.getCode());
        bookingOrderMapper.update(order);

        // 清空摄影师的工作地点
        photographerMapper.clearWorkLocation(order.getPhotographerId());
    }
}
