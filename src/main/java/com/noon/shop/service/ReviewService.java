package com.noon.shop.service;

import com.noon.shop.dao.ReviewDAO;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.Review;
import com.noon.shop.util.JsonUtils;
import com.sun.org.apache.regexp.internal.RE;
import javafx.scene.effect.SepiaTone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ReviewService {
    @Autowired
    ReviewDAO reviewDAO;
    @Autowired
    ProductService productService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void add(Review review) {
        Set<String> keys = stringRedisTemplate.keys("reviews:*");
        stringRedisTemplate.delete(keys);
        reviewDAO.save(review);
    }

    public List<Review> list(Product product) {
        List<Review> reviews;
        String reviewsStr = stringRedisTemplate.opsForValue().get("reviews:pid" + product.getId());
        if (reviewsStr == null) {
            reviews = reviewDAO.findByProductOrderByIdDesc(product);
            reviewsStr = JsonUtils.obj2String(reviews);
            stringRedisTemplate.opsForValue().set("reviews:pid" + product.getId(), reviewsStr);
        } else {
            reviews = JsonUtils.string2List(reviewsStr, Review.class);
        }

        return reviews;
    }

    public int getCount(Product product) {
        int count;
        String countStr = stringRedisTemplate.opsForValue().get("reviews:count-pid"+product.getId());
        if (countStr == null) {
            count = reviewDAO.countByProduct(product);
            stringRedisTemplate.opsForValue().set("reviews:count-pid"+product.getId(), String.valueOf(count));
        } else {
            count = Integer.parseInt(countStr);
        }
        return count;
    }
}
