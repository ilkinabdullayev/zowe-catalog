package org.zowe.zowecatalog.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zowe.zowecatalog.catalog.service.CatalogView;
import org.zowe.zowecatalog.component.ComponentView;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ui")
@RequiredArgsConstructor
public class UIController {

    private final UIService uiService;

    @GetMapping("/catalogs/latest")
    public List<ComponentView> getLastVersionCatalogViewComponents() {
        return uiService.getLatestVersionComponents();
    }

    @GetMapping("/catalogs/latest/info")
    public CatalogView getLastVersionCatalogViewInfo() {
        return uiService.getLatestVersionInfo();
    }

    @GetMapping("/catalogs/version/{version}")
    public List<ComponentView> getCatalogComponentsByVersion(@PathVariable("version") String version) {
        return uiService.getCatalogComponentsByVersion(version);
    }

    @GetMapping("/catalogs/version/{version}/info")
    public CatalogView getCatalogInfoByVersion(@PathVariable("version") String version) {
        return uiService.getCatalogInfoByVersion(version);
    }

    @GetMapping("/catalogs/versions")
    public List<UIVersionView> getAllListComponentVersions() {
        return uiService.getAllCatalogVersions();
    }

    @GetMapping("/components/names")
    public List<UIComponentNameView> getAllListComponentNames() {
        return uiService.getAllComponentNames();
    }
}
