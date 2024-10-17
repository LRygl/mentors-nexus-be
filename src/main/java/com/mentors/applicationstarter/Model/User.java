package com.mentors.applicationstarter.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mentors.applicationstarter.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mn_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "userGenerator")
    @SequenceGenerator(name = "userGenerator", sequenceName = "application_user_sequence", allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    
    private UUID UUID;
    private String firstName;
    private String lastName;
    private String email;
    private String telephoneNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    //SUBJECT DETAILS
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date registerDate;
    private Date lastUpdatedDate;
    private UUID passwordResetOperationUUID;
    private Date passwordResetExpiryDate;
    private Boolean isAccountNonLocked;


    //USER CONSENT DETAILS
    private Boolean personalDataProcessing;
    private Boolean personalDataPublishing;
    private Boolean marketing;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonProperty("role")
    public String getRoleName() {
        return role != null ? role.name() : null;
    }

}
