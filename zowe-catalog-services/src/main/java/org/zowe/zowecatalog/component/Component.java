package org.zowe.zowecatalog.component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.zowe.zowecatalog.release.Release;

import java.util.List;

@Data
@EqualsAndHashCode(of = "name")
public class Component {

    private String name;
    private String repository;
    private String version;
    private List<Release> releases;
}
