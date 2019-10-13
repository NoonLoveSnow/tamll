package com.noon.shop.service;

import com.noon.shop.dao.PropertyDAO;
import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.Property;
import com.noon.shop.util.JsonUtils;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PropertyService {
    @Autowired
    PropertyDAO propertyDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    public void add(Property property) {
        Set<String> keys=stringRedisTemplate.keys("properties:*");
        stringRedisTemplate.delete(keys);
        propertyDAO.save(property);
    }

    public void delete(int id) {
        Set<String> keys=stringRedisTemplate.keys("properties:*");
        stringRedisTemplate.delete(keys);
        propertyDAO.deleteById(id);
    }

    public void update(Property property) {
        Set<String> keys=stringRedisTemplate.keys("properties:*");
        stringRedisTemplate.delete(keys);
        propertyDAO.save(property);
    }

    public Property get(int id) {
        String propertyStr = stringRedisTemplate.opsForValue().get("properties:id" + id);
        Property property;
        if (propertyStr == null) {
            property = propertyDAO.getOne(id);
            propertyStr = JsonUtils.obj2String(property);
            stringRedisTemplate.opsForValue().set("properties:id" + id, propertyStr);
        } else {
            property = JsonUtils.string2Obj(propertyStr, Property.class);
        }
        return property;
    }

    public Page4Navigator list(int cid, int start, int size, int navNum) {
        Category category = categoryService.get(cid);
        Page4Navigator<Property> page4Navigator;
        String pageStr = stringRedisTemplate.opsForValue().get("properties:cid" + cid + "-page" + start + "-size" + size);
        if (pageStr == null) {
            Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
            Page<Property> page = propertyDAO.findByCategory(category, pageable);
            page4Navigator = new Page4Navigator(page, navNum);
            pageStr = JsonUtils.obj2String(page4Navigator);
            stringRedisTemplate.opsForValue().set("properties:cid" + cid + "-page" + start + "-size" + size, pageStr);
        } else {
            page4Navigator = JsonUtils.string2Obj(pageStr, Page4Navigator.class);
            List<Property> properties = JsonUtils.linkedHashMap2List(page4Navigator.getContent(), Property.class);
            page4Navigator.setContent(properties);
        }
        return page4Navigator;

    }

    public List listBycategory(Category category) {
        List<Property> properties;
        String propertiesStr = stringRedisTemplate.opsForValue().get("properties:cid" + category.getId());
        if (propertiesStr == null) {
            properties = propertyDAO.findByCategory(category);
            propertiesStr = JsonUtils.obj2String(properties);
            stringRedisTemplate.opsForValue().set("properties:cid" + category.getId(), propertiesStr);
        } else {
            properties = JsonUtils.string2List(propertiesStr, Property.class);
        }

        return properties;
    }
}
