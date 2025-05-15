/**
 * This file is part of Pico Media Server.
 *
 * Copyright 2024 PICONET
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.co.piconet.media.server.test;

import java.io.File;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

public class AccessTokenGenerator
{
	private static RSAPublicKey readX509PublicKey(File file) throws Exception {
		KeyFactory factory = KeyFactory.getInstance("RSA");
	    try (FileReader keyReader = new FileReader(file);
	      PemReader pemReader = new PemReader(keyReader)) {

	        PemObject pemObject = pemReader.readPemObject();
	        byte[] content = pemObject.getContent();
	        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
	        return (RSAPublicKey) factory.generatePublic(pubKeySpec);
	    }
	}

	private static RSAPrivateKey readX509PrivateKey(File file) throws Exception {
		KeyFactory factory = KeyFactory.getInstance("RSA");

	    try (FileReader keyReader = new FileReader(file);
	      PemReader pemReader = new PemReader(keyReader)) {

	        PemObject pemObject = pemReader.readPemObject();
	        byte[] content = pemObject.getContent();
	        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
	        return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
	    }
	}

	public static void main(String[] args)
	{

		try {
			String subject = "test";
			String adience = subject;
			File dir = new File("./docs/keypairs", subject);

			File publicFile = new File(dir, String.format("%s-public.pem", subject));
			File privateFile = new File(dir, String.format("%s-private.pem", subject));

			RSAPublicKey pulbicKey = readX509PublicKey(publicFile);
			RSAPrivateKey privateKey = readX509PrivateKey(privateFile);

			Algorithm algorithm = Algorithm.RSA256(pulbicKey, privateKey);
			Instant now = Instant.now();
			String jti = UUID.randomUUID().toString();

			int expireDays = 1 * 365;
 
		    String accessToken = JWT.create()
		        .withSubject(subject)
		        .withAudience(adience)
		        .withNotBefore(now)
		        .withClaim("scope", List.of("read", "write"))
		        .withIssuer("piconet")
		        .withExpiresAt(now.plus(expireDays, ChronoUnit.DAYS))
		        .withIssuedAt(now)
		        .withJWTId(jti)
		        .sign(algorithm);

		    System.out.println(accessToken);

		} catch (JWTCreationException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}