package com.mxn.pdf_sign_demo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ITextTestController {
    /**
     * 保存的文件名字
     */
    public static final String SAVE_PATH =  "static/签署合同.pdf";
    /**
     * 临时模板路径
     */
    public static final String TEMP_PATH = "static/itext.pdf";
    /**
     * 合同模板地址
     * 如果你的模板在服务器上，填写服务器的地址也可以
     */
    private String templatePath="static/测试模板.pdf";
    /**
     * 生成后的合同文件
     */
    private String outputFileName ="static/生成好的合同文件.pdf";
    /**
     * 签署完成的合同地址
     */
    private String endPdf = "static/签署测试.pdf";

    /**
     * 创建PDF附件信息
     */
    public static void createPdf() {
        Document doc = null;
        try {
            doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(ResourceUtils.getURL("classpath:").getPath()+TEMP_PATH));
            doc.open();
            BaseFont bfChi = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font fontChi = new Font(bfChi, 8, Font.NORMAL);

            PdfPTable table = new PdfPTable(5);

            Font fontTitle = new Font(bfChi, 15, Font.NORMAL);
            PdfPCell cell = new PdfPCell(new Paragraph("*货运*运输服务协议-附件1 运输费用报价",fontTitle));


            cell.setColspan(5);
            table.addCell(cell);
//			"序号"
            table.addCell(new Paragraph("序号",fontChi));
            table.addCell(new Paragraph("品类",fontChi));
            table.addCell(new Paragraph("名称",fontChi));
            table.addCell(new Paragraph("计算方式",fontChi));
            table.addCell(new Paragraph("费率",fontChi));

            table.addCell(new Paragraph("1",fontChi));
            table.addCell(new Paragraph("货运",fontChi));
            table.addCell(new Paragraph("费率1.0",fontChi));
            table.addCell(new Paragraph("算",fontChi));
            table.addCell(new Paragraph("0~100万-5.7%，上限：500元，下限：20元",fontChi));

            table.addCell(new Paragraph("2",fontChi));
            table.addCell(new Paragraph("货运",fontChi));
            table.addCell(new Paragraph("费率1.0",fontChi));
            table.addCell(new Paragraph("倒",fontChi));
            table.addCell(new Paragraph("100万~200万-5.6%，无上限、下限",fontChi));

            table.addCell(new Paragraph("3",fontChi));
            table.addCell(new Paragraph("货运",fontChi));
            table.addCell(new Paragraph("费率1.0",fontChi));
            table.addCell(new Paragraph("算",fontChi));
            table.addCell(new Paragraph("200万~300万-5.5%，无上限、下限",fontChi));


            doc.add(table);

//			doc.add(new Paragraph("Hello World,看看中文支持不........aaaaaaaaaaaaaaaaa",fontChi));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }

    /**
     * 根据PDF模板生成PDF文件
     * @return
     */
    @GetMapping("generatePdf")
    public String generatePdf() throws Exception{
//        File file = ResourceUtils.getFile("classpath:"+SAVE_PATH);
        File pdfFile = new File(ResourceUtils.getURL("classpath:").getPath()+SAVE_PATH);
        try {
            PdfReader pdfReader;
            PdfStamper pdfStamper;
            ByteArrayOutputStream baos;

            Document document = new Document();
//

            PdfSmartCopy pdfSmartCopy = new PdfSmartCopy(document,
                    new FileOutputStream(pdfFile));

            document.open();

            File file = ResourceUtils.getFile("classpath:"+templatePath);
            pdfReader = new PdfReader(file.getPath());
            int n = pdfReader.getNumberOfPages();
            log.info("页数："+n);
            baos = new ByteArrayOutputStream();
            pdfStamper = new PdfStamper(pdfReader, baos);

            for(int i = 1; i <= n; i++) {
                AcroFields acroFields = pdfStamper.getAcroFields();

                //key statement 1
                acroFields.setGenerateAppearances(true);

                //acroFields.setExtraMargin(5, 5);
                acroFields.setField("customerAddress", "上海市浦东新区田子路520弄1号楼");
                acroFields.setField("customerCompanyName", "上海百度有限公司");
                acroFields.setField("customerName", "张三");
                acroFields.setField("customerPhone", "15216667777");
                acroFields.setField("customerMail", "123456789@sian.com");

                acroFields.setField("vendorAddress", "上海市浦东新区瑟瑟发抖路182号");
                acroFields.setField("vendorCompanyName", "牧小农科技技术有限公司");
                acroFields.setField("vendorName", "王五");
                acroFields.setField("vendorPhone", "15688886666");
                acroFields.setField("vendorMail", "123567@qq.com");

                acroFields.setField("effectiveStartTime", "2021年05月25");
                acroFields.setField("effectiveEndTime", "2022年05月25");

                //true代表生成的PDF文件不可编辑
                pdfStamper.setFormFlattening(true);

                pdfStamper.close();

                pdfReader = new PdfReader(baos.toByteArray());


                pdfSmartCopy.addPage(pdfSmartCopy.getImportedPage(pdfReader, i));
                pdfSmartCopy.freeReader(pdfReader);
                pdfReader.close();
            }
            pdfReader.close();
            document.close();
        } catch(DocumentException dex) {
            dex.printStackTrace();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        //创建PDF文件
        createPdf();


        File file3 = new File(ResourceUtils.getURL("classpath:").getPath()+TEMP_PATH);
        File file1 = new File(ResourceUtils.getURL("classpath:").getPath()+outputFileName);

        List<File> files = new ArrayList<>();
        files.add(pdfFile);
        files.add(file3);

        try {
            PdfUtil pdfUtil = new PdfUtil();
            pdfUtil.mergeFileToPDF(files,file1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //如果你是上传文件服务器上，这里可以上传文件
//        String url = fileServer.uploadPdf(File2byte(file1));

        //删除总文件
        //如果是你本地预览就不要删除了，删了就看不到了
//        if(file1.exists()){
//            file1.delete();
//        }
        //删除模板文件
        if(pdfFile.exists()){
            System.gc();
            pdfFile.delete();
        }
        //删除产品文件
        if(file3.exists()){
            file3.delete();
        }
        return "success";
    }



    /**
     * 签署合同
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    @GetMapping("addContent")
    public String addContent() throws IOException, DocumentException {

        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont);

        //这里可以填写本地地址，也可以是服务器上的文件地址
        PdfReader reader = new PdfReader(ResourceUtils.getURL("classpath:").getPath()+outputFileName);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(ResourceUtils.getURL("classpath:").getPath()+endPdf));

//
        PdfContentByte over = stamper.getOverContent(1);
        ColumnText columnText = new ColumnText(over);

        PdfContentByte over1 = stamper.getOverContent(1);
        ColumnText columnText1 = new ColumnText(over1);

        PdfContentByte over2 = stamper.getOverContent(1);
        ColumnText columnText2 = new ColumnText(over2);


        PdfContentByte over3 = stamper.getOverContent(1);
        ColumnText columnText3 = new ColumnText(over3);
        // llx 和 urx  最小的值决定离左边的距离. lly 和 ury 最大的值决定离下边的距离
        // llx 左对齐
        // lly 上对齐
        // urx 宽带
        // ury 高度
        columnText.setSimpleColumn(29, 117, 221, 16);
        Paragraph elements = new Paragraph(0, new Chunk("上海华为科技有限公司"));

        columnText1.setSimpleColumn(26, 75, 221, 16);
        Paragraph elements1 = new Paragraph(0, new Chunk("2021年03月03日"));

        columnText2.setSimpleColumn(800, 120, 200, 16);
        Paragraph elements2 = new Paragraph(0, new Chunk("北京百度科技有限公司"));

        columnText3.setSimpleColumn(800, 74, 181, 16);
        Paragraph elements3 = new Paragraph(0, new Chunk("2022年03月03日"));

//            acroFields.setField("customerSigntime", "2021年03月03日");
//                acroFields.setField("vendorSigntime", "2021年03月09日");
        // 设置字体，如果不设置添加的中文将无法显示
        elements.setFont(font);
        columnText.addElement(elements);
        columnText.go();

        elements1.setFont(font);
        columnText1.addElement(elements1);
        columnText1.go();

        elements2.setFont(font);
        columnText2.addElement(elements2);
        columnText2.go();

        elements3.setFont(font);
        columnText3.addElement(elements3);
        columnText3.go();

        stamper.close();

        File tempFile = new File(ResourceUtils.getURL("classpath:").getPath()+"签署测试.pdf");

        //如果是你要上传到服务器上，填写服务器的地址

//        String url = fileServer.uploadPdf(File2byte(tempFile));
//        log.info("url："+url);

        //如果是上传服务器后，要删除信息
        //本地不要删除，否则没有文件
//        if(tempFile.exists()){
//            tempFile.delete();
//        }

        return "success";
    }

}
