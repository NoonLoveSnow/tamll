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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;
    public List<Category>list(){
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        return categoryDAO.findAll(sort);
    }
    public Page4Navigator list(int start,int size,int navNum){
        Pageable pageable= PageRequest.of(start,size,Sort.Direction.DESC,"id");
        Page page=categoryDAO.findAll(pageable);
        return new Page4Navigator(page,navNum);
    }
    public void add(Category c){
        categoryDAO.save(c);
    }
    public void delete(int id){
        categoryDAO.deleteById(id);
    }
    public Category get(int id) {
        Category c= categoryDAO.getOne(id);

        return c;
    }
    public void update(Category bean) {
        categoryDAO.save(bean);
    }
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products =category.getProducts();
        if(null!=products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow =category.getProductsByRow();
        if(null!=productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p: ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
