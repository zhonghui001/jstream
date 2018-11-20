package com.jyb.controller;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Map;

public class WebApplication extends ResourceConfig {
    public WebApplication(){

        //The jackson feature and provider is used for object serialization
        //between client and server objects in to a json
//        register(JacksonFeature.class);
//        register(JacksonProvider.class);

        //404
        register(AppExceptionMapper.class);
        //Glassfish multipart file uploader feature
        register(MultiPartFeature.class);
        packages("com.jyb.controller");

    }


}
