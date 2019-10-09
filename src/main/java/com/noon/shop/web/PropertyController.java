package com.noon.shop.web;

import com.noon.shop.pojo.Property;
import com.noon.shop.service.PropertyService;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator list(@PathVariable("cid")int cid, @RequestParam(defaultValue = "0")int start,@RequestParam(defaultValue = "5")int size){
       start= start<0?0:start;
        return propertyService.list(cid,start,size,7);
    }
    @GetMapping("/properties/{id}")
    public Object get(@PathVariable("id")int id){
        return  propertyService.get(id);
    }
    @PostMapping("/properties")
    public Object add(@RequestBody Property property){
        propertyService.add(property);
        return property;
    }
    @PutMapping("/properties")
    public Object update(@RequestBody Property property){
        propertyService.update(property);
        return property;
    }
    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id){
        propertyService.delete(id);
        return null;
    }
}
