package org.zowe.zowecatalog;

import lombok.Data;
import org.zowe.zowecatalog.github.Release;

import java.util.List;

@Data
public class Component {

    private String name;
    private String repository;
    private String version;
    private List<Release> releases;
}
