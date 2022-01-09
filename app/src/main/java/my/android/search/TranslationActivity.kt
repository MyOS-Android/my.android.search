package my.android.search

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_tabs.*
import java.util.*

class TranslationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)

        val defaultLocale = Locale.getDefault()
        val query = intent.getStringExtra(KEY_QUERY).toString()

        val tabTitle = resources.getStringArray(R.array.translator_tab_title)
        val urls = resources.getStringArray(R.array.translator_url)

        urls[0] += defaultLocale.language + "/"
        urls[1] += if (defaultLocale.language.equals("zh")) defaultLocale.language + "-" + defaultLocale.country + "&text=" else defaultLocale.language + "&text="

        viewPager.adapter = ViewStateAdapter(supportFragmentManager, lifecycle, query, urls)
        TabLayoutMediator(tabs, viewPager) {tab, position -> tab.text = tabTitle[position] }.attach()

        viewPager.recyclerView.enforceSingleScrollDirection()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
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

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Use reflection to reduce Viewpager2 slide sensitivity, so that PhotoView inside can zoom presently
        val recyclerView = (ViewPager2::class.java.getDeclaredField("mRecyclerView").apply{ isAccessible = true }).get(viewPager) as RecyclerView
        (RecyclerView::class.java.getDeclaredField("mTouchSlop")).apply {
            isAccessible = true
            set(recyclerView, (get(recyclerView) as Int) * 7)
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
        const val KEY_QUERY = "KEY_QUERY"
    }
}
