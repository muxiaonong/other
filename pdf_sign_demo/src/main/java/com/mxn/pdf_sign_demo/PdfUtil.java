package com.mxn.pdf_sign_demo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

/***
 * pdf 相关操作
 * @author mxn
 */
@Slf4j
@Component
public class PdfUtil {

    /**
     * 合并PDF文件
     * @param files 文件列表
     * @param output 输出的PDF文件
     */
    public void mergeFileToPDF(List<File> files, File output) {
        Document document = null;
        PdfCopy copy = null;
        OutputStream os = null;
        try {
            os = new FileOutputStream(output);
            document = new Document();
            copy = new PdfCopy(document, os);
            document.open();
            for (File file : files) {
                if (!file.exists()) {
                    continue;
                }
                String fileName = file.getName();
                if (fileName.endsWith(".pdf")) {
                    PdfContentByte cb = copy.getDirectContent();
                    PdfOutline root = cb.getRootOutline();
                    new PdfOutline(root, new PdfDestination(PdfDestination.XYZ), fileName
                            .substring(0, fileName.lastIndexOf(".")));
                    // 不使用reader来维护文件，否则删除不掉文件，一直被占用
                    try (InputStream is = new FileInputStream(file)) {
                        PdfReader reader = new PdfReader(is);
                        int n = reader.getNumberOfPages();
                        for (int j = 1; j <= n; j++) {
                            document.newPage();
                            PdfImportedPage page = copy.getImportedPage(reader, j);
                            copy.addPage(page);
                        }
                    } catch(Exception e) {
                        log.warn("error to close file : {}" + file.getCanonicalPath(), e);
//                        e.printStackTrace();
                    }
                } else {
                    log.warn("file may not be merged to pdf. name:" + file.getCanonicalPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
            if (copy != null) {
                copy.close();
            }
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
    }


    /**
     * 将文件转换成byte数组
     * @param file
     * @return
     * **/
    public static byte[] File2byte(File file){
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }



}