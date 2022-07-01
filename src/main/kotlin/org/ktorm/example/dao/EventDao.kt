package org.ktorm.example.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.example.model.Event
import org.ktorm.example.model.EventTable
import org.ktorm.example.model.EventTable1
import org.ktorm.example.model.events
import org.springframework.stereotype.Component

/**
 * @author xiefangxiang
 * @date 2022/6/30 16:23
 * 生成的 SQL 和我们写出来的 Kotlin 代码高度一致
 */
@Component
class EventDao(private val database: Database) {
    fun findByAreaIdAndAlgoIds(areaId: Long, algoIds: List<Long>) {
        database.from(EventTable)
            .select(EventTable.algoId, EventTable.algoName, EventTable.eventTime)
            .where { (EventTable.areaId eq areaId) and (EventTable.algoId inList algoIds) }
            .forEach { row -> println("findByAreaIdAndAlgoIds-${row[EventTable.algoId]}  ${row[EventTable.algoName]}  ${row[EventTable.eventTime]}") }
    }

    fun statisticsByAreaIdAndAlgoIds(areaId: Long, algoIds: List<Long>) {
        database.from(EventTable)
            .select(EventTable.algoId, EventTable.algoName, count(EventTable.algoId))
            .where { (EventTable.areaId eq areaId) and (EventTable.algoId inList algoIds) }
            .groupBy(EventTable.algoId)
    }

    //报错,java.lang.IllegalStateException: No entity class configured for table: 't_event_info'
    //说明没有绑定实体类,是不可以用序列API
    fun notWork() {
        val toList = database.sequenceOf(EventTable)
            .filter { EventTable.areaId eq 1 }
            .toList()
    }

    fun findByAreaIdAndAlgoIds1(areaId: Long, algoIds: List<Long>) {
        //SQL DSL
        val toList = database.from(EventTable1)
            .select(EventTable1.algoId, EventTable1.algoName, EventTable1.eventTime)
            .where { (EventTable1.areaId eq areaId) and (EventTable1.algoId inList algoIds) }
            .map { row -> EventTable1.createEntity(row) }
            .toList()

        //序列
        val toList1: List<Event> = database.events
            .filterColumns { it.columns - it.id - it.areaId }//还可以用-剔除字段
            .filter { EventTable1.areaId eq areaId }
            .filter { EventTable1.algoId inList algoIds }
            .toList()
    }

    fun statisticsByAreaIdAndAlgoIds1(areaId: Long, algoIds: List<Long>) {
        //SQL DSL
        database.from(EventTable1)
            .select(EventTable1.algoId, EventTable1.algoName, count(EventTable1.algoId))
            .where { (EventTable1.areaId eq areaId) and (EventTable1.algoId inList algoIds) }
            .groupBy(EventTable1.algoId)
            .orderBy(count(EventTable1.algoId).desc())
            .forEach { row -> println("算法id:${row.getLong(1)} 算法名称:${row.getString(2)} 事件汇总:${row.getInt(3)}次") }

        //序列
        val toList = database.sequenceOf(EventTable1)
            .filterColumns { it.columns - it.id - it.areaId }
            .filter { EventTable1.areaId eq areaId }
            .filter { EventTable1.algoId inList algoIds }
            .groupingBy { EventTable1.algoId }  //groupBy是一种终止操作,把数据全部取到内存再分组
            .eachCount()
        //如果表结构关联了外表的id,这里用外表id做filter还会自动生成连表SQL
        //生成SQL
        /*select *
                from t_employee
                left join t_department _ref0 on t_employee.department_id = _ref0.id
                where (t_employee.department_id = ?) and (t_employee.manager_id is not null)*/
        //序列 API 会自动 left join 引用表，有时这可能会造成一点浪费。如果你希望对查询进行更细粒度的控制，你可以使用前面章节中介绍的查询 DSL
        //所以单表用序列api自动生成SQL-连表用SQL DSL
    }
}

data class AlgoCnt(
    var algoId: Long,
    var algoName: String,
    var count: Long,
)