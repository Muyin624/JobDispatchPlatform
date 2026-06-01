package org.main.jobdispatchplatform.controller;

import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Param;
import org.main.jobdispatchplatform.common.AssignRequest;
import org.main.jobdispatchplatform.common.PageRequest;
import org.main.jobdispatchplatform.common.PageResult;
import org.main.jobdispatchplatform.common.Result;
import org.main.jobdispatchplatform.entity.BookingOrder;
import org.main.jobdispatchplatform.mapper.BookingOrderMapper;
import org.main.jobdispatchplatform.service.BookingOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookingOrder")
public class BookingOrderController {
    private final BookingOrderMapper bookingOrderMapper;
    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderMapper bookingOrderMapper, BookingOrderService bookingOrderService) {
        this.bookingOrderMapper = bookingOrderMapper;
        this.bookingOrderService = bookingOrderService;
    }

    @GetMapping("/list")
    public Result<List<BookingOrder>> findAll() {
        return Result.success(bookingOrderMapper.findAll()) ;
    }

    @GetMapping("/{id}")
    public Result<BookingOrder> findById(@PathVariable int id) {
        return Result.success(bookingOrderMapper.findById(id));
    }

    @PostMapping
    public Result<String> insert(@Valid @RequestBody BookingOrder bookingOrder) {
        bookingOrderMapper.insert(bookingOrder);
        return Result.success ("BookingOrder added successfully");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody BookingOrder bookingOrder) {
        bookingOrderMapper.update(bookingOrder);
        return Result.success ("BookingOrder updated successfully");
    }

    @PostMapping("/place")
    public Result<String> placeOrder(@Valid @RequestBody BookingOrder bookingOrder) {
        bookingOrderService.placeOrder(bookingOrder);
        return Result.success ("下单成功");
    }

    @GetMapping("/page")
    public Result<PageResult<BookingOrder>> findByPage(PageRequest pageRequest) {
        List<BookingOrder> list = bookingOrderMapper.findByPage(pageRequest.getPageSize(), pageRequest.getOffset());
        long total = bookingOrderMapper.count();
        return Result.success(PageResult.of(list,total, pageRequest.getPageNum(),pageRequest.getPageSize()));
    }

    @PostMapping("/assign")
    public Result<String> assign(@Valid @RequestBody AssignRequest assignRequest) {
        bookingOrderService.assignOrder(assignRequest.getOrderId(),assignRequest.getPhotographerId());
        return Result.success("订单分配成功");
    }
}
