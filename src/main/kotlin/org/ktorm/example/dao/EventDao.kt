package org.ktorm.example.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.example.model.EventTable
import org.ktorm.example.model.EventTable1
import org.springframework.stereotype.Component

/**
 * @author xiefangxiang
 * @date 2022/6/30 16:23
 */
@Component
class EventDao(private val database: Database) {
    fun statistics(areaId: Long, algoIds: List<Long>) {
        val list = database.from(EventTable)
            .select(EventTable.algoId, EventTable.algoName, count(EventTable.algoId))
            .where { (EventTable.areaId eq areaId) and (EventTable.algoId inList algoIds) }
            .groupBy(EventTable.algoId)
            .forEach { row -> println("method1-${row[EventTable.algoName]}") }
    }

    //报错,java.lang.IllegalStateException: No entity class configured for table: 't_event_info'
    //说明没有绑定实体类,是不可以用序列API
    fun statistics1() {
        val toList = database.sequenceOf(EventTable)
            .filter { EventTable.areaId eq 1 }
            .toList()
    }

    fun statistics() {
        database.from(EventTable1)
            .select(EventTable1.algoId, EventTable1.algoName, count(EventTable1.algoId))
            .where { EventTable1.areaId eq 1 }
            .groupBy(EventTable1.algoId)
    }

    fun statistics2(areaId: Long, algoIds: List<Long>) {
        val toList = database.sequenceOf(EventTable1)
            .filter { EventTable1.areaId eq areaId }
            .filter { EventTable1.algoId inList algoIds }
            .toList()
        //如果表结构关联了外表的id,这里用外表id做filter还会自动生成连表SQL
        //生成SQL
        /*select *
                from t_employee
                left join t_department _ref0 on t_employee.department_id = _ref0.id
                where (t_employee.department_id = ?) and (t_employee.manager_id is not null)*/
        //序列 API 会自动 left join 引用表，有时这可能会造成一点浪费。如果你希望对查询进行更细粒度的控制，你可以使用前面章节中介绍的查询 DSL
    }
}