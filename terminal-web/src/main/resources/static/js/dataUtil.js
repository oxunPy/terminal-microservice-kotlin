/**
 * Created by Xurshidbek on 04.06.2018.
 */

var _url_ajax_i18n_list = '/data/i18n';
var _url_ajax_company_list = '/data/company';
var _url_ajax_currency_list = '/data/currency';
var _url_ajax_dealer_list = '/data/dealer';
var _url_ajax_dealer_client_list = '/data/dealerClient';
var _url_ajax_product_list = '/data/product';
var _url_ajax_product_group = '/data/productGroup';
var _url_ajax_warehouse = '/data/warehouse';
var _url_ajax_invoicetypes = '/data/invoicetypes';
var _url_ajax_unit_list = '/data/unit';

var DataModule = function () {
    function _ajaxBase(url, queryParams, callback) {
        $.ajax({
            url: url,
            method: 'POST',
            contentType: 'application/json',
            data: queryParams,
            cache: false,
            dataType: 'json',
            success: function (data) {
                callback.success(data);
            }
        });
    }

    function _select2Base(selector, url, queryParams, selectedValue) {
        $.ajax({
            url: url,
            method: 'POST',
            contentType: 'application/json',
            data: queryParams,
            cache: false,
            dataType: 'json',
            success: function (data) {
                //  select2 ga ro'yhatni yuklash
                var elem = selector.select2({
                    placeholder: '',
                    allowClear: true,
                    data: data
                });
                //  select2 da tanlab qo'yish
                selector.val(selectedValue).trigger('change');
            }
        });
    }


    function _select2TreeBase(selector, url, queryParams, selectedValue) {
        $.ajax({
            url: url,
            method: 'POST',
            contentType: 'application/json',
            data: queryParams,
            cache: false,
            dataType: 'json',
            success: function (data) {
                //  Tree base select2
                var elem = selector.select2ToTree({
                    treeData: {
                        dataArr: data,
                        valFld: "id",
                        labelFld: "text",
                        incFld: 'children'
                    }
                });
                selector.val(selectedValue).trigger('change');
            }
        });
    }

    // public functions
    return {
        //main function
        init: function () {
            //initialize here something.
        },

        i18n: function (callback) {
            return _ajaxBase(_url_ajax_i18n_list, null, callback);
        },
        select2Company: function(selector, queryParams, selectedValue) {
            return _select2Base(selector, _url_ajax_company_list, queryParams, selectedValue);
        },
        select2Dealer: function(selector, queryParams, selectedValue) {
            return _select2Base(selector, _url_ajax_dealer_list, queryParams, selectedValue);
        },
        select2Currency: function(selector, queryParam, selectedValue){
            return _select2Base(selector,_url_ajax_currency_list, queryParam, selectedValue);
        },
        select2DealerClient: function(selector, pathVariable, queryParam, selectedValue){
            return _select2Base(selector, _url_ajax_dealer_client_list + pathVariable, queryParam, selectedValue);
        },
        select2Product: function(selector, queryParam, selectedValue){
            return _select2Base(selector, _url_ajax_product_list, queryParam, selectedValue);
        },
        select2Unit: function(selector, queryParam, selectedValue){
            return _select2Base(selector, _url_ajax_unit_list, queryParam, selectedValue)
        },
        select2ProductGroup: function(selector, parentId, queryParam, selectedValue){
            $.ajax({
                url: _url_ajax_product_group,
                method: 'POST',
                data: queryParam,
                contentType: 'application/json',
                cache: false,
                dataType: 'json',
                success: function (data) {
                    let elem = selector.select2({
                        placeholder: '',
                        allowClear: true,
                        data: data
                    });
                    selector.val(parentId).trigger('change');
                }
            });
        },
        select2WareHouse: function(selector, queryParam, selectedValue){
            $.ajax({
                url: _url_ajax_warehouse,
                method: 'POST',
                data: queryParam,
                contentType: 'application/json',
                cache: false,
                dataType: 'json',
                success: function (data) {
                    //  Tree base select2
                    var elem = selector.select2ToTree({
                        treeData: {
                            dataArr: data,
                            valFld: "id",
                            labelFld: "name",
                        }
                    });

                    selector.val(data.filter((d)=> d.isDefault)[0].id).trigger('change');
                }
            });
        },
        select2InvoiceTypes: function(selector, queryParam, selectedValue){
            return _select2Base(selector, _url_ajax_invoicetypes, queryParam, selectedValue);
        },
        select2Boolean: function (selector, queryParams, selectedValue) {
            selector.select2({
                placeholder: '',
                allowClear: true,
                data: [
                    {"id": "", "text": '' + _('label.not.applicable') + ''},
                    {"id": "true", "text": '' + _('label.yes') + ''},
                    {"id": "false", "text": '' + _('label.no') + ''}
                ]
            });
        }
    };

}();