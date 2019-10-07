package org.zowe.zowecatalog.catalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zowe.zowecatalog.api.ZoweSuccessResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogs")
public class ZoweCatalogController {

    @GetMapping
    public ZoweSuccessResponse getAllListCatalog() {
        List<CatalogView> list = new ArrayList<>();

        CatalogView catalogView = new CatalogView();
        catalogView.setAbout("Hello");
        catalogView.setLatest(true);
        catalogView.setVersion("1.0");
        list.add(catalogView);
        return new ZoweSuccessResponse(list);
    }

    @GetMapping("/{version}")
    public ZoweSuccessResponse getCatalogByVersion(@RequestParam("version") String version) {
        List<CatalogView> list = new ArrayList<>();
        list.add(new CatalogView());
        return new ZoweSuccessResponse(list);
    }
}
