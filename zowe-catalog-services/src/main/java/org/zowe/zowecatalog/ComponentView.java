package org.zowe.zowecatalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.zowe.zowecatalog.github.Release;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ComponentView {

    private String name;
    private String iconURL;
    private String githubURL;
    private List<Release> releases;

}
