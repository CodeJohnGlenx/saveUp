package com.appsAndDawns.saveUp.model

import com.appsAndDawns.saveUp.R


// model used in automatically delete items
data class SettingsAutomaticallyDeleteSelectionModel(var image: Int, var selectionString: String)

object SelectionSupplier {
    val settingsAutomaticallyDeleteSelectionModels = mutableListOf(
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_one, "over this day"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_two, "over three days"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_two, "over this week"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_three, "over two weeks"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_four, "over this month"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_three, "over three months"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_three, "over six months"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_four, "over this year"),
        SettingsAutomaticallyDeleteSelectionModel(R.drawable.ic_calendar_selection_never, "never")
        )
}

