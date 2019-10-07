package org.zowe.zowecatalog.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UIVersionView {
    private String version;

    public UIVersionView(String version) {
        this.version = version;
    }
}
