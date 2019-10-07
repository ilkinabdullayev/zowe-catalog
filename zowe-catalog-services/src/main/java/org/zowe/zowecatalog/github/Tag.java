package org.zowe.zowecatalog.github;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag {
    private String name;
    private String mergeCommitUrl;
}
