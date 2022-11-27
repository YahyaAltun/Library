package com.example.library54.controller;

import com.example.library54.domain.User;
import com.example.library54.dto.UserDTO;
import com.example.library54.dto.request.CreateUserRequest;
import com.example.library54.dto.request.UpdateRequest;
import com.example.library54.dto.response.RLResponse;
import com.example.library54.exception.message.ErrorMessage;
import com.example.library54.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class UserConttroller {

    private UserService userService;

    @GetMapping("/user/loans")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<UserDTO>> getAllUserLoansByPage(@RequestParam("page") int page,
                                                               @RequestParam("size") int size,
                                                               @RequestParam("sort") String prop,
                                                               @RequestParam("type") Sort.Direction type){

        Pageable pageable= PageRequest.of(page, size, Sort.by(type, prop));
        Page<UserDTO> userDTOPage=userService.getUserLoanPage(pageable);
        return ResponseEntity.ok(userDTOPage);

    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or  hasRole('EMPLOYEE')")
    public ResponseEntity<Page> getAllUsersByPage(@RequestParam(required = false ,value="name") String name,
                                                  @RequestParam(required = false ,value="page") int page,
                                                  @RequestParam(required = false ,value="size") int size,
                                                  @RequestParam(required = false ,value="sort") String prop,
                                                  @RequestParam(required = false ,value="type") Sort.Direction type){
        Pageable pageable= PageRequest.of(page, size, Sort.by(type, prop));
        Page userDTOPage=userService.getUsersPage(name,pageable);

        return ResponseEntity.ok(userDTOPage);
    }
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')or hasRole('EMPLOYEE')")
    public ResponseEntity<RLResponse> getUserById(@PathVariable Long id){
        UserDTO userDTO= userService.findById(id);

        RLResponse response=new RLResponse();
        response.setId(id);
        response.setFirstName(userDTO.getFirstName());

        return ResponseEntity.ok(response);

    }
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or  hasRole('EMPLOYEE')")
    public  ResponseEntity<Map<String,String>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        User newUser= userService.saveUser(createUserRequest);

        Map<String,String> map=new HashMap<>();
        map.put("id : ", newUser.getId().toString());
        map.put("name : ",newUser.getFirstName());

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RLResponse> updateUsersByAdmin(@PathVariable Long id, @Valid @RequestBody
    UpdateRequest userUpdateRequest){

        User userUpdated= userService.updateUserByAdmin(id,userUpdateRequest);
        userService.updateUserByAdmin(id,userUpdateRequest);

        RLResponse response=new RLResponse();
        response.setId(id);
        response.setFirstName(userUpdated.getFirstName());

        return ResponseEntity.ok(response);

    }
    @PutMapping("/user/{id}") //TODO ayri yapildi tek yapilabilir mi
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String,String>> updateUserByEmployee(@PathVariable Long id, @Valid @RequestBody
    UpdateRequest userUpdateRequest){

        User userUpdated=  userService.memberUserUpdate(id,userUpdateRequest);
        Map<String,String> map=new HashMap<>();

        if (userUpdateRequest.getFirstName()!=userUpdated.getFirstName()){

            map.put("id : ", id.toString());
            String str= ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE;
            map.put("error : ",str);
        }else{
            map.put("id : ", userUpdated.getId().toString());
            map.put("name : ",userUpdated.getFirstName());
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }


    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEMBER') or hasRole('EMPLOYEE')")
    public ResponseEntity<RLResponse> getAuthenticatedUser(HttpServletRequest request){
        UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDetails.getAuthorities().forEach(t->System.out.println(t.getAuthority()));

        Long id= (Long) request.getAttribute("id");
        UserDTO userDTO= userService.findById(id);

        RLResponse response=new RLResponse();
        response.setId(id);
        response.setFirstName(userDTO.getFirstName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RLResponse> deleteUser(@PathVariable Long id){

        UserDTO userDTO=  userService.deleteUser(id);

        RLResponse response=new RLResponse();
        response.setId(id);
        response.setFirstName(userDTO.getFirstName());

        return ResponseEntity.ok(response);

    }
}
