package com.example.minishop

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    var checked: Boolean = false
) : Serializable