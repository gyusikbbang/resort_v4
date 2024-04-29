package dev.mvc.resort_v4sbm3c;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.mvc.contents.Contents;
import dev.mvc.tool.Tool;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Windows: path = "C:/kd/deploy/resort_v2sbm3c_blog/contents/storage";
        // ▶ file:///C:/kd/deploy/resort_v2sbm3c_blog/contents/storage
      
        // Ubuntu: path = "/home/ubuntu/deploy/resort_v2sbm3c_blog/contents/storage";
        // ▶ file:////home/ubuntu/deploy/resort_v2sbm3c_blog/contents/storage
      
        // JSP 인식되는 경로: http://localhost:9091/contents/storage";
        registry.addResourceHandler("/contents/storage/**").addResourceLocations("file:///" +  Contents.getUploadDir());
        
        // JSP 인식되는 경로: http://localhost:9091/attachfile/storage";
        // registry.addResourceHandler("/contents/storage/**").addResourceLocations("file:///" +  Tool.getOSPath() + "/attachfile/storage/");
        
        // JSP 인식되는 경로: http://localhost:9091/member/storage";
        // registry.addResourceHandler("/contents/storage/**").addResourceLocations("file:///" +  Tool.getOSPath() + "/member/storage/");

        // Windows: path = "C:/kd/deploy/resort_v2sbm3c_blog/contents/storage";
        // ▶ file:///C:/kd/deploy/resort_v2sbm3c_blog/contents/storage

        // Ubuntu: path = "/home/ubuntu/deploy/resort_v2sbm3c_blog/contents/storage";
        // ▶ file:////home/ubuntu/deploy/resort_v2sbm3c_blog/contents/storage

        // HTML 인식되는 경로: http://localhost:9091/mp3/storage";
//        registry.addResourceHandler("/mp3/storage/**").addResourceLocations("file:///" +  MP3.getUploadDir());
//
//        // HTML 인식되는 경로: http://localhost:9091/mp4/storage";
//        registry.addResourceHandler("/mp4/storage/**").addResourceLocations("file:///" +  MP4.getUploadDir());

        // JSP 인식되는 경로: http://localhost:9091/attachfile/storage";
        // registry.addResourceHandler("/attachfile/storage/**").addResourceLocations("file:///" +  Tool.getOSPath() + "/attachfile/storage/");

        // JSP 인식되는 경로: http://localhost:9091/member/storage";
        // registry.addResourceHandler("/member/storage/**").addResourceLocations("file:///" +  Tool.getOSPath() + "/member/storage/");
    }
 
}
