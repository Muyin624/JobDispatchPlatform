package org.main.jobdispatchplatform.controller;

import jakarta.validation.Valid;
import org.main.jobdispatchplatform.common.Result;
import org.main.jobdispatchplatform.entity.Photographer;
import org.main.jobdispatchplatform.mapper.PhotographerMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photographer")
public class PhotographerController {
    private final PhotographerMapper photographerMapper;

    public PhotographerController(PhotographerMapper photographerMapper) {
        this.photographerMapper = photographerMapper;
    }

    @GetMapping("/list")
    public Result<List<Photographer>> findAll() {
        return Result.success(photographerMapper.findAll());
    }

    @GetMapping("/{id}")
    public Result<Photographer> findById(@PathVariable int id) {
        return Result.success(photographerMapper.findById(id));
    }

    @PostMapping
    public Result<String> insert(@Valid @RequestBody Photographer photographer){
        photographerMapper.insert(photographer);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@Valid @RequestBody Photographer photographer) {
        photographerMapper.update(photographer);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@Valid @PathVariable int id) {
        photographerMapper.delete(id);
        return Result.success("删除成功");
    }

}
