package com.noon.shop.service;

import com.noon.shop.dao.ReviewDAO;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.Review;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    ReviewDAO reviewDAO;
    @Autowired
    ProductService productService;

    public void add(Review review){
        reviewDAO.save(review);
    }
    public List<Review> list(Product product){
        List<Review> reviews=reviewDAO.findByProductOrderByIdDesc(product);
        return reviews;
    }
    public int getCount (Product product){

return reviewDAO.countByProduct(product);
    }
}
