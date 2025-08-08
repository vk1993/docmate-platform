package com.docmate.taxonomy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = {"com.docmate.common.entity"})
@EnableJpaRepositories(basePackages = {"com.docmate.taxonomy.repository"})
public class TaxonomyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxonomyServiceApplication.class, args);
    }
}
