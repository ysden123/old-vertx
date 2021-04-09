/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.old.vertx.restserver;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class RestServer extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(RestServer.class);

    @Override
    public void start() throws Exception {
        super.start();
        Config conf = ConfigFactory.load();

        int port = conf.getInt("port");

        var router = Router.router(vertx);

        router.post("/rest/long")
                .handler(routingContext ->
                        vertx.executeBlocking(
                                blockingHandler -> {
                                    logger.debug("in blockingHandler");
                                    var deliveryOptions = new DeliveryOptions().setSendTimeout(120_000);
                                    vertx.eventBus().send(
                                            LongService.EB_ADDRESS,
                                            "test",
                                            deliveryOptions,
                                            ar -> {
                                                if (ar.succeeded()) {
                                                    blockingHandler.complete(ar.result().body());
                                                } else {
                                                    blockingHandler.fail(ar.cause().getMessage());
                                                }
                                            });
                                },
                                resultHandler -> {
                                    logger.debug("in resultHandler");
                                    if (resultHandler.succeeded()) {
                                        if (routingContext.response().closed()) {
                                            logger.error("Response was closed");
                                        } else
                                            routingContext.response().end(resultHandler.result().toString());
                                    } else {
                                        logger.error(resultHandler.cause().getMessage());
                                        routingContext.response().setStatusCode(500).end(resultHandler.cause().getMessage());
                                    }
                                }
                        ));

        router.post("/rest/long2")
                .handler(routingContext -> {
                    var deliveryOptions = new DeliveryOptions().setSendTimeout(120_000);
                    vertx.eventBus().send(
                            LongService.EB_ADDRESS,
                            "test",
                            deliveryOptions,
                            ar -> {
                                if (ar.succeeded()) {
                                    if (routingContext.response().closed()) {
                                        logger.error("Response was closed");
                                    } else
                                        routingContext.response().end(ar.result().body().toString());
                                } else {
                                    logger.error(ar.cause().getMessage());
                                    routingContext.response().setStatusCode(500).end(ar.cause().getMessage());
                                }
                            });
                });
        var server = vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port);
    }
}
