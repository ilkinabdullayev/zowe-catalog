package org.zowe.zowecatalog.catalog;

import lombok.Data;
import org.zowe.zowecatalog.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class Catalog {
    private String version;
    private String description;
    private List<Component> components;
    private ZonedDateTime releaseDate;
}
