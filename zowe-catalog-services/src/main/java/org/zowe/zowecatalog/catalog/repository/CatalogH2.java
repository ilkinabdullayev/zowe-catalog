package org.zowe.zowecatalog.catalog.repository;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "catalogs")
@Data
@NoArgsConstructor
public class CatalogH2 implements Serializable {

    private static final long serialVersionUID = 8591924456571955678L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private String contentJson;

}
