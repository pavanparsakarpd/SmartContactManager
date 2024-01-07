package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.services.EmailService;

@Controller
public class ForgotController {
	Random random = new Random();
	@Autowired
	private EmailService emailService;
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,HttpSession session) {
		System.out.println("email"+email);
		
		int otp = random.nextInt(9999999);
		System.out.println("ramdom "+otp);
		
		//send otp over Email here........
		String subject="Otp from SCM";
		String message="<h1>OTP="+otp+"</h1>";
		String to=email;
		boolean flag  = this.emailService.sendEMail(subject, message, email);
		if(flag) {
			return "verify_otp";
			
		}else {
			session.setAttribute("message", "check your email id");
			return "forgot_email_form";
		}
		
	}

}
