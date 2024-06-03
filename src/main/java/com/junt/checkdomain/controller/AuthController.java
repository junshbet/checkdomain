package com.junt.checkdomain.controller;

import com.junt.checkdomain.model.Role;
import com.junt.checkdomain.model.User;
import com.junt.checkdomain.request.ChangePasswordRequest;
import com.junt.checkdomain.request.LoginRequest;
import com.junt.checkdomain.request.RegisterRequest;
import com.junt.checkdomain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @GetMapping("/login")
    public String getlogin(Model model) {
        model.addAttribute("login", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/unauthorized")
    public String autho(Model model) {
        model.addAttribute("login", new LoginRequest());
        return "auth/unauthorized";
    }

    @PostMapping("/login-handel")
    public String login(@ModelAttribute LoginRequest login,
                        HttpServletRequest request,
                        HttpSession session,
                        Model model) {
        System.out.println(login.toString());
        boolean back = false;
        if (login.getUsername().isBlank()) {
            back = true;
            model.addAttribute("user", "Enter you username.");
        }
        if (login.getPassword().isBlank()) {
            back = true;
            model.addAttribute("pass", "Enter you password.");
        }
        if (back){
            model.addAttribute("login", login);
            return "auth/login";
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            return "redirect:/";
        } catch (AuthenticationException ex) {
            model.addAttribute("login", login);
            model.addAttribute("message", "Wrong account or password !");
            return "auth/login";
        }
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("register", new RegisterRequest());
        return "auth/register";
    }


    @PostMapping("/register")
    public String registerHandel(@ModelAttribute RegisterRequest request, Model model) {
        System.out.println(request.toString());
        boolean back = false;
        if (request.getEmail().isBlank()){
            back = true;
            model.addAttribute("email", "Enter you email.");
        }
        if (request.getFullname().isBlank()){
            back = true;
            model.addAttribute("fullname", "Enter you fullname.");
        }
        if (request.getUsername().isBlank()) {
            back = true;
            model.addAttribute("username", "Enter you username.");
        }
        if (request.getPassword().isBlank()) {
            back = true;
            model.addAttribute("password", "Enter you password.");
        }
        if (userService.existsUserByEmail(request.getEmail())) {
            back = true;
            model.addAttribute("email", "Email already exists.");
        }
        if (userService.existsUserByUsername(request.getUsername())) {
            back = true;
            model.addAttribute("username", "Username already exists.");
        }
        if (back){
            model.addAttribute("register", request);
            return "auth/register";
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setRole(Role.USER);
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/add-account")
    public String addTK(Model model) {
        model.addAttribute("register", new RegisterRequest());
        return "auth/add-account";
    }


    @PostMapping("/add-account")
    public String addAccount(@ModelAttribute RegisterRequest request, Model model) {
        boolean back = false;
        if (request.getEmail().isBlank()){
            back = true;
            model.addAttribute("email", "Enter you email.");
        }
        if (request.getFullname().isBlank()){
            back = true;
            model.addAttribute("fullname", "Enter you fullname.");
        }
        if (request.getUsername().isBlank()) {
            back = true;
            model.addAttribute("username", "Enter you username.");
        }
        if (request.getPassword().isBlank()) {
            back = true;
            model.addAttribute("password", "Enter you password.");
        }
        if (userService.existsUserByEmail(request.getEmail())) {
            back = true;
            model.addAttribute("email", "Email already exists.");
        }
        if (userService.existsUserByUsername(request.getUsername())) {
            back = true;
            model.addAttribute("username", "Username already exists.");
        }
        if (back){
            model.addAttribute("register", request);
            return "auth/add-account";
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFullname(request.getFullname());
        user.setRole(Role.ADMIN);
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/change-password")
    public String getChange(Model model) {
        model.addAttribute("change", new ChangePasswordRequest());
        return "auth/change-password";
    }
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request, Model model, HttpServletRequest httpRequest) {
        System.out.println(request.toString());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            user = userService.getUserByUsername(userDetails.getUsername());
        }

        boolean back = false;
        if (request.getOldPassword().isBlank()){
            back = true;
            model.addAttribute("oldPasswordError", "Enter your old password.");
        }
        if (request.getNewPassword().isBlank()){
            back = true;
            model.addAttribute("newPasswordError", "Enter your new password.");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            back = true;
            model.addAttribute("oldPasswordError", "Your old password is incorrect.");
        }
        if (back){
            model.addAttribute("changePasswordRequest", request);
            return "auth/change-password";
        }

        user.setPassword(request.getNewPassword());
        userService.saveUser(user);
        SecurityContextHolder.clearContext();
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
