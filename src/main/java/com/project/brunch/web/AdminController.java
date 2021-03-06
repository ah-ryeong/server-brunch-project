package com.project.brunch.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.brunch.domain.comment.CommentRepository;
import com.project.brunch.domain.post.Post;
import com.project.brunch.domain.post.PostRepository;
import com.project.brunch.domain.user.User;
import com.project.brunch.domain.user.UserRepository;
import com.project.brunch.domain.user.UserRole;
import com.project.brunch.service.admin.AdminCommentService;
import com.project.brunch.service.admin.AdminPostService;
import com.project.brunch.service.admin.AdminUserService;
import com.project.brunch.service.crawling.user.NowCrawling;
import com.project.brunch.util.GoogleMailSend;
import com.project.brunch.util.MyPage;
import com.project.brunch.util.PagingList;
import com.project.brunch.web.dto.admin.AdminDto;
import com.project.brunch.web.dto.admin.AdminSearchDto;
import com.project.brunch.web.dto.admin.CommentDto;
import com.project.brunch.web.dto.post.PostRespDto;

import lombok.RequiredArgsConstructor;

@Controller
@CrossOrigin(origins = "/*")
@RequestMapping("brunch")
@RequiredArgsConstructor
public class AdminController {

	private static final Logger log = LoggerFactory.getLogger(AdminController.class);

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final AdminUserService adminUserService;
	private final AdminPostService adminPostService;
	private final PagingList pagingList;
	private final AdminCommentService adminCommentService;

	private PostRespDto postDto;
	private GoogleMailSend googleMailSend;
	public static String useremail;

	// ????????? ?????????
	@GetMapping("/admin/login")
	public String adminLoginForm() {
		log.info("/admin/loginForm ??????");
		return "login";
	}

	// ????????? ?????? ????????????
	@GetMapping("/admin")
	public String adminDashForm(Model model) {
		AdminDto adminDto = adminUserService.??????Count();
		List<AdminDto> readCountRank = adminPostService.readCountRank????????????();
		for (int i = 0; i < readCountRank.size(); i++) {
			readCountRank.get(i).setRank(i + 1);
		}
		List<AdminDto> updatePost = adminPostService.?????????????????????();
		for (int i = 0; i < updatePost.size(); i++) {
			updatePost.get(i).setRank(i + 1);
		}
		List<AdminDto> likeCountRank = adminPostService.likeCountRank????????????();
		for (int i = 0; i < likeCountRank.size(); i++) {
			likeCountRank.get(i).setRank(i + 1);
		}

		model.addAttribute("adminDto", adminDto)
			.addAttribute("readCountRank", readCountRank)
			.addAttribute("updatePost", updatePost)
			.addAttribute("likeCountRank", likeCountRank);

		return "dashboard";
	}
	
	@PutMapping("/brunch/admin/main/{id}")
	public List<Post> mainPostUpate(@PathVariable int id) {
		List<Post> postBoolean = adminPostService.??????????????????????????????();
		
		return postBoolean;
	}

	// ????????? ?????? ?????? ????????? - ?????????
//	@GetMapping("/admin/user")
//	public String userList(Model model, @RequestParam(value = "page", defaultValue = "1") Integer pageNum) {
//		List<AdminSearchDto> adminSearchDto = adminUserService.??????????????????(pageNum);
//		Integer[] pageList = pagingList.???????????????????????????(pageNum);
//		
//		model.addAttribute("userlist", adminSearchDto);
//		model.addAttribute("pagelist", pageList);
//		
//		return "user";
//	}
	
	// ?????? ????????? ?????????
	@GetMapping("/admin/user")
	public String userListTest(
			Model model, 
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		UserRole userRole = userRepository.findAll().get(0).getUserRole().USER;
		Page<User> users = userRepository.findByUserRole(userRole, pageable);
		model.addAttribute("users", users);
		List<MyPage> lists = new ArrayList<MyPage>();
		for (int i=1; i<users.getTotalPages(); i++) {
			lists.add(new MyPage(i));
		}
		model.addAttribute("lists", lists);
		return "user";
	}

	// ????????? ?????? ?????? ????????????
	@GetMapping("/admin/user/search")
	public String adminUserSearch(@RequestParam(value="keyword") String keyword, Model model) {
		List<AdminSearchDto> adminSearchDto = adminUserService.??????????????????(keyword);
		model.addAttribute("search", adminSearchDto);
		return "user";
	}

	// ????????? ?????? ?????? - ?????? ?????? ????????? ??????
	@DeleteMapping("/admin/user/{id}")
	public @ResponseBody int adminUserDelete(@PathVariable int id) {
		// ????????? ????????????
		useremail = adminUserService.???????????????(id);
		googleMailSend = new GoogleMailSend();
		googleMailSend.sendMail(useremail);
		adminUserService.????????????(id);
		return id;
	}

	// ????????? ????????? ?????? ????????? - ????????? 
	@GetMapping("/admin/post")
	public String postList(Model model, @RequestParam(value = "page", defaultValue = "1") Integer pageNum) {
		List<AdminSearchDto> adminSearchDto = adminPostService.?????????????????????(pageNum);
		Integer[] pageList = pagingList.??????????????????????????????(pageNum);
		
		model.addAttribute("postlist", adminSearchDto);
		model.addAttribute("pagelist", pageList);
		
		return "post";
	}
	
	// ????????? ????????? ?????? - ?????? ?????? ????????? ?????????
	@DeleteMapping("/admin/post/{id}")
	public @ResponseBody int adminPostDelete(@PathVariable int id) {
		adminPostService.delete(id);

		return id;
	}

	// ????????? ????????? ????????????
	@GetMapping("/admin/post/search")
	public String adminPostSearch(@RequestParam(value="keyword") String keyword, Model model) {
		List<AdminSearchDto> adminSearchDto = adminPostService.?????????????????????(keyword);
		model.addAttribute("search", adminSearchDto);
		return "post";
	}
	
	// ????????? ?????? ????????? ????????? ?????? (???????????????????????????)

	@GetMapping("/admin/main")
	public String adminMainForm(Model model) {
		List<AdminDto> getMainPost = adminPostService.???????????????????????????();
		model.addAttribute("getMainPost", getMainPost);
		
		System.out.println("AdminController : mainpost : " + getMainPost);
		return "main";
	}

	// ????????? ?????? ????????? ????????? ??????
	@GetMapping("/admin/comment")
	public String adminCommnetForm(Model model) {
		
		List<CommentDto> comments = adminCommentService.getCommentList();
		model.addAttribute("comments", comments);
		
		return "comment";
	}
	
	@GetMapping("/save")
	   public @ResponseBody String userSave(NowCrawling nowCrawling) {
	      List<User> users;
	      try {
	         users = nowCrawling.getNowCrawling();
	         userRepository.saveAll(users);
	         return "????????? ?????? ????????????";
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	      return null;
	   }
}