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

package org.bremersee.dccon.service;

import org.bremersee.dccon.model.PasswordInformation;
import org.bremersee.dccon.repository.DomainRepository;
import org.springframework.stereotype.Component;

/**
 * The domain service implementation.
 *
 * @author Christian Bremer
 */
@Component("domainService")
public class DomainServiceImpl implements DomainService {

  private DomainRepository domainRepository;

  /**
   * Instantiates a new domain service.
   *
   * @param domainRepository the domain repository
   */
  public DomainServiceImpl(DomainRepository domainRepository) {
    this.domainRepository = domainRepository;
  }

  @Override
  public PasswordInformation getPasswordInformation() {
    return domainRepository.getPasswordInformation();
  }

  @Override
  public String createRandomPassword() {
    return domainRepository.createRandomPassword();
  }
}
