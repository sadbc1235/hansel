package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import model.Member;
//import service.MemberMybatisDAO;
import service.MemberMybatisDAO;

@Controller
@RequestMapping("/member/")
public class MemberController {
	
	HttpServletRequest request;
	Model m;
	HttpSession session;

	   

	@ModelAttribute
	void init(HttpServletRequest request, Model m) {
      this.request = request;
      this.m = m;
      this.session = request.getSession();
	}
	
	@Autowired
	MemberMybatisDAO md;
	
//	메인 페이지
	@RequestMapping("index")
	public String index() throws Exception {
		
		return "index";
	}
	
//	로그인 페이지
	@RequestMapping("signIn")
	public String signIn() throws Exception {
		
		return "member/signIn";
	}
	
	@RequestMapping("signInPro")
	public String signInPro(String userId, String pwd) throws Exception {
		
		HttpSession session = request.getSession(); 
		
		String msg = "";
		String url = "/member/signIn";
		
//		로그인할 유저를 입력한 아이디로 가져옴
		Member mem = md.selectOne(userId);
//		유저의 유무 확인
		if(mem != null) {
//			현재 입력한 비밀번호와 db에 있는 비밀번호 비교
			if(pwd.equals(mem.getPwd())) {
				session.setAttribute("userId", userId);
				mem.setLogin(0);
				md.updateLogin(mem);
				msg = mem.getUserId() + "님이 로그인 하였습니다.";
				url = "/member/index";
			} else {
				msg ="비밀번호가 틀립니다.";
			}
		} else {
			msg = "유효하지 않은 회원입니다.";
		}
		
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		return "alert";
	}
	
//	회원가입 페이지
	@RequestMapping("signUp")
	public String signUp() throws Exception {
		
		return "member/signUp";
	}

	@RequestMapping("signUpPro")
	public String signUpPro(String pwd, String pwdOk, String userId, Member mem) throws Exception {
//		한글 인코딩
		request.setCharacterEncoding("utf-8");
		
		String msg = "비밀번호와 비밀번호확인 이 일치 하지 않습니다.";
		String url = "/member/signUp";
		
		mem = md.selectOne(userId);
		
//		아이디 유효성 확인
		if(mem == null) {
	//		비밀번호 확인 false 면 회원가입 페이지
			if(pwd.equals(pwdOk)) {
				
	//			member 의 setter에 각각 정보 입력
				mem = new Member();
				mem.setUserId(request.getParameter("userId"));
				mem.setPwd(request.getParameter("pwd"));
				mem.setAddress(request.getParameter("address"));
				mem.setEmail(request.getParameter("email"));
				mem.setTel(request.getParameter("tel"));
				mem.setPetName(request.getParameter("petName"));
				mem.setLogin(1);
				mem.setUserType(0);
				mem.setUserReport(0);
				
				int num = md.insertUser(mem);
				
				if(num > 0) {
					msg = mem.getUserId() + "님의 가입이 완료되었습니다.";
					url = "/member/signIn";
				} else {
					msg = "회원가입을 실패 했습니다.";
					url = "/member/signUp";
				}
			}
		} else {
			msg = "이미 있는 아이디 입니다.";
			url = "/member/signUp";
		}
		
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		return "alert";
	}

	
//	로그아웃
	@RequestMapping("logout")
	public String logout() throws Exception {
		
		HttpSession session = request.getSession(); 
		
		String userId = (String)session.getAttribute("userId");
		Member mem = md.selectOne(userId);
		mem.setLogin(1);
		md.updateLogin(mem);
		
		session.invalidate();
		
		String msg = userId + "님이 로그아웃 되었습니다.";
		String url = "/member/index"; 
		
		request.setAttribute("msg", msg);
		request.setAttribute("url", url);
		
		return "alert";
	}
	
//	이메일 페이지
	@RequestMapping("email")
	public String sendEmail() throws Exception {
		
		return "member/email";
	}
	
}





















