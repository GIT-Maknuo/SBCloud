package com.sbcloud.users.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Column(unique = true)
    private String username;
    
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    private Boolean enabled;
    
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Debe ser un correo electrónico válido")
    @Column(unique = true)
    private String email;
    
    @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})//evitar error al serializar respuesta
    @ManyToMany
    @JoinTable(name = "users_roles",
    			joinColumns = {@JoinColumn(name="user_id")},
    			inverseJoinColumns = {@JoinColumn(name="role_id")},
    			uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"}) })
    private List<Role> roles;    
    
    @Transient
    private boolean admin;
    
    public User(Long id, String username, String password, boolean b, String email) {
    	this.id=id;
    	this.username=username;
    	this.password=password;
    	this.enabled=b;
    	this.email=email;    	
	}
   
}

