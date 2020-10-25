module zkysms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires sqlite.jdbc;
    requires java.sql;
    requires webcam.capture;
    requires bridj;
    requires org.apache.commons.io;
    requires commons.validator;
    requires jakarta.mail;
    requires google.api.services.drive.v3.rev197;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.json.jackson2;
    requires com.google.errorprone.annotations;
    requires google.api.client;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.fasterxml.jackson.core;
    requires jdk.httpserver;
    requires org.slf4j;
    requires org.slf4j.jul;

    opens zkysms to javafx.fxml;
    exports zkysms;
    opens zkysms.ui.homepage to javafx.fxml;
    opens zkysms.ui.registrationform.student to javafx.fxml;
    opens zkysms.ui.registrationform.teacher to javafx.fxml;
    opens zkysms.ui.loginpage to javafx.fxml;
    opens zkysms.ui.setup to javafx.fxml;
    opens zkysms.ui.unlockkey to javafx.fxml;
}