package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.HelloWorldConfiguration;
import com.example.helloworld.api.Saying;
import com.example.helloworld.core.Template;
import com.example.helloworld.zookeeper.ZookeeperConfiguration;
import com.example.helloworld.zookeeper.ZookeeperUtils;
import com.google.common.base.Optional;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.params.DateTimeParam;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldResource.class);

    private final Template template;
    private final AtomicLong counter;
    private HelloWorldConfiguration configuration;

    public HelloWorldResource(Template template, HelloWorldConfiguration configuration) {
        this.template = template;
        this.counter = new AtomicLong();
        this.configuration = configuration;
    }

    @GET
    @Timed(name = "get-requests")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        return new Saying(counter.incrementAndGet(), template.render(name));
    }

    @POST
    public void receiveHello(@Valid Saying saying) {
        LOGGER.info("Received a saying: {}", saying);
    }

    @GET
    @Path("/date")
    @Produces(MediaType.TEXT_PLAIN)
    public String receiveDate(@QueryParam("date") Optional<DateTimeParam> dateTimeParam) {
        if (dateTimeParam.isPresent()) {
            final DateTimeParam actualDateTimeParam = dateTimeParam.get();
            LOGGER.info("Received a date: {}", actualDateTimeParam);
            return actualDateTimeParam.get().toString();
        } else {
            LOGGER.warn("No received date");
            return null;
        }
    }

    @GET
    @Path("/sayMyName")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayMyName() {
        return configuration.getZookeeperConfiguration().getNodeName();
    }

    @GET
    @Path("/sayOurNames")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayOurNames() throws Exception {

        ZookeeperConfiguration zkConf = configuration.getZookeeperConfiguration();
        CuratorFramework client = ZookeeperUtils.getClient(zkConf);
        List<String> nodes = client.getChildren().forPath(zkConf.getRootNode());
        LOGGER.info(">>> Nodes: " + nodes.size());
        String names = "";
        for(String node:nodes) {
            LOGGER.info(">>> Other node: " + node);
            String serviceURI = new String(client.getData().forPath(zkConf.getRootNode() + "/" + node));
            String otherServiceNameURI = "http://"+serviceURI+"/hello-world/sayMyName";
            LOGGER.info(">>> otherServiceNameURI: " + otherServiceNameURI);
            String otherName = sendGet(otherServiceNameURI);
            names += otherName + ", ";
            LOGGER.info(">>> Other service URI: " + serviceURI);
        }
        return names;
    }

    private String sendGet(String url) throws IOException {

        GetMethod method = new GetMethod(url);
        HttpClient client = new HttpClient();
        int status = client.executeMethod(method);
        return method.getResponseBodyAsString();
    }
}
