package com.upload.app.core.util;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;


public class JwtToken {

	/** token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj */
	public static final String SECRET = "JKKLJOoasdlfj";

	/** token 过期时间: 10天 */
	public static final int calendarField = Calendar.DATE;
	public static final int calendarInterval = 10;

	 // 过期时间30天
    private static final long EXPIRE_TIME = 24 * 60 * 30 * 1000;

/*	*//**
	 * JWT生成Token.<br/>
	 *
	 * JWT构成: header, payload, signature
	 *
	 * @param user_id
	 *            登录成功后用户user_id, 参数user_id不可传空
	 *//*
	public static String createToken(Integer user_id) throws Exception {
		Date iatDate = new Date();
		// expire time
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(calendarField, calendarInterval);
		Date expiresDate = nowTime.getTime();

		// header Map
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");

		// build token
		// param backups {iss:Service, aud:APP}
		String token = JWT.create().withHeader(map) // header
				.withClaim("iss", "Service") // payload
				.withClaim("aud", "APP").withClaim("user_id", null == user_id ? null : user_id.toString())
				.withIssuedAt(iatDate) // sign time
				.withExpiresAt(expiresDate) // expire time
				.sign(Algorithm.HMAC256(SECRET)); // signature

		return token;
	}

	*//**
	 * 解密Token
	 *
	 * @param token
	 * @return
	 * @throws Exception
	 *//*
	public static Map<String, Claim> verifyToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
			// e.printStackTrace();
			// token 校验失败, 抛出Token验证非法异常
		}
		return jwt.getClaims();
	}

	*//**
	 * 根据Token获取user_id
	 *
	 * @param token
	 * @return user_id
	 *//*
	public static Long getAppUID(String token) {
		Map<String, Claim> claims = verifyToken(token);
		Claim user_id_claim = claims.get("user_id");
		if (null == user_id_claim || StringUtils.isEmpty(user_id_claim.asString())) {
			// token 校验失败, 抛出Token验证非法异常
		}
		return Long.valueOf(user_id_claim.asString());
	}*/



	  /**
     * 校验token是否正确
     *
     * @param token    密钥
     * @param username 登录名
     * @param password 密码
     * @return
     */
    public static boolean verify(String token, String username, String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(password);

            JWTVerifier verifier = JWT.require(algorithm).withClaim("username", username).build();

            DecodedJWT jwt = verifier.verify(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取登录名
     *
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);

            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名
     *
     * @param username
     * @param password
     * @return
     */
    public static String sign(String username, String password) {
        try {
            // 指定过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);

            Algorithm algorithm = Algorithm.HMAC256(password);

            return JWT.create()
                    .withClaim("username", username)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
