package org.main.jobdispatchplatform.controller;

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
    public List<Photographer> findAll() {
        return photographerMapper.findAll();
    }

    @GetMapping("/{id}")
    public Photographer findById(@PathVariable int id) {
        return photographerMapper.findById(id);
    }

    @PostMapping
    public String insert(@RequestBody Photographer photographer){
        photographerMapper.insert(photographer);
        return "添加成功";
    }

    @PutMapping
    public String update(@RequestBody Photographer photographer) {
        photographerMapper.update(photographer);
        return "修改成功";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        photographerMapper.delete(id);
        return "删除成功";
    }

}
