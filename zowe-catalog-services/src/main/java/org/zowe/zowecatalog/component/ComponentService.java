package org.zowe.zowecatalog.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zowe.zowecatalog.cache.ZoweCatalogCacheService;
import org.zowe.zowecatalog.catalog.Catalog;
import org.zowe.zowecatalog.catalog.service.CatalogView;
import org.zowe.zowecatalog.release.Release;
import org.zowe.zowecatalog.release.ReleaseView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComponentService {

    private final ZoweCatalogCacheService zoweCatalogCacheService;


    public List<String> getAllComponentNames() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        return  getAllComponents(catalogs)
                .map(Component::getName)
                .collect(Collectors.toList());
    }


    public List<ComponentView> getAllComponents() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        List<Component> components = getAllComponents(catalogs).collect(Collectors.toList());
        log.debug("Component size {}", components.size());

        //need to check list is not empty
        String latestVersionOfCatalog = catalogs.get(0).getVersion();


        List<ComponentView> componentViews = getListComponentView(components);
        componentViews
                .stream()
                .forEach(com -> {
                    com.setCurrentZoweVersion(latestVersionOfCatalog);
                });

        return componentViews;
    }

    public ComponentView getComponentByName(String name) {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        Optional<Component> componentOptional = getAllComponents(catalogs)
                .filter(c -> c.getName().equals(name))
                .findFirst();

        if (!componentOptional.isPresent()) {
            throw new ComponentNotFoundException("Component is not exist " + name);
        }


        //need to check list is not empty
        String latestVersionOfCatalog = catalogs.get(0).getVersion();

        ComponentView componentView = getComponentView(componentOptional.get());
        componentView.setCurrentZoweVersion(latestVersionOfCatalog);
        return componentView;
    }


    private Stream<Component> getAllComponents(List<Catalog> catalogs) {
        return catalogs
                .stream()
                .flatMap(catalog -> {
                    if (catalog.getComponents() == null) {
                        return Stream.empty();
                    }

                    return catalog.getComponents().stream();
                }).distinct();
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
        componentView.setId(generateUUID(component.getName()));

        List<Release> releases = component.getReleases();
        Optional<Release> optionalRelease = releases
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

        List<ReleaseView> releaseViews = getReleaseViews(releases);
        componentView.setReleases(releaseViews);
        return componentView;
    }

    private List<ReleaseView> getReleaseViews(List<Release> releases) {
        return releases.stream().map(this::getReleaseView).collect(Collectors.toList());
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

    public String generateUUID(String text) {
        String uuid = UUID.nameUUIDFromBytes(text.getBytes()).toString();
        return "" + uuid.substring(uuid.length() - 7);
    }

}
