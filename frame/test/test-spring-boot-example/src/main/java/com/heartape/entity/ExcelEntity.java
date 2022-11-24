package com.heartape.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 30)
@HeadFontStyle(fontHeightInPoints = 16)
@ContentFontStyle(fontHeightInPoints = 16)
public class ExcelEntity {
    @ColumnWidth(20)
    @ExcelProperty("编号")
    private Integer id;
    @ColumnWidth(20)
    @ExcelProperty("姓名")
    private String name;
}
