package com.finance.expensesservice.dao.util;

import com.finance.expensesservice.domain.Category;
import com.finance.expensesservice.domain.ExpensesTransaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;

public class TransactionSpecifications {

    private static final String TRANSACTION_DATE_FIELD = "transactionDate";
    private static final String CATEGORY_FIELD = "category";
    private static final String USER_ID_FIELD = "userId";
    private static final String ID_FIELD = "id";
    private static final String ORDER_ASC = "asc";

    public static Specification<ExpensesTransaction> getTransactions(Integer userId, Integer categoryId, String order) {
        return Specifications.where((root, criteria, builder) -> {
            if (!StringUtils.isEmpty(order)) {
                criteria.orderBy(ORDER_ASC.equals(order) ? builder.asc(root.get(TRANSACTION_DATE_FIELD)) :
                        builder.desc(root.get(TRANSACTION_DATE_FIELD)));
            }
            Join<ExpensesTransaction, Category> category = root.join(CATEGORY_FIELD, JoinType.INNER);
            Predicate userIdPredicate = builder.equal(category.<Integer>get(USER_ID_FIELD), userId);
            Predicate categoryIdPredicate = builder.equal(category.<Integer>get(ID_FIELD), categoryId);

            return categoryId == null ? userIdPredicate :
                    builder.and(userIdPredicate, categoryIdPredicate);
        });
    }
}
