package com.noon.shop.service;

import com.noon.shop.dao.CategoryDAO;
import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.Product;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    RedisTemplate redisTemplate;


    public List<Category> list() {

        List<Category> categories = (List<Category>) redisTemplate.opsForValue().get("categories:all");
        if (categories == null) {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            categories = categoryDAO.findAll(sort);
            redisTemplate.opsForValue().set("categories:all", categories);
        }
//System.out.println("-------------------------------------------------------------------------"+i++);
        return categories;
    }

    public Page4Navigator list(int start, int size, int navNum) {
        Page4Navigator page4Navigator = (Page4Navigator) redisTemplate.opsForValue().get("categories:page" + start + "-size" + size);
        if (page4Navigator == null) {
            Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
            Page page = categoryDAO.findAll(pageable);
            page4Navigator = new Page4Navigator(page, navNum);
            redisTemplate.opsForValue().set("categories:page" + start + "-size" + size, page4Navigator);
        }

        return page4Navigator;
    }

    public void add(Category c) {
        Set<String> keys = redisTemplate.keys("categories:*");
        redisTemplate.delete(keys);
        categoryDAO.save(c);
    }

    public void delete(int id) {
        Set<String> keys = redisTemplate.keys("categories:*");
        redisTemplate.delete(keys);
        categoryDAO.deleteById(id);
    }

    public Category get(int id) {

        Category c = (Category) redisTemplate.opsForValue().get("categories:id" + id);
        if (c == null) {
            c = categoryDAO.findById(id).get();
            redisTemplate.opsForValue().set("categories:id" + id, c);
        }
        return c;
    }

    public void update(Category bean) {
        Set<String> keys = redisTemplate.keys("categories:*");
        redisTemplate.delete(keys);
        categoryDAO.save(bean);
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
