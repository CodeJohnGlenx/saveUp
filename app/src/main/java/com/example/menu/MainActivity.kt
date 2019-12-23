package com.example.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.menu.Data.DataClass
import com.example.menu.Fragments.CalendarFragment
import com.example.menu.Fragments.HomeFragment
import com.example.menu.Fragments.StatisticsFragment
import com.example.menu.Fragments.TrashFragment
import com.example.menu.Model.Expenditure
import com.example.menu.Model.Supplier
import com.example.menu.RealmClass.ItemModel
import com.google.android.material.navigation.NavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {  // item selected listener for navigation view
    var dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    var manager = supportFragmentManager  // for supporting fragments



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // contains the navigation drawer and the fragment frame layout
        // setting realm on the home fragment when app is opened
        Realm.init(this)
        var config = RealmConfiguration.Builder().name("items.realm").build()
        var realm = Realm.getInstance(config)
        this.setTitle("Home")


        Supplier.expenditures.clear()
        val allItems = realm.where(ItemModel::class.java).findAll()
        allItems.forEach { thisItem ->
            val thisItemDate = dateFormat.format(thisItem.itemDate!!.time)
            if (dateFormat.format(DataClass.selectedDate!!.time) == thisItemDate)
                Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                if (thisItem.itemType != "Cash Deposit") {
                } else {
                }
        }



        ShowHomeFragment()
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
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white))


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {   // when navigation item is selected
        when (item.itemId) {
            R.id.nav_home -> {  // when item selected is nav_home, show the home fragment
                this.setTitle("Home")

                Supplier.expenditures.clear()
                var config = RealmConfiguration.Builder().name("items.realm").build()
                var realm = Realm.getInstance(config)
                val allItems = realm.where(ItemModel::class.java).findAll()
                allItems.forEach { thisItem ->
                    val thisItemDate = dateFormat.format(thisItem.itemDate!!.time)
                    if (dateFormat.format(Calendar.getInstance().time) == thisItemDate)
                        Supplier.expenditures.add(0, Expenditure(thisItem.itemTitle!!, thisItem.itemValue!!, thisItem.itemId!!, thisItem.itemType!!))
                }

                ShowHomeFragment()

                ShowHomeFragment()
            }
            R.id.nav_calendar -> {  // when item selected is nav_calendar, show the calendar fragment
                this.setTitle("Calendar")

                ShowCalendarFragment()
            }
            R.id.nav_trash -> {  // when item selected is nav_trash, show the trash fragment
                ShowTrashFragment()
                this.setTitle("Trash")
            }

            R.id.nav_statistics -> {
                ShowStatisticsFragment()
                this.setTitle("Statistics")
            }
        }
        drawer_layout!!.closeDrawer(GravityCompat.START)  // close drawer layout once item is selected
        return true

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  // setting the menu bar
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {  // action when item on menu bar is selected
        return when (item.itemId) {
            R.id.menu_search -> {
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_add -> {
                Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {  // closing the drawer when back pressed is clicked
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun ShowHomeFragment() {  // showing the home fragment
        val transaction = manager.beginTransaction()
        val fragment = HomeFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        this.setTitle("Home")


    }

    fun ShowCalendarFragment() {  // showing the calendar fragment
        val transaction = manager.beginTransaction()
        val fragment = CalendarFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun ShowTrashFragment() {  // showing the calendar fragment
        val transaction = manager.beginTransaction()
        val fragment = TrashFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun ShowStatisticsFragment() {
        val transaction = manager.beginTransaction()
        val fragment = StatisticsFragment()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}

