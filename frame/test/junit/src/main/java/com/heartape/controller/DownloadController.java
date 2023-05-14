package com.heartape.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.heartape.config.ExcelConfig;
import com.heartape.entity.ExcelEntity;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/download")
public class DownloadController {

    @SneakyThrows
    @GetMapping("/excel")
    public void excel(HttpServletResponse response){
        String filename = URLEncoder.encode("excel测试文件.xlsx", StandardCharsets.UTF_8);

        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        ServletOutputStream outputStream = response.getOutputStream();

        List<ExcelEntity> list = List.of(new ExcelEntity(1, "jackson"));

        EasyExcel
                .write(outputStream, ExcelEntity.class)
                .excelType(ExcelTypeEnum.XLSX)
                .autoCloseStream(true)
                .charset(StandardCharsets.UTF_8)
                .sheet("sheet1")
                .registerWriteHandler(new ExcelConfig())
                .doWrite(list);
    }
}
