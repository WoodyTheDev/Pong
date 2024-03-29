module pong.javafx {
    requires transitive javafx.controls;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires spring.context;
    requires spring.websocket;
    requires spring.messaging;
    requires spring.web;
    requires spring.beans;
    requires spring.core;
    requires lombok;
    requires org.json;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires stomp.websocket;
    requires tyrus.standalone.client;
    requires javafx.fxml;
    requires transitive log4j;

    exports pong.javafx.game.view;
    exports pong.javafx.game.controller;
    exports pong.javafx.game.model;
    exports pong.javafx.game.remote;
    exports pong.javafx.gamehistory.controller;
    exports pong.javafx.gamehistory.model;
    exports pong.javafx.gamehistory.view;
    exports pong.javafx.user.controller;
    exports pong.javafx.user.view;
    exports pong.javafx.user.model;
    exports pong.javafx.user.task;
    exports pong.javafx;

    opens pong.javafx.game.model;
    opens pong.javafx.gamehistory.model;
    opens pong.javafx.gamehistory.controller;
}
