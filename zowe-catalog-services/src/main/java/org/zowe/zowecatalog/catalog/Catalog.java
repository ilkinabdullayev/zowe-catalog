package org.zowe.zowecatalog.catalog;

import lombok.Data;
import lombok.ToString;
import org.zowe.zowecatalog.component.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@ToString(exclude = "description")
public class Catalog {
    private String version;
    private String description;
    private List<Component> components;
    private ZonedDateTime releaseDate;
}
