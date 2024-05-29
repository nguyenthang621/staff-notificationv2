package com.istt.staff_notification_v2.security;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.istt.staff_notification_v2.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtTokenProvider {

	private final String JWT_SECRET = "JWT_SECRET";

	// JWT_EXPIRATION
	private final long JWT_EXPIRATION = 604800000L;

	// Tạo ra jwt từ thông tin user
	public String generateToken(User userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
		// Tạo chuỗi json web token từ id của user.
		return Jwts.builder().setSubject(userDetails.getUser_id()).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
	}

	// Lấy thông tin user từ jwt
	public String getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();

		return String.valueOf(claims.getSubject());
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException ex) {
			System.out.println("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			System.out.println("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			System.out.println("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			System.out.println("JWT claims string is empty.");
		}
		return false;
	}
}
