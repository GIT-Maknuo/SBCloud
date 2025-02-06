package com.sbcloud.users.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sbcloud.users.entities.User;
import com.sbcloud.users.services.UserService;

@RestController
public class UserController {
	
	private final Logger log = LoggerFactory.getLogger(UserController.class);
    
    private UserService userService;    

    public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
    public ResponseEntity<?> getAllUsers() {
    	log.info("Metodo controller::getAllUsers()");
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
    	log.info("Metodo controller::getUserById()");
        return userService.findById(id).map(ResponseEntity::ok)
        		   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
    	log.info("Metodo controller getUserByUsername(), username:{}", username);
        return userService.findByUsername(username).map(ResponseEntity::ok)
        		   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) { 
    	log.info("Metodo controller createUser(), User:{}", user);
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    	log.info("Metodo controller updateUser(), User:{}", userDetails);
        return userService.update(id, userDetails)
        		.map((ResponseEntity::ok))
        		.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));   	
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    	log.info("Metodo controller::deleteUser()");
    	return userService.findById(id) 
    			.map(product -> { 
    				userService.delete(id); 
    		    		return ResponseEntity.noContent().build(); 
    	    		}) 
    	    	.orElse(ResponseEntity.notFound().build());  	
    }
}
