package com.example.terminalweb.controller

import com.example.common.constants.AccountType
import com.example.common.constants.ConstEndpoints
import com.example.common.constants.InvoiceType
import com.example.common.forms.*
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.util.R
import com.example.terminalweb.service.*
import org.apache.commons.lang.StringUtils
import org.apache.tomcat.util.json.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.InetAddress
import java.net.URLDecoder
import java.net.UnknownHostException
import java.nio.file.Files
import java.util.*
import javax.activation.MimetypesFileTypeMap
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class AdminDashboardController{
    @GetMapping(ConstEndpoints.URL_MAIN_PAGE)
    fun main() = "/dashboard/main"
}

@Controller
class WebAgentController(private val dealerService: DealerService, private val accountService: AccountService){

    @GetMapping(ConstEndpoints.URL_AJAX_DEALERS_PAGE)
    fun pageDealers() = "/dealer/view"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DEALERS_ALL)
    fun list(@RequestBody filterForm: FilterForm): DataTablesForm<DealerForm?>? {
        return dealerService.getDealersDataTable(filterForm)
    }

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_CLIENT)
    fun pageClients() = "/agent/clients"

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_SUPPLIER)
    fun pageSupplier() = "/agent/suppliers"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_ACCOUNTS)
    fun getAccountDataTable(@RequestBody filterForm: FilterForm) = accountService.getAccountDataTable(filterForm)
}

@Controller
class WebCompanyController(private val settingService: SettingService){
    @Value("\${upload.folder.img}")
    private var IMG_FOLDER: String? = null

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_COMPANY)
    fun page() = "/company/view"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_COMPANIES)
    fun getCompaniesDataTable(@RequestBody filterForm: FilterForm, request: HttpServletRequest?): DataTablesForm<CompanyForm?>? {
        return settingService.getCompanyDataTable(filterForm, request)
    }

    @GetMapping(ConstEndpoints.URL_AJAX_COMPANY_IMG)
    fun img(response: HttpServletResponse?, @PathVariable("filename") filename: String){
        val folder = File(System.getProperty("user.home") + IMG_FOLDER)
        if(!folder.exists()){
            folder.mkdirs()
        }
        val file = File(folder, URLDecoder.decode(filename))
        if(!file.exists()) throw FileNotFoundException()

        /*Finding MIME type for explicitly setting MIME*/
        val mimeType = MimetypesFileTypeMap().getContentType(file)
        response!!.contentType = mimeType

        /*Browser tries to open it*/
        response.addHeader("Content-Disposition", "attachment;filename=$filename")
        response.outputStream.write(Files.readAllBytes(file.toPath()))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_COMPANY_SAVE)
    fun saveCompany(locale: Locale, @ModelAttribute companyForm: CompanyForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(settingService.saveCompany(companyForm))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_COMPANY_LOGO_SAVE)
    fun saveLogo(request: HttpServletRequest, @RequestParam("companyId", required = false) companyId: Long, @RequestParam("file") multipartFile: MultipartFile): ResponseEntity<String> {
        return ResponseEntity.ok(settingService.saveCompanyLogoAndGetURI(request, companyId, multipartFile))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_COMPANY_FAVICON_SAVE)
    fun saveFavicon(request: HttpServletRequest, @RequestParam(value = "companyId", required = false) companyId: Long, @RequestParam(value = "file") multipartFile: MultipartFile): ResponseEntity<String> {
        return ResponseEntity.ok(settingService.saveCompanyFaviconAndGetURI(request, companyId, multipartFile))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_EDIT_COMPANY)
    fun editCompany(locale: Locale, @ModelAttribute companyForm: CompanyForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(settingService.editCompany(companyForm))
    }

    @DeleteMapping(ConstEndpoints.URL_AJAX_DELETE_COMPANY)
    fun deleteCompany(locale: Locale, @PathVariable("id") companyId: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(settingService.deleteCompany(companyId))
    }

    @GetMapping(ConstEndpoints.URL_AJAX_COMPANY_GET)
    fun getCompany(locale: Locale, request: HttpServletRequest, @PathVariable("id") companyId: Long): ResponseEntity<CompanyForm> {
        return ResponseEntity.ok(settingService.getCompanyById(request, companyId))
    }

    @GetMapping(ConstEndpoints.URL_AJAX_COMPANY_LOGO)
    fun getCompanyLogo(response: HttpServletResponse){
        val logoFileName = settingService.getMainCompanyLogoName()

        val folder = File(System.getProperty("user.home") + IMG_FOLDER)
        if(!folder.exists()) folder.mkdirs()

        var file: File? = null
        if(StringUtils.isBlank(logoFileName)) file = File("classpath:/static/media/window_and_door.svg")
        else file = File(folder, URLDecoder.decode(logoFileName))

        if(!file.exists()) throw FileNotFoundException()

        /*Finding MIME type for explicitly setting MIME */
        val mimeType = MimetypesFileTypeMap().getContentType(file)
        response.contentType = mimeType

        /*Browser tries to open it*/
        response.addHeader("Content-Disposition", "attachment;filename=$logoFileName")
        response.outputStream.write(Files.readAllBytes(file.toPath()))
    }

    @GetMapping(ConstEndpoints.URL_AJAX_COMPANY_FAVICON)
    fun getCompanyFavicon(response: HttpServletResponse){
        val faviconFileName = settingService.getMainCompanyFaviconName()

        val folder = File(System.getProperty("user.home") + IMG_FOLDER)
        if(!folder.exists()) folder.mkdirs()

        var file: File? = null
        if(StringUtils.isBlank(faviconFileName)) file = File("classpath:/static/media/favicons/favicon.ico")
        else file = File(folder, URLDecoder.decode(faviconFileName))

        if(!file.exists()) throw FileNotFoundException()

        /*Finding MIME type for explicitly setting MIME */
        val mimeType = MimetypesFileTypeMap().getContentType(file)
        response.contentType = mimeType

        /*Browser tries to open it*/
        response.addHeader("Content-Disposition", "attachment;filename=$faviconFileName")
        response.outputStream.write(Files.readAllBytes(file.toPath()))
    }
}

@Controller
class WebWareHouseController(private val wareHouseService: WareHouseService){
    @GetMapping(ConstEndpoints.URL_AJAX_WAREHOUSES_PAGE)
    fun page() = "/warehouse/view"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_WAREHOUSES)
    fun getWareHouseDatatable(@RequestBody filterForm: FilterForm) = wareHouseService.getWareHouseDataTable(filterForm)

    @PostMapping(ConstEndpoints.URL_AJAX_WAREHOUSE_SAVE)
    fun save(locale: Locale, @ModelAttribute wareHouseForm: WareHouseForm) = ResponseEntity.ok(wareHouseService.saveWarehouse(wareHouseForm))

    @PutMapping(ConstEndpoints.URL_AJAX_WAREHOUSE_EDIT)
    fun edit(locale: Locale, @ModelAttribute wareHouseForm: WareHouseForm) = ResponseEntity.ok(wareHouseService.editWarehouse(wareHouseForm))

    @DeleteMapping(ConstEndpoints.URL_AJAX_WAREHOUSE_DELETE)
    fun deleteWareHouse(locale: Locale, @PathVariable("id") wareHouseId: Long) = ResponseEntity.ok(wareHouseService.deleteWarehouse(wareHouseId))

    @GetMapping(ConstEndpoints.URL_AJAX_WAREHOUSE_GET)
    fun getWareHouse(@PathVariable("id") wareHouseId: Long) = ResponseEntity.ok(wareHouseService.getWarehouse(wareHouseId))
}

@Controller
class WebDataController(private val settingService: SettingService, private val dealerService: DealerService, private val productService: ProductService, private val wareHouseService: WareHouseService, private val accountService: AccountService){
    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_I18N)
    fun i18n(locale: Locale): HashMap<Any, Any> {
        val bundle = R.bundle()
        val map = HashMap<Any, Any>()
        bundle?.keySet()?.forEach{key -> map.put(key, bundle.getString(key))}
        return map
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_COMPANY)
    fun dataCompany() = settingService.findAllCompany()

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_CURRENCY)
    fun dataCurrency() = settingService.findAllCurrency()

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_DEALER)
    fun dataDealers() = dealerService.findAllDealer()

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_DEALER_CLIENT)
    fun dataDealerClient(@PathVariable("account_type") accountType: AccountType) = accountService.findAllDealerClients(accountType)

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_PRODUCT)
    fun dataProduct() = productService.findAllProduct()

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_WAREHOUSE)
    fun dataWareHouse() = wareHouseService.findAllWareHouse()

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_UNIT)
    fun dataUnit() = productService.findAllUnit()

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_DATA_INVOICE_TYPES)
    fun dataInvoiceTypes() = arrayOf(
                                Select2Form(InvoiceType.PRIXOD_BAZA.ordinal.toLong(), InvoiceType.PRIXOD_BAZA.name),
                                Select2Form(InvoiceType.VOZVRAT_BAZA.ordinal.toLong(), InvoiceType.VOZVRAT_BAZA.name),
                                Select2Form(InvoiceType.RASXOD_KLIENT.ordinal.toLong(), InvoiceType.RASXOD_KLIENT.name),
                                Select2Form(InvoiceType.VOZVRAT_KLIENT.ordinal.toLong(), InvoiceType.VOZVRAT_KLIENT.name),
                                Select2Form(InvoiceType.GODOWN.ordinal.toLong(), InvoiceType.GODOWN.name))
}

@Controller
class WebDataTableController{
    @ResponseBody
    @GetMapping(ConstEndpoints.URL_AJAX_DATA_TABLES)
    fun localisation(locale: Locale): JSONParser {
        val jsonString = """{
                              "processing": "${R.getString(locale, "datatables.processing")}",
                              "search": "${R.getString(locale, "datatables.search")}",
                              "lengthMenu": "${R.getString(locale, "datatables.lengthMenu")}",
                              "info": "${R.getString(locale, "datatables.info")}",
                              "infoEmpty": "${R.getString(locale, "datatables.infoEmpty")}",
                              "infoFiltered": "${R.getString(locale, "datatables.infoFiltered")}",
                              "loadingRecords": "${R.getString(locale, "datatables.loadingRecords")}",
                              "zeroRecords": "${R.getString(locale, "datatables.zeroRecords")}",
                              "emptyTable": "${R.getString(locale, "datatables.emptyTable")}",
                              "paginate": {
                                "first": "${R.getString(locale, "datatables.first")}",
                                "previous": "${R.getString(locale, "datatables.previous")}",
                                "next": "${R.getString(locale, "datatables.next")}",
                                "last": "${R.getString(locale, "datatables.last")}"
                              },
                              "aria": {
                                "sortAscending": "${R.getString(locale, "datatables.sortAscending")}",
                                "sortDescending": "${R.getString(locale, "datatables.sortDescending")}"
                              },
                              "select": {
                                "rows": {
                                  "_": "${R.getString(locale, "datatables._")}",
                                  "0": "${R.getString(locale, "datatables.0")}",
                                  "1": "${R.getString(locale, "datatables.1")}"
                                }
                              }
                            }"""
        return JSONParser(jsonString)
    }
}

@Controller
class WebInvoiceController(private val invoiceService: InvoiceService){
    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_INVOICE)
    fun page() = "/invoice/view"

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_INVOICE_ITEM)
    fun pageInvoiceItem(model: Model, @PathVariable("id") invoiceId: Long): String {
        model.addAttribute("invoiceId", invoiceId)
        return "/invoice/invoice_items"
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_INVOICES)
    fun invoiceDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<InvoiceForm?>? {
        return invoiceService.getInvoiceDatatable(locale, filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_INVOICE_ITEMS_BY_ID)
    fun invoiceItemDataTable(locale: Locale, @PathVariable("id") invoiceId: Long): DataTablesForm<InvoiceItemForm?>? {
        return invoiceService.getInvoiceItemsDatatable(invoiceId)
    }
}

@Controller
class WebLoginController{
    @GetMapping(value = [ConstEndpoints.URL_AJAX_LOGIN_PAGE, ConstEndpoints.URL_AJAX_LOGIN])
    fun login(@RequestParam("error", required = false) error: String?, @RequestParam("logout", required = false) logout: String?, @RequestParam("denied", required = false) denied: String?): String {
        return "/user/login"
    }


    @PostMapping(ConstEndpoints.URL_AJAX_LOGIN_PAGE)
    fun loginPost(model: Model, @RequestParam("error", required = false) error: String?, @RequestParam("logout", required = false) logout: String?, @RequestParam("denied", required = false) denied: String?): String{
        if(!StringUtils.isEmpty(error)){
            model.addAttribute("error", true)
        }
        if(!StringUtils.isEmpty(logout)){
            model.addAttribute("logout", true)
        }
        if(!StringUtils.isEmpty(denied)){
            model.addAttribute("denied", true)
        }
        return "/user/login"
    }
}

@Controller
class WebReportController(private val reportService: ReportService){
    @GetMapping(ConstEndpoints.URL_AJAX_REPORTS_PAGE)
    fun page() = "/report/view"

    @PostMapping(ConstEndpoints.URL_AJAX_REPORTS_BY_DATE)
    fun getDataTableReports(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<ReportByDateForm?>? {
        return reportService.getReportsDatatable(filterForm)
    }

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_SALES)
    fun pageSales() = "/report/sales"

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_PURCHASES)
    fun pagePurchases() = "/report/purchases"

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_RETURN_CLIENT)
    fun pageReturnClient() = "/report/returnClient"

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_RETURN_BASE)
    fun pageReturnBase() = "/report/returnBase"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_REPORT_SALES)
    fun getReportSalesDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<InvoiceForm?>? {
        return reportService.getReportSales(locale, filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_REPORT_PURCHASES)
    fun getReportPurchasesDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<InvoiceForm?>? {
        return reportService.getReportPurchases(locale, filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_REPORT_RETURN_CLIENT)
    fun getReportClientDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<InvoiceForm?>? {
        return reportService.getReportReturnClient(locale, filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_REPORT_RETURN_BASE)
    fun getReportReturnBaseDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<InvoiceForm?>? {
        return reportService.getReportReturnBase(locale, filterForm)
    }
}

@Controller
class WebProductAndServiceController(private val productService: ProductService){
    @GetMapping(ConstEndpoints.URL_AJAX_PRODUCTS_PAGE)
    fun pageProduct() = "/product/view"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_PRODUCT_LIST)
    fun dataTableProduct(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<ProductForm?>? {
        return productService.getProductsDataTable(filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_UPLOAD_PRODUCT_EXCEL)
    fun uploadExcel(locale: Locale, @ModelAttribute fileForm: FileForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.uploadExcelProducts(fileForm))
    }

    @ResponseBody
    @GetMapping(ConstEndpoints.URL_AJAX_DOWNLOAD_TEMPLATE_EXCEL, produces = arrayOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
    fun downloadExcel(locale: Locale, @RequestParam("search", required = false, defaultValue = "") searchText: String, response: HttpServletResponse){
        productService.getProductsExcel(locale, searchText, response)
    }

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_PRODUCT_GROUPS)
    fun pageProductGroups() = "/product/productGroup"

    @GetMapping(ConstEndpoints.URL_AJAX_PAGE_UNITS)
    fun pageUnit() = "/product/unit"

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_PRODUCT_GROUP_LIST)
    fun getProductGroupDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<ProductGroupForm?>? {
        return productService.getProductGroupDataTable(filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_UNIT_LIST)
    fun getUnitDataTable(locale: Locale, @RequestBody filterForm: FilterForm): DataTablesForm<UnitForm?>? {
        return productService.getUnitDataTable(filterForm)
    }

    @ResponseBody
    @PostMapping(ConstEndpoints.URL_AJAX_PRODUCT_PRICE_LIST)
    fun getProductPriceDataTable(locale: Locale, @PathVariable("id") id: Long, @RequestBody filterForm: FilterForm): DataTablesForm<ProductPriceForm?>? {
        return productService.getProductPriceDataTable(id, filterForm)
    }

    @GetMapping(ConstEndpoints.URL_AJAX_GET_UNIT)
    fun getUnit(@PathVariable("id") unitId: Long): ResponseEntity<UnitForm> {
        return ResponseEntity.ok(productService.getUnit(unitId))
    }

    @GetMapping(ConstEndpoints.URL_AJAX_GET_PRODUCT_GROUP)
    fun getProductGroup(@PathVariable("id") productGroupId: Long): ResponseEntity<ProductGroupForm> {
        return ResponseEntity.ok(productService.getProductGroup(productGroupId))
    }

    @PutMapping(ConstEndpoints.URL_AJAX_EDIT_UNIT)
    fun editUnit(locale: Locale, @ModelAttribute unitForm: UnitForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.createAndEditUnit(unitForm))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_SAVE_UNIT)
    fun saveUnit(locale: Locale, @ModelAttribute unitForm: UnitForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.createAndEditUnit(unitForm))
    }

    @PutMapping(ConstEndpoints.URL_AJAX_EDIT_PRODUCT_GROUP)
    fun editProductGroup(locale: Locale, @ModelAttribute productGroupForm: ProductGroupForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.createAndEditProductGroup(productGroupForm))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_SAVE_PRODUCT_GROUP)
    fun saveProductGroup(locale: Locale, @ModelAttribute productGroupForm: ProductGroupForm): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.createAndEditProductGroup(productGroupForm))
    }

    @DeleteMapping(ConstEndpoints.URL_AJAX_DELETE_UNIT)
    fun deleteUnit(locale: Locale, @PathVariable("id") unitId: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.deleteUnit(unitId))
    }

    @DeleteMapping(ConstEndpoints.URL_AJAX_DELETE_PRODUCT_GROUP)
    fun deleteProductGroup(locale: Locale, @PathVariable("id") productGroupId: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.deleteProductGroup(productGroupId))
    }

    @PostMapping(ConstEndpoints.URL_AJAX_SAVE_OR_EDIT_PRODUCT, produces = arrayOf(MediaType.MULTIPART_FORM_DATA_VALUE))
    fun saveProduct(lcoale: Locale, @RequestPart("body") productForm: ProductSaveOrEditForm, @RequestPart("images") images: Array<MultipartFile>): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.saveProduct(productForm))
    }

    @ResponseBody
    @GetMapping(ConstEndpoints.URL_AJAX_GET_PRODUCT)
    fun getProduct(@PathVariable("id") productId: Long): ResponseEntity<ProductSaveOrEditForm> {
        return ResponseEntity.ok(productService.getProduct(productId))
    }

    @DeleteMapping(ConstEndpoints.URL_AJAX_DELETE_PRODUCT)
    fun deleteProduct(@PathVariable("id") productId: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(productService.deleteProduct(productId))
    }

}

@Controller
@RequestMapping("/api/connection")
class ApiController(private val serverProperties: ServerProperties, private val qrCodeService: QRCodeService){
    @GetMapping
    @Throws(IOException::class)
    fun index(model: Model): String {
        var myIP: InetAddress? = null
        try {
            myIP = InetAddress.getLocalHost()
        } catch (ex: UnknownHostException){
            ex.printStackTrace()
        }
        model.addAttribute("text", "Api is working!")
        model.addAttribute("qrCodeContent", "/api/connection/generateQRCode?qrContent=http://${myIP!!.hostAddress}:${serverProperties.port}")
        return "index"
    }

    @GetMapping("/generateQRCode")
    fun generateQRCode(qrContent: String, response: HttpServletResponse){
        response.contentType = "image/png"
        val qrCode = qrCodeService.generateQRCode(qrContent, 400, 400)
        if (qrCode != null) {
            response.outputStream.write(qrCode)
        }
    }
}