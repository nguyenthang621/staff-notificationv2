//package com.istt.staff_notification_v2.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.istt.staff_notification_v2.entity.Rule;
//
//@Repository
//public interface RuleRepo extends JpaRepository<Rule, String> {
//
//	Optional<Rule> findByRuleId(String ruleId);
//
//	@Query("SELECT r FROM Rule r WHERE r.employee.fullname LIKE :x ")
//	Page<Rule> searchByEmployeeName(@Param("x") String s, Pageable pageable);
//
//	@Query("SELECT r FROM Rule r WHERE r.employee.employeeId = :x ")
//	Optional<Rule> findByEmployeeId(@Param("x") String s);
//
//	@Query("SELECT r FROM Rule r WHERE r.ruleId in :ids")
//	Optional<List<Rule>> findByRuleIds(@Param("ids") List<String> ruleIds);
//
//	@Query("SELECT r FROM Rule r")
//	Optional<List<Rule>> getAll();
//}
