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
    private Set<ExpensesTransaction> expensesTransactions = new HashSet<>();

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

    public Set<ExpensesTransaction> getExpensesTransactions() {
        return expensesTransactions;
    }

    public void setExpensesTransactions(Set<ExpensesTransaction> expensesTransactions) {
        this.expensesTransactions = expensesTransactions;
    }

    @JsonIgnore
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private void add(ExpensesTransaction expensesTransaction){
        expensesTransaction.setCategory(this);
        this.expensesTransactions.add(expensesTransaction);
    }

    public void addAll(List<ExpensesTransaction> expensesTransactions) {
        expensesTransactions.forEach(this::add);
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
