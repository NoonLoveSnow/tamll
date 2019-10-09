package com.noon.shop.service;

import com.noon.shop.dao.PropertyDAO;
import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.Property;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;
    public void add(Property property){
            propertyDAO.save(property);
    }

    public void delete(int id){
        propertyDAO.deleteById(id);
    }
    public void update(Property property){
        propertyDAO.save(property) ;
    }
    public  Property get(int id){

        return  propertyDAO.getOne(id);
    }
public Page4Navigator list(int cid,int start,int size,int navNum){
    Category category=categoryService.get(cid);
    Pageable pageable= PageRequest.of(start,size, Sort.Direction.DESC,"id");
    Page<Property>page=propertyDAO.findByCategory(category,pageable);
    return new Page4Navigator(page,navNum);

}

public List listBycategory(Category category){
    return propertyDAO.findByCategory(category);
}
}
