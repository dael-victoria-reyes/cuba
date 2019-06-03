/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */

package com.haulmont.cuba.core.app;

import com.google.common.base.Strings;
import com.haulmont.bali.util.StringHelper;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.PersistenceSecurity;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.global.queryconditions.Condition;
import com.haulmont.cuba.core.global.queryconditions.ConditionJpqlGenerator;
import com.haulmont.cuba.core.sys.QueryMacroHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds {@link Query} instance to use in DataService.
 */
@Component(JpqlQueryBuilder.NAME)
@Scope("prototype")
public class JpqlQueryBuilder {

    public static final String NAME = "cuba_JpqlQueryBuilder";

    private static final Logger log = LoggerFactory.getLogger(JpqlQueryBuilder.class);

    protected Object id;
    protected List<Object> ids;

    protected String queryString;
    protected Map<String, Object> queryParams;
    protected String[] noConversionParams;
    protected Condition condition;
    protected Sort sort;

    protected String entityName;
    protected boolean singleResult;

    protected String resultQuery;
    protected Map<String, Object> resultParams;

    @Inject
    protected Metadata metadata;

    @Inject
    protected PersistenceSecurity security;

    @Inject
    protected ConditionJpqlGenerator conditionJpqlGenerator;

    @Inject
    protected SortJpqlGenerator sortJpqlGenerator;

    public JpqlQueryBuilder setId(Object id) {
        this.id = id;
        return this;
    }

    public JpqlQueryBuilder setIds(List<Object> ids) {
        this.ids = ids;
        return this;
    }

    public JpqlQueryBuilder setQuery(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public JpqlQueryBuilder setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public JpqlQueryBuilder setNoConversionParams(String[] noConversionParams) {
        this.noConversionParams = noConversionParams;
        return this;
    }

    public JpqlQueryBuilder setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public JpqlQueryBuilder setSort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public void buildQueryString() {
        if (Strings.isNullOrEmpty(queryString)) {
            if (id != null) {
                resultQuery = String.format("select e from %s e where e.%s = :entityId", entityName, getPkName(entityName));
                resultParams = new HashMap<>();
                resultParams.put("entityId", id);
            } else if (ids != null && ids.isEmpty()) {

            }
        }


        this.entityName = entityName;
        String qs;
        if (ids != null && !ids.isEmpty()) {
            qs = "select e from " + entityName + " e where e." + getPkName(entityName) + " in :entityIdList";
            this.queryParams = new HashMap<>();
            this.queryParams.put("entityIdList", ids);

        } else if (queryString == null && id == null) {
            qs = "select e from " + entityName + " e";
            this.queryParams = Collections.emptyMap();

        } else if (!StringUtils.isBlank(queryString)) {
            qs = queryString;
            this.queryParams = queryParams;
            this.noConversionParams = noConversionParams;

        } else {
            qs = "select e from " + entityName + " e where e." + getPkName(entityName) + " = :entityId";
            this.queryParams = new HashMap<>();
            this.queryParams.put("entityId", id);
        }
        if (condition != null) {
            Set<String> nonNullParamNames = queryParams.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            Condition actualized = condition.actualize(nonNullParamNames);
            qs = conditionJpqlGenerator.processQuery(qs, actualized);
        }
        if (sort != null) {
            resultQueryString = sortJpqlGenerator.processQuery(entityName, qs, sort);
        }

        return resultQueryString;
    }

    protected String getPkName(String entityName) {
        MetaClass metaClass = metadata.getClassNN(entityName);
        String pkName = metadata.getTools().getPrimaryKeyName(metaClass);
        if (pkName == null)
            throw new IllegalStateException(String.format("Entity %s has no primary key", entityName));
        return pkName;
    }

    public void setSingleResult(boolean singleResult) {
        this.singleResult = singleResult;
    }

    public void restrictByPreviousResults(UUID sessionId, int queryKey) {
        QueryTransformer transformer = QueryTransformerFactory.createTransformer(queryString);
        MetaClass metaClass = metadata.getClassNN(entityName);
        MetaProperty primaryKey = metadata.getTools().getPrimaryKeyProperty(metaClass);
        if (primaryKey == null)
            throw new IllegalStateException(String.format("Entity %s has no primary key", entityName));
        Class type = primaryKey.getJavaType();
        String entityIdField;
        if (UUID.class.equals(type)) {
            entityIdField = "entityId";
        } else if (Long.class.equals(type)) {
            entityIdField = "longEntityId";
        } else if (Integer.class.equals(type)) {
            entityIdField = "intEntityId";
        } else if (String.class.equals(type)) {
            entityIdField = "stringEntityId";
        } else {
            throw new IllegalStateException(
                    String.format("Unsupported primary key type: %s for %s", type.getSimpleName(), entityName));
        }
        transformer.addJoinAndWhere(
                ", sys$QueryResult _qr",
                String.format("_qr.%s = {E}.%s and _qr.sessionId = :_qr_sessionId and _qr.queryKey = %s",
                        entityIdField, primaryKey.getName(), queryKey)
        );
        queryString = transformer.getResult();
        this.queryParams.put("_qr_sessionId", sessionId);
    }

    public String getQueryString() {
        return queryString;
    }

    public Query getQuery(EntityManager em) {
        Query query = em.createQuery(queryString);

        //we have to replace parameter names in macros because for {@link com.haulmont.cuba.core.sys.querymacro.TimeBetweenQueryMacroHandler}
        //we need to replace a parameter with number of days with its value before macros is expanded to JPQL expression
        replaceParamsInMacros(query);

        applyConstraints(query);

        QueryParser parser = QueryTransformerFactory.createParser(queryString);
        Set<String> paramNames = parser.getParamNames();

        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            String name = entry.getKey();
            if (paramNames.contains(name)) {
                Object value = entry.getValue();

                if (value instanceof TemporalValue) {
                    TemporalValue temporalValue = (TemporalValue) value;
                    query.setParameter(name, temporalValue.date, temporalValue.type);
                } else {
                    if (noConversionParams != null && Arrays.asList(noConversionParams).contains(name)) {
                        query.setParameter(name, value, false);
                    } else {
                        query.setParameter(name, value);
                    }
                }
            } else {
                if (entry.getValue() != null)
                    throw new DevelopmentException("Parameter '" + name + "' is not used in the query");
            }
        }

        return query;
    }

    protected void replaceParamsInMacros(Query query) {
        Collection<QueryMacroHandler> handlers = AppBeans.getAll(QueryMacroHandler.class).values();
        String modifiedQuery = query.getQueryString();
        for (QueryMacroHandler handler : handlers) {
            modifiedQuery = handler.replaceQueryParams(modifiedQuery, queryParams);
        }
        query.setQueryString(modifiedQuery);
    }

    protected void applyConstraints(Query query) {
        boolean constraintsApplied = security.applyConstraints(query);
        if (constraintsApplied && singleResult) {
            QueryParser parser = QueryTransformerFactory.createParser(query.getQueryString());
            if (parser.isQueryWithJoins()) {
                QueryTransformer transformer = QueryTransformerFactory.createTransformer(query.getQueryString());
                transformer.addDistinct();
                query.setQueryString(transformer.getResult());
            }
        }
        if (constraintsApplied && log.isDebugEnabled())
            log.debug("Constraints applied: {}", printQuery(query.getQueryString()));
    }

    public static String printQuery(String query) {
        if (query == null)
            return null;
        else
            return StringHelper.removeExtraSpaces(query.replace('\n', ' '));
    }
}
