package com.example.common.forms

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.util.*

data class AccountForm (
    var id: Int? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var printableName: String? = null,

    var phoneNumber: String? = null,

    var openingBalance: BigDecimal? = null
)


data class CompanyForm (
    var id: Long? = null,

    var companyName: String? = null,

    var faviconUrl: String? = null,

    var logoImgUrl: String? = null,

    var manager: String? = null,

    var director: String? = null,

    var email: String? = null,

    var isMain: Boolean? = null,

    var motto: String? = null,

    var phoneNumber: String? = null,

    var telegramContact: String? = null,

    var favicon: MultipartFile? = null,

    var logo: MultipartFile? = null,
)

data class DealerForm(
    var id: Long? = null,

    var name: String? = null,

    var dealerCode: String? = null,
)

data class FileForm(
    var fileName: String? = null,

    var file: MultipartFile? = null
)

data class InvoiceForm (
    var id: Long? = null,

    @NotNull(message = "Идентификатор учетной записи не должен быть нулевым")
    var dealerClientId: Long? = null,

    var printableName: String? = null,

    var warehouseId: Long? = null,

    var warehouse: String? = null,

    @NotNull(message = "Дата не может быть нулевой")
    var date: Date? = null,

    var type: String? = null,

    var info: String? = null,

    var countOfItems: Long? = null,

    var total: BigDecimal? = null
)

data class InvoiceItemForm (
    var id: Long? = null,

    var productName: String? = null,

    @DecimalMin(value = "0",message = "Значение количества должно быть положительным")
    var quantity: BigDecimal? = null,

    @DecimalMin(value = "0", message = "Значение курса должно быть положительным")
    var rate: BigDecimal? = null,

    @DecimalMin(value = "0", message = "Значение курса продажи должно быть положительным")
    var sellingRate: BigDecimal? = null,

    @DecimalMin(value = "0", message = "Значение первоначальной продажи быть положительным")
    var originalRate: BigDecimal? = null,

    @NotNull(message = "Значение валюты не должно быть нулевым")
    var currencyCode: String? = null,

    var total: BigDecimal? = null,
)


data class ProductForm (
    var id: Long? = null,

    var productName: String? = null,

    var rate: BigDecimal? = null,

    var originalRate: BigDecimal? = null,

    var currency: String? = null,

    var groupName: String? = null,

    var barcode: String? = null,
)

data class ProductPriceForm (
    var id: Long? = null,

    var date: String? = null,

    var standardCost: String? = null,

    var currencyId: Long? = null,

    var currency: String? = null,
)

data class ProductSaveOrEditForm (
    var id: Long? = null,

    var productName: String? = null,

    var barcode: String? = null,

    var productGroupId: Long? = null,

    var baseUnitId0: Long? = null,

    var baseUnitId1: Long? = null,

    var baseUnitVal0: Double? = null,

    var baseUnitVal1: Double? = null,

    var buyProductPrice: ProductPriceForm? = null,

    var sellProductPrice: ProductPriceForm? = null,
)

data class ProductGroupForm (
    var id: Long? = null,

    var name: String? = null,

    var info: String? = null,

    var parentId: Long? = null,

    var parentName: String? = null,
)

data class ReportByDateForm (
    var invoiceItemId: Int? = null,

    var dateOfSell: String? = null,

    var productQuantity: BigDecimal? = null,

    var productPrice: BigDecimal? = null,

    var typeOfOperation: Int? = null,

    var productName: String? = null,

    var priceCurrency: Int? = null,
)

data class ReportForm (
    var id: Long? = null,

    var date: Date? = null,

    var clientName: String? = null,

    var warehouse: String? = null,
)

data class WareHouseForm(
    var id: Long? = null,

    var name: String? = null,

    var isDefault: Boolean? = null
)


data class Select2Form(
    var id: Long? = null,

    var text: String? = null,

    var children: List<Select2Form>? = null
)

data class UnitForm(
    var id: Long? = null,

    var name: String? = null,

    var symbol: String? = null
)
