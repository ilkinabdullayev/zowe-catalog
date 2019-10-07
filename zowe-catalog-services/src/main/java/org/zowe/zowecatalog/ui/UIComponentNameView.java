package org.zowe.zowecatalog.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UIComponentNameView {
    private String name;

    public UIComponentNameView(String name) {
        this.name = name;
    }
}
