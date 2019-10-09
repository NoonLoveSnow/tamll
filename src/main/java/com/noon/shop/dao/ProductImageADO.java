package com.noon.shop.dao;

import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageADO extends JpaRepository<ProductImage,Integer> {
    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product,String type);
}
