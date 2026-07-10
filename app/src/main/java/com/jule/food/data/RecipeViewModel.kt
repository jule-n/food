package com.jule.food.data

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
import com.jule.food.R
import com.jule.food.utils.UUIDSerializer
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

    private var _isEditGroceriesScreenActive by mutableStateOf<Boolean>(false)
    val isEditGroceriesScreenActive get() = _isEditGroceriesScreenActive
    fun setIsEditGroceriesScreenActive(value: Boolean) {
        _isEditGroceriesScreenActive = value
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
    fun addRecipe(name: String, context: Context, images: List<String> = listOf(), groceries: List<GroceryItem> = listOf(), tags: List<UUID> = listOf(), note: String = "") : UUID {
        val newRecipe = Recipe(name = name, groceries = groceries.toMutableStateList(), images = images.toMutableStateList(), tags = tags.toMutableStateList(), note = note)

        _recipes.add(newRecipe)
        _recipes.sortBy { it.name }

        Log.d("addRecipe", "Added new recipe \"$name\"")

        saveToFile(context)

        return newRecipe.id
    }
    // Add a new recipe with a name and id, and optionally, images, groceries, tags and notes
    fun addRecipe(name: String, images: List<String> = listOf(), groceries: List<GroceryItem> = listOf(), tags: List<UUID> = listOf(), note: String = "", id: UUID) {
        val newRecipe = Recipe(name = name, groceries = groceries.toMutableStateList(), images = images.toMutableStateList(), tags = tags.toMutableStateList(), note = note, id = id)

        _recipes.add(newRecipe)
        _recipes.sortBy { it.name }

        Log.d("addRecipe", "Added new recipe \"$name\"")
    }

    // Add image paths to a recipe
    fun addImagesToRecipe(id: UUID, images: List<String>, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("addImagesToRecipe", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.images.addAll(images)

        saveToFile(context)
    }
    // Change a recipe name
    fun changeRecipeName(id: UUID, newName: String, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("changeRecipeName", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.name = newName
        _recipes.sortBy { it.name }

        saveToFile(context)
    }
    // Change the tags of a recipe
    fun changeRecipeTags(id: UUID, newTags: List<UUID>, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("changeRecipeTags", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.tags.clear()
        recipe.tags.addAll(newTags)

        saveToFile(context)
    }
    // Change the images of a recipe
    fun changeRecipeImages(id: UUID, newImages: List<String>, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("changeRecipeImages", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.images.clear()
        recipe.images.addAll(newImages)

        saveToFile(context)
    }
    // Change the groceries of a recipe
    fun changeRecipeGroceries(id: UUID, newGroceries: List<GroceryItem>) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("changeRecipeGroceries", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.groceries.clear()
        recipe.groceries.addAll(newGroceries)
        print("Changed Groceries: now ${recipe.groceries.count()} Groceries")
    }
    // Delete an image from a recipe
    fun deleteRecipeImage(id: UUID, imagePath: String, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("deleteRecipeImage", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.images.remove(imagePath)
        // Delete the image file
        deleteFile(imagePath)

        saveToFile(context)
    }
    // Delete an image from a recipe
    fun deleteRecipeImages(id: UUID, imagePaths: List<String>, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("deleteRecipeImage", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.images.removeAll(imagePaths)
        // Delete the image file
        deleteFiles(imagePaths)

        saveToFile(context)
    }
    // Change the note of a recipe
    fun changeRecipeNote(id: UUID, newNote: String, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("changeRecipeNote", "Recipe with ID \"$id\" not found")
            return
        }
        recipe.note = newNote

        saveToFile(context)
    }

    // Delete a recipe
    fun removeRecipe(id: UUID, context: Context) {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("removeRecipe", "Recipe with ID \"$id\" not found")
            return
        }
        Log.d("removeRecipe", "Deleting recipe \"${recipe.name}\"")
        // Delete all image files from this recipe
        deleteFiles(recipe.images)
        _recipes.remove(recipe)

        _recentRecipeIds.remove(id)

        saveToFile(context)

//        if (selectedRecipeId == id) {}
    }
    // Add a new tag
    fun addTag(tag: Tag, context: Context) {
        _tags.add(tag)

        saveToFile(context)
    }
    fun addTagWithoutSaving(tag: Tag) {
        _tags.add(tag)
    }
    // Add multiple tags
    fun addTags(tags: List<Tag>, context: Context) {
        _tags.addAll(tags)

        saveToFile(context)
    }
    // Change the name of a tag
    fun changeTagName(id: UUID, newName: String, context: Context) {
        val tag = tags.firstOrNull { it.id == id }
        if (tag == null) {
            Log.e("changeTagName", "Tag with ID \"$id\" not found")
            return
        }
        tag.name = newName

        saveToFile(context)
    }
    // Change the icon of a tag
    fun changeTagIconIndex(id: UUID, newIndex: Int, context: Context) {
        val tag = tags.firstOrNull { it.id == id }
        if (tag == null) {
            Log.e("changeTagIconIndex", "Tag with ID \"$id\" not found")
            return
        }
        tag.iconIndex = newIndex

        saveToFile(context)
    }
    // Change the recipes that have this tag
    fun changeTagRecipes(id: UUID, newRecipeIds: List<UUID>, context: Context) {
        recipes.forEach { recipe ->
            // Loop through all recipes, remove the tag from it if it is there
            recipe.tags.removeIf { tagId -> tagId == id }
            // If the new list of recipes contains the tag, add it
            if (newRecipeIds.contains(recipe.id))
                recipe.tags.add(id)
        }

        saveToFile(context)
    }
    // Delete a tag
    fun deleteTagId(id: UUID, context: Context) {
        recipes.forEach { recipe ->
            recipe.tags.removeIf { tagId -> tagId == id }
        }

        val tag = tags.firstOrNull { it.id == id }
        if (tag == null) {
            Log.e("deleteTagId", "Tag with ID \"$id\" not found")
            return
        }
        _tags.remove(tag)

        saveToFile(context)
    }
    // Change the tag order
    fun reorderTags(fromIndex: Int, toIndex: Int, context: Context) {
        _tags.apply {
            add(toIndex, removeAt(fromIndex))
        }
        saveToFile(context)
    }
    // Get a recipe name from its UUID
    fun getRecipeNameFromId(id: UUID): String {
        val recipe = _recipes.firstOrNull { it.id == id }
        if (recipe == null) {
            Log.e("getRecipeNameFromId", "Recipe with ID \"$id\" not found")
            return "NULL"
        }
        return recipe.name
    }


    fun onRemoveGroceryLocation(locationId: UUID) {
        var index = 0
        _recipes.forEach {
            it.groceries.forEach {
                if (it.locationId == locationId) {
                    it.locationId = null
                    index++
                }
            }
        }

        Log.d("RecipeVM:onRemoveGroceryLocation", "Removed location from $index items")
    }
    fun onAddGroceryNameToLocation(groceryName: String, locationId: UUID) {
        var index = 0
        _recipes.forEach {
            it.groceries.forEach {
                if (it.name.trim() == groceryName.trim()) {
                    it.locationId = locationId
                    index++
                }
            }
        }

        Log.d("RecipeVM:onAddGroceryNameToLocation", "Added location to $index items ($groceryName)")
    }
    fun onRemoveGroceryNameFromAllLocations(groceryName: String) {
        var index = 0
        _recipes.forEach {
            it.groceries.forEach {
                if (it.name.trim() == groceryName.trim()) {
                    it.locationId = null
                    index++
                }
            }
        }

        Log.d("RecipeVM:onRemoveGroceryNameFromAllLocations", "Removed ${groceryName} from all locations ($index)")
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
            val groceries = recipe.groceries.map { SaveableGroceryItem(it.name, it.details, categoryId = it.categoryId, locationId = it.locationId) }
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
            _dataLoaded = true
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
            addRecipe("Default", id = UUID.randomUUID())
        }
        _dataLoaded = true
    }

    fun import(recipes: SaveableRecipes) {
        _recipes.clear()
        _tags.clear()

        recipes.recipes.forEach { recipe ->
            val groceries = recipe.groceries.map { GroceryItem(it.name, it.details, categoryId = it.categoryId, locationId = it.locationId) }
            addRecipe(recipe.name, recipe.images, groceries, recipe.tags, recipe.note, recipe.id)
        }
        recipes.tags.forEach { tag ->
            addTagWithoutSaving(Tag(tag.name, tag.iconIndex, tag.id))
        }

        _recentRecipeIds.clear()
        _recentRecipeIds.addAll(recipes.recentRecipeIds ?: listOf())
    }

    fun deleteImageFiles() {
        val images = getImagePaths()
        deleteFiles(images)
    }
}