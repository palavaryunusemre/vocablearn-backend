package com.vocablearn.security;

import com.vocablearn.domain.AppUser;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class AppUserPrincipal implements UserDetails {

	private final AppUser appUser;

	public AppUserPrincipal(AppUser appUser) {
		this.appUser = appUser;
	}

	public Long getId() {
		return appUser.getId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole().name()));
	}

	@Override
	public String getPassword() {
		return appUser.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return appUser.getEmail();
	}
}
