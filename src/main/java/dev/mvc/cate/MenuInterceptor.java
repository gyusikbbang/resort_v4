package dev.mvc.cate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Component
public class MenuInterceptor implements HandlerInterceptor {

  @Autowired
  private CateProc cateProc; // CateProc를 주입해야 합니다.

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // 컨트롤러가 호출되기 전에 실행됩니다.
    HttpSession session = request.getSession();
    String id = (String) session.getAttribute("id");

    if (id == null) {
      return  true;
    } else {
      ArrayList<CateVOMenu> menu = cateProc.menu();
      request.setAttribute("menu", menu); // 모델 어트리뷰트로 설정합니다.
      return true; // 다음 인터셉터 또는 컨트롤러로 요청을 전달합니다.
    }

  }

  // 다음 두 메서드는 필요에 따라 오버라이드할 수 있습니다.
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    // 컨트롤러가 실행된 후에 실행됩니다.
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    // 뷰가 렌더링된 후에 실행됩니다.
  }
}