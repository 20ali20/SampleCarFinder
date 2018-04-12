package com.alimojarrad.fair.Models

data class JsonDOM(
        var results : ArrayList<Result>?=null
)

data class Result(
        var provider: Provider?=null,
        var branch_id : String?=null,
        var location: Location?=null,
        var address: Address?=null,
        var cars : ArrayList<Car>?=null,
        var distance : Int?=null
)

data class Provider(
        var company_code: String? = null,
        var company_name: String? = null
)

data class Location (
        var latitude : Double?=null,
        var longitude : Double?=null
)

data class Address (
        var line1 : String?=null,
        var city : String?=null,
        var region: String?=null,
        var postal_code: String?=null,
        var country: String?=null
)

data class Car (
        var vehicle_info: VehicleInfo?=null,
        var rates: ArrayList<Rate>?=null,
        var estimated_total:Estimate?=null
)

data class VehicleInfo (
        var acriss_code: String?=null,
        var transmission: String?=null,
        var fuel : String?=null,
        var air_conditioning : Boolean?=false,
        var category: String?=null,
        var type : String?=null
)

data class Rate (
    var type : String?=null,
    var price : Price?=null
)

data class Price (
        var amount : String?=null,
        var currency : String?=null
)

data class Estimate (
        var amount : String?=null,
        var currency : String?=null
)