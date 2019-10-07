package org.zowe.zowecatalog.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@Data
@AllArgsConstructor
@ToString(of = {"version", "commit"})
public class Content {

    private String version;
    private String text;
    private Commit commit;


}
