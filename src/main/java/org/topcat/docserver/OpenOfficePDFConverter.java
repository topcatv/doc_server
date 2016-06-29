package org.topcat.docserver;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.File;

public class OpenOfficePDFConverter implements PDFConverter {

	private static OfficeManager officeManager;
	private static String office_home = "/Applications/LibreOffice.app/Contents/";

	private static int port[] = { 8100 };

	public static void setOffice_home(String office_home) {
		OpenOfficePDFConverter.office_home = office_home;
	}

	public void convert2PDF(String inputFile, String pdfFile) {
		convert2PDF(new File(inputFile), pdfFile);
	}

	public static void main(String[] args) {
		OpenOfficePDFConverter openOfficePDFConverter = new OpenOfficePDFConverter();
		openOfficePDFConverter.convert2PDF("/Users/topcat/Downloads/6-6平台改版.docx");
	}

	public void convert2PDF(String inputFile) {
		String pdfFile = inputFile + ".pdf";
		convert2PDF(inputFile, pdfFile);
	}

	public void startService() {
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		try {
			System.out.println("准备启动服务....");
			configuration.setOfficeHome(office_home);// 设置OpenOffice.org安装目录
			configuration.setPortNumbers(port); // 设置转换端口，默认为8100
			configuration.setTaskExecutionTimeout(1000 * 60 * 5L);// 设置任务执行超时为5分钟
			configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24L);// 设置任务队列超时为24小时

			officeManager = configuration.buildOfficeManager();
			officeManager.start(); // 启动服务
			System.out.println("office转换服务启动成功!");
		} catch (Exception ce) {
			ce.printStackTrace();
			System.out.println("office转换服务启动失败!详细信息:" + ce);
		}
	}

	public void stopService() {
		System.out.println("关闭office转换服务....");
		if (officeManager != null) {
			officeManager.stop();
		}
		System.out.println("关闭office转换成功!");
	}

	@Override
	public void convert2PDF(File inputFile, String pdfFile) {
		startService();
		System.out.println("进行文档转换转换:" + inputFile.getAbsolutePath() + " --> "
				+ pdfFile);
		OfficeDocumentConverter converter = new OfficeDocumentConverter(
				officeManager);
		converter.convert(inputFile, new File(pdfFile));
		stopService();
		System.out.println();
	}
}