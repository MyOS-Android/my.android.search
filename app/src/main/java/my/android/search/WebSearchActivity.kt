package my.android.search

import android.app.SearchManager
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
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

        lateinit var query: String

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        var meta = sp.getBoolean(getString(R.string.meta_search_key), true)

        if (intent.action == Intent.ACTION_WEB_SEARCH || intent.action == Intent.ACTION_SEND  ) {
            query = intent.getStringExtra(SearchManager.QUERY) ?: ""
        }
        else {
            query = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
            meta = intent.getBooleanExtra(META, true)
        }

        if (query.isEmpty()) {
            finish()
            return
        }

        if (meta) {
            // Meta search enabled
            setContentView(R.layout.activity_tabs)

            val tabTitle = resources.getStringArray(R.array.meta_search_tab_title)
            val urls = resources.getStringArray(R.array.meta_search_url)

            // Get user choice of web search engine
            urls[0] = sp.getString(getString(R.string.search_engine_key), getString(R.string.url_duck))
            urls[1] = sp.getString(getString(R.string.searx_search_engine_key), getString(R.string.url_searx_1))
            urls[2] = sp.getString(getString(R.string.twitter_search_engine_key), getString(R.string.url_twitter))
            urls[3] = sp.getString(getString(R.string.reddit_search_engine_key), getString(R.string.url_reddit))
            urls[4] = sp.getString(getString(R.string.youtube_search_engine_key), getString(R.string.url_youtube))
            urls[5] = sp.getString(getString(R.string.custom_search_engine_key), getString(R.string.url_custom))

            viewPager.adapter = ViewStateAdapter(supportFragmentManager, lifecycle, query, urls)
            TabLayoutMediator(tabs, viewPager) {tab, position -> tab.text = tabTitle[position] }.attach()

            viewPager.recyclerView.enforceSingleScrollDirection()

            // Use reflection to reduce Viewpager2 slide sensitivity, so that PhotoView inside can zoom presently
            val recyclerView = (ViewPager2::class.java.getDeclaredField("mRecyclerView").apply{ isAccessible = true }).get(viewPager) as RecyclerView
            (RecyclerView::class.java.getDeclaredField("mTouchSlop")).apply {
                isAccessible = true
                set(recyclerView, (get(recyclerView) as Int) * 6)
            }

            // Click on tab 0-4 to temporarily change it's search engine
            val tabStrip: LinearLayout = tabs.getChildAt(0) as LinearLayout
            tabStrip.getChildAt(0).setOnClickListener {
                if (viewPager.currentItem == 0) {
                    val menuEntries = resources.getStringArray(R.array.web_search_engine_entries)
                    val menuValues = resources.getStringArray(R.array.web_search_engine_values)
                    PopupMenu(baseContext, it).run {
                        for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                        show()
                        setOnMenuItemClickListener { menuItem->
                            urls[0] = menuValues[menuItem.itemId]
                            (supportFragmentManager.findFragmentByTag("f0") as TextSearchFragment).reload(menuValues[menuItem.itemId] + query)
                            true
                        }
                    }
                }
            }
            tabStrip.getChildAt(1).setOnClickListener {
                if (viewPager.currentItem == 1) {
                    val menuEntries = resources.getStringArray(R.array.searx_search_engine_entries)
                    val menuValues = resources.getStringArray(R.array.searx_search_engine_values)
                    PopupMenu(baseContext, it).run {
                        for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                        show()
                        setOnMenuItemClickListener { menuItem->
                            urls[1] = menuValues[menuItem.itemId]
                            (supportFragmentManager.findFragmentByTag("f1") as TextSearchFragment).reload(menuValues[menuItem.itemId] + query)
                            true
                        }
                    }
                }
            }
            tabStrip.getChildAt(2).setOnClickListener {
                if (viewPager.currentItem == 2) {
                    val menuEntries = resources.getStringArray(R.array.twitter_search_engine_entries)
                    val menuValues = resources.getStringArray(R.array.twitter_search_engine_values)
                    PopupMenu(baseContext, it).run {
                        for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                        show()
                        setOnMenuItemClickListener { menuItem->
                            urls[2] = menuValues[menuItem.itemId]
                            (supportFragmentManager.findFragmentByTag("f2") as TextSearchFragment).reload(menuValues[menuItem.itemId] + query)
                            true
                        }
                    }
                }
            }
            tabStrip.getChildAt(3).setOnClickListener {
                if (viewPager.currentItem == 3) {
                    val menuEntries = resources.getStringArray(R.array.reddit_search_engine_entries)
                    val menuValues = resources.getStringArray(R.array.reddit_search_engine_values)
                    PopupMenu(baseContext, it).run {
                        for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                        show()
                        setOnMenuItemClickListener { menuItem->
                            urls[3] = menuValues[menuItem.itemId]
                            (supportFragmentManager.findFragmentByTag("f3") as TextSearchFragment).reload(menuValues[menuItem.itemId] + query)
                            true
                        }
                    }
                }
            }
            tabStrip.getChildAt(4).setOnClickListener {
                if (viewPager.currentItem == 4) {
                    val menuEntries = resources.getStringArray(R.array.youtube_search_engine_entries)
                    val menuValues = resources.getStringArray(R.array.youtube_search_engine_values)
                    PopupMenu(baseContext, it).run {
                        for (i in menuEntries.indices) menu.add(Menu.NONE, i, i, menuEntries[i])
                        show()
                        setOnMenuItemClickListener { menuItem->
                            urls[4] = menuValues[menuItem.itemId]
                            (supportFragmentManager.findFragmentByTag("f4") as TextSearchFragment).reload(menuValues[menuItem.itemId] + query)
                            true
                        }
                    }
                }
            }

            tabs.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    // Restore default background
                    if (tab.position <= 4) tab.view.background = tabs.background
                    tab.view.setOnLongClickListener(null)
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    // Draw a popup menu indicator for tab 0 and 1
                    if (tab.position <= 4) {
                        tab.view.apply {
                            doOnLayout {
                                background = MaterialShapeDrawable(
                                    ShapeAppearanceModel.builder()
                                        .setTopLeftCornerSize((width + height - (8 * resources.displayMetrics.densityDpi / 160)).toFloat())
                                        .setTopLeftCorner(CutCornerTreatment())
                                        .build()
                                ).apply { fillColor = ColorStateList.valueOf(getColor(R.color.colorPrimary)) }
                            }
                        }
                    }

                    tab.view.setOnLongClickListener { v->
                        PopupMenu(baseContext, v).run {
                            menu.add(Menu.NONE, 0, 0, R.string.menuitem_browser)
                            menu.add(Menu.NONE, 1, 1, R.string.menuitem_share_hyperlink)
                            menu.add(Menu.NONE, 2, 2, R.string.menuitem_copy_hyperlink)
                            show()
                            setOnMenuItemClickListener { menuItem->
                                (supportFragmentManager.findFragmentByTag("f${tabs.selectedTabPosition}") as TextSearchFragment).getCurrentUrl()?.let { url->
                                    when(menuItem.itemId) {
                                        0-> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                        1-> startActivity(Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, url)
                                            type = "text/plain"
                                        })
                                        2-> (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("", url))
                                    }
                                }
                                true
                            }
                        }
                        true
                    }
                }
            })
        }
        else {
            val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(sp.getString(getString(R.string.search_engine_key), getString(R.string.url_duck))+query))
            startActivity(searchIntent)
            finish()
            return
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
