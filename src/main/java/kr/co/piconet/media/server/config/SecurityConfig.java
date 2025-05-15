/**
 * This file is part of Pico Media Server.
 *
 * Copyright 2024 Jaeik Shin
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

package kr.co.piconet.media.server.config;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import kr.co.piconet.media.server.util.PicomediaConfig;

@Configuration
@EnableWebSecurity(debug = false)
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() throws Exception {
    	return web -> {
    		web.ignoring()
    			.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
    			.requestMatchers("/test/**");
    	};
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.formLogin(formLogin -> formLogin.disable())
//        	.formLogin(Customizer.withDefaults())
            .authorizeHttpRequests((authorize) -> authorize
            	.requestMatchers(new AntPathRequestMatcher("/public/**", HttpMethod.GET.toString())).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer((oauth2) -> {
                oauth2.jwt(Customizer.withDefaults());
            })
            .csrf(AbstractHttpConfigurer::disable);
//            .with(new OAuth2AuthorizationServerConfigurer(), Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    	UserDetails userDetails = User.withUsername("test")
    		.password(passwordEncoder.encode("1234"))
    		.roles("USER")
    		.build();
        return new InMemoryUserDetailsManager(userDetails);
    }

//    @Bean
//    AuthorizationServerSettings authorizationServerSettings() {
//        return AuthorizationServerSettings.builder()
//            .tokenEndpoint("/oauth/token") // default : /oauth2/token
//            .build();
//    }

//    @Bean
//    RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
//        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
//            .clientId(clientId)
//            .clientSecret(passwordEncoder.encode(clientSecret))
//            .scope(OidcScopes.OPENID)
//            .scope(OidcScopes.PROFILE)
//            .scope("read")
//            .scope("write")
//            .scope("read.write")
//            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
////            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
////            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//            .redirectUri("http://localhost:9000/authorized")
//            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
//            .tokenSettings(
//            	TokenSettings.builder()
////            		.accessTokenTimeToLive(Duration.ofDays(365))
//            		.accessTokenTimeToLive(Duration.ofSeconds(accessTokenExpiration))
////            		.refreshTokenTimeToLive(Duration.ofDays(180))
//            		.build()
//            )
//            .build();
//        return new InMemoryRegisteredClientRepository(client);
//    }

    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(List.of(
            new JwtTimestampValidator(Duration.ofSeconds(0))
        )));
        return jwtDecoder;
    }

    @Bean
    JWKSource<SecurityContext> jwkSource(KeyPair keyPair, PicomediaConfig config) {
    	RSAPublicKey publicKey = loadPublicKey(config);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    KeyPair keyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	protected static RSAPublicKey loadPublicKey(PicomediaConfig config)  {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			File file = new File(config.getPicomedia().getPublicKeyPath());
		    try (FileReader keyReader = new FileReader(file);
		      PemReader pemReader = new PemReader(keyReader)) {

		        PemObject pemObject = pemReader.readPemObject();
		        byte[] content = pemObject.getContent();
		        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		        return (RSAPublicKey) factory.generatePublic(pubKeySpec);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}