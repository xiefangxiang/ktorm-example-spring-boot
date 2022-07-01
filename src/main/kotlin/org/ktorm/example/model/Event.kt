package org.ktorm.example.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import java.time.LocalDateTime

/**
 * @author xiefangxiang
 * @date 2022/6/30 16:46
 */

//表结构,没有进行实体类和列绑定,只能用于SQL DSL
object EventTable : Table<Nothing>("t_event_info") {
    val id = long("id").primaryKey()
    val areaId = long("area_id")
    val algoId = long("algorithm_id")
    val algoName = varchar("algorithm_name")
    val eventTime = datetime("event_time")
}

//除了 SQL DSL 以外,Ktorm 也支持实体对象.既支持SQL DSL也支持sequenceOf序列API
//表结构,这种实体类与列绑定,就可以使用序列 API 对实体进行各种灵活的操作,这种自动生成SQL
//先给 Database 定义两个扩展属性，它们使用 sequenceOf 函数创建序列对象并返回,这两个属性可以帮助我们提高代码的可读性：
//val Database.departments get() = this.sequenceOf(Departments)
//Ktorm 提供了一套名为”实体序列”的 API，用来从数据库中获取实体对象。正如其名字所示，它的风格和使用方式与 Kotlin 标准库中的序列 API 极其类似，它提供了许多同名的扩展函数，比如 filter、map、reduce 等
object EventTable1 : Table<Event>("t_event_info") {
    val id = long("id").primaryKey().bindTo { it.id }
    val areaId = long("area_id").bindTo { it.areaId }
    val algoId = long("algorithm_id").bindTo { it.algoId }
    val algoName = varchar("algorithm_name").bindTo { it.algoName }
    val eventTime = datetime("event_time").bindTo { it.eventTime }
}

//实体类
interface Event : Entity<Event> {
    companion object : Entity.Factory<Event>()

    val id: Long
    val areaId: Long
    val algoId: Long
    val algoName: String
    val eventTime: LocalDateTime
}

val Database.events get() = this.sequenceOf(EventTable1)