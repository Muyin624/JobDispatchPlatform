package org.main.jobdispatchplatform.controller;

import jakarta.validation.Valid;
import org.main.jobdispatchplatform.common.Result;
import org.main.jobdispatchplatform.entity.Spot;
import org.main.jobdispatchplatform.mapper.SpotMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spot")
public class SpotController {
    private final SpotMapper spotMapper;
    public SpotController(SpotMapper spotMapper) {
        this.spotMapper = spotMapper;
    }

    @GetMapping("/list")
    public Result<List<Spot>> findAll() {
        return Result.success(spotMapper.findAll());
    }

    @GetMapping("/{id}")
    public Result<Spot> findById(@PathVariable int id) {
        return Result.success(spotMapper.findById(id));
    }

    @PostMapping
    public Result<String> insert(@Valid @RequestBody Spot spot) {
        spotMapper.insert(spot);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody Spot spot) {
        spotMapper.update(spot);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable int id) {
        spotMapper.delete(id);
        return Result.success("删除成功");
    }
}
