package org.zowe.zowecatalog.catalog.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zowe.zowecatalog.catalog.CatalogService;
import org.zowe.zowecatalog.catalog.service.CatalogView;

import java.util.List;

@Api(tags = "Zowe Catalogs", description = "REST API for catalogs")
@RestController
@RequestMapping("/api/v1/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @ApiOperation(value = "List catalog of zowe")
    @GetMapping
    public List<CatalogView> getAllListCatalog() {
        return catalogService.getAllCatalogs();
    }

    @ApiOperation(value = "Latest version of zowe catalog")
    @GetMapping("/latest")
    public CatalogView getLastVersionCatalogView() {
        return catalogService.getLatestVersion();
    }

    @ApiOperation(value = "Get catalog zowe by version")
    @GetMapping("/version/{version}")
    public CatalogView getCatalogByVersion(@PathVariable("version") String version) {
        return catalogService.getCatalogByVersion(version);
    }

    @ApiOperation(value = "List component releases of zowe")
    @GetMapping("/versions")
    public List<String> getAllListComponentVersions() {
        return catalogService.getAllCatalogVersions();
    }
}
