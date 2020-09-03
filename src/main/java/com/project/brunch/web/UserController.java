package com.project.brunch.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.brunch.config.auth.LoginUserAnnotation;
import com.project.brunch.config.auth.dto.LoginUser;
import com.project.brunch.domain.user.User;
import com.project.brunch.domain.user.UserRepository;
import com.project.brunch.service.UserService;
import com.project.brunch.service.crawling.user.NowCrawling;
import com.project.brunch.web.dto.user.UserNavProfileRespDto;
import com.project.brunch.web.dto.user.UserProfileRespDto;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "/*")
@RequestMapping("brunch")
@RequiredArgsConstructor
public class UserController {

	private String TAG = "< UserController > ";
	private final UserRepository userRepository;
	private final UserService userService;

	// 로그인한 유저 정보 뿌리기 (nav bar)
	@GetMapping("/user/loginUser")
	public UserNavProfileRespDto userNavProfile(@LoginUserAnnotation LoginUser loginUser) {
		System.out.println(TAG + "userLogin() loginUser 아이디 확인 : " + loginUser.getId());
		UserNavProfileRespDto userNavProfileRespDto = userService.로그인유저찾기(loginUser);
		return userNavProfileRespDto;
	}

	// 로그인한 유저 프로필 업데이트하기
	@PutMapping("/user/profileEdit")
	public UserProfileRespDto userProfileEdit(@RequestBody Map<String, Object> data,
			@LoginUserAnnotation LoginUser loginUser) {
		UserProfileRespDto userProfileRespDto = userService.프로필수정하기(data, loginUser);
		return userProfileRespDto;
	}

	// 리액트 메인 페이지에 User 뿌리기 + 안드로이드 브런치나우 태그별 작가 목록 뿌리기
	@GetMapping("/user/list")
	public List<User> userList() {
		return userRepository.findAll();
	}

	// 브런치 유저 크롤링 -> 추후 삭제
	@GetMapping("/user/craw/save")
	public @ResponseBody String userSave(NowCrawling nowCrawling) {
		List<User> users;
		try {
			users = nowCrawling.getNowCrawling();
			userRepository.saveAll(users);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "크롤링 유저 저장완료";
	}

//	@PostMapping("/saveadmin")
//	public String saveAdmin() {
//		List<User> adminUser = null;
//		User admin = User.builder()
//				.email("admin.brunch.co.kr")
//				.profileImage("https://img1.daumcdn.net/thumb/C500x500.fpng/?fname=http://t1.daumcdn.net/brunch/service/user/2xr/image/lylAHyGq9DgVNt5QFKYhQBIYPko.png")
//				.nickName("admin")
//				.userRole(UserRole.ADMIN)
//				.build();
//		adminUser.add(admin);
//		
//		userRepository.saveAll(adminUser);
//		
//		return "어드민 저장완료";
//	}
	
}
