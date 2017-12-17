package com.finance.expensesservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by siarh on 28/02/2017.
 */
@Entity
@Table(name = "EXPENSES_CATEGORY")
public class Category extends Base implements Serializable {

    @Column(name = "NAME")
    private String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    @Column(name = "USER_ID")
    private Integer userId;

    public Category() {
    }

    public Category(Category category) {
        this.name = category.getName();
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @JsonIgnore
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void add(Transaction transaction){
        transaction.setCategory(this);
        this.transactions.add(transaction);
    }

    public void addAll(List<Transaction> transactions) {
        transactions.forEach(this::add);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category that = (Category) o;

        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
