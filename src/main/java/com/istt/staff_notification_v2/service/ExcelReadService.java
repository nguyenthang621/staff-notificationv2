package com.istt.staff_notification_v2.service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import com.istt.staff_notification_v2.dto.LevelDTO;
import com.istt.staff_notification_v2.entity.Level;
import com.istt.staff_notification_v2.repository.LevelRepo;

@Service
public class ExcelReadService {
	@Autowired
	private LevelRepo levelRepo;
	@Autowired
    private ResourceLoader resourceLoader;

	public List<LevelDTO> ReadDataFromExcel() throws EncryptedDocumentException, InvalidFormatException, IOException{ 
		
		String excelPath = "src/main/resources/data/datalevel.xlsx";
		Workbook workbook = WorkbookFactory.create(new File(excelPath));
		// Retrieving the number of sheets in the Workbook
//        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");
//        System.out.println("Retrieving Sheets using for-each loop");
        List<Level> levels = new ArrayList<Level>();
        for(Sheet sheet: workbook) {
//            System.out.println("=> " + sheet.getSheetName());
            
            DataFormatter dataFormatter = new DataFormatter();
            for (Row row: sheet) {
            	String name = row.getCell(0).getStringCellValue();
            	Long code = (long) row.getCell(1).getNumericCellValue();
            	String des =  row.getCell(2).getStringCellValue();
            	
            	
            	Level level = new Level();
            	level.setLevelId(UUID.randomUUID().toString().replaceAll("-", ""));
            	level.setLevelName(name);
            	level.setLevelCode(code);
            	level.setDescription(des);
            	levels.add(level);
            }   
        }
        ModelMapper mapper = new ModelMapper();
        levelRepo.saveAll(levels);
		return levels.stream().map(rule -> mapper.map(rule, LevelDTO.class)).collect(Collectors.toList());
	}
}
