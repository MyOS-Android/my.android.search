package my.android.search

import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_tabs.*


class WebSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        if (query.isNotBlank()) {
            //if (sp.getBoolean(getString(R.string.meta_search_key), true)) {
            if (intent.getBooleanExtra(META, true)) {
                // Meta search enabled
                setContentView(R.layout.activity_tabs)

                //TODO: Show Only Enabled Tabs
                //var enabledtabs = sp.getString(getString(R.string.search_engine_key), getString(R.string.url_duck))

                val tabTitle = resources.getStringArray(R.array.meta_search_tab_title)
                val urls = resources.getStringArray(R.array.meta_search_url)

                // Get user choice of web search engine
                urls[0] = sp.getString(getString(R.string.search_engine_key), getString(R.string.url_duck))
                urls[1] = sp.getString(getString(R.string.searx_search_engine_key), getString(R.string.url_searx))
                urls[2] = sp.getString(getString(R.string.twitter_search_engine_key), getString(R.string.url_twitter))
                urls[3] = sp.getString(getString(R.string.reddit_search_engine_key), getString(R.string.url_reddit))
                urls[4] = sp.getString(getString(R.string.youtube_search_engine_key), getString(R.string.url_youtube))
                urls[5] = sp.getString(getString(R.string.custom_search_engine_key), getString(R.string.url_custom))

                //TODO: Use random instance
                //urls[2] = if (urls[2] == "random_instance") resources.getStringArray(R.array.twitter_search_engine_values).random() else urls[2]

                viewPager.adapter = ViewStateAdapter(supportFragmentManager, lifecycle, query, urls)
                TabLayoutMediator(tabs, viewPager) {tab, position -> tab.text = tabTitle[position] }.attach()

                viewPager.recyclerView.enforceSingleScrollDirection()

                // Use reflection to reduce Viewpager2 slide sensitivity, so that PhotoView inside can zoom presently
                val recyclerView = (ViewPager2::class.java.getDeclaredField("mRecyclerView").apply{ isAccessible = true }).get(viewPager) as RecyclerView
                (RecyclerView::class.java.getDeclaredField("mTouchSlop")).apply {
                    isAccessible = true
                    set(recyclerView, (get(recyclerView) as Int) * 7)
                }

                // Long click on tab 0 or 1 to temporarily change it's search engine
                val tabStrip: LinearLayout = tabs.getChildAt(0) as LinearLayout
                tabStrip.getChildAt(0).setOnLongClickListener {
                    // Show popup when the tab is displayed
                    if (viewPager.currentItem != 0) false
                    else {
                        val menuEntries = resources.getStringArray(R.array.web_search_engine_entries)
                        val menuValues = resources.getStringArray(R.array.web_search_engine_values)
                        //val f = (viewPager.adapter as ViewStateAdapter).getFragmentAt(0)
                        PopupMenu(baseContext, it).run {
                            for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                            show()
                            setOnMenuItemClickListener {
                                (supportFragmentManager.findFragmentByTag("f0") as TextSearchFragment).reload(menuValues[it.itemId] + query)
                                true
                            }
                        }
                        true
                    }
                }
                tabStrip.getChildAt(1).setOnLongClickListener {
                    // Show popup when the tab is displayed
                    if (viewPager.currentItem != 1) false
                    else {
                        val menuEntries = resources.getStringArray(R.array.searx_search_engine_entries)
                        val menuValues = resources.getStringArray(R.array.searx_search_engine_values)
                        PopupMenu(baseContext, it).run {
                            for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                            show()
                            setOnMenuItemClickListener {
                                (supportFragmentManager.findFragmentByTag("f1") as TextSearchFragment).reload(menuValues[it.itemId] + query)
                                true
                            }
                        }

                        true
                    }
                }
                tabStrip.getChildAt(2).setOnLongClickListener {
                    // Show popup when the tab is displayed
                    if (viewPager.currentItem != 2) false
                    else {
                        val menuEntries = resources.getStringArray(R.array.twitter_search_engine_entries)
                        val menuValues = resources.getStringArray(R.array.twitter_search_engine_values)
                        PopupMenu(baseContext, it).run {
                            for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                            show()
                            setOnMenuItemClickListener {
                                (supportFragmentManager.findFragmentByTag("f2") as TextSearchFragment).reload(menuValues[it.itemId] + query)
                                true
                            }
                        }

                        true
                    }
                }
                tabStrip.getChildAt(3).setOnLongClickListener {
                    // Show popup when the tab is displayed
                    if (viewPager.currentItem != 3) false
                    else {
                        val menuEntries = resources.getStringArray(R.array.reddit_search_engine_entries)
                        val menuValues = resources.getStringArray(R.array.reddit_search_engine_values)
                        PopupMenu(baseContext, it).run {
                            for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                            show()
                            setOnMenuItemClickListener {
                                (supportFragmentManager.findFragmentByTag("f3") as TextSearchFragment).reload(menuValues[it.itemId] + query)
                                true
                            }
                        }

                        true
                    }
                }
                tabStrip.getChildAt(4).setOnLongClickListener {
                    // Show popup when the tab is displayed
                    if (viewPager.currentItem != 4) false
                    else {
                        val menuEntries = resources.getStringArray(R.array.youtube_search_engine_entries)
                        val menuValues = resources.getStringArray(R.array.youtube_search_engine_values)
                        PopupMenu(baseContext, it).run {
                            for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                            show()
                            setOnMenuItemClickListener {
                                (supportFragmentManager.findFragmentByTag("f4") as TextSearchFragment).reload(menuValues[it.itemId] + query)
                                true
                            }
                        }

                        true
                    }
                }
                tabs.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        // Draw a popup menu indicator for tab 0 and 1
                        if (tab?.position == 0 || tab?.position == 1 || tab?.position == 2 || tab?.position == 3 || tab?.position == 4) {
                            tab.view.apply {
                                background = MaterialShapeDrawable(
                                    ShapeAppearanceModel.builder()
                                        .setTopLeftCornerSize((width + height - (8 * resources.displayMetrics.densityDpi / 160)).toFloat())
                                        .setTopLeftCorner(CutCornerTreatment())
                                        .build()
                                ).apply { fillColor = ColorStateList.valueOf(getColor(R.color.ic_launcher_background)) }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        // Restore default background
                        tab?.view?.background = tabs.background
                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        // Draw a popup menu indicator for tab 0 and 1
                        if (tab?.position == 0 || tab?.position == 1 || tab?.position == 2 || tab?.position == 3 || tab?.position == 4) {
                            tab.view.apply {
                                background = MaterialShapeDrawable(
                                    ShapeAppearanceModel.builder()
                                        .setTopLeftCornerSize((width + height - (8 * resources.displayMetrics.densityDpi / 160)).toFloat())
                                        .setTopLeftCorner(CutCornerTreatment())
                                        .build()
                                ).apply { fillColor = ColorStateList.valueOf(getColor(R.color.ic_launcher_background)) }
                            }
                        }
                    }
                })
            }
            else {
                // Default search enabled
                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(sp.getString(getString(R.string.search_engine_key), getString(R.string.url_duck))+query))
                startActivity(searchIntent)
                finish()
                return
            }
        }
        else {
            finish()
            return
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // The first time the tab layout is shown, the tab selected event is not sent to OnTabSelectedListener, so we need to set the popup menu indicator here.
        // This is a really annoying behavior
        if ( tabs.selectedTabPosition < 5)  {
            tabs.getTabAt(tabs.selectedTabPosition)?.view?.apply {
                background = MaterialShapeDrawable(
                    ShapeAppearanceModel.builder()
                        .setTopLeftCornerSize((width + height - (8 * resources.displayMetrics.densityDpi / 160)).toFloat())
                        .setTopLeftCorner(CutCornerTreatment())
                        .build()
                ).apply { fillColor = ColorStateList.valueOf(getColor(R.color.ic_launcher_background)) }
            }
        }
    }

    private class ViewStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, val query: String, val url: Array<String>)
        : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = url.size

        override fun createFragment(position: Int): Fragment {
            return TextSearchFragment.newInstance(url[position] + query)
        }
    }

    companion object {
        const val META = "META"
    }
}
