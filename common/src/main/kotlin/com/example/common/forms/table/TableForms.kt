package com.example.common.forms.table

import java.io.Serializable
import java.util.stream.Collectors

data class ColumnsForm(
    var name: String? = null,

    var data: String? = null,

    var searchable: Boolean = false,

    var orderable: Boolean = false,

    var searchForm: SearchForm? = null
) : Serializable  {
    companion object {
        private const val serialVersionUID = -546384123348787675L
    }
}


data class DataTablesForm<T>(
    var draw: Int = 0,

    var recordsTotal: Int = 0,

    var recordsFiltered: Int = 0,

    var error: String? = null,

    var data: List<T>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 4567927313238036854L
    }
}

data class FilterForm(
    var draw: Int = 0,

    var start: Int = 0,

    var length: Int = 0,

    var columns: List<ColumnsForm> = listOf(),

    var order: List<OrderForm>? = listOf(),

    var search: SearchForm? = null,

    var filter: Map<String, Any>? = null,

    val searchText: String? = search?.value
) : Serializable {

    fun orderMap(): Map<String, OrderForm>? {
        if(order == null) return null
        return order!!.stream().filter { columns.size > it.column }.collect(Collectors.toMap({columns.get(it.column).data}, {it}))
    }

    companion object {
        private const val serialVersionUID = -1183975305038088044L
    }
}

data class OrderForm(
    var column: Int = 0,

    var dir: String? = null

) : Serializable {
    companion object {
        private const val serialVersionUID = -2069131671088646133L
    }
}

data class SearchForm(
    var value: String? = null,

    var regex: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = -3029620531741939122L
    }
}
