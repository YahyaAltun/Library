package com.example.library54.controller;

import com.example.library54.domain.User;
import com.example.library54.dto.request.RegisterRequest;
import com.example.library54.dto.request.SignInRequest;
import com.example.library54.dto.response.SignInResponse;
import com.example.library54.security.jwt.JwtUtils;
import com.example.library54.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class UserJWTController {

    private UserService userService;
    private AuthenticationManager authManager;
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ResponseEntity<Map<String,String>> register(@Valid @RequestBody RegisterRequest registerRequest){
        User newUser=userService.register(registerRequest);

        Map<String,String> map=new HashMap<>();
        map.put("id : ", newUser.getId().toString());
        map.put("name : ",newUser.getFirstName());

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
    @PostMapping("/signin")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public ResponseEntity<SignInResponse> authenticate(@Valid  @RequestBody SignInRequest signInRequest){
        Authentication authentication= authManager.authenticate(new
                UsernamePasswordAuthenticationToken(signInRequest.getEmail(),signInRequest.getPassword()));

        String token= jwtUtils.generateJwtToken(authentication);

        SignInResponse response=new SignInResponse();
        response.setToken(token);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
