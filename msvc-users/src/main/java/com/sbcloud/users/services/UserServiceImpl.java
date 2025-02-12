package com.sbcloud.users.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbcloud.users.entities.Role;
import com.sbcloud.users.entities.User;
import com.sbcloud.users.repositories.RoleRepository;
import com.sbcloud.users.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    
    private UserRepository userRepository;
    
    private RoleRepository roleRepository;
    
    private PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
	@Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return Optional.of(userRepository.findByUsername(username));
    }

    @Override
	@Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
	@Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

	@Override
	@Transactional
	public User save(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		user.setRoles(getRoles(user));
		if(user.getEnabled() == null) {
			user.setEnabled(true);
		}
		return userRepository.save(user);
	}

	private List<Role> getRoles(User user) {
		if(user.isAdmin()) {
			return Arrays.asList(roleRepository.findByName("ROLE_USER").orElseThrow(),
						roleRepository.findByName("ROLE_ADMIN").orElseThrow());
		}
		return Arrays.asList(roleRepository.findByName("ROLE_USER").orElseThrow());		
	}
	
	@Override
	@Transactional
	public Optional<User> update(Long id, User user) {
		return this.findById(id)
        .map(existingUser -> {
        	existingUser = new User(id, user.getUsername(), existingUser.getPassword(),
        							user.getEnabled()== null ? true : user.getEnabled(),
        							user.getEmail());
        	existingUser.setRoles(getRoles(user));
            return Optional.of(userRepository.save(existingUser));
        })
        .orElseGet(() -> Optional.empty()); 
	}
}
