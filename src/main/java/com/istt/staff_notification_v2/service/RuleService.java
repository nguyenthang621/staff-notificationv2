//package com.istt.staff_notification_v2.service;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.persistence.NoResultException;
//import javax.transaction.Transactional;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
//import org.zalando.problem.Problem;
//import org.zalando.problem.Status;
//
//import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
//import com.istt.staff_notification_v2.dto.ResponseDTO;
//import com.istt.staff_notification_v2.dto.RuleDTO;
//import com.istt.staff_notification_v2.dto.SearchDTO;
//import com.istt.staff_notification_v2.entity.Employee;
//import com.istt.staff_notification_v2.entity.Rule;
//import com.istt.staff_notification_v2.repository.EmployeeRepo;
//import com.istt.staff_notification_v2.repository.RuleRepo;
//
//public interface RuleService {
//
//	RuleDTO create(RuleDTO ruleDTO);
//
//	RuleDTO delete(String id);
//
//	ResponseDTO<List<RuleDTO>> search(SearchDTO searchDTO);
//
//	RuleDTO update(RuleDTO ruleDTO);
//
//	List<RuleDTO> getAll();
//
//}
//
//@Service
//class RuleServiceImpl implements RuleService {
//
//	@Autowired
//	private RuleRepo ruleRepo;
//
//	@Autowired
//	private EmployeeRepo employeeRepo;
//
//	private static final String ENTITY_NAME = "isttRule";
//
//	@Transactional
//	@Override
//	public RuleDTO create(RuleDTO ruleDTO) {
//		try {
//			Employee employee = employeeRepo.findByEmployeeId(ruleDTO.getEmployee().getEmployeeId()).get();
//			if (ruleRepo.findByEmployeeId(ruleDTO.getEmployee().getEmployeeId()).isPresent())
//				throw new BadRequestAlertException("Bad request: Rule already exists", ENTITY_NAME, "Exists");
//			// validate departmentlist and employeelist request
//
//			ModelMapper mapper = new ModelMapper();
//			Rule rule = mapper.map(ruleDTO, Rule.class);
//			rule.setRuleId(UUID.randomUUID().toString().replaceAll("-", ""));
//			rule.setEmployee(employee);
//			ruleRepo.save(rule);
//			return ruleDTO;
//		} catch (ResourceAccessException e) {
//			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
//		} catch (HttpServerErrorException | HttpClientErrorException e) {
//			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
//		}
//	}
//
//	@Transactional
//	@Override
//	public RuleDTO delete(String id) {
//		try {
//			Rule rule = ruleRepo.findById(id).orElseThrow(NoResultException::new);
//			if (rule != null) {
//				ruleRepo.deleteById(id);
//				return new ModelMapper().map(rule, RuleDTO.class);
//			}
//			return null;
//		} catch (ResourceAccessException e) {
//			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
//		} catch (HttpServerErrorException | HttpClientErrorException e) {
//			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
//		}
//	}
//
//	@Override
//	public ResponseDTO<List<RuleDTO>> search(SearchDTO searchDTO) {
//		try {
//			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
//					.stream().map(order -> {
//						if (order.getOrder().equals(SearchDTO.ASC))
//							return Sort.Order.asc(order.getProperty());
//
//						return Sort.Order.desc(order.getProperty());
//					}).collect(Collectors.toList());
//			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));
//
//			Page<Rule> page = ruleRepo.searchByEmployeeName(searchDTO.getValue(), pageable);
//
//			List<RuleDTO> ruleDTOs = new ArrayList<>();
//			for (Rule rule : page.getContent()) {
//				RuleDTO ruleDTO = new ModelMapper().map(rule, RuleDTO.class);
//				ruleDTOs.add(ruleDTO);
//			}
//
//			ResponseDTO<List<RuleDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
//			responseDTO.setData(ruleDTOs);
//			return responseDTO;
//		} catch (ResourceAccessException e) {
//			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
//		} catch (HttpServerErrorException | HttpClientErrorException e) {
//			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
//		}
//	}
//
//	@Override
//	@Transactional
//	public RuleDTO update(RuleDTO ruleDTO) {
//		try {
//			if (ruleRepo.findByRuleId(ruleDTO.getRuleId()).isEmpty())
//				throw new BadRequestAlertException("Rule not found", ENTITY_NAME, "Not found");
//
//			ruleRepo.save(new ModelMapper().map(ruleDTO, Rule.class));
//			return ruleDTO;
//		} catch (ResourceAccessException e) {
//			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
//		} catch (HttpServerErrorException | HttpClientErrorException e) {
//			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
//		}
//	}
//
//	@Override
//	public List<RuleDTO> getAll() {
//		try {
//			List<Rule> rules = ruleRepo.findAll();
//			return rules.stream().map(rule -> new ModelMapper().map(rule, RuleDTO.class)).collect(Collectors.toList());
//		} catch (ResourceAccessException e) {
//			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
//		} catch (HttpServerErrorException | HttpClientErrorException e) {
//			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
//		}
//	}
//
//}
