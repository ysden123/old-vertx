/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.old.vertx.restserver;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("==>main");
        Vertx vertx = Vertx.vertx();

        logger.info("Deploying restServer...");
        vertx.deployVerticle(LongService.class.getName());
        vertx.deployVerticle(RestServer.class.getName());
    }
}
