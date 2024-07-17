package com.istt.staff_notification_v2.security.securityv2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.istt.staff_notification_v2.entity.Group;
import com.istt.staff_notification_v2.entity.Role;
import com.istt.staff_notification_v2.entity.User;
import com.istt.staff_notification_v2.repository.RoleRepo;

public class UserPrincipal implements UserDetails {

	private String user_id;

	private String name;

	private String username;
	
	@Autowired 
	private RoleRepo roleRepo;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserPrincipal(String user_id, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.user_id = user_id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserPrincipal create(User user) {
		
		Set<Group> groups = user.getGroups();
		Set<Role> roles = new HashSet<Role>();
		Set<String> permissions = new HashSet<String>();
		for (Group group: groups) {
			roles.addAll(group.getRoles());
		}
		for (Role role : roles) {
			permissions.add(role.getRole());
		}
		
		List<GrantedAuthority> authorities = permissions.stream()
				.map(permission -> new SimpleGrantedAuthority(permission.toUpperCase())).collect(Collectors.toList());
		return new UserPrincipal(user.getUserId(), user.getUsername(), user.getPassword(), authorities);
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserPrincipal that = (UserPrincipal) o;
		return Objects.equals(user_id, that.user_id);
	}

	@Override
	public int hashCode() {

		return Objects.hash(user_id);
	}
}
