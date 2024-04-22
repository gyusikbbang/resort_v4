package dev.mvc.contents;


import java.util.ArrayList;
import java.util.Optional;


import dev.mvc.cate.CateProInter;
import dev.mvc.cate.CateVOMenu;
import dev.mvc.member.MemberProInter;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import dev.mvc.cate.CateVO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(value = "/contents")
public class ContentsCont {
  @Autowired
  @Qualifier("dev.mvc.member.MemberProc") // @Component("dev.mvc.admin.AdminProc")
  private MemberProInter memberProC;

  @Autowired
  @Qualifier("dev.mvc.cate.CateProc")  // @Component("dev.mvc.cate.CateProc")
  private CateProInter cateProc;

  @Autowired
  @Qualifier("dev.mvc.contents.ContentsProc") // @Component("dev.mvc.contents.ContentsProc")
  private ContentsProcInter contentsProc;

  public ContentsCont() {
    System.out.println("-> ContentsCont created.");
  }

  /**
   * POST 요청시 JSP 페이지에서 JSTL 호출 기능 지원, 새로고침 방지, EL에서 param으로 접근
   * POST → url → GET → 데이터 전송
   *
   * @return
   */


  @GetMapping("/msg")
  public String msg(Model model, CateVO cateVO, String url) {


    return url; // forward
  }

  // 등록 폼, contents 테이블은 FK로 cateno를 사용함.
  // http://localhost:9091/contents/create.do  X
  // http://localhost:9091/contents/create.do?cateno=1  // cateno 변수값을 보내는 목적
  // http://localhost:9091/contents/create.do?cateno=2
  // http://localhost:9091/contents/create.do?cateno=3
  @GetMapping(value = "/create")
  public String create(Model model, CateVO cateVO, ContentsVO contentsVO, @RequestParam(defaultValue = "1") Integer cateno) {


    Optional<CateVO> read = this.cateProc.read(cateno);
    if (read.isPresent()) {



      model.addAttribute("cateVO", read.get());
      model.addAttribute("cateno", cateno);
      return "contents/create";

    } else {
      return "redirect:/contents/list_all";
    }


  }

  /**
   * 등록 처리 http://localhost:9091/contents/create.do
   *
   * @return
   */
  @PostMapping(value = "/create")
  public String create(Model model, HttpServletRequest request,
                       HttpSession session,
                       ContentsVO contentsVO
    , RedirectAttributes ra) {


    if (memberProC.isMemberAdmin(session)) { // 관리자로 로그인한경우
      // ------------------------------------------------------------------------------
      // 파일 전송 코드 시작
      // ------------------------------------------------------------------------------
      String file1 = "";          // 원본 파일명 image
      String file1saved = "";   // 저장된 파일명, image
      String thumb1 = "";     // preview image

      String upDir = Contents.getUploadDir(); // 파일을 업로드할 폴더 준비
      System.out.println("-> upDir: " + upDir);

      // 전송 파일이 없어도 file1MF 객체가 생성됨.
      // <input type='file' class="form-control" name='file1MF' id='file1MF'
      //           value='' placeholder="파일 선택">
      MultipartFile mf = contentsVO.getFile1MF();

      file1 = mf.getOriginalFilename(); // 원본 파일명 산출, 01.jpg
      System.out.println("-> 원본 파일명 산출 file1: " + file1);

      long size1 = mf.getSize();  // 파일 크기


      if (size1 > 0) { // 파일 크기 체크
        // 파일 저장 후 업로드된 파일명이 리턴됨, spring.jsp, spring_1.jpg...
        if (Tool.checkUploadFile(file1) == true) { // 업로드 가능한 파일인지 검사
          file1saved = Upload.saveFileSpring(mf, upDir);

          if (Tool.isImage(file1saved)) { // 이미지인지 검사
            // thumb 이미지 생성후 파일명 리턴됨, width: 200, height: 150
            thumb1 = Tool.preview(upDir, file1saved, 200, 150);
          }

          contentsVO.setFile1(file1);   // 순수 원본 파일명
          contentsVO.setFile1saved(file1saved); // 저장된 파일명(파일명 중복 처리)
          contentsVO.setThumb1(thumb1);      // 원본이미지 축소판
          contentsVO.setSize1(size1);  // 파일 크기
          int memberno = (int) session.getAttribute("memberno"); // adminno FK
          contentsVO.setMemberno(memberno);
          int cnt = this.contentsProc.create(contentsVO);
          return "redirect:/contents/list_all";


        } else {
          ra.addFlashAttribute("cnt", "0"); // 업로드 할 수 없는 파일
          ra.addFlashAttribute("code", "check_upload_file_fail"); // 업로드 할 수 없는 파일
          ra.addFlashAttribute("url", "/contents/msg"); // msg.jsp, redirect parameter 적용
          return "redirect:/contents/msg";
        }

      } else { // 글만등록하는경우
        int memberno = (int) session.getAttribute("memberno"); // adminno FK
        contentsVO.setMemberno(memberno);
        int cnt = this.contentsProc.create(contentsVO);
        System.out.println("글만 등록");
        return "redirect:/contents/list_all";
      }


    } else { //admin 아닐경우
      return "redirect:/member/login";
    }

  }

  /**
   * 전체 목록, 관리자만 사용 가능
   * http://localhost:9091/contents/list_all.do
   *
   * @return
   */
  @GetMapping("list_all")
  public String list_all(HttpSession session, Model model) {
    System.out.println("-> list_all");

    if (this.memberProC.isMemberAdmin(session) == true) {


      ArrayList<ContentsVO> list = this.contentsProc.list_all();

      // for문을 사용하여 객체를 추출, Call By Reference 기반의 원본 객체 값 변경
//      for (ContentsVO contentsVO : list) {
//        String title = contentsVO.getTitle();
//        String content = contentsVO.getContent();
//
//        title = Tool.convertChar(title);  // 특수 문자 처리
//        content = Tool.convertChar(content);
//
//        contentsVO.setTitle(title);
//        contentsVO.setContent(content);
//
//      }

      model.addAttribute("list", list);
      return "/contents/list_all";
    } else {
      return "redirect:/member/login";

    }


  }
  /**
   * 카테고리별 목록
   * http://localhost:9091/contents/list_by_cateno?cateno=5
   * http://localhost:9091/contents/list_by_cateno?cateno=6
   * @return
   */
  @GetMapping(value="/list_by_cateno")
  public String list_by_cateno(HttpSession session, Model model, int cateno) {
    ArrayList<CateVOMenu> menu = this.cateProc.menu();
    model.addAttribute("menu", menu);

    Optional<CateVO> read = this.cateProc.read(cateno);
    if (read.isPresent()) {
      model.addAttribute("cateVO", read.get());

      ArrayList<ContentsVO> list = this.contentsProc.list_by_cateno(cateno);
      model.addAttribute("list", list);

      // System.out.println("-> size: " + list.size());

      return "contents/list_by_cateno";
    } else {
      return "redirect:/contents/list_all";
    }

  }

}
