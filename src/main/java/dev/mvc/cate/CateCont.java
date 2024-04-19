package dev.mvc.cate;

import java.io.UnsupportedEncodingException;

import dev.mvc.member.MemberProInter;
import dev.mvc.tool.Tool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RequestMapping("/cate")
@Controller
public class CateCont {
  @Autowired
  @Qualifier("dev.mvc.cate.CateProc")
  private CateProInter cateProc;

  /**
   * 페이지당 출력할 레코드 개수
   */
  public int record_per_page = 3;

  @Autowired
  @Qualifier("dev.mvc.member.MemberProc")
  private MemberProInter memberProc;
  /**
   * 블록당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨  nowPage는 1부터 시작
   */
  public int page_per_blcok = 10;

  // 객체 생성
  public CateCont() {
    System.out.println("-> CateCont created.");
  }

//  @GetMapping(value="/list_all") // 오버랜딩cate/list_all
//  @ResponseBody
//  public String create() {
//    return "<h2>Create test.</h2>";
//  }

  // 폼 출력
//  @GetMapping(value = "/list_all") // 오버랜딩cate/list_all
//  public String list(CateVO cateVO, Model model) {
//    cateVO.setNamesub("-");
//    ArrayList<CateVO> list_all_name_y = this.cateProc.list_all_name_y();
//
//
//    ArrayList<CateVO> list = this.cateProc.list();
//
//
//    model.addAttribute("list", list);
//    return "/cate/list_all"; // /templates/cate/list_all.html
//  }

  @GetMapping(value = "/list_y") // 오버랜딩cate/list_all
  public String list_y(CateVO cateVO, Model model) {
    ArrayList<CateVO> list = this.cateProc.list_all_name_y();


    model.addAttribute("catelist", list);
    return "cate/list_y"; // /templates/cate/list_all.html
  }

  // 폼 데이터 처리


//  @GetMapping("/list_all")
//     public String list(Model model) {
//      ArrayList<CateVO> list = this.cateProc.list();
//
//
//      model.addAttribute("catelist",list);
//      return "cate/list_all";
//    }

  // 단일 조회
  @GetMapping("/read/{cateno}")
  public String read(Model model, @PathVariable int cateno,
                     @RequestParam(name = "word", defaultValue = "")
                     String word, @RequestParam(name = "type") String type,
                     @RequestParam(defaultValue = "1") int now_page, HttpSession session) {
    String id = (String) session.getAttribute("id");
    String grade = (String) session.getAttribute("grade");

    if (this.memberProc.isMemberAdmin(session)) {
      Optional<CateVO> read = this.cateProc.read(cateno);

      type = Tool.typecheckNull(type);

      int count = this.cateProc.list_search_count(word, type);
      int num = count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("num", num);
      ArrayList<CateVO> catelist = this.cateProc.list_search_paging(word, type, now_page, this.record_per_page);
      String paging = this.cateProc.pagingBox(now_page, word, type, "/cate/search", count, record_per_page, page_per_blcok);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      model.addAttribute("searchlist", catelist);

      if (read.isPresent()) {
        model.addAttribute("word", word);
        model.addAttribute("type", type);
        model.addAttribute("cateVO", read.get());
        return "cate/read";
      } else {
        return "redirect:/cate/list_all";
      }
    } else {
      return "redirect:/member/login";
    }


  }

  @GetMapping("/read_y/{cateno}")
  public String read_y(Model model, @PathVariable int cateno) {

    Optional<CateVO> read = this.cateProc.read(cateno);

    ArrayList<CateVO> list = this.cateProc.list();


    model.addAttribute("catelist", list);

    if (read.isPresent()) {

      model.addAttribute("cateVO", read.get());
      return "cate/read_y"; // 적절한 뷰 이름을 반환합니다.
    } else {
      return "redirect:/cate/list_y";
    }


  }

  // 수정 폼
  @GetMapping("/update/{cateno}")
  public String updateForm(Model model, @PathVariable int cateno,
                           @RequestParam(name = "word", defaultValue = "") String word,
                           @PathVariable(required = false) @RequestParam(name = "type", required = false, defaultValue = "") String type,
                           @RequestParam(defaultValue = "1") int now_page, HttpSession session) {
    String id = (String) session.getAttribute("id");
    String grade = (String) session.getAttribute("grade");
    if (this.memberProc.isMemberAdmin(session)) {
      Optional<CateVO> read = this.cateProc.read(cateno);


      type = Tool.typecheckNull(type);

      if (word == null || word.equals("null")) {
        word = read.get().getName();

      }


      int count = this.cateProc.list_search_count(word, type);
      ArrayList<CateVO> catelist = this.cateProc.list_search_paging(word, type, now_page, this.record_per_page);
      int num = count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("num", num);
      String paging = this.cateProc.pagingBox(now_page, word, type, "/cate/search", count, record_per_page, page_per_blcok);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      model.addAttribute("word", word);
      model.addAttribute("type", type);


      model.addAttribute("searchlist", catelist);


      if (read.isPresent()) {
        model.addAttribute("cateVO", read.get());


        return "cate/update"; // 적절한 뷰 이름을 반환합니다
      } else {
        return "redirect:cate/search";
      }
    } else {
      return "redirect:/";
    }


  }


  @PostMapping("/update/{cateno}")
  public String update(Model model, @Valid CateVO cateVO, BindingResult bindingResult,
                       @PathVariable("cateno") int cateno, @RequestParam(name = "word", defaultValue = "") String word,
                       @RequestParam(name = "type", defaultValue = "100") String type, RedirectAttributes rttr,
                       @RequestParam(defaultValue = "1") int now_page, HttpSession session) throws UnsupportedEncodingException {
    if (this.memberProc.isMemberAdmin(session)) {


      int count = this.cateProc.list_search_count(word, type);
      ArrayList<CateVO> catelist = this.cateProc.list_search_paging(word, type, now_page, this.record_per_page);
      int num = count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("num", num);
      String paging = this.cateProc.pagingBox(now_page, word, type, "/cate/search", count, record_per_page, page_per_blcok);
      model.addAttribute("paging", paging);
      model.addAttribute("searchlist", catelist);
      model.addAttribute("now_page", now_page);

      if (bindingResult.hasErrors()) {
        return "cate/update"; // 유효성 검사 에러가 있을 경우 수정 폼으로 이동
      }

      if (word != null && type != null) {
        model.addAttribute("word", word);
        model.addAttribute("type", type);
      }

      String visible = cateVO.getVisible().toUpperCase();
      cateVO.setVisible(visible);

      int cnt = this.cateProc.update(cateVO);
      model.addAttribute("cnt", cnt);

      if (cnt == 1) {
        rttr.addFlashAttribute("m", cateno + "번이 수정되었습니다");

        String redirectUrl = String.format("/cate/update/%d?word=%s&type=%s&now_page=%s", cateno, Tool.encode(word), Tool.encode(type), now_page);
        return "redirect:" + redirectUrl;
      } else {
        model.addAttribute("code", "update_fail");
      }

      model.addAttribute("cateVO", cateVO);
      return "cate/updatemsg"; // 수정 결과 메시지를 표시하는 뷰로 이동

    } else {
      return "redirect:/";
    }
  }

  /***
   *
   *  출력 순서 높임: seqno 10 ->1
   * @param model
   * @param cateno 조회할 카테고리 번호
   */


  @PostMapping("/upforward/{cateno}")
  public String update_forward(Model model, @PathVariable("cateno") int cateno, @RequestParam(name = "type",
    defaultValue = "100") String type, @RequestParam(name = "word",
    defaultValue = "") String word, HttpServletRequest request,
                               @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                               @RequestParam(name = "num") int num,
                               @RequestParam(name = "name") String name,
                               HttpSession session,
                               RedirectAttributes rttr) {
    if (this.memberProc.isMemberAdmin(session)) {
      int cnt = this.cateProc.update_forward(cateno);
      model.addAttribute("cnt", cnt);
      model.addAttribute("now_page", now_page);
      if (cnt == 1) {
        String referer = request.getHeader("Referer");
        // Referer 헤더를 통해 이전 페이지의 주소를 가져옴
        if (referer != null && referer.contains("/cate/read")) {
          // 이전 페이지가 read 페이지인 경우
          rttr.addFlashAttribute("up", num + "번의 순서가 1 증가 되었습니다. 중분류 이름: " + name);
          return "redirect:/cate/read/" + cateno + "?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        } else {
          // 이전 페이지가 list_all 페이지인 경우
          rttr.addFlashAttribute("up", num + "번의 순서가 1 증가 되었습니다. 중분류 이름: " + name);
          return "redirect:/cate/search?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        }
      } else {
        model.addAttribute("code", "update_fail");
        return "redirect:/cate/list_all/";
      }
    } else {
      return "redirect:/";
    }

  }


  @PostMapping("/upbackward/{cateno}")
  public String upbackward(Model model, @PathVariable("cateno") int cateno,
                           @RequestParam(name = "type", defaultValue = "100") String type,
                           @RequestParam(name = "word", defaultValue = "") String word,
                           @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                           @RequestParam(name = "num") int num,
                           @RequestParam(name = "name") String name,
                           HttpServletRequest request, RedirectAttributes rttr,
                           HttpSession session) {
    if (this.memberProc.isMemberAdmin(session)) {
      int cnt = this.cateProc.update_backward(cateno);

      model.addAttribute("cnt", cnt);
      model.addAttribute("now_page", now_page);
      if (cnt == 1) {
        String referer = request.getHeader("Referer");
        // Referer 헤더를 통해 이전 페이지의 주소를 가져옴
        if (referer != null && referer.contains("/cate/read")) {
          // 이전 페이지가 read 페이지인 경우
          rttr.addFlashAttribute("down", num + "번의 순서가 1 감소 되었습니다. 중분류 이름: " + name);
          return "redirect:/cate/read/" + cateno + "?word" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        } else {
          // 이전 페이지가 list_all 페이지인 경우
          rttr.addFlashAttribute("down", num + "번의 순서가 1 감소 되었습니다. 중분류 이름: " + name);
          return "redirect:/cate/search?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        }
      } else {
        model.addAttribute("code", "update_fail");
        return "redirect:/cate/search";
      }
    } else {
      return "redirect:/member/login";
    }

  }


  /***
   *
   *  삭제: cateno 기준으로 공개 처리
   * @param model
   * @param cateno 수정할 카테고리 번호
   */

  @PostMapping("/show/{cateno}")
  public String show(Model model, @PathVariable("cateno") int cateno,
                     @RequestParam(name = "type", defaultValue = "100") String type,
                     @RequestParam(name = "word", defaultValue = "") String word,
                     @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                     @RequestParam(name = "num") int num,
                     @RequestParam(name = "name") String name,
                     HttpServletRequest request, RedirectAttributes rttr,
                     HttpSession session) {
    if (this.memberProc.isMemberAdmin(session)) {


      int cnt = this.cateProc.show(cateno);
      model.addAttribute("cnt", cnt);
      model.addAttribute("now_page", now_page);
      if (cnt == 1) {
        String referer = request.getHeader("Referer");
        // Referer 헤더를 통해 이전 페이지의 주소를 가져옴
        if (referer != null && referer.contains("/cate/read")) {
          // 이전 페이지가 read 페이지인 경우
          rttr.addFlashAttribute("show", num + "번이 공개 처리 되었습니다. 중분류 이름: " + name);
          return "redirect:/cate/read/" + cateno + "?word" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        } else {
          // 이전 페이지가 list_all 페이지인 경우
          rttr.addFlashAttribute("show", num + "번이 공개 처리 되었습니다. 중분류 이름: " + name);
          return "redirect:cate/search?type=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        }
      } else {
        model.addAttribute("code", "update_fail");
        return "redirect:cate/search";
      }

    } else {
      return "redirect:/member/login";
    }
  }

  /***
   *
   *  삭제: cateno 기준으로 비공개 처리
   * @param model
   * @param cateno 수정할 카테고리 번호
   */


  @PostMapping("/hide/{cateno}")
  public String hide(Model model, @PathVariable("cateno") int cateno,
                     @RequestParam(name = "type", defaultValue = "100") String type,
                     @RequestParam(name = "word", defaultValue = "") String word,
                     @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                     @RequestParam(name = "num") int num,
                     @RequestParam(name = "name") String name,
                     HttpServletRequest request, RedirectAttributes rttr,
                     HttpSession session) {
    if (this.memberProc.isMemberAdmin(session)) {


      int cnt = this.cateProc.hide(cateno);
      model.addAttribute("cnt", cnt);
      model.addAttribute("now_page", now_page);
      if (cnt == 1) {
        String referer = request.getHeader("Referer");
        // Referer 헤더를 통해 이전 페이지의 주소를 가져옴
        if (referer != null && referer.contains("/cate/read")) {
          // 이전 페이지가 read 페이지인 경우
          rttr.addFlashAttribute("hide", num + "번이 숨김 처리 되었습니다. 중분류 이름: " + name);
          return "redirect:cate/read/" + cateno + "?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        } else {
          // 이전 페이지가 list_all 페이지인 경우
          rttr.addFlashAttribute("hide", num + "번이 숨김 처리 되었습니다. 중분류 이름: " + name);
          return "redirect:cate/search?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;
        }
      } else {
        model.addAttribute("code", "update_fail");
        return "redirect:cate/search";
      }
    } else {
      return "redirect:/member/login";
    }
  }


  /***
   *
   *  삭제: cateno 기준으로 삭제
   * @param model
   * @param cateno 조회할 카테고리 번호
   */

  @PostMapping("/delete/{cateno}")
  public String delete(Model model, @PathVariable("cateno") int cateno,
                       @RequestParam("name") String name, @RequestParam("word") String word,
                       @RequestParam(value = "type", defaultValue = "100") String type,
                       @RequestParam(name = "now_page", defaultValue = "1") int now_page
    , @RequestParam(value = "num") int num,
                       RedirectAttributes rttr,
                       HttpSession session) {


    if (this.memberProc.isMemberAdmin(session)) {


      int count = this.cateProc.delete(cateno);
      int search_cnt = this.cateProc.list_search_count(word, type);
      if (search_cnt % this.record_per_page == 0) {
        now_page = now_page - 1;
        if (now_page < 1) {
          now_page = 1;
        }
      }

      model.addAttribute("word", word);
      model.addAttribute("now_page", now_page);
      model.addAttribute("type", type);

      if (count == 1 && (word == null || word.isEmpty() || "null".equals(word))) {
        rttr.addFlashAttribute("delete", num + "번 중분류 '" + name + "'이 삭제되었습니다.");
        return "redirect:/cate/search?now_page=" + now_page;
      } else if (count == 1) {
        rttr.addFlashAttribute("delete", num + "번 중분류 '" + name + "'이 삭제되었습니다.");
        return "redirect:/cate/search?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page" + now_page;
      } else {
        rttr.addFlashAttribute("delete", "삭제 실패");
        return "redirect:/cate/search";
      }
    }else {
      return "redirect:/member/login";
    }
  }


  @PostMapping("/multidelete")
  public String multidelete(@RequestParam("catenoList") List<Integer> catenoList, RedirectAttributes rttr,HttpSession session) {

    if (this.memberProc.isMemberAdmin(session)) {
      int count = this.cateProc.multiple_delete(catenoList);
      if (count > 0) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < catenoList.size(); i++) {
          Integer cateno = catenoList.get(i);
          sb.append(cateno).append("번");
          if (i < catenoList.size() - 1) { // 현재 항목이 리스트의 마지막이 아닌 경우에만 쉼표와 공백 추가
            sb.append(", ");
          }
        }
        String catenoString = sb.toString(); // 문자열로 변환
        rttr.addFlashAttribute("multi", "선택한 " + catenoString + " 카테고리가 삭제되었습니다. " + "삭제된 개수: " + count + "개");
      } else {
        rttr.addFlashAttribute("multi", "선택한 카테고리 삭제에 실패했습니다.");
      }
      return "redirect:/cate/list_all";
    } else {
      return "redirect:/member/login";
    }

  }

//  /**
//   * 등록폼 + 검색번호
//   * @param model
//   * @param type
//   * @param word
//   * @param cateVO
//   * @param now_page 현재 페이지 번호
//   * @return dw
//   */

  @GetMapping("/search")
  public String searchForm(HttpSession session, Model
    model, @RequestParam(name = "type", defaultValue = "100") String type, String word, CateVO cateVO,
                           @RequestParam(name = "now_page", defaultValue = "1") int now_page) {

    String id = (String) session.getAttribute("id");
    String grade = (String) session.getAttribute("grade");
    if (memberProc.isMemberAdmin(session)) {
      if (now_page < 1) {
        now_page = 1;
      }


      word = Tool.wordcheckNull(word);
      type = Tool.wordcheckNull(type);
      String types = "";


      if (type.equals("100")) {
        types = "중분류";
      } else if (type.equals("200")) {
        types = "소분류";
      } else {
        types = "중분류 + 소분류";
      }


      int count = this.cateProc.list_search_count(word, type);
      // 일련 번호 생성
      int num = count - ((now_page - 1) * this.record_per_page);
      ArrayList<CateVO> catelist = this.cateProc.list_search_paging(word, type, now_page, this.record_per_page);
      String paging = this.cateProc.pagingBox(now_page, word, type, "/cate/search", count, record_per_page, page_per_blcok);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      model.addAttribute("count", count);

      model.addAttribute("num", num);
      if (catelist.isEmpty()) {
        model.addAttribute("word", word);
        model.addAttribute("type", type);
        model.addAttribute("nulllist", "결과가 없습니다.");
      } else if (!catelist.isEmpty() && !word.equals("")) {
        model.addAttribute("word", word);
        model.addAttribute("type", type);
        model.addAttribute("search", types + ":" + word + "에  대한 검색 결과 : 총 " + catelist.size() + "건");
      }

      model.addAttribute("word", word);
      model.addAttribute("type", type);
      model.addAttribute("searchlist", catelist);


      return "cate/search_all"; // Assuming "search_result" is the name of the view to display the search results
    } else {
      return "redirect:/";


    }


  }

  @PostMapping(value = "/create") // 오버랜딩cate/list_all
  public String create(Model model, HttpServletResponse response, @Valid CateVO cateVO, BindingResult bindingResult,
                       RedirectAttributes rttr, @RequestParam(name = "type", defaultValue = "100") String type,
                       HttpSession session,
                       @RequestParam(name = "word", defaultValue = "") String word, @RequestParam(defaultValue = "1") int now_page) {

    if (this.memberProc.isMemberAdmin(session)) {
      int count = this.cateProc.list_search_count(word, type);
      model.addAttribute("type", type);
      model.addAttribute("word", word);
      ArrayList<CateVO> list = this.cateProc.list_search_paging(word, type, now_page, this.record_per_page);

      int num = count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("num", num);
      model.addAttribute("searchlist", list);
      String paging = this.cateProc.pagingBox(now_page, word, type, "/cate/search", count, record_per_page, page_per_blcok);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      model.addAttribute("count", count);

      model.addAttribute("num", num);
      if (bindingResult.hasErrors()) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        rttr.addFlashAttribute("nameErrors", bindingResult.getFieldErrors("name"));
        rttr.addFlashAttribute("namesubErrors", bindingResult.getFieldErrors("namesub"));
        rttr.addFlashAttribute("seqnoErrors", bindingResult.getFieldErrors("seqno"));
        return "redirect:/cate/search?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;

      }


      int cnt = this.cateProc.create(cateVO);
      model.addAttribute("cnt", cnt);
      System.out.println("-> cnt: " + cnt);

      if (cnt == 1) {
        rttr.addFlashAttribute("create", "중분류: " + cateVO.getName() + "" + " <br>  소분류: " + cateVO.getNamesub() + " <br>" + " 생성 완료");
//      model.addAttribute("code", "create_success");
//      model.addAttribute("name", cateVO.getName());
//      model.addAttribute("namesub", cateVO.getNamesub());
        return "redirect:/cate/search?word=" + Tool.encode(word) + "&type=" + Tool.encode(type) + "&now_page=" + now_page;

      } else {
        model.addAttribute("code", "create_fail");
        return "cate/msg"; // /templates/cate/msg.html
      }

    } else {
      return "redirect:/member/login";
    }

  }


}
