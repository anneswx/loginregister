package com.Team12.CS5800.VotingApplication.controller;

import java.time.LocalDateTime;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.Team12.CS5800.VotingApplication.model.EmailAuthGrabber;
import com.Team12.CS5800.VotingApplication.model.SessionGrabber;
import com.Team12.CS5800.VotingApplication.service.RegisterService;

@Controller
@SessionAttributes("name")
public class RegisterController {
	
	@Autowired
    RegisterService service;

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegisterPage(ModelAndView model){
        return "register";
    }

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public ModelAndView showWelcomePage(ModelAndView model, @RequestParam String username, @RequestParam String password, @RequestParam String password2, @RequestParam int ssn, @RequestParam String address, @RequestParam String first_name, @RequestParam String last_name, @RequestParam String city, @RequestParam String state, @RequestParam String zipcode, @RequestParam String email, HttpServletResponse response){

    	if (!password.equals(password2)) {
    		model.addObject("errorMessage", "Passwords must match");
    		return model;
    	}
    	
    	boolean successful = service.registerUser(username, password, email, first_name, last_name, ssn, address, city, state, zipcode);

    	int userID;
    	if (!successful) {
    		model.addObject("errorMessage", "That username is not available");
    		return model;
    	}
    	else {
    		userID = service.getUser(username).getID();
    	}
    	
    	model.setViewName("redirect:/");
    	
    	SessionGrabber sg = new SessionGrabber();
    	
    	String cookieToAdd = sg.generateSessionID() + LocalDateTime.now();
    	sg.storeSession(cookieToAdd, userID);
    	
    	response.addCookie(new Cookie("sessionID", cookieToAdd));
    	
    	EmailAuthGrabber eag = new EmailAuthGrabber();
    	String emailAuthKey = eag.generateEmailAuthID() + email;
    	eag.storeEmailAuthKey(emailAuthKey, userID);

    return model;
    
    }

}
