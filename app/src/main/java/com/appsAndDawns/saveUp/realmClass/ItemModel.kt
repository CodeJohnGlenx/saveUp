package com.appsAndDawns.saveUp.realmClass

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

// Realm Classes
// items.realm
// removedItems.realm
// themeMode.realm  // id: themeMode  // title/type: Light Mode or Dark Mode
// deleteDateSelection.realm  // id: deleteDateSelection  // title/type: never(default)   // see SettingsSelectionAdapter for more info
// excludeBalance.realm  // id: excludeBalance  // title/type: never(default)  // see ExcludeBalanceAdapter for more info
// includeItems.realm  // id: includeItems  // title/type: include all items(default)
// bookmarkItemsRealm.realm  // id: (unique uuid)  // for bookmark items   // See settingsFragment
// tutorialRealm.realm  // id: home, calendar, statistics, trash, settings  // value: 0.0 (default) not default: 1.0
@RealmClass
open class ItemModel: RealmObject() {
    @PrimaryKey
    var itemId: String? = null
    var itemType: String? = null
    var itemTitle: String? = null
    var itemDate: Date? = null
    var itemTime: Date? = null
    var itemValue: Double?  = null
}
