package com.noon.shop.Comparator;

import com.noon.shop.pojo.Product;

import java.util.Comparator;

public class ProductSaleComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getSaleCount()-o1.getSaleCount();
    }
}
