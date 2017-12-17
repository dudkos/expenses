package com.finance.expensesservice.controller;

import com.finance.expensesservice.domain.Category;
import com.finance.expensesservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by siarh on 06/05/2017.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoriesService;

    @Autowired
    public CategoryController(CategoryService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Category findExpensesCategoryById(@PathVariable Integer id) {
        return categoriesService.findCategoryById(id, false);
    }

    @GetMapping
    public List<Category> findExpensesCategories() {
        return categoriesService.findCategories();
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Category createExpensesCategory(@RequestBody Category expensesCategory) {
        return categoriesService.createCategory(expensesCategory);
    }

    @PutMapping(value = "/{categoryId}")
    public Category updateExpensesCategory(@PathVariable Integer categoryId,
                                                       @RequestBody Category expensesCategory) {
        return categoriesService.updateCategory(categoryId, expensesCategory);
    }

    @DeleteMapping(value = "/{categoryId}")
    public ResponseEntity<Void> deleteExpensesCategory(@PathVariable Integer categoryId) {
        categoriesService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
