package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserOrderByDateDesc(User user);

    List<Expense> findAllByOrderByDateDesc();

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND (:category IS NULL OR e.category = :category) AND (:from IS NULL OR e.date >= :from) AND (:to IS NULL OR e.date <= :to) AND (:keyword IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%',:keyword,'%')) OR LOWER(e.category) LIKE LOWER(CONCAT('%',:keyword,'%'))) ORDER BY e.date DESC")
    List<Expense> filterExpenses(@Param("user") User user,
                                 @Param("category") String category,
                                 @Param("from") LocalDate from,
                                 @Param("to") LocalDate to,
                                 @Param("keyword") String keyword);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user")
    BigDecimal sumAmountByUser(@Param("user") User user);

    @Query("SELECT SUM(e.amount) FROM Expense e")
    BigDecimal sumAllAmounts();

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<Object[]> sumByCategory(@Param("user") User user);

    @Query("SELECT MONTH(e.date), SUM(e.amount) FROM Expense e WHERE e.user = :user AND YEAR(e.date) = :year GROUP BY MONTH(e.date) ORDER BY MONTH(e.date)")
    List<Object[]> sumByMonthAndYear(@Param("user") User user, @Param("year") int year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    BigDecimal sumByUserAndMonth(@Param("user") User user, @Param("month") int month, @Param("year") int year);

    long countByUser(User user);

    List<Expense> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate from, LocalDate to);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> sumByCategoryAllUsers();

    @Query("SELECT u.username, SUM(e.amount) FROM Expense e JOIN e.user u GROUP BY u.username ORDER BY SUM(e.amount) DESC")
    List<Object[]> findTopSpenders();
}
