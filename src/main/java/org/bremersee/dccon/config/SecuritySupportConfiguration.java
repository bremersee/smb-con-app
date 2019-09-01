/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.dccon.config;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.security.OAuth2Properties;
import org.bremersee.security.authentication.KeycloakJwtConverter;
import org.bremersee.security.authentication.PasswordFlowAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * The security support configuration.
 *
 * @author Christian Bremer
 */
@Configuration
@EnableConfigurationProperties({OAuth2Properties.class})
@Slf4j
public class SecuritySupportConfiguration {

  private OAuth2Properties oauthProperties;

  /**
   * Instantiates a new security support configuration.
   *
   * @param oauthProperties the o auth 2 properties
   */
  @Autowired
  public SecuritySupportConfiguration(OAuth2Properties oauthProperties) {
    this.oauthProperties = oauthProperties;
  }

  /**
   * Keycloak jwt converter.
   *
   * @return the keycloak jwt converter
   */
  @Profile("!basic-auth")
  @Bean
  public KeycloakJwtConverter keycloakJwtConverter() {
    log.info("Using keycloak jwt converter.");
    return new KeycloakJwtConverter();
  }

  /**
   * Password flow authentication manager.
   *
   * @param jwtDecoder           the jwt decoder
   * @param keycloakJwtConverter the keycloak jwt converter
   * @param restTemplateBuilder  the rest template builder
   * @return the password flow authentication manager
   */
  @Profile("!basic-auth")
  @Bean
  public PasswordFlowAuthenticationManager passwordFlowAuthenticationManager(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") JwtDecoder jwtDecoder,
      KeycloakJwtConverter keycloakJwtConverter,
      RestTemplateBuilder restTemplateBuilder) {

    log.info("Using password flow authentication manager.");
    final PasswordFlowAuthenticationManager manager
        = new PasswordFlowAuthenticationManager(oauthProperties, jwtDecoder, restTemplateBuilder);
    manager.setJwtAuthenticationConverter(keycloakJwtConverter);
    return manager;
  }

}
