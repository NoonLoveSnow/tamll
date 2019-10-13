package com.noon.shop.service;

import com.noon.shop.dao.PropertyValueDAO;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.Property;
import com.noon.shop.pojo.PropertyValue;
import com.noon.shop.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PropertyValueService {
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void update(PropertyValue propertyValue) {
        Set<String> keys = stringRedisTemplate.keys("propertyValues:*");
        stringRedisTemplate.delete(keys);
        propertyValueDAO.save(propertyValue);
    }

    public void init(Product product) {
        List<Property> properties = propertyService.listBycategory(product.getCategory());
        for (Property property : properties) {
            PropertyValue propertyValue = this.getByPropertyAndProduct(property, product);
            if (null == propertyValue) {
                PropertyValue pv = new PropertyValue();
                pv.setProduct(product);
                pv.setProperty(property);
                propertyValueDAO.save(pv);
            }
        }
    }

    public PropertyValue getByPropertyAndProduct(Property property, Product product) {
        PropertyValue propertyValue;
        String pvStr = stringRedisTemplate.opsForValue().get("propertyValues:id" + property.getId() + "-pid" + product.getId());
        if (pvStr == null) {
            propertyValue = propertyValueDAO.getByPropertyAndProduct(property, product);
            pvStr = JsonUtils.obj2String(propertyValue);
            stringRedisTemplate.opsForValue().set("propertyValues:id" + property.getId() + "-pid" + product.getId(), pvStr);
        } else {
            propertyValue = JsonUtils.string2Obj(pvStr, PropertyValue.class);
        }

        return propertyValue;
    }

    public List<PropertyValue> list(Product product) {
        List<PropertyValue> pvs;
        String pvsStr = stringRedisTemplate.opsForValue().get("propertyValues:pid" + product.getId());
        if (pvsStr == null) {
            pvs = propertyValueDAO.findByProductOrderByIdDesc(product);
            pvsStr = JsonUtils.obj2String(pvs);
            stringRedisTemplate.opsForValue().set("propertyValues:pid" + product.getId(), pvsStr);
        } else {
            pvs = JsonUtils.string2List(pvsStr, PropertyValue.class);
        }

        return pvs;
    }
}
