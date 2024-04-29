package dev.mvc.cate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 카테고리 작업을 처리하는 인터페이스입니다.
 */
public interface CateProInter {

  /**
   * 기능:새로운 카테고리를 생성합니다.
   *
   * @param cateVO 생성할 카테고리 객체
   * @return int (영향을 받을 레코드 수)
   */
  public int create(CateVO cateVO);




  /**
   * 기능:모든 카테고리의 목록을 가져옵니다.
   *
   * @return ArrayList 카테고리 객체의 목록
   */
  public ArrayList<CateVO> list();


  /**
   * 기능:카테고리 ID를 기준으로 카테고리를 검색합니다.
   *
   * @param cateno 기준 검색
   * @return 검색된 CateVO 포함하는 Optional
   */
  public Optional<CateVO> read(int cateno);

  /**
   * 기존 카테고리를 업데이트합니다.
   *
   * @param cateVO 업데이트된 정보를 포함하는 카테고리 객체
   * @return int(영향을 받을 레코드 수)
   */
  public int update(CateVO cateVO);

  /**
   * 카테고리 ID를 기준으로 카테고리를 삭제합니다.
   *
   * @param cateno 삭제할 카테고리의 cateno
   * @return int(영향을 받을 레코드 수)
   */
  public int delete(int cateno);

  /**
   * 여러 카테고리 ID를 기준으로 카테고리를 삭제합니다.
   *
   * @param cateno 삭제할 카테고리 cateno 목록
   * @return int(영향을 받을 레코드 수)
   */
  public int multiple_delete(List<Integer> cateno);

  /**
   * 기능: 원하는 카테고리 꼴등 만들기
   *
   * @param cateno 원하는 카테고리의 cateno
   * @return int(영향을 받을 레코드 수)
   */
  public int min(int cateno);

  /**
   * 기능: 원하는 카테고리 1등 만들기
   *
   * @param cateno 원하는 카테고리의 cateno
   * @return 카테고리 ID 중 최대값
   */
  public int max(int cateno);

  /**
   * 기능: 카테고리 seqno 1 증가
   *
   * @param cateno 증가할 카테고리의 cateno
   * @return int(영향을 받을 레코드 수)
   */
  public int update_forward(int cateno);

  /**
   * 기능: 카테고리 seqno 1 감수
   *
   * @param cateno 감소할 카테고리의 cateno
   * @return int(영향을 받을 레코드 수)
   */
  public int update_backward(int cateno);

  /**
   * 숨겨진 카테고리를 표시합니다.
   *
   * @param cateno 표시할 카테고리의 cateno
   * @return 영향을 받은 레코드의 수
   */
  public int show(int cateno);

  /**
   * 카테고리를 숨깁니다.
   *
   * @param cateno 숨길 카테고리의 cateno
   * @return 영향을 받은 레코드의 수
   */
  public int hide(int cateno);

  /**
   * 기능 : 보임 여부가 'Y' 인 리스트 목록
   *
   * @return 표시된 카테고리 객체의 목록
   */
  public ArrayList<CateVO> list_all_name_y();

  /**
   *  메뉴에 출력하는거 소분류
   * @param name
   * @return
   */
  public ArrayList<CateVO> list_namesub_y(String name);
  /**
   *
   * @param name
   * @return
   */
  public ArrayList<CateVO> list_namesub();


  public ArrayList<CateVOMenu> menu();

  public ArrayList<CateVO> list_search_paging(String word,String type, int now_page,int record_per_page);


  public int list_search_count(String word,String type);

  public String pagingBox(int now_page, String word,String type, String list_file, int search_count,int record_per_page, int page_per_block);


  /**
   *
   * 관련 자료수 업데이트
   * @param cateno
   * @return
   */
  public int update_cnt(int cateno);
}
