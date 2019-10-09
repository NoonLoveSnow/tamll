package com.noon.shop.dao;


import com.noon.shop.pojo.Property;
import com.noon.shop.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import com.noon.shop.pojo.Product;
import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{
List findByProductOrderByIdDesc(Product product);
PropertyValue getByPropertyAndProduct(Property property,Product product);
}
