//package com.istt.staff_notification_v2.service;
//import com.istt.staff_notification_v2.dto.LevelDTO;
//import com.istt.staff_notification_v2.entity.Level;
//import com.istt.staff_notification_v2.repository.LevelRepo;
//
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
//public class FileService {
//	
//    public static boolean isValidExcelFile(MultipartFile file){
//        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
//    }
//    
//    
//   public static List<Level> getCustomersDataFromExcel(InputStream inputStream){
//        List<Level> customers = new ArrayList<>();
//       try {
//           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
//           XSSFSheet sheet = workbook.getSheet("level");
//           int rowIndex =0;
//           for (Row row : sheet){
//               if (rowIndex ==0){
//                   rowIndex++;
//                   continue;
//               }
//               Iterator<Cell> cellIterator = row.iterator();
//               int cellIndex = 0;
//               Level levelDTO = new Level();
//               while (cellIterator.hasNext()){
//                   Cell cell = cellIterator.next();
//                   switch (cellIndex) {
//                   case 0 : 
//                	   levelDTO.setLevelName(cell.getStringCellValue());
//                   case 1 : levelDTO.setLevelCode(Long.parseLong(cell.getStringCellValue()));
//                   case 2 : levelDTO.setDescription(cell.getStringCellValue());
//                   default : {
//                   }
//				}
//                   cellIndex++;
//               }
//              levelDTO.setLevelId(UUID.randomUUID().toString().replaceAll("-", ""));
//              customers.add(levelDTO);
//           }
//       } catch (IOException e) {
//           e.getStackTrace();
//       }
//       return customers;
//   }
//
//}
