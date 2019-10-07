package org.zowe.zowecatalog.cache;


import org.springframework.stereotype.Service;
import org.zowe.zowecatalog.catalog.Catalog;
import org.zowe.zowecatalog.github.Content;
import org.zowe.zowecatalog.util.FileUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZoweCatalogCacheService {

    private Map<String, Catalog> mapCatalog = new HashMap<>();


    public void putAllCatalogsToCache() {
        List<Content> contents = FileUtil.readAllContent("statics/catalogs");



    }

}
