package dev.mvc.member;

import dev.mvc.tool.Security;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Service("dev.mvc.member.MemberProc")
public class MemberProC implements MemberProInter {

  @Autowired
  private MemberDAOInter memberDAO;

  @Autowired
  private Security security;

  public MemberProC() {
//    System.out.println("memberProc created");
  }

  @Override
  public int checkID(String id) {
    int cnt = this.memberDAO.checkID(id);
    return cnt;
  }

  @Override
  public int create(MemberVO memberVO) {

    memberVO.setPasswd(this.security.aesEncode(memberVO.getPasswd()));
    int cnt = this.memberDAO.create(memberVO);
    return cnt;
  }

  @Override
  public ArrayList<MemberVO> list() {
    ArrayList<MemberVO> list = this.memberDAO.list();
    return list;
  }

  @Override
  public MemberVO read(int memberno) {
    MemberVO memberVO = this.memberDAO.read(memberno);
    return memberVO;
  }

  @Override
  public MemberVO readById(String id) {
    MemberVO memberVO = this.memberDAO.readById(id);
    return memberVO;
  }


  @Override
  public boolean isMember(HttpSession session) {
    boolean sw = false; // 로그인하지 않은 것으로 초기화
    String grade = (String) session.getAttribute("grade");
//    System.out.println(grade);

    if (grade != null) {
      if (grade.equals("admin") || grade.equals("member")) {
        sw = true;  // 로그인 한 경우
      }
    }
    return sw;
  }

  @Override
  public boolean isMemberAdmin(HttpSession session) {
    boolean sw = false; // 로그인하지 않은 것으로 초기화
    String grade = (String) session.getAttribute("grade");
    if (grade != null) {
      if (grade.equals("admin")) {
        sw = true;  // 로그인 한 경우
      }
    }
    return sw;
  }

  @Override
  public int update(MemberVO memberVO) {
    int cnt = this.memberDAO.update(memberVO);
    return cnt;
  }

  @Override
  public int delete(int memberno) {
    int cnt = this.memberDAO.delete(memberno);
    return cnt;
  }

  @Override
  public int passwd_check(HashMap<String, Object> map) {
    int cnt = this.memberDAO.passwd_check(map);
    return cnt;
  }

  @Override
  public int passwd_update(HashMap<String, Object> map) {
    int cnt = this.memberDAO.passwd_update(map);
    return cnt;
  }

  @Override
  public int login(HashMap<String, Object> map) {
    int cnt = this.memberDAO.login(map);
    return cnt;
  }

  @Override
  public int update_grade(HashMap<String, Object> map) {
    return this.memberDAO.update_grade(map);
  }


}
