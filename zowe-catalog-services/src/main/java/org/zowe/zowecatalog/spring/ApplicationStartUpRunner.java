package org.zowe.zowecatalog.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.zowe.zowecatalog.catalog.CatalogService;

@Slf4j
@Configuration
public class ApplicationStartUpRunner implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            CatalogService catalogService = applicationContext.getBean(CatalogService.class);

            // Load the default catalogs
            catalogService.loadCatalogs();
        } catch (NoSuchBeanDefinitionException ex) {
            log.error(ex.getMessage());

            // Close the application
            int exitCode = SpringApplication.exit(applicationContext, () -> -1);
            System.exit(exitCode);
        }
    }

}
