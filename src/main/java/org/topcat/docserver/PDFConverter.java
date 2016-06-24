package org.topcat.docserver;

import java.io.File;

public interface PDFConverter {
	public void convert2PDF(String inputFile, String pdfFile);
	public void convert2PDF(File inputFile, String pdfFile);
}
