package com.noon.shop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.noon.shop.dao.ProductDAO;
import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.Product;
import com.noon.shop.util.JsonUtils;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public Product get(int id) {
        Product product;
        String pString = stringRedisTemplate.opsForValue().get("products:id" + id);
        if (pString == null) {
            product = productDAO.getOne(id);
            pString = JsonUtils.obj2String(product);
            stringRedisTemplate.opsForValue().set("products:id" + id, pString);
        } else {
            product = JsonUtils.string2Obj(pString, Product.class);
        }
        return product;
    }

    public void delete(int id) {
        Set<String> keys=stringRedisTemplate.keys("products:*");
        stringRedisTemplate.delete(keys);
        productDAO.deleteById(id);
    }

    public void update(Product product) {
        Set<String> keys=stringRedisTemplate.keys("products:*");
        stringRedisTemplate.delete(keys);
        productDAO.save(product);
    }

    public void add(Product product) {
        Set<String> keys=stringRedisTemplate.keys("products:*");
        stringRedisTemplate.delete(keys);
        productDAO.save(product);

    }

    public Page4Navigator list(int cid, int start, int size, int pageNum) {
        Category category = categoryService.get(cid);
        Page4Navigator<Product> page4Navigator ;

        String pageStr = stringRedisTemplate.opsForValue().get("products:cid" + cid + "-page" + start + "-size" + size);

        if (pageStr == null) {
            Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
            Page page = productDAO.findByCategory(category, pageable);
            page4Navigator = new Page4Navigator(page, pageNum);
            productImageService.setFirstProductImage(page4Navigator.getContent());
            pageStr = JsonUtils.obj2String(page4Navigator);
            stringRedisTemplate.opsForValue().set("products:cid" + cid + "-page" + start + "-size" + size, pageStr);
        } else {
            page4Navigator = JsonUtils.string2Obj(pageStr, Page4Navigator.class);
            List<Product> products =  JsonUtils.linkedHashMap2List(page4Navigator.getContent(),Product.class);
            page4Navigator.setContent(products);
        }

        return page4Navigator;
}

    public List<Product> listByCategory(Category category) {

        List products ;
        String psStr=stringRedisTemplate.opsForValue().get("products:cid"+ category.getId());
        if (psStr==null) {
            products = productDAO.findByCategoryOrderById(category);
            psStr=JsonUtils.obj2String(products);
            stringRedisTemplate.opsForValue().set("products:cid"+ category.getId(),psStr);
        }else {
          products= JsonUtils.string2List(psStr,Product.class);

        }

        return products;
    }

    public void fill(Category category) {
        List<Product> products = this.listByCategory(category);
        productImageService.setFirstProductImage(products);
        category.setProducts(products);
    }

    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }

    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;
        for (Category c :
                categories) {
            List<Product> products = c.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);//每8个为一行
                productsByRow.add(productsOfEachRow);
            }
            c.setProductsByRow(productsByRow);
        }

    }

    public void setReviewAndSaleCount(Product product) {
        int reviewCount = reviewService.getCount(product);
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);
        product.setReviewCount(reviewCount);
    }

    public void setReviewAndSaleCount(List<Product> products) {
        for (Product p : products) {
            setReviewAndSaleCount(p);
        }
    }

    public List<Product> search(String keyword, int start, int size) {
        Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
        List<Product> products = productDAO.findByNameLike("%" + keyword + "%", pageable);
        return products;
    }

}
