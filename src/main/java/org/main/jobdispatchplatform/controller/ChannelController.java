package org.main.jobdispatchplatform.controller;

import jakarta.validation.Valid;
import org.main.jobdispatchplatform.common.Result;
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
    public Result<List<Channel>> list() {
        return Result.success(channelMapper.findAll());
    }

    @GetMapping("/{id}")
    public Result<Channel> get(@Valid @PathVariable int id) {
        return Result.success(channelMapper.findById(id));
    }

    @PostMapping
    public Result<String> insert(@Valid @RequestBody Channel channel) {
        channelMapper.insert(channel);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody Channel channel) {
        channelMapper.update(channel);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@Valid @PathVariable int id) {
        channelMapper.delete(id);
        return Result.success("删除成功");
    }
}
