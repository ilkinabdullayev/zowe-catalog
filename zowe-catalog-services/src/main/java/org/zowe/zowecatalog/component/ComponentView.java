package org.zowe.zowecatalog.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.zowe.zowecatalog.release.Release;
import org.zowe.zowecatalog.release.ReleaseView;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ComponentView {

    private String id;
    private String name;
    private String repository;
    private ReleaseView currentRelease;
    private String currentZoweVersion;
    private List<ReleaseView> releases;

}
