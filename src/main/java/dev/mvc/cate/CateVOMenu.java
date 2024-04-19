package dev.mvc.cate;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter @Getter
public class CateVOMenu {
    /** 카테고리 번호 */

    
    /** 중분류 명 */
    private String name;
    
    ArrayList<CateVO> listnamesub;
}
