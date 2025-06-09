package com.jule.food

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID


class Recipe(
    name: String,
    val images: SnapshotStateList<String> = mutableStateListOf(),
    val groceries: SnapshotStateList<GroceryItem> = mutableStateListOf(),
    val tags: MutableList<UUID> = mutableListOf()
) {
    var name by mutableStateOf(name)
    val id: UUID = UUID.randomUUID()
}
@Serializable
data class Tag(
    var name: String,
    var iconIndex: Int,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)

@Serializable
class SaveableRecipe(
    val name: String,
    val images: List<String>,
    val groceries: List<SaveableGroceryItem>,
    val tags: List<@Serializable(with = UUIDSerializer::class)UUID>,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)
@Serializable
class SaveableRecipes(
    val recipes: List<SaveableRecipe>,
    val tags: List<Tag>,
    val favoriteTags: List<@Serializable(with = UUIDSerializer::class)UUID>
)

fun getTagsFromIds(ids: List<UUID>, tags: List<Tag>): List<Tag> {
    return tags.filter { ids.contains(it.id) }
}
fun getTagFromId(id: UUID, tags: List<Tag>): Tag {
    return tags[tags.indexOfFirst { it.id == id } ]
}
fun getRecipeFromId(id: UUID, recipes: List<Recipe>): Recipe {
    return recipes[recipes.indexOfFirst { it.id == id} ]
}

@DrawableRes val tagIcons: List<Int> = listOf(
    R.drawable.apple,
    R.drawable.aubergine,
    R.drawable.baguette,
    R.drawable.banana,
    R.drawable.burrito,
    R.drawable.can_food,
    R.drawable.candy,
    R.drawable.candy_bar,
    R.drawable.candy_cane,
    R.drawable.carrot,
    R.drawable.cheese,
    R.drawable.chili,
    R.drawable.chopsticks_noodles,
    R.drawable.chopsticks_noodles_bowl,
    R.drawable.citrus_slice,
    R.drawable.cocktail,
    R.drawable.corn,
    R.drawable.croissant,
    R.drawable.cupcake,
    R.drawable.cup_straw,
    R.drawable.drumstick,
    R.drawable.egg,
    R.drawable.egg_fried,
    R.drawable.fish,
    R.drawable.fondue,
    R.drawable.french_fries,
    R.drawable.grape,
    R.drawable.grill,
    R.drawable.grocery_basket,
    R.drawable.hamburger,
    R.drawable.hat_chef,
    R.drawable.hotdog,
    R.drawable.jar,
    R.drawable.leafy_green,
    R.drawable.melon,
    R.drawable.microwave,
    R.drawable.mug,
    R.drawable.mug_hot,
    R.drawable.mushroom,
    R.drawable.noodle,
    R.drawable.onion,
    R.drawable.oven,
    R.drawable.peanuts,
    R.drawable.peapod,
    R.drawable.pear,
    R.drawable.pepper,
    R.drawable.picnic,
    R.drawable.pineapple,
    R.drawable.pizza_slice,
    R.drawable.popcorn,
    R.drawable.radish,
    R.drawable.rice,
    R.drawable.salad,
    R.drawable.salt_shaker,
    R.drawable.sandwich,
    R.drawable.shrimp,
    R.drawable.soup,
    R.drawable.skewer,
    R.drawable.steak,
    R.drawable.strawberry,
    R.drawable.stroopwafel,
    R.drawable.sushi,
    R.drawable.tomato,
    R.drawable.wheat,
    R.drawable.wheat_no,
    R.drawable.utensils,
    R.drawable.restaurant,
    R.drawable.room_service
)

class RecipeViewModel : ViewModel() {
    private var _recipes = mutableStateListOf<Recipe>()
    val recipes get() = _recipes

    private var _tags = mutableStateListOf<Tag>()
    val tags get() = _tags

    private var _favoriteTags = mutableStateListOf<UUID>()
    val favoriteTags get() = _favoriteTags

    private var _selectedRecipeId: UUID by mutableStateOf(UUID.randomUUID())
    val selectedRecipeIndex get() = _selectedRecipeId
    private var _showRecipeEnabled by mutableStateOf(false)
    val showRecipeEnabled get() = _showRecipeEnabled

    fun changeSelectedRecipe(newId: UUID) {
        _selectedRecipeId = newId
    }
    fun changeSelectedRecipe(recipe: Recipe) {
        _selectedRecipeId = recipe.id
    }
    fun changeShowRecipe(newValue: Boolean) {
        _showRecipeEnabled = newValue
    }

    fun addRecipe(name: String, images: List<String> = listOf(), groceries: List<GroceryItem> = listOf(), tags: List<UUID> = listOf()) : UUID {
        val newRecipe = Recipe(name = name, groceries = groceries.toMutableStateList(), images = images.toMutableStateList(), tags = tags.toMutableList())

        _recipes.add(newRecipe)
//        _tags.addAllWithoutDuplicates(tags)
        _recipes.sortBy { it.name }
        return newRecipe.id
    }

    fun addImagesToRecipe(id: UUID, images: List<String>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].images.addAll(images)
    }
    fun changeRecipeName(id: UUID, newName: String) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].name = newName
        _recipes.sortBy { it.name }
    }
    fun changeRecipeTags(id: UUID, newTags: List<UUID>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].tags.clear()
        _recipes[index].tags.addAll(newTags)
    }
    fun changeRecipeImages(id: UUID, newImages: List<String>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].images.clear()
        _recipes[index].images.addAll(newImages)
    }
    fun changeRecipeGroceries(id: UUID, newGroceries: List<GroceryItem>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].groceries.clear()
        _recipes[index].groceries.addAll(newGroceries)
    }
    fun deleteRecipeImages(paths: List<String>) {
        paths.forEach { image ->
            deleteFile(image)
        }
    }

    fun removeRecipe(id: UUID) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes.removeAt(index)
    }
    fun addTag(tag: Tag) {
        _tags.add(tag)
        _tags.sortBy { it.name }
    }
    fun addTags(tags: List<Tag>) {
        _tags.addAll(tags)
        _tags.sortBy { it.name }
    }
    fun changeFavoriteTags(ids: List<UUID>) {
        _favoriteTags.clear()
        _favoriteTags.addAll(ids)
    }

    fun changeTags(newTags: List<Tag>) {
        tags.clear()
        tags.addAll(newTags)
        _tags.sortBy { it.name }
    }
    fun deleteTagIds(ids: List<UUID>) {
        recipes.forEach { recipe ->
            recipe.tags.removeIf { tagId -> ids.contains(tagId) }
        }
        tags.removeIf { tag -> ids.contains(tag.id) }
        _favoriteTags.removeIf { tagId -> ids.contains(tagId) }
    }
    fun changeTagName(id: UUID, newName: String) {
        val index = tags.indexOfFirst { it.id == id }
        _tags[index].name = newName
        _tags.sortBy { it.name }
    }
    fun changeTagIconIndex(id: UUID, newIndex: Int) {
        val index = tags.indexOfFirst { it.id == id }
        _tags[index].iconIndex = newIndex
    }
    fun changeTagRecipes(id: UUID, newRecipes: List<Recipe>) {
        recipes.forEach { recipe ->
            recipe.tags.removeIf { tagId -> tagId == id }
            if (newRecipes.contains(recipe))
                recipe.tags.add(id)
        }
    }
    fun deleteTagId(id: UUID) {
        recipes.forEach { recipe ->
            recipe.tags.removeIf { tagId -> tagId == id }
        }
        _favoriteTags.removeIf { tagId -> tagId == id }

        val index = tags.indexOfFirst { it.id == id }
        _tags.removeAt(index)
    }

    fun getImagePaths(): List<String> {
        val output = mutableListOf<String>()
        recipes.forEach { recipe ->
            output.addAll(recipe.images)
        }
        return output
    }

    private fun getSaveable(): SaveableRecipes {
        val output = mutableListOf<SaveableRecipe>()
        recipes.forEach { recipe ->
            val groceries = recipe.groceries.map { SaveableGroceryItem(it.name, it.details) }
            output.add(SaveableRecipe(recipe.name, recipe.images, groceries, recipe.tags))
        }
        return SaveableRecipes(output, tags, favoriteTags)
    }

    fun getJson(): String {
        return Json.encodeToString(getSaveable())
    }


    fun saveToFile(context: Context) {
        writeJsonToFile(context, "recipes.json", getSaveable())
    }

    fun getFromFile(context: Context) {
        val recipes: SaveableRecipes = getJsonFromFile(context, "recipes.json") ?: return

        import(recipes)
    }

    fun import(recipes: SaveableRecipes) {
        _recipes.clear()
        _tags.clear()
        _favoriteTags.clear()

        recipes.recipes.forEach { recipe ->
            val groceries = recipe.groceries.map { GroceryItem(it.name, it.details) }
            addRecipe(recipe.name, recipe.images, groceries, recipe.tags)
        }
        addTags(recipes.tags)
        _favoriteTags.addAll(recipes.favoriteTags)

        _showRecipeEnabled = false
    }

    fun deleteImageFiles() {
        val images = getImagePaths()
        deleteRecipeImages(images)
    }
}