package com.finance.expensesservice.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finance.common.dto.Transaction;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by siarh on 25/02/2017.
 */
@MappedSuperclass
public abstract class Base extends Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @JsonIgnore
    @Version
    @Column(name = "LAST_MODIFICATION")
    private Timestamp lastModification;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getLastModification() {
        return lastModification;
    }

    public void setLastModification(Timestamp lastModification) {
        this.lastModification = lastModification;
    }
}
