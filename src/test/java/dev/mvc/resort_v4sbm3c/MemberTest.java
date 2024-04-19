package dev.mvc.resort_v4sbm3c;


import dev.mvc.member.MemberProInter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MemberTest {


  @Qualifier("dev.mvc.member.MemberProc")
  @Autowired
  private MemberProInter memberProC;

   @Test
  public  void checkId() {
     int checkId = this.memberProC.checkID("team1");

     if (checkId == 1 )  {
       System.out.println("중복아이디 입니다");

     } else {
       System.out.println("회원 가입 가능");
     }
   }
}
