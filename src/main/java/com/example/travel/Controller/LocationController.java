package com.example.travel.Controller;

import com.example.travel.entity.LocationItem;
import com.example.travel.mapper.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*") // 允许前端跨域请求
public class LocationController {

    @Autowired
    private LocationMapper locationMapper;

    // 1. 查询所有地点
    @GetMapping
    public List<LocationItem> getAll(@RequestParam(required = false) String city) {
        if (city != null && !city.isEmpty() && !city.equals("全部")) {
            return locationMapper.selectByCity(city);
        }
        return locationMapper.selectAll();
    }

    // 2. 新增地点
    @PostMapping
    public String add(@RequestBody LocationItem item) {
        // 🌟 打印看看前端传过来的对象到底长啥样
        System.out.println("收到前端传来的对象: " + item);

        if (item.getCity() == null || item.getCity().trim().isEmpty()) {
            item.setCity("其他城市");
        }

        int rows = locationMapper.insert(item);
        return rows > 0 ? "添加成功" : "添加失败";
    }

    // 3. 修改地点
    @PutMapping
    public String update(@RequestBody LocationItem item) {
        int rows = locationMapper.update(item);
        return rows > 0 ? "修改成功" : "修改失败";
    }

    // 4. 删除地点
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        int rows = locationMapper.delete(id);
        return rows > 0 ? "删除成功" : "删除失败";
    }
}