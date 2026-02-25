package org.univ_paris8.iut.montreuil.arollet.qualite_dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class MasterAnnonceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterAnnonceApplication.class, args);
    }
}
