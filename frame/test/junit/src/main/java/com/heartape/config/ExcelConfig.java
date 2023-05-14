package com.heartape.config;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.AbstractVerticalCellStyleStrategy;

public class ExcelConfig extends AbstractVerticalCellStyleStrategy {
    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        return super.headCellStyle(head);
    }
}
