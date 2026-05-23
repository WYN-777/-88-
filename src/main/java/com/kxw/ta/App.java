package com.kxw.ta;

import com.kxw.ta.web.AdminServlet;
import com.kxw.ta.web.AuthServlet;
import com.kxw.ta.web.DashboardServlet;
import com.kxw.ta.web.EncodingFilter;
import com.kxw.ta.web.FileServlet;
import com.kxw.ta.web.MoServlet;
import com.kxw.ta.web.TaServlet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.MultipartConfigElement;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class App {
    public static void main(String[] args) throws Exception {
        Path projectDir = Paths.get("").toAbsolutePath();
        Path webappDir = projectDir.resolve("src/main/webapp");
        Path dataDir = projectDir.resolve("data");
        Path uploadTempDir = dataDir.resolve("tmp");
        Files.createDirectories(uploadTempDir);

        System.setProperty("app.dataDir", dataDir.toString());

        int port = Integer.parseInt(System.getProperty("port", System.getenv().getOrDefault("PORT", "8080")));

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(projectDir.resolve("target/tomcat").toString());
        tomcat.setPort(port);
        tomcat.getConnector();
        tomcat.getConnector().setURIEncoding("UTF-8");

        Context context = tomcat.addWebapp("", webappDir.toAbsolutePath().toString());
        context.setParentClassLoader(Thread.currentThread().getContextClassLoader());
        context.addWelcomeFile("index.jsp");

        Tomcat.addServlet(context, "auth", new AuthServlet()).addMapping("/auth/*");
        Tomcat.addServlet(context, "dashboard", new DashboardServlet()).addMapping("/dashboard");
        Tomcat.addServlet(context, "mo", new MoServlet()).addMapping("/mo/*");
        Tomcat.addServlet(context, "admin", new AdminServlet()).addMapping("/admin/*");
        Tomcat.addServlet(context, "files", new FileServlet()).addMapping("/files/*");

        Wrapper ta = Tomcat.addServlet(context, "ta", new TaServlet());
        ta.addMapping("/ta/*");
        ta.setMultipartConfigElement(new MultipartConfigElement(
                uploadTempDir.toString(),
                5L * 1024L * 1024L,
                6L * 1024L * 1024L,
                1024 * 1024));

        FilterDef encodingFilter = new FilterDef();
        encodingFilter.setFilterName("encoding");
        encodingFilter.setFilterClass(EncodingFilter.class.getName());
        context.addFilterDef(encodingFilter);

        FilterMap encodingMap = new FilterMap();
        encodingMap.setFilterName("encoding");
        encodingMap.addURLPattern("/*");
        context.addFilterMap(encodingMap);

        tomcat.start();
        System.out.println("TA Recruitment Portal running at http://localhost:" + port);
        tomcat.getServer().await();
    }
}
