package com.canehealth.qtakes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;

@Path("/hello")
public class ExampleResource {
    private static final String DEFAULT_PIPER_FILE = "META-INF/resources/Default.piper";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(DEFAULT_PIPER_FILE).getFile());
        String absolutePath = file.getAbsolutePath();
        return absolutePath;

    }
}