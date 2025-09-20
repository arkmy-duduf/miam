
package com.example.miam.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.miam.data.dao.*
@Database(
  entities=[Product::class, InventoryItem::class, Recipe::class, RecipeIngredient::class, ShoppingItem::class],
  version=2,
  exportSchema=false
)
abstract class AppDatabase: RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun inventoryDao(): InventoryDao
  abstract fun recipeDao(): RecipeDao
  abstract fun shoppingDao(): ShoppingDao
  companion object {
    @Volatile private var INSTANCE: AppDatabase? = null
    fun get(ctx: Context): AppDatabase = INSTANCE ?: synchronized(this) {
      Room.databaseBuilder(ctx, AppDatabase::class.java, "miam.db")
        .fallbackToDestructiveMigration()
        .build().also { INSTANCE = it }
    }
  }
}
