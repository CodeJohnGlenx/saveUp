package com.appsAndDawns.saveUp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appsAndDawns.saveUp.R
import com.appsAndDawns.saveUp.realmClass.ItemModel
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.layout_credits.*

class CreditsFragment : Fragment() {
    val themeConfig: RealmConfiguration? =
        RealmConfiguration.Builder().name("themeMode.realm").build()
    val themeRealm: Realm? = Realm.getInstance(themeConfig!!)
    var themeMode =
        themeRealm!!.where(ItemModel::class.java).equalTo("itemId", "themeMode").findFirst()

    /*
    override fun onAttach(context: Context) {
        Log.d(logTag, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Create")
        super.onCreate(savedInstanceState)
        Realm.init(activity!!)
    }

     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.layout_credits,
            container,
            false
        )
    }

    /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(logTag, "On Activity Created")
        super.onActivityCreated(savedInstanceState)
    }

     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpTheme()
        super.onViewCreated(view, savedInstanceState)
    }


/*
override fun onResume() {
    Log.d(logTag, "On Resume")
    super.onResume()
}

override fun onPause() {
    Log.d(logTag, "On Pause")
    super.onPause()
}

override fun onStop() {
    Log.d(logTag, "On Stop")
    super.onStop()
}

override fun onDestroyView() {
    Log.d(logTag, "On Destroy View")
    super.onDestroyView()
}

override fun onDestroy() {
    Log.d(logTag, "On Destroy")
    super.onDestroy()
}

override fun onDetach() {
    Log.d(logTag, "On Detach")
    super.onDetach()
}
 */

    private fun setUpTheme()
    {
        if (themeMode!!.itemType == "Dark Mode")
        {
            layout_credits_scroll_view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))
            layout_credits_relative_layout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.dark_grey_two))

            layout_credits_tv_programmer.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_glen.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            layout_credits_tv_icons_designer.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_adona.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            layout_credits_tv_researchers.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_valdez.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_mayor.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            layout_credits_tv_github_dependencies.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_android_times_square.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_mp_android_chart.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_mreram_showcaseview.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            layout_credits_tv_teachers.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_ps.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            layout_credits_tv_ps_message.setTextColor(ContextCompat.getColor(activity!!, R.color.white))

            layout_credits_tv_version.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        }
    }

}