package org.zowe.zowecatalog.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zowe.zowecatalog.cache.ZoweCatalogCacheService;
import org.zowe.zowecatalog.catalog.Catalog;
import org.zowe.zowecatalog.catalog.service.CatalogNotFoundException;
import org.zowe.zowecatalog.catalog.service.CatalogView;
import org.zowe.zowecatalog.component.Component;
import org.zowe.zowecatalog.component.ComponentView;
import org.zowe.zowecatalog.release.Release;
import org.zowe.zowecatalog.release.ReleaseView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UIService {
    //TODO: need implement mapstruct lib

    private final ZoweCatalogCacheService zoweCatalogCacheService;

    public List<UIVersionView> getAllCatalogVersions() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        return catalogs
                .stream()
                .map(catalog -> new UIVersionView(catalog.getVersion()))
                .collect(Collectors.toList());
    }

    public List<UIComponentNameView> getAllComponentNames() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        return  catalogs
                .stream()
                .flatMap(catalog -> {
                    if (catalog.getComponents() == null) {
                        return Stream.empty();
                    }

                    return catalog.getComponents().stream();
                })
                .map(component -> new UIComponentNameView(component.getName()))
                .collect(Collectors.toList());
    }


    public List<ComponentView> getLatestVersionComponents() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();

        //need to check list is not empty
        Catalog catalog = catalogs.get(0);
        return getListComponentView(catalog.getComponents());
    }

    public CatalogView getLatestVersionInfo() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();

        //need to check list is not empty
        Catalog catalog = catalogs.get(0);
        String latestVersionOfCatalog = catalog.getVersion();
        log.debug("Latest version is {}", latestVersionOfCatalog);

        CatalogView catalogView = getCatalogView(catalog, latestVersionOfCatalog);
        catalogView.setComponents(null);
        return catalogView;
    }

    public List<ComponentView> getCatalogComponentsByVersion(String version) {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        Optional<Catalog> catalogOptional = catalogs.stream()
                .filter(c -> c.getVersion().equals(version))
                .findFirst();

        if (!catalogOptional.isPresent()) {
            throw new CatalogNotFoundException("Catalog is not exist" + version);
        }

        return getListComponentView(catalogOptional.get().getComponents());
    }

    public CatalogView getCatalogInfoByVersion(String version) {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();

        //need to check list is not empty
        String latestVersionOfCatalog = catalogs.get(0).getVersion();

        Optional<Catalog> catalogOptional = catalogs.stream()
                .filter(c -> c.getVersion().equals(version))
                .findFirst();

        if (!catalogOptional.isPresent()) {
            throw new CatalogNotFoundException("Catalog is not exist" + version);
        }

        CatalogView catalogView = getCatalogView(catalogOptional.get(), latestVersionOfCatalog);
        catalogView.setComponents(null);
        return catalogView;
    }

    private CatalogView getCatalogView(Catalog catalog, String latestVersionOfCatalog) {
        CatalogView catalogView = new CatalogView();
        catalogView.setVersion(catalog.getVersion());
        catalogView.setLatest(catalog.getVersion().equals(latestVersionOfCatalog));
        catalogView.setDescription(catalog.getDescription());

        String releaseDateString = catalog.getReleaseDate().format(DateTimeFormatter.ISO_DATE_TIME);
        catalogView.setReleaseDate(releaseDateString);

        List<ComponentView> componentViews = getListComponentView(catalog.getComponents());
        catalogView.setComponents(componentViews);
        return catalogView;
    }

    private List<ComponentView> getListComponentView(List<Component> components) {
        List<ComponentView> componentViews = new ArrayList<>();
        if (components != null) {
            components.forEach(component -> {
                ComponentView componentView = getComponentView(component);
                componentViews.add(componentView);
            });
        }

        return componentViews;
    }

    private ComponentView getComponentView(Component component) {
        ComponentView componentView = new ComponentView();
        componentView.setName(component.getName());
        componentView.setRepository(component.getRepository());

        Optional<Release> optionalRelease = component.getReleases()
                .stream()
                .filter(release -> release.getVersion().equals(component.getVersion()))
                .findAny();

        ReleaseView releaseView = null;
        if (optionalRelease.isPresent()) {
            releaseView = getReleaseView(optionalRelease.get());
        } else {
            releaseView = new ReleaseView(component.getVersion());
        }

        componentView.setCurrentRelease(releaseView);
        return componentView;
    }

    private ReleaseView getReleaseView(Release release) {
        ReleaseView releaseView = new ReleaseView();
        releaseView.setCommitHash(release.getCommitHash());
        releaseView.setNotes(release.getNotes());
        releaseView.setVersion(release.getVersion());
        String releaseDateString = release.getReleaseDate().format(DateTimeFormatter.ISO_DATE_TIME);
        releaseView.setReleaseDate(releaseDateString);
        return releaseView;
    }
}
