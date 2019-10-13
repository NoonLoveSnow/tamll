package com.noon.shop.web;

import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.ProductImage;
import com.noon.shop.service.CategoryService;
import com.noon.shop.service.ProductImageService;
import com.noon.shop.util.ImageUtil;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public Page4Navigator list(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "5") int size){
        Page4Navigator page4Navigator=categoryService.list(start,size,7);

        return page4Navigator;
    }
    @PostMapping("/categories")
    public Object add(Category c, MultipartFile image, HttpSession session)throws Exception{
        categoryService.add(c);
        SaveOrUpdateImg(c,image,session);
        return c;
    }
    void SaveOrUpdateImg(Category c,MultipartFile image,HttpSession session)throws Exception{
        String imgFolder=session.getServletContext().getRealPath("img/category");
        File imgFile=new File(imgFolder,c.getId()+".jpg");
        if (!imgFile.getParentFile().exists())
            imgFile.getParentFile().mkdirs();
            image.transferTo(imgFile);
        BufferedImage img = ImageUtil.change2jpg(imgFile);
        ImageIO.write(img, "jpg", imgFile);
    }
    @DeleteMapping("/categories/{id}")
    public Object delete(@PathVariable("id") int id,HttpSession session){
        categoryService.delete(id);
        File img=new File(session.getServletContext().getRealPath("img/category"),id+".jpg");
        img.delete();
        return null;
}
    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) throws Exception {
        Category bean=categoryService.get(id);
        return bean;
    }
    @PutMapping("/categories/{id}")
    public Object update(Category bean, MultipartFile image,HttpSession session) throws Exception {
        //String name = request.getParameter("name");
       // bean.setName(name);
        categoryService.update(bean);
        if(image!=null) {
            SaveOrUpdateImg(bean, image, session);
        }
        return bean;
    }

}


