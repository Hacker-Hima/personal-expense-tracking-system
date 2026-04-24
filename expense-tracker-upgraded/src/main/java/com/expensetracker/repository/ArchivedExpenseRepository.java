package com.expensetracker.repository;

import com.expensetracker.model.ArchivedExpense;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArchivedExpenseRepository extends JpaRepository<ArchivedExpense, Long> {
    List<ArchivedExpense> findByUserOrderByDateDesc(User user);
    List<ArchivedExpense> findByUserAndArchiveYearAndArchiveMonthOrderByDateDesc(User user, int year, int month);
}
