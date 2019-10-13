package com.noon.shop.service;

import com.noon.shop.dao.ProductImageADO;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.ProductImage;
import com.noon.shop.util.JsonUtils;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductImageService {
    public static final String type_single = "single";
    public static final String type_detail = "detail";
    @Autowired
    ProductImageADO productImageADO;
    @Autowired
    ProductService productService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void  add(ProductImage productImage){
        Set<String>keys=stringRedisTemplate.keys("productImage:*");
        stringRedisTemplate.delete(keys);
        productImageADO.save(productImage);
    }

    public void delete(int id){
        Set<String>keys=stringRedisTemplate.keys("productImage:*");
        stringRedisTemplate.delete(keys);
        productImageADO.deleteById(id);
    }
    public  ProductImage get(int id){
        ProductImage productImage;
        String imgString=stringRedisTemplate.opsForValue().get("productImage:id"+id);
        if (imgString==null){
            productImage=productImageADO.getOne(id);
            imgString= JsonUtils.obj2String(productImage);
            stringRedisTemplate.opsForValue().set("productImage:id"+id,imgString);
        }else{
            productImage=JsonUtils.string2Obj(imgString,ProductImage.class);
        }

        return productImage;
    }
    public List listSingleProductImage(Product product){
        return this.listByProductAndType(product,type_single);
    }
    public List listDetailProductImage(Product product){
       return this.listByProductAndType(product,type_detail);
    }
    public List<ProductImage> listByProductAndType(Product product,String type){
        List<ProductImage> imgs;
        String imgsStr=stringRedisTemplate.opsForValue().get("productImage:pid"+product.getId()+"-"+type);
        if (imgsStr==null){
            imgs=productImageADO.findByProductAndTypeOrderByIdDesc(product,type);
            imgsStr=JsonUtils.obj2String(imgs);
            stringRedisTemplate.opsForValue().set("productImage:pid"+product.getId()+"-"+type,imgsStr);
        }else{
            imgs=JsonUtils.string2List(imgsStr,ProductImage.class);
        }
        return imgs;
    }
    public void setFirstProductImage(Product product){//用于订单项产品图片
        List<ProductImage> singleImages=this.listByProductAndType(product,type_single);
        if (!singleImages.isEmpty()){
            product.setFirstProductImage(singleImages.get(0));
        }else {
            product.setFirstProductImage(new ProductImage());
        }
    }
    public void setFirstProductImage(List<Product> products){
        for (Product p:products) {
            setFirstProductImage( p);
        }
    }
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProductImage(orderItem.getProduct());

        }
    }
}
