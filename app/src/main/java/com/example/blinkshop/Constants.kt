package com.example.blinkshop

object Constants {
    val  MERCHANT_ID = "PGTESTPAYUAT"
    val SALT_KEY = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399"
    var apiEndPoint = "/pg/v1/pay"
    val merchantTransactionId=System.currentTimeMillis().toString()
    val merchantUserId=System.currentTimeMillis().toString()

    val allProductsCategory = arrayOf(
        "Vegetables & Fruits",
        "Dairy & Breakfast",
        "Munchies",
        "Cold Drinks & Juices",
        "Instant & Frozen Food",
        "Tea Coffee & healthy Drinks",
        "Bakery & Biscuits",
        "Sweet Tooth",
        "Atta ,Rice & Dal",
        "Dry Fruits Masala & Oil",
        "Sauces & Spreads",
        "Chicken Meat & Fish",
        "Pan Corner",
        "Organic & Premium",
        "Baby Care",
        "Pharma & Wellness",
        "Cleaning Essential",
        "Home & Office",
        "Personal Care",
        "Pet Care"
    )
    val allProductsCategoryIcon = arrayOf(
        R.drawable.vegetable,
        R.drawable.dairy_breakfast,
        R.drawable.munchies,
        R.drawable.cold_and_juices,
        R.drawable.instant,
        R.drawable.tea,
        R.drawable.bakery_biscuits,
        R.drawable.sweet_tooth,
        R.drawable.atta_rice,
        R.drawable.masala,
        R.drawable.sauce_spreads,
        R.drawable.chicken_meat,
        R.drawable.paan_corner,
        R.drawable.organic_premium,
        R.drawable.baby,
        R.drawable.pharma_wellness,
        R.drawable.cleaning,
        R.drawable.home_office,
        R.drawable.personal_care,
        R.drawable.pet_care,

    )
}