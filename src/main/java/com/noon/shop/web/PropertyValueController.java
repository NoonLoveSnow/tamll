package com.noon.shop.web;

import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.PropertyValue;
import com.noon.shop.service.ProductService;
import com.noon.shop.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;
@GetMapping("products/{pid}/propertyValues")
    public List list(@PathVariable("pid")int pid){
   Product  product=productService.get(pid);
   propertyValueService.init(product);
   List <PropertyValue> values=propertyValueService.list(product);
   return  values;
}
@PutMapping("/propertyValues")
    public  Object update(@RequestBody PropertyValue propertyValue){
        propertyValueService.update(propertyValue);
        return propertyValue;
}
}
