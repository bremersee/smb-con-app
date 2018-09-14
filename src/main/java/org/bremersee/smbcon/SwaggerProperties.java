/*
 * Copyright 2017 the original author or authors.
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

package org.bremersee.smbcon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.swagger")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SwaggerProperties {

  private String pathMapping = "/";

  private String title = "Samba Connector";

  private String description = "Configuration of users, groups and DNS of a Samba 4 Server.";

  private String version = "1.0.0";

  private String termsOfServiceUrl = "urn:tos";

  private String contactName;

  private String contactUrl;

  private String contactEmail;

  private String license = "Apache 2.0";

  private String licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0";

}
