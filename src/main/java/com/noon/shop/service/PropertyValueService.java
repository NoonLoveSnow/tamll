package com.noon.shop.service;

import com.noon.shop.dao.PropertyValueDAO;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.Property;
import com.noon.shop.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueService {
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueDAO propertyValueDAO;

    public void update(PropertyValue propertyValue){
       propertyValueDAO.save(propertyValue);
    }
public void init(Product product){
    List<Property> properties=propertyService.listBycategory(product.getCategory());
    for (Property property:
properties         ) {
        PropertyValue propertyValue=propertyValueDAO.getByPropertyAndProduct(property,product);
        if (null==propertyValue){
            PropertyValue pv=new PropertyValue();
            pv.setProduct(product);
            pv.setProperty(property);
            propertyValueDAO.save(pv);
        }
    }
}

public PropertyValue getByPropertyAndProduct(Property property,Product product){
    return propertyValueDAO.getByPropertyAndProduct(property,product);
}
public List<PropertyValue> list(Product product){
    return propertyValueDAO.findByProductOrderByIdDesc(product);
}
}
