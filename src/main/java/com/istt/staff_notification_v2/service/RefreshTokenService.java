//package com.istt.staff_notification_v2.service;
//
//import java.time.Instant;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.istt.staff_notification_v2.entity.User;
//import com.istt.staff_notification_v2.repository.UserRepo;
//import com.istt.staff_notification_v2.utils.TokenRefreshException;
//
//@Service
//public class RefreshTokenService {
//	@Value("${jwt.jwtRefreshExpirationMs}")
//	private Long refreshTokenDurationMs;
//
//	@Autowired
//	private UserRepo userRepo;
//
//	public Optional<User> findByToken(String token) {
//		return userRepo.findByAccessToken(token);
//	}
//
//	@Transactional
//	public RefreshToken createRefreshToken(Integer userId) {
//		RefreshToken refreshToken = new RefreshToken();
//
//		refreshToken.setUser(userRepository.findById(userId).get());
//		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
//		refreshToken.setToken(UUID.randomUUID().toString());
//
//		RefreshToken refreshToken1 = refreshTokenRepository.getRefreshTokenById(userId);
//		if (refreshToken1 != null) {
//			refreshTokenRepository.deleteByUserId(userId);
//		}
//		refreshToken = refreshTokenRepository.save(refreshToken);
//		return refreshToken;
//	}
//
//	public RefreshToken verifyExpiration(RefreshToken token) {
//		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
//			refreshTokenRepository.delete(token);
//			throw new TokenRefreshException(token.getToken(),
//					"Refresh token was expired. Please make a new signin request");
//		}
//
//		return token;
//	}
//
//	@Transactional
//	public int deleteByUserId(Integer userId) {
//		return refreshTokenRepository.deleteByUserId(userId);
//	}
//}
