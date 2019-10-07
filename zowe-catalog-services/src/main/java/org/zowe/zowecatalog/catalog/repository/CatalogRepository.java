package org.zowe.zowecatalog.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<CatalogH2, String> {
}
