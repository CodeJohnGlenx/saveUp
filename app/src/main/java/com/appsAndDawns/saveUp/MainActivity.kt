package com.appsAndDawns.saveUp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.appsAndDawns.saveUp.data.DataClass
import com.appsAndDawns.saveUp.fragment.*
import com.appsAndDawns.saveUp.realmClass.ItemModel
import com.google.android.material.navigation.NavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.confirmation_dialog.*

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {  // item selected listener for navigation view
    private var manager = supportFragmentManager  // for supporting fragments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // contains the navigation drawer and the fragment frame layout

        Realm.init(this)
        // creating realms
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        val deleteDateSelectionConfig = RealmConfiguration.Builder().name("deleteDateSelection.realm").build()
        val deleteDateSelectionRealm = Realm.getInstance(deleteDateSelectionConfig)
        val includeItemsConfig = RealmConfiguration.Builder().name("includeItems.realm").build()
        val includeItemsRealm = Realm.getInstance(includeItemsConfig)
        val tutorialConfig = RealmConfiguration.Builder().name("tutorialRealm.realm").build()
        val tutorialRealm = Realm.getInstance(tutorialConfig)

        //val itemConfig: RealmConfiguration = RealmConfiguration.Builder().name("items.realm").build()
        //val itemRealm: Realm = Realm.getInstance(itemConfig)
        //val bookmarkItemsConfig: RealmConfiguration = RealmConfiguration.Builder().name("bookmarkItemsRealm.realm").build()
        //val bookmarkItemRealm: Realm = Realm.getInstance(bookmarkItemsConfig)
        //val removedItemConfig: RealmConfiguration = RealmConfiguration.Builder().name("removedItems.realm").build()
        //val removedItemRealm: Realm = Realm.getInstance(removedItemConfig)

        // checking if themeMode ID is already created, if not then create one.
        val themeCheck = themeRealm.where(ItemModel::class.java).findFirst()
        if (themeCheck == null) {
            themeRealm.beginTransaction()
            val theme = themeRealm.createObject(ItemModel::class.java, "themeMode")
            theme.itemType = getString(R.string.light_mode)
            theme.itemTitle = getString(R.string.light_mode)
            themeRealm.commitTransaction()
        }

        // checking if deleteDateSelection ID is already created, if not then create one.
        val deleteDateSelectionCheck = deleteDateSelectionRealm.where(ItemModel::class.java).findFirst()
        if (deleteDateSelectionCheck == null) {
            deleteDateSelectionRealm.beginTransaction()
            val deleteDateSelection = deleteDateSelectionRealm.createObject(ItemModel::class.java, "deleteDateSelection")
            deleteDateSelection.itemType = "never"
            deleteDateSelection.itemTitle = "never"
            deleteDateSelectionRealm.commitTransaction()
        }

        // checking if includeItems ID is already created, if not then create one.
        val includeItemsCheck = includeItemsRealm.where(ItemModel::class.java).findFirst()
        if (includeItemsCheck == null) {
            includeItemsRealm.beginTransaction()
            val includeItems = includeItemsRealm.createObject(ItemModel::class.java, "includeItems")
            includeItems.itemType = "include all items"
            includeItems.itemTitle = "include all items"
            includeItemsRealm.commitTransaction()
        }


        // checking if tutorial IDs ID are already created, if not then create one.
        val tutorialCheck = tutorialRealm.where(ItemModel::class.java).findFirst()
        if (tutorialCheck == null) {
            tutorialRealm.beginTransaction()
            val home = tutorialRealm.createObject(ItemModel::class.java, "home")
            home.itemValue = 0.0
            val calendar = tutorialRealm.createObject(ItemModel::class.java, "calendar")
            calendar.itemValue = 0.0
            val statistics = tutorialRealm.createObject(ItemModel::class.java, "statistics")
            statistics.itemValue = 0.0
            val trash = tutorialRealm.createObject(ItemModel::class.java, "trash")
            trash.itemValue = 0.0
            val settings = tutorialRealm.createObject(ItemModel::class.java, "settings")
            settings.itemValue = 0.0
            tutorialRealm.commitTransaction()
        }


        this.title = getString(R.string.home)

        DataClass.insertSupplierItems(DataClass.realm, DataClass.includeItems)  // loads the supplier based on includeItems option

        // code for the menu bar and the navigation bar // usually about setting it
        setSupportActionBar(tool_bar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            tool_bar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)


        changeThemeMode()
        setNavigationViewThemeMode()
        showHomeFragment()


        //updateOldItemTypes(itemRealm, removedItemRealm, bookmarkItemRealm)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {   // when navigation item is selected
        when (item.itemId) {
            R.id.nav_home -> {
                DataClass.insertSupplierItems(DataClass.realm, DataClass.includeItems)
                this.title = getString(R.string.home)
                drawer_layout!!.closeDrawer(GravityCompat.START)  // close drawer layout once item is selected
                showHomeFragment()
            }
            R.id.nav_calendar -> {
                this.title = getString(R.string.calendar)
                drawer_layout!!.closeDrawer(GravityCompat.START)
                showCalendarFragment()
            }
            R.id.nav_trash -> {
                this.title = getString(R.string.trash)
                drawer_layout!!.closeDrawer(GravityCompat.START)
                showTrashFragment()
            }
            R.id.nav_statistics -> {
                this.title = getString(R.string.statistics)
                drawer_layout!!.closeDrawer(GravityCompat.START)
                showStatisticsFragment()
            }

            R.id.nav_settings -> {
                this.title = getString(R.string.settings)
                drawer_layout!!.closeDrawer(GravityCompat.START)
                showSettingsFragment()
            }

            R.id.nav_credits -> {
                this.title = getString(R.string.credits)
                drawer_layout!!.closeDrawer(GravityCompat.START)
                showCreditsFragment()
            }


            R.id.nav_tutorial -> {
                // create dialog for tutorial confirmation
                val confirmationMessage = "Do you want to start a tutorial?"
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.confirmation_dialog)
                dialog.tv_confirmation_text_confirmation_dialog.text = confirmationMessage
                dialog.setCancelable(true)
                dialog.show()


                dialog.tv_confirmation_no_confirmation_dialog.setOnClickListener { dialog.dismiss() }  // dismiss if no

                dialog.tv_confirmation_yes_confirmation_dialog.setOnClickListener {  // reset tutorial IDs if yes
                    DataClass.tutorialRealm.beginTransaction()
                    DataClass.tutorialHome!!.itemValue = 0.0
                    DataClass.tutorialCalendar!!.itemValue = 0.0
                    DataClass.tutorialSettings!!.itemValue = 0.0
                    DataClass.tutorialTrash!!.itemValue = 0.0
                    DataClass.tutorialStatistics!!.itemValue = 0.0
                    DataClass.tutorialRealm.commitTransaction()

                    dialog.dismiss()
                    drawer_layout!!.closeDrawer(GravityCompat.START)
                    showHomeFragment()
                }


                if (DataClass.themeMode!!.itemType == getString(R.string.dark_mode)) {
                    dialog.tv_confirmation_confirmation_dialog.setTextColor(ContextCompat.getColor(this, R.color.white))
                    dialog.relative_layout_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey_three))
                    dialog.tv_confirmation_text_confirmation_dialog.setTextColor(ContextCompat.getColor(this, R.color.white))
                    dialog.tv_confirmation_text_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey_three))
                    dialog.tv_confirmation_no_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey_three))
                    dialog.tv_confirmation_yes_confirmation_dialog.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_grey_three))
                }

                this.title =  getString(R.string.home)
            }

        }
        return true
    }


    override fun onBackPressed() {  // closing the drawer when back pressed is clicked
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showHomeFragment() {
        val transaction = manager.beginTransaction()
        val fragment = HomeFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        this.title = getString(R.string.home)
    }

    private fun showCalendarFragment() {
        val transaction = manager.beginTransaction()
        val fragment = CalendarFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showTrashFragment() {
        val transaction = manager.beginTransaction()
        val fragment = TrashFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showStatisticsFragment() {
        val transaction = manager.beginTransaction()
        val fragment = StatisticsFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showSettingsFragment() {
        val transaction = manager.beginTransaction()
        val fragment = SettingsFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showCreditsFragment() {
        val transaction = manager.beginTransaction()
        val fragment = CreditsFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun changeThemeMode() {
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        val switch = nav_view.menu.findItem(R.id.theme_mode)
        val actionSwitch = switch.actionView as Switch

        actionSwitch.setOnCheckedChangeListener { _, _ ->
            if (actionSwitch.isChecked) {  // if NavigationView Dark Mode is checked, then turn to Dark Mode
                // Navigation View (Main Activity)
                themeRealm.beginTransaction()
                themeMode!!.itemType = getString(R.string.dark_mode)
                themeRealm.commitTransaction()
                setNavigationViewThemeMode()
                showHomeFragment()
            } else {  // else if Navigation Dark Mode is unchecked, then turn to Light Mode
                nav_view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                nav_view.itemBackground = ContextCompat.getDrawable(this@MainActivity, R.drawable.custom_nav_view_item_bg_light_theme)
                nav_view.itemTextColor = ContextCompat.getColorStateList(this@MainActivity, R.color.item_text_color_light_theme)
                nav_view.itemIconTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.item_icon_tint_light_theme)
                themeRealm.beginTransaction()
                themeMode!!.itemType = getString(R.string.light_mode)
                themeRealm.commitTransaction()
                showHomeFragment()
            }
        }

    }

    private fun setNavigationViewThemeMode() {  // sets the themeMode of NavigationView. LightMode or DarkMode
        val themeConfig = RealmConfiguration.Builder().name("themeMode.realm").build()
        val themeRealm = Realm.getInstance(themeConfig)
        val themeMode = themeRealm.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()
        val switch = nav_view.menu.findItem(R.id.theme_mode)
        val actionSwitch = switch.actionView as Switch

        if (themeMode!!.itemType == getString(R.string.dark_mode)) {
            nav_view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.dark_grey_two))
            nav_view.itemBackground = ContextCompat.getDrawable(this@MainActivity, R.drawable.custom_nav_view_item_bg_dark_theme)
            nav_view.itemTextColor = ContextCompat.getColorStateList(this@MainActivity, R.color.item_text_color_dark_theme)
            nav_view.itemIconTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.item_icon_tint_dark_theme)
            actionSwitch.isChecked = true
            fragment_container.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.dark_grey_two))
        } else {
            nav_view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            nav_view.itemBackground = ContextCompat.getDrawable(this@MainActivity, R.drawable.custom_nav_view_item_bg_light_theme)
            nav_view.itemTextColor = ContextCompat.getColorStateList(this@MainActivity, R.color.item_text_color_light_theme)
            nav_view.itemIconTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.item_icon_tint_light_theme)
            actionSwitch.isChecked = false
        }
    }


    /*
    private fun updateOldItemTypes(itemRealm: Realm, removedItemRealm: Realm, bookmarkItemRealm: Realm) {
        val allItems = itemRealm.where(ItemModel::class.java).findAll()
        allItems.forEach { thisItem ->
            when (thisItem.itemType) {
                "Cash Deposit" -> {
                    itemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.income)
                    itemRealm.commitTransaction()
                }
                "Fare" -> {
                    itemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.transportation)
                    itemRealm.commitTransaction()
                }
                "School Expenses" -> {
                    itemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.education)
                    itemRealm.commitTransaction()
                }
            }
        }

        val allRemovedItems = removedItemRealm.where(ItemModel::class.java).findAll()
        allRemovedItems.forEach { thisItem ->
            when (thisItem.itemType) {
                "Cash Deposit" -> {
                    removedItemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.income)
                    removedItemRealm.commitTransaction()
                }
                "Fare" -> {
                    removedItemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.transportation)
                    removedItemRealm.commitTransaction()
                }
                "School Expenses" -> {
                    removedItemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.education)
                    removedItemRealm.commitTransaction()
                }
            }
        }

        val allBookmarkItems = bookmarkItemRealm.where(ItemModel::class.java).findAll()
        allBookmarkItems.forEach { thisItem ->
            when (thisItem.itemType) {
                "Cash Deposit" -> {
                    bookmarkItemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.income)
                    bookmarkItemRealm.commitTransaction()
                }
                "Fare" -> {
                    bookmarkItemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.transportation)
                    bookmarkItemRealm.commitTransaction()
                }
                "School Expenses" -> {
                    bookmarkItemRealm.beginTransaction()
                    thisItem.itemType = getString(R.string.education)
                    bookmarkItemRealm.commitTransaction()
                }
            }
        }
    }

     */
}

