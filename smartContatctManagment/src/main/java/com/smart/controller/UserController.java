package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepo;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOder;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.razorpay.*;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MyOrderRepo myOrderRepo;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String name = principal.getName();
		System.out.println("Name is:" + name);
		User user = userRepository.getUserByUserName(name);
		System.out.println("Name is:" + user);
		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";
	}

	// add contacts/form Controller
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// proccessing add form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			System.out.println("Contact" + contact);
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			// Image logic for processing

			if (file.isEmpty()) {
				contact.setImage("contact.png");
				System.out.println("File empty");

			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("/static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File Upload succesfully..");
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Your contact is added successfully", "success"));
			System.out.println("User contact is added successfully");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR" + e.getMessage());
			session.setAttribute("message", new Message("Something went wrong", "danger"));
			e.printStackTrace();

		}

		return "normal/add_contact_form";

	}

	// Getting all Contacts Handler
	// pagination per page 5 contacts
	// CUrrent page 0
	@GetMapping("/show-contacts/{page}")
	public String getAllContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
//		m.addAttribute("title","Show Contacts");
//		String userName = principal.getName();
//		User userByUserName = this.userRepository.getUserByUserName(userName);
//		List<Contact> contacts = userByUserName.getContacts();

		// ---------OR-------
		// We can use Contact repository also
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);

		// current page
		// contact per page
		PageRequest pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		System.out.println("Contacts" + contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";

	}

	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal) {
		System.out.println("Contact id" + cId);
		Optional<Contact> contactId = contactRepository.findById(cId);
		Contact contact = contactId.get();
		// Security
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		System.out.println("UserId " + user.getId());
		System.out.println("ContactId " + contact.getUser().getId());
		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getName());

		}
		return "normal/contact_detail";
	}

	// delete controller
	@GetMapping("/delete/{cId}")
	public String deleteContactById(@PathVariable("cId") Integer cId, Model m, HttpSession session,Principal principal
			) {
		Optional<Contact> contactOption = this.contactRepository.findById(cId);
		Contact contact = contactOption.get();
		
		//Delete old photo
		//contact.setUser(null);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		
		//contactRepository.delete(contact);

		session.setAttribute("message", new Message("deleted successfully", "success"));

		return "redirect:/user/show-contacts/0";

	}

	// update controller
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model m) {
		m.addAttribute("title", "Update");
		Contact contact = this.contactRepository.findById(cId).get();
		System.out.println("contacts" + contact);
		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			HttpSession session, Principal principal) {

		try {
			Contact oldDetail = this.contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

				// Delete old Image
				// Update new Image
				File deleteFile = new ClassPathResource("/static/image").getFile();

				File file1 = new File(deleteFile, oldDetail.getImage());
				file1.delete();

				// Update new Image
				File saveFile = new ClassPathResource("/static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				System.out.println("File Upload succesfully..");

			} else {
				contact.setImage(oldDetail.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact Update successfully", "success"));

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Name" + contact.getName());
		System.out.println("Name" + contact.getcId());
		return "redirect:/user/" + contact.getcId() + "/contact";
	}
	
	//profile handler
	
	@GetMapping("/profile")
	public String yourProfile(Model model){
		model.addAttribute("title","Profile PAge");
		
		return "normal/profile";
	}
	
	
	//Open settings controller
	@GetMapping("/settings")
	public String openSetting() {
		
		
		return "normal/settings";
	}
	
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,
			Principal principal, HttpSession session
			) {
	System.out.println("Password"+oldPassword+" "+ newPassword);
	String userName = principal.getName();
	User userByUserName = this.userRepository.getUserByUserName(userName);
	System.out.println("username"+userByUserName.getPassword());
	if(this.bCryptPasswordEncoder.matches(oldPassword, userByUserName.getPassword())) {
		
		userByUserName.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(userByUserName);
		session.setAttribute("message", new Message("your password is reset successfully..", "success"));
	}else {
		//error
		session.setAttribute("message", new Message("wrong password..", "error"));
		return "redirect:/user/settings";
	}
		return "redirect:/user/index";
	}
	
	
	//RazorPay integration for creating order
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object>data,Principal principal) {
		System.out.println("Hello order"+data);
		Order order = null ;
		int amount=Integer.parseInt(data.get("amount").toString());
	try {
		var client=	new RazorpayClient("rzp_test_uTEM1LDzgTRWNy", "hBtf9qYN6KueTzoiiiTgJ3cX");
		JSONObject options = new JSONObject();
		options.put("amount",amount*100);
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");
	 order = client.Orders.create(options);
		System.out.println("Order "+order);
		
		MyOder myOder = new MyOder();
		myOder.setAmount(order.get("amount")+"");
		myOder.setOrderId(order.get("id"));
		myOder.setPaymentId(null);
		myOder.setStatus(order.get("created"));
		myOder.setUser(this.userRepository.getUserByUserName(principal.getName()));
		myOder.setReciept(order.get("receipt"));
		this.myOrderRepo.save(myOder);
		
	} catch (RazorpayException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		return order.toString() ;
		
	}
	
}
