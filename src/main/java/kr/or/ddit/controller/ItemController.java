package kr.or.ddit.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.vo.ItemVO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;

@RequestMapping("/item")
@Slf4j
@Controller
public class ItemController {
	
	private String uploadPath = "C:\\eclipse-jee-2020-06-R-win32-x86_64\\workspace\\springProduct\\src\\main\\webapp\\resources\\upload"; 
	
	//상품 등록 폼
	@GetMapping("/register")
	public String registerForm() {
		return "item/register";
	}
	
	//상품등록 실행
	@PostMapping("/register")
	public String register(ItemVO itemVO,Model model) {
		/*
		 * ItemVO: ItemVO [itemId=0, itemName=우산, price=10000, description=비를 피해
		 *  ,pictureUrl=null, pictureUrl2=null
		 *  ,picture=org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@326ba3d4
		 *   ,itemAttachVO=null]
		 */
		log.info("ItemVO: "+itemVO.toString());
		
		MultipartFile file = itemVO.getPicture();
			
		//파일이름
		log.info("originalName: "+ file.getOriginalFilename());
		//파일크기
		log.info("size: "+file.getSize());
		//MIME TYPE는?
		log.info("contentType: "+file.getContentType());
		
		String createdFileName = uploadFile(file.getOriginalFilename(),file);
		
		model.addAttribute("msg","등록이 완료되었습니다.");
		//forwarding
		return "item/success";
	}
	
	@GetMapping("/uploadForm")
	public String uploadForm() {
		return "item/uploadForm";
	}
	
	//다중 이미지 업로드
	//요청 URI : /uploadFormAction
	//jsp name과 파라미터의 name은 같아야 한다.
	@PostMapping("/uploadFormAction")
	public String uploadFormAction(MultipartFile[] uploadFile,Model model) {
		//MultipartFile: 스프링에서 제공해주는 타입
		
		/*
		 	--잘 쓰는 방법
		 	String...= getOriginalFileName(): 업로드 되는 파일의 이름(실제파일명)
		 	boolean... = file.isEmpty() : 파일이 없다면 true
		 	long... = file.getSize() : 업로드 되는 파일의 크기
		 	file.transferTo(File file) : 파일을 저장
		 	
		 	--잘 안씀
		 	String ...= file.getName();
		 	byte[]... = file.getBytes(); : byte[]로 데이터 변환
		 	InputStream ... =getInputStream(): 파일 데이터와 연결된 InputStream을 반환
		 */
		//make folder 시작	연/월/일----------------------
		File uploadFolder = new File(uploadPath,getFolder());
		log.info("uploadFolder: "+uploadFolder);
		
		//만약에 ... upload>연>월>일 폴더가 없다면 생성
		if(uploadFolder.exists()==false) {
			uploadFolder.mkdirs();
		}
		
		//make folder 끝----------------------
		for (MultipartFile multipartFile : uploadFile) {
			log.info("-------------");
			//이미지명
			log.info("upload File Name: "+multipartFile.getOriginalFilename());
			log.info("upload File Size: "+multipartFile.getSize());
			//서버의 어느 폴더로 복사할 건지?
			//파일명은 어떻게 되는지?
			String uploadFileName = multipartFile.getOriginalFilename();
			//순수한 파일명만 가져오기
			uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
			log.info("only file name: "+uploadFileName);
			
			//---------------같은날 같은 이미지를 업로드 시 파일 중복 방지 시작---------------------
			//랜덤값 생성
			UUID uuid = UUID.randomUUID();
			//원래 파일 이름과 구분하기 위해 _를 붙임
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			//---------------같은날 같은 이미지를 업로드 시 파일 중복 방지 끝-----------------------
			//File 객체 설계(복사할 대상 경로(서버쪽), 파일명(UUid가 붙은)
			//저장경로,uploadPath\\2022\\10\\28
			File saveFile = new File(uploadFolder,uploadFileName);
			
			//파일 복사 실행
			try {
				multipartFile.transferTo(saveFile);
				
				//썸네일 처리 시작------------------------
				//파일 타입 이미지 여부 체크
				if(checkImageType(saveFile)) {
					FileOutputStream thumnail = new FileOutputStream(
								new File(uploadFolder,"s_"+uploadFileName)
							);
					Thumbnailator.createThumbnail(multipartFile.getInputStream(),thumnail,100,100);
					thumnail.close();
					
				}
				//썸네일 처리 끝------------------------
			} catch (IllegalStateException | IOException e) {
				log.error(e.getMessage());
				
			}
		}
		model.addAttribute("msg","success");
		return "item/success";
	}
	
	//이미지인지 체크(썸네일용)
	//모바일과 같은 환경에서 많은 데이터를 소비해야 하므로
	//이미지의 경우 특별한 경우가 아니면 섬네일을 제작해줘야함
	public boolean checkImageType(File file) {
		/*
		 	jpeg/ jpg(jpeg 이미지)의 MIME 타입: image/jpeg
		 	//MIME 타입을 통해 이미지 여부 확인
		 	file.toPath() : 파일 객체를 Path객체로 변화
		 */
		String contentType;
		try {
			contentType = Files.probeContentType(file.toPath());
			log.info("contentType: "+contentType);
			return contentType.startsWith("image");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return false;
	}
	
	
	
	
	//연/월/일 폴더 생성
	public String getFolder() {
		//format 지정
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		
		//날짜 객체 생성
		Date date = new Date();
		String str = sdf.format(date);
		
		return str.replace("-", File.separator);
	}
	
	
	//파일을 복사(client파일 -> 서버로 복사)
	//파라미터 : 파일명,파일객체의 바이트s
	private String uploadFile(String originalName,MultipartFile multipartFile) {
	
		//랜덤수를 생성
		UUID uid = UUID.randomUUID(); 
		
		String createFileName = uid.toString() + "_" + originalName;
		
		//복사 준비
		File target = new File(uploadPath,createFileName);
		
		//복사 실행
		try {
			multipartFile.transferTo(target);
		}catch (IllegalStateException e) {
			log.error(e.getMessage());
		}catch (IOException e) {
			log.error(e.getMessage());
		}
		return createFileName;
	}
}
