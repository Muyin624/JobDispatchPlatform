package org.main.jobdispatchplatform.controller;

import org.main.jobdispatchplatform.entity.Channel;
import org.main.jobdispatchplatform.mapper.ChannelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channel")
public class ChannelController {
    private final ChannelMapper channelMapper;

    public ChannelController(ChannelMapper channelMapper) {
        this.channelMapper = channelMapper;
    }

    @GetMapping("/list")
    public List<Channel> list() {
        return channelMapper.findAll();
    }

    @GetMapping("/{id}")
    public Channel get(@PathVariable int id) {
        return channelMapper.findById(id);
    }

    @PostMapping
    public String insert(@RequestBody Channel channel) {
        channelMapper.insert(channel);
        return "新增成功";
    }

    @PutMapping
    public String update(@RequestBody Channel channel) {
        channelMapper.update(channel);
        return "修改成功";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        channelMapper.delete(id);
        return "删除成功";
    }
}
