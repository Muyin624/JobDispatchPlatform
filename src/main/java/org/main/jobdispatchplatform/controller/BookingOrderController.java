package org.main.jobdispatchplatform.controller;

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
    public List<BookingOrder> findAll() {
        return bookingOrderMapper.findAll();
    }

    @GetMapping("/{id}")
    public BookingOrder findById(@PathVariable int id) {
        return bookingOrderMapper.findById(id);
    }

    @PostMapping
    public String insert(@RequestBody BookingOrder bookingOrder) {
        bookingOrderMapper.insert(bookingOrder);
        return "BookingOrder added successfully";
    }

    @PutMapping
    public String update(@RequestBody BookingOrder bookingOrder) {
        bookingOrderMapper.update(bookingOrder);
        return "BookingOrder updated successfully";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestBody BookingOrder bookingOrder) {
        bookingOrderService.placeOrder(bookingOrder);
        return "下单成功";
    }
}
