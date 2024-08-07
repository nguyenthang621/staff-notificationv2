package com.istt.staff_notification_v2.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.istt.staff_notification_v2.apis.errors.BadRequestAlertException;
import com.istt.staff_notification_v2.configuration.ApplicationProperties;
import com.istt.staff_notification_v2.dto.BusinessDaysDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchAttendence;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.BusinessDays;
import com.istt.staff_notification_v2.entity.Employee;
import com.istt.staff_notification_v2.repository.BusinessDaysRepo;
import com.istt.staff_notification_v2.repository.EmployeeRepo;
import com.istt.staff_notification_v2.repository.UserRepo;
import com.istt.staff_notification_v2.utils.utils;

public interface BusinessDaysService {
	List<BusinessDaysDTO> getAll();

	ResponseDTO<List<BusinessDaysDTO>> search(SearchAttendence searchAttendence);

	BusinessDaysDTO get(String id);

	List<BusinessDaysDTO> findByDescription(String description);

	BusinessDaysDTO create(BusinessDaysDTO businessDaysDTO);

	BusinessDaysDTO update(BusinessDaysDTO businessDaysDTO);

	Boolean delete(String id);

	Boolean deleteByListId(List<String> ids);

	ResponseDTO<List<BusinessDaysDTO>> searchByType(SearchDTO searchDTO);
}

@Service
class BusinessDaysServiceImpl implements BusinessDaysService {

	@Autowired
	private BusinessDaysRepo businessDaysRepo;
	
	@Autowired 
	private EmployeeRepo employeeRepo;
	
	@Autowired
	private EmployeeService employeeService;

	@Autowired
	ApplicationProperties props;

	private static final String ENTITY_NAME = "isttBusinessDays";
	private static final Logger logger = LogManager.getLogger(BusinessDaysService.class);

	@Override
	public List<BusinessDaysDTO> getAll() {
		List<BusinessDays> businessDays = businessDaysRepo.getAll();
		ModelMapper mapper = new ModelMapper();
		return businessDays.stream().map(day -> mapper.map(day, BusinessDaysDTO.class)).collect(Collectors.toList());
	}

	@Override
	public List<BusinessDaysDTO> findByDescription(String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BusinessDaysDTO create(BusinessDaysDTO businessDaysDTO) {
		try {
			int count=0;
			if(businessDaysDTO.getStartdate().getYear()!= new Date().getYear()||businessDaysDTO.getEnddate().getYear()!= new Date().getYear())
				throw new BadRequestAlertException("Only this year", ENTITY_NAME, "logic data");
			BusinessDays businessDays = new ModelMapper().map(businessDaysDTO, BusinessDays.class);
			businessDays.setBussinessdaysId(UUID.randomUUID().toString().replaceAll("-", ""));
			if (!props.getTYPE_BUSINESSDAYS().contains(businessDaysDTO.getType())) {
				logger.error("Invalid TYPE BUSINESSDAYS");
				throw new BadRequestAlertException("Invalid TYPE BUSINESSDAYS", ENTITY_NAME, "Invalid");
			}
			businessDaysDTO.setStartdate(new utils().resetStartDate(businessDaysDTO.getStartdate()));
			businessDaysDTO.setEnddate(new utils().resetStartDate(businessDaysDTO.getEnddate()));

			List<BusinessDays> splitBusinessDays = new utils()
					.handleSplitBusinessDays(new ModelMapper().map(businessDaysDTO, BusinessDays.class));

			for (BusinessDays splitBusinessDay : splitBusinessDays) {
				splitBusinessDay.setBussinessdaysId(UUID.randomUUID().toString().replaceAll("-", ""));
				if (!businessDaysRepo.existsByStartdate(splitBusinessDay.getStartdate())) {
//					System.out.println(splitBusinessDay.getStartdate());
					count++;
					businessDaysRepo.save(splitBusinessDay);
				}
			}
			//check type neu nghi khong luong thi tru ngay phep con lai khong lam gi ca
			if(businessDaysDTO.getType().equals("K")) {
				employeeService.calListDayOff(count, false);
			}
			return businessDaysDTO;
		} catch (ResourceAccessException e) {
			logger.trace(Status.EXPECTATION_FAILED.toString());
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			logger.trace(Status.SERVICE_UNAVAILABLE.toString());
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public BusinessDaysDTO update(BusinessDaysDTO businessDaysDTO) {
		try {
			BusinessDays businessDays = businessDaysRepo.findByBussinessdaysId(businessDaysDTO.getBussinessdaysId())
					.orElseThrow(NoResultException::new);
			if (!props.getTYPE_BUSINESSDAYS().contains(businessDaysDTO.getType())) {
				logger.error("Invalid TYPE BUSINESSDAYS");
				throw new BadRequestAlertException("Invalid TYPE BUSINESSDAYS", ENTITY_NAME, "Invalid");
			}
			Employee employee = employeeRepo.findByEmail("nnkhanh@istt.com.vn");
			logger.error("truoc khi tru phep"+employee.getCountOfDayOff());
			//neu update lai type
			if(!businessDaysDTO.getType().equals(businessDays.getType())) {
				//neu update tu co luong sang khong luong thi tru ngay phep di
				if(businessDaysDTO.getType().equals("K")) {
					employeeService.calListDayOff(1, false);
					logger.error("sau khi tru phep"+employee.getCountOfDayOff());
				}
				//neu update tu khong luong sang co luong thi cong ngay phep vao
				else employeeService.calListDayOff(1, true);
			}
			businessDays.setType(businessDaysDTO.getType());
			businessDays.setDescription(businessDaysDTO.getDescription());
			businessDaysRepo.save(businessDays);
			return businessDaysDTO;
		} catch (ResourceAccessException e) {
			logger.trace(Status.EXPECTATION_FAILED.toString());
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			logger.trace(Status.SERVICE_UNAVAILABLE.toString());
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public Boolean delete(String id) {
		if (businessDaysRepo.findById(id).isEmpty()) {
			logger.error("not found businessdays");
			return false;
		}
		BusinessDays businessDays = businessDaysRepo.findById(id).get();
		//check xem ngay nghi do luong hay khong
		//neu khong luong(co bi tru ngay phep thi cong lai ngay phep cho moi nguoi)
		if(businessDays.getType().equals("K")) 
			employeeService.calListDayOff(1, true);
		businessDaysRepo.deleteById(id);
		return true;
	}

	@Override
	public Boolean deleteByListId(List<String> ids) {
		List<BusinessDays> businessDays = businessDaysRepo.findAllById(ids);
		if (businessDays.size() < 1) {
			logger.error("not found businessDays");
			return false;
		}
		
		for (BusinessDays businessDays2 : businessDays) {
			if(businessDays2.getType().equals("K")) 
				employeeService.calListDayOff(1, true);
		}
		
		businessDaysRepo.deleteAll(businessDays);
		return true;
	}

	@Override
	public BusinessDaysDTO get(String id) {
		if (businessDaysRepo.findById(id).isEmpty()) {
			logger.error("not found businessdays");
			throw new NoResultException();
		}
		ModelMapper mapper = new ModelMapper();
		return mapper.map(businessDaysRepo.findById(id).get(), BusinessDaysDTO.class);
	}

	@Override
	public ResponseDTO<List<BusinessDaysDTO>> searchByType(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<BusinessDays> page = businessDaysRepo.findByType(searchDTO.getValue(), pageable);
			ModelMapper mapper = new ModelMapper();
			List<BusinessDaysDTO> businessDaysDTOs = page.getContent().stream()
					.map(day -> mapper.map(day, BusinessDaysDTO.class)).collect(Collectors.toList());

			ResponseDTO<List<BusinessDaysDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(businessDaysDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			logger.trace(Status.EXPECTATION_FAILED.toString());
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			logger.trace(Status.SERVICE_UNAVAILABLE.toString());
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<List<BusinessDaysDTO>> search(SearchAttendence searchAttendence) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchAttendence.getSearch().getOrders())
					.orElseGet(Collections::emptyList).stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchAttendence.getSearch().getPage(),
					searchAttendence.getSearch().getSize(), Sort.by(orders));

			if (searchAttendence.getType() == null)
				searchAttendence.setType("%%");

			Page<BusinessDays> page = businessDaysRepo.search(searchAttendence.getStartDate(),
					searchAttendence.getEndDate(), searchAttendence.getType(), pageable);

			ModelMapper mapper = new ModelMapper();
			List<BusinessDaysDTO> businessDaysDTOs = page.getContent().stream()
					.map(day -> mapper.map(day, BusinessDaysDTO.class)).collect(Collectors.toList());

			ResponseDTO<List<BusinessDaysDTO>> responseDTO = mapper.map(page, ResponseDTO.class);
			responseDTO.setData(businessDaysDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			logger.trace(Status.EXPECTATION_FAILED.toString());
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			logger.trace(Status.SERVICE_UNAVAILABLE.toString());
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

}