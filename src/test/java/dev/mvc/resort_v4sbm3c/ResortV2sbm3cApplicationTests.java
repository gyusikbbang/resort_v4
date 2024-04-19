package dev.mvc.resort_v4sbm3c;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import dev.mvc.cate.CateProInter;

@SpringBootTest
class ResortV2sbm3cApplicationTests {


  @Qualifier("dev.mvc.cate.CateProc")
  @Autowired
  private CateProInter cateProc;


//	@Test
//	void contextLoads() {
//	}
//
//	@Test
//	public void testCreate() {
//		CateVO cateVO = new CateVO();
//		cateVO.setName("개발");
//		cateVO.setNamesub("-");
//		int cnt = this.cateDAO.create(cateVO);
//		System.out.println("-> cnt: "+ cnt);
//	}
//
//  @Test
//  public void testCreate() {
//    CateVO cateVO = new CateVO();
//    cateVO.setName("개발");
//    cateVO.setNamesub("-");
//    int cnt = this.cateProc.create(cateVO);
//    System.out.println("-> cnt: " + cnt);
//  }

  @Test
  public void listall() {


  }


//	@Test
//	public void read() {
//
//		int cateno =9;
//		CateVO cateVO  = this.cateProc.read(cateno);
//		System.out.println(cateVO.getName());
//
//
//	}


//	@Test
//	public void update() {
//
//		int cateno =9;
//		CateVO cateVO  = this.cateProc.read(cateno);
//		System.out.println(cateVO.getName());
//
//
//	}



  @Test

  public  void countTest() {


    int count = this.cateProc.list_search_count("개발","100");
    System.out.println(count);
  }
}
