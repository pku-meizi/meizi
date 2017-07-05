package com.meiziaccess;

import com.meiziaccess.CommandTool.MyHttpUtil;
import com.meiziaccess.model.*;
import com.meiziaccess.model.UploadItem;
import com.meiziaccess.model.UploadItemList;
import com.meiziaccess.model.UploadObject;
import com.meiziaccess.model.UploadRepository;

import com.meiziaccess.upload.UploadTool;
import com.meiziaccess.upload.UploadToolInterface;
import com.meiziaccess.uploadModel.UploadLogRepository;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.HttpRequest;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.util.*;
import com.meiziaccess.secure.*;



@SpringBootApplication
@RestController
@EnableRedisHttpSession
//@EnableScheduling
public class MeiziaccessApplication  {
	@Autowired
	UploadRepository uploadRepository;

//	private int vendor_type = 1;
//登录
	@RequestMapping(value = "/authenticate")
	public Map<String, Object> authenticate(HttpServletRequest request){

		Map<String, Object> model = new HashMap<String, Object>();
		if(request.getSession().getAttribute("username") != null){
			System.out.println("session-get-username=" +  request.getSession().getAttribute("username").toString());
			model.put("status", true);
			return model;
		}

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		JSONObject objData = MyHttpUtil.post(username, password);

		if(objData == null){
			model.put("status", false);
		}else{
			if(objData.getInt("code") ==  200){
				model.put("status", true);
				int vendor_type = objData.getInt("data");
				System.out.println("session-set-username=" + username);
				request.getSession().setAttribute("username", username);
				request.getSession().setAttribute("vendor_type", vendor_type);
				System.out.println(objData.toString());
			}else{
				model.put("status", false);
			}
		}
		return model;
	}

	@RequestMapping(value = "/logout")
	public Map<String, Object> logout(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		if(request.getSession().getAttribute("username") != null){
			System.out.println("session-delete-username=" +  request.getSession().getAttribute("username").toString());
			request.getSession().removeAttribute("username");
			request.getSession().removeAttribute("vendor_type");
			return model;
		}
		model.put("status", false);
		return model;
	}

	@Value("${configure.upload.local_path}")
	private String upload_local_path;


	//xml，视频，关键帧在不同文件夹,home页面读取数据库数据
	@RequestMapping("/data-source-association")
	@ResponseBody
	public Map<String, Object> getItemsAssociation(HttpServletRequest request) {

		// 判断登录没有
		Object o = request.getSession().getAttribute("username");
		Map<String, Object> map = new HashMap<>();
		if (o == null){
			List<UploadItem> uploadstatus = new ArrayList<>();
			map.put("data",uploadstatus);
		}else{
			Object o = request.getSession().getAttribute("vendor_type");
			int vendor_type = Integer.parseInt(o.toString());
			List<UploadItem> uploadstatus = uploadRepository.findByStatusAndVendor_type(0,vendor_type);
			map.put("data",uploadstatus);
		}
		return map;
	}

	/**
	 *
	 * 1 网络台
	 * 2 BTV
	 * 3 南方素材
	 * 4 海外素材
	 * 5 电视剧
     */
	//xml，视频，关键帧在不同文件夹,home页面刷新按钮扫描数据
	@RequestMapping("/data-refresh-association")
	@ResponseBody
	public boolean refreshItemsAssociation(HttpServletRequest request) {
		System.out.println("查找路径 " + upload_local_path);
		Object o = request.getSession().getAttribute("vendor_type");
		if (o == null) {
			return false;
		}
		int vendor_type = Integer.parseInt(o.toString());
		List<UploadItem> list = UploadTool.getUploadItemsAssociation(upload_local_path, vendor_type);

		/* search in database*/
//		System.out.println("查找数据库 ");
//		List<UploadItem> uploadList = uploadRepository.findAll();
//		list.removeAll(uploadList);
//		map.put("data", list);

		System.out.println("查找后的list数据保存到数据库");
		System.out.println(list.get(0).getPath());
		if (list.isEmpty()) {
			System.out.println("查到的是空");
		} else{
			for (int i = 0; i < list.size(); i++) {

				if (uploadRepository.findByTitle(list.get(i).getTitle()).isEmpty()) {
					list.get(i).setUpload_time(new Date());
					list.get(i).setUpload(true);
					list.get(i).setVendor_type(vendor_type);
					uploadRepository.save(list.get(i));
				} else {
					System.out.println("有了");
				}
			}
	}
		return true;
	}

	@Value("${configure.upload.remote_path}")
	String upload_remote_path;

	@Autowired
	UploadLogRepository uploadLogRepository;

	@Value("${configure.upload.vendor_name}")
	String vendor_name;

	@Value("${configure.local.vendor_path}")
	String vendor_path;

	@Value("${configure.upload.uploader_name}")
	String uploader_name;

	@Value("${configure.upload.trans_path}")
	String trans_path;

	@Value("${configure.upload.play_path}")
	String play_path;

	public boolean updateDatabase(List<UploadItem> list){
		for(UploadItem item : list){
			item.setUpload_time(new Date());
			item.setUpload(true);
			uploadRepository.save(item);
		}
		return true;
	}
//	public boolean upField(List<UploadItem> li){
//		for (UploadItem item : li){
//			item.setUpload_time(new Date());
//			item.setStatus(1);
//
//		}
//		return true;
//	}

	@RequestMapping(value = "/upload-association", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String, Object> uploadItemsAssociation(@RequestBody UploadItem item, HttpServletRequest request) {

		Map<String, Object> map = new HashMap<>();

		if (item == null){
			map.put("status", false);
			return map;
		}

		List<UploadItem> list = new ArrayList<>();
		list.add(item);

		UploadToolInterface tool = new UploadTool();
		Object o = request.getSession().getAttribute("vendor_type");
		if(o == null){
			map.put("status", false);
			return map;
		}

		//先将上传的数据写入本地数据库，不在页面显示
		item.setStatus(1);
		updateDatabase(list);

		//然后上传到远程服务器
		int vendor_type = Integer.parseInt(o.toString());
		tool.uploadItemDirsAssociation(upload_remote_path, list, uploadLogRepository, ""+vendor_type, vendor_path, uploader_name, trans_path, play_path);
		map.put("status", true);
		return map;
	}

	public static void main(String[] args) {
		SpringApplication.run(MeiziaccessApplication.class, args);
	}

	//删除实体文件
	@RequestMapping(value = "/delete-association", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
	@ResponseBody
	public  Map<String, Object> deleteItemsAssociation(@RequestBody UploadItem item, HttpServletRequest request) {
		Map<String , Object> map = new HashedMap();
		if (item == null){
			map.put("status", false);
			return map;
		}

		List<UploadItem> list = new ArrayList<>();
		list.add(item);

        for (int i=0;i<list.size();i++){
            uploadRepository.delete(list.get(i).getId());
        }
		UploadToolInterface tool = new UploadTool();

		//获取到要删除的素材信息，进行删除
		tool.deleteItemDirsAssociation(item);
		map.put("status", true);
		return map;
	}


	//已上传页面数据
	@RequestMapping( "/end_data" )
	public Map<String, Object> home_end(HttpServletRequest request){
		Object o = request.getSession().getAttribute("vendor_type");
		int vendor_type = Integer.parseInt(o.toString());
		List<UploadItem> uploadItems = uploadRepository.findByStatusAndVendor_type(1, vendor_type);
		Map<String, Object> map = new HashMap<>();
		map.put("data",uploadItems);
		return map;
	}

	@Autowired
	ItemMediaRepository itemMediaRepository;

	//订单页面数据
	@RequestMapping("/indent_data")
	public Map<String, Object> indent(){
		List<ItemMedia> itemMedias = itemMediaRepository.findAll();
		Map<String, Object> map = new HashMap<>();
		map.put("data",itemMedias);
		return map;
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename="+new Date().toLocaleString()+".xls");
		List<ItemMedia> list = itemMediaRepository.findAll();
		Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), ItemMedia.class, list);
		workbook.write(response.getOutputStream());
	}

}
