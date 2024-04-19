package dev.mvc.resort_v4sbm3c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.mvc"}) // ★★★★★★ 패키지 주의 ★★★★★★ 
public class ResortV4sbm3cApplication {

  public static void main(String[] args) {
    SpringApplication.run(ResortV4sbm3cApplication.class, args);
  }

}
