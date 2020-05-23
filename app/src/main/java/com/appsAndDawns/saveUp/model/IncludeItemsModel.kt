package com.appsAndDawns.saveUp.model


// model used in include items
import com.appsAndDawns.saveUp.R

data class IncludeItemsModel(var image: Int, var selectionString: String)

object IncludeItemsSelectionSupplier {
    val includeItemsModels = mutableListOf(
        IncludeItemsModel(R.drawable.ic_calendar_selection_one, "within this day"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_two, "within three days"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_two, "within this week"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_three, "within two weeks"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_four, "within this month"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_three, "within three months"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_three, "within six months"),
        IncludeItemsModel(R.drawable.ic_calendar_selection_four, "within this year"),
        IncludeItemsModel(R.drawable.ic_check, "include all items")
    )
}