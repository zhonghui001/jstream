package com.jyb.controller;

import com.jyb.controller.action.HelloResource;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.requireNonNull;

public class JettyServer {

    private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);

    private Server server;
    private final ServerConfig serverConfig;

    JettyServer(ServerConfig serverConfig){
        this.serverConfig =requireNonNull(serverConfig,"serverconfig 为null");

    }

    public void start()throws Exception{
        //--初始化 --- 获取context句柄
        int jettyPort = serverConfig.getServerPort();
        int maxFormContextSize = serverConfig.getMaxFromContentSize();

        //创建server
        this.server = new Server(jettyPort);
        server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",
                maxFormContextSize);

        HandlerList handlers = loadHandlers();  //加载路由

        server.setHandler(handlers);
        logger.info("web server 启动成功，端口为："+jettyPort);
        server.start();


    }


    private HandlerList loadHandlers() {

        HandlerList handlers = new HandlerList();
        //扫描注解 生成对应的映射
        ServletHolder servlet = new ServletHolder(new ServletContainer(new WebApplication()));


        //服务的提供
        ServletContextHandler contextHandler = new ServletContextHandler(
                ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");
        contextHandler.addServlet(servlet, "/sys/*");



        //静态资源
//        ResourceHandler resourceHandler = new ResourceHandler();
//        resourceHandler.setDirectoriesListed(true);
//        resourceHandler.setResourceBase("/data/gitrep/jstream/jstream-controller/src/main/webapp");
//        //resourceHandler.setResourceBase("./webapp");
//
//        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });


        final ServletHolder staticServlet = new ServletHolder(new DefaultServlet());
        contextHandler.addServlet(staticServlet, "/css/*");
        contextHandler.addServlet(staticServlet, "/js/*");
        contextHandler.addServlet(staticServlet, "/images/*");
        contextHandler.addServlet(staticServlet, "/fonts/*");
        contextHandler.addServlet(staticServlet, "/favicon.ico");
        contextHandler.addServlet(staticServlet, "/");
        //contextHandler.setResourceBase("/data/gitrep/jstream/jstream-controller/src/main/webapp");
        contextHandler.setResourceBase("./webapp");

        System.out.println(System.getProperty("user.dir"));

        handlers.addHandler(contextHandler);

        return handlers;
    }

    public static void main(String[] args) throws Exception {
        Integer port = 8089;
        if (args.length!=0)
            port = Integer.parseInt(args[0]);
        ServerConfig config = new ServerConfig().setServerPort(port);
        JettyServer jettyServer = new JettyServer(config);
        jettyServer.start();

    }

}
