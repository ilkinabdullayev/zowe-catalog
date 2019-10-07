package org.zowe.zowecatalog.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zowe.zowecatalog.cache.ZoweCatalogCacheService;
import org.zowe.zowecatalog.github.ZoweGithubService;
import org.zowe.zowecatalog.util.FileUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZoweCatalogApplicationListener {

    private final ZoweCatalogCacheService catalogCacheService;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.trace("Zowecatalog listener started");
        catalogCacheService.putAllCatalogsToCache();
        log.trace("Zowecatalog listener stopped");
    }

}
