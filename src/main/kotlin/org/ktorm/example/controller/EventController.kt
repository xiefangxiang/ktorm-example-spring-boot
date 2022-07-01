package org.ktorm.example.controller

import org.ktorm.example.dao.EventDao
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author xiefangxiang
 * @date 2022/6/30 17:39
 */

@RestController
class EventController(private val eventDao: EventDao) {
    @GetMapping("/query")
    fun query() {
        eventDao.statistics(1, listOf(10, 11))
        eventDao.statistics()
        //eventDao.statistics1()
        eventDao.statistics2(1, listOf(10, 11))
    }
}