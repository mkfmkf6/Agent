package com.agent.service.impl;

import com.agent.entity.employees.Employees;
import com.agent.mapper.EmployeeMapper;
import com.agent.result.Result;
import com.agent.service.DocumentService;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private EmployeeMapper employeeMapper;

    private static final String DOCUMENT_DIR = "documents";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    private void createDirectoryIfNotExists() throws IOException {
        Path dirPath = Paths.get(DOCUMENT_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    private String getGenderName(Integer gender) {
        return gender == 1 ? "男" : (gender == 2 ? "女" : "未知");
    }

    private XWPFParagraph createTitle(XWPFDocument document, String title) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(title);
        run.setBold(true);
        run.setFontSize(18);
        run.setFontFamily("宋体");
        return paragraph;
    }

    private XWPFParagraph createSubtitle(XWPFDocument document, String subtitle) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingAfter(100);
        XWPFRun run = paragraph.createRun();
        run.setText(subtitle);
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("宋体");
        return paragraph;
    }

    private XWPFParagraph createText(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingAfter(50);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("宋体");
        return paragraph;
    }

    private XWPFParagraph createListItem(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingAfter(30);
        paragraph.setIndentationFirstLine(420);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setFontFamily("宋体");
        return paragraph;
    }

    private XWPFTable createTable(XWPFDocument document, String[] headers, String[][] data) {
        XWPFTable table = document.createTable(data.length + 1, headers.length);
        table.setWidth("100%");

        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = table.getRow(0).getCell(i);
            cell.setColor("D3D3D3");
            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            run.setText(headers[i]);
            run.setBold(true);
            run.setFontSize(12);
            run.setFontFamily("宋体");
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        }

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < headers.length; col++) {
                XWPFTableCell cell = table.getRow(row + 1).getCell(col);
                XWPFParagraph paragraph = cell.getParagraphs().get(0);
                XWPFRun run = paragraph.createRun();
                run.setText(data[row][col]);
                run.setFontSize(12);
                run.setFontFamily("宋体");
                paragraph.setAlignment(ParagraphAlignment.CENTER);
            }
        }

        return table;
    }

    @Override
    public Result<String> generateOnboardingMaterials(Integer empId) {
        Employees employee = employeeMapper.selectById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        try {
            createDirectoryIfNotExists();

            String fileName = "入职材料_" + employee.getName() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".docx";
            String filePath = DOCUMENT_DIR + "/" + fileName;

            try (XWPFDocument document = new XWPFDocument()) {
                createTitle(document, "新员工入职材料");

                createSubtitle(document, "一、员工基本信息");
                createText(document, "工号：" + employee.getEmpNo());
                createText(document, "姓名：" + employee.getName());
                createText(document, "性别：" + getGenderName(employee.getGender()));
                createText(document, "部门：" + employee.getDepartment());
                createText(document, "岗位：" + employee.getPosition());
                createText(document, "职级：" + employee.getJobLevel());
                createText(document, "入职日期：" + LocalDate.now().format(DATE_FORMATTER));
                createText(document, "联系电话：" + employee.getPhone());
                createText(document, "企业邮箱：" + employee.getEmail());

                createSubtitle(document, "二、入职材料清单");
                createListItem(document, "□ 劳动合同（一式两份）");
                createListItem(document, "□ 保密协议（一式两份）");
                createListItem(document, "□ 员工手册签收单");
                createListItem(document, "□ 入职登记表");
                createListItem(document, "□ 身份证复印件（正反面）");
                createListItem(document, "□ 学历证书复印件");
                createListItem(document, "□ 离职证明（如有）");
                createListItem(document, "□ 社保卡复印件");
                createListItem(document, "□ 银行卡复印件");
                createListItem(document, "□ 社保公积金开户申请表");

                createSubtitle(document, "三、账号开通确认");
                String email = employee.getEmail();
                if (email == null || email.isEmpty()) {
                    email = employee.getName() + "@company.com";
                }
                createListItem(document, "□ 企业邮箱：" + email);
                createListItem(document, "□ OA系统账号");
                createListItem(document, "□ VPN账号");
                createListItem(document, "□ 门禁卡");
                createListItem(document, "□ 工位电脑");

                createSubtitle(document, "四、培训安排");
                createListItem(document, "□ 公司制度培训");
                createListItem(document, "□ 部门业务培训");
                createListItem(document, "□ 岗位技能培训");

                createSubtitle(document, "五、经办人签字");
                createText(document, "HR经办人：________________________");
                createText(document, "日期：________年______月______日");
                createText(document, " ");
                createText(document, "员工签字：________________________");
                createText(document, "日期：________年______月______日");

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    document.write(fos);
                }

                return Result.success("入职材料已生成！\n文件路径：" + filePath);
            }
        } catch (IOException e) {
            return Result.error("生成入职材料失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> generateOffboardingList(Integer empId) {
        Employees employee = employeeMapper.selectById(empId);
        if (employee == null) {
            return Result.error("员工不存在");
        }

        try {
            createDirectoryIfNotExists();

            String fileName = "离职清单_" + employee.getName() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".docx";
            String filePath = DOCUMENT_DIR + "/" + fileName;

            try (XWPFDocument document = new XWPFDocument()) {
                createTitle(document, "员工离职交接清单");

                createSubtitle(document, "一、员工基本信息");
                createText(document, "工号：" + employee.getEmpNo());
                createText(document, "姓名：" + employee.getName());
                createText(document, "部门：" + employee.getDepartment());
                createText(document, "岗位：" + employee.getPosition());
                createText(document, "离职日期：" + (employee.getResignDate() != null ? employee.getResignDate().format(DATE_FORMATTER) : LocalDate.now().format(DATE_FORMATTER)));

                createSubtitle(document, "二、工作交接清单");
                createListItem(document, "□ 工作内容交接说明");
                createListItem(document, "□ 项目文档移交");
                createListItem(document, "□ 客户资料移交");
                createListItem(document, "□ 待办事项清单");
                createListItem(document, "□ 工作账号密码（系统、邮箱等）");
                createListItem(document, "□ 其他工作相关资料");

                createSubtitle(document, "三、资产归还清单");
                createTable(document, new String[]{"物品名称", "规格型号", "数量", "状态", "接收人"},
                        new String[][]{
                                {"笔记本电脑", "", "1", "□正常 □损坏", ""},
                                {"显示器", "", "1", "□正常 □损坏", ""},
                                {"键盘", "", "1", "□正常 □损坏", ""},
                                {"鼠标", "", "1", "□正常 □损坏", ""},
                                {"电源适配器", "", "1", "□正常 □损坏", ""},
                                {"门禁卡", "", "1", "□已归还", ""},
                                {"工牌", "", "1", "□已归还", ""},
                                {"其他资产", "", "", "", ""}
                        });

                createSubtitle(document, "四、账号注销清单");
                createListItem(document, "□ 企业邮箱账号");
                createListItem(document, "□ OA系统账号");
                createListItem(document, "□ VPN账号");
                createListItem(document, "□ 内部系统账号");
                createListItem(document, "□ 其他系统账号");

                createSubtitle(document, "五、财务结算");
                createListItem(document, "□ 工资结算确认");
                createListItem(document, "□ 报销款项结清");
                createListItem(document, "□ 借款还清");
                createListItem(document, "□ 其他财务事项");

                createSubtitle(document, "六、交接确认");
                createText(document, "");
                createText(document, "本人确认已完成以上所有交接事项，并归还所有公司资产。");
                createText(document, "");
                createText(document, "交接人签字：________________________");
                createText(document, "日期：________年______月______日");
                createText(document, " ");
                createText(document, "接收人签字：________________________");
                createText(document, "日期：________年______月______日");
                createText(document, " ");
                createText(document, "部门负责人签字：________________________");
                createText(document, "日期：________年______月______日");
                createText(document, " ");
                createText(document, "HR经办人签字：________________________");
                createText(document, "日期：________年______月______日");

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    document.write(fos);
                }

                return Result.success("离职清单已生成！\n文件路径：" + filePath);
            }
        } catch (IOException e) {
            return Result.error("生成离职清单失败：" + e.getMessage());
        }
    }
}