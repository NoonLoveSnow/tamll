package com.noon.shop.service;

import com.noon.shop.dao.ProductImageADO;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.ProductImage;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {
    public static final String type_single = "single";
    public static final String type_detail = "detail";
    @Autowired
    ProductImageADO productImageADO;
    @Autowired
    ProductService productService;

    public void  add(ProductImage productImage){
        productImageADO.save(productImage);
    }

    public void delete(int id){
        productImageADO.deleteById(id);
    }
    public  ProductImage get(int id){
        return productImageADO.getOne(id);
    }
    public List listSingleProductImage(Product product){
        return productImageADO.findByProductAndTypeOrderByIdDesc(product,type_single);
    }
    public List listDetailProductImage(Product product){
        return productImageADO.findByProductAndTypeOrderByIdDesc(product,type_detail);
    }
    public void setFirstProductImage(Product product){//用于订单项产品图片
        List<ProductImage> singleImages=productImageADO.findByProductAndTypeOrderByIdDesc(product,type_single);
        if (!singleImages.isEmpty()){
            product.setFirstProductImage(singleImages.get(0));
        }else {
            product.setFirstProductImage(new ProductImage());
        }
    }
    public void setFirstProductImage(List<Product> products){
        for (Product p:products
             ) {
            setFirstProductImage(p);
        }
    }
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProductImage(orderItem.getProduct());

        }
    }
}
