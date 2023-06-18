package com.example.terminalmobile.controller

import com.example.common.dto.*
import com.example.common.util.JwtTokenUtil
import com.example.terminalmobile.service.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(private val accountService: AccountService){
    @GetMapping
    fun getAllAccountsWithType(@RequestParam type: Int) = ResponseEntity.ok(accountService.getAllAccountsWithType(type))

    @GetMapping("/name")
    fun getAllAccountsWithTypeAndNameLike(@RequestParam(name = "type") type: Int, @RequestParam(name = "name") name: String) = ResponseEntity.ok(accountService.getAllAccountsWithTypeAndNameLike(type, name))
}

@RestController("DealerControllerMobile")
@RequestMapping("/api/v1/dealer")
class DealerController(private val dealerService: DealerService){
    @GetMapping
    fun getDealers(@RequestParam("name") name: String): ResponseEntity<List<DealerDto?>> {
        return ResponseEntity.ok(dealerService.getDealersWithName(name))
    }
}

@RestController
@RequestMapping("/api/v1/invoice")
class InvoiceController(private val invoiceService: InvoiceService){

    @PostMapping
    fun createInvoice(@RequestParam source: Int, @RequestParam wareHouseId: Int, @RequestParam uid: String, @RequestBody @Valid invoiceDto: InvoiceDto): ResponseEntity<Long> {
        return if(invoiceService.getInvoiceUid(uid) == null) ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(source, wareHouseId, invoiceDto, uid))
               else ResponseEntity.status(HttpStatus.OK).body(invoiceService.update(source, wareHouseId, invoiceDto, uid))
    }

    @GetMapping
    fun getInvoices(@RequestParam type: Int,
                    @RequestParam(value = "wareHouseId", required = false) wareHouseId: Int,
                    @RequestParam source: Int,
                    @RequestParam(name = "page", defaultValue = "1", required = false) page: Int,
                    @RequestParam(name = "size", defaultValue = "10", required = false) size: Int,
                    @RequestParam(name = "from", defaultValue = "1970-01-01", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") fromDate: LocalDate,
                    @RequestParam(name = "to", defaultValue = "2100-01-01", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") toDate: LocalDate): ResponseEntity<PagedResponseDto<InvoiceDto?>> {
        return ResponseEntity.ok(invoiceService.getAll(type, wareHouseId, source, page, size, fromDate, toDate))
    }

    @GetMapping("/{id}/items")
    fun getInvoiceItems(@PathVariable id: Long): ResponseEntity<List<InvoiceItemDto?>> {
        return ResponseEntity.ok(invoiceService.getAllItemsById(id))
    }
}

@RestController
@RequestMapping("/api/v1/product")
class ProductController(private val productService: ProductService){
    @GetMapping("/product-barcode/{barcode}")
    fun getProductListByBarcode(@PathVariable barcode: String): ResponseEntity<List<ProductDto?>> {
        return ResponseEntity.ok(productService.findProductListByBarcode(barcode))
    }

    @GetMapping("/get-product-by-name/{product_name}")
    fun getProductByName(@PathVariable("product_name") productName: String): ResponseEntity<List<ProductDto?>> {
        return ResponseEntity.ok(productService.findProductByNameLike(productName))
    }

    @GetMapping("/get-product-balance-by-id/{product_id}/{warehouse_id}")
    fun getProductOstatokById(@PathVariable("product_id") productId: Int, @PathVariable("warehouse_id") wareHouseId: Int): ResponseEntity<BigDecimal> {
        return ResponseEntity.ok(productService.getProductOstatokById(productId, wareHouseId) ?: BigDecimal.ZERO)
    }
}


@RestController
@RequestMapping("/api/v1/report")
class ReportController(private val reportService: ReportService){
    @GetMapping
    fun getReportByDate(@RequestParam("from", required = false, defaultValue = "") from: String,
                        @RequestParam("to", required = false, defaultValue = "") to: String,
                        @RequestParam("itemsPerPage", required = false, defaultValue = "5") itemsPerPage: Int,
                        @RequestParam("numberOfPage", required = false, defaultValue = "1") numberOfPage: Int): ResponseEntity<List<ReportByDateDto?>> {
        return ResponseEntity.ok().body(reportService.getProductsByDate(from, to, itemsPerPage, numberOfPage))
    }

    @GetMapping("/today")
    fun getTodayIncomeExpenseReport(@RequestParam source: Int, @RequestParam wareHouseId: Int): ResponseEntity<ReportTodayIncomeExpenseDto> {
        return ResponseEntity.ok().body(reportService.getTodaysReport(source, wareHouseId))
    }
}

@RestController
@RequestMapping("/api/v1/test")
class TestConnectionController{
    @GetMapping
    fun testConnection() = ResponseEntity.ok(true)
}

@RestController
@RequestMapping("/api/v1/user")
class UserController(private val userService: UserService, private val jwtTokenUtil: JwtTokenUtil){
    @GetMapping("/login/{login}/{password}")
    fun getLoginPass(@PathVariable("login") login: String, @PathVariable("password") password: String, model: Model): ResponseEntity<UserDto> {
        val dto = userService.login(login, password) ?: return ResponseEntity.notFound().build()
        dto.jwtToken = jwtTokenUtil.generateToken(dto.login!!)
        model.addAttribute("name", "Saidov Oxunjon")
        return ResponseEntity.ok(dto)
    }
}

@RestController
@RequestMapping("/api/v1/warehouse")
class WareHouseController(private val wareHouseService: WareHouseService){
    @GetMapping
    fun getAllWareHouses(@RequestParam(value = "name", required = false, defaultValue = "") wareHouseName: String): ResponseEntity<List<WareHouseDto?>> {
        return ResponseEntity.ok(wareHouseService.getAllWareHousesWithName(wareHouseName))
    }
}