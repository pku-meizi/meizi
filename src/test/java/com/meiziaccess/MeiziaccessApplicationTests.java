package com.meiziaccess;

import com.meiziaccess.download.DownloadTool;
import com.meiziaccess.download.DownloadToolInterface;
import com.meiziaccess.model.ItemMedia;
import com.meiziaccess.model.ItemMediaRepository;
import com.meiziaccess.service.UploadService;
import com.meiziaccess.upload.UploadTool;
import com.meiziaccess.upload.UploadToolInterface;
import com.meiziaccess.uploadModel.UploadLog;
import com.meiziaccess.uploadModel.UploadLogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MeiziaccessApplication.class)
@WebAppConfiguration
public class MeiziaccessApplicationTests {

	@Autowired
	private UploadLogRepository uploadLogRepository;

	@Autowired
	private ItemMediaRepository itemMediaRepository;

	@Test
	public void test() throws Exception {

		//测试修改数据库
//		UploadToolInterface tool = new UploadTool();
//		System.out.println(tool.updateDatabase("E:\\dianshitai", uploadLogRepository));

		//测试数据库连接
//		Date date = new Date();
//		uploadLogRepository.save(new UploadLog("dianshitai", date, "admin",
//				"/user/meizi/xml", "/user/meizi/video",
//				"/user/derc/vedorVideo", 10, "dianshitaisuoyou"));
//		System.out.println(uploadLogRepository.findAll().get(0).toString());

		//测试读取文件
//		UploadToolInterface tool = new UploadTool();
//		Map<String, String> map = tool.readFile("E:\\dianshitai\\upload.txt");
//		System.out.println(map.get("number"));
//		System.out.println(map.get("price"));
//		System.out.println(map.get("copyright"));

//		//测试上传文件服务
//		UploadService uploadService = new UploadService();
//		uploadService.uploadXmlAndVideo("E:\\dianshitai", uploadLogRepository);
////		uploadService.uploadXmlAndVideo("/Users/lhq/Workspace/dianshitai", uploadLogRepository);

		//测试下载服务
//		DownloadToolInterface downloadTool = new DownloadTool();
//		downloadTool.download();
	}


}
