package dev.mvc.map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/maps")
public class MapCont {

  public MapCont() {

  }

  @GetMapping("/maps")
  public String maps() {

    return "map/daum";
  }

}
