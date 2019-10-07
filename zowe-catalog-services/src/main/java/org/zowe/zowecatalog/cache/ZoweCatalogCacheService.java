package org.zowe.zowecatalog.cache;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zowe.zowecatalog.catalog.Catalog;
import org.zowe.zowecatalog.catalog.CatalogFetcher;
import org.zowe.zowecatalog.github.GithubService;
import org.zowe.zowecatalog.release.Release;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoweCatalogCacheService {

    private static Map<String, Catalog> mapCatalog = new HashMap<>();
    private static Map<String, List<Release>> repoMap = new HashMap<>();

    private final CatalogFetcher catalogFetcher;
    private final GithubService githubService;

    /*
    TODO:There is not implemented any cache lib. Simply HashMap cache :)
     */
    public void putAllCatalogsToCache() {
        log.info("Caching is started");
        catalogFetcher.getAllCatalogsFromFolder().forEach(catalog -> {
                    if (catalog.getComponents() == null) {
                       log.warn("Catalog doesnt have component version for {}", catalog.getVersion());
                    } else {
                        catalog.getComponents().forEach(component -> {
                            List<Release> allReleases;
                            if (repoMap.containsKey(component.getRepository())) {
                                log.debug("{} is already exist in cache", component.getRepository());
                                allReleases = repoMap.get(component.getRepository());
                            } else {
                                allReleases = githubService.getRepositoryReleases(component.getRepository());
                                repoMap.put(component.getRepository(), allReleases);
                            }

                            component.setReleases(allReleases);
                        });
                    }


                    mapCatalog.put(catalog.getVersion(), catalog);
                });


        log.info("Caching is stopped");
    }


    public List<Catalog> getAllCatalogs() {
        return new ArrayList<>(mapCatalog.values())
                .stream()
                .sorted(Comparator.comparing(Catalog::getVersion, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
