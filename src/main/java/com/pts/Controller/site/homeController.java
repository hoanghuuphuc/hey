package com.pts.Controller.site;

import com.pts.DAO.*;
import com.pts.Service.AccountService;
import com.pts.Service.CategoryService;
import com.pts.Service.CourseService;

import java.util.Base64;
import com.pts.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@Controller

public class homeController {
    @Autowired
    AccountService accountService;
    @Autowired
    CourseService courseService;

    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ContentDAO contentDAO;
    @Autowired
    ChapterDAO chapterDAO;
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    AccountDAO accountDAO;

    @Autowired
    CourseDAO courseDAO;
    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/...")
    public String main(){
        return "/stie/index";
    }




    @RequestMapping("")
    public String index(Model m,HttpServletRequest request){

        String username =request.getRemoteUser();
        Account acc =accountDAO.laytk(username);
        m.addAttribute("photo",acc);

        //khoa hoc free
        List<Course> courses = courseService.findAll().stream().filter(Course::isTps_Status).filter(course -> course.getTps_Price()==0).collect(Collectors.toList());
        m.addAttribute("courses",courses);

        //khoa hoc co phi
        List<Course> courses1 = courseService.findAll().stream().filter(Course::isTps_Status).filter(course -> course.getTps_Price() >0).collect(Collectors.toList());
        m.addAttribute("coursess",courses1);

        //danh muc khoa hoc
        List<Category>categories =categoryDAO.findAll();
        m.addAttribute("categories",categories);




        return "/site/home";
    }


//    trang chi ti???t


    @RequestMapping("/khoa-hoc/{tps_id}")
    public String trangchitiet(Model m, @PathVariable("tps_id") int id, HttpServletRequest request) throws FileNotFoundException {
        //tim ki???m n???i dung theo id

            Course course = courseService.findById(id);
            m.addAttribute("detail", course);
            String username=request.getRemoteUser();
//        System.out.println(username);
            Order order = orderDAO.ktKhoaHoc(username, id);
            if (order != null) {
                m.addAttribute("owned", true);
            } else {
                m.addAttribute("owned", false);
            }

            //n???i d???ng ch????ng
            List<Chapter> cc = chapterDAO.findByCourse(id);
            m.addAttribute("listC", cc);

            //doc file
            int htt = id;
            String url = "P:\\Code_QuanTrong\\Code_Khoa_Hoc\\backend\\TP_Store\\src\\main\\resources\\static\\noidung\\" + htt + ".txt";
            // ?????c d??? li???u t??? File v???i Scanner
            try {
                FileInputStream fileInputStream = new FileInputStream(url);
                Scanner scanner = new Scanner(fileInputStream);
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    sb.append(line).append("\n");
                }
                String fileContent = sb.toString();
                m.addAttribute("fileContent", fileContent);
            } catch (FileNotFoundException e){
                System.out.println("loi");
            }
            //tong chuong
            int count=chapterDAO.findByCourse(id).size();

            m.addAttribute("soluongChuong",count);

            //tong bai hoc
            int totalContent = 0;
            for (Chapter chapter : cc) {
                totalContent += chapter.getContents().size();
                m.addAttribute("totalContent",totalContent);
            }
            return "/site/details";

    }



//    @RequestMapping("/khoa-hoc")
//    public String chitiet(){
//        return "/site/details";
//
//    }
}
