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

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.security.core.AuthorityConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The security properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.access")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Slf4j
public class SecurityProperties {

  private static final String IS_AUTHENTICATED = "isAuthenticated()";

  private List<String> roles = new ArrayList<>();

  private List<String> ipAddresses = new ArrayList<>();

  private List<SimpleUser> basicAuthUsers = new ArrayList<>();

  /**
   * Build access expression for actuator endpoints.
   *
   * @return the access expression for actuator endpoints
   */
  String buildAccess() {
    Set<String> roleSet = new HashSet<>(roles);
    if (roleSet.isEmpty()) {
      roleSet.add(AuthorityConstants.ACTUATOR_ROLE_NAME);
      roleSet.add(AuthorityConstants.ADMIN_ROLE_NAME);
    }
    final StringBuilder sb = new StringBuilder();
    roleSet.forEach(
        role -> sb.append(" or ").append("hasAuthority('").append(role).append("')"));
    ipAddresses.forEach(
        ipAddress -> sb.append(" or ").append("hasIpAddress('").append(ipAddress).append("')"));
    final String access = sb.toString().substring(" or ".length());
    log.info("msg=[Actuator access expression created.] expression=[{}]", access);
    return access;
  }

  /**
   * Build basic auth user details user details.
   *
   * @return the user details
   */
  UserDetails[] buildBasicAuthUserDetails() {
    final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return getBasicAuthUsers().stream().map(
        simpleUser -> User.builder()
            .username(simpleUser.getName())
            .password(simpleUser.getPassword())
            .authorities(simpleUser.buildAuthorities())
            .passwordEncoder(encoder::encode)
            .build())
        .toArray(UserDetails[]::new);
  }

  /**
   * A simple user.
   */
  @Getter
  @Setter
  @ToString(exclude = "password")
  @EqualsAndHashCode(exclude = "password")
  @NoArgsConstructor
  static class SimpleUser implements Serializable, Principal {

    private static final long serialVersionUID = -1393400622632455935L;

    private String name;

    private String password;

    private List<String> authorities = new ArrayList<>();

    /**
     * Build authorities.
     *
     * @return the authorities
     */
    String[] buildAuthorities() {
      if (authorities == null || authorities.isEmpty()) {
        return new String[]{AuthorityConstants.LOCAL_USER_ROLE_NAME};
      }
      return authorities.toArray(new String[0]);
    }

  }
}
