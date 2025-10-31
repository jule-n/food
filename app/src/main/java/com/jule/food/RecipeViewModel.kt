package com.jule.food

import android.content.Context
import android.util.Log
import android.widget.Toast
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
    val tags: SnapshotStateList<UUID> = mutableStateListOf(),
    note: String = "",
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
    var note by mutableStateOf(note)
}

class Tag(
    name: String,
    iconIndex: Int,
    val id: UUID = UUID.randomUUID()
) {
    var name by mutableStateOf(name)
    var iconIndex by mutableIntStateOf(iconIndex)
}

@Serializable
class SaveableTag(
    val name: String,
    var iconIndex: Int,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)

// Serializable data class for storing a recipe
@Serializable
class SaveableRecipe(
    val name: String,
    val images: List<String>,
    val groceries: List<SaveableGroceryItem>,
    val tags: List<@Serializable(with = UUIDSerializer::class)UUID>,
    val note: String = "",
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID()
)
// Serializable data class for storing recipes and tags
@Serializable
class SaveableRecipes(
    val recipes: List<SaveableRecipe>,
    val tags: List<SaveableTag>,
    val recentRecipeIds: List<@Serializable(with = UUIDSerializer::class)UUID>? = null
)

// Function for getting tags from UUIDs
fun getTagsFromIds(ids: List<UUID>, tags: List<Tag>): List<Tag> {
    return tags.filter { ids.contains(it.id) }
}
fun getTagFromId(id: UUID, tags: List<Tag>): Tag {
    return tags[tags.indexOfFirst { it.id == id } ]
}
// Function for getting a recipe from its UUID
fun getRecipeFromId(id: UUID, recipes: List<Recipe>): Recipe {
    return recipes[recipes.indexOfFirst { it.id == id} ]
}
// Function that checks whether a given recipe name is valid
fun isRecipeError(name: String): Boolean {
    return name.isEmpty() || name.length > 40
}
// Function that checks whether a given tag name is valid
fun isTagNameTooLong(name: String): Boolean {
    return name.length > 20
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
    private var _dataLoaded by mutableStateOf(false)
    val dataLoaded: Boolean
        get() = _dataLoaded

    private var _recipes = mutableStateListOf<Recipe>()
    val recipes get() = _recipes

    private var _tags = mutableStateListOf<Tag>()
    val tags get() = _tags

    private var _selectedTagIds = mutableStateListOf<UUID>()
    val selectedTagIds get() = _selectedTagIds

    fun addSelectedTagId(id: UUID) {
        _selectedTagIds.add(id)
    }
    fun removeSelectedTagId(id: UUID) {
        _selectedTagIds.remove(id)
    }

    private var _isTagSelectionExpanded by mutableStateOf(false)
    val isTagSelectionExpanded get() = _isTagSelectionExpanded
    fun changeIsTagSelectionExpanded(newValue: Boolean) {
        _isTagSelectionExpanded = newValue
    }

    private var _isSearchBarExpanded by mutableStateOf(false)
    val isSearchBarExpanded get() = _isSearchBarExpanded

    fun changeIsSearchBarExpanded(newValue: Boolean) {
        _isSearchBarExpanded = newValue
    }

    private var _selectedRecipeId by mutableStateOf<UUID?>(null)
    val selectedRecipeId get() = _selectedRecipeId
    fun setSelectedRecipeId(id: UUID, fromSearch: Boolean) {
        _selectedRecipeId = id
        _lastSelectedRecipeFromSearch = fromSearch
    }
    fun resetSelectedRecipeId() {
        _selectedRecipeId = null
    }
    private var _lastSelectedRecipeFromSearch by mutableStateOf(false)
    val lastSelectedRecipeFromSearch get() = _lastSelectedRecipeFromSearch

    private var _selectedRecipeImageIndex by mutableStateOf<Int?>(null)
    val selectedRecipeImageIndex get() = _selectedRecipeImageIndex
    fun setSelectedRecipeImageIndex(index: Int?) {
        _selectedRecipeImageIndex = index
    }

    private var _recentRecipeIds = mutableStateListOf<UUID>()
    val recentRecipeIds get() = _recentRecipeIds

    // Add a recipe to the list of recent recipes
    fun addToRecentRecipes(newId: UUID) {
        if (!_recentRecipeIds.contains(newId)) {
            _recentRecipeIds.add(0, newId)
            if (_recentRecipeIds.size > 3) // Limit length to 3
                _recentRecipeIds.removeAt(_recentRecipeIds.lastIndex)
        } else { // If it is already there, remove it and add it to the front
            _recentRecipeIds.remove(newId)
            _recentRecipeIds.add(0, newId)
        }
    }

    // Add a new recipe with a name, and optionally, images, groceries, tags and notes. Returns the ID
    fun addRecipe(name: String, images: List<String> = listOf(), groceries: List<GroceryItem> = listOf(), tags: List<UUID> = listOf(), note: String = "") : UUID {
        val newRecipe = Recipe(name = name, groceries = groceries.toMutableStateList(), images = images.toMutableStateList(), tags = tags.toMutableStateList(), note = note)

        _recipes.add(newRecipe)
        _recipes.sortBy { it.name }
        return newRecipe.id
    }
    // Add a new recipe with a name and id, and optionally, images, groceries, tags and notes
    private fun addRecipe(name: String, images: List<String> = listOf(), groceries: List<GroceryItem> = listOf(), tags: List<UUID> = listOf(), note: String = "", id: UUID) {
        val newRecipe = Recipe(name = name, groceries = groceries.toMutableStateList(), images = images.toMutableStateList(), tags = tags.toMutableStateList(), note = note, id = id)

        _recipes.add(newRecipe)
        _recipes.sortBy { it.name }
    }

    // Add image paths to a recipe
    fun addImagesToRecipe(id: UUID, images: List<String>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].images.addAll(images)
    }
    // Change a recipe name
    fun changeRecipeName(id: UUID, newName: String) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].name = newName
        _recipes.sortBy { it.name }
    }
    // Change the tags of a recipe
    fun changeRecipeTags(id: UUID, newTags: List<UUID>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].tags.clear()
        _recipes[index].tags.addAll(newTags)
    }
    // Change the images of a recipe
    fun changeRecipeImages(id: UUID, newImages: List<String>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].images.clear()
        _recipes[index].images.addAll(newImages)
    }
    // Change the groceries of a recipe
    fun changeRecipeGroceries(id: UUID, newGroceries: List<GroceryItem>) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].groceries.clear()
        _recipes[index].groceries.addAll(newGroceries)
    }
    // Delete an image from a recipe
    fun deleteRecipeImage(id: UUID, imagePath: String) {
        val index = _recipes.indexOfFirst { it.id == id }
        _recipes[index].images.remove(imagePath)
        // Delete the image file
        deleteFile(imagePath)
    }
    // Change the note of a recipe
    fun changeRecipeNote(id: UUID, newNote: String) {
        val recipe = _recipes.firstOrNull { it.id == id } ?: return
        recipe.note = newNote
    }

    // Delete a recipe
    fun removeRecipe(id: UUID) {
        val index = _recipes.indexOfFirst { it.id == id }
        // Delete all image files from this recipe
        deleteFiles(_recipes[index].images)
        _recipes.removeAt(index)

//        if (selectedRecipeId == id) {}
    }
    // Add a new tag
    fun addTag(tag: Tag) {
        _tags.add(tag)
        _tags.sortBy { it.name }
    }
    // Add multiple tags
    fun addTags(tags: List<Tag>) {
        _tags.addAll(tags)
        _tags.sortBy { it.name }
    }
    // Change the name of a tag
    fun changeTagName(id: UUID, newName: String) {
        val index = tags.indexOfFirst { it.id == id }
        _tags[index].name = newName
        _tags.sortBy { it.name }
    }
    // Change the icon of a tag
    fun changeTagIconIndex(id: UUID, newIndex: Int) {
        val index = tags.indexOfFirst { it.id == id }
        _tags[index].iconIndex = newIndex
    }
    // Change the recipes that have this tag
    fun changeTagRecipes(id: UUID, newRecipeIds: List<UUID>) {
        recipes.forEach { recipe ->
            // Loop through all recipes, remove the tag from it if it is there
            recipe.tags.removeIf { tagId -> tagId == id }
            // If the new list of recipes contains the tag, add it
            if (newRecipeIds.contains(recipe.id))
                recipe.tags.add(id)
        }
    }
    // Delete a tag
    fun deleteTagId(id: UUID) {
        recipes.forEach { recipe ->
            recipe.tags.removeIf { tagId -> tagId == id }
        }

        val index = tags.indexOfFirst { it.id == id }
        _tags.removeAt(index)
    }
    // Get a recipe name from its UUID
    fun getRecipeNameFromId(id: UUID): String {
        val index = recipes.indexOfFirst { it.id == id }
        if (index == -1)
            return "NO RECIPE"
        return recipes[index].name
    }

    // Get all image paths from all recipes
    fun getImagePaths(): List<String> {
        val output = mutableListOf<String>()
        recipes.forEach { recipe ->
            output.addAll(recipe.images)
        }
        return output
    }

    // Get the saveable data type
    private fun getSaveable(): SaveableRecipes {
        val saveableRecipes = mutableListOf<SaveableRecipe>()
        recipes.forEach { recipe ->
            // Go through all recipes and convert them to the saveable type
            val groceries = recipe.groceries.map { SaveableGroceryItem(it.name, it.details) }
            saveableRecipes.add(SaveableRecipe(recipe.name, recipe.images, groceries, recipe.tags, recipe.note, recipe.id))
        }
        val saveableTags = tags.map { SaveableTag(it.name, it.iconIndex, it.id) }

        return SaveableRecipes(saveableRecipes, saveableTags, recentRecipeIds)
    }

    // Get the Json string for exporting the data 
    fun getJson(): String {
        return Json.encodeToString(getSaveable())
    }

    fun saveToFile(context: Context) {
        writeJsonToFile(context, "recipes.json", getSaveable())
    }

    fun getFromFile(context: Context) {
        Log.d("getFromFile","Loading recipe data...")
        val recipes: SaveableRecipes? = getJsonFromFile(context, "recipes.json", ignoreKeys = true)
        if (recipes == null) {
            Log.e("getFromFile","Recipe Data Not Found")
            Toast.makeText(context, "Recipe Data Not Found", Toast.LENGTH_SHORT).show()
            return
        }

        import(recipes)

        _dataLoaded = true
        Log.d("getFromFile","Recipe Data Loaded!")
    }

    fun initializeEmpty() {
        if (recipes.isEmpty()) {
            addRecipe("Default")
        }
        _dataLoaded = true
    }

    fun import(recipes: SaveableRecipes) {
        _recipes.clear()
        _tags.clear()

        recipes.recipes.forEach { recipe ->
            val groceries = recipe.groceries.map { GroceryItem(it.name, it.details) }
            addRecipe(recipe.name, recipe.images, groceries, recipe.tags, recipe.note, recipe.id)
        }
        recipes.tags.forEach { tag ->
            addTag(Tag(tag.name, tag.iconIndex, tag.id))
        }

        _recentRecipeIds.clear()
        _recentRecipeIds.addAll(recipes.recentRecipeIds ?: listOf())
    }

    fun deleteImageFiles() {
        val images = getImagePaths()
        deleteFiles(images)
    }
}