package renewal.awesome_travel_backoffice.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.airline.service.AirlineService;
import renewal.awesome_travel_backoffice.airport.service.AirportService;
import renewal.awesome_travel_backoffice.citycode.service.CityCodeService;
import renewal.awesome_travel_backoffice.countrycode.service.CountryCodeService;
import renewal.common.entity.Airline;
import renewal.common.entity.AirportCode;
import renewal.common.entity.CityCode;
import renewal.common.entity.CountryCode;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final CountryCodeService countryCodeService;
    private final CityCodeService cityCodeService;
    private final AirlineService airlineService;
    private final AirportService airportService;

    // 국가 코드 Excel 업로드
    public int uploadCountryCodes(MultipartFile file) throws IOException {
        List<CountryCode> countryCodes = new ArrayList<>();

        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int successCount = 0;
            int skipCount = 0;

            // 첫 번째 행(헤더)은 건너뛰기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    String code = getCellValue(row.getCell(0));
                    String nameKor = getCellValue(row.getCell(1));
                    String nameEng = getCellValue(row.getCell(2));

                    if (code == null || code.trim().isEmpty()) {
                        skipCount++;
                        continue;
                    }

                    CountryCode countryCode = new CountryCode(
                            code.trim().toUpperCase(),
                            nameKor != null ? nameKor.trim() : "",
                            nameEng != null ? nameEng.trim() : "");

                    // 기존 코드가 있으면 업데이트, 없으면 생성
                    if (countryCodeService.existsByCode(countryCode.getCode())) {
                        countryCodeService.updateCountryCode(countryCode.getCode(), countryCode);
                    } else {
                        countryCodeService.createCountryCode(countryCode);
                    }
                    successCount++;
                } catch (Exception e) {
                    System.err.println("Error processing row " + i + ": " + e.getMessage());
                    skipCount++;
                }
            }

            return successCount;
        }
    }

    // 도시 코드 Excel 업로드
    public int uploadCityCodes(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int successCount = 0;
            int skipCount = 0;

            // 첫 번째 행(헤더)은 건너뛰기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    String country = getCellValue(row.getCell(0));
                    CountryCode countryCode = countryCodeService.getCountryCodeByCode(country).get();
                    String code = getCellValue(row.getCell(1));
                    String kor = getCellValue(row.getCell(2));
                    String eng = getCellValue(row.getCell(3));
                    Long utcOffsetMins = Long.valueOf(getCellValue(row.getCell(3)));

                    if (code == null || code.trim().isEmpty() || country == null || country.trim().isEmpty()) {
                        skipCount++;
                        continue;
                    }

                    CityCode cityCode = new CityCode(
                            code.trim().toUpperCase(),
                            eng != null ? eng.trim() : "",
                            kor != null ? kor.trim() : "",
                            utcOffsetMins,
                            countryCode);

                    // 기존 코드가 있으면 업데이트, 없으면 생성
                    if (cityCodeService.existsByCode(cityCode.getCityCode())) {
                        cityCodeService.updateCityCode(cityCode.getCityCode(), cityCode);
                    } else {
                        cityCodeService.createCityCode(cityCode);
                    }

                    successCount++;
                } catch (Exception e) {
                    System.err.println("Error processing row " + i + ": " + e.getMessage());
                    skipCount++;
                }
            }

            return successCount;
        }
    }

    // 국가 코드 Excel 다운로드
    public byte[] downloadCountryCodes() throws IOException {
        List<CountryCode> countryCodes = countryCodeService.getAllCountryCodesList();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("국가 코드");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = { "국가 코드", "국가명 (한글)", "국가명 (영문)" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowNum = 1;
            for (CountryCode country : countryCodes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(country.getCode());
                row.createCell(1).setCellValue(country.getNameKor());
                row.createCell(2).setCellValue(country.getNameEng());
            }

            // 열 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 도시 코드 Excel 다운로드
    public byte[] downloadCityCodes() throws IOException {
        List<CityCode> cityCodes = cityCodeService.getAllCityCodesList();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("도시 코드");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = { "국가 코드", "도시 코드", "도시명 (한글)", "도시명 (영문)" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowNum = 1;
            for (CityCode city : cityCodes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0)
                        .setCellValue(city.getCountryCode() != null ? city.getCountryCode().getCountryCode() : "");
                row.createCell(1).setCellValue(city.getCityCode());
                row.createCell(2).setCellValue(city.getCityKor());
                row.createCell(3).setCellValue(city.getCityEng());
            }

            // 열 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Excel 템플릿 다운로드 (국가 코드)
    public byte[] downloadCountryCodeTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("국가 코드");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            String[] headers = { "국가 코드", "국가명 (한글)", "국가명 (영문)" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 예시 데이터
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("KR");
            exampleRow.createCell(1).setCellValue("대한민국");
            exampleRow.createCell(2).setCellValue("South Korea");

            // 열 너비 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 2000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Excel 템플릿 다운로드 (도시 코드)
    public byte[] downloadCityCodeTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("도시 코드");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            String[] headers = { "국가 코드", "도시 코드", "도시명 (한글)", "도시명 (영문)" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 예시 데이터
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("KR");
            exampleRow.createCell(1).setCellValue("SEL");
            exampleRow.createCell(2).setCellValue("서울");
            exampleRow.createCell(3).setCellValue("Seoul");

            // 열 너비 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 2000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 항공사 Excel 업로드
    public int uploadAirlines(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int successCount = 0;

            // 첫 번째 행(헤더)은 건너뛰기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    String code = getCellValue(row.getCell(0));
                    String nameKor = getCellValue(row.getCell(1));
                    String nameEng = getCellValue(row.getCell(2));
                    String infantSeats = getCellValue(row.getCell(3));

                    if (code == null || code.trim().isEmpty()) {
                        continue;
                    }

                    boolean infantSeatsRequired = "Y".equalsIgnoreCase(infantSeats) || "예".equals(infantSeats)
                            || "true".equalsIgnoreCase(infantSeats);

                    Airline airline = new Airline(
                            code.trim().toUpperCase(),
                            nameKor != null ? nameKor.trim() : "",
                            nameEng != null ? nameEng.trim() : "",
                            infantSeatsRequired);

                    // 기존 코드가 있으면 업데이트, 없으면 생성
                    if (airlineService.existsByCode(airline.getCode())) {
                        airlineService.updateAirline(airline.getCode(), airline);
                    } else {
                        airlineService.createAirline(airline);
                    }
                    successCount++;
                } catch (Exception e) {
                    System.err.println("Error processing row " + i + ": " + e.getMessage());
                }
            }

            return successCount;
        }
    }

    // 항공사 Excel 다운로드
    public byte[] downloadAirlines() throws IOException {
        List<Airline> airlines = airlineService.getAllAirlinesList();

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("항공사");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = { "항공사 코드", "항공사명 (한글)", "항공사명 (영문)", "유아 좌석 필수" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowNum = 1;
            for (Airline airline : airlines) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(airline.getCode());
                row.createCell(1).setCellValue(airline.getNameKor());
                row.createCell(2).setCellValue(airline.getNameEng());
                row.createCell(3).setCellValue(airline.isInfantSeatsRequired() ? "Y" : "N");
            }

            // 열 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 항공사 Excel 템플릿 다운로드
    public byte[] downloadAirlineTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("항공사");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            String[] headers = { "항공사 코드", "항공사명 (한글)", "항공사명 (영문)", "유아 좌석 필수" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 예시 데이터
            Row exampleRow1 = sheet.createRow(1);
            exampleRow1.createCell(0).setCellValue("KE");
            exampleRow1.createCell(1).setCellValue("대한항공");
            exampleRow1.createCell(2).setCellValue("Korean Air");
            exampleRow1.createCell(3).setCellValue("Y");

            Row exampleRow2 = sheet.createRow(2);
            exampleRow2.createCell(0).setCellValue("OZ");
            exampleRow2.createCell(1).setCellValue("아시아나항공");
            exampleRow2.createCell(2).setCellValue("Asiana Airlines");
            exampleRow2.createCell(3).setCellValue("N");

            // 열 너비 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 2000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 공항 Excel 업로드
    public int uploadAirports(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int successCount = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    String code = getCellValue(row.getCell(0));
                    String countryCode = getCellValue(row.getCell(1));
                    String cityCode = getCellValue(row.getCell(2));
                    String nameKor = getCellValue(row.getCell(3));
                    String nameEng = getCellValue(row.getCell(4));
                    String typeStr = getCellValue(row.getCell(5));

                    if (code == null || code.trim().isEmpty())
                        continue;

                    // AirportCode.AirportType type = AirportCode.AirportType.BOTH;
                    // if (typeStr != null) {
                    // try {
                    // type = AirportCode.AirportType.valueOf(typeStr.toUpperCase());
                    // } catch (Exception e) {
                    // if ("국제".equals(typeStr)) type = AirportCode.AirportType.INTERNATIONAL;
                    // else if ("국내".equals(typeStr)) type = AirportCode.AirportType.DOMESTIC;
                    // else if ("국제/국내".equals(typeStr)) type = AirportCode.AirportType.BOTH;
                    // }
                    // }
                    CityCode cityCode2 = cityCodeService.getCityCodeByCode(cityCode).get();
                    AirportCode airport = new AirportCode(
                            code.trim().toUpperCase(),
                            nameKor != null ? nameKor.trim() : "",
                            nameEng != null ? nameEng.trim() : "",
                            cityCode2);

                    if (airportService.existsByCode(airport.getAirportCode())) {
                        airportService.updateAirport(airport.getAirportCode(), airport);
                    } else {
                        airportService.createAirport(airport);
                    }
                    successCount++;
                } catch (Exception e) {
                    System.err.println("Error processing row " + i + ": " + e.getMessage());
                }
            }
            return successCount;
        }
    }

    // 공항 Excel 다운로드
    public byte[] downloadAirports() throws IOException {
        List<AirportCode> airports = airportService.getAllAirportsList();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("공항");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] headers = { "공항 코드", "국가 코드", "도시 코드", "공항명 (한글)", "공항명 (영문)", "공항 유형" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (AirportCode airport : airports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(airport.getAirportCode());
                row.createCell(1).setCellValue(airport.getCityCode().toString());
                row.createCell(2).setCellValue(airport.getAirportKor());
                row.createCell(3).setCellValue(airport.getAirportEng());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 공항 Excel 템플릿
    public byte[] downloadAirportTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("공항");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] headers = { "공항 코드", "국가 코드", "도시 코드", "공항명 (한글)", "공항명 (영문)", "공항 유형" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Row example1 = sheet.createRow(1);
            example1.createCell(0).setCellValue("ICN");
            example1.createCell(1).setCellValue("KR");
            example1.createCell(2).setCellValue("SEL");
            example1.createCell(3).setCellValue("인천국제공항");
            example1.createCell(4).setCellValue("Incheon International Airport");
            example1.createCell(5).setCellValue("INTERNATIONAL");

            Row example2 = sheet.createRow(2);
            example2.createCell(0).setCellValue("GMP");
            example2.createCell(1).setCellValue("KR");
            example2.createCell(2).setCellValue("SEL");
            example2.createCell(3).setCellValue("김포국제공항");
            example2.createCell(4).setCellValue("Gimpo International Airport");
            example2.createCell(5).setCellValue("BOTH");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 2000);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 셀 값 읽기 (여러 타입 대응)
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
