package org.topcat.docserver;

import java.io.File;

public interface PDFConverter {
	void convert2PDF(String inputFile, String pdfFile);
	void convert2PDF(File inputFile, String pdfFile);
}
