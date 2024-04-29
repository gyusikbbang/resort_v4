package dev.mvc.tool;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class Download extends jakarta.servlet.http.HttpServlet {
  static final long serialVersionUID = 1L;

  private ServletConfig config = null;

  public Download() {
    super();
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    this.config = config;
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    doProcess(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    doProcess(request, response);
  }

  protected void doProcess(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    ServletContext ctx = config.getServletContext();

    String dir = Tool.getOSPath() + request.getParameter("dir");
    String filename = request.getParameter("filename");
    String downname = request.getParameter("downname");

    File file = new File(dir, filename);
    if (file.exists()) {
      System.out.println(file.getAbsolutePath() + " 파일이 존재합니다.");
    } else {
      System.out.println(filename + " 다운로드 요청이 있었으나 파일이 존재하지 않습니다.");
      System.out.println(file.getAbsolutePath());
      return;
    }

    String contentType = ctx.getMimeType(filename);
    if (contentType == null) {
      contentType = "application/octet-stream";
    }

    String disposition = getDisposition(downname, getBrowser(request));
    response.addHeader("Content-disposition", disposition);
    response.setHeader("Content-Transfer-Encoding", "binary");
    response.setContentLength((int) file.length());
    response.setContentType(contentType);
    response.setHeader("Connection", "close");
    response.setCharacterEncoding("utf-8");

    byte buffer[] = new byte[4096];

    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
         BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

      int read;
      while ((read = bis.read(buffer)) != -1) {
        bos.write(buffer, 0, read);
      }
    } catch (Exception e) {
      System.out.println("Error while downloading file: " + e.getMessage());
    }
  }

  public String getBrowser(HttpServletRequest request) {
    String header = request.getHeader("User-Agent");

    if (header.indexOf("Mozilla") > -1) {
      return "Mozilla";
    } else if (header.indexOf("Chrome") > -1) {
      return "Chrome";
    } else if (header.indexOf("Opera") > -1) {
      return "Opera";
    } else {
      return "Firefox";
    }
  }

  public static synchronized String getDisposition(String filename, String browser) {
    String dispositionPrefix = "attachment;filename=";
    String encodedFilename = null;

    try {
      encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
      // '['와 ']'를 인코딩하여 치환
      encodedFilename = encodedFilename.replaceAll("\\[", "%5B").replaceAll("\\]", "%5D");
    } catch (Exception e) {
      System.out.println("Error while encoding filename: " + e.getMessage());
    }

    return dispositionPrefix + encodedFilename;
  }
}
