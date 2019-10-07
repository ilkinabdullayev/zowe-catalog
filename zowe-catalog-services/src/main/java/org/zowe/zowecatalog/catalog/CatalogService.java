package org.zowe.zowecatalog.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zowe.zowecatalog.cache.ZoweCatalogCacheService;
import org.zowe.zowecatalog.catalog.repository.CatalogH2;
import org.zowe.zowecatalog.catalog.repository.CatalogRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {
    //TODO: need implement mapstruct lib
    //TODO: Move duplicated method to common class

    private final CatalogRepository catalogRepository;

    private final ZoweCatalogCacheService zoweCatalogCacheService;

    @Transactional(rollbackFor = Exception.class)
    public void loadCatalogs() {
        List<CatalogH2> catalogs = new ArrayList<>();

        CatalogH2 catalog1 = new CatalogH2();
        catalog1.setContentJson("{\"version\":\"1.0.0\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"releaseDate\":\"2019-02-05T15:52:25Z\"}");
        catalogs.add(catalog1);

        CatalogH2 catalog2 = new CatalogH2();
        catalog2.setContentJson("{\"version\":\"1.0.1\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"releaseDate\":\"2019-02-13T19:28:24Z\"}");
        catalogs.add(catalog2);

        CatalogH2 catalog3 = new CatalogH2();
        catalog3.setContentJson("{\"version\":\"1.1.0\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"releaseDate\":\"2019-03-29T19:34:28Z\"}");
        catalogs.add(catalog3);

        CatalogH2 catalog4 = new CatalogH2();
        catalog4.setContentJson("{\"version\":\"1.2.0\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"releaseDate\":\"2019-04-30T13:35:25Z\"}");
        catalogs.add(catalog4);

        CatalogH2 catalog5 = new CatalogH2();
        catalog5.setContentJson("{\"version\":\"1.3.0\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"components\":[{\"name\":\"Imperative CLI Framework\",\"repository\":\"imperative\",\"version\":\"2.4.8\"},{\"name\":\"Zowe API Mediation Layer\",\"repository\":\"api-layer\",\"version\":\"1.1.5\"},{\"name\":\"zowe-common-c(Zowe Application Framework)\",\"repository\":\"zowe-common-c\",\"version\":\"zss-v1.3.0\"},{\"name\":\"Zowe CLI\",\"repository\":\"zowe-cli\",\"version\":\"2.28.2\"},{\"name\":\"Zowe CLI Plug-in for IBM CICS\",\"repository\":\"zowe-cli-cics-plugin\",\"version\":\"1.0.0\"},{\"name\":\"Zowe CLI Plug-in for IBM Db2 Database\",\"repository\":\"zowe-cli-db2-plugin\",\"version\":\"2.1.0\"},{\"name\":\"Zowe Desktop Data Sets UI Plugin\",\"repository\":\"explorer-mvs\",\"version\":\"0.0.15\"},{\"name\":\"Zowe Desktop Eclipse Orion-based React Editor\",\"repository\":\"orion-editor-component\",\"version\":\"0.0.9\"},{\"name\":\"Zowe Desktop JES UI Plugin\",\"repository\":\"explorer-jes\",\"version\":\"0.0.21\"},{\"name\":\"Zowe Desktop Web Plug-in Server\",\"repository\":\"explorer-ui-server\",\"version\":\"0.2.8\"},{\"name\":\"Zowe Desktop z/OS Unix Files UI Plugin\",\"repository\":\"explorer-uss\",\"version\":\"0.0.13\"},{\"name\":\"Zowe Explorer Common REST Server\",\"repository\":\"explorer-api-common\",\"version\":\"0.3.2\"},{\"name\":\"Zowe Explorer Data Sets REST Server\",\"repository\":\"data-sets\",\"version\":\"0.2.2\"},{\"name\":\"Zowe Explorer Jobs REST Server\",\"repository\":\"jobs\",\"version\":\"0.2.4\"},{\"name\":\"zosmf-auth(Zowe Application Framework Authentication Handlers)\",\"repository\":\"zosmf-auth\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop TN3270 Emulator Plug-in\",\"repository\":\"tn3270-ng2\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop Sample Angular Application\",\"repository\":\"sample-angular-app\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop Sample iFrame Application\",\"repository\":\"sample-iframe-app\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop Sample React Application\",\"repository\":\"sample-react-app\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop VT Emulator Plugin-in\",\"repository\":\"vt-ng2\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop Editor Plugin-in\",\"repository\":\"zlux-editor\",\"version\":\"1.3.0\"},{\"name\":\"Zowe Desktop Workflows Plugin-in\",\"repository\":\"zlux-workflow\",\"version\":\"1.3.0\"}],\"releaseDate\":\"2019-06-28T18:00:13Z\"}");
        catalogs.add(catalog5);

        CatalogH2 catalog6 = new CatalogH2();
        catalog6.setContentJson("{\"version\":\"1.4.0\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"components\":[{\"name\":\"Imperative CLI Framework\",\"repository\":\"imperative\",\"version\":\"lts-incremental\"},{\"name\":\"Zowe API Mediation Layer\",\"repository\":\"api-layer\",\"version\":\"master\"},{\"name\":\"zowe-common-c(Zowe Application Framework)\",\"repository\":\"zowe-common-c\",\"version\":\"staging\"},{\"name\":\"Zowe CLI\",\"repository\":\"zowe-cli\",\"version\":\"lts-incremental\"},{\"name\":\"Zowe CLI Plug-in for IBM CICS\",\"repository\":\"zowe-cli-cics-plugin\",\"version\":\"lts-incremental\"},{\"name\":\"Zowe CLI Plug-in for IBM Db2 Database\",\"repository\":\"zowe-cli-db2-plugin\",\"version\":\"lts-incremental\"},{\"name\":\"Zowe Desktop Data Sets UI Plugin\",\"repository\":\"explorer-mvs\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Eclipse Orion-based React Editor\",\"repository\":\"orion-editor-component\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop JES UI Plugin\",\"repository\":\"explorer-jes\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Web Plug-in Server\",\"repository\":\"explorer-ui-server\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop z/OS Unix Files UI Plugin\",\"repository\":\"explorer-uss\",\"version\":\"staging\"},{\"name\":\"Zowe Explorer Common REST Server\",\"repository\":\"explorer-api-common\",\"version\":\"staging\"},{\"name\":\"Zowe Explorer Data Sets REST Server\",\"repository\":\"data-sets\",\"version\":\"staging\"},{\"name\":\"Zowe Explorer Jobs REST Server\",\"repository\":\"jobs\",\"version\":\"staging\"},{\"name\":\"zosmf-auth(Zowe Application Framework Authentication Handlers)\",\"repository\":\"zosmf-auth\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop TN3270 Emulator Plug-in\",\"repository\":\"tn3270-ng2\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Sample Angular Application\",\"repository\":\"sample-angular-app\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Sample iFrame Application\",\"repository\":\"sample-iframe-app\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Sample React Application\",\"repository\":\"sample-react-app\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop VT Emulator Plugin-in\",\"repository\":\"vt-ng2\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Editor Plugin-in\",\"repository\":\"zlux-editor\",\"version\":\"staging\"},{\"name\":\"Zowe Desktop Workflows Plugin-in\",\"repository\":\"zlux-workflow\",\"version\":\"staging\"},{\"name\":\"Zowe Visual Studio Code Extension\",\"repository\":\"vscode-extension-for-zowe\",\"version\":\"master\"}],\"releaseDate\":\"2019-09-02T16:42:29Z\"}");
        catalogs.add(catalog6);

        CatalogH2 catalog7 = new CatalogH2();
        catalog7.setContentJson("{\"version\":\"1.5.0\",\"description\":\"Zowe is an open source project created to host technologies that benefit the Z platform from all members of the Z community (Integrated Software Vendors, System Integrators and z/OS consumers). Zowe, like Mac or Windows, comes with a set of APIs and OS capabilities that applications build on and also includes some applications out of the box. Zowe offers modern interfaces to interact with z/OS and allows you to work with z/OS in a way that is similar to what you experience on cloud platforms today. You can use these interfaces as delivered or through plug-ins and extensions that are created by clients or third-party vendors.\",\"components\":[{\"name\":\"Imperative CLI Framework\",\"repository\":\"imperative\",\"version\":\"2.6.0\"},{\"name\":\"Zowe API Mediation Layer\",\"repository\":\"api-layer\",\"version\":\"1.1.9\"},{\"name\":\"zowe-common-c(Zowe Application Framework)\",\"repository\":\"zowe-common-c\",\"version\":\"zss-v1.5.0\"},{\"name\":\"Zowe CLI\",\"repository\":\"zowe-cli\",\"version\":\"2.31.2\"},{\"name\":\"Zowe CLI Plug-in for IBM CICS\",\"repository\":\"zowe-cli-cics-plugin\",\"version\":\"1.1.2\"},{\"name\":\"Zowe CLI Plug-in for IBM Db2 Database\",\"repository\":\"zowe-cli-db2-plugin\",\"version\":\"2.1.1\"},{\"name\":\"Zowe Desktop Data Sets UI Plugin\",\"repository\":\"explorer-mvs\",\"version\":\"0.0.15\"},{\"name\":\"Zowe Desktop Eclipse Orion-based React Editor\",\"repository\":\"orion-editor-component\",\"version\":\"0.0.9\"},{\"name\":\"Zowe Desktop JES UI Plugin\",\"repository\":\"explorer-jes\",\"version\":\"0.0.22\"},{\"name\":\"Zowe Desktop Web Plug-in Server\",\"repository\":\"explorer-ui-server\",\"version\":\"0.2.8\"},{\"name\":\"Zowe Desktop z/OS Unix Files UI Plugin\",\"repository\":\"explorer-uss\",\"version\":\"0.0.13\"},{\"name\":\"Zowe Explorer Common REST Server\",\"repository\":\"explorer-api-common\",\"version\":\"0.3.5\"},{\"name\":\"Zowe Explorer Data Sets REST Server\",\"repository\":\"data-sets\",\"version\":\"0.2.4\"},{\"name\":\"Zowe Explorer Jobs REST Server\",\"repository\":\"jobs\",\"version\":\"0.2.7\"},{\"name\":\"zosmf-auth(Zowe Application Framework Authentication Handlers)\",\"repository\":\"zosmf-auth\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop TN3270 Emulator Plug-in\",\"repository\":\"tn3270-ng2\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop Sample Angular Application\",\"repository\":\"sample-angular-app\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop Sample iFrame Application\",\"repository\":\"sample-iframe-app\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop Sample React Application\",\"repository\":\"sample-react-app\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop VT Emulator Plugin-in\",\"repository\":\"vt-ng2\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop Editor Plugin-in\",\"repository\":\"zlux-editor\",\"version\":\"1.5.0\"},{\"name\":\"Zowe Desktop Workflows Plugin-in\",\"repository\":\"zlux-workflow\",\"version\":\"1.5.0\"}],\"releaseDate\":\"2019-10-02T15:56:47Z\"}");
        catalogs.add(catalog7);

        catalogRepository.saveAll(catalogs);
    }

    public List<CatalogView> getAllCatalogs() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();

        //need to check list is not empty
        String latestVersionOfCatalog = catalogs.get(0).getVersion();

        log.debug("Catalog size {}", catalogs.size());
        List<CatalogView> catalogViews = new ArrayList<>();

        catalogs.forEach(catalog -> {
            CatalogView catalogView = getCatalogView(catalog, latestVersionOfCatalog);
            catalogViews.add(catalogView);
        });


        return catalogViews;
    }

    public List<String> getAllCatalogVersions() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();
        return catalogs
                .stream()
                .map(Catalog::getVersion)
                .collect(Collectors.toList());
    }

    public CatalogView getLatestVersion() {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();

        //need to check list is not empty
        Catalog catalog = catalogs.get(0);
        String latestVersionOfCatalog = catalog.getVersion();
        log.debug("Latest version is {}", latestVersionOfCatalog);

        return getCatalogView(catalogs.get(0), latestVersionOfCatalog);
    }

    public CatalogView getCatalogByVersion(String version) {
        List<Catalog> catalogs = zoweCatalogCacheService.getAllCatalogs();

        //need to check list is not empty
        String latestVersionOfCatalog = catalogs.get(0).getVersion();

        Optional<Catalog> catalogOptional = catalogs.stream()
                .filter(c -> c.getVersion().equals(version))
                .findFirst();

        if (!catalogOptional.isPresent()) {
            throw new CatalogNotFoundException("Catalog is not exist" + version);
        }

        return getCatalogView(catalogOptional.get(), latestVersionOfCatalog);
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
        componentView.setId(generateUUID(component.getName()));

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

    public String generateUUID(String text) {
        String uuid = UUID.nameUUIDFromBytes(text.getBytes()).toString();
        return "" + uuid.substring(uuid.length() - 7);
    }
}
