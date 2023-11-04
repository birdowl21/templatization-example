package com.excel.template.controller;

import com.excel.template.entity.Field;
import com.excel.template.entity.Organization;
import com.excel.template.reqobj.Column;
import com.excel.template.reqobj.DeleteFields;
import com.excel.template.reqobj.DeletedField;
import com.excel.template.reqobj.EditedField;
import com.excel.template.service.OrganizationService;
import com.excel.template.service.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TemplateController {
    private OrganizationService organizationService;
    private TemplateService templateService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public TemplateController(OrganizationService organizationService, TemplateService templateService) {
        this.organizationService = organizationService;
        this.templateService = templateService;
    }


    @GetMapping("/org/{orgId}")
    public ModelAndView orgTemplate(@PathVariable("orgId") int orgId, Model model, HttpSession session)
    {
        Organization organization= organizationService.findById(orgId).orElse(null);
        if(organization==null)
            return new ModelAndView("error");
        session.setAttribute("orgId",orgId);
        List<Field> fields = organization.getFields();
        System.out.println("fields: "+fields);
        model.addAttribute("templates", fields);
        model.addAttribute("column",new Column());
        model.addAttribute("orgName",organization.getOrgName());
        return new ModelAndView("template");
    }

    @PostMapping(value = "/org/{orgId}/add-column")
    public String addColumn(@PathVariable("orgId") int orgId, Column column)
    {
        try{
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return "error";
            templateService.addColumn(organization,column);
            return "redirect:/org/"+orgId;
        }catch (Exception e){
            return "error";
        }


    }

    @GetMapping("/org/{orgId}/get-column/{fieldId}")
    public ResponseEntity getColumnDetails(@PathVariable("orgId") int orgId, @PathVariable("fieldId") int fieldId,HttpSession session)
    {
        Organization organization= organizationService.findById(orgId).orElse(null);
        if(organization==null)
            return ResponseEntity.badRequest().build();
        Optional<Field> field= organization.getFields().stream().filter(f->f.getFieldId()==fieldId).findFirst();
        if(field.isEmpty())
            return ResponseEntity.badRequest().build();
        System.out.println(field.get().getFieldId());
        return ResponseEntity.ok(field.get());
    }

    @PostMapping("/org/{orgId}/edit-column/{fieldId}")
    public ResponseEntity editColumn(@PathVariable("orgId") int orgId,@PathVariable("fieldId") int fieldId, @RequestBody EditedField column)
    {

        try{
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            Optional<Field> field= organization.getFields().stream().filter(f->f.getFieldId()==fieldId).findFirst();
            if(field.isEmpty())
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
//            Column column = mapper.readValue(columnStr,Column.class);
            System.out.println("column name: "+column.getFieldName());
            String fieldName=column.getFieldName();
            templateService.editColumn(field.get(),fieldName);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/org/{orgId}/delete-column")
    public ResponseEntity deleteColumn(@PathVariable("orgId") int orgId,@RequestBody DeletedField deletedField)
    {
        try{
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            int fieldId=deletedField.getFieldId();
            Optional<Field> field= organization.getFields().stream().filter(f->f.getFieldId()==fieldId).findFirst();
            if(field.isEmpty())
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            templateService.deleteColumn(orgId,field.get());
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/org/{orgId}/page2")
    public ModelAndView getPage2(@PathVariable("orgId") int orgId,Model model)
    {
        model.addAttribute("orgId",orgId);
        return new ModelAndView("page2");
    }

    @GetMapping("/org/{orgId}/page2/generateExcel")
    public ResponseEntity<?> download(@PathVariable("orgId") int orgId)
    {
        // Return an error response to the client
        HttpHeaders errorHeaders = new HttpHeaders();
        errorHeaders.setContentType(MediaType.APPLICATION_JSON);

        try{
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return new ResponseEntity<>("Bad Request",errorHeaders, HttpStatus.BAD_REQUEST);
            //create POJO
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Organization Template");
            List<Field> fields = organization.getFields();
            System.out.print("fields: "+fields);
            int rownum = 0;
            for (Field field : fields) {
                XSSFRow row = sheet.createRow(rownum++);
                row.createCell(0).setCellValue(field.getFieldName());
            }
            // Create a ByteArrayOutputStream to store the generated Excel data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(outputStream.size());
            headers.setContentDispositionFormData("attachment", "generated_excel.xlsx");

            // Return the Excel data as a response entity
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            // Customize the error response as needed
            String errorMessage = "Error generating Excel file.";
            return new ResponseEntity<>(errorMessage, errorHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping(value = "/org/{orgId}/page2/uploadExcel",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@PathVariable("orgId") int orgId, @RequestParam("file") MultipartFile excelFile)
    {
        try {
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
            byte[] fileBytes=excelFile.getBytes();
            InputStream fileInputStream = new ByteArrayInputStream(fileBytes);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

//            List<Field> fields = new ArrayList<>();

            int maxCols=0;
            int maxRows=sheet.getLastRowNum()+1;
            for (Row row : sheet) {
                int lastCellNum = row.getLastCellNum();
                if (lastCellNum > maxCols) {
                    maxCols = lastCellNum;
                }
            }

            EditedField[][] columns= new EditedField[maxRows][maxCols];

            for (int rowNum = 0; rowNum <maxRows; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                for(int i=0;i<maxCols;i++)
                {
                    EditedField cell = new EditedField();
                    cell.setFieldName(addCell(row.getCell(i)));
                    columns[rowNum][i]=cell;
                }


            }
            workbook.close();
//            templateService.saveAll(fields);
            return new ResponseEntity<>(columns, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(value = "/org/{orgId}/delete-all")
    public ResponseEntity deleteAllRecords(@PathVariable("orgId") int orgId)
    {
        try{
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
            templateService.deleteAll(orgId);
            System.out.println("Successfully deleted all records!");
            return new ResponseEntity<>("Successfully deleted all records!", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting records!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/org/{orgId}/delete-records")
    public ResponseEntity deleteRecordsByIds(@PathVariable("orgId") int orgId,@RequestBody List<String> deleteFieldIdStrings)
    {
        try{
            Organization organization= organizationService.findById(orgId).orElse(null);
            if(organization==null)
                return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
            List<Long> deleteFieldIds=new ArrayList<>();
            for(String s: deleteFieldIdStrings)
            {
                deleteFieldIds.add(Long.parseLong(s));
            }
            templateService.deleteAllByIds(orgId,deleteFieldIds);
            System.out.println("Successfully deleted the records!");
            return new ResponseEntity<>("Successfully deleted records!", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting records!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private String addCell(Cell cell) throws Exception
    {

        if(cell==null)
            return "";
        String res="";
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            res = cell.getStringCellValue();

        }
        else if (cellType == CellType.NUMERIC) {
            res=""+(int)cell.getNumericCellValue();
        }
        else if(cellType==CellType.BOOLEAN)
        {
            res=""+cell.getBooleanCellValue();
        }
        else{
            throw new Exception("Cell Type is not accepted");
        }
        return res;
    }


    @ModelAttribute("session")
    public HttpSession requestURI(final HttpSession session) {
        return session;
    }
}
