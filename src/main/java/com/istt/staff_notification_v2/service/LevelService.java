package com.istt.staff_notification_v2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

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
import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.dto.ResponseDTO;
import com.istt.staff_notification_v2.dto.SearchDTO;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.repository.LevelRepo;

public interface LevelService {
	LevelDTO create(LevelDTO levelDTO);

	LevelDTO delete(String id);

	ResponseDTO<List<LevelDTO>> search(SearchDTO searchDTO);

}

@Service
class LevelServiceImpl implements LevelService {

	@Autowired
	private LevelRepo levelRepo;

	private static final String ENTITY_NAME = "isttLevel";

	@Override
	public LevelDTO create(LevelDTO levelDTO) {
		try {
			if (levelRepo.findByLevelNameorLevelCode(levelDTO.getLevelName(), levelDTO.getLevelCode()).isPresent()) {
				throw new BadRequestAlertException("Level Name or Level code already exists", ENTITY_NAME, "exist");
			}
			ModelMapper mapper = new ModelMapper();
			Level level = mapper.map(levelDTO, Level.class);
			level.setLevelId(UUID.randomUUID().toString().replaceAll("-", ""));
			levelRepo.save(level);
			return levelDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public LevelDTO delete(String id) {
		try {
			Level level = levelRepo.findByLevelId(id).orElseThrow(NoResultException::new);
			if (level != null) {
				levelRepo.deleteById(id);
				return new ModelMapper().map(level, LevelDTO.class);
			}
			return null;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}
	}

	@Override
	public ResponseDTO<List<LevelDTO>> search(SearchDTO searchDTO) {
		try {
			List<Sort.Order> orders = Optional.ofNullable(searchDTO.getOrders()).orElseGet(Collections::emptyList)
					.stream().map(order -> {
						if (order.getOrder().equals(SearchDTO.ASC))
							return Sort.Order.asc(order.getProperty());

						return Sort.Order.desc(order.getProperty());
					}).collect(Collectors.toList());
			Pageable pageable = PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), Sort.by(orders));

			Page<Level> page = levelRepo.searchByLevelName(searchDTO.getValue(), pageable);

			List<LevelDTO> levelDTOs = new ArrayList<>();
			for (Level level : page.getContent()) {
				LevelDTO levelDTO = new ModelMapper().map(level, LevelDTO.class);
				levelDTOs.add(levelDTO);
			}

			ResponseDTO<List<LevelDTO>> responseDTO = new ModelMapper().map(page, ResponseDTO.class);
			responseDTO.setData(levelDTOs);
			return responseDTO;
		} catch (ResourceAccessException e) {
			throw Problem.builder().withStatus(Status.EXPECTATION_FAILED).withDetail("ResourceAccessException").build();
		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw Problem.builder().withStatus(Status.SERVICE_UNAVAILABLE).withDetail("SERVICE_UNAVAILABLE").build();
		}

	}

}
