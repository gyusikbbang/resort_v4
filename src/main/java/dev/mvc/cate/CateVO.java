package dev.mvc.cate;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

//list_all TABLE CATE(
//    CATENO                            NUMBER(10)     NOT NULL    PRIMARY KEY,
//    NAME                              VARCHAR2(30)     NOT NULL,
//    CNT                               NUMBER(7)    DEFAULT 0     NOT NULL,
//    RDATE                             DATE     NOT NULL,
//    SEQNO                             NUMBER(5)    DEFAULT 0     NOT NULL,
//    VISIBLE                           CHAR(1)    DEFAULT 'N'     NOT NULL
//);

@Setter
@Getter
public class CateVO {


  /**
   * 오버렌딩 카테고리 번호
   */
  private int cateno;

  /**
   * 중분류명
   */
  @NotEmpty(message = "중분류명은 필수 입력 항목입니다.")
  @Size(min = 2, max = 10, message = "중분류명의 입력 글자 수는 최소 2자에서 10자 이어야합니다.")
  private String name;

  /**
   * 소분류명
   */
  @NotEmpty(message = "소분류명은 필수 입력 항목입니다.")
  @Size(min = 1, max = 10, message = "소분류명의 입력 글자 수는 최소 2자에서 영어 30자(한글 10자) 이어야합니다.")
  private String namesub;

  /**
   * 관련 자료수
   */
  @NotNull(message = "관련자료수는 필수 입력 항목입니다.")

  @Max(value = 100000000)
  @Min(value = 0)
  private Integer cnt = 0;

  /**
   * 등록일
   */
  private String rdate;

  /**
   * 출력 순서
   */
  @NotNull(message = "출력순서는 필수 입력 항목입니다.")
  @Positive(message = "양수만 가능합니다.")
  @Max(value = 100000000)
  @Min(value = 1)

  private Integer seqno;
  /**
   * 출력 모드
   */
  @NotEmpty(message = "출력모드는 필수 입력 항목입니다.")
  @Size(min = 1, max = 1, message = "출력모드는 1글자만 가능합니다.")
  @Pattern(regexp = "[YNyn]", message = "출력모드는 'Y' 또는 'N' 중 하나여야 합니다.")
  private String visible = "Y";


}