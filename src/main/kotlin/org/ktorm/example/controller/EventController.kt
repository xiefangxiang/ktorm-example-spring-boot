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
        eventDao.findByAreaIdAndAlgoIds1(1, listOf(10, 11))
        eventDao.statisticsByAreaIdAndAlgoIds1(1, listOf(10, 11))
    }
}