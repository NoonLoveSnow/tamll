package com.noon.shop.service;

import com.noon.shop.dao.ProductDAO;
import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.Product;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public Product get(int id) {
        return productDAO.getOne(id);
    }

    public void delete(int id) {
        productDAO.deleteById(id);
    }

    public void update(Product product) {
        productDAO.save(product);
    }

    public void add(Product product) {
        productDAO.save(product);

    }

    public Page4Navigator list(int cid, int start, int size, int pageNum) {
        Category category = categoryService.get(cid);

        Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
        Page page = productDAO.findByCategory(category, pageable);
        Page4Navigator page4Navigator = new Page4Navigator(page, pageNum);
        return page4Navigator;
    }

    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
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
        for (Product p : products
                ) {

        }
    }

    public List<Product> search(String keyword, int start, int size) {
        Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
        List<Product> products = productDAO.findByNameLike("%" + keyword + "%", pageable);
        return products;
    }

}
