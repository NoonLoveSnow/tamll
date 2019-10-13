package com.noon.shop.web;

import com.noon.shop.pojo.Product;
import com.noon.shop.service.ProductImageService;
import com.noon.shop.service.ProductService;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductContrller {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    @GetMapping("/products/{id}")
    public Object get(@PathVariable("id") int id){
        return productService.get(id);
    }
    @GetMapping("/categories/{cid}/products")
    public Page4Navigator list(@PathVariable("cid")int cid, @RequestParam(defaultValue = "0") int start,@RequestParam(defaultValue = "5") int size) throws Exception{
        start = start<0?0:start;


       Page4Navigator page4Navigator=productService.list(cid, start, size, 7);


        return page4Navigator;
    }
    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id")int id) throws Exception{
        productService.delete(id);
        return null;
    }
    @PutMapping("/products")
    public Object update(@RequestBody Product product) throws Exception{
        productService.update(product);
        return  product;
    }
    @PostMapping("/products")
    public Object add(@RequestBody Product product) throws Exception{
        productService.add(product);

        return product;
    }




}
