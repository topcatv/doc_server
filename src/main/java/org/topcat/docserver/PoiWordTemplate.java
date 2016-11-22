package org.topcat.docserver;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by topcat on 2016/11/21.
 */
public class PoiWordTemplate implements WordTemplate {
    ;

    @Override
    public void fromTemplate(InputStream input, OutputStream output, Map<String, String> context) {
        if (CollectionUtils.isEmpty(context) || input == null || output == null) {
            return;
        }
        Set<String> keySet = context.keySet();
        try {
            XWPFDocument doc = new XWPFDocument(input);
            for (XWPFParagraph p : doc.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        for (String contextKey :
                                keySet) {
                            String key = String.format("$%s", contextKey);
                            String value = context.get(contextKey);
                            if (text != null && text.contains(key)) {
                                text = text.replace(key, value);
                                r.setText(text, 0);
                            }
                        }
                    }
                }
            }
            for (XWPFTable tbl : doc.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun r : p.getRuns()) {
                                String text = r.getText(0);
                                for (String contextKey :
                                        keySet) {
                                    String key = String.format("$%s", contextKey);
                                    String value = context.get(contextKey);
                                    if (text.contains(key)) {
                                        text = text.replace(key, value);
                                        r.setText(text);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            doc.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        Map<String, String> context = new HashMap<>();
        context.put("name", "我");
        new PoiWordTemplate().fromTemplate(new FileInputStream("/Users/topcat/Downloads/维护组关于各网站安全性加固方案（初稿）.docx"), new FileOutputStream("/Users/topcat/Downloads/output.docx"), context);
    }
}
