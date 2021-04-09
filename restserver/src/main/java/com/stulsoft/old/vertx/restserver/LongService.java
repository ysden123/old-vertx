/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.old.vertx.restserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Stul
 */
public class LongService extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(LongService.class);
    public static final String EB_ADDRESS = LongService.class.getName();

    @Override
    public void start() throws Exception {
        super.start();
        vertx.eventBus().consumer(EB_ADDRESS, this::handler);
    }

    private void handler(Message<String> message) {
        logger.info("==>handler");
        vertx.setTimer(60_000, l -> {
//        vertx.setTimer(500, l -> {
            logger.info("completed");
            var response = message.body() + " handled";
            message.reply(response);
        });

    }
}
