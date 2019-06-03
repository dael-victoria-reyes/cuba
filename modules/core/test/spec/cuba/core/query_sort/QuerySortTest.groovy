package spec.cuba.core.query_sort

import com.haulmont.cuba.core.app.JpqlSortExpressionProvider
import com.haulmont.cuba.core.app.JpqlQueryBuilder
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.Sort
import com.haulmont.cuba.testsupport.TestContainer
import com.haulmont.cuba.testsupport.TestJpqlSortExpressionProvider
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class QuerySortTest extends Specification {

    @Shared @ClassRule
    public TestContainer cont = TestContainer.Common.INSTANCE

    def "sort"() {

        JpqlQueryBuilder queryBuilder

        when: "by single property"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select u from sec$User u', null,
                Sort.by('name'), [:], null, null, null, 'sec$User')

        then:

        queryBuilder.getQueryString() == 'select u from sec$User u order by u.name'

        when: "by two properties"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select u from sec$User u', null,
                Sort.by('login', 'name'), [:], null, null, null, 'sec$User')

        then:

        queryBuilder.getQueryString() == 'select u from sec$User u order by u.login, u.name'

        when: "by two properties desc"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select u from sec$User u', null,
                Sort.by(Sort.Direction.DESC, 'login', 'name'), [:], null, null, null, 'sec$User')

        then:

        queryBuilder.getQueryString() == 'select u from sec$User u order by u.login desc, u.name desc'

        when: "by reference property"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select u from sec$User u', null,
                Sort.by('group.name'), [:], null, null, null, 'sec$User')

        then:

        queryBuilder.getQueryString() == 'select u from sec$User u left join u.group u_group order by u_group.name'

        when: "by reference property desc"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select u from sec$User u', null,
                Sort.by(Sort.Direction.DESC, 'group.name'), [:], null, null, null, 'sec$User')

        then:

        queryBuilder.getQueryString() == 'select u from sec$User u left join u.group u_group order by u_group.name desc'
    }

    def "sort by single property with order function and nulls first"() {

        JpqlQueryBuilder queryBuilder
        TestJpqlSortExpressionProvider sortExpressionProvider

        setup:
        sortExpressionProvider =  AppBeans.get(JpqlSortExpressionProvider)
        Metadata metadata = AppBeans.get(Metadata)
        sortExpressionProvider.addToUpperPath(metadata.getClassNN('test$Order').getPropertyPath('number'))

        when:

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select e from test$Order e', null,
                Sort.by('number'), [:], null, null, null, 'test$Order')

        then:

        queryBuilder.getQueryString() == 'select e from test$Order e order by upper( e.number) asc nulls first'

        cleanup:
        sortExpressionProvider.resetToUpperPaths()
    }

    def "sort by multiple properties in different directions is not supported"() {

        JpqlQueryBuilder queryBuilder

        when:

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select u from sec$User u', null,
                Sort.by(Sort.Order.asc('login'), Sort.Order.desc('name')), [:], null, null, null, 'sec$User')

        then:

        thrown(UnsupportedOperationException)
    }

    def "sort by non-persistent property"() {

        JpqlQueryBuilder queryBuilder

        when: "by single non-persistent property"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select e from sys$EntitySnapshot e', null,
                Sort.by('changeDate'), [:], null, null, null, 'sys$EntitySnapshot')

        then:

        queryBuilder.getQueryString() == 'select e from sys$EntitySnapshot e order by e.snapshotDate'

        when: "by persistent and non-persistent property"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select e from sys$EntitySnapshot e', null,
                Sort.by('createTs', 'changeDate'), [:], null, null, null, 'sys$EntitySnapshot')

        then:

        queryBuilder.getQueryString() == 'select e from sys$EntitySnapshot e order by e.createTs, e.snapshotDate'

        when: "by single non-persistent property desc"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select e from sys$EntitySnapshot e', null,
                Sort.by(Sort.Direction.DESC, 'changeDate'), [:], null, null, null, 'sys$EntitySnapshot')

        then:

        queryBuilder.getQueryString() == 'select e from sys$EntitySnapshot e order by e.snapshotDate desc'

        when: "by non-persistent property related to two other properties"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select e from sys$EntitySnapshot e', null,
                Sort.by('label'), [:], null, null, null, 'sys$EntitySnapshot')

        then:

        queryBuilder.getQueryString() == 'select e from sys$EntitySnapshot e left join e.author e_author order by e.snapshotDate, e_author.login, e_author.name'

        when: "by non-persistent property related to two other properties desc"

        queryBuilder = AppBeans.get(JpqlQueryBuilder)
        queryBuilder.init('select e from sys$EntitySnapshot e', null,
                Sort.by(Sort.Direction.DESC, 'label'), [:], null, null, null, 'sys$EntitySnapshot')

        then:

        queryBuilder.getQueryString() == 'select e from sys$EntitySnapshot e left join e.author e_author order by e.snapshotDate desc, e_author.login desc, e_author.name desc'
    }
}
