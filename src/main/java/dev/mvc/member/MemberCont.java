package dev.mvc.member;

import dev.mvc.tool.Security;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberCont {


  @Autowired
  @Qualifier("dev.mvc.member.MemberProc")
  private MemberProInter memberProc;


  @Autowired
  private Security security;

  public MemberCont() {
//    System.out.println("MemberCont created");
  }


  @PostMapping("/checkID")  //http:localhost:9091/meber/checkId?id=admin
  @ResponseBody
  public String checkId(String id) {
    System.out.println("-> id  " + id);

    int cnt = this.memberProc.checkID(id);

    //{"cnt": cnt}
    return "{\"cnt\":" + cnt + "}";
  }


  @GetMapping("/create")

  public String createForm(Model model, MemberVO memberVO) {


    return "/member/create";
  }

  @PostMapping("/create")

  public String createmember(Model model, MemberVO memberVO, RedirectAttributes rrtr) {


    int check_ID = this.memberProc.checkID(memberVO.getId());

    if (memberVO.getTel() == null || memberVO.getTel().equals("")) {
      memberVO.setTel("010-0000-0000");
    }




    if (check_ID == 0) {
      memberVO.setGrade(15);
      int count = memberProc.create(memberVO);
      if (count == 1) {



        rrtr.addFlashAttribute("success", 1);
        rrtr.addFlashAttribute("come", memberVO.getMname() + "님 회원가입 축하드립니다! ");
        return "redirect:/member/login";
      } else {
        rrtr.addFlashAttribute("fail", "다시 시도해주세요 ");
        return "redirect:/member/create";
      }
    } else {
      rrtr.addFlashAttribute("fail", "아이디 중복입니다 다시 만들어주세요 ");
      return "redirect:/member/create";
    }

  }

  @GetMapping("/list")
  public String list_member(Model model, MemberVO memberVO, HttpSession session, RedirectAttributes rttr) {

    String id = (String) session.getAttribute("id");

    if (this.memberProc.isMemberAdmin(session)) {
      ArrayList<MemberVO> list = this.memberProc.list();
      model.addAttribute("list", list);
      return "member/memberList";
    } else {
      rttr.addFlashAttribute("Abnormal", "비정상적인 접근입니다 홈으로 돌아갑니다");
      return "redirect:/";
    }


  }

  /**
   * 조히
   *
   * @param model
   * @param memberno 회원 번호
   * @return
   */
  @GetMapping("/read")
  public String read(Model model, @RequestParam(name = "memberno") Integer memberno, HttpSession session,
                     RedirectAttributes rttr) {
    System.out.println(memberno);
    MemberVO read = this.memberProc.read(memberno);

    String id = (String) session.getAttribute("id");



    if (this.memberProc.isMemberAdmin(session)) {
      if (read != null) {

        model.addAttribute("memberVO", read);
        return "member/read";
      } else {
        rttr.addFlashAttribute("Abnormal", "비정상적인 접근입니다 홈으로 돌아갑니다");
        return "redirect:/member/list";
      }
    }else{
        rttr.addFlashAttribute("Abnormal", "비정상적인 접근입니다 홈으로 돌아갑니다");
        return "redirect:/";
      }


    }


    @PostMapping("/update")

    public String updatemember (Model model, MemberVO memberVO, RedirectAttributes rrtr,HttpSession session){

      if (this.memberProc.isMemberAdmin(session)) {
        int check_ID = this.memberProc.checkID(memberVO.getId());

        if (memberVO.getTel() == null || memberVO.getTel().equals("")) {
          memberVO.setTel("010-0000-0000");
        }
        int count = memberProc.update(memberVO);
        System.out.println(check_ID);
        System.out.println(memberVO.getId());
        if (check_ID == 1) {
          if (count == 1) {


            rrtr.addFlashAttribute("success", 1);
            rrtr.addFlashAttribute("come", memberVO.getMname() + "님 수정 완료 되었습니다! ");

            return "redirect:/member/read?memberno=" + memberVO.getMemberno();
          } else {
            rrtr.addFlashAttribute("fail", "다시 시도해주세요 ");


            return "redirect:/member/read?memberno=" + memberVO.getMemberno();
          }
        } else {
          rrtr.addFlashAttribute("fail", "아이디 중복입니다 다시 만들어주세요 ");
          return "redirect:/member/create";
        }
      } else  {
        rrtr.addFlashAttribute("Abnormal", "비정상적인 접근입니다 홈으로 돌아갑니다");
        return "redirect:/member/list";
      }


    }

    @PostMapping("/delete")
    public String delete (Model model, @RequestParam("memberno") Integer memberno,
      @RequestParam("mname") String mname,HttpSession session,
      RedirectAttributes rttr){
      if (this.memberProc.isMemberAdmin(session)) {
        int count = this.memberProc.delete(memberno);


        if (count == 1) {
          rttr.addFlashAttribute("delete", mname + "'이 삭제되었습니다.");
          return "redirect:/member/list";
        } else {
          rttr.addFlashAttribute("delete", "삭제 실패");
          return "redirect:/cate/search";
        }
      }else {
        return "redirect:/member/login";
      }
    }

    /**
     * 로그인 폼
     *
     * @param model
     * @param memberVO
     * @param session
     * @param request
     * @return
     */

    @GetMapping("/login")

    public String loginForm (Model model, MemberVO memberVO, HttpSession session, HttpServletRequest request){

      // Cookie 관련 ----------------------------------------------
      Cookie[] cookies = request.getCookies();
      Cookie cookie = null;

      String ck_id = ""; // id 저장
      String ck_id_save = ""; // id 저장 여부를 체크
      String ck_passwd = ""; // passwd 저장
      String ck_passwd_save = ""; // passwd 저장 여부를 체크

      if (cookies != null) { // 쿠키가 존재한다면
        for (int i = 0; i < cookies.length; i++) {
          cookie = cookies[i]; // 쿠키 객체 추출

          if (cookie.getName().equals("ck_id")) {
            ck_id = cookie.getValue();
          } else if (cookie.getName().equals("ck_id_save")) {
            ck_id_save = cookie.getValue();  // Y, N
          } else if (cookie.getName().equals("ck_passwd")) {
            ck_passwd = cookie.getValue();         // 1234
          } else if (cookie.getName().equals("ck_passwd_save")) {
            ck_passwd_save = cookie.getValue();  // Y, N
          }
        }
      }
      // Cookie 관련 ----------------------------------------------


      //    <input type='text' class="form-control" name='id' id='id'
      //            value='${ck_id }' required="required"
      //            style='width: 30%;' placeholder="아이디" autofocus="autofocus">
      model.addAttribute("ck_id", ck_id);

      //    <input type='checkbox' name='id_save' value='Y'
      //            ${ck_id_save == 'Y' ? "checked='checked'" : "" }> 저장
      model.addAttribute("ck_id_save", ck_id_save);

      model.addAttribute("ck_passwd", ck_passwd);
      model.addAttribute("ck_passwd_save", ck_passwd_save);


      String id = (String) session.getAttribute("id");

      if (id == null) {
        return "/member/login_cookie";
      } else {
        return "redirect:/";
      }

      //-------------------------------------------------------------------
      // 쿠키 코드 종료
      //-------------------------------------------------------------------

    }

    /**
     * 쿠키 기반 로그인 처리
     *
     * @param model
     * @param id          아이디
     * @param passwd      패스워드
     * @param id_save     아이디 저장여부
     * @param passwd_save 패스워드 저장여부
     * @param session     세션
     * @param response    응답
     * @param request     요청
     * @param rttr        리아랙트
     * @return
     */
    @PostMapping("/login")

    public String login (Model model,
      String id, String passwd,
      @RequestParam(value = "id_save", defaultValue = "") String id_save,
      @RequestParam(value = "passwd_save", defaultValue = "") String passwd_save,
      HttpSession session,
      HttpServletResponse response,
      HttpServletRequest request,
      RedirectAttributes rttr
  ){


      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("id", id);
      map.put("passwd", this.security.aesEncode(passwd));

      int cnt = this.memberProc.login(map);
      System.out.println(cnt);
      if (cnt == 1) {
        MemberVO memberVO = this.memberProc.readById(id);
        // id를 이용하여 회원 정보 조회
        session.setAttribute("memberno", memberVO.getMemberno());
        session.setAttribute("id", memberVO.getId());
        session.setAttribute("mname", memberVO.getMname());
        // session.setAttribute("grade", memverVO.getGrade());

        if (memberVO.getGrade() >= 1 && memberVO.getGrade() <= 10) {
          session.setAttribute("grade", "admin");
        } else if (memberVO.getGrade() >= 11 && memberVO.getGrade() <= 20) {
          session.setAttribute("grade", "member");
        } else if (memberVO.getGrade() >= 21) {
          session.setAttribute("grade", "guest");
        }
        rttr.addFlashAttribute("login", memberVO.getMname() + "님 안녕하세요");


        // -------------------------------------------------------------------
        // id 관련 쿠기 저장
        // -------------------------------------------------------------------
        if (id_save.equals("Y")) { // id를 저장할 경우, Checkbox를 체크한 경우
          Cookie ck_id = new Cookie("ck_id", id);
          ck_id.setPath("/");  // root 폴더에 쿠키를 기록함으로 모든 경로에서 쿠기 접근 가능
          ck_id.setMaxAge(60 * 60 * 24 * 30); // 30 day, 초단위
          response.addCookie(ck_id); // id 저장
        } else { // N, id를 저장하지 않는 경우, Checkbox를 체크 해제한 경우
          Cookie ck_id = new Cookie("ck_id", "");
          ck_id.setPath("/");
          ck_id.setMaxAge(0);
          response.addCookie(ck_id); // id 저장
        }

        // id를 저장할지 선택하는  CheckBox 체크 여부
        Cookie ck_id_save = new Cookie("ck_id_save", id_save);
        ck_id_save.setPath("/");
        ck_id_save.setMaxAge(60 * 60 * 24 * 30); // 30 day
        response.addCookie(ck_id_save);
        // -------------------------------------------------------------------

        // -------------------------------------------------------------------
        // Password 관련 쿠기 저장
        // -------------------------------------------------------------------
        if (passwd_save.equals("Y")) { // 패스워드 저장할 경우
          Cookie ck_passwd = new Cookie("ck_passwd", passwd);
          ck_passwd.setPath("/");
          ck_passwd.setMaxAge(60 * 60 * 24 * 30); // 30 day
          response.addCookie(ck_passwd);
        } else { // N, 패스워드를 저장하지 않을 경우
          Cookie ck_passwd = new Cookie("ck_passwd", "");
          ck_passwd.setPath("/");
          ck_passwd.setMaxAge(0);
          response.addCookie(ck_passwd);
        }
        // passwd를 저장할지 선택하는  CheckBox 체크 여부
        Cookie ck_passwd_save = new Cookie("ck_passwd_save", passwd_save);
        ck_passwd_save.setPath("/");
        ck_passwd_save.setMaxAge(60 * 60 * 24 * 30); // 30 day
        response.addCookie(ck_passwd_save);
        // -------------------------------------------------------------------

        return "redirect:/";


      } else {
        rttr.addFlashAttribute("error", "아이디 또는 비밀번호 오류입니다.");
        return "redirect:/member/login";
      }


    }


    @GetMapping("/update_password")
    public String update_password (Model model, HttpSession session, RedirectAttributes rttr){
      Integer memberno = (Integer) session.getAttribute("memberno");

      if (memberProc.isMember(session)) {
        MemberVO read = this.memberProc.read(memberno);
        if (read == null) {
          return "redirect:/member/list";
        } else {
          model.addAttribute("memberVO", read);
          return "member/update_password";
        }
      } else {
        rttr.addFlashAttribute("Abnormal", "로그인이 필요합니다");
        return "redirect:/member/login";
      }


    }

    @PostMapping("/checkpassword")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPassword (@RequestBody String json_src, HttpSession session){
      HashMap<String, Object> map = new HashMap<>();
      JSONObject src = new JSONObject(json_src);
      String pastpasswd = src.getString("pastpasswd");
      Integer memberno = (Integer) session.getAttribute("memberno");

      map.put("passwd", this.security.aesEncode(pastpasswd));
      map.put("memberno", memberno);

      int check = this.memberProc.passwd_check(map);

      Map<String, Object> result = new HashMap<>();
      result.put("check", check);

      return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // 여기서 비밀번호 일치 여부를 확인하고 결과를 반환합니다.


    @PostMapping("/update_password")
    public String updatepass (Model model, MemberVO memberVO,
      String pastpasswd,
      String passwd1,
      String passwd2,
      RedirectAttributes rrtr,
      HttpSession session
  ){
      if (this.memberProc.isMember(session)) {
        HashMap<String, Object> mapcheck = new HashMap<String, Object>();


        mapcheck.put("passwd", this.security.aesEncode(pastpasswd));
        mapcheck.put("memberno", memberVO.getMemberno());


        int check_pass = this.memberProc.passwd_check(mapcheck);


        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("passwd", this.security.aesEncode(passwd2));
        map.put("memberno", memberVO.getMemberno());
        int count = memberProc.passwd_update(map);


        if (check_pass == 1 && passwd1.equals(passwd2) &&
          !pastpasswd.equals(passwd1) && !pastpasswd.equals(passwd2) &&
          passwd1.trim().length() > 3 && passwd2.trim().length() > 3) {
          if (count == 1) {


            String mname = (String) session.getAttribute("mname");
            rrtr.addFlashAttribute("success", 1);
            rrtr.addFlashAttribute("update", mname + "님 패스워드 수정이 완료되었습니다 ");
            session.invalidate();
            return "redirect:/";
          } else {
            rrtr.addFlashAttribute("fail", "다시 시도해주세요 ");
            return "redirect:/member/update_password";
          }
        } else {
          rrtr.addFlashAttribute("fail", "알수없는 오류 입니다. ");
          return "redirect:/member/update_password";
        }
      } else {
        rrtr.addFlashAttribute("fail", "로그인이 필요합니다. ");
        return "redirect:/member/login";

      }

    }

    @GetMapping("/my_page")
    public String mypage (Model model, HttpSession session, RedirectAttributes rttr){


      if (this.memberProc.isMember(session)) {
        String id = (String) session.getAttribute("id");
        MemberVO memberVO = this.memberProc.readById(id);

        model.addAttribute("memberVO", memberVO);

        return "/member/my_page";

      } else {
        rttr.addFlashAttribute("Abnormal", "비정상적인 접근입니다 홈으로 돌아갑니다");
        return "redirect:/";
      }
    }

    @GetMapping("/logout")
    public String logout (HttpSession session, RedirectAttributes redirectAttributes){
      // 세션을 무효화합니다.
      session.invalidate();

      // 로그아웃 성공 메시지(선택 사항)
      redirectAttributes.addFlashAttribute("logout", "로그아웃 되었습니다.");

      // 홈 페이지로 리다이렉트합니다.
      return "redirect:/";

    }


    @GetMapping("/update_grade")
    public String update_gradeForm (Model model, Integer memberno, HttpSession session, RedirectAttributes rttr){

      if (memberno == null) {
        rttr.addFlashAttribute("Abnormal", "비정상적인 접근입니다 홈으로 돌아갑니다");
        return "redirect:/";
      }

      MemberVO read = this.memberProc.read(memberno);
      if (read == null) {
        return "redirect:/member/list";
      } else {
        model.addAttribute("memberVO", read);
        return "member/update_grade";
      }

    }

    @PostMapping("update_grade")
    public String updategrade (Model model, MemberVO memberVO,
      Integer grade, Integer memberno,

      RedirectAttributes rrtr,
      HttpSession session
  ){


      HashMap<String, Object> map = new HashMap<String, Object>();


      map.put("grade", grade);
      map.put("memberno", memberno);
      int count = this.memberProc.update_grade(map);


      if (count == 1) {


        rrtr.addFlashAttribute("success", 1);
        rrtr.addFlashAttribute("update", "등급  수정이 완료되었습니다 ");

        return "redirect:/member/list";
      } else {
        rrtr.addFlashAttribute("fail", "다시 시도해주세요 ");
        return "redirect:/member/update_grade";
      }

    }
  }

//-------------------------------------------------------------------
// 코드 시작
//-------------------------------------------------------------------

