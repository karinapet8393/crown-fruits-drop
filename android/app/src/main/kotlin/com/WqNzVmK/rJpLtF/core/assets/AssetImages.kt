package com.WqNzVmK.rJpLtF.core.assets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView

// Инфраструктура шаблона — НЕ удалять и не переписывать.
// AI-ассеты лежат в <project>/assets/*.png (gen-assets.sh), Gradle пакует эту папку
// как Android assets (sourceSets в app/build.gradle.kts). Имена файлов — только через
// константы AppAssets (core/assets/AppAssets.kt), не строками во фрагментах.
object AssetImages {

    // ~1/8 heap: фоны 1080x1920 не декодируются заново на каждом экране.
    private val cache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8 / 1024).toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    /** PNG из assets или null, если файла нет (приложение не падает). */
    fun bitmap(context: Context, fileName: String): Bitmap? {
        cache.get(fileName)?.let { return it }
        val decoded = runCatching {
            context.applicationContext.assets.open(fileName).use { BitmapFactory.decodeStream(it) }
        }.getOrNull() ?: return null
        cache.put(fileName, decoded)
        return decoded
    }
}

/**
 * Ставит картинку из assets в ImageView. Возвращает false, если файла нет —
 * тогда ImageView остаётся с фоном/placeholder из XML.
 */
fun ImageView.loadAsset(fileName: String): Boolean {
    val bitmap = AssetImages.bitmap(context, fileName) ?: return false
    setImageBitmap(bitmap)
    return true
}
