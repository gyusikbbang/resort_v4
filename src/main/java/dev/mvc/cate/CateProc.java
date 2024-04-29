package dev.mvc.cate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("dev.mvc.cate.CateProc")
public class CateProc implements CateProInter {


  @Autowired
  private CateDAOInter cateDAO;







  public CateProc() {
    System.out.println("CateProc created");
  }

  @Override
  public int create(CateVO cateVO) {

    int cnt = this.cateDAO.create(cateVO);
    return cnt;
  }


  @Override
  public ArrayList<CateVO> list() {


    return this.cateDAO.list();
  }

  @Override
  public Optional<CateVO> read(int cateno) {

    Optional<CateVO> cateVO = this.cateDAO.read(cateno);


    return cateVO;

  }

  @Override
  public int update(CateVO cateVO) {
    return this.cateDAO.update(cateVO);
  }

  @Override
  public int delete(int cateno) {
    return this.cateDAO.delete(cateno);
  }


  @Override
  public int multiple_delete(List<Integer> cateno) {
    return this.cateDAO.multiple_delete(cateno);
  }

  @Override
  public int min(int cateno) {
    return this.cateDAO.min(cateno);
  }


  @Override
  public int max(int cateno) {
    return this.cateDAO.max(cateno);
  }

  @Override
  public int update_forward(int cateno) {
    return this.cateDAO.update_forward(cateno);
  }

  @Override
  public int update_backward(int cateno) {
    return this.cateDAO.update_backward(cateno);
  }

  @Override
  public int show(int cateno) {
    return this.cateDAO.show(cateno);
  }

  @Override
  public int hide(int cateno) {
    return this.cateDAO.hide(cateno);
  }

  @Override
  public ArrayList<CateVO> list_all_name_y() {
    return this.cateDAO.list_all_name_y();
  }

  @Override
  public ArrayList<CateVO> list_namesub_y(String name) {
    return this.cateDAO.list_namesub_y(name);
  }

  @Override
  public ArrayList<CateVO> list_namesub() {
    return this.cateDAO.list_namesub();
  }


  public ArrayList<CateVOMenu> menu() {
    ArrayList<CateVOMenu> menu = new ArrayList<CateVOMenu>();


    // 중분류
    ArrayList<CateVO> list = this.cateDAO.list_all_name_y();


    // 소분류
    for (CateVO cateVO : list) {
      CateVOMenu cateVOMenu = new CateVOMenu();
      cateVOMenu.setName(cateVO.getName());

      ArrayList<CateVO> list_namesub = this.cateDAO.list_namesub_y(cateVO.getName());
      cateVOMenu.setListnamesub(list_namesub);
      if (list_namesub.size() > 0 ){
        menu.add(cateVOMenu);
      }

    }

    return menu;
  }

  /**
   *
   * @param search 검색 + 타입
   * @param now_page + 현재 페이지
   * @param record_per_page 표시될 개수
   *
   * @return
   */


  @Override
  public ArrayList<CateVO> list_search_paging(String word,String type,int now_page,int record_per_page) {

    /*
    예) 페이지당 10개의 레코드 출력
    1 page: WHERE r >= 1 AND r <= 10
    2 page: WHERE r >= 11 AND r <= 20
    3 page: WHERE r >= 21 AND r <= 30

     예) 페이지당 3개의 레코드 출력
    1 page: WHERE r >= 1 AND r <= 3
    2 page: WHERE r >= 4 AND r <= 6
    3 page: WHERE r >= 7 AND r <= 9

    페이지에서 출력할 시작 레코드 번호 계산 기준값, nowPage는 1부터 시작
    1 페이지 시작 rownum: now_page = 1, (1 - 1) * 10 --> 0
    2 페이지 시작 rownum: now_page = 2, (2 - 1) * 10 --> 10
    3 페이지 시작 rownum: now_page = 3, (3 - 1) * 10 --> 20
    */
    int begin_of_page = (now_page - 1) * record_per_page;

    // 시작 rownum 결정
    // 1 페이지 = 0 + 1: 1
    // 2 페이지 = 10 + 1: 11
    // 3 페이지 = 20 + 1: 21
    int start_num = begin_of_page + 1;

    //  종료 rownum
    // 1 페이지 = 0 + 10: 10
    // 2 페이지 = 10 + 10: 20
    // 3 페이지 = 20 + 10: 30
    int end_num = begin_of_page + record_per_page;
    /*
    1 페이지: WHERE r >= 1 AND r <= 10
    2 페이지: WHERE r >= 11 AND r <= 20
    3 페이지: WHERE r >= 21 AND r <= 30
    */

    // System.out.println("begin_of_page: " + begin_of_page);
    // System.out.println("WHERE r >= "+start_num+" AND r <= " + end_num);
    Map<String ,Object> map = new HashMap<String,Object>();

    map.put("word",word);
    map.put("type",type);
    map.put("start_num",start_num);
    map.put("end_num",end_num);
    ArrayList <CateVO> list = this.cateDAO.list_search_paging(map);
    return list;
  }

  @Override
  public int list_search_count(String word, String type) {

    HashMap<String,Object> search = new HashMap<String,Object>();
    search.put("word",word);
    search.put("type",type);

    int count = this.cateDAO.list_search_count(search);
    return count;
  }

  /**
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음]
   *
   *
   * @param now_page  현재 페이지
   * @param word 검색어
   * @param list_file 목록 파일명
   * @param search_count 검색 레코드수
   * @return 페이징 생성 문자열
   */
  /**
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음]
   *
   * @param cateno 카테고리번호
   * @param now_page  현재 페이지
   * @param word 검색어
   * @param list_file 목록 파일명
   * @param search_count 검색 레코드수
   * @return 페이징 생성 문자열
   */
  @Override
  public String pagingBox( int now_page, String word,String type, String list_file, int search_count,int record_per_page,int page_per_block){

    // 전체 페이지 수: (double)1/10 -> 0.1 -> 1 페이지, (double)12/10 -> 1.2 페이지 -> 2 페이지
    int total_page = (int)(Math.ceil((double)search_count / record_per_page ));
    // 전체 그룹  수: (double)1/10 -> 0.1 -> 1 그룹, (double)12/10 -> 1.2 그룹-> 2 그룹
    int total_grp = (int)(Math.ceil((double)total_page / page_per_block));
    // 현재 그룹 번호: (double)13/10 -> 1.3 -> 2 그룹
    int now_grp = (int)(Math.ceil((double)now_page / page_per_block));

    // 1 group: 1, 2, 3 ... 9, 10
    // 2 group: 11, 12 ... 19, 20
    // 3 group: 21, 22 ... 29, 30
    int start_page = ((now_grp - 1) *page_per_block) + 1; // 특정 그룹의 시작  페이지
    int end_page = (now_grp * page_per_block);               // 특정 그룹의 마지막 페이지

    StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름

    // style이 java 파일에 명시되는 경우는 로직에 따라 css가 영향을 많이 받는 경우에 사용하는 방법
    str.append("<nav aria-label='...'>");
    str.append("<ul class='pagination justify-content-center'>");
//      str.append("현재 페이지: " + nowPage + " / " + totalPage + "  ");

    // 이전 10개 페이지로 이동
    // now_grp: 1 (1 ~ 10 page)
    // now_grp: 2 (11 ~ 20 page)
    // now_grp: 3 (21 ~ 30 page)
    // 현재 2그룹일 경우: (2 - 1) * 10 = 1그룹의 마지막 페이지 10
    // 현재 3그룹일 경우: (3 - 1) * 10 = 2그룹의 마지막 페이지 20
    int _now_page = (now_grp - 1) * page_per_block;
    if (now_grp >= 2){ // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성
      str.append("<li class='page-item'><A class='page-link' href='"+list_file+"?&word="+word+"&now_page="+_now_page+"&type='"+type+"'>이전</A></span>");
    }

    // 중앙의 페이지 목록
    for(int i=start_page; i<=end_page; i++){
      if (i > total_page){ // 마지막 페이지를 넘어갔다면 페이 출력 종료
        break;
      }

      if (now_page == i){ // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
        str.append("<li class='page-item active'><A class='page-link'>"+i+"</li>"); // 현재 페이지, 강조
      }else{
        // 현재 페이지가 아닌 페이지는 이동이 가능하도록 링크를 설정
        str.append("<li class='page-item'><a class='page-link' href='" + list_file + "?word=" + word + "&type=" + type + "&now_page=" + i + "'>" + i + "</a></li>");
      }
    }

    // 10개 다음 페이지로 이동
    // nowGrp: 1 (1 ~ 10 page),  nowGrp: 2 (11 ~ 20 page),  nowGrp: 3 (21 ~ 30 page)
    // 현재 페이지 5일경우 -> 현재 1그룹: (1 * 10) + 1 = 2그룹의 시작페이지 11
    // 현재 페이지 15일경우 -> 현재 2그룹: (2 * 10) + 1 = 3그룹의 시작페이지 21
    // 현재 페이지 25일경우 -> 현재 3그룹: (3 * 10) + 1 = 4그룹의 시작페이지 31
    _now_page = (now_grp * page_per_block)+1; //  최대 페이지수 + 1
    if (now_grp < total_grp){
      str.append("<li class='page-item'><A class='page-link' href='"+list_file+"?&word="+word+"&now_page="+_now_page+"'>다음</A></span>");
    }
    str.append("</ul>");
    str.append("</nav>");
    return str.toString();
  }

  /**
   *
   * 관련 자료수 업데이트
   * @param cateno
   * @return
   */
  @Override
  public int update_cnt(int cateno) {
    return this.cateDAO.update_cnt(cateno);
  }

}
