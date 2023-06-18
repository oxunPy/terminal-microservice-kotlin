package com.example.terminalweb.util

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType

class PoiUtil {
    companion object{
        fun getStringValueFromCell(cell: Cell): String {
            when (cell.cellType) {
                CellType.NUMERIC -> return cell.numericCellValue.toString()
                CellType.STRING -> return cell.stringCellValue.toString()
                CellType.BOOLEAN -> return cell.booleanCellValue.toString()
                else -> return ""
            }
        }
    }

}