package com.jyb.controller.action;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@javax.inject.Singleton
@Path("/etl_builder")
public class HelloResource {

//    public EtlResource(@Context ServletContext servletContext)
//    {
//        this.sylphContext = (SylphContext) servletContext.getAttribute("sylphContext");
//    }

    @GET
    @Path("save")
    @Produces({MediaType.APPLICATION_JSON})
    public Map hello(@Context HttpServletRequest request, @QueryParam("actuator") String actuator){
        Map<String, String> map = new HashMap<>();
        map.put("errorcod",actuator);
        return map;

    }
}
