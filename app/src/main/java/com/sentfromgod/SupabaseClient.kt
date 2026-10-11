
package com.sentfromgod.app

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://ldcditsyijrvjhhwemek.supabase.co",
        supabaseKey = "sb_publishable_AGDMtWBKRlC1bYRmE0V5IA_KqC8R637"
    ) {
        install(Auth)
    }
}
