package dev.mvc.contents;


import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.*;


import dev.mvc.cate.CateVO;
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
  public int record_per_page = 4;
  public int page_per_blcok = 10;

  public ContentsCont() {
//    System.out.println("-> ContentsCont created.");
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
  public String create(Model model, CateVO cateVO, ContentsVO contentsVO, @RequestParam(defaultValue = "0") Integer cateno, @RequestParam(value = "name", defaultValue = "") String name) {


    Optional<CateVO> read = this.cateProc.read(cateno);
    if (read.isPresent()) {


      model.addAttribute("cateVO", read.get());
      model.addAttribute("cateno", cateno);

      return "contents/create";

    } else {

      ArrayList<CateVO> list = this.cateProc.list_namesub();

      model.addAttribute("catelist", list);

      model.addAttribute("cateno", cateno);

      return "contents/it ";

    }


  }

  @GetMapping("/getSubCategories")
  @ResponseBody // 이 어노테이션을 추가하여 메서드가 JSON 응답을 반환하도록 함
  public ArrayList<CateVO> getSubCategories(@RequestParam("name") String name) {
    return cateProc.list_namesub_y(name);
  }

  @GetMapping("/getSubcate")
  @ResponseBody // 이 어노테이션을 추가하여 메서드가 JSON 응답을 반환하도록 함
  public ArrayList<CateVO> getsubcate() {
    return cateProc.list_all_name_y();
  }

  /**
   * 등록 처리 http://localhost:9091/contents/create.do
   *
   * @return
   */
  @PostMapping(value = "/create")
  public String create(Model model, HttpServletRequest request,
                       HttpSession session, @RequestParam(defaultValue = "0") int cateno, @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                       @RequestParam(name = "word", defaultValue = "") String word,
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

      // 전송 파일이 없어도 file1MF 객체가 생성됨.
      // <input type='file' class="form-control" name='file1MF' id='file1MF'
      //           value='' placeholder="파일 선택">
      MultipartFile mf = contentsVO.getFile1MF();

      file1 = mf.getOriginalFilename(); // 원본 파일명 산출, 01.jpg


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
          return "redirect:/contents/search?nowpage="+now_page+"&contentsno="+contentsVO.getContentsno()+"&cateno=" + cateno;


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

        return "redirect:/contents/search?nowpage="+now_page+"&contentsno="+contentsVO.getContentsno()+"&cateno=" + cateno;
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
      return "/contents/list_by_cateno";
    } else {
      return "redirect:/member/login";

    }


  }

  /**
   * 카테고리별 목록
   * http://localhost:9091/contents/list_by_cateno?cateno=5
   * http://localhost:9091/contents/list_by_cateno?cateno=6
   *
   * @return
   */
  @GetMapping(value = "/list_by_cateno")
  public String list_by_cateno(HttpSession session, Model model, @RequestParam(defaultValue = "0") Integer cateno) {
    ArrayList<CateVOMenu> menu = this.cateProc.menu();
    model.addAttribute("menu", menu);

    if (cateno == null || cateno == 0) {
      if (this.memberProC.isMemberAdmin(session)) {

        ArrayList<ContentsVO> list = this.contentsProc.list_all();
        model.addAttribute("cateVO", new CateVO()); // NullPointer 방지용 빈 객체 추가
        model.addAttribute("list", list);
        model.addAttribute("cateno", cateno);
        return "/contents/list_by_cateno";
      } else {
        return "redirect:/member/login";
      }
    } else {
      Optional<CateVO> read = this.cateProc.read(cateno);
      if (read.isPresent()) {
        model.addAttribute("cateVO", read.get());
        ArrayList<ContentsVO> list = this.contentsProc.list_by_cateno(cateno);
        model.addAttribute("list", list);
        model.addAttribute("cateno", cateno);
        return "contents/list_by_cateno";
      } else {
        model.addAttribute("cateno", cateno);
        return "redirect:/contents/list_all";
      }
    }
  }

  /**
   * 조회
   * http://localhost:9091/contents/read.do?contentsno=17
   *
   * @return
   */
  @GetMapping("/read")
  public String read(Model model, int contentsno, @RequestParam Integer cateno, @RequestParam(name = "now_page", defaultValue = "1") int now_page, @RequestParam(name = "word", defaultValue = "") String word) { // int cateno = (int)request.getParameter("cateno");


    ContentsVO contentsVO = this.contentsProc.read(contentsno);
    if (contentsVO == null) {
      return "redirect:/contents/list_by_cateno";
    } else {
      String title = contentsVO.getTitle();
      String content = contentsVO.getContent();


      long size1 = contentsVO.getSize1();
      String size1_label = Tool.unit(size1);
      contentsVO.setSize1_label(size1_label);

      model.addAttribute("contentsVO", contentsVO);

      Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());
      if (read.isPresent()) {
        model.addAttribute("cateVO", read.get());
        model.addAttribute("cateno", cateno);
        model.addAttribute("word", word);
        model.addAttribute("now_page", now_page);

        // 조회에서 화면 하단에 출력
        // ArrayList<ReplyVO> reply_list = this.replyProc.list_contents(contentsno);
        // mav.addObject("reply_list", reply_list);

        return "/contents/read";
      } else {
        return "redirect:/contents/search";
      }

    }
  }

  /**
   * 맵 등록/수정/삭제 폼
   * http://localhost:9091/contents/map?contentsno=1
   *
   * @return
   */
  @GetMapping("/map")
  public String map(int contentsno, Model model,@RequestParam(name = "word", defaultValue = "") String word,@RequestParam(name = "now_page", defaultValue = "1") int now_page,
                    @RequestParam(defaultValue = "0") Integer cateno) {


    ContentsVO contentsVO = this.contentsProc.read(contentsno); // map 정보 읽어 오기
    model.addAttribute("contentsVO", contentsVO); // request.setAttribute("contentsVO", contentsVO);

    Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());// 그룹 정보 읽기

    if (read.isPresent()) {
      model.addAttribute("cateVO", read.get());
      model.addAttribute("now_page",now_page);
      model.addAttribute("cateno",cateno);
      model.addAttribute("word",word);
      return "contents/map";
    } else {
      return "redirect:/contents/search";
    }

  }

  /**
   * MAP 등록/수정/삭제 처리
   * http://localhost:9091/contents/map.do
   *
   * @param //ontentsVO
   * @return
   */
  @PostMapping(value = "/map")
  public String map_update(int contentsno, String map, Model model) {


    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("contentsno", contentsno);
    hashMap.put("map", map);


    int count = this.contentsProc.map(hashMap);

    if (count == 1) {
      model.addAttribute("contentsVO", count);
      return "redirect:/contents/read?contentsno=" + contentsno;
    } else {
      return "redirect:/contents/map?contentsno" + contentsno;
    }


    // /webapp/WEB-INF/views/contents/read.jsp


  }

  /**
   * Youtube 등록/수정/삭제 처리
   * http://localhost:9091/contents/youtube
   *
   * @param
   * @return
   */

  @GetMapping("/youtube")
  public String youtube(int contentsno, Model model, String youtube,@RequestParam(name = "word", defaultValue = "") String word, CateVO cateVO,
                        @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                        @RequestParam(defaultValue = "0") Integer cateno, RedirectAttributes ra ) {


    ContentsVO contentsVO = this.contentsProc.read(contentsno); // map 정보 읽어 오기
    model.addAttribute("contentsVO", contentsVO); // request.setAttribute("contentsVO", contentsVO);

    Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());// 그룹 정보 읽기

    if (read.isPresent()) {
      model.addAttribute("cateVO", read.get());
      model.addAttribute("now_page" ,now_page);
      model.addAttribute("word" ,word);
      model.addAttribute("cateno" ,cateno);
      return "contents/youtube";
    } else {
      return "redirect:/contents/search";
    }

  }
  @PostMapping(value = "/youtube")
  public String youtube_update(Model model, int contentsno, String youtube,@RequestParam(name = "word", defaultValue = "") String search_word, CateVO cateVO,
                               @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                               @RequestParam(defaultValue = "0") Integer cateno, RedirectAttributes ra) {


    if (youtube.trim().length() > 0) {  // 삭제 중인지 확인, 삭제가 아니면 youtube 크기 변경
      youtube = Tool.youtubeResize(youtube, 640);  // youtube 영상의 크기를 width 기준 640 px로 변경
    }

    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("contentsno", contentsno);
    hashMap.put("youtube", youtube);

    int cnt = this.contentsProc.youtube(hashMap);
    ra.addFlashAttribute("now_page" ,now_page);
    ra.addFlashAttribute("word" ,search_word);
    ra.addFlashAttribute("cateno" ,cateno);
    if (cnt != 0 && !youtube.isEmpty()) {

      return "redirect:/contents/read?contentsno=" + contentsno + "&now_page=" + now_page +"&word="+Tool.encode(search_word)+ "&cateno=" + cateno;
    } else if (cnt != 0 && youtube.isEmpty()) {
      return "redirect:/contents/youtube?contentsno=" + contentsno + "&now_page=" + now_page +"&word="+Tool.encode(search_word)+ "&cateno=" + cateno;

    } else {
      return "redirect:/contents/youtube?contentsno=" + contentsno + "&now_page=" + now_page +"&word="+Tool.encode(search_word)+ "&cateno=" + cateno;
    }

  }


  @GetMapping("/search")
  public String search_paging(HttpSession session, Model
    model, @RequestParam(name = "word", defaultValue = "") String word, CateVO cateVO,
                              @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                              @RequestParam(defaultValue = "0") Integer cateno, ContentsVO contentsVO) {


    model.addAttribute("cateno", cateno);


    HashMap<String, Object> map = new HashMap<>();
    if (memberProC.isMemberAdmin(session)) {
      if (now_page < 1) {
        now_page = 1;
      }


      word = Tool.wordcheckNull(word);


      if (cateno != 0) {
        Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());
        model.addAttribute("cateVO", read.get());
      }

      map.put("cateno", cateno);
      map.put("word", word);

      int count = this.contentsProc.search_count(map);
      // 일련 번호 생성
      int num = count - ((now_page - 1) * this.record_per_page);

      ArrayList<ContentsVO> contentslist = this.contentsProc.list_by_cateno_search_paging(contentsVO);
      String paging = this.contentsProc.pagingBox(cateno, now_page, word, "/contents/search", count, Contents.RECORD_PER_PAGE, Contents.RECORD_PER_PAGE);
      model.addAttribute("paging", paging);

      model.addAttribute("now_page", now_page);
      model.addAttribute("count", count);

      model.addAttribute("num", num);
      if (count == 0 && word.equals("")) {
        model.addAttribute("word", word);
        model.addAttribute("add","리스트가 없습니다");
      } else if (count == 0 && !word.equals("")) {
        model.addAttribute("nulllist", "결과가 없습니다.");
      } else if (count != 0 && !word.equals("")) {
        model.addAttribute("word", word);
        model.addAttribute("search", word + "에 대한 검색 결과 : 총 " + count + "건");
      }

      model.addAttribute("word", word);
      model.addAttribute("searchlist", contentslist);




    }
    return "contents/search_all"; // Assuming "search_all" is the name of the view to display the search results
  }
  @GetMapping("/search_grid")
  public String search_paging_grid(HttpSession session, Model
    model, String word, CateVO cateVO,
                                   @RequestParam(name = "now_page", defaultValue = "1") int now_page,
                                   @RequestParam(defaultValue = "0") Integer cateno, ContentsVO contentsVO) {


    model.addAttribute("cateno", cateno);


    HashMap<String, Object> map = new HashMap<>();
    if (memberProC.isMemberAdmin(session)) {
      if (now_page < 1) {
        now_page = 1;
      }


      word = Tool.wordcheckNull(word);

      if (cateno != 0) {
        Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());
        model.addAttribute("cateVO", read.get());
      }

      map.put("cateno", cateno);
      map.put("word", word);

      int count = this.contentsProc.search_count(map);
      // 일련 번호 생성
      int num = count - ((now_page - 1) * this.record_per_page);

      ArrayList<ContentsVO> catelist = this.contentsProc.list_by_cateno_search_paging(contentsVO);
      String paging = this.contentsProc.pagingBox(cateno, now_page, word, "/contents/search_grid", count, Contents.RECORD_PER_PAGE, Contents.RECORD_PER_PAGE);
      model.addAttribute("paging", paging);

      model.addAttribute("now_page", now_page);
      model.addAttribute("count", count);

      model.addAttribute("num", num);
      if (catelist.isEmpty()) {
        model.addAttribute("word", word);

        model.addAttribute("nulllist", "결과가 없습니다.");
      } else if (!catelist.isEmpty() && !word.equals("")) {
        model.addAttribute("word", word);

        model.addAttribute("search", word + "에  대한 검색 결과 : 총 " + count + "건");
      }

      model.addAttribute("word", word);

      model.addAttribute("searchlist", catelist);


      return "contents/search_all_grid"; // Assuming "search_result" is the name of the view to display the search results
    } else {
      return "redirect:/";


    }


  }

  //* 수정 폼
//   * http://localhost:9091/contents/update_text.do?contentsno=1
//    *
//    * @return
//    */
  @GetMapping(value = "/update_text")
  public String update_text(HttpSession session, int contentsno, Model model,RedirectAttributes ra,@RequestParam(name = "word", defaultValue = "") String word,@RequestParam(name = "now_page", defaultValue = "1") int now_page,
                            @RequestParam(defaultValue = "0") Integer cateno ) {


    if (this.memberProC.isMemberAdmin(session)) { // 관리자로 로그인한경우
      ContentsVO contentsVO = this.contentsProc.read(contentsno);


      if (contentsVO != null) {
        model.addAttribute("contentsVO", contentsVO);

        model.addAttribute("word", word);
        model.addAttribute("now_page", now_page);
        model.addAttribute("cateno", cateno);
        Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());
        model.addAttribute("cateVO", read.get());


        // String content = "장소:\n인원:\n준비물:\n비용:\n기타:\n";

        // mav.addObject("content", content);
        return "/contents/update_text"; // /WEB-INF/views/contents/update_text.jsp
      } else {
        return "redirect:/contents/"; // /WEB-INF/views/contents/update_text.jsp
      }




    } else {
      return "redirect:/member/login";
    }
  }


  /**
   * 수정 처리
   * http://localhost:9091/contents/update_text.do?contentsno=1
   *
   * @return
   */
  @PostMapping(value = "/update_text")
  public String  update_text(HttpSession session, ContentsVO contentsVO,RedirectAttributes ra,int contentsno, Model model,@RequestParam(name = "search_word", defaultValue = "") String search_word,@RequestParam(name = "now_page", defaultValue = "1") int now_page,
                             @RequestParam(defaultValue = "0") Integer cateno) {

    // System.out.println("-> word: " + contentsVO.getWord());

    if (this.memberProC.isMemberAdmin(session)) { // 관리자 로그인 확인
      HashMap<String, Object> hashMap = new HashMap<String, Object>();
      hashMap.put("contentsno", contentsVO.getContentsno());
      hashMap.put("passwd", contentsVO.getPasswd());


      if (this.contentsProc.password_check(hashMap) == 1) { // 패스워드 일치
        int count = this.contentsProc.update_text(contentsVO);// 글수정


        // mav 객체 이용
        ra.addFlashAttribute("contentsno", contentsVO.getContentsno());
        ra.addFlashAttribute("cateno", contentsVO.getCateno());
        System.out.println("count->" +count);
        if (count != 0) {
          ra.addFlashAttribute("now_page", contentsVO.getNow_page()); // now_page를 Flash Attribute로 저장
          ra.addFlashAttribute("cnt", "1");
          ra.addFlashAttribute("word",search_word);
// URL에 파라미터의 전송
          return "redirect:/contents/read?contentsno=" + contentsVO.getContentsno() + "&now_page=" + now_page +"&word="+Tool.encode(search_word)+ "&cateno=" + contentsVO.getCateno();
        } else {

          ra.addFlashAttribute("cnt", "-1");
          return "redirect:/contents/msg";  // POST -> GET -> JSP 출력
        }


      } else { // 패스워드 불일치
        ra.addFlashAttribute("code", "passwd_fail");
        ra.addFlashAttribute("cnt", "0");
        ra.addFlashAttribute("url", "/contents/msg"); // msg.jsp, redirect parameter 적용
        return "redirect:/contents/msg";  // POST -> GET -> JSP 출력
      }
    } else { // 정상적인 로그인이 아닌 경우 로그인 유도
      ra.addFlashAttribute("url", "/admin/login_need"); // /WEB-INF/views/admin/login_need.jsp
      return "redirect:/contents/msg";
    }

  }
  /**
   * 파일 수정 폼
   * http://localhost:9091/contents/update_file.do?contentsno=1
   *
   * @return
   */
  @GetMapping(value = "/update_file")
  public String  update_file(HttpSession session,int contentsno,Model model,@RequestParam(name = "word", defaultValue = "") String word,@RequestParam(name = "now_page", defaultValue = "1") int now_page,
                             @RequestParam(defaultValue = "0") Integer cateno) {

    if (memberProC.isMemberAdmin(session)) {
      ContentsVO contentsVO = this.contentsProc.read(contentsno);
      Optional<CateVO> read = this.cateProc.read(contentsVO.getCateno());
      if (read.isPresent()) {
        model.addAttribute("now_page" ,now_page);
        model.addAttribute("word" ,word);
        model.addAttribute("cateno" ,cateno);
        model.addAttribute("cateVO", read.get());
        model.addAttribute("contentsVO",contentsVO);
        return "contents/update_file";

      } else {
        return  "redirect:/";
      }

    } else {
      return "redirect:/member/login";
    }



  }

  /**
   * 파일 수정 처리 http://localhost:9091/contents/update_file.do
   *
   * @return
   */
  @PostMapping(value = "/update_file")
  public String update_file(HttpSession session, ContentsVO contentsVO,int contentsno,Model model,@RequestParam(name = "word", defaultValue = "") String word,@RequestParam(name = "now_page", defaultValue = "1") int now_page,
                            @RequestParam(defaultValue = "0") Integer cateno,RedirectAttributes ra) {


    System.out.println("cccc -> update_file");

    if (this.memberProC.isMemberAdmin(session)) {
      // 삭제할 파일 정보를 읽어옴, 기존에 등록된 레코드 저장용
      ContentsVO contentsVO_old = contentsProc.read(contentsVO.getContentsno());

      // -------------------------------------------------------------------
      // 파일 삭제 시작
      // -------------------------------------------------------------------
      String file1saved = contentsVO_old.getFile1saved();  // 실제 저장된 파일명
      String thumb1 = contentsVO_old.getThumb1();       // 실제 저장된 preview 이미지 파일명
      long size1 = 0;

      String upDir = Contents.getUploadDir(); // C:/kd/deploy/resort_v2sbm3c/contents/storage/

      Tool.deleteFile(upDir, file1saved);  // 실제 저장된 파일삭제
      Tool.deleteFile(upDir, thumb1);     // preview 이미지 삭제
      // -------------------------------------------------------------------
      // 파일 삭제 종료
      // -------------------------------------------------------------------

      // -------------------------------------------------------------------
      // 파일 전송 시작
      // -------------------------------------------------------------------
      String file1 = "";          // 원본 파일명 image

      // 전송 파일이 없어도 file1MF 객체가 생성됨.
      // <input type='file' class="form-control" name='file1MF' id='file1MF'
      //           value='' placeholder="파일 선택">
      MultipartFile mf = contentsVO.getFile1MF();

      file1 = mf.getOriginalFilename(); // 원본 파일명
      size1 = mf.getSize();  // 파일 크기

      if (size1 > 0) { // 폼에서 새롭게 올리는 파일이 있는지 파일 크기로 체크 ★
        // 파일 저장 후 업로드된 파일명이 리턴됨, spring.jsp, spring_1.jpg...
        file1saved = Upload.saveFileSpring(mf, upDir);

        if (Tool.isImage(file1saved)) { // 이미지인지 검사
          // thumb 이미지 생성후 파일명 리턴됨, width: 250, height: 200
          thumb1 = Tool.preview(upDir, file1saved, 250, 200);
        }

      } else { // 파일이 삭제만 되고 새로 올리지 않는 경우
        file1 = "";
        file1saved = "";
        thumb1 = "";
        size1 = 0;
      }

      contentsVO.setFile1(file1);
      contentsVO.setFile1saved(file1saved);
      contentsVO.setThumb1(thumb1);
      contentsVO.setSize1(size1);
      // -------------------------------------------------------------------
      // 파일 전송 코드 종료
      // -------------------------------------------------------------------

      int count = this.contentsProc.update_file(contentsVO);// Oracle 처리
      model.addAttribute("contentsno", contentsVO.getContentsno());
      model.addAttribute("cateno", contentsVO.getCateno());
      model.addAttribute("now_page", contentsVO.getNow_page());
      model.addAttribute("word",word);
      if (count != 0) {
        ra.addFlashAttribute("contentsno", contentsVO.getContentsno());
        ra.addFlashAttribute("cateno", contentsVO.getCateno());
        ra.addFlashAttribute("now_page", contentsVO.getNow_page());
        ra.addFlashAttribute("word",word);
        ra.addFlashAttribute("cnt","1");
        return "redirect:/contents/read?contentsno="+ contentsVO.getContentsno()+"&now_page="+now_page+"&word="+Tool.encode(word)+"&cateno="+cateno; // request -> param으로 접근 전환

      } else {
        ra.addFlashAttribute("cnt","-1");
        return "redirect:/contents/update_file?contentsno="+ contentsVO.getContentsno()+"&now_page="+now_page+"&word="+Tool.encode(word)+"&cateno="+cateno; // request -> param으로 접근 전환
      }



    } else {
      // login_need.jsp, redirect parameter 적용
      return "redirect:/member/login"; // GET
    }

    // redirect하게되면 전부 데이터가 삭제됨으로 mav 객체에 다시 저장



  }

  @PostMapping(value = "/delete")
  public String delete(ContentsVO contentsVO, HttpSession session, Model model,@RequestParam(name = "word", defaultValue = "") String word,
                       @RequestParam(defaultValue = "0") int cateno,RedirectAttributes ra) {


    // -------------------------------------------------------------------
    // 파일 삭제 시작
    // -------------------------------------------------------------------
    // 삭제할 파일 정보를 읽어옴.
    ContentsVO contentsVO_read = contentsProc.read(contentsVO.getContentsno());
    int now_page = contentsVO.getNow_page();

    HashMap<String, Object> hashMap = new HashMap<String, Object>();
    hashMap.put("cateno", contentsVO.getCateno());
    hashMap.put("word", contentsVO.getWord());

    if (contentsProc.search_count(hashMap) % Contents.RECORD_PER_PAGE == 0) {
      now_page = now_page - 1; // 삭제시 DBMS는 바로 적용되나 크롬은 새로고침등의 필요로 단계가 작동 해야함.
      if (now_page < 1) {
        now_page = 1; // 시작 페이지
      }
    }
    if (this.memberProC.isMemberAdmin(session)) {
      if (contentsVO_read != null) {

        String file1saved = contentsVO.getFile1saved();
        String thumb1 = contentsVO.getThumb1();

        String uploadDir = Contents.getUploadDir();
        Tool.deleteFile(uploadDir, file1saved);  // 실제 저장된 파일삭제
        Tool.deleteFile(uploadDir, thumb1);     // preview 이미지 삭제
        // -------------------------------------------------------------------
        // 파일 삭제 종료
        // -------------------------------------------------------------------

        int cnt = this.contentsProc.delete(contentsVO.getContentsno());// DBMS 삭제


        if (cnt != 0) {

          // -------------------------------------------------------------------------------------

          model.addAttribute("cateno", contentsVO.getCateno());
          model.addAttribute("now_page", now_page);
          ra.addFlashAttribute("cateno", contentsVO.getCateno());
          ra.addFlashAttribute("now_page", now_page);
          ra.addFlashAttribute("del",1);
          return "redirect:/contents/search?now_page"+now_page+"&word"+Tool.encode(word)+"&cateno="+cateno;
        } else {
          model.addAttribute("cateno", contentsVO.getCateno());
          model.addAttribute("now_page", now_page);
          ra.addFlashAttribute("cateno", contentsVO.getCateno());
          ra.addFlashAttribute("now_page", now_page);
          ra.addFlashAttribute("del",-1);
          return "redirect:/contents/search?now_page"+now_page+"&word"+Tool.encode(word)+"&cateno="+cateno;
        }
        // -------------------------------------------------------------------------------------
        // 마지막 페이지의 마지막 레코드 삭제시의 페이지 번호 -1 처리
        // -------------------------------------------------------------------------------------
        // 마지막 페이지의 마지막 10번째 레코드를 삭제후
        // 하나의 페이지가 3개의 레코드로 구성되는 경우 현재 9개의 레코드가 남아 있으면
        // 페이지수를 4 -> 3으로 감소 시켜야함, 마지막 페이지의 마지막 레코드 삭제시 나머지는 0 발생

      } else {
        return "redirect:/contents/search?now_page"+now_page+"&word"+Tool.encode(word)+"&cateno="+cateno;
      }
    } else  {
      return "redirect:/member/login";
    }



  }



}