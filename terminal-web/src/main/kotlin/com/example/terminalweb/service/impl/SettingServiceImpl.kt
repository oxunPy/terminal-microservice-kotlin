package com.example.terminalweb.service.impl

import com.example.common.constants.IMG_TYPE
import com.example.common.forms.CompanyForm
import com.example.common.forms.Select2Form
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.forms.table.OrderForm
import com.example.terminalweb.repository.SettingRepository
import com.example.common.dto.data_interface.CompanyDtoProjection
import com.example.terminalweb.service.SettingService
import com.example.terminalweb.util.RequestUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@Service("SettingService")
class SettingServiceImpl(private val settingRepository: SettingRepository): SettingService {

    @Value("\${upload.folder.img}")
    private var IMG_FOLDER: String? = null

    override fun getCompanyDataTable(filterForm: FilterForm?, request: HttpServletRequest?): DataTablesForm<CompanyForm?>? {
        val sort = getSort(filterForm!!.orderMap())

        if(sort.isEmpty){
            sort.and(Sort.Order.asc("id"))
        }

        val pageRequest = PageRequest.of(filterForm.start / filterForm.length, filterForm.length, sort)
        val pageCompanies = settingRepository.getCompanies(pageRequest)
        val companyList = pageCompanies!!.stream()
            .map { CompanyForm(it!!.getId(), it.getCompanyName(),
                               RequestUtil.getRequestURI(request!!, "/img/company/" + it.getFaviconImgName()),
                               RequestUtil.getRequestURI(request, "/img/company/" + it.getLogoImgName()),
                               it.getManager(), it.getDirector(), it.getEmail(), it.getIsMain(), it.getMotto(), it.getPhoneNumber(), it.getTelegramContact())}
            .collect(Collectors.toList())

        val dataTable: DataTablesForm<CompanyForm?> = DataTablesForm()
        dataTable.data = companyList
        dataTable.draw = filterForm.draw
        dataTable.recordsFiltered = pageCompanies.totalElements.toInt()
        dataTable.recordsTotal = pageCompanies.totalElements.toInt()
        return dataTable
    }

    override fun saveCompany(companyForm: CompanyForm?): Boolean? {
        if(companyForm == null) return null
        var newCompanyId = settingRepository.createOrUpdateCompany(companyForm.companyName, companyForm.isMain == null, companyForm.phoneNumber, companyForm.motto,
                                                                   companyForm.director, companyForm.manager, companyForm.telegramContact, companyForm.email, null)
        // save company logo and favicon
        if(companyForm.favicon != null) saveFile(companyForm.favicon!!, newCompanyId, IMG_TYPE.LOGO)
        if(companyForm.logo != null) saveFile(companyForm.logo!!, newCompanyId, IMG_TYPE.LOGO)

        if(companyForm.isMain!! && newCompanyId != null) setCompanyMain(newCompanyId)
        return newCompanyId != null
    }

    override fun editCompany(companyForm: CompanyForm?): Boolean? {
        if (companyForm?.id == null) return null
        val existingForm = getCompanyById(null, companyForm.id)
        if (companyForm.isMain != null && companyForm.isMain!!) {
            setCompanyMain(companyForm.id!!)
        }
        if (companyForm.director != null) existingForm!!.director = companyForm.director
        if (companyForm.companyName != null) existingForm!!.companyName = companyForm.companyName
        if (companyForm.email != null) existingForm!!.email = companyForm.email
        if (companyForm.manager != null) existingForm!!.manager = companyForm.manager
        if (companyForm.motto != null) existingForm!!.motto = companyForm.motto
        if (companyForm.phoneNumber != null) existingForm!!.phoneNumber = companyForm.phoneNumber
        if (companyForm.telegramContact != null) existingForm!!.telegramContact = companyForm.telegramContact
        existingForm!!.isMain = (companyForm.isMain != null && existingForm.isMain!!)

        if (companyForm.logo != null && !companyForm.logo!!.isEmpty) saveFile(
            companyForm.logo!!,
            companyForm.id,
            IMG_TYPE.LOGO
        )
        if (companyForm.favicon != null && !companyForm.favicon!!.isEmpty) saveFile(
            companyForm.favicon!!,
            companyForm.id,
            IMG_TYPE.FAVICON
        )
        return settingRepository.createOrUpdateCompany(existingForm.companyName, existingForm.isMain, existingForm.phoneNumber, existingForm.motto, existingForm.director,
                                                       existingForm.manager, existingForm.telegramContact, existingForm.email, existingForm.id) == companyForm.id
    }

    override fun deleteCompany(companyId: Long?): Boolean? {
        return settingRepository.deleteCompany(companyId)
    }

    override fun saveCompanyLogoAndGetURI(request: HttpServletRequest?, companyId: Long?, file: MultipartFile?): String? {
        var filePath: String? = null
        if (file != null && !file.isEmpty) {
            filePath = saveFile(file, companyId, IMG_TYPE.LOGO);              // saves files and return fileNames
        }
        return getFileUrls(request!!, arrayOf(filePath!!)).get(0);
    }

    override fun saveCompanyFaviconAndGetURI(request: HttpServletRequest?, companyId: Long?, file: MultipartFile? ): String? {
        var filePath: String? = null
        if(file != null && !file.isEmpty){
            filePath = saveFile(file, companyId, IMG_TYPE.FAVICON)
        }
        return getFileUrls(request!!, arrayOf(filePath!!)).get(0)
    }

    override fun findAllCompany(): List<Select2Form?>? {
        return settingRepository.getCompanies(PageRequest.of(0, Integer.MAX_VALUE))!!.content.stream()
            .map { Select2Form(it!!.getId(), it.getCompanyName()) }.collect(Collectors.toList())
    }

    override fun findAllCurrency(): List<Select2Form?>? {
        return settingRepository.getAllCurrencies()!!.stream().map { Select2Form(it!!.getId(), it.getCode()) }.collect(Collectors.toList())
    }

    override fun getCompanyById(request: HttpServletRequest?, id: Long?): CompanyForm? {
        val companyOpt: Optional<CompanyDtoProjection?> = settingRepository.getCompanies(PageRequest.of(0, Integer.MAX_VALUE))!!
                                                                           .stream().filter { it!!.getId() == id }.findFirst()

        return companyOpt.map {
                                CompanyForm(it!!.getId(), it.getCompanyName(), "", "", it.getManager(), it.getDirector(),
                                            it.getEmail(), it.getIsMain(), it.getMotto(), it.getPhoneNumber(), it.getTelegramContact())}.get()
    }

    override fun getMainCompanyLogoName(): String? {
        return settingRepository.getMainCompanyLogoName()
    }

    override fun getMainCompanyFaviconName(): String? {
        return settingRepository.getMainCompanyFaviconName()
    }


    private fun getSort(orderMap: Map<String, OrderForm>?): Sort {
        val orders: ArrayList<Sort.Order> = ArrayList()
        orderMap?.forEach{ (column, orderForm) ->
            var property: String? = null

            when(column){
                "name" -> property = "name"
                "director" -> property = "director"
                "manager" -> property = "manager"
            }
            val direction = Sort.Direction.fromString(orderForm.dir!!)
            orders.add(if(direction.isAscending) Sort.Order.asc(property!!) else Sort.Order.desc(property!!))
        }
        return Sort.by(orders)
    }

    private fun setCompanyMain(companyId: Long): Boolean {
        return settingRepository.makeCompanyMain(companyId)!!
    }

    private fun setCompanyFavicon(companyId: Long, fileId: Long) {
        settingRepository.setFavicon(companyId, fileId)
    }

    private fun setCompanyLogo(companyId: Long, fileId: Long) {
        settingRepository.setLogo(companyId, fileId)
    }

    private fun getFileUrls(request: HttpServletRequest, filePaths: Array<String>?): List<String>{
        if(filePaths == null) return ArrayList()

        val fileUrls = ArrayList<String>()
        for(filePath in filePaths){
            fileUrls.add(RequestUtil.getRequestURL(request, "api/mobile/message/file/" + URLEncoder.encode(filePath))!!)
        }
        return fileUrls
    }

    fun saveFile(file: MultipartFile, companyId: Long?, imgType: IMG_TYPE): String? {                      // save files and return fileNames
        val folder = File(System.getProperty("user.home") + IMG_FOLDER)
        if (!folder.exists()) folder.mkdirs()
        var filePath: String? = null
        try {
            val newFileName = UUID.randomUUID().toString() + "_" + file.originalFilename!!
                .replace("[+]".toRegex(), " ")
            filePath = newFileName
            Files.copy(
                file.inputStream,
                Paths.get(System.getProperty("user.home") + IMG_FOLDER).resolve(newFileName),
                StandardCopyOption.REPLACE_EXISTING
            )

            // #save file and set it on company
            val fileId = settingRepository.createAsFile(newFileName, file.originalFilename)
            if (imgType === IMG_TYPE.FAVICON) setCompanyFavicon(companyId!!, fileId!!)
            if (imgType === IMG_TYPE.LOGO) setCompanyLogo(companyId!!, fileId!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return filePath
    }
}