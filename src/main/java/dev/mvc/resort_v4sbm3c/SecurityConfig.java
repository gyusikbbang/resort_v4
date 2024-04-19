package dev.mvc.resort_v4sbm3c;

import dev.mvc.tool.Security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//환경 설정 클래스로 자동 실행되어 객체를 생성한후 이용됨.
@Configuration
public class SecurityConfig {
  @Bean
  Security security() {
    return new Security();
  }
 }
