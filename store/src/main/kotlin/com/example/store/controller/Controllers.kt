package com.example.store.controller


import com.example.store.service.StoreService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(value = "API for Account service")
@RequestMapping("/api/store")
class StoreController(private val storeService: StoreService) {

    @GetMapping("/presents")
    @ApiOperation(value = "All available products")
    fun getAllProducts() = storeService.getPresents()

    @GetMapping("/{id}")
    @ApiOperation(value = "Getting product by id")
    fun getProductById(@PathVariable("id") id: Long) = storeService.getById(id)
}