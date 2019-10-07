package org.zowe.zowecatalog.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.zowe.zowecatalog.ComponentView;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CatalogView {
    private String version;
    private String about;
    private boolean latest;
    private String releaseDate;
    private List<ComponentView> components;
}
