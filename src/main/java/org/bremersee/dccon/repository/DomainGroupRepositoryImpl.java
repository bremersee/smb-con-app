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

package org.bremersee.dccon.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.data.ldaptive.LdaptiveEntryMapper;
import org.bremersee.data.ldaptive.LdaptiveTemplate;
import org.bremersee.dccon.config.DomainControllerProperties;
import org.bremersee.dccon.model.DomainGroup;
import org.bremersee.dccon.repository.cli.CommandExecutor;
import org.bremersee.dccon.repository.cli.CommandExecutorResponse;
import org.bremersee.dccon.repository.cli.CommandExecutorResponseValidator;
import org.bremersee.dccon.repository.ldap.DomainGroupLdapConstants;
import org.bremersee.dccon.repository.ldap.DomainGroupLdapMapper;
import org.bremersee.exception.ServiceException;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The domain group repository.
 *
 * @author Christian Bremer
 */
@Profile("ldap")
@Component("domainGroupRepository")
@Slf4j
public class DomainGroupRepositoryImpl extends AbstractRepository implements DomainGroupRepository {

  private LdaptiveEntryMapper<DomainGroup> domainGroupLdapMapper;

  /**
   * Instantiates a new domain group repository.
   *
   * @param properties the properties
   * @param ldapTemplateProvider the ldap template provider
   */
  public DomainGroupRepositoryImpl(
      final DomainControllerProperties properties,
      final ObjectProvider<LdaptiveTemplate> ldapTemplateProvider) {
    super(properties, ldapTemplateProvider.getIfAvailable());
    domainGroupLdapMapper = new DomainGroupLdapMapper(properties);
  }

  /**
   * Sets domain group ldap mapper.
   *
   * @param domainGroupLdapMapper the domain group ldap mapper
   */
  @SuppressWarnings("unused")
  public void setDomainGroupLdapMapper(
      final LdaptiveEntryMapper<DomainGroup> domainGroupLdapMapper) {
    if (domainGroupLdapMapper != null) {
      this.domainGroupLdapMapper = domainGroupLdapMapper;
    }
  }

  @Override
  public Stream<DomainGroup> findAll(final String query) {
    final SearchRequest searchRequest = new SearchRequest(
        getProperties().getGroupBaseDn(),
        new SearchFilter(getProperties().getGroupFindAllFilter()));
    searchRequest.setSearchScope(getProperties().getGroupFindAllSearchScope());
    searchRequest.setBinaryAttributes(DomainGroupLdapConstants.BINARY_ATTRIBUTES);
    if (query == null || query.trim().length() == 0) {
      return getLdapTemplate().findAll(searchRequest, domainGroupLdapMapper);
    } else {
      return getLdapTemplate().findAll(searchRequest, domainGroupLdapMapper)
          .filter(domainGroup -> isQueryResult(domainGroup, query.trim().toLowerCase()));
    }
  }

  /**
   * Is query result boolean.
   *
   * @param domainGroup the domain group
   * @param query the query
   * @return the boolean
   */
  static boolean isQueryResult(final DomainGroup domainGroup, final String query) {
    return query != null && query.length() > 2 && domainGroup != null
        && (contains(domainGroup.getName(), query)
        || contains(domainGroup.getDescription(), query)
        || contains(domainGroup.getMembers(), query));
  }

  @Override
  public Optional<DomainGroup> findOne(final String groupName) {
    final SearchFilter searchFilter = new SearchFilter(getProperties().getGroupFindOneFilter());
    searchFilter.setParameter(0, groupName);
    final SearchRequest searchRequest = new SearchRequest(
        getProperties().getGroupBaseDn(),
        searchFilter);
    searchRequest.setSearchScope(getProperties().getGroupFindOneSearchScope());
    searchRequest.setBinaryAttributes(DomainGroupLdapConstants.BINARY_ATTRIBUTES);
    return getLdapTemplate().findOne(searchRequest, domainGroupLdapMapper);
  }

  @Override
  public boolean exists(final String groupName) {
    return getLdapTemplate()
        .exists(DomainGroup.builder().name(groupName).build(), domainGroupLdapMapper);
  }

  @Override
  public DomainGroup save(final DomainGroup domainGroup) {
    if (!exists(domainGroup.getName())) {
      doAdd(domainGroup);
    }
    return getLdapTemplate().save(domainGroup, domainGroupLdapMapper);
  }

  /**
   * Add group.
   *
   * @param domainGroup the domain group
   */
  void doAdd(final DomainGroup domainGroup) {
    kinit();
    final List<String> commands = new ArrayList<>();
    sudo(commands);
    commands.add(getProperties().getSambaToolBinary());
    commands.add("group");
    commands.add("add");
    commands.add(domainGroup.getName());
    auth(commands);
    CommandExecutor.exec(
        commands,
        null,
        getProperties().getSambaToolExecDir(),
        (CommandExecutorResponseValidator) response -> {
          if (!exists(domainGroup.getName())) {
            throw ServiceException.internalServerError("msg=[Saving group failed.] groupName=["
                    + domainGroup.getName() + "] "
                    + CommandExecutorResponse.toExceptionMessage(response),
                "org.bremersee:dc-con-app:7729c3c7-aeff-49f2-9243-dd5aee4b023a");
          }
        });
  }

  @Override
  public boolean delete(final String groupName) {

    if (exists(groupName)) {
      doDelete(groupName);
      return true;
    }
    return false;
  }

  /**
   * Delete group.
   *
   * @param groupName the group name
   */
  void doDelete(final String groupName) {
    kinit();
    final List<String> commands = new ArrayList<>();
    sudo(commands);
    commands.add(getProperties().getSambaToolBinary());
    commands.add("group");
    commands.add("delete");
    commands.add(groupName);
    auth(commands);
    CommandExecutor.exec(
        commands,
        null,
        getProperties().getSambaToolExecDir(),
        (CommandExecutorResponseValidator) response -> {
          if (exists(groupName)) {
            throw ServiceException.internalServerError(
                "msg=[Deleting group failed.] groupName=[" + groupName + "] "
                    + CommandExecutorResponse.toExceptionMessage(response),
                "org.bremersee:dc-con-app:28f610a5-1679-47d9-8f90-2a4d75882d52");
          }
        });
  }


}
