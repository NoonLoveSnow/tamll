package com.noon.shop.web;

import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.ProductImage;
import com.noon.shop.service.CategoryService;
import com.noon.shop.service.ProductImageService;
import com.noon.shop.service.ProductService;
import com.noon.shop.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductImageController {

    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;
    @GetMapping("/products/{pid}/productImages")
    public List list(String type, @PathVariable("pid")int pid){
        Product product=productService.get(pid);
        if (productImageService.type_single.equals(type)){
            List singleImages=productImageService.listSingleProductImage(product);
            return singleImages;
        }
        else if (productImageService.type_detail.equals(type)){
            List detailImages=productImageService.listDetailProductImage(product);

            return detailImages;
        }else return new ArrayList();
    }
    @PostMapping("/productImages")
    public Object add(int pid, String type, MultipartFile img , HttpSession session){
        ProductImage productImage=new ProductImage();
        Product product = productService.get(pid);
        productImage.setProduct(product);
        productImage.setType(type);
        productImageService.add(productImage);
        String folder;
        if (ProductImageService.type_single.equals(type)){
            folder="img/productSingle";
        }else {
            folder="img/productDetail";
        }
        File imageFolder=new File(session.getServletContext().getRealPath(folder));
        File imgFile=new File(imageFolder,productImage.getId()+".jpg");
        String imageName=imgFile.getName();
        if (!imgFile.getParentFile().exists()){
            imgFile.mkdirs();
        }
        try {
            img.transferTo(imgFile);
            BufferedImage image= ImageUtil.change2jpg(imgFile);
            ImageIO.write(image,"jpg",imgFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (ProductImageService.type_single.equals(type)){
            String imgFolder_small=session.getServletContext().getRealPath("img/productSingle_small");
            String imgFolder_middle=session.getServletContext().getRealPath("img/productSingle_middle");
            File f_small=new File(imgFolder_small,imageName);
            File f_middle=new File(imgFolder_middle,imageName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(imgFile, 56, 56, f_small);
            ImageUtil.resizeImage(imgFile, 217, 190, f_middle);
        }
        return productImage;
    }
@DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable("id")int id ,HttpSession session){
        ProductImage image=productImageService.get(id);
        productImageService.delete(id);
        String folder= "img/";
        if (productImageService.type_single.equals(image.getType())){
            folder+="productSingle";
        }else {
            folder+="productDetail";
        }
        File imageFolder=new File(session.getServletContext().getRealPath(folder));
        File imageFile=new File(imageFolder,image+".jpg");
        String imageName=imageFile.getName();
        imageFile.delete();
    if(ProductImageService.type_single.equals(image.getType())){
        String imageFolder_small=session.getServletContext().getRealPath("/img/productSingle_small");
        String imageFolder_middle=session.getServletContext().getRealPath("/img/productSingle_middle");
        File f_small=new File(imageFolder_small,imageName);
        File f_middle=new File(imageFolder_middle,imageName);
        f_middle.delete();
        f_small.delete();
    }
    return  null;
}



















}
