package dev.mvc.resort_v4sbm3c;

import dev.mvc.cate.CateProInter;
import dev.mvc.cate.CateVOMenu;
import dev.mvc.tool.Security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;


@Controller
public class HomeCont {


  @Qualifier("dev.mvc.cate.CateProc")
  @Autowired
  private CateProInter cateProc;

  @Autowired
  private Security security;

  public HomeCont() {
    System.out.println("HomeCont Create");
  }

  @GetMapping("/")
  public String home(Model model, HttpServletRequest request, HttpSession session) {
    if (this.security != null) {

    }
    ArrayList<CateVOMenu> menu = this.cateProc.menu();

    String id = (String) session.getAttribute("id");








    return "index";
  }

}
